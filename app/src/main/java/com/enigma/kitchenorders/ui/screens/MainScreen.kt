package com.enigma.kitchenorders.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.enigma.kitchenorders.R
import com.enigma.kitchenorders.data.local.model.OrderWithItems
import com.enigma.kitchenorders.ui.components.BottomNavPill
import com.enigma.kitchenorders.ui.components.HeroIllustration
import com.enigma.kitchenorders.ui.components.MakkaeHero
import com.enigma.kitchenorders.ui.components.MotionDuration
import com.enigma.kitchenorders.ui.components.NavItemSpec
import com.enigma.kitchenorders.ui.components.rememberHeroCollapseState
import com.enigma.kitchenorders.ui.components.rememberMotionMillis
import com.enigma.kitchenorders.ui.theme.*
import com.enigma.kitchenorders.ui.viewmodel.KitchenEvent
import com.enigma.kitchenorders.ui.viewmodel.KitchenViewModel
import com.enigma.kitchenorders.ui.viewmodel.OrderEntryEvent
import com.enigma.kitchenorders.ui.viewmodel.OrderEntryViewModel
import com.enigma.kitchenorders.util.PdfExporter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private const val PAGE_KASIR = 0
private const val PAGE_DAPUR = 1
private const val PAGE_PRODUKSI = 2

private const val UNDO_WINDOW_MILLIS = 6_000L
private val NavPillBottomMargin = 12.dp

private val NavItems = listOf(
    NavItemSpec(R.string.nav_kasir, Icons.Default.PointOfSale),
    NavItemSpec(R.string.nav_dapur, Icons.Default.Kitchen),
    NavItemSpec(R.string.nav_produksi, Icons.Default.Inventory2)
)


