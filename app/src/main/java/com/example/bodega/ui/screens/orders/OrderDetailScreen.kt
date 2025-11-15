package com.example.bodega.ui.screens.orders

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bodega.ui.components.AppHeader
import com.example.bodega.viewmodel.OrderViewModel

@Composable
fun OrderDetailScreen(navController: NavController, viewModel: OrderViewModel, orderId: Int) {
    val orderWithProducts by viewModel.getOrderWithProducts(orderId).collectAsState(initial = null)

    Scaffold(
        topBar = {
            AppHeader(title = "bodega", subtitle = "Detalle del Pedido")
        }
    ) { padding ->
        Column(modifier = Modifier.padding(16.dp).padding(padding)) {
            orderWithProducts?.let {
                Text("ID del Pedido: ${it.order.orderId}")
                Text("ID del Cliente: ${it.order.customerId}")
                Text("Fecha del Pedido: ${it.order.orderDate}")
                Text("Productos:")
                it.products.forEach { product ->
                    Text("- ${product.name}")
                }
            }
        }
    }
}