package com.example.nutrivision.ui.camera

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.nutrivision.data.BoundingBox
import com.example.nutrivision.databinding.FragmentCameraBinding
import com.example.nutrivision.ml.BestStunting
import com.example.nutrivision.ml.BestFood // Import the new model
import com.example.nutrivision.ui.result.ResultActivity
import com.example.nutrivision.ui.result.ResultFoodActivity
import org.tensorflow.lite.support.image.TensorImage
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {

    private lateinit var binding: FragmentCameraBinding
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private lateinit var photoFile: File
    private var isGalleryImage = false
    private var selectedImageUri: Uri? = null
    private var useFoodModel = false // Toggle variable for model selection

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraBinding.inflate(inflater, container, false)

        // Add switch listener for model toggle
        binding.modelSwitch.setOnCheckedChangeListener { _, isChecked ->
            useFoodModel = isChecked // Toggle between models based on switch state
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraExecutor = Executors.newSingleThreadExecutor()
        startCamera()

        binding.captureButton.setOnClickListener {
            isGalleryImage = false
            takePhoto()
        }

        binding.galleryButton.setOnClickListener {
            isGalleryImage = true
            selectImageFromGallery()
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            val bitmap = uriToBitmap(it)
            processModel(bitmap)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (exc: Exception) {
                Log.e("CameraFragment", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        photoFile = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "captured_image.jpg"
        )
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                    processModel(bitmap)
                }

                override fun onError(exc: ImageCaptureException) {
                    Log.e("CameraFragment", "Photo capture failed: ${exc.message}", exc)
                }
            }
        )
    }

    private fun selectImageFromGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun processModel(bitmap: Bitmap) {
        if (useFoodModel) {
            // Process using BestFood model
            val model = BestFood.newInstance(requireContext())
            // Add model processing logic here...
            // Navigate to ResultFoodActivity with processed data
            val intent = Intent(requireContext(), ResultFoodActivity::class.java)
            if (isGalleryImage && selectedImageUri != null) {
                intent.putExtra("captured_image_uri", selectedImageUri.toString())
            } else {
                intent.putExtra("captured_image_path", photoFile.absolutePath)
            }
            startActivity(intent)
            model.close()
        } else {
            // Process using BestStunting model
            val model = BestStunting.newInstance(requireContext())
            val image = TensorImage.fromBitmap(bitmap)
            val outputs = model.process(image)
            val outputTensor = outputs.outputAsTensorBuffer
            val outputArray = outputTensor.floatArray
            val labelMap = mapOf(0 to "Stunting", 1 to "Normal")

            val numDetections = outputTensor.shape[2]
            var bestScore = 0f
            var bestLabel = "Not Stunting"
            var bestBoundingBox: BoundingBox? = null

            for (i in 0 until numDetections) {
                val index = i * 6
                val score = outputArray[index + 4]
                val classId = outputArray[index + 5].toInt()

                if (score > bestScore) {
                    bestScore = score
                    bestLabel = labelMap[classId] ?: "Unknown"
                    val x = outputArray[index]
                    val y = outputArray[index + 1]
                    val width = outputArray[index + 2]
                    val height = outputArray[index + 3]
                    bestBoundingBox = BoundingBox(x, y, x + width, y + height, bestLabel, bestScore)
                }
            }

            val intent = Intent(requireContext(), ResultActivity::class.java).apply {
                if (isGalleryImage && selectedImageUri != null) {
                    putExtra("captured_image_uri", selectedImageUri.toString())
                } else {
                    putExtra("captured_image_path", photoFile.absolutePath)
                }
                bestBoundingBox?.let {
                    putExtra("boundingBoxes", arrayListOf(it))
                }
            }
            startActivity(intent)
            model.close()
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun uriToBitmap(uri: Uri): Bitmap {
        val source = ImageDecoder.createSource(requireContext().contentResolver, uri)
        return ImageDecoder.decodeBitmap(source).copy(Bitmap.Config.ARGB_8888, true)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
