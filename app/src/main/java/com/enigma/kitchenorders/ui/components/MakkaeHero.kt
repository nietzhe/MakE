package com.enigma.kitchenorders.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enigma.kitchenorders.ui.theme.*
import java.util.Calendar

/**
 * Makkae Hero Header component specified in UI_ENHANCE.md Section 6.1 & Section 9.
 * Height ≈ 272dp, bottom corners 44dp.
 * Sambal base background with 160° SambalLight -> SambalDark gradient,
 * DotPattern overlay, large Kunyit circle at top-right, and Tumpeng/Pot illustration.
 */
@Composable
fun MakkaeHero(
    pendingOrdersCount: Int,
    isKitchenMode: Boolean = false,
    dateText: String = "",
    modifier: Modifier = Modifier
) {
    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greetingText = when {
        currentHour < 11 -> "Selamat pagi, Mak!"
        currentHour < 15 -> "Selamat siang, Mak!"
        currentHour < 18 -> "Selamat sore, Mak!"
        else -> "Selamat malam, Mak!"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(272.dp)
            .clip(RoundedCornerShape(bottomStart = 44.dp, bottomEnd = 44.dp))
            .background(Sambal) // Section 9 rule: Base color to ensure text contrast
            .background(
                Brush.linearGradient(
                    colors = listOf(SambalLight, SambalDark),
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 1000f)
                )
            )
    ) {
        // 1. Dot Pattern overlay
        DotPattern(baseAlpha = if (isKitchenMode) 0.6f else 1.0f)

        // 2. Large Kunyit circle bleeding out top-right (190dp, alpha 0.9)
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val circleRadius = 95.dp.toPx()
            drawCircle(
                color = Kunyit.copy(alpha = 0.9f),
                radius = circleRadius,
                center = Offset(size.width - 20.dp.toPx(), 40.dp.toPx())
            )
        }

        // 3. Top-right Illustration
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 40.dp, end = 16.dp)
        ) {
            if (isKitchenMode) {
                SteamingPotIllustration()
            } else {
                TumpengIllustration()
            }
        }

        // 4. Content Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Row: Logo
            Text(
                text = "MAKKAE",
                fontFamily = FrauncesFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                letterSpacing = 0.1.sp,
                color = KunyitPale
            )

            // Middle Section: Dynamic Greeting & Subtitle / Date
            Column {
                if (isKitchenMode && dateText.isNotBlank()) {
                    Text(
                        text = "AYO MASAK UNTUK",
                        fontFamily = PlusJakartaSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 0.06.sp,
                        color = KunyitPale
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = dateText,
                        fontFamily = FrauncesFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 32.sp,
                        lineHeight = 36.sp,
                        color = Color.White
                    )
                } else {
                    Text(
                        text = greetingText,
                        fontFamily = FrauncesFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 30.sp,
                        lineHeight = 34.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Translucent capsule chip (white at alpha 0.2)
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "$pendingOrdersCount pesanan menunggu hari ini",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        fontFamily = PlusJakartaSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }

            // Bottom spacing reserved for floating toggle overlap (-26dp)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
