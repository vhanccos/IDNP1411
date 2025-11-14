package com.example.bodega.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.bodega.ui.screens.customers.CustomerListScreen
import com.example.bodega.ui.screens.orders.OrderListScreen
import com.example.bodega.ui.screens.products.ProductListScreen
import com.example.bodega.ui.screens.products.AddEditProductScreen
import com.example.bodega.ui.screens.orders.NewOrderScreen
import com.example.bodega.ui.screens.orders.OrderDetailScreen
import com.example.bodega.ui.screens.customers.AddEditCustomerScreen
import com.example.bodega.ui.screens.import.CSVImportScreen
import com.example.bodega.viewmodel.CustomerViewModel
import com.example.bodega.viewmodel.OrderViewModel
import com.example.bodega.viewmodel.ProductViewModel
import com.example.bodega.viewmodel.ViewModelFactory

sealed class Screen(val route: String) {
    data object ProductList : Screen("product_list")
    data object AddEditProduct : Screen("add_edit_product") {
        fun createRoute(productId: Int) = "add_edit_product/$productId"
    }
    data object OrderList : Screen("order_list")
    data object NewOrder : Screen("new_order")
    data object OrderDetail : Screen("order_detail/{orderId}") {
        fun createRoute(orderId: Int) = "order_detail/$orderId"
    }
    data object EditOrder : Screen("edit_order/{orderId}") {
        fun createRoute(orderId: Int) = "edit_order/$orderId"
    }
    data object CustomerList : Screen("customer_list")
    data object AddEditCustomer : Screen("add_edit_customer") {
        fun createRoute(customerId: Int) = "add_edit_customer/$customerId"
    }
    data object CustomerOrders : Screen("customer_orders/{customerId}") {
        fun createRoute(customerId: Int) = "customer_orders/$customerId"
    }
    data object CSVImport : Screen("csv_import")
}

@Composable
fun AppNavigation(viewModelFactory: ViewModelFactory) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.ProductList.route) {
        composable(Screen.ProductList.route) {
            val viewModel: ProductViewModel = viewModel(factory = viewModelFactory)
            ProductListScreen(navController = navController, viewModel = viewModel)
        }
        composable(
            route = Screen.AddEditProduct.route,
            arguments = listOf(navArgument("productId") { type = NavType.IntType; defaultValue = -1 })
        ) { backStackEntry ->
            val viewModel: ProductViewModel = viewModel(factory = viewModelFactory)
            val productId = backStackEntry.arguments?.getInt("productId")
            AddEditProductScreen(navController, viewModel, if (productId == -1) null else productId)
        }
        composable(Screen.OrderList.route) {
            val viewModel: OrderViewModel = viewModel(factory = viewModelFactory)
            OrderListScreen(navController = navController, viewModel = viewModel)
        }
        composable(Screen.NewOrder.route) {
            val viewModel: OrderViewModel = viewModel(factory = viewModelFactory)
            NewOrderScreen(navController, viewModel)
        }
        composable(Screen.OrderDetail.route) { backStackEntry ->
            val viewModel: OrderViewModel = viewModel(factory = viewModelFactory)
            val orderId = backStackEntry.arguments?.getString("orderId")?.toIntOrNull()
            if (orderId != null) {
                OrderDetailScreen(navController, viewModel, orderId)
            }
        }
        composable(Screen.EditOrder.route) { backStackEntry ->
            val viewModel: OrderViewModel = viewModel(factory = viewModelFactory)
            val orderId = backStackEntry.arguments?.getString("orderId")?.toIntOrNull()
            if (orderId != null) {
                com.example.bodega.ui.screens.orders.EditOrderScreen(navController, viewModel, orderId)
            }
        }
        composable(Screen.CustomerList.route) {
            val viewModel: CustomerViewModel = viewModel(factory = viewModelFactory)
            CustomerListScreen(navController = navController, viewModel = viewModel)
        }
        composable(
            route = Screen.AddEditCustomer.route,
            arguments = listOf(navArgument("customerId") { type = NavType.IntType; defaultValue = -1 })
        ) { backStackEntry ->
            val viewModel: CustomerViewModel = viewModel(factory = viewModelFactory)
            val customerId = backStackEntry.arguments?.getInt("customerId")
            AddEditCustomerScreen(navController, viewModel, if (customerId == -1) null else customerId)
        }
        composable(
            route = Screen.CustomerOrders.route,
            arguments = listOf(navArgument("customerId") { type = NavType.IntType; defaultValue = -1 })
        ) { backStackEntry ->
            val customerViewModel: CustomerViewModel = viewModel(factory = viewModelFactory)
            val orderViewModel: OrderViewModel = viewModel(factory = viewModelFactory)
            val customerId = backStackEntry.arguments?.getInt("customerId")
            if (customerId != null) {
                com.example.bodega.ui.screens.customers.CustomerOrdersScreen(navController, customerViewModel, orderViewModel, customerId)
            }
        }
        composable(Screen.CSVImport.route) {
            val viewModel: ProductViewModel = viewModel(factory = viewModelFactory)
            CSVImportScreen(navController = navController, productViewModel = viewModel)
        }
    }
}
