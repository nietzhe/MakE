package com.enigma.kitchenorders.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enigma.kitchenorders.data.local.entity.MenuEntity
import com.enigma.kitchenorders.ui.theme.*
import java.text.NumberFormat
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    businessName: String,
    menus: List<MenuEntity>,
    onBusinessNameChange: (String) -> Unit,
    onSaveMenu: (MenuEntity) -> Unit,
    onDeleteMenu: (MenuEntity) -> Unit,
    onNavigateBack: () -> Unit
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }

    var editingBusinessName by remember { mutableStateOf(businessName) }
    var showAddMenuDialog by remember { mutableStateOf(false) }
    var editingMenu by remember { mutableStateOf<MenuEntity?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var menuToDelete by remember { mutableStateOf<MenuEntity?>(null) }

    var menuName by remember { mutableStateOf("") }
    var menuPrice by remember { mutableStateOf("") }
    var menuActive by remember { mutableStateOf(true) }

    LaunchedEffect(businessName) {
        editingBusinessName = businessName
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Pengaturan",
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
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    editingMenu = null
                    menuName = ""
                    menuPrice = ""
                    menuActive = true
                    showAddMenuDialog = true
                },
                icon = { Icon(Icons.Default.Add, null, tint = Kopi) },
                text = {
                    Text(
                        text = "Tambah Menu",
                        fontFamily = PlusJakartaSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Kopi
                    )
                },
                containerColor = Kunyit,
                contentColor = Kopi,
                shape = RoundedCornerShape(22.dp)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Informasi Bisnis",
                    fontFamily = PlusJakartaSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Sambal
                )
            }

            item {
                OutlinedTextField(
                    value = editingBusinessName,
                    onValueChange = { editingBusinessName = it },
                    label = { Text("Nama Bisnis") },
                    leadingIcon = { Icon(Icons.Default.Store, null, tint = Sambal) },
                    trailingIcon = {
                        if (editingBusinessName != businessName) {
                            IconButton(onClick = { onBusinessNameChange(editingBusinessName) }) {
                                Icon(Icons.Default.Check, "Simpan", tint = Sambal)
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Kopi,
                        unfocusedTextColor = Kopi,
                        focusedBorderColor = Sambal,
                        unfocusedBorderColor = Line
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item { HorizontalDivider(color = Line) }

            // "Menu List" section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daftar Menu",
                        fontFamily = PlusJakartaSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Sambal
                    )
                    Text(
                        text = "${menus.size} menu",
                        fontFamily = PlusJakartaSansFontFamily,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            if (menus.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Restaurant,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Belum ada menu",
                                fontFamily = PlusJakartaSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = Kopi
                            )
                            Text(
                                text = "Tap tombol + untuk menambah",
                                fontFamily = PlusJakartaSansFontFamily,
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            items(menus) { menu ->
                Card(
                    onClick = {
                        editingMenu = menu
                        menuName = menu.name
                        menuPrice = menu.currentPrice.toLong().toString()
                        menuActive = menu.isActive
                        showAddMenuDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = menu.name,
                                fontFamily = PlusJakartaSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Kopi
                            )
                            Text(
                                text = currencyFormat.format(menu.currentPrice),
                                fontFamily = PlusJakartaSansFontFamily,
                                fontSize = 14.sp,
                                color = Sambal
                            )
                        }
                        if (!menu.isActive) {
                            AssistChip(
                                onClick = {},
                                label = { Text("Nonaktif") },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = Color(0xFFFFD9CF),
                                    labelColor = Sambal
                                )
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = TextSecondary
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    if (showAddMenuDialog) {
        AlertDialog(
            onDismissRequest = { showAddMenuDialog = false },
            title = {
                Text(
                    text = if (editingMenu == null) "Tambah Menu" else "Edit Menu",
                    fontFamily = FrauncesFontFamily,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = menuName,
                        onValueChange = { menuName = it },
                        label = { Text("Nama Menu") },
                        placeholder = { Text("cth: Nasi Urap Ayam Bakar") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = menuPrice,
                        onValueChange = { menuPrice = it.filter { c -> c.isDigit() } },
                        label = { Text("Harga (Rp)") },
                        placeholder = { Text("cth: 15000") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Menu aktif", fontFamily = PlusJakartaSansFontFamily)
                        Switch(
                            checked = menuActive,
                            onCheckedChange = { menuActive = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Sambal
                            )
                        )
                    }

                    if (editingMenu != null) {
                        HorizontalDivider(color = Line)
                        TextButton(
                            onClick = {
                                menuToDelete = editingMenu
                                showDeleteConfirmation = true
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Hapus Menu Ini", fontFamily = PlusJakartaSansFontFamily, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val price = menuPrice.toDoubleOrNull() ?: 0.0
                        val menu = MenuEntity(
                            id = editingMenu?.id ?: 0,
                            name = menuName.trim(),
                            currentPrice = price,
                            isActive = menuActive
                        )
                        onSaveMenu(menu)
                        showAddMenuDialog = false
                    },
                    enabled = menuName.isNotBlank() && menuPrice.isNotBlank()
                ) {
                    Text("SIMPAN", fontFamily = PlusJakartaSansFontFamily, fontWeight = FontWeight.Bold, color = Sambal)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMenuDialog = false }) {
                    Text("BATAL", fontFamily = PlusJakartaSansFontFamily, color = TextSecondary)
                }
            }
        )
    }
    
    if (showDeleteConfirmation && menuToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteConfirmation = false
                menuToDelete = null
            },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text("Hapus Menu?", fontFamily = FrauncesFontFamily, fontWeight = FontWeight.SemiBold)
            },
            text = {
                Text(
                    "Menu \"${menuToDelete?.name}\" akan dihapus secara permanen. Tindakan ini tidak dapat dibatalkan.",
                    fontFamily = PlusJakartaSansFontFamily
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        menuToDelete?.let { onDeleteMenu(it) }
                        showDeleteConfirmation = false
                        showAddMenuDialog = false
                        menuToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("HAPUS", fontFamily = PlusJakartaSansFontFamily, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        menuToDelete = null
                    }
                ) {
                    Text("BATAL", fontFamily = PlusJakartaSansFontFamily, color = TextSecondary)
                }
            }
        )
    }
}
