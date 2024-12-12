package com.teamproject.petis.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teamproject.petis.databinding.ItemHighlightBinding
import com.teamproject.petis.model.Highlight

class HighlightAdapter : ListAdapter<Highlight, HighlightAdapter.HighlightViewHolder>(
    HighlightDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HighlightViewHolder {
        val binding = ItemHighlightBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HighlightViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HighlightViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HighlightViewHolder(
        private val binding: ItemHighlightBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Highlight) {
            binding.apply {
                tvHighlightTitle.text = item.title
                tvHighlightDescription.text = item.description
                ivHighlightIcon.setImageResource(item.icon)

                // Opsional: Set background kustom jika disediakan
                item.backgroundDrawable?.let {
                    root.setBackgroundResource(it)
                }
            }
        }
    }

    class HighlightDiffCallback : DiffUtil.ItemCallback<Highlight>() {
        override fun areItemsTheSame(
            oldItem: Highlight,
            newItem: Highlight
        ): Boolean = oldItem.title == newItem.title

        override fun areContentsTheSame(
            oldItem: Highlight,
            newItem: Highlight
        ): Boolean = oldItem == newItem
    }
}