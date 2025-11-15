package com.example.bodega.ui.screens.orders

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bodega.ui.components.AppHeader
import com.example.bodega.ui.components.OrderCard
import com.example.bodega.ui.navigation.Screen
import com.example.bodega.viewmodel.OrderViewModel

@Composable
fun OrderListScreen(navController: NavController, viewModel: OrderViewModel) {
    val orders by viewModel.allOrderSummariesWithCustomer.collectAsState()

    Scaffold(
        topBar = {
            AppHeader(title = "bodega", subtitle = "Pedidos")
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(Screen.NewOrder.route)
            }) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo Pedido")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(orders) { orderSummary ->
                OrderCard(
                    order = orderSummary,
                    modifier = Modifier.padding(8.dp),
                    onClick = {
                        navController.navigate(Screen.OrderDetail.createRoute(orderSummary.orderId))
                    },
                    onEdit = {
                        navController.navigate("edit_order/${orderSummary.orderId}")
                    },
                    onDelete = {
                        viewModel.deleteOrderWithDetails(
                            com.example.bodega.data.database.entities.Order(
                                orderId = orderSummary.orderId,
                                customerId = orderSummary.customer.customerId,
                                orderDate = orderSummary.orderDate
                            )
                        )
                    }
                )
            }
        }
    }
}
