package com.teamproject.petis.ui.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teamproject.petis.api.ApiClient
import com.teamproject.petis.response.ProductResponseItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductViewModel : ViewModel() {
    private val _products = MutableLiveData<List<ProductResponseItem>>()
    val products: LiveData<List<ProductResponseItem>> = _products

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchProducts() {
        _isLoading.value = true
        ApiClient.apiService.getProduct().enqueue(object : Callback<List<ProductResponseItem>> {
            override fun onResponse(
                call: Call<List<ProductResponseItem>>,
                response: Response<List<ProductResponseItem>>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _products.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Failed to fetch products"
                }
            }

            override fun onFailure(call: Call<List<ProductResponseItem>>, t: Throwable) {
                _isLoading.value = false
                _error.value = t.message ?: "Unknown error"
            }
        })
    }

    fun fetchProductsByType(type: String) {
        _isLoading.value = true
        ApiClient.apiService.getProductsByType(type).enqueue(object : Callback<List<ProductResponseItem>> {
            override fun onResponse(
                call: Call<List<ProductResponseItem>>,
                response: Response<List<ProductResponseItem>>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _products.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Failed to fetch products by type"
                }
            }

            override fun onFailure(call: Call<List<ProductResponseItem>>, t: Throwable) {
                _isLoading.value = false
                _error.value = t.message ?: "Unknown error"
            }
        })
    }
}
