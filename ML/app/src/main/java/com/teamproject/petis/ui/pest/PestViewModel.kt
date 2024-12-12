package com.teamproject.petis.ui.pest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamproject.petis.response.PestResponseItem
import kotlinx.coroutines.launch

class PestViewModel(private val pestRepository: PestRepository) : ViewModel() {
    private val _pestList = MutableLiveData<List<PestResponseItem>>()
    val pestList: LiveData<List<PestResponseItem>> = _pestList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun fetchPestData() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = pestRepository.getPestList()
                _pestList.value = result
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun refreshPestData() {
        fetchPestData()
    }
}