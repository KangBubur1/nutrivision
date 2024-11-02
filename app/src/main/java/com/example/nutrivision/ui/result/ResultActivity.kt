package com.example.nutrivision.ui.result

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nutrivision.data.BoundingBox
import com.example.nutrivision.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the image path and classification results
        val imagePath = intent.getStringExtra("captured_image")
        val boundingBoxes: List<BoundingBox>? = intent.getParcelableArrayListExtra("boundingBoxes")

        // Set the captured image
        imagePath?.let {
            val bitmap = BitmapFactory.decodeFile(it)
            binding.capturedImageView.setImageBitmap(bitmap)
        }

        // Display classification results
        displayClassificationResults(boundingBoxes)

        // Update the final result based on bounding boxes
        val finalResultLabel = determineFinalResult(boundingBoxes)
        binding.finalResultLabel.text = finalResultLabel
    }

    private fun determineFinalResult(boundingBoxes: List<BoundingBox>?): String {
        // Check if there are any bounding boxes and return the appropriate result
        return if (boundingBoxes.isNullOrEmpty()) {
            "No detections"
        } else {
            val hasStunting = boundingBoxes.any { it.label == "Stunting" }
            if (hasStunting) "Result: Stunting" else "Result: Not Stunting"
        }
    }

    private fun displayClassificationResults(boundingBoxes: List<BoundingBox>?) {
        if (boundingBoxes.isNullOrEmpty()) {
            binding.classificationTextView.text = "No detections"
            return
        }

        val results = StringBuilder()
        boundingBoxes.forEach { box ->
            results.append("Label: ${box.label}, Score: ${String.format("%.2f", box.score)}\n")
        }

        binding.classificationTextView.text = results.toString()
    }
}
