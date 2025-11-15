package com.example.bodega.ui.screens.customers

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bodega.ui.components.AppHeader
import com.example.bodega.ui.components.OrderCard
import com.example.bodega.ui.navigation.Screen
import com.example.bodega.viewmodel.CustomerViewModel
import com.example.bodega.data.database.relations.OrderSummaryWithCustomer
import com.example.bodega.viewmodel.OrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerOrdersScreen(navController: NavController, customerViewModel: CustomerViewModel, orderViewModel: OrderViewModel, customerId: Int) {
    val customer by customerViewModel.getCustomerById(customerId).collectAsState(initial = null)
    val orderSummaries by customerViewModel.getOrderSummariesForCustomer(customerId).collectAsState(initial = emptyList())

    val customerName = customer?.let { "${it.firstName} ${it.lastName}" } ?: "Cliente"

    Scaffold(
        topBar = {
            AppHeader(title = "bodega", subtitle = "Pedidos de $customerName")
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            customer?.let { cust ->
                LazyColumn {
                    items(orderSummaries) { orderSummary ->
                        val orderSummaryWithCustomer = OrderSummaryWithCustomer(
                            orderId = orderSummary.orderId,
                            orderDate = orderSummary.orderDate,
                            total = orderSummary.total,
                            customer = cust
                        )
                        OrderCard(
                            order = orderSummaryWithCustomer,
                            modifier = Modifier.padding(8.dp),
                            onClick = {
                                navController.navigate(Screen.OrderDetail.createRoute(orderSummary.orderId))
                            },
                            onEdit = {
                                navController.navigate(Screen.EditOrder.createRoute(orderSummary.orderId))
                            },
                            onDelete = {
                                orderViewModel.deleteOrderWithDetails(
                                    com.example.bodega.data.database.entities.Order(
                                        orderId = orderSummary.orderId,
                                        customerId = cust.customerId,
                                        orderDate = orderSummary.orderDate
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}
