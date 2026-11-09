package com.enigma.kitchenorders.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.RiceBowl
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.enigma.kitchenorders.ui.theme.Kopi


@Composable
fun MenuIconBox(
    menuName: String,
    modifier: Modifier = Modifier
) {
    val nameLower = menuName.lowercase()

    val (bgColor, icon) = when {
        nameLower.contains("nasi") || nameLower.contains("rice") -> {
            Color(0xFFFFE9B0) to Icons.Default.RiceBowl
        }
        nameLower.contains("ayam") || nameLower.contains("daging") || nameLower.contains("chicken") || nameLower.contains("bakar") || nameLower.contains("goreng") -> {
            Color(0xFFFFD9CF) to Icons.Default.Restaurant
        }
        nameLower.contains("urap") || nameLower.contains("sayur") || nameLower.contains("tumis") || nameLower.contains("lalap") -> {
            Color(0xFFDDEBCB) to Icons.Default.Grass
        }
        else -> {
            Color(0xFFF1E4CE) to Icons.Default.Fastfood
        }
    }

    Surface(
        modifier = modifier.size(48.dp),
        shape = RoundedCornerShape(16.dp),
        color = bgColor
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Kopi,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}
