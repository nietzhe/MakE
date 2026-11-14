package com.enigma.kitchenorders.ui.navigation

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Settings : Screen("settings")
    object MenuManagement : Screen("menu_management")
}
