package com.enigma.kitchenorders.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enigma.kitchenorders.R
import com.enigma.kitchenorders.ui.theme.*
import com.enigma.kitchenorders.util.DateUtils
import java.text.SimpleDateFormat
import java.util.*


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DateChips(
    selectedDate: Long,
    onDateChange: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val shortDateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")) }
    val today = DateUtils.startOfDay()
    val tomorrow = DateUtils.startOfDay(1)

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Chip(
            text = shortDateFormat.format(Date(selectedDate)),
            selected = false,
            leadingIcon = true,
            description = stringResource(R.string.chip_pick_date),
            onClick = {
                val cal = Calendar.getInstance().apply { timeInMillis = selectedDate }
                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        val picked = Calendar.getInstance().apply {
                            set(year, month, day, 0, 0, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        onDateChange(picked.timeInMillis)
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        )
        Chip(
            text = stringResource(R.string.chip_today),
            selected = selectedDate == today,
            onClick = { onDateChange(today) }
        )
        Chip(
            text = stringResource(R.string.chip_tomorrow),
            selected = selectedDate == tomorrow,
            onClick = { onDateChange(tomorrow) }
        )
    }
}

@Composable
private fun Chip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: Boolean = false,
    description: String? = null
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = 48.dp)
            .semantics { this.selected = selected },
        shape = RoundedCornerShape(16.dp),
        color = if (selected) Kunyit else Color.White.copy(alpha = 0.12f)
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = 48.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = description,
                    tint = KunyitPale,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontFamily = PlusJakartaSansFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = if (selected) Kopi else Color.White
            )
        }
    }
}
