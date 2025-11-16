package com.example.bodega.ui.screens.products

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bodega.data.database.entities.Product
import com.example.bodega.viewmodel.ProductViewModel
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    navController: NavController,
    viewModel: ProductViewModel,
    productId: Int?
) {
    // Estados del formulario
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }
    var expandedDropdown by remember { mutableStateOf(false) }

    // Estados de validación
    var nameError by remember { mutableStateOf<String?>(null) }
    var priceError by remember { mutableStateOf<String?>(null) }
    var categoryError by remember { mutableStateOf<String?>(null) }

    // Estados de UI
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    Log.d("AddEditProductScreen", "Received productId: $productId")

    // Obtener producto si es edición
    val product by if (productId != null && productId != -1) {
        viewModel.getProductById(productId).collectAsState(initial = null)
    } else {
        remember { mutableStateOf(null) }
    }

    // Obtener todas las categorías
    val categories by viewModel.allCategories.collectAsState()

    Log.d("AddEditProductScreen", "Product from collectAsState: $product")

    // Cargar datos del producto al editar
    LaunchedEffect(product) {
        product?.let {
            name = it.name
            price = it.price.toString()
            selectedCategoryId = it.categoryId
        }
    }

    val screenTitle = if (product == null) "Agregar Producto" else "Editar Producto"
    val isEditMode = product != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(screenTitle) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Campo: Nombre del Producto
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = null
                },
                label = { Text("Nombre del Producto *") },
                isError = nameError != null,
                supportingText = {
                    nameError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Campo: Precio
            OutlinedTextField(
                value = price,
                onValueChange = {
                    // Permitir solo números y punto decimal
                    if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                        price = it
                        priceError = null
                    }
                },
                label = { Text("Precio *") },
                isError = priceError != null,
                supportingText = {
                    priceError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                prefix = { Text("S/ ") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Dropdown: Categoría
            ExposedDropdownMenuBox(
                expanded = expandedDropdown,
                onExpandedChange = { expandedDropdown = it }
            ) {
                OutlinedTextField(
                    value = categories.find { it.categoryId == selectedCategoryId }?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría *") },
                    isError = categoryError != null,
                    supportingText = {
                        categoryError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expandedDropdown,
                    onDismissRequest = { expandedDropdown = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                selectedCategoryId = category.categoryId
                                categoryError = null
                                expandedDropdown = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón: Guardar
            Button(
                onClick = {
                    // Validar campos
                    var hasErrors = false

                    if (name.isBlank()) {
                        nameError = "El nombre es obligatorio"
                        hasErrors = true
                    }

                    if (price.isBlank()) {
                        priceError = "El precio es obligatorio"
                        hasErrors = true
                    } else {
                        try {
                            val priceValue = BigDecimal(price)
                            if (priceValue <= BigDecimal.ZERO) {
                                priceError = "El precio debe ser mayor a 0"
                                hasErrors = true
                            }
                        } catch (e: NumberFormatException) {
                            priceError = "Precio inválido"
                            hasErrors = true
                        }
                    }

                    if (selectedCategoryId == null) {
                        categoryError = "Selecciona una categoría"
                        hasErrors = true
                    }

                    // Si no hay errores, guardar
                    if (!hasErrors) {
                        isLoading = true
                        if (isEditMode) {
                            val updatedProduct = product!!.copy(
                                name = name.trim(),
                                price = BigDecimal(price),
                                categoryId = selectedCategoryId!!
                            )
                            viewModel.updateProduct(updatedProduct)
                        } else {
                            viewModel.addProduct(
                                name = name.trim(),
                                price = price,
                                categoryId = selectedCategoryId!!
                            )
                        }
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(if (isEditMode) "Actualizar Producto" else "Agregar Producto")
                }
            }

            // Botón: Eliminar (solo en modo edición)
            if (isEditMode) {
                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar Producto")
                }
            }
        }

        // Diálogo de confirmación para eliminar
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Confirmar Eliminación") },
                text = {
                    Text("¿Estás seguro de que deseas eliminar el producto '${product?.name}'? Esta acción no se puede deshacer.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            product?.let { viewModel.deleteProduct(it) }
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
                    OutlinedButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}