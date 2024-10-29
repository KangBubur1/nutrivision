package com.example.nutrivision.ui.home

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nutrivision.R
import com.example.nutrivision.data.FoodItem
import com.example.nutrivision.databinding.FragmentHomeBinding
import com.example.nutrivision.ui.adapter.FoodRecommendAdapter
import com.example.nutrivision.ui.custom.adapter.CustomSpinnerAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var foodRecommendAdapter: FoodRecommendAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCardBackground()
        setupRecyclerView()
        setupLocationSpinner()
    }

    private fun setupRecyclerView() {
        binding.foodrecommendRV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val foodItems = listOf(
            FoodItem(imageResId = R.drawable.home, title = "Children Image 1"),
            FoodItem(imageResId = R.drawable.childrenimage, title = "Children Image 2"),
            FoodItem(imageResId = R.drawable.childrenimage, title = "Children Image 3"),
            FoodItem(imageResId = R.drawable.childrenimage, title = "Children Image 4"),
            FoodItem(imageResId = R.drawable.childrenimage, title = "Children Image 5")
        )
        foodRecommendAdapter = FoodRecommendAdapter(requireContext(), foodItems)
        binding.foodrecommendRV.adapter = foodRecommendAdapter
    }

    private fun setupCardBackground() {
        binding.cardBackground.setCornerRadii(0f, 0f, 75f, 75f)
        binding.cardBackground.setGradientColors(
            startColor = Color.parseColor("#0A9B62"),
            endColor = Color.parseColor("#4C137D53")
        )
        binding.cardBackground.setGradientOrientation(GradientDrawable.Orientation.LEFT_RIGHT)
    }

    private fun setupLocationSpinner() {
        val locations = listOf("Jogjakarta", "Jakarta", "Bandung", "Surabaya")
        val adapter = CustomSpinnerAdapter(requireContext(), locations)
        binding.locationSpinner.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
