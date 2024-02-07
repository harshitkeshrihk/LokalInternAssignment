package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.Viewmodel.ProductViewModel
import com.example.myapplication.Viewmodel.ProductViewModelFactory
import com.example.myapplication.adapter.ProductAdapter
import com.example.myapplication.adapter.ProductClickListener
import com.example.myapplication.databinding.MainActivityBinding
import com.example.myapplication.network.RetrofitInstance
import com.example.myapplication.repository.ProductRepository
import com.example.myapplication.util.Resource

class MainActivity : AppCompatActivity(),ProductClickListener {

    private lateinit var binding: MainActivityBinding
    private lateinit var adapter: ProductAdapter

    private val productViewModel: ProductViewModel by lazy {
        val factory = ProductViewModelFactory(ProductRepository(RetrofitInstance.productService))
        ViewModelProvider(this, factory).get(ProductViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.myToolbar)

        setUpMenu()
        setUpObservers()

        binding.swipeRefreshLayout.setOnRefreshListener {
            productViewModel.manualRefresh()
            productViewModel.fetchProducts()
        }

        productViewModel.fetchProducts()
    }

    private fun setUpObservers(){
        productViewModel.products.observe(this) { resource ->
            when (resource) {
                is Resource.Success -> {
                    // Update UI with product information
                    if(resource.data != null) {
                        binding.progressBar.visibility = View.GONE
                        binding.productsRecyclerView.visibility = View.VISIBLE
                        binding.productsRecyclerView.layoutManager = GridLayoutManager(this,2)
                        adapter = ProductAdapter(this, resource.data,this)
                        binding.productsRecyclerView.adapter = adapter
                    }
                }

                is Resource.Loading -> {
                    // Show loading state if needed
                    showLoadingState()
                }

                is Resource.Error -> {
                    // Handle error state if needed
                    resource.message?.let { showErrorState(it) }
                }
            }
        }

        productViewModel.lastRefreshTime.observe(this, Observer { lastRefreshTime ->
            // Update UI to display last refresh time
            binding.refreshTime.text = "last refresh time: " + lastRefreshTime.toString()
        })

        productViewModel.isRefreshing.observe(this, Observer { isRefreshing ->
            binding.swipeRefreshLayout.isRefreshing = isRefreshing
        })

    }

    private fun setUpMenu(){
        binding.myToolbar.setNavigationIcon(R.drawable.baseline_arrow_back_ios_new_24)
        binding.myToolbar.setNavigationOnClickListener{v->
            finish()
        }
    }

    private fun showLoadingState() {
        // Show loading indicator or perform any UI action for loading state
        // For example, you can display a progress bar.
        // You can customize this part based on your UI design.
        binding.progressBar.visibility = View.VISIBLE
        binding.productsRecyclerView.visibility = View.GONE
    }

    private fun showErrorState(errorMessage: String) {
        // Handle error state
        // For example, display an error message to the user or show a retry button.
        // You can customize this part based on your UI design.
        binding.progressBar.visibility = View.GONE
        binding.productsRecyclerView.visibility = View.GONE
        Toast.makeText(this, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
    }



    override fun onProductClick(product: ProductX) {
        val intent = Intent(this, ProductActivity::class.java)
        intent.putExtra("productData", product)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        productViewModel.stopAutomaticRefresh()
    }
}