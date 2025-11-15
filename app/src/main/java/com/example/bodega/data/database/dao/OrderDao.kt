package com.example.bodega.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Delete
import androidx.room.Update
import com.example.bodega.data.database.entities.Order
import com.example.bodega.data.database.entities.OrderDetail
import com.example.bodega.data.database.relations.OrderWithProducts
import com.example.bodega.data.database.relations.OrderWithDetails
import com.example.bodega.data.database.relations.OrderSummaryWithCustomer
import com.example.bodega.data.database.relations.OrderSummary
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order): Long

    @Update
    suspend fun updateOrder(order: Order)

    @Delete
    suspend fun deleteOrder(order: Order)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderDetail(orderDetail: OrderDetail)

    @Update
    suspend fun updateOrderDetail(orderDetail: OrderDetail)

    @Delete
    suspend fun deleteOrderDetail(orderDetail: OrderDetail)

    @Query("DELETE FROM order_details WHERE orderId = :orderId")
    suspend fun deleteOrderDetailsByOrderId(orderId: Int)


    @Query("SELECT * FROM orders")
    fun getAllOrders(): Flow<List<Order>>

    @Transaction
    @Query("""
        SELECT o.orderId, o.orderDate, SUM(p.price * od.quantity) as total, c.*
        FROM orders o
        JOIN order_details od ON o.orderId = od.orderId
        JOIN products p ON od.productId = p.productId
        JOIN customers c ON o.customerId = c.customerId
        GROUP BY o.orderId
    """)
    fun getAllOrderSummariesWithCustomer(): Flow<List<OrderSummaryWithCustomer>>

    @Transaction
    @Query("SELECT * FROM orders")
    fun getAllOrdersWithCustomer(): Flow<List<com.example.bodega.data.database.relations.OrderWithCustomer>>

    @Transaction
    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    fun getOrderWithProducts(orderId: Int): Flow<OrderWithProducts>

    @Transaction
    @Query("SELECT * FROM orders")
    fun getAllOrdersWithDetails(): Flow<List<OrderWithDetails>>

    @Transaction
    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    fun getOrderWithDetails(orderId: Int): Flow<OrderWithDetails>

    @Transaction
    @Query("""
        SELECT o.orderId, o.customerId, o.orderDate, SUM(p.price * od.quantity) as total
        FROM orders o
        JOIN order_details od ON o.orderId = od.orderId
        JOIN products p ON od.productId = p.productId
        WHERE o.customerId = :customerId
        GROUP BY o.orderId
    """)
    fun getOrderSummariesForCustomer(customerId: Int): Flow<List<OrderSummary>>

    @Query("SELECT COUNT(*) FROM orders")
    suspend fun getOrderCount(): Int

    @Query("SELECT COUNT(*) FROM order_details")
    suspend fun getOrderDetailCount(): Int
}
