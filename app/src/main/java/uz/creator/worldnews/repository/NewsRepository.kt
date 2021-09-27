package uz.creator.worldnews.repository

import uz.creator.worldnews.networking.RetrofitInstance

class NewsRepository(
) {

    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery, pageNumber)

    suspend fun internationalNews() = RetrofitInstance.api.getInternational()

    suspend fun searchNewsForSame(searchQuery: String) =
        RetrofitInstance.api.searchNewsForSame(searchQuery)

}