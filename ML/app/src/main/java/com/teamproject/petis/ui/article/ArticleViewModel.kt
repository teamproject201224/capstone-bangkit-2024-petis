package com.teamproject.petis.ui.article

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamproject.petis.api.ApiClient
import com.teamproject.petis.response.ArticleResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArticleViewModel : ViewModel() {
    private val _articles = MutableLiveData<List<ArticleResponse>>()
    val articles: LiveData<List<ArticleResponse>> = _articles

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchArticles() {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                ApiClient.apiService.getArticles().enqueue(object : Callback<List<ArticleResponse>> {
                    override fun onResponse(
                        call: Call<List<ArticleResponse>>,
                        response: Response<List<ArticleResponse>>
                    ) {
                        _isLoading.value = false
                        if (response.isSuccessful) {
                            _articles.value = response.body() ?: emptyList()
                        } else {
                            _error.value = "Failed to fetch articles: ${response.code()}"
                        }
                    }

                    override fun onFailure(call: Call<List<ArticleResponse>>, t: Throwable) {
                        _isLoading.value = false
                        _error.value = "Error: ${t.message}"
                    }
                })
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = "Unexpected error: ${e.localizedMessage}"
            }
        }
    }
}