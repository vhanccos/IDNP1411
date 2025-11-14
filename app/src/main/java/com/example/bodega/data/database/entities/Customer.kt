package com.example.bodega.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true)
    val customerId: Int = 0,
    val firstName: String,
    val lastName: String,
    val email: String,
    val address: String,
    val phone: String
)
