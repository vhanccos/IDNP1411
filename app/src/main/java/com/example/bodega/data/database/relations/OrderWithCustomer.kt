package com.example.bodega.data.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.bodega.data.database.entities.Customer
import com.example.bodega.data.database.entities.Order

data class OrderWithCustomer(
    @Embedded val order: Order,
    @Relation(
        parentColumn = "customerId",
        entityColumn = "customerId"
    )
    val customer: Customer
)
