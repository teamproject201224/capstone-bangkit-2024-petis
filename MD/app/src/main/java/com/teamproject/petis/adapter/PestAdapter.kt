package com.teamproject.petis.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.teamproject.petis.R
import com.teamproject.petis.databinding.ItemPestBinding
import com.teamproject.petis.response.PestResponseItem

class PestAdapter(
    private val onItemClickListener: (PestResponseItem) -> Unit
) : ListAdapter<PestResponseItem, PestAdapter.PestViewHolder>(PestDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PestViewHolder {
        val binding = ItemPestBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PestViewHolder(binding, onItemClickListener)
    }

    override fun onBindViewHolder(holder: PestViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PestViewHolder(
        private val binding: ItemPestBinding,
        private val onItemClickListener: (PestResponseItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pest: PestResponseItem) {
            binding.apply {
                pestNameTextView.text = pest.name
                pestDescriptionTextView.text = pest.description

                Glide.with(itemView.context)
                    .load(pest.image)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(pestImageView)

                root.setOnClickListener { onItemClickListener(pest) }
            }
        }
    }

    class PestDiffCallback : DiffUtil.ItemCallback<PestResponseItem>() {
        override fun areItemsTheSame(oldItem: PestResponseItem, newItem: PestResponseItem): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: PestResponseItem, newItem: PestResponseItem): Boolean {
            return oldItem == newItem
        }
    }
}