package com.ertools.memofy.ui.task

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ertools.memofy.databinding.ItemAnnexBinding
import com.ertools.memofy.database.annexes.Annex
import com.ertools.memofy.utils.BitmapConverter

class AnnexesRecyclerAdapter(
    private val context: Context,
    private val viewModel: TaskViewModel,
    private var annexes: List<Annex>
) : RecyclerView.Adapter<AnnexesRecyclerAdapter.ItemAnnexHolder>() {

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

        annex.thumbnail?.let {
            view.annexIcon.setImageBitmap(BitmapConverter.stringToBitmap(it))
        }

        view.annexName.text = annex.name

        view.annexDeleteButton.setOnClickListener {
            viewModel.deleteAnnex(annex)
        }
    }

    override fun getItemCount(): Int {
        return annexes.size
    }
}