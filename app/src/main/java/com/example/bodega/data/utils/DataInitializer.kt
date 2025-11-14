package com.example.bodega.data.utils

import android.content.Context
import com.example.bodega.data.database.entities.Category
import com.example.bodega.data.database.entities.Customer
import com.example.bodega.data.database.entities.Product
import com.example.bodega.data.repository.BodegaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigDecimal

class DataInitializer(
    private val context: Context,
    private val repository: BodegaRepository
) {

    suspend fun shouldPopulateDatabase(): Boolean {
        return withContext(Dispatchers.IO) {
            // Check if any tables have data - if any table has data, assume DB is already populated
            repository.getCategoryCount() == 0 &&
                       repository.getCustomerCount() == 0 &&
                       repository.getProductCount() == 0
        }
    }

    suspend fun populateDatabase() {
        withContext(Dispatchers.IO) {
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
        }
    }
}
