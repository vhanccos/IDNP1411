package com.example.bodega.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Delete
import androidx.room.Update
import com.example.bodega.data.database.entities.Customer
import com.example.bodega.data.database.relations.CustomerWithOrders
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer)

    @Update
    suspend fun updateCustomer(customer: Customer)

    @Delete
    suspend fun deleteCustomer(customer: Customer)

    @Query("SELECT * FROM customers")
    fun getAllCustomers(): Flow<List<Customer>>

    @Transaction
    @Query("SELECT * FROM customers WHERE customerId = :customerId")
    fun getCustomerWithOrders(customerId: Int): Flow<CustomerWithOrders>

    @Query("SELECT * FROM customers WHERE customerId = :customerId")
    fun getCustomerById(customerId: Int): Flow<Customer>

    @Query("SELECT COUNT(*) FROM customers")
    suspend fun getCustomerCount(): Int
}
