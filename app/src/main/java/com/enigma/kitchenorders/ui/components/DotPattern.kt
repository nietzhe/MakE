package com.enigma.kitchenorders.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Decorative dot pattern specified in UI_ENHANCE.md Section 5.1:
 * - White dots at alpha 0.14 (radius 3dp, 22dp grid)
 * - Layered with larger circles at alpha 0.07 (radius 12dp, 44dp grid, 11dp offset)
 * Drawn via Canvas to keep APK size lightweight.
 */
@Composable
fun DotPattern(
    modifier: Modifier = Modifier,
    baseAlpha: Float = 1.0f
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        val smallGridSize = 22.dp.toPx()
        val smallRadius = 3.dp.toPx()
        val smallDotColor = Color.White.copy(alpha = 0.14f * baseAlpha)

        val largeGridSize = 44.dp.toPx()
        val largeRadius = 12.dp.toPx()
        val largeOffset = 11.dp.toPx()
        val largeDotColor = Color.White.copy(alpha = 0.07f * baseAlpha)

        // Draw small dot grid
        var x = smallGridSize / 2
        while (x < width) {
            var y = smallGridSize / 2
            while (y < height) {
                drawCircle(
                    color = smallDotColor,
                    radius = smallRadius,
                    center = Offset(x, y)
                )
                y += smallGridSize
            }
            x += smallGridSize
        }

        // Draw larger circle overlay grid
        var lx = largeGridSize / 2 + largeOffset
        while (lx < width + largeRadius) {
            var ly = largeGridSize / 2 + largeOffset
            while (ly < height + largeRadius) {
                drawCircle(
                    color = largeDotColor,
                    radius = largeRadius,
                    center = Offset(lx, ly)
                )
                ly += largeGridSize
            }
            lx += largeGridSize
        }
    }
}
