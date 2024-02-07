package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.PagerSnapHelper
import com.bumptech.glide.Glide
import com.example.myapplication.adapter.ProductImagesAdapter
import com.example.myapplication.databinding.ActivityProductBinding
import com.example.myapplication.util.DotsIndicatorDecoration

class ProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.myToolbar)

        setUpMenu()

        val product = intent.getSerializableExtra("productData") as? ProductX
        if(product!=null){
            updateUi(product)
        }
        binding.proDetailsAddCartBtn.setOnClickListener {
            Toast.makeText(this,"Work in Progress",Toast.LENGTH_SHORT).show()
        }
        binding.proDetailsLikeBtn.setOnClickListener {
            Toast.makeText(this,"Work in Progress",Toast.LENGTH_SHORT).show()
        }
    }

    private fun setUpMenu() {
        binding.myToolbar.setNavigationIcon(R.drawable.baseline_arrow_back_ios_new_24)
        binding.myToolbar.setNavigationOnClickListener{v->
           finish()
        }
    }

    private fun updateUi(product : ProductX) {
        binding.proDetailsTitleTv.text = product.title
        binding.proDetailsPriceTv.text = "$" + product.price.toString()
        binding.proDetailsSpecificsText.text = product.description
        binding.proBrandText.text = product.brand
        binding.proCategoryText.text = product.category
        binding.proStockText.text = product.stock.toString()
        binding.proDetailsRatingBar.rating = (product.rating ?: 0.0).toFloat()

        setImagesView(product.images)
    }

    private fun setImagesView(images: List<String>) {
            binding.proDetailsImagesRecyclerview.isNestedScrollingEnabled = false
            val adapter = ProductImagesAdapter(
                this,
                images ?: emptyList()
            )
            binding.proDetailsImagesRecyclerview.adapter = adapter
            val rad = resources.getDimension(R.dimen.radius)
            val dotsHeight = resources.getDimensionPixelSize(R.dimen.dots_height)
            val inactiveColor = ContextCompat.getColor(this, R.color.gray)
            val activeColor = ContextCompat.getColor(this, R.color.blue_accent_300)
            val itemDecoration =
                DotsIndicatorDecoration(rad, rad * 4, dotsHeight, inactiveColor, activeColor)
            binding.proDetailsImagesRecyclerview.addItemDecoration(itemDecoration)
            PagerSnapHelper().attachToRecyclerView(binding.proDetailsImagesRecyclerview)
    }
}