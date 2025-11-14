package com.example.bodega.data.repository

import com.example.bodega.data.database.dao.CategoryDao
import com.example.bodega.data.database.dao.CustomerDao
import com.example.bodega.data.database.dao.OrderDao
import com.example.bodega.data.database.dao.ProductDao
import com.example.bodega.data.database.entities.Category
import com.example.bodega.data.database.entities.Customer
import com.example.bodega.data.database.entities.Order
import com.example.bodega.data.database.entities.OrderDetail
import com.example.bodega.data.database.entities.Product
import com.example.bodega.data.database.relations.CategoryWithProducts
import com.example.bodega.data.database.relations.CustomerWithOrders
import com.example.bodega.data.database.relations.OrderWithDetails
import com.example.bodega.data.database.relations.OrderWithProducts
import kotlinx.coroutines.flow.Flow

class BodegaRepositoryImpl(
    private val categoryDao: CategoryDao,
    private val customerDao: CustomerDao,
    private val productDao: ProductDao,
    private val orderDao: OrderDao
) : BodegaRepository {

        // Category

        override fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()

        override fun getCategoryWithProducts(categoryId: Int): Flow<CategoryWithProducts> = categoryDao.getCategoryWithProducts(categoryId)

        override suspend fun insertCategory(category: Category) = categoryDao.insertCategory(category)

        override suspend fun updateCategory(category: Category) = categoryDao.updateCategory(category)

        override suspend fun deleteCategory(category: Category) = categoryDao.deleteCategory(category)

    

        // Customer

        override fun getAllCustomers(): Flow<List<Customer>> = customerDao.getAllCustomers()

            override fun getCustomerWithOrders(customerId: Int): Flow<CustomerWithOrders> = customerDao.getCustomerWithOrders(customerId)

            override suspend fun insertCustomer(customer: Customer) = customerDao.insertCustomer(customer)

            override suspend fun updateCustomer(customer: Customer) = customerDao.updateCustomer(customer)

            override suspend fun deleteCustomer(customer: Customer) = customerDao.deleteCustomer(customer)

            override fun getCustomerById(customerId: Int): Flow<Customer> = customerDao.getCustomerById(customerId)

    

        // Product

        override fun getAllProducts(): Flow<List<Product>> = productDao.getAllProducts()

        override fun getProductById(productId: Int): Flow<Product> = productDao.getProductById(productId)

        override suspend fun insertProduct(product: Product) = productDao.insertProduct(product)

        override suspend fun updateProduct(product: Product) = productDao.updateProduct(product)

        override suspend fun deleteProduct(product: Product) = productDao.deleteProduct(product)

        override suspend fun getCategoryCount(): Int = categoryDao.getCategoryCount()

        override suspend fun getCustomerCount(): Int = customerDao.getCustomerCount()

        override suspend fun getProductCount(): Int = productDao.getProductCount()


        // Order

        override fun getAllOrders(): Flow<List<Order>> = orderDao.getAllOrders()

        override fun getOrderWithProducts(orderId: Int): Flow<OrderWithProducts> = orderDao.getOrderWithProducts(orderId)

        override fun getAllOrdersWithDetails(): Flow<List<com.example.bodega.data.database.relations.OrderWithDetails>> = orderDao.getAllOrdersWithDetails()

        override fun getAllOrdersWithCustomer(): Flow<List<com.example.bodega.data.database.relations.OrderWithCustomer>> = orderDao.getAllOrdersWithCustomer()


        override fun getOrderWithDetails(orderId: Int): Flow<OrderWithDetails> = orderDao.getOrderWithDetails(orderId)

        override suspend fun insertOrderWithDetails(order: Order, products: Map<Product, Int>) {

            val orderId = orderDao.insertOrder(order)

            products.forEach { (product, quantity) ->

                val orderDetail = OrderDetail(

                    orderId = orderId.toInt(),

                    productId = product.productId,

                    quantity = quantity

                )

                orderDao.insertOrderDetail(orderDetail)

            }

        }

        override suspend fun updateOrderWithDetails(order: Order, products: Map<Product, Int>) {
            orderDao.deleteOrderDetailsByOrderId(order.orderId)
            products.forEach { (product, quantity) ->
                val orderDetail = OrderDetail(
                    orderId = order.orderId,
                    productId = product.productId,
                    quantity = quantity
                )
                orderDao.insertOrderDetail(orderDetail)
            }
        }

        override suspend fun updateOrder(order: Order) = orderDao.updateOrder(order)

        override suspend fun deleteOrder(order: Order) = orderDao.deleteOrder(order)

        override suspend fun deleteOrderWithDetails(order: Order) {
            orderDao.deleteOrder(order) // This will cascade delete the details due to the foreign key constraint
        }
}
