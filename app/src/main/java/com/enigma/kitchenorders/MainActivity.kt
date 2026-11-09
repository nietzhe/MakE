package com.enigma.kitchenorders

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.enigma.kitchenorders.ui.KitchenViewModelFactory
import com.enigma.kitchenorders.ui.navigation.Screen
import com.enigma.kitchenorders.ui.screens.*
import com.enigma.kitchenorders.ui.theme.KitchenOrdersTheme
import com.enigma.kitchenorders.ui.viewmodel.KitchenViewModel
import com.enigma.kitchenorders.ui.viewmodel.OrderEntryViewModel
import com.enigma.kitchenorders.ui.viewmodel.SettingsViewModel
import com.enigma.kitchenorders.util.PdfExporter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val app = application as KitchenApp
        val viewModelFactory = KitchenViewModelFactory(app.repository, app.preferencesManager)
        val pdfExporter = PdfExporter(this)
        
        setContent {
            KitchenOrdersTheme {
                val navController = rememberNavController()
                val scope = rememberCoroutineScope()
                val businessName by app.preferencesManager.businessName.collectAsState(initial = "Dapur Saya")
                
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route
                ) {
                    composable(Screen.Home.route) {
                        HomeScreen(
                            businessName = businessName,
                            onNavigateToKasir = { navController.navigate(Screen.OrderEntry.route) },
                            onNavigateToDapur = { navController.navigate(Screen.Kitchen.route) },
                            onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                        )
                    }
                    
                    composable(Screen.OrderEntry.route) {
                        val viewModel: OrderEntryViewModel = viewModel(factory = viewModelFactory)
                        val menus by viewModel.activeMenus.collectAsState()
                        val cart by viewModel.cart.collectAsState()
                        val customerName by viewModel.customerName.collectAsState()
                        val deliveryDate by viewModel.deliveryDate.collectAsState()
                        
                        // Listen for order saved events
                        LaunchedEffect(viewModel) {
                            viewModel.orderSaved.collectLatest { savedCustomerName ->
                                Toast.makeText(
                                    this@MainActivity,
                                    "Pesanan untuk \"$savedCustomerName\" berhasil disimpan!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.popBackStack()
                            }
                        }
                        
                        OrderEntryScreen(
                            menus = menus,
                            cart = cart,
                            customerName = customerName,
                            deliveryDate = deliveryDate,
                            onCustomerNameChange = viewModel::setCustomerName,
                            onDeliveryDateChange = viewModel::setDeliveryDate,
                            onAddItem = viewModel::addItemToCart,
                            onRemoveItem = viewModel::removeCartItem,
                            onSubmitOrder = {
                                viewModel.submitOrder()
                            },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    
                    composable(Screen.Kitchen.route) {
                        val viewModel: KitchenViewModel = viewModel(factory = viewModelFactory)
                        val selectedDate by viewModel.selectedDate.collectAsState()
                        val filterKeyword by viewModel.filterKeyword.collectAsState()
                        val aggregations by viewModel.uiState.collectAsState()
                        
                        KitchenScreen(
                            businessName = businessName,
                            selectedDate = selectedDate,
                            filterKeyword = filterKeyword,
                            menuAggregations = aggregations,
                            onDateChange = viewModel::setDate,
                            onFilterChange = viewModel::setFilter,
                            onExportPdf = {
                                pdfExporter.exportAndShare(
                                    businessName = businessName,
                                    deliveryDate = selectedDate,
                                    aggregations = aggregations
                                )
                            },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    
                    composable(Screen.Settings.route) {
                        val viewModel: SettingsViewModel = viewModel(factory = viewModelFactory)
                        val menus by viewModel.allMenus.collectAsState()
                        
                        SettingsScreen(
                            businessName = businessName,
                            menus = menus,
                            onBusinessNameChange = { name ->
                                scope.launch {
                                    app.preferencesManager.setBusinessName(name)
                                }
                            },
                            onSaveMenu = viewModel::saveMenu,
                            onDeleteMenu = viewModel::deleteMenu,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
