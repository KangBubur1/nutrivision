package com.example.nutrivision.ui.result


import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nutrivision.R
import com.example.nutrivision.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the data passed from CameraFragment
        val imagePath = intent.getStringExtra("captured_image")
        val isStunting = intent.getBooleanExtra("is_stunting", false)

        // Set the captured image
        imagePath?.let {
            val bitmap = BitmapFactory.decodeFile(it)
            binding.capturedImageView.setImageBitmap(bitmap)
        }

        // Display classification result
        val classificationResult = if (isStunting) {
            "Stunting Detected"
        } else {
            "Nutrition Food Detected"
        }
        binding.classificationTextView.text = classificationResult
    }
}