@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun MainScreen(
    businessName: String,
    orderEntryViewModel: OrderEntryViewModel,
    kitchenViewModel: KitchenViewModel,
    pdfExporter: PdfExporter,
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val pagerState = rememberPagerState(pageCount = { NavItems.size })
    val snackbarHostState = remember { SnackbarHostState() }
    val heroState = rememberHeroCollapseState()
    val colorMotion = rememberMotionMillis(MotionDuration.Color)

    // kasir state
    val menus by orderEntryViewModel.activeMenus.collectAsState()
    val cart by orderEntryViewModel.cart.collectAsState()
    val customerName by orderEntryViewModel.customerName.collectAsState()
    val deliveryDate by orderEntryViewModel.deliveryDate.collectAsState()
    val editingOrder by orderEntryViewModel.editingOrder.collectAsState()

    // dapur - produksi state
    val selectedDate by kitchenViewModel.selectedDate.collectAsState()
    val activeOrders by kitchenViewModel.activeOrders.collectAsState()
    val doneOrders by kitchenViewModel.doneOrders.collectAsState()
    val totals by kitchenViewModel.productionTotals.collectAsState()
    val doneQuantity by kitchenViewModel.doneQuantity.collectAsState()
    val actionMenuOrderId by kitchenViewModel.actionMenuOrderId.collectAsState()
    val pendingAction by kitchenViewModel.pendingAction.collectAsState()

    var kasirBlocksSwipe by remember { mutableStateOf(false) }
    var pendingEdit by remember { mutableStateOf<OrderWithItems?>(null) }

    val swipeEnabled = !kasirBlocksSwipe && actionMenuOrderId == null && pendingAction == null && pendingEdit == null
    val page = pagerState.currentPage

    fun goToPage(target: Int) {
        focusManager.clearFocus()
        scope.launch { pagerState.animateScrollToPage(target) }
    }

    fun beginEdit(order: OrderWithItems) {
        orderEntryViewModel.startEditing(order)
        kitchenViewModel.dismissActions()
        goToPage(PAGE_KASIR)
    }

    BackHandler(enabled = page != PAGE_KASIR) { goToPage(PAGE_KASIR) }
    BackHandler(enabled = actionMenuOrderId != null) { kitchenViewModel.dismissActions() }

    LaunchedEffect(page) {
        heroState.expand()
        if (page != PAGE_DAPUR) kitchenViewModel.dismissActions()
    }

    LaunchedEffect(orderEntryViewModel) {
        orderEntryViewModel.events.collect { event ->
            when (event) {
                is OrderEntryEvent.Created -> Toast.makeText(
                    context,
                    context.getString(R.string.toast_order_saved, event.customerName),
                    Toast.LENGTH_SHORT
                ).show()

                is OrderEntryEvent.Updated -> {
                    Toast.makeText(
                        context,
                        context.getString(R.string.toast_order_updated, event.customerName),
                        Toast.LENGTH_SHORT
                    ).show()
                    kitchenViewModel.setDate(event.deliveryDate)
                    goToPage(PAGE_DAPUR)
                }

                OrderEntryEvent.EditRejected -> {
                    Toast.makeText(context, context.getString(R.string.toast_order_not_editable), Toast.LENGTH_LONG).show()
                    goToPage(PAGE_DAPUR)
                }
            }
        }
    }

    val deletedMessage = stringResource(R.string.snackbar_order_deleted)
    val undoLabel = stringResource(R.string.snackbar_undo)
    LaunchedEffect(kitchenViewModel) {
        kitchenViewModel.events.collect { event ->
            when (event) {
                is KitchenEvent.OrderDeleted -> {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    launch {
                        val result = withTimeoutOrNull(UNDO_WINDOW_MILLIS) {
                            snackbarHostState.showSnackbar(
                                message = deletedMessage,
                                actionLabel = undoLabel,
                                duration = SnackbarDuration.Indefinite
                            )
                        }
                        if (result == SnackbarResult.ActionPerformed) kitchenViewModel.undoDelete(event.orderId)
                    }
                }
            }
        }
    }

    val dateText = remember(selectedDate) {
        SimpleDateFormat("EEEE, dd MMMM", Locale("id", "ID")).format(Date(selectedDate))
    }
    val greeting = stringResource(
        when (remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }) {
            in 0..10 -> R.string.greeting_morning
            in 11..14 -> R.string.greeting_noon
            in 15..17 -> R.string.greeting_afternoon
            else -> R.string.greeting_night
        }
    )
    val heroLabel: String?
    val heroTitle: String
    val heroChip: String?
    val heroIllustration: HeroIllustration
    when (page) {
        PAGE_DAPUR -> {
            heroLabel = stringResource(R.string.hero_dapur_label)
            heroTitle = dateText
            heroChip = stringResource(R.string.hero_active_orders, activeOrders.size)
            heroIllustration = HeroIllustration.SteamingPot
        }

        PAGE_PRODUKSI -> {
            heroLabel = dateText
            heroTitle = stringResource(R.string.produksi_title)
            heroChip = stringResource(R.string.produksi_summary, totals.size, totals.sumOf { it.totalQuantity })
            heroIllustration = HeroIllustration.SteamingPot
        }

        else -> {
            heroLabel = null
            heroTitle = greeting
            heroChip = null
            heroIllustration = HeroIllustration.Tumpeng
        }
    }

    // btm nav block
    val imeVisible = WindowInsets.isImeVisible
    val navBarInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    var pillHeightPx by remember { mutableIntStateOf(0) }
    val pillHeight: Dp = with(density) { pillHeightPx.toDp() }
    val navBlock: Dp = if (imeVisible) 0.dp else pillHeight + NavPillBottomMargin + navBarInset
    val listBottomPadding = navBlock + 24.dp

    val pillColor by animateColorAsState(
        targetValue = if (page == PAGE_KASIR) Kopi else KopiCardStart,
        animationSpec = tween(colorMotion),
        label = "NavPillColor"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Tepung)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .nestedScroll(heroState.nestedScrollConnection)
        ) {
            MakkaeHero(
                collapse = heroState,
                pageName = stringResource(NavItems[page].label),
                label = heroLabel,
                title = heroTitle,
                chip = heroChip,
                illustration = heroIllustration,
                showShare = page != PAGE_KASIR,
                onShare = {
                    when (page) {
                        PAGE_DAPUR -> pdfExporter.exportOrders(businessName, selectedDate, activeOrders, doneOrders)
                        PAGE_PRODUKSI -> pdfExporter.exportProduction(businessName, selectedDate, totals, doneQuantity)
                    }
                },
                onSettings = onOpenSettings,
                dotAlpha = if (page == PAGE_KASIR) 1f else 0.6f
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                userScrollEnabled = swipeEnabled,
                beyondBoundsPageCount = 1
            ) { pageIndex ->
                when (pageIndex) {
                    PAGE_KASIR -> OrderEntryScreen(
                        menus = menus,
                        cart = cart,
                        customerName = customerName,
                        deliveryDate = deliveryDate,
                        editingName = editingOrder?.customerName,
                        bottomInset = navBlock,
                        onCustomerNameChange = orderEntryViewModel::setCustomerName,
                        onDeliveryDateChange = orderEntryViewModel::setDeliveryDate,
                        onAddItem = orderEntryViewModel::addItemToCart,
                        onRemoveItem = orderEntryViewModel::removeCartItem,
                        onSubmitOrder = orderEntryViewModel::submitOrder,
                        onCancelEdit = {
                            orderEntryViewModel.cancelEditing()
                            goToPage(PAGE_DAPUR)
                        },
                        onBlockSwipeChange = { kasirBlocksSwipe = it }
                    )

                    PAGE_DAPUR -> DapurPage(
                        viewModel = kitchenViewModel,
                        bottomPadding = listBottomPadding,
                        onEditOrder = { order ->
                            val formHasContent = customerName.isNotBlank() || cart.isNotEmpty()
                            val editingSame = editingOrder?.id == order.order.id
                        
                            if (formHasContent && !editingSame) {
                                pendingEdit = order
                            } else {
                                beginEdit(order)
                            }
                        }
                    )

                    else -> ProduksiPage(
                        viewModel = kitchenViewModel,
                        bottomPadding = listBottomPadding
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = navBlock),
            snackbar = { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Tepung,
                    contentColor = Kopi,
                    actionColor = Sambal
                )
            }
        )

        AnimatedVisibility(
            visible = !imeVisible,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = fadeIn(tween(colorMotion)) + slideInVertically(tween(colorMotion)) { it },
            exit = fadeOut(tween(colorMotion)) + slideOutVertically(tween(colorMotion)) { it }
        ) {
            val navDescription = stringResource(R.string.nav_bar_description)
            BottomNavPill(
                items = NavItems,
                selectedIndex = page,
                onSelect = ::goToPage,
                containerColor = pillColor,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(bottom = NavPillBottomMargin)
                    .onSizeChanged { pillHeightPx = it.height }
                    .semantics { contentDescription = navDescription }
            )
        }
    }

    pendingAction?.let { action ->
        OrderActionDialog(
            action = action,
            onConfirm = kitchenViewModel::confirmPendingAction,
            onDismiss = kitchenViewModel::dismissPendingAction
        )
    }

    pendingEdit?.let { order ->
        AlertDialog(
            onDismissRequest = { pendingEdit = null },
            containerColor = Tepung,
            text = {
                Text(
                    text = stringResource(R.string.dialog_replace_form_message, order.order.customerName),
                    color = Kopi
                )
            },
            dismissButton = {
                TextButton(onClick = { pendingEdit = null }, modifier = Modifier.heightIn(min = 48.dp)) {
                    Text(stringResource(R.string.dialog_replace_form_cancel), fontWeight = FontWeight.Bold, color = TextSecondary)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        pendingEdit = null
                        beginEdit(order)
                    },
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Text(stringResource(R.string.dialog_replace_form_confirm), fontWeight = FontWeight.Bold, color = Sambal)
                }
            }
        )
    }
}
