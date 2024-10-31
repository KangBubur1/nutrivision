package com.example.nutrivision.ui.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
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
        lastBitmap?.let {
            analyzeImage(it) // Analyze the image and draw bounding boxes
        }
        imageProxy.close()
    }

    @OptIn(ExperimentalGetImage::class)
    private fun convertToBitmap(imageProxy: ImageProxy): Bitmap {
        val image = imageProxy.image ?: return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

        if (imageProxy.format == ImageFormat.YUV_420_888) {
            val nv21 = yuv420888ToNv21(image)
            val yuvImage = YuvImage(nv21, ImageFormat.NV21, imageProxy.width, imageProxy.height, null)
            val out = ByteArrayOutputStream()
            yuvImage.compressToJpeg(Rect(0, 0, imageProxy.width, imageProxy.height), 100, out)
            val yuvByteArray = out.toByteArray()

            val bitmap = BitmapFactory.decodeByteArray(yuvByteArray, 0, yuvByteArray.size)
            return Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
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

    fun analyzeImage(bitmap: Bitmap) {
        val width = bitmap.width
        val height = bitmap.height

        if (width != inputSize || height != inputSize) {
            Log.e("ImageAnalyzer", "Bitmap dimensions do not match expected input size: $width x $height")
            return
        }

        val byteBuffer = ByteBuffer.allocateDirect(inputSize * inputSize * 3 * 4).apply {
            order(ByteOrder.nativeOrder())
        }

        for (y in 0 until inputSize) {
            for (x in 0 until inputSize) {
                val pixel = bitmap.getPixel(x, y)
                byteBuffer.putFloat((pixel shr 16 and 0xFF) / 255f) // Red
                byteBuffer.putFloat((pixel shr 8 and 0xFF) / 255f)  // Green
                byteBuffer.putFloat((pixel and 0xFF) / 255f)        // Blue
            }
        }

        if (useStuntingModel) {
            stuntingModel.run(byteBuffer, outputSizeStunting)
            Log.d("ImageAnalyzer", "Stunting model output: ${outputSizeStunting.contentDeepToString()}")
            drawBoundingBoxes(outputSizeStunting, "Stunting")
        } else {
            nutritionModel.run(byteBuffer, outputSizeNutrition)
            Log.d("ImageAnalyzer", "Nutrition model output: ${outputSizeNutrition.contentDeepToString()}")
            drawBoundingBoxes(outputSizeNutrition, "Nutrition")
        }
    }

    private fun drawBoundingBoxes(output: Array<Array<FloatArray>>, modelType: String) {
        overlayView.clear()
        val boundingBoxes = mutableListOf<OverlayView.BoundingBox>()
        val scoreThreshold = 0.3f // Set threshold value

        for (i in output[0].indices) {
            val score = output[0][i][4]
            Log.d("ImageAnalyzer", "Box $i: score = $score, bounding box = [${output[0][i][0]}, ${output[0][i][1]}, ${output[0][i][2]}, ${output[0][i][3]}]")
            if (score > scoreThreshold) { // Only consider detections above the threshold
                val left = output[0][i][0] * overlayView.width
                val top = output[0][i][1] * overlayView.height
                val right = output[0][i][2] * overlayView.width
                val bottom = output[0][i][3] * overlayView.height
                val label = if (modelType == "Stunting") "Stunting" else "Nutrition"

                boundingBoxes.add(OverlayView.BoundingBox(left, top, right, bottom, label, score))
            }
        }

        overlayView.setResults(boundingBoxes, imageHeight = 1080, imageWidth = 2069)
    }
}
