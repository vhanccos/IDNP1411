package com.example.bodega.ui.screens.orders

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import com.example.bodega.ui.components.AppHeader
import com.example.bodega.viewmodel.OrderViewModel
import com.example.bodega.data.database.entities.Product
import com.example.bodega.data.database.relations.OrderWithDetails

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditOrderScreen(navController: NavController, viewModel: OrderViewModel, orderId: Int) {
    val orderWithDetails by viewModel.getOrderWithDetails(orderId).collectAsState(initial = null)
    val allProducts by viewModel.allProducts.collectAsState()

    var selectedProducts by remember { mutableStateOf<Map<Product, Int>>(emptyMap()) }
    var productExpanded by remember { mutableStateOf(false) }
    var selectedProductId by remember { mutableStateOf<Int?>(null) }
    var quantity by remember { mutableStateOf("1") }

    LaunchedEffect(orderWithDetails, allProducts) {
        orderWithDetails?.let { orderData ->
            val productMap = allProducts.associateBy { it.productId }
            val initialProducts = orderData.orderDetails.mapNotNull { detail ->
                productMap[detail.productId]?.let { product ->
                    product to detail.quantity
                }
            }.toMap(mutableMapOf()) // Explicitly specify mutableMapOf() to resolve ambiguity
            selectedProducts = initialProducts
        }
    }

    Scaffold(
        topBar = {
            AppHeader(title = "bodega", subtitle = "Editar Pedido")
        }
    ) { padding ->
        Column(modifier = Modifier.padding(16.dp).padding(padding)) {
            orderWithDetails?.let { orderData ->
                Text("Editando Pedido #${orderData.order.orderId}", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))

                // Product selection
                ExposedDropdownMenuBox(
                    expanded = productExpanded,
                    onExpandedChange = { productExpanded = !productExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedProductId?.let { allProducts.find { p -> p.productId == it }?.name } ?: "",
                        onValueChange = {},
                        label = { Text("Seleccionar Producto") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = productExpanded) },
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                    )
                    DropdownMenu(
                        expanded = productExpanded,
                        onDismissRequest = { productExpanded = false }
                    ) {
                        allProducts.forEach { product ->
                            DropdownMenuItem(
                                text = { Text("${product.name} - S/ ${product.price}") },
                                onClick = {
                                    selectedProductId = product.productId
                                    productExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Cantidad") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )

                Button(
                    onClick = {
                        selectedProductId?.let { productId ->
                            val qty = quantity.toIntOrNull() ?: 1
                            allProducts.find { it.productId == productId }?.let { product ->
                                selectedProducts = selectedProducts + (product to qty)
                            }
                        }
                    },
                    enabled = selectedProductId != null && quantity.toIntOrNull() != null
                ) {
                    Text("Añadir/Actualizar Producto")
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Productos en el pedido:", style = MaterialTheme.typography.titleMedium)
                selectedProducts.forEach { (product, qty) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${product.name} x$qty")
                        Button(onClick = {
                            selectedProducts = selectedProducts - product
                        }) {
                            Text("Quitar")
                        }
                    }
                }

                val total = selectedProducts.entries.sumOf { (product, qty) ->
                    product.price * qty.toBigDecimal()
                }
                Text(text = "Total: S/ $total", style = MaterialTheme.typography.headlineSmall)

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        viewModel.updateOrderWithDetails(orderData.order, selectedProducts)
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar Cambios")
                }
            } ?: Text("Cargando pedido...")
        }
    }
}