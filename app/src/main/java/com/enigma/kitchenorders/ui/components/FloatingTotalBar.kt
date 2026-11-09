package com.enigma.kitchenorders.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enigma.kitchenorders.ui.theme.*


@Composable
fun FloatingTotalBar(
    totalPriceFormatted: String,
    onSaveOrder: () -> Unit,
    enabled: Boolean = true,
    buttonText: String = "Simpan pesanan",
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(12.dp, RoundedCornerShape(30.dp), ambientColor = WarmShadowColor),
        shape = RoundedCornerShape(30.dp),
        color = Kopi
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Text(
                    text = "Total",
                    fontFamily = PlusJakartaSansFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    color = TextSecondaryDark
                )
                Text(
                    text = totalPriceFormatted,
                    fontFamily = FrauncesFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    color = KunyitPale
                )
            }

            Button(
                onClick = onSaveOrder,
                enabled = enabled,
                modifier = Modifier
                    .height(52.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = if (enabled) listOf(Kunyit, KunyitLight) else listOf(Color.Gray, Color.LightGray),
                            start = Offset(0f, 0f),
                            end = Offset(200f, 200f)
                        ),
                        shape = RoundedCornerShape(22.dp)
                    ),
                shape = RoundedCornerShape(22.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Kopi,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = Color.DarkGray
                ),
                contentPadding = PaddingValues(horizontal = 20.dp)
            ) {
                Text(
                    text = buttonText,
                    fontFamily = PlusJakartaSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (enabled) Kopi else Color.DarkGray
                )
            }
        }
    }
}
