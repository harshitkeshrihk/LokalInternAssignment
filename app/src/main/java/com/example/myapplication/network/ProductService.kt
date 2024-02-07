package com.example.myapplication.network

import com.example.myapplication.Product
import retrofit2.Response
import retrofit2.http.GET

interface ProductService {
    @GET("products")
    suspend fun getProducts(): Response<Product>
}