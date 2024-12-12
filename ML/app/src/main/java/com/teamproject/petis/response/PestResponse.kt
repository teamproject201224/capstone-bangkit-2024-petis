package com.teamproject.petis.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

typealias PestResponse = List<PestResponseItem>

@Parcelize
data class PestResponseItem(
	@field:SerializedName("image")
	val image: String = "",

	@field:SerializedName("solution")
	val solution: String = "",

	@field:SerializedName("name")
	val name: String = "",

	@field:SerializedName("description")
	val description: String = ""
) : Parcelable