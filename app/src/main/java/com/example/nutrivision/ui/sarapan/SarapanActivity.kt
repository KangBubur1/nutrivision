package com.example.nutrivision.ui.sarapan

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nutrivision.databinding.ActivitySarapanBinding

class SarapanActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySarapanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySarapanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Menambahkan click listener untuk rlSayurRebus
        binding.rlSayurRebus.setOnClickListener {
            val intent = Intent(this, SayurRebusDetailActivity::class.java)
            startActivity(intent)
        }

        }
    }

