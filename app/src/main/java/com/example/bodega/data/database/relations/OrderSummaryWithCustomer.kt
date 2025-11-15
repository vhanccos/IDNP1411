package com.example.bodega.data.database.relations

import androidx.room.Embedded
import com.example.bodega.data.database.entities.Customer
import java.math.BigDecimal
import java.util.Date

data class OrderSummaryWithCustomer(
    val orderId: Int,
    val orderDate: Date,
    val total: BigDecimal,
    @Embedded val customer: Customer
)
