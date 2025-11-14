package com.example.bodega.data.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.bodega.data.database.entities.Category
import com.example.bodega.data.database.entities.Product

data class CategoryWithProducts(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId"
    )
    val products: List<Product>
)
