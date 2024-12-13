package com.teamproject.petis.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.teamproject.petis.api.ApiClient
import com.teamproject.petis.response.ArticleResponse
import com.teamproject.petis.utils.SharedPreferencesManager
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferencesManager = SharedPreferencesManager(application)

    private val _articles = MutableLiveData<List<ArticleResponse>>()
    val articles: LiveData<List<ArticleResponse>> = _articles

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> = _username

    private val _greeting = MutableLiveData<String>()
    val greeting: LiveData<String> = _greeting

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        fetchArticles()
        setUsername()
        setGreeting()
    }

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
                            // Ambil 4 artikel pertama untuk ditampilkan di home
                            val limitedArticles = response.body()?.take(4) ?: emptyList()
                            _articles.value = limitedArticles
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
                _error.value = "Error: ${e.message}"
            }
        }
    }

    private fun setUsername() {
        val userName = sharedPreferencesManager.getUserName()
        Log.d("HomeViewModel", "Setting username: $userName")
        _username.value = userName
    }

    private fun setGreeting() {
        val currentTime = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when (currentTime) {
            in 0..11 -> "Welcome and Good Morning,"
            in 12..15 -> "Welcome and Good Afternoon,"
            in 16..20 -> "Welcome and Good Evening,"
            else -> "Welcome and Good Night,"
        }
        _greeting.value = greeting
    }
}