package com.enigma.kitchenorders.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enigma.kitchenorders.R
import com.enigma.kitchenorders.data.local.model.OrderWithItems
import com.enigma.kitchenorders.ui.components.*
import com.enigma.kitchenorders.ui.theme.*
import com.enigma.kitchenorders.ui.viewmodel.KitchenViewModel
import com.enigma.kitchenorders.ui.viewmodel.OrderDialogKind
import com.enigma.kitchenorders.ui.viewmodel.PendingOrderAction

private val PageHorizontalPadding = 20.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DapurPage(
    viewModel: KitchenViewModel,
    bottomPadding: Dp,
    onEditOrder: (OrderWithItems) -> Unit
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val activeOrders by viewModel.activeOrders.collectAsState()
    val doneOrders by viewModel.doneOrders.collectAsState()
    val readyExpanded by viewModel.readySectionExpanded.collectAsState()
    val expandedIds by viewModel.expandedOrderIds.collectAsState()
    val actionMenuId by viewModel.actionMenuOrderId.collectAsState()
    val celebratingId by viewModel.celebratingOrderId.collectAsState()
    val motion = rememberMotionMillis(MotionDuration.Expand)

    @Composable
    fun OrderEntryCard(order: OrderWithItems, modifier: Modifier) {
        val id = order.order.id
        OrderCard(
            order = order,
            expanded = id in expandedIds,
            actionsOpen = actionMenuId == id,
            celebrating = celebratingId == id,
            onToggleExpanded = {
                if (actionMenuId != null) viewModel.dismissActions() else viewModel.toggleExpanded(id)
            },
            onOpenActions = { viewModel.openActions(id) },
            onDismissActions = viewModel::dismissActions,
            onDelete = { viewModel.requestDelete(order) },
            onEdit = { onEditOrder(order) },
            onDone = { viewModel.requestDone(order) },
            onReopen = { viewModel.requestReopen(order) },
            modifier = modifier
        )
    }

    DarkPageBackground {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(actionMenuId != null) {
                    if (actionMenuId != null) {
                        detectTapGestures(onTap = { viewModel.dismissActions() })
                    }
                },
            contentPadding = PaddingValues(top = 16.dp, bottom = bottomPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(key = "date_chips") {
                DateChips(
                    selectedDate = selectedDate,
                    onDateChange = viewModel::setDate,
                    modifier = Modifier.padding(horizontal = PageHorizontalPadding)
                )
            }

            if (activeOrders.isEmpty() && doneOrders.isEmpty()) {
                item(key = "empty") {
                    EmptyState(
                        title = stringResource(R.string.dapur_empty_title),
                        subtitle = stringResource(R.string.dapur_empty_subtitle)
                    )
                }
            } else {
                item(key = "active_header") {
                    SectionTitle(
                        text = stringResource(R.string.section_active, activeOrders.size),
                        modifier = Modifier.padding(horizontal = PageHorizontalPadding)
                    )
                }
                if (activeOrders.isEmpty()) {
                    item(key = "active_empty") {
                        Text(
                            text = stringResource(R.string.section_active_empty),
                            modifier = Modifier.padding(horizontal = PageHorizontalPadding),
                            fontFamily = PlusJakartaSansFontFamily,
                            fontSize = 14.sp,
                            color = TextSecondaryDark
                        )
                    }
                }
                items(activeOrders, key = { it.order.id }) { order ->
                    OrderEntryCard(
                        order,
                        Modifier
                            .padding(horizontal = PageHorizontalPadding)
                            .animateItemPlacement(tween(motion))
                    )
                }

                if (doneOrders.isNotEmpty()) {
                    item(key = "ready_header") {
                        ReadyHeader(
                            count = doneOrders.size,
                            expanded = readyExpanded,
                            onToggle = { viewModel.setReadySectionExpanded(!readyExpanded) },
                            modifier = Modifier.padding(horizontal = PageHorizontalPadding)
                        )
                    }
                    if (readyExpanded) {
                        items(doneOrders, key = { it.order.id }) { order ->
                            OrderEntryCard(
                                order,
                                Modifier
                                    .padding(horizontal = PageHorizontalPadding)
                                    .animateItemPlacement(tween(motion))
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier.padding(top = 4.dp),
        fontFamily = FrauncesFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        color = KunyitPale
    )
}

@Composable
private fun ReadyHeader(
    count: Int,
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val motion = rememberMotionMillis(MotionDuration.Expand)
    val angle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(motion),
        label = "ReadyChevron"
    )
    val state = stringResource(if (expanded) R.string.section_ready_expanded else R.string.section_ready_collapsed)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clickable(role = Role.Button, onClick = onToggle)
            .semantics { stateDescription = state },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.section_ready, count),
            modifier = Modifier.weight(1f),
            fontFamily = FrauncesFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = KunyitPale
        )
        Icon(
            imageVector = Icons.Default.ExpandMore,
            contentDescription = null,
            tint = KunyitPale,
            modifier = Modifier
                .size(28.dp)
                .rotate(angle)
        )
    }
}

@Composable
internal fun EmptyState(title: String, subtitle: String?, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SteamingPotIllustration()
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            fontFamily = FrauncesFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = TextSecondaryDark,
            textAlign = TextAlign.Center
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                fontFamily = PlusJakartaSansFontFamily,
                fontSize = 13.sp,
                color = TextSecondaryDark,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun OrderActionDialog(
    action: PendingOrderAction,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val message: String
    val cancelLabel: String
    val confirmLabel: String
    when (action.kind) {
        OrderDialogKind.DELETE -> {
            message = stringResource(R.string.dialog_delete_message, action.customerName)
            cancelLabel = stringResource(R.string.dialog_delete_cancel)
            confirmLabel = stringResource(R.string.dialog_delete_confirm)
        }

        OrderDialogKind.DONE -> {
            message = stringResource(R.string.dialog_done_message, action.customerName)
            cancelLabel = stringResource(R.string.dialog_done_cancel)
            confirmLabel = stringResource(R.string.dialog_done_confirm)
        }

        OrderDialogKind.REOPEN -> {
            message = stringResource(R.string.dialog_reopen_message, action.customerName)
            cancelLabel = stringResource(R.string.dialog_reopen_cancel)
            confirmLabel = stringResource(R.string.dialog_reopen_confirm)
        }
    }
    val isDelete = action.kind == OrderDialogKind.DELETE
    val cancelFocus = remember { FocusRequester() }
    if (isDelete) {
        LaunchedEffect(Unit) {
            withFrameNanos { }
            runCatching { cancelFocus.requestFocus() }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Tepung,
        text = {
            Text(
                text = message,
                fontFamily = PlusJakartaSansFontFamily,
                fontSize = 16.sp,
                color = Kopi
            )
        },
        dismissButton = {
            if (isDelete) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .focusRequester(cancelFocus),
                    colors = ButtonDefaults.buttonColors(containerColor = Kopi, contentColor = Color.White)
                ) {
                    Text(cancelLabel, fontWeight = FontWeight.Bold)
                }
            } else {
                TextButton(onClick = onDismiss, modifier = Modifier.heightIn(min = 48.dp)) {
                    Text(cancelLabel, fontWeight = FontWeight.Bold, color = TextSecondary)
                }
            }
        },
        confirmButton = {
            when (action.kind) {
                OrderDialogKind.DELETE -> TextButton(onClick = onConfirm, modifier = Modifier.heightIn(min = 48.dp)) {
                    Text(confirmLabel, fontWeight = FontWeight.Bold, color = Sambal)
                }

                OrderDialogKind.DONE -> Button(
                    onClick = onConfirm,
                    modifier = Modifier.heightIn(min = 48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Pandan, contentColor = Color.White)
                ) {
                    Text(confirmLabel, fontWeight = FontWeight.Bold)
                }

                OrderDialogKind.REOPEN -> Button(
                    onClick = onConfirm,
                    modifier = Modifier.heightIn(min = 48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Kunyit, contentColor = Kopi)
                ) {
                    Text(confirmLabel, fontWeight = FontWeight.Bold)
                }
            }
        }
    )
}
