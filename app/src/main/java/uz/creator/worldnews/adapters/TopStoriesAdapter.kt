package uz.creator.worldnews.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import uz.creator.worldnews.R
import uz.creator.worldnews.databinding.ItemRoundTopHeadlinesBinding
import uz.creator.worldnews.models.Article

class TopStoriesAdapter : RecyclerView.Adapter<TopStoriesAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(var binding: ItemRoundTopHeadlinesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(article: Article) {
            Picasso.get().load(article.urlToImage).error(R.drawable.errorplaceholder)
                .placeholder(R.drawable.loadplaceholder).into(binding.imageViewTopHeadlinesRound)
            when {
                article.title != null -> {
                    binding.textViewTopHeadlinesRound.text = article.title
                }
                article.author != null -> {
                    binding.textViewTopHeadlinesRound.text = article.author
                }
                else -> {
                    binding.textViewTopHeadlinesRound.text = article.description
                }
            }

            binding.root.setOnClickListener {
                onItemClickListener?.let { it(article) }
            }
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            ItemRoundTopHeadlinesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Article) -> Unit)? = null

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.onBind(differ.currentList[position])
    }

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }
}