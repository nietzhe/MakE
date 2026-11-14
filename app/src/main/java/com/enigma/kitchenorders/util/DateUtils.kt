package com.enigma.kitchenorders.util

import java.util.Calendar

object DateUtils {
    // [offsetDays] days from [now] (0 = today, 1 = tomorrow)
    fun startOfDay(offsetDays: Int = 0, now: Long = System.currentTimeMillis()): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = now
            add(Calendar.DAY_OF_YEAR, offsetDays)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }
}
