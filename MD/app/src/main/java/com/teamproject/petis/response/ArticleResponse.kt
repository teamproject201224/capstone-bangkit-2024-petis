package com.teamproject.petis.response

import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArticleResponse(
	@SerializedName("id") val id: Int? = null,
	@SerializedName("image") val image: String = "",
	@SerializedName("name") val name: String = "",
	@SerializedName("title") val title: String = "",
	@SerializedName("link") val link: String = "",
	@SerializedName("content") val content: String = ""
) : Parcelable {
	// Gunakan name atau title sesuai kebutuhan
	val displayTitle: String
		get() = when {
			name.isNotEmpty() -> name
			title.isNotEmpty() -> title
			else -> "Untitled"
		}
}
