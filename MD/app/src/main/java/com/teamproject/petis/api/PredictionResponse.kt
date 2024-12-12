package com.teamproject.petis.api

import android.content.Context
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class PredictionResponse(
	@field:SerializedName("confidence")
	val confidence: Double,

	@field:SerializedName("predicted_class")
	val predictedClass: String,

	@field:SerializedName("products")
	val products: List<RecommendedProductResponse>
) : Parcelable {
	fun getPredictedClassName(context: Context): String {
		return try {
			context.assets.open("labels.txt").bufferedReader().use { reader ->
				reader.lineSequence()
					.firstOrNull { line ->
						// Format labels.txt: index:class:description
						line.contains(predictedClass, ignoreCase = true)
					}?.split(":")?.getOrNull(2) ?: predictedClass
			}
		} catch (e: Exception) {
			predictedClass
		}
	}
}