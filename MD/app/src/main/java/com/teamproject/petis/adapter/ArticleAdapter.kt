package com.teamproject.petis.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.teamproject.petis.R
import com.teamproject.petis.databinding.ItemArticleBinding
import com.teamproject.petis.response.ArticleResponse

class ArticleAdapter(private val onItemClick: (ArticleResponse) -> Unit) :
    ListAdapter<ArticleResponse, ArticleAdapter.ArticleViewHolder>(ArticleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemArticleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ArticleViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ArticleViewHolder(
        private val binding: ItemArticleBinding,
        private val onItemClick: (ArticleResponse) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(article: ArticleResponse) {
            binding.apply {
                // Prioritaskan name, jika kosong gunakan title
                textViewEventName.text = article.displayTitle
                textViewArticleDescription.text = article.content.take(100) + "..."

                Glide.with(itemView.context)
                    .load(article.image)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .into(imageViewEventLogo)

                root.setOnClickListener {
                    onItemClick(article)
                }
            }
        }
    }

    class ArticleDiffCallback : DiffUtil.ItemCallback<ArticleResponse>() {
        override fun areItemsTheSame(
            oldItem: ArticleResponse,
            newItem: ArticleResponse
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: ArticleResponse,
            newItem: ArticleResponse
        ): Boolean = oldItem == newItem
    }
}