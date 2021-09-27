package uz.creator.worldnews.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import uz.creator.worldnews.BaseApplication
import uz.creator.worldnews.models.NewsResponse
import uz.creator.worldnews.repository.NewsRepository
import uz.creator.worldnews.utils.Resource
import uz.creator.worldnews.utils.UtilMethods
import java.io.IOException

class NewsViewModel(
    app: Application,
    private val newsRepository: NewsRepository
) : AndroidViewModel(app) {

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    private var breakingNewsResponse: NewsResponse? = null

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    private var searchNewsResponse: NewsResponse? = null
    private var newSearchQuery: String? = null
    private var oldSearchQuery: String? = null

    val topStoriesNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    private var topStoriesNewsResponse: NewsResponse? = null

    val internationalNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    private var internationalNewsResponse: NewsResponse? = null

    val searchNewsForSame: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    private var searchNewsForSameResponse: NewsResponse? = null

    init {
        getBreakingNews("us")
        getTopStoriesNews()
        getInternationalNews()
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch(Dispatchers.IO) {
        safeSearchNewsCall(searchQuery)
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch(Dispatchers.IO) {
        safeBreakingNewsCall(countryCode)
    }

    private fun getTopStoriesNews() = viewModelScope.launch(Dispatchers.IO) {
        safeTopStoriesNewsCall()
    }

    private fun getInternationalNews() = viewModelScope.launch(Dispatchers.IO) {
        safeInternationalNewsCall()
    }

    fun searchNewsForSame(searchQuery: String) = viewModelScope.launch(Dispatchers.IO) {
        safeSearchNewsForSame(searchQuery)
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                breakingNewsPage++
                if (breakingNewsResponse == null) {
                    breakingNewsResponse = resultResponse
                } else {
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }


    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                if (searchNewsResponse == null || newSearchQuery != oldSearchQuery) {
                    searchNewsPage = 1
                    oldSearchQuery = newSearchQuery
                    searchNewsResponse = resultResponse
                } else {
                    searchNewsPage++
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleTopStoriesResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                if (topStoriesNewsResponse == null) {
                    topStoriesNewsResponse = resultResponse
                } else {
                    val oldArticles = topStoriesNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(topStoriesNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleInternationalResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                if (internationalNewsResponse == null) {
                    internationalNewsResponse = resultResponse
                } else {
                    val oldArticles = internationalNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(internationalNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsForSame(response: Response<NewsResponse>): Resource<NewsResponse>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                if (searchNewsForSameResponse == null) {
                    searchNewsForSameResponse = resultResponse
                } else {
                    val oldArticles = searchNewsForSameResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsForSameResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeSearchNewsCall(searchQuery: String) {
        newSearchQuery = searchQuery
        searchNews.postValue(Resource.Loading())
        try {
            if (UtilMethods.isInternetAvailable(getApplication<BaseApplication>())) {
                val response = newsRepository.searchNews(searchQuery, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            } else {
                searchNews.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchNews.postValue(Resource.Error("Network Failure"))
                else -> searchNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeBreakingNewsCall(countryCode: String) {
        breakingNews.postValue(Resource.Loading())
        try {
            if (UtilMethods.isInternetAvailable(getApplication<BaseApplication>())) {
                val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
                breakingNews.postValue(handleBreakingNewsResponse(response))
            } else {
                breakingNews.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> breakingNews.postValue(Resource.Error("Network Failure"))
                else -> breakingNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeTopStoriesNewsCall() {
        topStoriesNews.postValue(Resource.Loading())
        try {
            if (UtilMethods.isInternetAvailable(getApplication<BaseApplication>())) {
                val response = newsRepository.searchNews("Top Story", 1)
                topStoriesNews.postValue(handleTopStoriesResponse(response))
            } else {
                topStoriesNews.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> topStoriesNews.postValue(Resource.Error("Network Failure"))
                else -> topStoriesNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeInternationalNewsCall() {
        internationalNews.postValue(Resource.Loading())
        try {
            if (UtilMethods.isInternetAvailable(getApplication<BaseApplication>())) {
                val response = newsRepository.internationalNews()
                internationalNews.postValue(handleInternationalResponse(response))
            } else {
                internationalNews.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> internationalNews.postValue(Resource.Error("Network Failure"))
                else -> internationalNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeSearchNewsForSame(searchQuery: String) {
        searchNewsForSame.postValue(Resource.Loading())
        try {
            if (UtilMethods.isInternetAvailable(getApplication<BaseApplication>())) {
                val response = newsRepository.searchNewsForSame(searchQuery)
                searchNewsForSame.postValue(handleSearchNewsForSame(response))
            } else {
                searchNewsForSame.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchNewsForSame.postValue(Resource.Error("Network Failure"))
                else -> searchNewsForSame.postValue(Resource.Error("Conversion Error"))
            }
        }
    }
}