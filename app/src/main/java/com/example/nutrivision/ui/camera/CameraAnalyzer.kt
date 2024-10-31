package com.example.nutrivision.ui.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.YuvImage
import android.media.Image
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.nutrivision.ui.custom.OverlayView
import org.tensorflow.lite.Interpreter
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ImageAnalyzer(
    private val stuntingModel: Interpreter,
    private val nutritionModel: Interpreter,
    private val overlayView: OverlayView
) : ImageAnalysis.Analyzer {

    var useStuntingModel: Boolean = true
    private val inputSize = 640
    private val outputSizeStunting = Array(1) { Array(6) { FloatArray(8400) } }
    private val outputSizeNutrition = Array(1) { Array(6) { FloatArray(8400) } }
    private var lastBitmap: Bitmap? = null

    override fun analyze(imageProxy: ImageProxy) {
        lastBitmap = convertToBitmap(imageProxy)
        analyzeImage(lastBitmap!!) // Analyze the image and draw bounding boxes
        imageProxy.close()
    }

    @OptIn(ExperimentalGetImage::class)
    private fun convertToBitmap(imageProxy: ImageProxy): Bitmap {
        val image = imageProxy.image ?: return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

        // Check if the format is YUV_420_888
        if (imageProxy.format == ImageFormat.YUV_420_888) {
            val nv21 = yuv420888ToNv21(image)
            val yuvImage = YuvImage(nv21, ImageFormat.NV21, imageProxy.width, imageProxy.height, null)
            val out = ByteArrayOutputStream()
            yuvImage.compressToJpeg(android.graphics.Rect(0, 0, imageProxy.width, imageProxy.height), 100, out)
            val yuvByteArray = out.toByteArray()

            // Decode the bitmap and ensure it matches inputSize
            val bitmap = BitmapFactory.decodeByteArray(yuvByteArray, 0, yuvByteArray.size)
            return Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true) // Scale to input size
        }
        return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    }

    private fun yuv420888ToNv21(image: Image): ByteArray {
        val ySize = image.planes[0].buffer.remaining()
        val uSize = image.planes[1].buffer.remaining()
        val vSize = image.planes[2].buffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)
        image.planes[0].buffer.get(nv21, 0, ySize)
        image.planes[2].buffer.get(nv21, ySize, vSize)
        image.planes[1].buffer.get(nv21, ySize + vSize, uSize)

        return nv21
    }

    fun getLastBitmap(): Bitmap? {
        return lastBitmap
    }
    private fun analyzeImage(bitmap: Bitmap) {
        val width = bitmap.width
        val height = bitmap.height

        // Ensure dimensions match the expected input size
        if (width != inputSize || height != inputSize) {
            Log.e("ImageAnalyzer", "Bitmap dimensions do not match expected input size: $width x $height")
            return
        }

        val byteBuffer = ByteBuffer.allocateDirect(inputSize * inputSize * 3 * 4)
        byteBuffer.order(ByteOrder.nativeOrder())

        for (y in 0 until inputSize) {
            for (x in 0 until inputSize) {
                val pixel = bitmap.getPixel(x, y)
                byteBuffer.putFloat((pixel shr 16 and 0xFF) / 255f)
                byteBuffer.putFloat((pixel shr 8 and 0xFF) / 255f)
                byteBuffer.putFloat((pixel and 0xFF) / 255f)
            }
        }


        if (useStuntingModel) {
            stuntingModel.run(byteBuffer, outputSizeStunting)
            Log.d("ImageAnalyzer", "Stunting model output: ${outputSizeStunting.contentDeepToString()}")

            // Assuming the output contains classification probabilities
            val classificationResults = outputSizeStunting[0] // Get the first batch output
            logClassificationResults(classificationResults)

            drawBoundingBoxes(outputSizeStunting) // Call updated draw method
        } else {
            nutritionModel.run(byteBuffer, outputSizeNutrition)
            Log.d("ImageAnalyzer", "Nutrition model output: ${outputSizeNutrition.contentDeepToString()}")

            // Assuming the output contains classification probabilities
            val classificationResults = outputSizeNutrition[0] // Get the first batch output
            logClassificationResults(classificationResults)

            drawBoundingBoxes(outputSizeNutrition) // Call updated draw method
        }
    }
    private fun logClassificationResults(results: Array<FloatArray>) {
        // Assuming results contain probabilities for stunting (e.g., [not_stunted, stunted])
        if (results.isNotEmpty() && results[0].isNotEmpty()) {
            val stuntedProbability = results[0][1] // Assuming index 1 is the probability of stunting
            val notStuntedProbability = results[0][0] // Assuming index 0 is the probability of not stunting

            Log.i("ClassificationResult", "Probability of Stunting: $stuntedProbability")
            Log.i("ClassificationResult", "Probability of Not Stunting: $notStuntedProbability")

            // Determine if stunting is detected based on a threshold
            val threshold = 0.5 // Adjust this threshold based on your needs
            if (stuntedProbability > threshold) {
                Log.i("ClassificationResult", "Result: Stunting Detected")
                // Perform actions for detected stunting, e.g., update UI
            } else {
                Log.i("ClassificationResult", "Result: No Stunting Detected")
                // Perform actions for no stunting detected
            }
        } else {
            Log.e("ClassificationResult", "Invalid results from model.")
        }
    }


    private fun drawBoundingBoxes(output: Array<Array<FloatArray>>) {
        // Here, implement the logic to draw bounding boxes on the overlayView
        // For example:
        overlayView.clear() // Clear previous boxes

        // Assuming output contains bounding box data, you'll need to interpret it:
        for (i in output[0].indices) { // Adjust as needed based on your model's output format
            val left = output[0][i][0] * overlayView.width
            val top = output[0][i][1] * overlayView.height
            val right = output[0][i][2] * overlayView.width
            val bottom = output[0][i][3] * overlayView.height

            overlayView.addBoundingBox(left, top, right, bottom)
        }
    }
}
