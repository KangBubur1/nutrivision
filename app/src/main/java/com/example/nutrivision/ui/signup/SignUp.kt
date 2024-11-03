package com.example.nutrivision.ui.signup

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nutrivision.R
import com.example.nutrivision.ui.login.Login

class SignUp : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Reference to the Register button and Login link
        val registerButton = findViewById<Button>(R.id.btnRegister)
        val loginText = findViewById<TextView>(R.id.tvRegister)

        // Mockup action for Register button
        registerButton.setOnClickListener {
            // Mock registration logic or show a confirmation message
            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()


            // Redirect to Login screen
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }

        // Navigate back to Login screen
        loginText.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }
}
