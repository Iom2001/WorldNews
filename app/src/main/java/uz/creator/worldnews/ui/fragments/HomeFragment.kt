package uz.creator.worldnews.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import uz.creator.worldnews.R
import uz.creator.worldnews.adapters.HomeViewpagerAdapter
import uz.creator.worldnews.adapters.NewsAdapter
import uz.creator.worldnews.adapters.TopStoriesAdapter
import uz.creator.worldnews.databinding.FragmentHomeBinding
import uz.creator.worldnews.ui.MainActivity
import uz.creator.worldnews.utils.Constants
import uz.creator.worldnews.utils.Constants.QUERY_PAGE_SIZE
import uz.creator.worldnews.utils.Resource
import uz.creator.worldnews.utils.UtilMethods
import uz.creator.worldnews.viewmodel.NewsViewModel
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var skeletonTopStores: Skeleton
    private lateinit var skeletonBreakingNews: Skeleton

    //    private lateinit var skeletonPagerNews: Skeleton
    private lateinit var topStoriesAdapter: TopStoriesAdapter
    private lateinit var homeViewpagerAdapter: HomeViewpagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        getInternetState()
        viewModel = (activity as MainActivity).viewModel

        setupRecyclerView()
        setUpNetworkingNews()

//        binding.dialogNoInternet.buttonTryAgain.setOnClickListener {
//            getInternetState()
//            setUpSkeleton()
//            setUpNetworkingNews()
//        }
        binding.homeSwipeRefresh.setColorSchemeResources(
            android.R.color.holo_blue_dark,
            android.R.color.holo_red_dark,
            android.R.color.holo_purple,
            android.R.color.holo_green_dark
        )
        binding.homeSwipeRefresh.setOnRefreshListener {
            getInternetState()
            setUpSkeleton()
            setUpNetworkingNews()
        }
        return binding.root
    }


    private fun setUpNetworkingNews() {

        viewModel.internationalNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { newsResponse ->
                        homeViewpagerAdapter.differ.submitList(newsResponse.articles.toList())
                    }
//                    skeletonPagerNews.showOriginal()
                    binding.homeSwipeRefresh.isRefreshing = false
                }
                is Resource.Error -> {
                    response.message?.let { message ->
                        Toast.makeText(activity, "An error occured: $message", Toast.LENGTH_LONG)
                            .show()
                        showErrorMessage(message)
                    }
                    binding.homeSwipeRefresh.isRefreshing = false
                }
                is Resource.Loading -> {
                }
            }
        })

        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    hideErrorMessage()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.breakingNewsPage == totalPages
                        if (isLastPage) {
                            binding.layoutMain.rvHome.setPadding(0, 0, 0, 0)
                        }
                    }
                    skeletonBreakingNews.showOriginal()
                    binding.homeSwipeRefresh.isRefreshing = false
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(activity, "An error occured: $message", Toast.LENGTH_LONG)
                            .show()
                        showErrorMessage(message)
                    }
                    binding.homeSwipeRefresh.isRefreshing = false
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })

        viewModel.topStoriesNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { newsResponse ->
                        topStoriesAdapter.differ.submitList(newsResponse.articles.toList())
                    }
                    skeletonTopStores.showOriginal()
                    binding.homeSwipeRefresh.isRefreshing = false
                }
                is Resource.Error -> {
                    response.message?.let { message ->
                        Toast.makeText(activity, "An error occured: $message", Toast.LENGTH_LONG)
                            .show()
                    }
                    binding.homeSwipeRefresh.isRefreshing = false
                }
                is Resource.Loading -> {
                }
            }
        })
    }

    private fun setupRecyclerView() {

        homeViewpagerAdapter = HomeViewpagerAdapter()
        binding.layoutMain.homeViewPager.adapter = homeViewpagerAdapter
        binding.layoutMain.homeViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        topStoriesAdapter = TopStoriesAdapter()
        binding.layoutMain.topStoriesRecyclerView.adapter = topStoriesAdapter

        newsAdapter = NewsAdapter()
        binding.layoutMain.rvHome.apply {
            adapter = newsAdapter
            addOnScrollListener(this@HomeFragment.scrollListener)
        }

        setUpSkeleton()

        homeViewpagerAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_mainFragment_to_articleFragment,
                bundle
            )
        }

        topStoriesAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_mainFragment_to_articleFragment,
                bundle
            )
        }

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_mainFragment_to_articleFragment,
                bundle
            )
        }
    }

    var isError = false
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNoErrors = !isError
            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate =
                isNoErrors && isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                        isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                viewModel.getBreakingNews("us")
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    private fun setUpSkeleton() {

//        if (!this::skeletonPagerNews.isInitialized || skeletonPagerNews == null) {
//            skeletonPagerNews = binding.layoutMain.homeViewPager.applySkeleton(
//                R.layout.shimmer_pager_headlines,
//                5
//            )
//            skeletonPagerNews.maskCornerRadius = 80F
//            skeletonPagerNews.shimmerDurationInMillis = 1500
//        }
//        skeletonPagerNews.showSkeleton()

        if (!this::skeletonBreakingNews.isInitialized || skeletonBreakingNews == null) {
            skeletonBreakingNews = binding.layoutMain.rvHome.applySkeleton(
                R.layout.shimmer_top_headlines,
                10
            )
            skeletonBreakingNews.maskCornerRadius = 40F
            skeletonBreakingNews.shimmerDurationInMillis = 1500
        }
        skeletonBreakingNews.showSkeleton()

        if (!this::skeletonTopStores.isInitialized || skeletonTopStores == null) {
            skeletonTopStores = binding.layoutMain.topStoriesRecyclerView.applySkeleton(
                R.layout.shimmer_round_top_headlines,
                6
            )
            skeletonTopStores.maskCornerRadius = 480F
            skeletonTopStores.shimmerDurationInMillis = 1500
        }
        skeletonTopStores.showSkeleton()
    }

    private fun getInternetState() {
        if (UtilMethods.isInternetAvailable(requireContext())) {
            hideErrorMessage()
        } else {
            showErrorMessage("No internet connection")
        }
    }

    private fun hideProgressBar() {
        binding.layoutMain.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.layoutMain.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideErrorMessage() {
//        MainActivity().hideNoInternetDialog()
        isError = false
    }

    private fun showErrorMessage(message: String) {
//        MainActivity().showNoInternetDialog()
        isError = true
    }
}