package com.enigma.kitchenorders.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enigma.kitchenorders.data.local.entity.MenuEntity
import com.enigma.kitchenorders.data.local.entity.OrderItemEntity
import com.enigma.kitchenorders.ui.components.FloatingTotalBar
import com.enigma.kitchenorders.ui.components.MenuIconBox
import com.enigma.kitchenorders.ui.components.StepperPill
import com.enigma.kitchenorders.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderEntryScreen(
    menus: List<MenuEntity>,
    cart: List<OrderItemEntity>,
    customerName: String,
    deliveryDate: Long,
    onCustomerNameChange: (String) -> Unit,
    onDeliveryDateChange: (Long) -> Unit,
    onAddItem: (MenuEntity, Int, String?) -> Unit,
    onRemoveItem: (Int) -> Unit,
    onSubmitOrder: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val dateFormat = remember { SimpleDateFormat("EEE, dd MMM", Locale("id", "ID")) }
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }

    var showAddItemDialog by remember { mutableStateOf(false) }
    var selectedMenu by remember { mutableStateOf<MenuEntity?>(null) }
    var itemQuantity by remember { mutableStateOf("1") }
    var itemNotes by remember { mutableStateOf("") }
    var menuDropdownExpanded by remember { mutableStateOf(false) }

    val totalPrice = cart.sumOf { it.priceSnapshot * it.quantity }
    val totalPriceFormatted = currencyFormat.format(totalPrice)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Catat Pesanan",
                        fontFamily = FrauncesFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Kembali", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Sambal,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Tepung,
        bottomBar = {
            FloatingTotalBar(
                totalPriceFormatted = totalPriceFormatted,
                onSaveOrder = onSubmitOrder,
                enabled = customerName.isNotBlank() && cart.isNotEmpty()
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // "pesanan baru" -- main card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(28.dp),
                            ambientColor = WarmShadowColor,
                            spotColor = WarmShadowColor
                        ),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Line)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Pesanan baru",
                                fontFamily = FrauncesFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.sp,
                                color = Kopi
                            )

                            Surface(
                                onClick = {
                                    val cal = Calendar.getInstance().apply { timeInMillis = deliveryDate }
                                    DatePickerDialog(
                                        context,
                                        { _, year, month, day ->
                                            val newCal = Calendar.getInstance().apply {
                                                set(year, month, day, 0, 0, 0)
                                                set(Calendar.MILLISECOND, 0)
                                            }
                                            onDeliveryDateChange(newCal.timeInMillis)
                                        },
                                        cal.get(Calendar.YEAR),
                                        cal.get(Calendar.MONTH),
                                        cal.get(Calendar.DAY_OF_MONTH)
                                    ).show()
                                },
                                shape = RoundedCornerShape(20.dp),
                                color = Color(0xFFE3EFD6)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        tint = Color(0xFF2F5A33),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Kirim ${dateFormat.format(Date(deliveryDate))}",
                                        fontFamily = PlusJakartaSansFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF2F5A33)
                                    )
                                }
                            }
                        }

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            color = Color(0xFFFFF8EA)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    modifier = Modifier.size(40.dp),
                                    shape = CircleShape,
                                    color = Sambal
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = if (customerName.isNotBlank()) customerName.take(1).uppercase() else "P",
                                            fontFamily = FrauncesFontFamily,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 18.sp,
                                            color = Color.White
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = customerName,
                                        onValueChange = onCustomerNameChange,
                                        placeholder = { Text("Nama Pelanggan") },
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Sambal,
                                            unfocusedBorderColor = Color.Transparent
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Text(
                                        text = "Pelanggan langganan",
                                        fontFamily = PlusJakartaSansFontFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }

                        // "Add Item" btn
                        OutlinedButton(
                            onClick = { showAddItemDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Kunyit),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Kopi)
                        ) {
                            Icon(Icons.Default.Add, null, tint = Kunyit)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (menus.isEmpty()) "Belum ada menu" else "Tambah Item",
                                fontFamily = PlusJakartaSansFontFamily,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        HorizontalDivider(color = Line)

                        // item list header
                        Text(
                            text = "Daftar Pesanan",
                            fontFamily = PlusJakartaSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextSecondary
                        )

                        if (cart.isEmpty()) {
                            Text(
                                text = "Belum ada item ditambahkan",
                                fontFamily = PlusJakartaSansFontFamily,
                                fontSize = 13.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        }
                    }
                }
            }

            // carts
            items(cart.indices.toList()) { index ->
                val item = cart[index]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MenuIconBox(menuName = item.menuNameSnapshot)

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.menuNameSnapshot,
                                fontFamily = PlusJakartaSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Kopi
                            )
                            Text(
                                text = currencyFormat.format(item.priceSnapshot),
                                fontFamily = PlusJakartaSansFontFamily,
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                            if (!item.notes.isNullOrBlank()) {
                                Text(
                                    text = "Catatan: ${item.notes}",
                                    fontFamily = PlusJakartaSansFontFamily,
                                    fontSize = 11.sp,
                                    color = Sambal
                                )
                            }
                        }

                        StepperPill(
                            quantity = item.quantity,
                            onDecrease = {
                                if (item.quantity > 1) {
                                    onRemoveItem(index)
                                } else {
                                    onRemoveItem(index)
                                }
                            },
                            onIncrease = {
                                val menu = menus.find { it.name == item.menuNameSnapshot }
                                if (menu != null) {
                                    onAddItem(menu, 1, item.notes)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddItemDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddItemDialog = false
                selectedMenu = null
                itemQuantity = "1"
                itemNotes = ""
            },
            title = {
                Text(
                    text = "Tambah Item",
                    fontFamily = FrauncesFontFamily,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ExposedDropdownMenuBox(
                        expanded = menuDropdownExpanded,
                        onExpandedChange = { menuDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedMenu?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Pilih Menu") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(menuDropdownExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = menuDropdownExpanded,
                            onDismissRequest = { menuDropdownExpanded = false }
                        ) {
                            menus.forEach { menu ->
                                DropdownMenuItem(
                                    text = {
                                        Text("${menu.name} - ${currencyFormat.format(menu.currentPrice)}")
                                    },
                                    onClick = {
                                        selectedMenu = menu
                                        menuDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = itemQuantity,
                        onValueChange = { itemQuantity = it.filter { c -> c.isDigit() } },
                        label = { Text("Jumlah") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = itemNotes,
                        onValueChange = { itemNotes = it },
                        label = { Text("Catatan (opsional)") },
                        placeholder = { Text("cth: tanpa nasi, pedas extra") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedMenu?.let { menu ->
                            val qty = itemQuantity.toIntOrNull() ?: 1
                            onAddItem(menu, qty, itemNotes.ifBlank { null })
                        }
                        showAddItemDialog = false
                        selectedMenu = null
                        itemQuantity = "1"
                        itemNotes = ""
                    },
                    enabled = selectedMenu != null && (itemQuantity.toIntOrNull() ?: 0) > 0
                ) {
                    Text("TAMBAH", fontWeight = FontWeight.Bold, color = Sambal)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddItemDialog = false
                    selectedMenu = null
                    itemQuantity = "1"
                    itemNotes = ""
                }) {
                    Text("BATAL", color = TextSecondary)
                }
            }
        )
    }
}
