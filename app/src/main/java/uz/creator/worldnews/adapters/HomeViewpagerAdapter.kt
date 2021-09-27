package uz.creator.worldnews.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import uz.creator.worldnews.R
import uz.creator.worldnews.databinding.ItemPagerNewsBinding
import uz.creator.worldnews.models.Article

class HomeViewpagerAdapter : RecyclerView.Adapter<HomeViewpagerAdapter.ArticleViewHolder>() {

    private var onItemClickListener: ((Article) -> Unit)? = null

    inner class ArticleViewHolder(var binding: ItemPagerNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBinding(article: Article) {

            Picasso.get().load(article.urlToImage).error(R.drawable.errorplaceholder)
                .placeholder(R.drawable.loadplaceholder).into(binding.imagePreference)
            binding.textPreferences.text = article.title

            binding.root.setOnClickListener {
                onItemClickListener?.let { it(article) }
            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ArticleViewHolder {

        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_pager_news, parent, false)
        val lp: ViewGroup.LayoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        view.layoutParams = lp

        return ArticleViewHolder(
            ItemPagerNewsBinding.bind(view)
        )
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.onBinding(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }
}