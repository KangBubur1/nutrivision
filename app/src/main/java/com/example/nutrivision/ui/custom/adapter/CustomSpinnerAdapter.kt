package com.example.nutrivision.ui.custom.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.nutrivision.R

class CustomSpinnerAdapter(
    context: Context,
    items: List<String>
) : ArrayAdapter<String>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createSpinnerItem(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createSpinnerItem(position, convertView, parent)
    }

    private fun createSpinnerItem(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?: LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false)
        val itemTextView: TextView = itemView.findViewById(R.id.tvSpinner)
        itemTextView.text = getItem(position)
        return itemView
    }
}