package com.enigma.kitchenorders.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.enigma.kitchenorders.ui.theme.Kunyit
import com.enigma.kitchenorders.ui.theme.KunyitPale
import com.enigma.kitchenorders.ui.theme.Sambal


@Composable
fun SteamingPotIllustration(
    modifier: Modifier = Modifier,
    animateSteam: Boolean = true
) {
    val transition = rememberInfiniteTransition(label = "SteamTransition")
    val steamOffset by if (animateSteam) {
        transition.animateFloat(
            initialValue = 0f,
            targetValue = -15f,
            animationSpec = infiniteRepeatable(
                animation = tween(3500, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "SteamOffset"
        )
    } else {
        rememberUpdatedState(0f)
    }

    val steamAlpha by if (animateSteam) {
        transition.animateFloat(
            initialValue = 0.6f,
            targetValue = 0.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(3500, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "SteamAlpha"
        )
    } else {
        rememberUpdatedState(0.6f)
    }

    Canvas(modifier = modifier.size(96.dp, 110.dp)) {
        val w = size.width
        val h = size.height
        val steamColor = KunyitPale.copy(alpha = steamAlpha)
        val steamStroke = Stroke(width = 3.dp.toPx())

        fun drawSteamLine(startX: Float, curyOffset: Float) {
            val path = Path().apply {
                moveTo(startX, h * 0.32f + curyOffset)
                cubicTo(
                    startX - 8.dp.toPx(), h * 0.22f + curyOffset,
                    startX + 8.dp.toPx(), h * 0.12f + curyOffset,
                    startX, h * 0.04f + curyOffset
                )
            }
            drawPath(path, color = steamColor, style = steamStroke)
        }

        drawSteamLine(w * 0.35f, steamOffset)
        drawSteamLine(w * 0.50f, steamOffset * 0.8f)
        drawSteamLine(w * 0.65f, steamOffset * 1.1f)

        val potPath = Path().apply {
            moveTo(w * 0.20f, h * 0.45f)
            lineTo(w * 0.80f, h * 0.45f)
            lineTo(w * 0.75f, h * 0.85f)
            quadraticBezierTo(w * 0.50f, h * 0.95f, w * 0.25f, h * 0.85f)
            close()
        }
        drawPath(potPath, color = Kunyit)

        val handleStroke = Stroke(width = 4.dp.toPx())
        drawArc(
            color = Sambal,
            startAngle = 100f,
            sweepAngle = 160f,
            useCenter = false,
            topLeft = Offset(w * 0.06f, h * 0.52f),
            size = Size(w * 0.20f, h * 0.20f),
            style = handleStroke
        )
        
        drawArc(
            color = Sambal,
            startAngle = -80f,
            sweepAngle = 160f,
            useCenter = false,
            topLeft = Offset(w * 0.74f, h * 0.52f),
            size = Size(w * 0.20f, h * 0.20f),
            style = handleStroke
        )

        drawRoundRect(
            color = KunyitPale,
            topLeft = Offset(w * 0.15f, h * 0.38f),
            size = Size(w * 0.70f, h * 0.08f),
            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
        )

        drawCircle(
            color = Kunyit,
            radius = w * 0.06f,
            center = Offset(w * 0.50f, h * 0.35f)
        )
    }
}
