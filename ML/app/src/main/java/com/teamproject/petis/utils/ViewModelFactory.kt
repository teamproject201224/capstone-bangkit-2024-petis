package com.teamproject.petis.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.teamproject.petis.api.ApiService
import com.teamproject.petis.ui.pest.PestRepository
import com.teamproject.petis.ui.pest.PestViewModel



class ViewModelFactory(private val pestRepository: PestRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(PestViewModel::class.java) -> {
                PestViewModel(pestRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}