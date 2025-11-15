package com.example.bodega.ui.screens.customers

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bodega.data.database.entities.Customer
import com.example.bodega.ui.components.AppHeader
import com.example.bodega.viewmodel.CustomerViewModel

@Composable
fun AddEditCustomerScreen(navController: NavController, viewModel: CustomerViewModel, customerId: Int?) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Log.d("AddEditCustomerScreen", "Received customerId: $customerId")

    val customer by if (customerId != null && customerId != -1) {
        viewModel.getCustomerById(customerId).collectAsState(initial = null)
    } else {
        remember { mutableStateOf(null) }
    }

    Log.d("AddEditCustomerScreen", "Customer from collectAsState: $customer")

    LaunchedEffect(customer) {
        customer?.let {
            firstName = it.firstName
            lastName = it.lastName
            email = it.email
            address = it.address
            phone = it.phone
        }
    }

    val screenTitle = if (customer == null) "Agregar Cliente" else "Actualizar Cliente"

    Scaffold(
        topBar = {
            AppHeader(title = "bodega", subtitle = screenTitle)
        }
    ) { padding ->
        Column(modifier = Modifier.padding(16.dp).padding(padding)) {
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Dirección") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    if (customer == null) {
                        viewModel.addCustomer(
                            firstName = firstName,
                            lastName = lastName,
                            email = email,
                            address = address,
                            phone = phone
                        )
                    } else {
                        val updatedCustomer = customer!!.copy(
                            firstName = firstName,
                            lastName = lastName,
                            email = email,
                            address = address,
                            phone = phone
                        )
                        viewModel.updateCustomer(updatedCustomer)
                    }
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (customer == null) "Agregar Cliente" else "Actualizar Cliente")
            }
            if (customer != null) {
                Button(
                    onClick = {
                        viewModel.deleteCustomer(customer!!)
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Text("Eliminar Cliente")
                }
            }
        }
    }
}