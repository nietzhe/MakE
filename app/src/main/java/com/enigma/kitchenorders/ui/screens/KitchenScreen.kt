package com.enigma.kitchenorders.ui.screens

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enigma.kitchenorders.ui.components.KitchenStatus
import com.enigma.kitchenorders.ui.components.MakkaeHero
import com.enigma.kitchenorders.ui.components.ModeToggle
import com.enigma.kitchenorders.ui.components.ProductionCard
import com.enigma.kitchenorders.ui.theme.*
import com.enigma.kitchenorders.ui.viewmodel.MenuAggregation
import java.text.SimpleDateFormat
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KitchenScreen(
    businessName: String,
    selectedDate: Long,
    filterKeyword: String,
    menuAggregations: List<MenuAggregation>,
    onDateChange: (Long) -> Unit,
    onFilterChange: (String) -> Unit,
    onExportPdf: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val dateFormat = remember { SimpleDateFormat("EEEE,\ndd MMMM", Locale("id", "ID")) }
    val shortDateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")) }
    var showFilterField by remember { mutableStateOf(false) }

    val expandedMenus = remember { mutableStateMapOf<String, Boolean>() }
    val totalOrdersCount = menuAggregations.sumOf { it.details.size }
    val totalMenusCount = menuAggregations.size

    Scaffold(
        containerColor = Kopi,
        bottomBar = {
            // share to wangsaf
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                color = Color.Transparent
            ) {
                Button(
                    onClick = onExportPdf,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .shadow(12.dp, RoundedCornerShape(22.dp), spotColor = Color.Black.copy(alpha = 0.4f))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Tepung, Kelapa),
                                start = Offset(0f, 0f),
                                end = Offset(400f, 400f)
                            ),
                            shape = RoundedCornerShape(22.dp)
                        ),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Sambal
                    ),
                    contentPadding = PaddingValues(horizontal = 20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Sambal,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Bagikan PDF ke WhatsApp",
                        fontFamily = PlusJakartaSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Sambal
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Sambal.copy(alpha = 0.65f), Color.Transparent),
                        center = Offset(width, 0f),
                        radius = width * 0.9f
                    ),
                    radius = width * 0.9f,
                    center = Offset(width, 0f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Kunyit.copy(alpha = 0.25f), Color.Transparent),
                        center = Offset(0f, height),
                        radius = width * 0.8f
                    ),
                    radius = width * 0.8f,
                    center = Offset(0f, height)
                )
            }

            // 2. Main Content
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 110.dp)
            ) {
                // Kitchen Hero Header
                item {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        MakkaeHero(
                            pendingOrdersCount = totalOrdersCount,
                            isKitchenMode = true,
                            dateText = dateFormat.format(Date(selectedDate))
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(-26.dp))
                    ModeToggle(
                        isKitchenMode = true,
                        onModeChanged = { isKitchen ->
                            if (!isKitchen) {
                                onNavigateBack()
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // date picker & filter row
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // date selector btn
                            Surface(
                                onClick = {
                                    val cal = Calendar.getInstance().apply { timeInMillis = selectedDate }
                                    DatePickerDialog(
                                        context,
                                        { _, year, month, day ->
                                            val newCal = Calendar.getInstance().apply {
                                                set(year, month, day, 0, 0, 0)
                                                set(Calendar.MILLISECOND, 0)
                                            }
                                            onDateChange(newCal.timeInMillis)
                                        },
                                        cal.get(Calendar.YEAR),
                                        cal.get(Calendar.MONTH),
                                        cal.get(Calendar.DAY_OF_MONTH)
                                    ).show()
                                },
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White.copy(alpha = 0.12f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = "Pilih tanggal",
                                        tint = KunyitPale,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = shortDateFormat.format(Date(selectedDate)),
                                        fontFamily = PlusJakartaSansFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color.White
                                    )
                                }
                            }

                            // filter toggle btn
                            IconButton(
                                onClick = { showFilterField = !showFilterField },
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
                                    .size(40.dp)
                            ) {
                                Icon(
                                    imageVector = if (filterKeyword.isNotBlank()) Icons.Default.FilterAlt else Icons.Default.FilterAltOff,
                                    contentDescription = "Filter",
                                    tint = KunyitPale
                                )
                            }
                        }

                        // filter text field
                        AnimatedVisibility(visible = showFilterField) {
                            OutlinedTextField(
                                value = filterKeyword,
                                onValueChange = onFilterChange,
                                label = { Text("Filter catatan", color = TextSecondaryDark) },
                                placeholder = { Text("cth: pedas, tanpa nasi", color = TextSecondaryDark) },
                                leadingIcon = { Icon(Icons.Default.Search, null, tint = KunyitPale) },
                                trailingIcon = {
                                    if (filterKeyword.isNotBlank()) {
                                        IconButton(onClick = { onFilterChange("") }) {
                                            Icon(Icons.Default.Clear, "Hapus", tint = KunyitPale)
                                        }
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Kunyit,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color.White.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "$totalOrdersCount pesanan",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    fontFamily = PlusJakartaSansFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp,
                                    color = KunyitPale
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color.White.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "$totalMenusCount menu",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    fontFamily = PlusJakartaSansFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp,
                                    color = KunyitPale
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // menu list (production)
                if (menuAggregations.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Inbox,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = TextSecondaryDark
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Belum ada pesanan",
                                    fontFamily = FrauncesFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp,
                                    color = TextSecondaryDark
                                )
                                Text(
                                    text = "untuk tanggal ini",
                                    fontFamily = PlusJakartaSansFontFamily,
                                    fontSize = 13.sp,
                                    color = TextSecondaryDark
                                )
                            }
                        }
                    }
                } else {
                    items(menuAggregations) { aggregation ->
                        val isExpanded = expandedMenus[aggregation.menuName] ?: false
                        val status = when {
                            aggregation.totalQuantity >= 20 -> KitchenStatus.DONE
                            aggregation.totalQuantity >= 10 -> KitchenStatus.COOKING
                            else -> KitchenStatus.NOT_STARTED
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 6.dp)
                        ) {
                            ProductionCard(
                                menuName = aggregation.menuName,
                                ordersCount = aggregation.details.size,
                                totalQuantity = aggregation.totalQuantity,
                                status = status,
                                onClick = {
                                    expandedMenus[aggregation.menuName] = !isExpanded
                                }
                            )

                            AnimatedVisibility(visible = isExpanded) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp),
                                    shape = RoundedCornerShape(bottomStart = 18.dp, bottomEnd = 18.dp),
                                    color = Color(0xFF35231A)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        aggregation.details.forEach { detail ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = "${detail.customerName} (${detail.quantity}x)",
                                                        fontFamily = PlusJakartaSansFontFamily,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 14.sp,
                                                        color = Color.White
                                                    )
                                                    if (!detail.notes.isNullOrBlank()) {
                                                        Text(
                                                            text = detail.notes,
                                                            fontFamily = PlusJakartaSansFontFamily,
                                                            fontSize = 12.sp,
                                                            color = Salmon
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
