package com.example.bodega

import android.app.Application
import com.example.bodega.data.database.BodegaDatabase
import com.example.bodega.data.repository.BodegaRepositoryImpl
import com.example.bodega.data.utils.DataInitializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class BodegaApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { BodegaDatabase.getDatabase(this) }
    val repository by lazy {
        BodegaRepositoryImpl(
            database.categoryDao(),
            database.customerDao(),
            database.productDao(),
            database.orderDao()
        )
    }

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            val dataInitializer = DataInitializer(this@BodegaApplication, repository)
            // Check if database is empty before populating
            if (dataInitializer.shouldPopulateDatabase()) {
                dataInitializer.populateDatabase()
            }
        }
    }
}
