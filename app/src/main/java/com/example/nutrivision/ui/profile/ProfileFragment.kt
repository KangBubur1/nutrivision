package com.example.nutrivision.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.nutrivision.R

class ProfileFragment : Fragment() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etBPJS: EditText
    private lateinit var etBirthdate: EditText
    private lateinit var etCity: EditText
    private lateinit var btnUpdateProfile: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi elemen UI
        etName = view.findViewById(R.id.etName)
        etEmail = view.findViewById(R.id.etEmail)
        etPhone = view.findViewById(R.id.etPhone)
        etBPJS = view.findViewById(R.id.etBPJS)
        etBirthdate = view.findViewById(R.id.etBirthdate)
        etCity = view.findViewById(R.id.etCity)
        btnUpdateProfile = view.findViewById(R.id.btnUpdateProfile)

        // Set aksi untuk tombol Update Profile
        btnUpdateProfile.setOnClickListener {
            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val phone = etPhone.text.toString()
            val bpjs = etBPJS.text.toString()
            val birthdate = etBirthdate.text.toString()
            val city = etCity.text.toString()

            // Logika untuk menyimpan atau memperbarui data pengguna
            if (name.isNotBlank() && email.isNotBlank() && phone.isNotBlank()) {
                Toast.makeText(activity, "Profile Updated!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
