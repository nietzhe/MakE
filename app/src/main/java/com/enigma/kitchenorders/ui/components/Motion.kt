package com.enigma.kitchenorders.ui.components

import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext


object MotionDuration {
    const val Expand = 200
    const val Color = 200
    const val Flag = 600
}

@Composable
fun rememberAnimationsEnabled(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) != 0f
    }
}

@Composable
fun rememberMotionMillis(baseMillis: Int): Int = if (rememberAnimationsEnabled()) baseMillis else 0
