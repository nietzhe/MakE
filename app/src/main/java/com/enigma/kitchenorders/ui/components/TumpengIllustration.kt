package com.enigma.kitchenorders.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.enigma.kitchenorders.ui.theme.Kunyit
import com.enigma.kitchenorders.ui.theme.KunyitLight
import com.enigma.kitchenorders.ui.theme.Pandan
import com.enigma.kitchenorders.ui.theme.Sambal


@Composable
fun TumpengIllustration(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(140.dp, 104.dp)) {
        val w = size.width
        val h = size.height

        // banana leaf plate
        val leafPath = Path().apply {
            moveTo(w * 0.05f, h * 0.75f)
            quadraticBezierTo(w * 0.5f, h * 0.98f, w * 0.95f, h * 0.75f)
            quadraticBezierTo(w * 0.98f, h * 0.88f, w * 0.85f, h * 0.96f)
            quadraticBezierTo(w * 0.5f, h * 1.05f, w * 0.15f, h * 0.96f)
            quadraticBezierTo(w * 0.02f, h * 0.88f, w * 0.05f, h * 0.75f)
        }
        drawPath(leafPath, color = Pandan)

        // leaf detail
        drawCircle(
            color = Color(0xFF3B613D),
            radius = w * 0.04f,
            center = Offset(w * 0.2f, h * 0.88f)
        )
        drawCircle(
            color = Color(0xFF3B613D),
            radius = w * 0.04f,
            center = Offset(w * 0.8f, h * 0.88f)
        )

        // Tumpeng (yellow cone)
        val conePath = Path().apply {
            moveTo(w * 0.5f, h * 0.18f) // Top peak
            lineTo(w * 0.82f, h * 0.78f) // Right base
            quadraticBezierTo(w * 0.5f, h * 0.86f, w * 0.18f, h * 0.78f) // Curved bottom
            close()
        }
        drawPath(conePath, color = Kunyit)

        // cone highlight / texture
        val highlightPath = Path().apply {
            moveTo(w * 0.5f, h * 0.18f)
            lineTo(w * 0.54f, h * 0.79f)
            quadraticBezierTo(w * 0.5f, h * 0.86f, w * 0.18f, h * 0.78f)
            close()
        }
        drawPath(highlightPath, color = KunyitLight)

        // rice texture
        val ringColor = Color(0xFFD6920E)
        drawArc(
            color = ringColor,
            startAngle = 10f,
            sweepAngle = 160f,
            useCenter = false,
            topLeft = Offset(w * 0.32f, h * 0.38f),
            size = Size(w * 0.36f, h * 0.12f),
            style = Stroke(width = 3.dp.toPx())
        )
        drawArc(
            color = ringColor,
            startAngle = 10f,
            sweepAngle = 160f,
            useCenter = false,
            topLeft = Offset(w * 0.24f, h * 0.54f),
            size = Size(w * 0.52f, h * 0.15f),
            style = Stroke(width = 3.dp.toPx())
        )

        // timun (cucumber)
        drawCircle(
            color = Color(0xFF7CAF7A),
            radius = w * 0.05f,
            center = Offset(w * 0.22f, h * 0.76f)
        )
        drawCircle(
            color = Color(0xFF4C7A4F),
            radius = w * 0.035f,
            center = Offset(w * 0.22f, h * 0.76f)
        )

        drawCircle(
            color = Color(0xFF7CAF7A),
            radius = w * 0.05f,
            center = Offset(w * 0.78f, h * 0.76f)
        )
        drawCircle(
            color = Color(0xFF4C7A4F),
            radius = w * 0.035f,
            center = Offset(w * 0.78f, h * 0.76f)
        )

        // sambal (red chili)
        val chiliPath = Path().apply {
            moveTo(w * 0.5f, h * 0.04f)
            quadraticBezierTo(w * 0.55f, h * 0.10f, w * 0.51f, h * 0.20f)
            quadraticBezierTo(w * 0.48f, h * 0.12f, w * 0.5f, h * 0.04f)
        }
        drawPath(chiliPath, color = Sambal)

        // chili leaf cap
        drawCircle(
            color = Pandan,
            radius = w * 0.02f,
            center = Offset(w * 0.5f, h * 0.18f)
        )
    }
}
