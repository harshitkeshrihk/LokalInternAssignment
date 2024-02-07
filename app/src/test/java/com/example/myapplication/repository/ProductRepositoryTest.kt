package com.example.myapplication.repository

import com.example.myapplication.Product
import com.example.myapplication.ProductX
import com.example.myapplication.getOrAwaitValue
import com.example.myapplication.network.ProductService
import com.example.myapplication.util.Resource
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.Response

class ProductRepositoryTest {

    @Mock
    lateinit var productsAPI: ProductService

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun test_GetProducts_EmptyList()  = runTest{
        val product = Product(10, emptyList(),10,5)
        Mockito.`when`(productsAPI.getProducts()).thenReturn(Response.success(product))

        val sut = ProductRepository(productsAPI)
        val result = sut.getProducts()
        Assert.assertEquals(true, result is Resource.Success)
        Assert.assertEquals(0,result.data!!.size)
    }

    @Test
    fun test_GetProducts_expectedProductList()  = runTest{
        val productList = listOf<ProductX>(
            ProductX("","","",12.0,1, emptyList(),12,4.5,5,"","Apple"),
            ProductX("","","",15.0,2, emptyList(),16,3.5,15,"","Samsung")

        )
        val product = Product(10, productList,10,5)
        Mockito.`when`(productsAPI.getProducts()).thenReturn(Response.success(product))

        val sut = ProductRepository(productsAPI)
        val result = sut.getProducts()
        Assert.assertEquals(true, result is Resource.Success)
        Assert.assertEquals(2,result.data!!.size)
        Assert.assertEquals("Apple",result.data!![0].title)
    }

    @Test
    fun test_GetProducts_expectedError()  = runTest{
        Mockito.`when`(productsAPI.getProducts()).thenReturn(Response.error(401,"Unauthorized".toResponseBody()))

        val sut = ProductRepository(productsAPI)
        val result = sut.getProducts()
        Assert.assertEquals(true, result is Resource.Error)
        Assert.assertEquals("Something Went Wrong",result.message)
    }

}