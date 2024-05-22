package com.ertools.memofy.ui.categories

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ertools.memofy.model.categories.Category
import com.ertools.memofy.databinding.ItemCategoryBinding
import com.google.android.material.snackbar.Snackbar

class CategoriesAdapter(
    private val context: Context,
    private val categories: List<Category>
) : RecyclerView.Adapter<CategoriesAdapter.ItemCategoryHolder>() {

    inner class ItemCategoryHolder(var view: ItemCategoryBinding)
        : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemCategoryHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ItemCategoryHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemCategoryHolder, position: Int) {
        val category: Category = categories[position]
        val view = holder.view

        view.categoryName.text = category.name
        if(category.resourceColor != null)
            view.categoryName.setTextColor(category.resourceColor)
        //view.categoryName.setTextColor(context.resources.getColor(category.resourceColor, null))

        view.categoryCardView.setOnClickListener {
            Snackbar.make(it, category.name?: "", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return categories.size
    }
}