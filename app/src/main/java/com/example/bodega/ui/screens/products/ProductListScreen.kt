package com.example.bodega.ui.screens.products

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bodega.ui.components.AppHeader
import com.example.bodega.ui.components.ProductCard
import com.example.bodega.viewmodel.ProductViewModel

@Composable
fun ProductListScreen(navController: NavController, viewModel: ProductViewModel) {
    val products by viewModel.allProducts.collectAsState()

    Scaffold(
        topBar = {
            AppHeader(title = "bodega", subtitle = "Productos")
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = androidx.compose.ui.Alignment.End
            ) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate("csv_import")
                    },
                    content = {
                        Icon(Icons.Default.Upload, contentDescription = "Importar CSV")
                    }
                )
                FloatingActionButton(onClick = {
                    navController.navigate("add_edit_product")
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar Producto")
                }
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(products) { product ->
                ProductCard(
                    product = product,
                    modifier = Modifier.padding(8.dp),
                    onClick = { /* Handle regular click if needed */ },
                    onEdit = {
                        navController.navigate("add_edit_product/${product.productId}")
                    },
                    onDelete = {
                        viewModel.deleteProduct(product)
                    }
                )
            }
        }
    }
}
