package com.enigma.kitchenorders.ui.components

import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enigma.kitchenorders.ui.theme.*

data class NavItemSpec(@StringRes val label: Int, val icon: ImageVector)

@Composable
fun BottomNavPill(
    items: List<NavItemSpec>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    val motion = rememberMotionMillis(MotionDuration.Color)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(12.dp, CircleShape, ambientColor = WarmShadowColor, spotColor = WarmShadowColor),
        shape = CircleShape,
        color = containerColor
    ) {
        Row(
            modifier = Modifier
                .padding(6.dp)
                .selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items.forEachIndexed { index, item ->
                val selected = index == selectedIndex
                val background by animateColorAsState(
                    targetValue = if (selected) Kunyit else Color.Transparent,
                    animationSpec = tween(motion),
                    label = "NavItemBackground"
                )
                val foreground by animateColorAsState(
                    targetValue = if (selected) Kopi else TextSecondaryDark,
                    animationSpec = tween(motion),
                    label = "NavItemForeground"
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 56.dp)
                        .clip(CircleShape)
                        .background(background)
                        .selectable(
                            selected = selected,
                            role = Role.Tab,
                            onClick = { onSelect(index) }
                        )
                        .padding(vertical = 6.dp, horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = foreground,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = stringResource(item.label),
                        fontFamily = PlusJakartaSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = foreground,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
