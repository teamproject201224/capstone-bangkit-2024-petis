package com.teamproject.petis.db

import androidx.lifecycle.LiveData

class HistoryRepository(private val historyDao: HistoryDao) {
    val allHistory: LiveData<List<HistoryEntity>> = historyDao.getAllHistory()

    suspend fun insert(historyEntity: HistoryEntity) {
        historyDao.insert(historyEntity)
    }

    suspend fun delete(historyEntity: HistoryEntity) {
        historyDao.delete(historyEntity)
    }
}