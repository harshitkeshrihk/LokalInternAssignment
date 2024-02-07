package com.example.myapplication.repository

import com.example.myapplication.Product
import com.example.myapplication.ProductX
import com.example.myapplication.network.ProductService
import com.example.myapplication.network.RetrofitInstance
import com.example.myapplication.util.Resource

class ProductRepository(private val productsAPI: ProductService) {
    suspend fun getProducts(): Resource<List<ProductX>> {
        val response = productsAPI.getProducts()
        if(response.isSuccessful){
            val responseBody = response.body()
            if(responseBody!=null){
                return Resource.Success(responseBody.products)
            }else{
                return Resource.Error("Something Went Wrong")
            }
        }else{
            return Resource.Error("Something Went Wrong")
        }
    }
}