package com.miapp.xanokotlin.Adaptador

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.miapp.xanokotlin.Activity.ProductDetailActivity
import com.miapp.xanokotlin.R
import com.miapp.xanokotlin.databinding.ItemProductBinding
import com.miapp.xanokotlin.model.Product

class ProductAdapter(private var products: List<Product>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var filteredProducts: List<Product> = products

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(filteredProducts[position])
    }

    override fun getItemCount() = filteredProducts.size

    fun filter(query: String) {
        filteredProducts = if (query.isEmpty()) {
            products
        } else {
            products.filter { it.name.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }

    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        filteredProducts = newProducts
        notifyDataSetChanged()
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.tvProductName.text = product.name
            binding.tvProductPrice.text = "$${product.price}"

            val imageUrl = product.imageUrls?.firstOrNull()?.url
            Glide.with(itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(binding.imgProduct)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ProductDetailActivity::class.java)
                intent.putExtra("PRODUCT_ID", product.id)
                itemView.context.startActivity(intent)
            }
        }
    }
}