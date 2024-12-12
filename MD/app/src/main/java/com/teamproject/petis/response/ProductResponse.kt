package com.teamproject.petis.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

typealias ProductResponse = List<ProductResponseItem>

@Parcelize
data class ProductResponseItem(
	@field:SerializedName("image")
	val image: String = "",

	@field:SerializedName("name")
	val name: String = "",

	@field:SerializedName("description")
	val description: String = "",

	@field:SerializedName("type")
	val type: String = ""
) : Parcelable