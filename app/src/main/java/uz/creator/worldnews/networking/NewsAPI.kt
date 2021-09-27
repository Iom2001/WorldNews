package uz.creator.worldnews.networking

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import uz.creator.worldnews.models.NewsResponse
import uz.creator.worldnews.utils.Constants.API_KEY

interface NewsAPI {

    @GET("top-headlines")
    suspend fun getBreakingNews(
        @Query("country")
        countryCode: String = "us",
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>

    @GET("everything")
    suspend fun getInternational(
//        @Query("language")
//        language: String = "us",
        @Query("q")
        searchQuery: String = "international",
        @Query("page")
        pageNumber: Int = 1,
        @Query("pageSize")
        pageSize: Int = 6,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>

    @GET("everything")
    suspend fun searchForNews(
        @Query("q")
        searchQuery: String,
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>

    @GET("everything")
    suspend fun searchNewsForSame(
        @Query("q")
        searchQuery: String,
        @Query("page")
        pageNumber: Int = 1,
        @Query("pageSize")
        pageSize: Int = 6,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>

}