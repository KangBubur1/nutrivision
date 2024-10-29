package com.example.nutrivision.ui.custom

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout

class CustomCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var cornerRadii: FloatArray = FloatArray(8)
    private val backgroundDrawable = GradientDrawable()

    init {
        // Set default gradient colors
        backgroundDrawable.orientation = GradientDrawable.Orientation.TOP_BOTTOM
        backgroundDrawable.colors = intArrayOf(
            0xFFE3F2FD.toInt(), // Light Blue (default)
            0xFFBBDEFB.toInt()  // Darker Blue (default)
        )
        background = backgroundDrawable
    }

    // Function to set each corner radius independently
    fun setCornerRadii(
        topLeft: Float,
        topRight: Float,
        bottomRight: Float,
        bottomLeft: Float
    ) {
        cornerRadii = floatArrayOf(
            topLeft, topLeft,
            topRight, topRight,
            bottomRight, bottomRight,
            bottomLeft, bottomLeft
        )
        backgroundDrawable.cornerRadii = cornerRadii
        background = backgroundDrawable
    }

    // Function to set the linear gradient colors
    fun setGradientColors(startColor: Int, endColor: Int) {
        backgroundDrawable.colors = intArrayOf(startColor, endColor)
        background = backgroundDrawable
    }

    // Function to set the linear gradient orientation
    fun setGradientOrientation(orientation: GradientDrawable.Orientation) {
        backgroundDrawable.orientation = orientation
        background = backgroundDrawable
    }
}
