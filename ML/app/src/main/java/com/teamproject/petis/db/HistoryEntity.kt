package com.teamproject.petis.db

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "history_table")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val prediction: String = "",
    val date: String = "",
    val imageUrl: String = "",
    val confidence: Float = 0f
) : Parcelable