package com.example.nutrivision.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BoundingBox(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
    val label: String,
    val score: Float
) : Parcelable
