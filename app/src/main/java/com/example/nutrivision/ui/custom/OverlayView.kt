package com.example.nutrivision.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.nutrivision.R
import java.text.NumberFormat
import kotlin.math.max

class OverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var boxPaint = Paint()
    private var textBackgroundPaint = Paint()
    private var textPaint = Paint()

    private var results: MutableList<BoundingBox> = mutableListOf()
    private var scaleFactor: Float = 1f
    private var bounds = Rect()

    init {
        initPaints()
    }

    private fun initPaints() {
        boxPaint.color = ContextCompat.getColor(context, R.color.bounding_box_color)
        boxPaint.style = Paint.Style.STROKE
        boxPaint.strokeWidth = 8f

        textBackgroundPaint.color = Color.BLACK
        textBackgroundPaint.style = Paint.Style.FILL
        textBackgroundPaint.textSize = 50f

        textPaint.color = Color.WHITE
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = 50f
    }

    // Set the detection results, which include bounding boxes
    fun setResults(detectionResults: MutableList<BoundingBox>, imageHeight: Int, imageWidth: Int) {
        // Scale factor calculation
        scaleFactor = max(width.toFloat() / imageWidth, height.toFloat() / imageHeight)

        // Update results with scaled bounding boxes
        results.clear()
        detectionResults.forEach { boundingBox ->
            results.add(boundingBox.copy(
                left = boundingBox.left * scaleFactor,
                top = boundingBox.top * scaleFactor,
                right = boundingBox.right * scaleFactor,
                bottom = boundingBox.bottom * scaleFactor
            ))
        }
        invalidate() // Request redraw
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        for (result in results) {
            val drawableRect = RectF(result.left, result.top, result.right, result.bottom)

            // Draw bounding box around detected objects
            canvas.drawRect(drawableRect, boxPaint)

            // Create text to display alongside detected objects
            val drawableText = "${result.label} ${NumberFormat.getPercentInstance().format(result.score)}"

            // Draw rect behind display text
            textBackgroundPaint.getTextBounds(drawableText, 0, drawableText.length, bounds)
            val textWidth = bounds.width()
            val textHeight = bounds.height()
            canvas.drawRect(
                drawableRect.left,
                drawableRect.top,
                drawableRect.left + textWidth + BOUNDING_RECT_TEXT_PADDING,
                drawableRect.top + textHeight + BOUNDING_RECT_TEXT_PADDING,
                textBackgroundPaint
            )

            // Draw text for detected object
            canvas.drawText(drawableText, drawableRect.left, drawableRect.top + bounds.height(), textPaint)
        }
    }

    // Clear the overlay results
    fun clear() {
        results.clear()
        invalidate()
    }

    companion object {
        private const val BOUNDING_RECT_TEXT_PADDING = 8
    }

    // Data class representing a bounding box
    data class BoundingBox(
        val left: Float,
        val top: Float,
        val right: Float,
        val bottom: Float,
        val label: String,
        val score: Float
    )
}
