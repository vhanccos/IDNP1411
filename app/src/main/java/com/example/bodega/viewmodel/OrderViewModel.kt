package com.example.bodega.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bodega.data.database.entities.Customer
import com.example.bodega.data.database.entities.Order
import com.example.bodega.data.database.entities.Product
import com.example.bodega.data.repository.BodegaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import com.example.bodega.data.database.relations.OrderWithProducts
import com.example.bodega.data.database.relations.OrderWithDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Date

class OrderViewModel(private val repository: BodegaRepository) : ViewModel() {

    val allOrders: StateFlow<List<Order>> = repository.getAllOrders()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allOrdersWithDetails = repository.getAllOrdersWithDetails()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allOrdersWithCustomer = repository.getAllOrdersWithCustomer()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allCustomers: StateFlow<List<Customer>> = repository.getAllCustomers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    val allProducts: StateFlow<List<Product>> = repository.getAllProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedProducts = MutableStateFlow<Map<Product, Int>>(emptyMap())
    val selectedProducts: StateFlow<Map<Product, Int>> = _selectedProducts.asStateFlow()

    fun addProductToOrder(product: Product, quantity: Int) {
        val currentProducts = _selectedProducts.value.toMutableMap()
        if (quantity > 0) {
            currentProducts[product] = quantity
        } else {
            currentProducts.remove(product)
        }
        _selectedProducts.value = currentProducts
    }

    fun createOrder(customerId: Int) {
        viewModelScope.launch {
            if (_selectedProducts.value.isNotEmpty()) {
                val order = Order(
                    customerId = customerId,
                    orderDate = Date()
                )
                repository.insertOrderWithDetails(order, _selectedProducts.value)
                _selectedProducts.value = emptyMap() // Clear selection
            }
        }
    }

    fun updateOrder(order: Order) {
        viewModelScope.launch {
            repository.updateOrder(order)
        }
    }

    fun updateOrderWithDetails(order: Order, products: Map<Product, Int>) {
        viewModelScope.launch {
            repository.updateOrderWithDetails(order, products)
        }
    }

    fun deleteOrder(order: Order) {
        viewModelScope.launch {
            repository.deleteOrder(order)
        }
    }

    fun deleteOrderWithDetails(order: Order) {
        viewModelScope.launch {
            repository.deleteOrderWithDetails(order)
        }
    }

    fun getOrderWithProducts(orderId: Int): Flow<OrderWithProducts> {
        return repository.getOrderWithProducts(orderId)
    }

    fun getOrderWithDetails(orderId: Int): Flow<OrderWithDetails> {
        return repository.getOrderWithDetails(orderId)
    }
}
