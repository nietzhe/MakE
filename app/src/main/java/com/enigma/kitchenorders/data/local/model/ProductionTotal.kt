package com.enigma.kitchenorders.data.local.model

/** Total quantity still to cook for one menu item. Intentionally carries no customer data. */
data class ProductionTotal(
    val menuName: String,
    val totalQuantity: Int
)
