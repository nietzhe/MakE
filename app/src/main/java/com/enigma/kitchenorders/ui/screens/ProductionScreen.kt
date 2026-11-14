package com.enigma.kitchenorders.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enigma.kitchenorders.R
import com.enigma.kitchenorders.data.local.model.ProductionTotal
import com.enigma.kitchenorders.ui.components.DarkPageBackground
import com.enigma.kitchenorders.ui.components.DateChips
import com.enigma.kitchenorders.ui.theme.*
import com.enigma.kitchenorders.ui.viewmodel.KitchenViewModel
import java.util.Locale


@Composable
fun ProduksiPage(
    viewModel: KitchenViewModel,
    bottomPadding: Dp
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val totals by viewModel.productionTotals.collectAsState()
    val doneQuantity by viewModel.doneQuantity.collectAsState()

    DarkPageBackground {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 16.dp, bottom = bottomPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(key = "date_chips") {
                DateChips(
                    selectedDate = selectedDate,
                    onDateChange = viewModel::setDate,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            if (totals.isEmpty()) {
                item(key = "empty") {
                    EmptyState(
                        title = stringResource(R.string.produksi_empty),
                        subtitle = null
                    )
                }
            } else {
                items(totals, key = { it.menuName }) { total ->
                    ProductionRow(
                        total = total,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }

            item(key = "done_line") {
                Text(
                    text = stringResource(R.string.produksi_done_line, doneQuantity),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    fontFamily = PlusJakartaSansFontFamily,
                    fontSize = 13.sp,
                    color = TextSecondaryDark
                )
            }
        }
    }
}

@Composable
private fun ProductionRow(total: ProductionTotal, modifier: Modifier = Modifier) {
    val description = stringResource(R.string.produksi_row_description, total.menuName, total.totalQuantity)
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) { contentDescription = description },
        shape = RoundedCornerShape(24.dp),
        color = Color.Transparent,
        border = BorderStroke(1.dp, KunyitPale.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(KopiCardStart.copy(alpha = 0.85f))
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = total.menuName.uppercase(Locale("id", "ID")),
                modifier = Modifier.weight(1f),
                fontFamily = PlusJakartaSansFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "${total.totalQuantity}",
                fontFamily = FrauncesFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 40.sp,
                color = KunyitLight
            )
        }
    }
}
