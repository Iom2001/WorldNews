package uz.creator.worldnews.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import uz.creator.worldnews.R
import uz.creator.worldnews.adapters.NewsAdapter
import uz.creator.worldnews.databinding.FragmentSavedBinding
import uz.creator.worldnews.db.SavedDatabase
import uz.creator.worldnews.models.Article

class SavedFragment : Fragment() {

    private lateinit var binding: FragmentSavedBinding
    private lateinit var savedDatabase: SavedDatabase

    //    private lateinit var articleList: ArrayList<Article>
    private lateinit var newsAdapter: NewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedDatabase = SavedDatabase.invoke(requireContext())
//        articleList = ArrayList()
        newsAdapter = NewsAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSavedBinding.inflate(inflater, container, false)
        binding.rvSaved.adapter = newsAdapter
        newsAdapter.setOnItemClickListener { article ->
            val bundle = Bundle().apply {
                putSerializable("article", article)
            }
            findNavController().navigate(
                R.id.action_mainFragment_to_articleFragment,
                bundle
            )
        }
        getSavedArticle()
        return binding.root
    }

    private fun getSavedArticle() {
        savedDatabase.savedDao().getBookmarks().observe(viewLifecycleOwner, Observer {
//            articleList.addAll(it)
            if (it != null) {
                if (it != null || it.size > 0)
                    newsAdapter.differ.submitList(it)
            }
        })
    }
}