package uz.creator.worldnews.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import uz.creator.worldnews.R
import uz.creator.worldnews.databinding.ItemArticleHorizontalBinding
import uz.creator.worldnews.models.Article
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class NewsAdapterHorizontal : RecyclerView.Adapter<NewsAdapterHorizontal.ArticleViewHolder>() {

    inner class ArticleViewHolder(var binding: ItemArticleHorizontalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(article: Article) {
            Picasso.get().load(article.urlToImage).error(R.drawable.errorplaceholder)
                .placeholder(R.drawable.loadplaceholder).into(binding.ivArticleImage)
            binding.tvSource.text = article.source?.name
            binding.tvPublishedAt.text = convertISOTime(
                binding.root.context,
                article.publishedAt
            )

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
            ItemArticleHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    private fun convertISOTime(context: Context, isoTime: String?): String? {

        val sdf = SimpleDateFormat(context.getString(R.string.default_time_format))
        var convertedDate: Date? = null
        var formattedDate: String? = null
        var formattedTime: String = "10:00 AM"
        try {
            convertedDate = sdf.parse(isoTime)
            formattedDate =
                SimpleDateFormat(context.getString(R.string.date_format)).format(convertedDate)
            formattedTime =
                SimpleDateFormat(context.getString(R.string.time_format)).format(convertedDate)

            if ((formattedTime.subSequence(6, 8).toString() == "PM" || formattedTime.subSequence(
                    6,
                    8
                ).toString() == "pm") && formattedTime.subSequence(0, 2).toString().toInt() > 12
            ) {
                formattedTime = (formattedTime.subSequence(0, 2).toString()
                    .toInt() - 12).toString() + formattedTime.subSequence(2, 8).toString()
            }
            if (formattedTime.subSequence(0, 2).toString() == "00") {
                formattedTime = (formattedTime.subSequence(0, 2).toString()
                    .toInt() + 1).toString() + formattedTime.subSequence(2, 8).toString()

            }
            if (formattedTime.subSequence(0, 2).toString() == "0:") {
                formattedTime = (formattedTime.subSequence(0, 1).toString()
                    .toInt() + 1).toString() + formattedTime.subSequence(2, 8).toString()

            }

        } catch (e: ParseException) {
            e.printStackTrace()
            Log.e("Error Date ", e.message!!)
        }
        return "$formattedDate | $formattedTime"
    }
}