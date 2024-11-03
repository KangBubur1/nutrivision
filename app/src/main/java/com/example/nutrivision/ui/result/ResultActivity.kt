package com.example.nutrivision.ui.result

import android.graphics.BitmapFactory
import android.net.Uri
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

        // Retrieve the image path and bounding boxes from the intent
        val imagePath = intent.getStringExtra("captured_image_path")
        val imageUriString = intent.getStringExtra("captured_image_uri")
        val boundingBoxes: List<BoundingBox>? = intent.getParcelableArrayListExtra("boundingBoxes")

        // Display the captured image based on the source
        if (!imagePath.isNullOrEmpty()) {
            // Image captured from camera
            val bitmap = BitmapFactory.decodeFile(imagePath)
            binding.capturedImageView.setImageBitmap(bitmap)
        } else if (!imageUriString.isNullOrEmpty()) {
            // Image selected from gallery
            val imageUri = Uri.parse(imageUriString)
            binding.capturedImageView.setImageURI(imageUri)
        }

        // Display classification results
        displayClassificationResults(boundingBoxes)

        // Update the final result based on bounding boxes
        val finalResultLabel = determineFinalResult(boundingBoxes)
        binding.finalResultLabel.text = finalResultLabel
    }

    private fun determineFinalResult(boundingBoxes: List<BoundingBox>?): String {
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
