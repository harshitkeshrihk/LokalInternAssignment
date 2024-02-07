package com.example.myapplication

import com.example.myapplication.network.ProductService
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection

class ProductsAPITest {


    @Mock
    lateinit var mockWebServer: MockWebServer

    @Mock
    lateinit var productsAPI: ProductService

    @Before
    fun setup(){
        mockWebServer = MockWebServer()
        productsAPI = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ProductService::class.java)
    }

    @Test
    fun testGetProducts() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody("{" +
                    "\"limit\": 10," +
                    "\"products\": [" +
                    "   {" +
                    "       \"id\": 1," +
                    "       \"title\": \"Sample Product\"," +
                    "       \"price\": 19" +
                    "   }" +
                    "]," +
                    "\"skip\": 0," +
                    "\"total\": 1" +
                    "}")
        mockWebServer.enqueue(mockResponse)

        // Call the function to be tested
        val response = productsAPI.getProducts()

        // Verify the request
        val request = mockWebServer.takeRequest()
        assert(request.path == "/products")

        // Verify the response
        assert(response.isSuccessful)
        val product = response.body()
        assert(product != null)
        assert(product?.limit == 10)
        assert(product?.skip == 0)
        assert(product?.total == 1)

        val productList = product?.products
        assert(productList != null && productList.isNotEmpty())
        val prod = productList?.get(0)
        assert(prod != null)
        assert(prod?.id == 1)
        assert(prod?.title == "Sample Product")
        assert(prod?.price == 19)

    }



    @After
    fun tearDown(){
       mockWebServer.shutdown()
    }
}