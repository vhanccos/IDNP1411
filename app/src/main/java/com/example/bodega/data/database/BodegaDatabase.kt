package com.example.bodega.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.bodega.data.database.dao.CategoryDao
import com.example.bodega.data.database.dao.CustomerDao
import com.example.bodega.data.database.dao.OrderDao
import com.example.bodega.data.database.dao.ProductDao
import com.example.bodega.data.database.entities.Category
import com.example.bodega.data.database.entities.Customer
import com.example.bodega.data.database.entities.Order
import com.example.bodega.data.database.entities.OrderDetail
import com.example.bodega.data.database.entities.Product

@Database(
    entities = [
        Category::class,
        Customer::class,
        Product::class,
        Order::class,
        OrderDetail::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BodegaDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun customerDao(): CustomerDao
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: BodegaDatabase? = null

        fun getDatabase(context: Context): BodegaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BodegaDatabase::class.java,
                    "bodega_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
