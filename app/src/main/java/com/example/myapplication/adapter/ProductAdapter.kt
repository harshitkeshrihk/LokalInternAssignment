package com.example.myapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.ProductX
import com.example.myapplication.R
import com.example.myapplication.databinding.ProductListItemBinding

//interface for clickListener
interface ProductClickListener {
    fun onProductClick(product: ProductX)
}

class ProductAdapter(private val context: Context, private val productList: List<ProductX>,private val clickListener: ProductClickListener) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    class ViewHolder(val binding: ProductListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ProductListItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productList[position]

        // Bind data to the ViewHolder using View Binding
        holder.binding.apply {
            productNameTv.text = product.title.toString()
            productPriceTv.text = "$" + product.price.toString()
            productOfferValueTv.text = product.discountPercentage.toString() + "%"
            productRatingBar.rating = (product.rating ?: 0.0).toFloat()
            productActualPriceTv.text = "$" + ((product.price*100*1.0)/(100-product.discountPercentage)).toInt().toString()
        }
        // Load thumbnail image using Glide or your preferred image loading library
        Glide.with(context).load(product.thumbnail).into(holder.binding.productImageView)

        //click listener
        holder.itemView.setOnClickListener{
            clickListener.onProductClick(product)
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}