package com.example.nutrivision.ui.signup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nutrivision.R
import com.example.nutrivision.databinding.ActivitySignUpBinding

class SignUp : AppCompatActivity() {

    private lateinit var binding : ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

    }
}