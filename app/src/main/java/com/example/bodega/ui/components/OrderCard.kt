package com.example.bodega.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bodega.data.database.relations.OrderSummaryWithCustomer
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun OrderCard(
    order: OrderSummaryWithCustomer,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "PE"))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Pedido #${order.orderId}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Cliente: ${order.customer.firstName} ${order.customer.lastName}", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "Fecha: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(order.orderDate)}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Total: ${currencyFormat.format(order.total)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Row(modifier = Modifier.padding(top = 8.dp)) {
                Button(onClick = onEdit) {
                    Text("Editar")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onDelete) {
                    Text("Eliminar")
                }
            }
        }
    }
}
