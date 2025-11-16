package com.example.bodega.ui.screens.customers

import android.util.Log
import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bodega.ui.components.AppHeader
import com.example.bodega.viewmodel.CustomerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCustomerScreen(
    navController: NavController,
    viewModel: CustomerViewModel,
    customerId: Int?
) {
    // Estados del formulario
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    // Estados de validación
    var firstNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }

    // Estados de UI
    var showDeleteDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    Log.d("AddEditCustomerScreen", "Received customerId: $customerId")

    // Cargar datos del cliente si existe
    val customer by if (customerId != null && customerId != -1) {
        viewModel.getCustomerById(customerId).collectAsState(initial = null)
    } else {
        remember { mutableStateOf(null) }
    }

    Log.d("AddEditCustomerScreen", "Customer from collectAsState: $customer")

    // Inicializar campos cuando se carga el cliente
    LaunchedEffect(customer) {
        customer?.let {
            firstName = it.firstName
            lastName = it.lastName
            email = it.email
            address = it.address
            phone = it.phone
        }
    }

    val isEditMode = customer != null
    val screenTitle = if (isEditMode) "Editar Cliente" else "Nuevo Cliente"

    // Función de validación
    fun validateFields(): Boolean {
        var isValid = true

        // Validar nombre (obligatorio)
        if (firstName.isBlank()) {
            firstNameError = "El nombre es obligatorio"
            isValid = false
        } else {
            firstNameError = null
        }

        // Validar email (formato si no está vacío)
        if (email.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Formato de email inválido"
            isValid = false
        } else {
            emailError = null
        }

        return isValid
    }

    // Función para guardar
    fun saveCustomer() {
        if (validateFields()) {
            if (isEditMode) {
                val updatedCustomer = customer!!.copy(
                    firstName = firstName.trim(),
                    lastName = lastName.trim(),
                    email = email.trim(),
                    address = address.trim(),
                    phone = phone.trim()
                )
                viewModel.updateCustomer(updatedCustomer)
            } else {
                viewModel.addCustomer(
                    firstName = firstName.trim(),
                    lastName = lastName.trim(),
                    email = email.trim(),
                    address = address.trim(),
                    phone = phone.trim()
                )
            }
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            AppHeader(title = "bodega", subtitle = screenTitle)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Nombre (obligatorio)
            OutlinedTextField(
                value = firstName,
                onValueChange = {
                    firstName = it
                    firstNameError = null
                },
                label = { Text("Nombre *") },
                isError = firstNameError != null,
                supportingText = {
                    firstNameError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Apellido
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Apellido") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = null
                },
                label = { Text("Correo Electrónico") },
                isError = emailError != null,
                supportingText = {
                    emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Teléfono
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Teléfono") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Dirección
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Dirección") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        saveCustomer()
                    }
                ),
                minLines = 2,
                maxLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón Cancelar
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }

                // Botón Guardar
                Button(
                    onClick = { saveCustomer() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isEditMode) "Actualizar" else "Agregar")
                }
            }

            // Botón Eliminar (solo en modo edición)
            if (isEditMode) {
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Eliminar Cliente")
                }
            }

            // Texto de campos obligatorios
            Text(
                text = "* Campos obligatorios",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    // Diálogo de confirmación de eliminación
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Confirmar eliminación") },
            text = {
                Text(
                    "¿Estás seguro de que deseas eliminar a " +
                            "${customer?.firstName} ${customer?.lastName}? " +
                            "Esta acción no se puede deshacer."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCustomer(customer!!)
                        showDeleteDialog = false
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}