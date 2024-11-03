package com.example.nutrivision.ui.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nutrivision.MainActivity
import com.example.nutrivision.R
import com.example.nutrivision.ui.signup.SignUp
import kotlinx.coroutines.delay

class Login : AppCompatActivity() {

    // Mock credentials
    private val mockEmail = "test@example.com"
    private val mockPassword = "password123"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText = findViewById<EditText>(R.id.etLoginEmail)
        val passwordEditText = findViewById<EditText>(R.id.etLoginPassword)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        val registerButton = findViewById<TextView>(R.id.tvRegister)
        val googleLoginButton = findViewById<Button>(R.id.btnGoogleLogin)
        val facebookLoginButton = findViewById<Button>(R.id.btnFacebookLogin)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (validateCredentials(email, password)) {
                // Successful login, navigate to MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Show error message
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
            }
        }

        registerButton.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        googleLoginButton.setOnClickListener {
            // Mock response for Google login
            Toast.makeText(this, "Attempting to log in with Google...", Toast.LENGTH_SHORT).show()

            Handler(Looper.getMainLooper()).postDelayed({
                // Navigate to MainActivity after mock delay
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Optional: call finish if you want to remove this activity from back stack
            }, 3000) // Adjust delay as needed
        }

        facebookLoginButton.setOnClickListener {
            // Mock response for Facebook login
            Toast.makeText(this, "Attempting to log in with Facebook...", Toast.LENGTH_SHORT).show()

            Handler(Looper.getMainLooper()).postDelayed({
                // Navigate to MainActivity after mock delay
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Optional: call finish if you want to remove this activity from back stack
            }, 3000) // Adjust delay as needed
        }
    }

    private fun validateCredentials(email: String, password: String): Boolean {
        return email == mockEmail && password == mockPassword
    }
}
