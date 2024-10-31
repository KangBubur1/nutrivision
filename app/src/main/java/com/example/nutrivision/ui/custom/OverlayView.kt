package com.example.nutrivision.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.nutrivision.R

class OverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val boxPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.bounding_box_color)
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }

    private val boundingBoxes = mutableListOf<RectF>()

    fun addBoundingBox(left: Float, top: Float, right: Float, bottom: Float) {
        boundingBoxes.add(RectF(left, top, right, bottom))
        invalidate() // Redraw the view
    }

    fun clear() {
        boundingBoxes.clear()
        invalidate() // Clear and redraw
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (box in boundingBoxes) {
            canvas.drawRect(box, boxPaint)
        }
    }
}
