package com.example.bodega.data.database.relations

import java.math.BigDecimal
import java.util.Date

data class OrderSummary(
    val orderId: Int,
    val customerId: Int,
    val orderDate: Date,
    val total: BigDecimal
)
