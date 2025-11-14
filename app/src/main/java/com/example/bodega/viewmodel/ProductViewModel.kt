package com.example.bodega.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bodega.data.database.entities.Category
import com.example.bodega.data.database.entities.Product
import com.example.bodega.data.repository.BodegaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal

class ProductViewModel(private val repository: BodegaRepository) : ViewModel() {

    val allProducts: StateFlow<List<Product>> = repository.getAllProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allCategories: StateFlow<List<Category>> = repository.getAllCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addProduct(name: String, price: String, categoryId: Int) {
        viewModelScope.launch {
            try {
                val product = Product(
                    name = name,
                    price = BigDecimal(price),
                    categoryId = categoryId
                )
                repository.insertProduct(product)
            } catch (e: NumberFormatException) {
                // Handle invalid price format
            }
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    fun getProductById(productId: Int): Flow<Product> {
        return repository.getProductById(productId)
    }

    fun importProductsFromCSV(csvData: List<List<String>>) {
        viewModelScope.launch {
            // Skip header row if it exists
            val dataRows = if (csvData.isNotEmpty()) csvData.drop(1) else emptyList()

            dataRows.forEach { row ->
                if (row.size >= 3) { // Minimum required fields: name, price, categoryId
                    val name = row[0].trim()
                    val priceStr = row[1].trim()
                    val categoryIdStr = row[2].trim()

                    try {
                        val price = BigDecimal(priceStr)
                        val categoryId = categoryIdStr.toIntOrNull() ?: 1 // Default to category 1 if invalid

                        val product = Product(
                            name = name,
                            price = price,
                            categoryId = categoryId
                        )
                        repository.insertProduct(product)
                    } catch (e: Exception) {
                        // Log error and continue with next row
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}
