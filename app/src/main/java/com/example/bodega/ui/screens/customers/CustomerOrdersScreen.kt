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
import com.example.bodega.ui.components.OrderCard
import com.example.bodega.ui.navigation.Screen
import com.example.bodega.viewmodel.CustomerViewModel
import com.example.bodega.data.database.relations.OrderWithCustomer
import com.example.bodega.viewmodel.OrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerOrdersScreen(navController: NavController, customerViewModel: CustomerViewModel, orderViewModel: OrderViewModel, customerId: Int) {
    val customerWithOrders by customerViewModel.getCustomerWithOrders(customerId).collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(customerWithOrders?.customer?.let { "${it.firstName} ${it.lastName}" } ?: "Customer Orders") })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            customerWithOrders?.let { customerDetails ->
                LazyColumn {
                    items(customerDetails.orders) { order ->
                        val orderWithCustomer = OrderWithCustomer(order = order, customer = customerDetails.customer)
                        OrderCard(
                            order = orderWithCustomer,
                            modifier = Modifier.padding(8.dp),
                            onClick = {
                                navController.navigate(Screen.OrderDetail.createRoute(order.orderId))
                            },
                            onEdit = {
                                navController.navigate(Screen.EditOrder.createRoute(order.orderId))
                            },
                            onDelete = {
                                orderViewModel.deleteOrderWithDetails(order)
                            }
                        )
                    }
                }
            }
        }
    }
}
