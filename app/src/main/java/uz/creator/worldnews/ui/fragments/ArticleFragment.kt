package uz.creator.worldnews.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import uz.creator.worldnews.R
import uz.creator.worldnews.adapters.NewsAdapter
import uz.creator.worldnews.adapters.NewsAdapterHorizontal
import uz.creator.worldnews.databinding.FragmentArticleBinding
import uz.creator.worldnews.db.SavedDatabase
import uz.creator.worldnews.models.Article
import uz.creator.worldnews.ui.MainActivity
import uz.creator.worldnews.utils.Resource
import uz.creator.worldnews.viewmodel.NewsViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ArticleFragment : Fragment() {

    private lateinit var article: Article
    private lateinit var binding: FragmentArticleBinding
    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapterHorizontal: NewsAdapterHorizontal
    private lateinit var savedDatabase: SavedDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            article = it.getSerializable("article") as Article
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentArticleBinding.inflate(inflater, container, false)
        viewModel = (activity as MainActivity).viewModel
        article.source?.name?.let { viewModel.searchNewsForSame(it) }
        savedDatabase = SavedDatabase.invoke(requireContext())
        setUpNetwork()
        setUpRv()
        newsAdapterHorizontal.setOnItemClickListener { article ->
            val navOption = NavOptions.Builder()
            navOption.setEnterAnim(R.anim.slide_in_right)
            navOption.setExitAnim(R.anim.slide_out_left)
            navOption.setPopEnterAnim(R.anim.slide_in_left)
            navOption.setPopExitAnim(R.anim.slide_out_right)
            val bundle = Bundle().apply {
                putSerializable("article", article)
            }
            findNavController().navigate(R.id.articleFragment, bundle, navOption.build())
        }
        binding.tvMain.text = article.source?.name
        binding.titleTv.text = article.title
        binding.author.text = article.author
        Picasso.get().load(article.urlToImage).error(R.drawable.errorplaceholder)
            .placeholder(R.drawable.loadplaceholder).into(binding.articleImage)
        binding.descriptionTv1.text = article.description
        binding.descriptionTv2.text = article.content

        if (!article.content.isNullOrEmpty()) {
            if (article.content!!.contains('['))
                binding.descriptionTv2.text = article.content!!
                    .substringBeforeLast('[')
            else
                binding.descriptionTv2.text = article.content!!
        } else {
            binding.descriptionTv2.text = ""
        }

        binding.date.text = convertISOTime(
            requireContext(),
            article.publishedAt
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkBookmark()

        binding.saveArticle.setOnClickListener {
            if (article != null) {
                if (binding.saveArticle.tag == "Unsaved") {
                    var savedArticle = Article(
                        null,
                        article.author,
                        article.content,
                        article.description,
                        article.publishedAt,
                        article.source,
                        article.title,
                        article.url,
                        article.urlToImage
                    )
                    savedDatabase.savedDao().addBookmark(savedArticle)
                    binding.saveArticle.tag = "Saved"
                    binding.saveArticle.setImageResource(R.drawable.ic_bookmark_white_fill)
                } else {
                    article.title?.let { it1 -> savedDatabase.savedDao().removeBookmark(it1) }
                    binding.saveArticle.tag = "Unsaved"
                    binding.saveArticle.setImageResource(R.drawable.ic_bookmark_border)
                }
            }

        }
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(context, "$message", Toast.LENGTH_SHORT).show()
    }

    private fun setUpRv() {
        newsAdapterHorizontal = NewsAdapterHorizontal()
        binding.rvArticle.adapter = newsAdapterHorizontal
    }

    private fun setUpNetwork() {
        viewModel.searchNewsForSame.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { newsResponse ->
                        newsAdapterHorizontal.differ.submitList(newsResponse.articles.toList())
                    }
                }
                is Resource.Error -> {
                    response.message?.let { message ->
                        Toast.makeText(activity, "An error occured: $message", Toast.LENGTH_LONG)
                            .show()
                        showErrorMessage(message)
                    }
                }
                is Resource.Loading -> {
                }
            }
        })
    }

    @SuppressLint("RestrictedApi")
    fun checkBookmark() {
        var item = article.title?.let { savedDatabase.savedDao().checkBookmark(it) }
        if (!item.isNullOrEmpty()) {
            binding.saveArticle.tag = "Saved"
            binding.saveArticle.setImageResource(R.drawable.ic_bookmark_white_fill)
        } else {
            binding.saveArticle.tag = "Unsaved"
            binding.saveArticle.setImageResource(R.drawable.ic_bookmark_border)
        }
    }

    private fun convertISOTime(context: Context, isoTime: String?): String? {

        val sdf = SimpleDateFormat(getString(R.string.default_time_format))
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