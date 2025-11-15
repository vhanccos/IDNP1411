package com.example.bodega.data.repository

import com.example.bodega.data.database.entities.Category
import com.example.bodega.data.database.entities.Customer
import com.example.bodega.data.database.entities.Order
import com.example.bodega.data.database.entities.OrderDetail
import com.example.bodega.data.database.entities.Product
import com.example.bodega.data.database.relations.CategoryWithProducts
import com.example.bodega.data.database.relations.CustomerWithOrders
import com.example.bodega.data.database.relations.OrderWithDetails
import com.example.bodega.data.database.relations.OrderWithProducts
import com.example.bodega.data.database.relations.OrderSummaryWithCustomer
import com.example.bodega.data.database.relations.OrderSummary
import kotlinx.coroutines.flow.Flow

interface BodegaRepository {
    // Category
    fun getAllCategories(): Flow<List<Category>>
    fun getCategoryWithProducts(categoryId: Int): Flow<CategoryWithProducts>
    suspend fun insertCategory(category: Category)
    suspend fun updateCategory(category: Category)
    suspend fun deleteCategory(category: Category)

    // Customer
    fun getAllCustomers(): Flow<List<Customer>>
    fun getCustomerWithOrders(customerId: Int): Flow<CustomerWithOrders>
    suspend fun insertCustomer(customer: Customer)
    suspend fun updateCustomer(customer: Customer)
    suspend fun deleteCustomer(customer: Customer)
    fun getCustomerById(customerId: Int): Flow<Customer>

    // Product
    fun getAllProducts(): Flow<List<Product>>
    fun getProductById(productId: Int): Flow<Product>
    suspend fun insertProduct(product: Product)
    suspend fun updateProduct(product: Product)
    suspend fun deleteProduct(product: Product)
    suspend fun getCategoryCount(): Int
    suspend fun getCustomerCount(): Int
    suspend fun getProductCount(): Int
    suspend fun getOrderCount(): Int
    suspend fun getOrderDetailCount(): Int

    // Order
    fun getAllOrders(): Flow<List<Order>>
    fun getAllOrdersWithDetails(): Flow<List<OrderWithDetails>>
    fun getAllOrderSummariesWithCustomer(): Flow<List<OrderSummaryWithCustomer>>
    fun getOrderSummariesForCustomer(customerId: Int): Flow<List<OrderSummary>>
    fun getOrderWithProducts(orderId: Int): Flow<OrderWithProducts>
    fun getOrderWithDetails(orderId: Int): Flow<OrderWithDetails>
    suspend fun insertOrder(order: Order): Long
    suspend fun insertOrderDetail(orderDetail: OrderDetail)
    suspend fun insertOrderWithDetails(order: Order, products: Map<Product, Int>)
    suspend fun updateOrderWithDetails(order: Order, products: Map<Product, Int>)
    suspend fun updateOrder(order: Order)
    suspend fun deleteOrder(order: Order)
    suspend fun deleteOrderWithDetails(order: Order)
}
