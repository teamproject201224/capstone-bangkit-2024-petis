package com.teamproject.petis.ui.article

import com.teamproject.petis.api.ApiClient
import com.teamproject.petis.response.ArticleResponse
import retrofit2.Call

class ArticleRepository {
    private val apiService = ApiClient.apiService

    fun getArticles(): Call<List<ArticleResponse>> {
        return apiService.getArticles()
    }
}