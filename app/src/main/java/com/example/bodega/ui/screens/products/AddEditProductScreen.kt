package com.example.bodega.ui.screens.products

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bodega.data.database.entities.Product
import com.example.bodega.ui.navigation.Screen
import com.example.bodega.viewmodel.ProductViewModel
import java.math.BigDecimal

@Composable
fun AddEditProductScreen(navController: NavController, viewModel: ProductViewModel, productId: Int?) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var categoryId by remember { mutableStateOf("") }

    Log.d("AddEditProductScreen", "Received productId: $productId")

    val product by if (productId != null && productId != -1) {
        viewModel.getProductById(productId).collectAsState(initial = null)
    } else {
        remember { mutableStateOf(null) }
    }

    Log.d("AddEditProductScreen", "Product from collectAsState: $product")

    LaunchedEffect(product) {
        product?.let {
            name = it.name
            price = it.price.toString()
            categoryId = it.categoryId.toString()
        }
    }

    Scaffold(
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // CSV Import FAB
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.CSVImport.route) },
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ) {
                    Icon(Icons.Default.Upload, contentDescription = "Importar CSV")
                }

                // Back button FAB
                FloatingActionButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre del Producto") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Precio") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = categoryId,
                onValueChange = { categoryId = it },
                label = { Text("ID de Categoría") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    if (product == null) {
                        viewModel.addProduct(
                            name = name,
                            price = price,
                            categoryId = categoryId.toInt()
                        )
                    } else {
                        val updatedProduct = product!!.copy(
                            name = name,
                            price = BigDecimal(price),
                            categoryId = categoryId.toInt()
                        )
                        viewModel.updateProduct(updatedProduct)
                    }
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (product == null) "Agregar Producto" else "Actualizar Producto")
            }
            if (product != null) {
                Button(
                    onClick = {
                        viewModel.deleteProduct(product!!)
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Text("Eliminar Producto")
                }
            }
        }
    }
}