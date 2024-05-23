package com.ertools.memofy.ui.task

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ertools.memofy.databinding.ItemAnnexBinding
import com.ertools.memofy.model.annexes.Annex

class AnnexAdapter(
    private val context: Context
) : RecyclerView.Adapter<AnnexAdapter.ItemAnnexHolder>() {
    private var annexes: List<Annex> = emptyList()

    inner class ItemAnnexHolder(var view: ItemAnnexBinding)
        : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAnnexHolder {
        val binding = ItemAnnexBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ItemAnnexHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemAnnexHolder, position: Int) {
        val annex: Annex = annexes[position]
        val view = holder.view

        view.annexName.text = annex.name
    }

    fun submitAnnexes(newAnnex: List<Annex>) {
        annexes = newAnnex
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return annexes.size
    }
}