package com.example.nutrivision.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nutrivision.R
import com.example.nutrivision.data.FoodItem
import com.example.nutrivision.databinding.FoodrecommendationItemBinding


class FoodRecommendAdapter(
    private val context: Context,
    private val items: List<FoodItem>
) : RecyclerView.Adapter<FoodRecommendAdapter.FoodViewHolder>() {

    // ViewHolder class to hold the views for each item
    class FoodViewHolder(private val binding: FoodrecommendationItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FoodItem) {
            binding.ivFoodImage.setImageResource(item.imageResId) // Set the image resource

        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = FoodrecommendationItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return FoodViewHolder(binding)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item) // Bind the item data to the view
    }

    // Return the size of the dataset (invoked by the layout manager)
    override fun getItemCount(): Int = items.size
}
