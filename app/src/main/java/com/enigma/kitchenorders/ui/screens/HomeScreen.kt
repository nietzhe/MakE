package com.enigma.kitchenorders.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enigma.kitchenorders.ui.components.MakkaeHero
import com.enigma.kitchenorders.ui.components.ModeToggle
import com.enigma.kitchenorders.ui.theme.*


@Composable
fun HomeScreen(
    businessName: String,
    onNavigateToKasir: () -> Unit,
    onNavigateToDapur: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Scaffold(
        containerColor = Tepung
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                // 1. Hero Header
                item {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        MakkaeHero(
                            pendingOrdersCount = 3,
                            isKitchenMode = false
                        )

                        // Settings Icon at Top Right
                        IconButton(
                            onClick = onNavigateToSettings,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 16.dp, end = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Pengaturan",
                                tint = KunyitPale
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(-26.dp))
                    ModeToggle(
                        isKitchenMode = false,
                        onModeChanged = { isKitchen ->
                            if (isKitchen) {
                                onNavigateToDapur()
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // action cards
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Aksi Kasir",
                            fontFamily = FrauncesFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            color = Kopi
                        )

                        // pesanan baru
                        Card(
                            onClick = onNavigateToKasir,
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(
                                    elevation = 6.dp,
                                    shape = RoundedCornerShape(28.dp),
                                    ambientColor = WarmShadowColor,
                                    spotColor = WarmShadowColor
                                ),
                            shape = RoundedCornerShape(28.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    modifier = Modifier.size(56.dp),
                                    shape = RoundedCornerShape(18.dp),
                                    color = SambalLight.copy(alpha = 0.15f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.PointOfSale,
                                            contentDescription = null,
                                            tint = Sambal,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Catat Pesanan Baru",
                                        fontFamily = FrauncesFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 18.sp,
                                        color = Kopi
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Pilih tanggal pengiriman & menu",
                                        fontFamily = PlusJakartaSansFontFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 13.sp,
                                        color = TextSecondary
                                    )
                                }

                                Surface(
                                    modifier = Modifier.size(40.dp),
                                    shape = CircleShape,
                                    color = Kunyit
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Tambah",
                                            tint = Kopi,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // rekap produksi
                        Card(
                            onClick = onNavigateToDapur,
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(
                                    elevation = 6.dp,
                                    shape = RoundedCornerShape(28.dp),
                                    ambientColor = WarmShadowColor,
                                    spotColor = WarmShadowColor
                                ),
                            shape = RoundedCornerShape(28.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    modifier = Modifier.size(56.dp),
                                    shape = RoundedCornerShape(18.dp),
                                    color = Kunyit.copy(alpha = 0.15f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Kitchen,
                                            contentDescription = null,
                                            tint = Kunyit,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Rekap Produksi",
                                        fontFamily = FrauncesFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 18.sp,
                                        color = Kopi
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Download Rekap Produksi",
                                        fontFamily = PlusJakartaSansFontFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 13.sp,
                                        color = TextSecondary
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
