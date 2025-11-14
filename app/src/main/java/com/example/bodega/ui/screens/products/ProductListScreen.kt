package com.example.bodega.ui.screens.products

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bodega.ui.components.ProductCard
import com.example.bodega.viewmodel.ProductViewModel

@Composable
fun ProductListScreen(navController: NavController, viewModel: ProductViewModel) {
    val products by viewModel.allProducts.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("add_edit_product")
            }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Producto")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(products) { product ->
                ProductCard(
                    product = product,
                    modifier = Modifier.padding(8.dp),
                    onClick = { /* Handle regular click if needed */ },
                    onLongClick = {
                        Log.d("ProductListScreen", "Long press on product with id: ${product.productId}")
                        navController.navigate("add_edit_product/${product.productId}")
                    }
                )
            }
        }
    }
}
