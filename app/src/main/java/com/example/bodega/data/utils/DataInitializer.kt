package com.example.bodega.data.utils

import android.content.Context
import com.example.bodega.data.database.entities.Category
import com.example.bodega.data.database.entities.Customer
import com.example.bodega.data.database.entities.Order
import com.example.bodega.data.database.entities.OrderDetail
import com.example.bodega.data.database.entities.Product
import com.example.bodega.data.repository.BodegaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DataInitializer(
    private val context: Context,
    private val repository: BodegaRepository
) {

    suspend fun shouldPopulateDatabase(): Boolean {
        return withContext(Dispatchers.IO) {
            // Check if any tables have data - if any table has data, assume DB is already populated
            repository.getCategoryCount() == 0 &&
                       repository.getCustomerCount() == 0 &&
                       repository.getProductCount() == 0 &&
                       repository.getOrderCount() == 0 &&
                       repository.getOrderDetailCount() == 0
        }
    }

    suspend fun populateDatabase() {
        withContext(Dispatchers.IO) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            // Populate Categories
            val categories = context.assets.open("categories.csv").bufferedReader().readLines()
            categories.forEach { name ->
                repository.insertCategory(Category(name = name))
            }

            // Populate Customers
            val customers = context.assets.open("customers.csv").bufferedReader().readLines()
            customers.forEach { line ->
                val parts = line.split(",")
                repository.insertCustomer(
                    Customer(
                        firstName = parts[0],
                        lastName = parts[1],
                        email = parts[2],
                        address = parts[3],
                        phone = parts[4]
                    )
                )
            }

            // Populate Products
            val products = context.assets.open("products.csv").bufferedReader().readLines()
            products.forEach { line ->
                val parts = line.split(",")
                repository.insertProduct(
                    Product(
                        name = parts[0],
                        price = BigDecimal(parts[1]),
                        categoryId = parts[2].toInt()
                    )
                )
            }

            // Populate Orders
            val ordersCsv = context.assets.open("orders.csv").bufferedReader().readLines()
            val insertedOrderIds = mutableListOf<Long>()
            ordersCsv.forEach { line ->
                val parts = line.split(",")
                val customerId = parts[0].toInt()
                val orderDate = dateFormat.parse(parts[1]) ?: Date()
                val order = Order(customerId = customerId, orderDate = orderDate)
                val newOrderId = repository.insertOrder(order)
                insertedOrderIds.add(newOrderId)
            }

            // Populate OrderDetails
            val orderDetailsCsv = context.assets.open("order_details.csv").bufferedReader().readLines()
            orderDetailsCsv.forEach { line ->
                val parts = line.split(",")
                val csvOrderId = parts[0].toInt() // This is the 1-based index from the CSV
                val actualOrderId = insertedOrderIds[csvOrderId - 1].toInt() // Map to actual auto-generated ID
                val productId = parts[1].toInt()
                val quantity = parts[2].toInt()
                repository.insertOrderDetail(OrderDetail(orderId = actualOrderId, productId = productId, quantity = quantity))
            }
        }
    }
}
