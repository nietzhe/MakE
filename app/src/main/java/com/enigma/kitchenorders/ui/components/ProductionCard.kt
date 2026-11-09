package com.enigma.kitchenorders.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enigma.kitchenorders.ui.theme.*

enum class KitchenStatus {
    DONE, COOKING, NOT_STARTED
}


@Composable
fun StatusBadge(
    status: KitchenStatus,
    modifier: Modifier = Modifier
) {
    val (color, icon, desc) = when (status) {
        KitchenStatus.DONE -> Triple(KunyitLight, Icons.Default.Check, "Selesai")
        KitchenStatus.COOKING -> Triple(Salmon, Icons.Default.LocalFireDepartment, "Sedang dimasak")
        KitchenStatus.NOT_STARTED -> Triple(PandanPale, Icons.Default.Schedule, "Belum mulai")
    }

    Surface(
        modifier = modifier.size(56.dp),
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.08f),
        border = BorderStroke(2.dp, color)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = desc,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}


@Composable
fun ProductionCard(
    menuName: String,
    ordersCount: Int,
    totalQuantity: Int,
    status: KitchenStatus,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = when (status) {
        KitchenStatus.DONE -> KunyitLight
        KitchenStatus.COOKING -> Salmon
        KitchenStatus.NOT_STARTED -> PandanPale
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, KunyitPale.copy(alpha = 0.15f)),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(KopiCardStart, KopiCardEnd),
                        start = Offset(0f, 0f),
                        end = Offset(500f, 500f)
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatusBadge(status = status)

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = menuName,
                        fontFamily = PlusJakartaSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "dari $ordersCount pesanan",
                        fontFamily = PlusJakartaSansFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "$totalQuantity",
                    fontFamily = FrauncesFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 40.sp,
                    color = statusColor
                )
            }
        }
    }
}
