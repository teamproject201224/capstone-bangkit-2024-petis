package com.teamproject.petis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Suppress("DEPRECATED_ANNOTATION")
@Parcelize
data class ProductResponseItem(
    val id: Int,
    val name: String,
    val description: String,
    val image: String,
    val type: String
) : Parcelable