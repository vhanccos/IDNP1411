package com.example.bodega.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bodega.ui.navigation.Screen
import com.example.bodega.ui.screens.customers.AddEditCustomerScreen
import com.example.bodega.ui.screens.customers.CustomerListScreen
import com.example.bodega.ui.screens.customers.CustomerOrdersScreen
import com.example.bodega.ui.screens.import.CSVImportScreen
import com.example.bodega.ui.screens.orders.NewOrderScreen
import com.example.bodega.ui.screens.orders.OrderDetailScreen
import com.example.bodega.ui.screens.orders.EditOrderScreen
import com.example.bodega.ui.screens.orders.OrderListScreen
import com.example.bodega.ui.screens.products.AddEditProductScreen
import com.example.bodega.ui.screens.products.ProductListScreen
import com.example.bodega.viewmodel.CustomerViewModel
import com.example.bodega.viewmodel.OrderViewModel
import com.example.bodega.viewmodel.ProductViewModel
import com.example.bodega.viewmodel.ViewModelFactory

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    data object Products : BottomNavItem(Screen.ProductList.route, Icons.Default.ShoppingCart, "Productos")
    data object Orders : BottomNavItem(Screen.OrderList.route, Icons.Default.ShoppingCart, "Pedidos")
    data object Customers : BottomNavItem(Screen.CustomerList.route, Icons.Default.Person, "Clientes")
}

@Composable
fun MainScreen(viewModelFactory: ViewModelFactory) {
    val navController = rememberNavController()

    val bottomNavItems = listOf(
        BottomNavItem.Products,
        BottomNavItem.Orders,
        BottomNavItem.Customers
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.ProductList.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.ProductList.route) {
                val viewModel: ProductViewModel = viewModel(factory = viewModelFactory)
                ProductListScreen(navController = navController, viewModel = viewModel)
            }
            composable(Screen.OrderList.route) {
                val viewModel: OrderViewModel = viewModel(factory = viewModelFactory)
                OrderListScreen(navController = navController, viewModel = viewModel)
            }
            composable(Screen.CustomerList.route) {
                val viewModel: CustomerViewModel = viewModel(factory = viewModelFactory)
                CustomerListScreen(navController = navController, viewModel = viewModel)
            }
            composable("add_edit_product") { backStackEntry ->
                val viewModel: ProductViewModel = viewModel(factory = viewModelFactory)
                AddEditProductScreen(navController = navController, viewModel = viewModel, productId = null)
            }
            composable(
                route = "add_edit_product/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.IntType })
            ) { backStackEntry ->
                val viewModel: ProductViewModel = viewModel(factory = viewModelFactory)
                val productId = backStackEntry.arguments?.getInt("productId")
                AddEditProductScreen(navController = navController, viewModel = viewModel, productId = productId)
            }
            composable("add_edit_customer") { backStackEntry ->
                val viewModel: CustomerViewModel = viewModel(factory = viewModelFactory)
                AddEditCustomerScreen(navController = navController, viewModel = viewModel, customerId = null)
            }
            composable(
                route = "add_edit_customer/{customerId}",
                arguments = listOf(navArgument("customerId") { type = NavType.IntType })
            ) { backStackEntry ->
                val viewModel: CustomerViewModel = viewModel(factory = viewModelFactory)
                val customerId = backStackEntry.arguments?.getInt("customerId")
                AddEditCustomerScreen(navController = navController, viewModel = viewModel, customerId = customerId)
            }
            composable(Screen.NewOrder.route) {
                val viewModel: OrderViewModel = viewModel(factory = viewModelFactory)
                NewOrderScreen(navController = navController, viewModel = viewModel)
            }
            composable(Screen.OrderDetail.route) { backStackEntry ->
                val viewModel: OrderViewModel = viewModel(factory = viewModelFactory)
                val orderId = backStackEntry.arguments?.getString("orderId")?.toIntOrNull()
                if (orderId != null) {
                    OrderDetailScreen(navController, viewModel, orderId)
                }
            }
            composable(
                route = "edit_order/{orderId}",
                arguments = listOf(navArgument("orderId") { type = NavType.IntType })
            ) { backStackEntry ->
                val viewModel: OrderViewModel = viewModel(factory = viewModelFactory)
                val orderId = backStackEntry.arguments?.getInt("orderId")
                EditOrderScreen(navController, viewModel, orderId ?: -1)
            }
            composable(Screen.CustomerOrders.route) { backStackEntry ->
                val customerViewModel: CustomerViewModel = viewModel(factory = viewModelFactory)
                val orderViewModel: OrderViewModel = viewModel(factory = viewModelFactory)
                val customerId = backStackEntry.arguments?.getString("customerId")?.toIntOrNull()
                if (customerId != null) {
                    CustomerOrdersScreen(navController, customerViewModel, orderViewModel, customerId)
                }
            }
            composable("csv_import") {
                val viewModel: ProductViewModel = viewModel(factory = viewModelFactory)
                CSVImportScreen(navController = navController, productViewModel = viewModel)
            }
        }
    }
}
