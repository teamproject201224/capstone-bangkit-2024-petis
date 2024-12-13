package com.teamproject.petis.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.teamproject.petis.R
import com.teamproject.petis.databinding.ItemHistoryBinding
import com.teamproject.petis.db.HistoryEntity
import com.teamproject.petis.ui.detail.DetailHistoryActivity

class HistoryAdapter(
    private val onDeleteClick: (HistoryEntity) -> Unit
) : ListAdapter<HistoryEntity, HistoryAdapter.HistoryViewHolder>(HistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoryViewHolder(binding, onDeleteClick)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HistoryViewHolder(
        private val binding: ItemHistoryBinding,
        private val onDeleteClick: (HistoryEntity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(history: HistoryEntity) {
            with(binding) {
                historyText.text = history.prediction
                historyDate.text = history.date

                Glide.with(imageViewHistory.context)
                    .load(history.imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .into(imageViewHistory)

                deleteButton.setOnClickListener {
                    onDeleteClick(history)
                }

                // Navigasi ke DetailHistoryActivity saat item diklik
                root.setOnClickListener {
                    val context = it.context
                    val intent = Intent(context, DetailHistoryActivity::class.java).apply {
                        putExtra("HISTORY_ENTITY", history)
                    }
                    context.startActivity(intent)
                }
            }
        }
    }

    class HistoryDiffCallback : DiffUtil.ItemCallback<HistoryEntity>() {
        override fun areItemsTheSame(oldItem: HistoryEntity, newItem: HistoryEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HistoryEntity, newItem: HistoryEntity): Boolean {
            return oldItem == newItem
        }
    }
}