package com.example.nutrivision.ui.camera

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.nutrivision.data.BoundingBox
import com.example.nutrivision.databinding.FragmentCameraBinding
import com.example.nutrivision.ml.BestStunting
import com.example.nutrivision.ui.result.ResultActivity
import org.tensorflow.lite.support.image.TensorImage
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {

    private lateinit var binding: FragmentCameraBinding
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null

    // Declare photoFile as a property of the fragment
    private lateinit var photoFile: File

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraExecutor = Executors.newSingleThreadExecutor()
        startCamera() // Start the camera when the view is created

        // Set up the capture button listener
        binding.captureButton.setOnClickListener {
            takePhoto()
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

        // Initialize photoFile here
        photoFile = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "captured_image.jpg"
        )
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                    model(bitmap) // Pass the captured image to the model
                }

                override fun onError(exc: ImageCaptureException) {
                    Log.e("CameraFragment", "Photo capture failed: ${exc.message}", exc)
                }
            }
        )
    }

    private fun model(bitmap: Bitmap) {
        val model = BestStunting.newInstance(requireContext())

        // Convert Bitmap to TensorImage
        val image = TensorImage.fromBitmap(bitmap)

        // Perform inference
        val outputs = model.process(image)

        // Access the raw output tensor
        val outputTensor = outputs.outputAsTensorBuffer
        val outputArray = outputTensor.floatArray  // Get raw data as a float array

        // Define label map
        val labelMap = mapOf(
            0 to "Stunting",
            1 to "Normal"
        )

        Log.d("ModelOutput", "Output shape: ${outputTensor.shape.joinToString()}")

        // Assuming each detection entry has 6 values [x, y, width, height, score, class]
        val numDetections = outputTensor.shape[2]  // Expected to be 8400
        var bestScore = 0f
        var bestLabel = "Not Stunting" // Default to "Not Stunting"
        var bestBoundingBox: BoundingBox? = null

        for (i in 0 until numDetections) {
            val index = i * 6
            val score = outputArray[index + 4] // Confidence score
            val classId = outputArray[index + 5].toInt() // Class label ID

            // Log the score for each detection
            Log.d("Detection", "Detection $i - Score: $score, Class: ${labelMap[classId]}")

            // Check if this detection has a higher score than the current best
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

        // Determine the final output based on the best score
        // If bestScore > 0.25, set bestLabel to "Not Stunting"
        if (bestScore > 0.25) {
            bestLabel = "Not Stunting"
        }

        // Log the best score and final label
        Log.d("FinalResult", "Best Score: $bestScore, Label: $bestLabel")

        // Start ResultActivity and pass the final result
        val intent = Intent(requireContext(), ResultActivity::class.java).apply {
            putExtra("captured_image", photoFile.absolutePath)
            putExtra("is_stunting", bestLabel == "Stunting") // Pass true if the label indicates stunting
            putExtra("confidence_score", bestScore)
            // Pass the best bounding box if it exists
            bestBoundingBox?.let {
                putExtra("boundingBoxes", arrayListOf(it)) // Pass the bounding box
            }
        }
        startActivity(intent)

        model.close()
    }



    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
