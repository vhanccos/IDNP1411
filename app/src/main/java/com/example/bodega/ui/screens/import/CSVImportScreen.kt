package com.example.bodega.ui.screens.import

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bodega.ui.components.AppHeader
import com.example.bodega.viewmodel.ProductViewModel
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CSVImportScreen(navController: NavController, productViewModel: ProductViewModel) {
    val context = LocalContext.current
    var fileUri by remember { mutableStateOf<Uri?>(null) }
    var csvData by remember { mutableStateOf<List<List<String>>>(emptyList()) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Function to parse a CSV file
    fun parseCSVFile(uri: Uri, context: android.content.Context, viewModel: ProductViewModel) {
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    val data = mutableListOf<List<String>>()

                    reader.forEachLine { line ->
                        // Handle potential empty lines or lines with only whitespace
                        if (line.isNotBlank()) {
                            val row = line.split(",").map { it.trim().replace("\"", "") }
                            data.add(row)
                        }
                    }

                    csvData = data
                }
            }
        } catch (e: SecurityException) {
            // Handle permission-related errors
            android.util.Log.e("CSVImport", "Security exception accessing file", e)
            // Show user-friendly error message
        } catch (e: Exception) {
            android.util.Log.e("CSVImport", "Error parsing CSV file", e)
            // Show user-friendly error message
        }
    }

    // File picker launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            fileUri = it
            // Parse CSV file
            parseCSVFile(it, context, productViewModel)
        }
    }

    Scaffold(
        topBar = {
            AppHeader(title = "bodega", subtitle = "Importar CSV")
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Select CSV file button
            Button(
                onClick = { launcher.launch("text/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Seleccionar Archivo CSV")
            }

            // Display file name if selected
            fileUri?.let { uri ->
                val fileName = getFileName(uri, context)
                Text(
                    text = "Archivo seleccionado: $fileName",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Preview of CSV data
            if (csvData.isNotEmpty()) {
                Text(
                    text = "Vista previa de los datos:",
                    style = MaterialTheme.typography.titleMedium
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(csvData) { row ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            row.forEach { cell ->
                                Text(
                                    text = cell,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 4.dp),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                // Import button
                Button(
                    onClick = { showConfirmationDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = csvData.size > 1 // At least header + 1 row of data
                ) {
                    Text("Importar Datos")
                }
            }

            // Confirmation Dialog
            if (showConfirmationDialog) {
                ImportConfirmationDialog(
                    onConfirm = {
                        showConfirmationDialog = false
                        isLoading = true
                        // Import the data using the ViewModel method
                        productViewModel.importProductsFromCSV(csvData)
                        isLoading = false
                        navController.popBackStack()
                    },
                    onDismiss = { showConfirmationDialog = false }
                )
            }

            // Loading indicator
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun ImportConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar Importación") },
        text = { Text("¿Estás seguro de importar estos datos? Esta acción no se puede deshacer.") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Importar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

// Helper function to get file name from URI
fun getFileName(uri: Uri, context: android.content.Context): String {
    return try {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0 && cursor.moveToFirst()) {
                cursor.getString(nameIndex)
            } else {
                uri.toString().split("/").lastOrNull() ?: "archivo.csv"
            }
        } ?: run {
            // Fallback if cursor is null
            uri.toString().split("/").lastOrNull() ?: "archivo.csv"
        }
    } catch (e: Exception) {
        // Log the error for debugging
        android.util.Log.e("CSVImport", "Error getting file name", e)
        "archivo.csv"
    }
}