package com.example.nutrivision.ui.camera

import android.content.Intent
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
import com.example.nutrivision.databinding.FragmentCameraBinding
import com.example.nutrivision.ui.result.ResultActivity
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {

    private lateinit var binding: FragmentCameraBinding
    private lateinit var stuntingModel: Interpreter
    private lateinit var nutritionModel: Interpreter
    private lateinit var cameraExecutor: ExecutorService
    private var imageAnalyzer: ImageAnalyzer? = null
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private lateinit var imageCapture: ImageCapture

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load models
        stuntingModel = Interpreter(loadModelFile("best_stunting.tflite"))
        nutritionModel = Interpreter(loadModelFile("best_food.tflite"))
        cameraExecutor = Executors.newSingleThreadExecutor()

        val overlayView = binding.overlayView
        imageAnalyzer = ImageAnalyzer(stuntingModel, nutritionModel, overlayView)

        startCamera()

        // Handle model switch
        binding.modelSwitch.setOnCheckedChangeListener { _, isChecked ->
            imageAnalyzer?.useStuntingModel = isChecked
            updateModelSwitchText(isChecked)
        }

        // Capture image
        binding.captureButton.setOnClickListener {
            captureImage()
        }

        // Switch camera
        binding.switchCameraButton.setOnClickListener {
            toggleCamera()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, imageAnalyzer!!)
                }

            imageCapture = ImageCapture.Builder().build()
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis, imageCapture)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun toggleCamera() {
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
            CameraSelector.DEFAULT_FRONT_CAMERA
        else
            CameraSelector.DEFAULT_BACK_CAMERA
        startCamera()
    }

    private fun captureImage() {
        val photoFile = createFile()
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraFragment", "Image capture failed: ${exception.message}")
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val capturedBitmap = imageAnalyzer?.getLastBitmap()
                    capturedBitmap?.let {
                        // Start the ResultActivity to show the results
                        val intent = Intent(requireContext(), ResultActivity::class.java).apply {
                            putExtra("captured_image", photoFile.absolutePath)
                            putExtra("is_stunting", imageAnalyzer?.useStuntingModel)
                        }
                        startActivity(intent)
                    }
                }
            })
    }

    private fun loadModelFile(modelName: String): ByteBuffer {
        requireContext().assets.openFd(modelName).use { fileDescriptor ->
            FileInputStream(fileDescriptor.fileDescriptor).use { inputStream ->
                val fileChannel = inputStream.channel
                val startOffset = fileDescriptor.startOffset
                val declaredLength = fileDescriptor.declaredLength
                return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
            }
        }
    }

    private fun createFile(): File {
        val dir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File(dir, "${System.currentTimeMillis()}.jpg")
    }

    private fun updateModelSwitchText(isStunting: Boolean) {
        val modelType = if (isStunting) "Stunting Detection" else "Nutrition Detection"
        binding.modelSwitch.text = modelType
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
    }
}