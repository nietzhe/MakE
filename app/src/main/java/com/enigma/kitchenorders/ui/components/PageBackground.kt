package com.enigma.kitchenorders.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.enigma.kitchenorders.ui.theme.Kopi
import com.enigma.kitchenorders.ui.theme.Kunyit
import com.enigma.kitchenorders.ui.theme.Sambal


@Composable
fun DarkPageBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize().background(Kopi)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Sambal.copy(alpha = 0.65f), Color.Transparent),
                    center = Offset(width, 0f),
                    radius = width * 0.9f
                ),
                radius = width * 0.9f,
                center = Offset(width, 0f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Kunyit.copy(alpha = 0.25f), Color.Transparent),
                    center = Offset(0f, height),
                    radius = width * 0.8f
                ),
                radius = width * 0.8f,
                center = Offset(0f, height)
            )
        }
        content()
    }
}
