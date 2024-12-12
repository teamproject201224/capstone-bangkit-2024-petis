package com.teamproject.petis.api

import com.teamproject.petis.response.ArticleResponse
import com.teamproject.petis.response.PestResponseItem
import com.teamproject.petis.response.ProductResponse
import com.teamproject.petis.response.ProductResponseItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("articles")
    fun getArticles(): Call<List<ArticleResponse>>

    @GET("pest")
    fun getPest(): Call<List<PestResponseItem>>

    // Tambahan endpoint untuk product
    @GET("products")
    fun getProduct(): Call<List<ProductResponseItem>>

    // Endpoint untuk mendapatkan produk berdasarkan tipe
    @GET("products/type/{type}")
    fun getProductsByType(@Path("type") type: String): Call<ProductResponse>
}