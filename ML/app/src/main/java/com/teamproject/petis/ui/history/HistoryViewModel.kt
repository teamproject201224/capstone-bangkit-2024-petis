package com.teamproject.petis.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamproject.petis.db.HistoryEntity
import com.teamproject.petis.db.HistoryRepository
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val repository: HistoryRepository
) : ViewModel() {
    val allHistory: LiveData<List<HistoryEntity>> = repository.allHistory

    fun insert(historyEntity: HistoryEntity) = viewModelScope.launch {
        repository.insert(historyEntity)
    }

    fun delete(historyEntity: HistoryEntity) = viewModelScope.launch {
        repository.delete(historyEntity)
    }
}