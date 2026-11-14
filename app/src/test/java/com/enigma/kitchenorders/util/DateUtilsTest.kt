package com.enigma.kitchenorders.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class DateUtilsTest {

    private fun millis(year: Int, month: Int, day: Int, hour: Int = 0, minute: Int = 0): Long =
        Calendar.getInstance().apply {
            clear()
            set(year, month, day, hour, minute, 30)
            set(Calendar.MILLISECOND, 123)
        }.timeInMillis

    @Test
    fun startOfDay_truncates_to_local_midnight() {
        val now = millis(2026, Calendar.NOVEMBER, 13, 15, 42)

        assertEquals(millis(2026, Calendar.NOVEMBER, 13).let { it - 30_123 }, DateUtils.startOfDay(now = now))
    }

    @Test
    fun startOfDay_with_offset_returns_the_next_days_midnight_across_month_end() {
        val now = millis(2026, Calendar.NOVEMBER, 30, 23, 59)

        assertEquals(millis(2026, Calendar.DECEMBER, 1).let { it - 30_123 }, DateUtils.startOfDay(1, now))
    }
}
