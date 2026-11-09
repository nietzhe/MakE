package com.enigma.kitchenorders.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enigma.kitchenorders.ui.theme.*


@Composable
fun StepperPill(
    quantity: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = KelapaPale
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                onClick = onDecrease,
                modifier = Modifier
                    .size(40.dp)
                    .shadow(2.dp, CircleShape),
                shape = CircleShape,
                color = Color.White
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Kurangi jumlah",
                        tint = Sambal,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            AnimatedContent(
                targetState = quantity,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInVertically { height -> height } + fadeIn()).togetherWith(
                            slideOutVertically { height -> -height } + fadeOut()
                        )
                    } else {
                        (slideInVertically { height -> -height } + fadeIn()).togetherWith(
                            slideOutVertically { height -> height } + fadeOut()
                        )
                    }
                },
                label = "QuantityAnimation"
            ) { targetCount ->
                Text(
                    text = "$targetCount",
                    fontFamily = PlusJakartaSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Kopi,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            Surface(
                onClick = onIncrease,
                modifier = Modifier
                    .size(40.dp)
                    .shadow(2.dp, CircleShape),
                shape = CircleShape,
                color = Kunyit
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tambah jumlah",
                        tint = Kopi,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
