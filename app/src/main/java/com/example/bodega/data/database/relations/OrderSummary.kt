package com.example.bodega.data.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.bodega.data.database.entities.Order
import com.example.bodega.data.database.entities.OrderDetail
import com.example.bodega.data.database.entities.Product
import java.math.BigDecimal

data class ProductWithQuantity(
    @Embedded val product: Product,
    val quantity: Int
) {
    fun getTotalPrice(): BigDecimal = product.price.multiply(quantity.toBigDecimal())
}

data class OrderSummary(
    @Embedded val order: Order,
    @Relation(
        parentColumn = "orderId",
        entityColumn = "orderId",
        entity = OrderDetail::class
    )
    val orderDetails: List<OrderDetail>
) {
    fun getTotalAmount(productsMap: Map<Int, Product>): BigDecimal {
        var total = BigDecimal.ZERO
        for (detail in orderDetails) {
            val product = productsMap[detail.productId]
            if (product != null) {
                total = total.add(product.price.multiply(detail.quantity.toBigDecimal()))
            }
        }
        return total
    }
    
    fun getProductsCount(): Int = orderDetails.size
    
    fun getProductsDetailed(productsMap: Map<Int, Product>): List<ProductWithQuantity> {
        return orderDetails.mapNotNull { detail ->
            val product = productsMap[detail.productId]
            if (product != null) {
                ProductWithQuantity(product, detail.quantity)
            } else {
                null
            }
        }
    }
}