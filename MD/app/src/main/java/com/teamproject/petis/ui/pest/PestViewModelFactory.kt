package com.teamproject.petis.ui.pest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.teamproject.petis.api.ApiClient

class PestViewModelFactory(private val pestRepository: PestRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PestViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PestViewModel(pestRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    companion object {
        fun create(): PestViewModelFactory {
            val apiService = ApiClient.apiService
            val pestRepository = PestRepository(apiService)
            return PestViewModelFactory(pestRepository)
        }
    }
}