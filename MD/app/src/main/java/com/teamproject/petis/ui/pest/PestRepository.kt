package com.teamproject.petis.ui.pest

import com.teamproject.petis.api.ApiService
import com.teamproject.petis.response.PestResponseItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class PestRepository(private val apiService: ApiService) {
    suspend fun getPestList(): List<PestResponseItem> = suspendCoroutine { continuation ->
        apiService.getPest().enqueue(object : Callback<List<PestResponseItem>> {
            override fun onResponse(
                call: Call<List<PestResponseItem>>,
                response: Response<List<PestResponseItem>>
            ) {
                if (response.isSuccessful) {
                    val pestList = response.body() ?: emptyList()
                    continuation.resume(pestList)
                } else {
                    continuation.resumeWithException(
                        Exception("Failed to fetch pest data: ${response.code()}")
                    )
                }
            }

            override fun onFailure(call: Call<List<PestResponseItem>>, t: Throwable) {
                continuation.resumeWithException(t)
            }
        })
    }
}