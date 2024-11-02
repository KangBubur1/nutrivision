package com.example.nutrivision.ui.sarapan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nutrivision.databinding.ActivitySayurRebusDetailBinding

class SayurRebusDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySayurRebusDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySayurRebusDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Di sini Anda bisa menambahkan logika lain untuk SayurRebusDetailActivity
        // Misalnya menampilkan informasi lebih lanjut tentang Sayur Rebus
    }
}
