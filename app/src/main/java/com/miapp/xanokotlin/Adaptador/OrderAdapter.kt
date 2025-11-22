package com.miapp.xanokotlin.Adaptador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.miapp.xanokotlin.databinding.ItemOrderBinding
import com.miapp.xanokotlin.model.Order

class OrderAdapter(
    private var orders: List<Order>,
    private val onAcceptClick: (Order) -> Unit,
    private val onRejectClick: (Order) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(private val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.tvOrderId.text = "Pedido #${order.id}"
            binding.tvOrderUserName.text = "Cliente: ${order.userName}"
            binding.tvOrderTotal.text = "Total: $${order.total}"
            binding.tvOrderStatus.text = "Estado: ${order.status}"

           
            if (order.status.equals("pendiente", ignoreCase = true)) {
                binding.layoutOrderActions.visibility = View.VISIBLE
                binding.btnAcceptOrder.setOnClickListener { onAcceptClick(order) }
                binding.btnRejectOrder.setOnClickListener { onRejectClick(order) }
            } else {
                binding.layoutOrderActions.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int = orders.size

    fun updateOrders(newOrders: List<Order>) {
        this.orders = newOrders
        notifyDataSetChanged()
    }
}