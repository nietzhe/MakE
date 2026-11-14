package com.enigma.kitchenorders

import android.os.Bundle
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
                    startDestination = Screen.Main.route
                ) {
                    composable(Screen.Main.route) {
                        val orderEntryViewModel: OrderEntryViewModel = viewModel(factory = viewModelFactory)
                        val kitchenViewModel: KitchenViewModel = viewModel(factory = viewModelFactory)

                        MainScreen(
                            businessName = businessName,
                            orderEntryViewModel = orderEntryViewModel,
                            kitchenViewModel = kitchenViewModel,
                            pdfExporter = pdfExporter,
                            onOpenSettings = { navController.navigate(Screen.Settings.route) }
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
