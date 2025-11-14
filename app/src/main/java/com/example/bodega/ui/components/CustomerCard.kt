package com.example.bodega.ui.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bodega.data.database.entities.Customer

@Composable
fun CustomerCard(customer: Customer, modifier: Modifier = Modifier, onClick: () -> Unit = {}, onLongClick: () -> Unit = {}) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "${customer.firstName} ${customer.lastName}", style = MaterialTheme.typography.titleMedium)
            Text(text = customer.email, style = MaterialTheme.typography.bodyMedium)
            Text(text = customer.address, style = MaterialTheme.typography.bodySmall)
            Text(text = customer.phone, style = MaterialTheme.typography.bodySmall)
        }
    }
}
