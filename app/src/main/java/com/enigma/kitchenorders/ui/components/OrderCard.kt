package com.enigma.kitchenorders.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enigma.kitchenorders.R
import com.enigma.kitchenorders.data.local.entity.OrderStatus
import com.enigma.kitchenorders.data.local.model.OrderWithItems
import com.enigma.kitchenorders.ui.theme.*

private val CardShape = RoundedCornerShape(24.dp)


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OrderCard(
    order: OrderWithItems,
    expanded: Boolean,
    actionsOpen: Boolean,
    celebrating: Boolean,
    onToggleExpanded: () -> Unit,
    onOpenActions: () -> Unit,
    onDismissActions: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onDone: () -> Unit,
    onReopen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDone = order.order.status == OrderStatus.DONE
    val name = order.order.customerName
    val haptics = LocalHapticFeedback.current
    val motion = rememberMotionMillis(MotionDuration.Expand)

    val titleColor = if (isDone) DoneText else Color.White
    val subColor = if (isDone) DoneTextSecondary else TextSecondaryDark
    val backgroundBrush = Brush.linearGradient(
        colors = if (isDone) listOf(DoneCardStart, DoneCardEnd) else listOf(KopiCardStart, KopiCardEnd),
        start = Offset(0f, 0f),
        end = Offset(500f, 500f)
    )

    val expandLabel = stringResource(if (expanded) R.string.order_collapse_label else R.string.order_expand_label)
    val longClickLabel = stringResource(R.string.order_long_click_label)
    val expandedText = stringResource(if (expanded) R.string.order_expanded else R.string.order_collapsed)
    val readyText = stringResource(R.string.order_state_ready)
    val stateText = if (isDone) "$expandedText, $readyText" else expandedText

    val bodyInteraction = Modifier
        .combinedClickable(
            onClickLabel = expandLabel,
            onClick = { if (actionsOpen) onDismissActions() else onToggleExpanded() },
            onLongClickLabel = longClickLabel,
            onLongClick = {
                if (!celebrating) {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    onOpenActions()
                }
            }
        )
        .semantics { stateDescription = stateText }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(backgroundBrush)
            .border(1.dp, KunyitPale.copy(alpha = 0.15f), CardShape)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .then(bodyInteraction)
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusIndicator(isDone = isDone, celebrating = celebrating)
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        fontFamily = FrauncesFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = titleColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    if (isDone) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = subColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.order_ready_subline),
                                fontFamily = PlusJakartaSansFontFamily,
                                fontSize = 13.sp,
                                color = subColor
                            )
                        }
                    } else {
                        Text(
                            text = stringResource(R.string.order_item_count, order.items.size),
                            fontFamily = PlusJakartaSansFontFamily,
                            fontSize = 13.sp,
                            color = subColor
                        )
                    }
                }
                val chevronAngle by animateFloatAsState(
                    targetValue = if (expanded) 180f else 0f,
                    animationSpec = tween(motion),
                    label = "OrderChevron"
                )
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = subColor,
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(chevronAngle)
                )
            }

            IconButton(
                onClick = if (actionsOpen) onDismissActions else onOpenActions,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.order_more_actions, name),
                    tint = titleColor
                )
            }
        }

        // items -- container as the header.
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(tween(motion)) + fadeIn(tween(motion)),
            exit = shrinkVertically(tween(motion)) + fadeOut(tween(motion))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(bodyInteraction)
                    .padding(start = 16.dp, end = 16.dp, bottom = 14.dp)
            ) {
                HorizontalDivider(color = KunyitPale.copy(alpha = 0.15f))
                Spacer(modifier = Modifier.height(10.dp))
                order.items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.menuNameSnapshot,
                                fontFamily = PlusJakartaSansFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = titleColor
                            )
                            if (!item.notes.isNullOrBlank()) {
                                Text(
                                    text = item.notes,
                                    fontFamily = PlusJakartaSansFontFamily,
                                    fontSize = 12.sp,
                                    color = if (isDone) subColor else Salmon
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "× ${item.quantity}",
                            fontFamily = FrauncesFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = if (isDone) subColor else KunyitLight
                        )
                    }
                }
            }
        }

        // action row / "selesai" flag
        AnimatedVisibility(
            visible = actionsOpen || celebrating,
            enter = expandVertically(tween(motion)) + fadeIn(tween(motion)),
            exit = shrinkVertically(tween(motion)) + fadeOut(tween(motion))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, bottom = 8.dp)
            ) {
                if (celebrating) {
                    DoneFlag()
                } else {
                    if (isDone) {
                        ActionButton(
                            label = stringResource(R.string.action_reopen),
                            icon = Icons.Default.Restore,
                            onClick = onReopen,
                            contentColor = KunyitPale,
                            borderColor = Kunyit,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ActionButton(
                                label = stringResource(R.string.action_delete),
                                icon = Icons.Default.DeleteOutline,
                                onClick = onDelete,
                                contentColor = Salmon,
                                borderColor = Sambal,
                                modifier = Modifier.weight(1f)
                            )
                            ActionButton(
                                label = stringResource(R.string.action_edit),
                                icon = Icons.Default.Edit,
                                onClick = onEdit,
                                contentColor = Color.White,
                                borderColor = KunyitPale.copy(alpha = 0.5f),
                                modifier = Modifier.weight(1f)
                            )
                            ActionButton(
                                label = stringResource(R.string.action_done),
                                icon = Icons.Default.Check,
                                onClick = onDone,
                                contentColor = Color.White,
                                containerColor = Pandan,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = onDismissActions,
                            modifier = Modifier.heightIn(min = 48.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.action_cancel_small),
                                fontFamily = PlusJakartaSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = KunyitPale
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusIndicator(isDone: Boolean, celebrating: Boolean) {
    val (color, icon, description) = when {
        isDone -> Triple(DoneText, Icons.Default.Check, stringResource(R.string.order_status_done))
        else -> Triple(KunyitLight, Icons.Default.Schedule, stringResource(R.string.order_status_active))
    }
    Surface(
        modifier = Modifier.size(48.dp),
        shape = CircleShape,
        color = if (celebrating) Pandan else Color.White.copy(alpha = 0.08f),
        border = BorderStroke(2.dp, if (celebrating) PandanPale else color)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = if (celebrating) Icons.Default.Check else icon,
                contentDescription = description,
                tint = if (celebrating) Color.White else color,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Composable
private fun DoneFlag() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .semantics { liveRegion = LiveRegionMode.Polite },
        shape = RoundedCornerShape(16.dp),
        color = Pandan
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.order_flag_done),
                fontFamily = PlusJakartaSansFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}

@Composable
private fun ActionButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    contentColor: Color,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Transparent,
    borderColor: Color? = null
) {
    Surface(
        onClick = onClick,
        modifier = modifier.heightIn(min = 56.dp),
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        contentColor = contentColor,
        border = borderColor?.let { BorderStroke(2.dp, it) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(22.dp))
            Text(
                text = label,
                fontFamily = PlusJakartaSansFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}
