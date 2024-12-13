package com.teamproject.petis.helper

import android.content.Context
import android.net.Uri
import android.util.Log
import com.teamproject.petis.api.PredictionResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class ImageClassifier(private val context: Context) {
    private val apiService = PredictionApiService.create()

    fun classifyImage(imageUri: Uri, callback: (PredictionResponse, String) -> Unit) {
        try {
            val file = convertUriToFile(imageUri)

            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)

            apiService.predictImage(imagePart).enqueue(object : Callback<PredictionResponse> {
                override fun onResponse(
                    call: Call<PredictionResponse>,
                    response: Response<PredictionResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { predictionResponse ->
                            // Gunakan metode getPredictedClassName untuk mendapatkan deskripsi
                            val description = predictionResponse.getPredictedClassName(context)

                            Log.d("ImageClassifier", "Raw Prediction: ${predictionResponse.predictedClass}")
                            Log.d("ImageClassifier", "Mapped Description: $description")

                            callback(predictionResponse, description)
                        } ?: callback(
                            PredictionResponse(
                                confidence = 0.0,
                                predictedClass = "Unrecognized",
                                products = emptyList()
                            ),
                            "Unrecognized"
                        )
                    } else {
                        Log.e("ImageClassifier", "Error Response: ${response.errorBody()?.string()}")
                        callback(
                            PredictionResponse(
                                confidence = 0.0,
                                predictedClass = "Error: ${response.code()} - ${response.message()}",
                                products = emptyList()
                            ),
                            "There is an error"
                        )
                    }
                }

                override fun onFailure(call: Call<PredictionResponse>, t: Throwable) {
                    Log.e("ImageClassifier", "Network Error: ${t.message}")
                    callback(
                        PredictionResponse(
                            confidence = 0.0,
                            predictedClass = "Failed to connect: ${t.message}",
                            products = emptyList()
                        ),
                        "Connection failed"
                    )
                }
            })
        } catch (e: Exception) {
            Log.e("ImageClassifier", "Exception: ${e.message}")
            callback(
                PredictionResponse(
                    confidence = 0.0,
                    predictedClass = "Error: ${e.message}",
                    products = emptyList()
                ),
                "Internal error"
            )
        }
    }

    private fun convertUriToFile(uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")

        inputStream?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }

        return tempFile
    }

    fun close() {
    }
}