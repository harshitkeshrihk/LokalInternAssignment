package com.example.myapplication.Viewmodel

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.ProductX
import com.example.myapplication.repository.ProductRepository
import com.example.myapplication.util.Resource
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class ProductViewModel(private val repository: ProductRepository,
    private val handler: Handler = Handler(Looper.getMainLooper())
) : ViewModel() {
    private val _products = MutableLiveData<Resource<List<ProductX>>>()
    val products: LiveData<Resource<List<ProductX>>> get() = _products

    private val _lastRefreshTime = MutableLiveData<String>()
    val lastRefreshTime: LiveData<String> get() = _lastRefreshTime

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> get() = _isRefreshing

    private val refreshHandler = handler
    private val refreshRunnable = object : Runnable {
        override fun run() {
            fetchProducts()
            refreshHandler.postDelayed(this, TimeUnit.MINUTES.toMillis(3))
        }
    }

    init {
        startAutomaticRefresh()
    }

    fun fetchProducts() {
        viewModelScope.launch {
            _products.value = Resource.Loading()
            _isRefreshing.value = true
            try {
                val result  = repository.getProducts()
                //to handle refresh time
                _lastRefreshTime.value = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(
                    Date()
                )
                _isRefreshing.value = false
                //updating value of products
                _products.postValue(result)
            } catch (e: Exception) {
                _products.value = Resource.Error("Error fecthing products",null)
                Log.e("ProductViewModel", "Error fetching products", e)
            }
        }
    }

    fun manualRefresh() {
        // Remove existing callbacks and start refreshing from the beginning
        refreshHandler.removeCallbacks(refreshRunnable)
        startAutomaticRefresh()
    }

    private fun startAutomaticRefresh() {
        refreshHandler.postDelayed(refreshRunnable, TimeUnit.MINUTES.toMillis(3))
    }

    fun stopAutomaticRefresh() {
        refreshHandler.removeCallbacks(refreshRunnable)
    }
}