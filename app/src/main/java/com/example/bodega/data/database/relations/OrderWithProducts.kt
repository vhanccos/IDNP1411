package com.example.bodega.data.database.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.bodega.data.database.entities.Order
import com.example.bodega.data.database.entities.OrderDetail
import com.example.bodega.data.database.entities.Product

data class OrderWithProducts(
    @Embedded val order: Order,
    @Relation(
        parentColumn = "orderId",
        entityColumn = "productId",
        associateBy = Junction(OrderDetail::class)
    )
    val products: List<Product>
)

// A separate relation for getting order details with quantities
data class OrderWithDetails(
    @Embedded val order: Order,
    @Relation(
        parentColumn = "orderId",
        entityColumn = "orderId",
        entity = OrderDetail::class
    )
    val orderDetails: List<OrderDetail>
)
