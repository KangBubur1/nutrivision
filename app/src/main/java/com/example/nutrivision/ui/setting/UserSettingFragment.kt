package com.example.nutrivision.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.nutrivision.databinding.UserSettingBinding
import com.example.nutrivision.R

class UserSettingFragment : Fragment() {

    private var _binding: UserSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = UserSettingBinding.inflate(inflater, container, false)

        // Mengatur klik pada lyMyAccount untuk navigasi ke ProfileFragment
        binding.lyMyAccount.setOnClickListener {
            findNavController().navigate(R.id.action_userSettingFragment_to_profileFragment)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
