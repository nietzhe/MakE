package com.enigma.kitchenorders.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enigma.kitchenorders.ui.theme.*


@Composable
fun ModeToggle(
    isKitchenMode: Boolean,
    onModeChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                ambientColor = WarmShadowColor,
                spotColor = WarmShadowColor
            ),
        shape = CircleShape,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // kasir
            val kasirBgColor by animateColorAsState(
                targetValue = if (!isKitchenMode) Sambal else Color.Transparent,
                animationSpec = tween(300),
                label = "KasirBgColor"
            )
            val kasirTextColor by animateColorAsState(
                targetValue = if (!isKitchenMode) Color.White else TextSecondary,
                animationSpec = tween(300),
                label = "KasirTextColor"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .clip(CircleShape)
                    .background(kasirBgColor)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onModeChanged(false) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "KASIR",
                    fontFamily = PlusJakartaSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = kasirTextColor
                )
            }

            // dapur
            val dapurBgColor by animateColorAsState(
                targetValue = if (isKitchenMode) Kunyit else Color.Transparent,
                animationSpec = tween(300),
                label = "DapurBgColor"
            )
            val dapurTextColor by animateColorAsState(
                targetValue = if (isKitchenMode) Kopi else TextSecondary,
                animationSpec = tween(300),
                label = "DapurTextColor"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .clip(CircleShape)
                    .background(dapurBgColor)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onModeChanged(true) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "DAPUR",
                    fontFamily = PlusJakartaSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = dapurTextColor
                )
            }
        }
    }
}
