package com.enigma.kitchenorders.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import com.enigma.kitchenorders.R
import com.enigma.kitchenorders.ui.theme.*

val HeroExpandedHeight = 190.dp
val HeroCollapsedHeight = 64.dp
private val HeroExpandedBarHeight = 56.dp

enum class HeroIllustration { Tumpeng, SteamingPot }


@Stable
class HeroCollapseState(private val expandedPx: Float, private val collapsedPx: Float) {
    var heightPx by mutableFloatStateOf(expandedPx)
        private set

    val fraction: Float
        get() = if (expandedPx == collapsedPx) 0f else ((expandedPx - heightPx) / (expandedPx - collapsedPx)).coerceIn(0f, 1f)

    fun expand() {
        heightPx = expandedPx
    }

    val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            if (available.y >= 0f) return Offset.Zero
            return Offset(0f, applyDelta(available.y))
        }

        override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
            if (available.y <= 0f) return Offset.Zero
            return Offset(0f, applyDelta(available.y))
        }
    }

    private fun applyDelta(delta: Float): Float {
        val new = (heightPx + delta).coerceIn(collapsedPx, expandedPx)
        val used = new - heightPx
        heightPx = new
        return used
    }
}

@Composable
fun rememberHeroCollapseState(): HeroCollapseState {
    val density = LocalDensity.current
    return remember(density) {
        with(density) { HeroCollapseState(HeroExpandedHeight.toPx(), HeroCollapsedHeight.toPx()) }
    }
}

@Composable
fun MakkaeHero(
    collapse: HeroCollapseState,
    pageName: String,
    label: String?,
    title: String,
    chip: String?,
    illustration: HeroIllustration,
    showShare: Boolean,
    onShare: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier,
    dotAlpha: Float = 1f
) {
    val density = LocalDensity.current
    val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val fraction = collapse.fraction
    val contentHeight: Dp = with(density) { collapse.heightPx.toDp() }
    val corner = lerp(44.dp, 24.dp, fraction)
    val barHeight = lerp(HeroExpandedBarHeight, HeroCollapsedHeight, fraction)
    val expandedAlpha = (1f - fraction * 1.6f).coerceIn(0f, 1f)
    val circleAlpha = (1f - fraction * 1.2f).coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(statusBarTop + contentHeight)
            .clip(RoundedCornerShape(bottomStart = corner, bottomEnd = corner))
            .background(Sambal)
            .background(
                Brush.linearGradient(
                    colors = listOf(SambalLight, SambalDark),
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 1000f)
                )
            )
    ) {
        DotPattern(baseAlpha = dotAlpha)
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Kunyit.copy(alpha = 0.9f * circleAlpha),
                radius = 72.dp.toPx(),
                center = Offset(size.width - 36.dp.toPx(), statusBarTop.toPx() + 96.dp.toPx())
            )
        }

        // expanded content: label, title, summary chip, illustration.
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = statusBarTop)
                .fillMaxWidth()
                .height(HeroExpandedHeight)
                .graphicsLayer { alpha = expandedAlpha }
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 14.dp)
            ) {
                when (illustration) {
                    HeroIllustration.Tumpeng -> TumpengIllustration()
                    HeroIllustration.SteamingPot -> SteamingPotIllustration()
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 24.dp, end = 128.dp, bottom = 16.dp)
            ) {
                if (!label.isNullOrBlank()) {
                    Text(
                        text = label,
                        fontFamily = PlusJakartaSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 0.06.sp,
                        color = KunyitPale,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }
                Text(
                    text = title,
                    fontFamily = FrauncesFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 26.sp,
                    lineHeight = 30.sp,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (!chip.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = chip,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            fontFamily = PlusJakartaSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = statusBarTop)
                .fillMaxWidth()
                .height(barHeight)
                .padding(start = 24.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "MAKKAE",
                fontFamily = FrauncesFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                letterSpacing = 0.1.sp,
                color = KunyitPale
            )
            Text(
                text = " · $pageName",
                modifier = Modifier
                    .weight(1f)
                    .graphicsLayer { alpha = fraction },
                fontFamily = PlusJakartaSansFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (showShare) {
                HeroIconButton(
                    icon = Icons.Default.Share,
                    description = stringResource(R.string.share_pdf),
                    onClick = onShare
                )
            }
            HeroIconButton(
                icon = Icons.Default.Settings,
                description = stringResource(R.string.settings),
                onClick = onSettings
            )
        }
    }
}

@Composable
private fun HeroIconButton(icon: ImageVector, description: String, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(48.dp)) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Kopi.copy(alpha = 0.35f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = description, tint = Color.White)
        }
    }
}
