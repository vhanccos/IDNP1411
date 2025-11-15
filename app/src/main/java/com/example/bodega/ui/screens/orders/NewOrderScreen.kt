package com.example.bodega.ui.screens.orders

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.runtime.collectAsState
import com.example.bodega.data.database.entities.Order
import com.example.bodega.viewmodel.OrderViewModel
import java.util.Date
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewOrderScreen(navController: NavController, viewModel: OrderViewModel) {
    val allCustomers by viewModel.allCustomers.collectAsState()
    val allProducts by viewModel.allProducts.collectAsState()
    val selectedProducts by viewModel.selectedProducts.collectAsState()

    var selectedCustomerId by remember { mutableStateOf<Int?>(null) }
    var selectedProductId by remember { mutableStateOf<Int?>(null) }
    var quantity by remember { mutableStateOf("1") }
    var customerExpanded by remember { mutableStateOf(false) }
    var productExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        // Customer selection dropdown
        ExposedDropdownMenuBox(
            expanded = customerExpanded,
            onExpandedChange = { customerExpanded = !customerExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedCustomerId?.let {
                    allCustomers.find { c -> c.customerId == it }?.firstName + " " + allCustomers.find { c -> c.customerId == it }?.lastName
                } ?: "",
                onValueChange = {},
                label = { Text("Seleccionar Cliente") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = customerExpanded) },
                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
            )

            DropdownMenu(
                expanded = customerExpanded,
                onDismissRequest = { customerExpanded = false }
            ) {
                allCustomers.forEach { customer ->
                    DropdownMenuItem(
                        text = { Text("${customer.firstName} ${customer.lastName} (${customer.email})") },
                        onClick = {
                            selectedCustomerId = customer.customerId
                            customerExpanded = false
                        }
                    )
                }
            }
        }

        // Product selection dropdown
        ExposedDropdownMenuBox(
            expanded = productExpanded,
            onExpandedChange = { productExpanded = !productExpanded },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            OutlinedTextField(
                value = selectedProductId?.let {
                    allProducts.find { p -> p.productId == it }?.name
                } ?: "",
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

        // Quantity input
        OutlinedTextField(
            value = quantity,
            onValueChange = { quantity = it },
            label = { Text("Cantidad") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        // Add to order button
        Button(
            onClick = {
                selectedProductId?.let { productId ->
                    val qty = quantity.toIntOrNull() ?: 1
                    val product = allProducts.find { it.productId == productId }
                    product?.let {
                        viewModel.addProductToOrder(it, qty)
                    }
                }
                // Reset selection
                selectedProductId = null
                quantity = "1"
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            enabled = selectedProductId != null && quantity.toIntOrNull() != null && quantity.toIntOrNull()!! > 0
        ) {
            Text("Agregar al Pedido")
        }

        // Selected products list
        Column(modifier = Modifier.padding(top = 16.dp)) {
            Text(text = "Productos Seleccionados:")
            selectedProducts.forEach { (product, qty) ->
                Text("${product.name} x$qty - S/ ${product.price * qty.toBigDecimal()}")
            }
            val total = selectedProducts.entries.sumOf { (product, qty) ->
                product.price * qty.toBigDecimal()
            }
            Text(text = "Total: S/ $total", style = androidx.compose.material3.MaterialTheme.typography.headlineSmall)
        }

        // Save order button
        Button(
            onClick = {
                if (selectedCustomerId != null && selectedProducts.isNotEmpty()) {
                    viewModel.createOrder(selectedCustomerId!!)
                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            enabled = selectedCustomerId != null && selectedProducts.isNotEmpty()
        ) {
            Text("Crear Pedido")
        }
    }
}