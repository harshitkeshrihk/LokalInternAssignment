package com.example.myapplication.Viewmodel

import android.os.Handler
import android.os.Looper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.myapplication.ProductX
import com.example.myapplication.getOrAwaitValue
import com.example.myapplication.repository.ProductRepository
import com.example.myapplication.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*

import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

class ProductViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var repository: ProductRepository

    private val mockHandler = mock<Handler>()
    private lateinit var sut: ProductViewModel


    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        sut = ProductViewModel(repository,mockHandler)
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun test_GetProducts_emptyList()  = runTest{
        Mockito.`when`(repository.getProducts()).thenReturn(Resource.Success(emptyList()))

        sut.fetchProducts()
        //to first execute all the coroutines on scheduler so that live data gets updated then only execute this test
        testDispatcher.scheduler.advanceUntilIdle()
        val result = sut.products.getOrAwaitValue()
        Assert.assertEquals(0,result.data!!.size)
    }

    @Test
    fun test_GetProducts_expectedError()  = runTest{
        Mockito.`when`(repository.getProducts()).thenReturn(Resource.Error("Something Went Wrong"))

        sut.fetchProducts()
        //to first execute all the coroutines on scheduler so that live data gets updated then only execute this test
        testDispatcher.scheduler.advanceUntilIdle()
        val result = sut.products.getOrAwaitValue()
        Assert.assertEquals(true,result is Resource.Error)
        Assert.assertEquals("Something Went Wrong",result.message)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}