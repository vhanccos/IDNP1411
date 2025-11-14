package com.example.bodega.ui.screens.customers

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bodega.ui.components.CustomerCard
import com.example.bodega.ui.navigation.Screen
import com.example.bodega.viewmodel.CustomerViewModel

@Composable
fun CustomerListScreen(navController: NavController, viewModel: CustomerViewModel) {
    val customers by viewModel.allCustomers.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("add_edit_customer")
            }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Cliente")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(customers) { customer ->
                CustomerCard(
                    customer = customer,
                    modifier = Modifier.padding(8.dp),
                    onClick = {
                        navController.navigate(Screen.CustomerOrders.createRoute(customer.customerId))
                    },
                    onLongClick = {
                        Log.d("CustomerListScreen", "Long press on customer with id: ${customer.customerId}")
                        navController.navigate("add_edit_customer/${customer.customerId}")
                    }
                )
            }
        }
    }
}
