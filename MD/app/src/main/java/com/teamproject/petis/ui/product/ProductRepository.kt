package com.teamproject.petis.ui.product

import com.teamproject.petis.api.ApiService
import com.teamproject.petis.response.ProductResponseItem
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductRepository(private val apiService: ApiService) {
    suspend fun getProductList(): List<ProductResponseItem> = suspendCoroutine { continuation ->
        apiService.getProduct().enqueue(object : Callback<List<ProductResponseItem>> {
            override fun onResponse(
                call: Call<List<ProductResponseItem>>,
                response: Response<List<ProductResponseItem>>
            ) {
                if (response.isSuccessful) {
                    val productList = response.body() ?: emptyList()
                    continuation.resume(productList)
                } else {
                    continuation.resumeWithException(
                        Exception("Failed to fetch product data: ${response.code()}")
                    )
                }
            }

            override fun onFailure(call: Call<List<ProductResponseItem>>, t: Throwable) {
                continuation.resumeWithException(t)
            }
        })
    }
}
