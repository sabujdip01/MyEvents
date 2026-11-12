package sabuj.m.myevents.feature.bookings

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import sabuj.m.myevents.core.ui.AppSnackbar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import sabuj.m.myevents.core.icons.EventIconDisplay
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.labelL
import sabuj.m.myevents.core.theme.labelM
import sabuj.m.myevents.core.theme.titleL
import sabuj.m.myevents.core.ui.ScreenScaffold
import sabuj.m.myevents.domain.model.Booking
import sabuj.m.myevents.domain.model.Event

/**
 * Bookings list screen.
 *
 * Layout:
 * - Top bar: "Bookings" title, long-press any card for edit mode
 * - Filter chip row: "All" + one chip per event
 * - List of [BookingCard]s, or empty state
 * - FAB: Add (normal) / Done (edit mode)
 * - Dialogs and sheets for add/edit/delete/payment
 *
 * @param onMenuClick Opens the navigation drawer.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookingsScreen(
    onMenuClick: () -> Unit,
    viewModel: BookingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Automatically clear snackbar if user navigates away
    androidx.compose.runtime.DisposableEffect(Unit) {
        onDispose {
            viewModel.snackbarDismissed()
        }
    }

    // Show snackbar with undo action whenever a booking is deleted
    LaunchedEffect(uiState.snackbarMessage) {
        val msg = uiState.snackbarMessage ?: return@LaunchedEffect
        try {
            kotlinx.coroutines.withTimeoutOrNull(5000L) {
                val result = snackbarHostState.showSnackbar(
                    message     = msg,
                    actionLabel = "UNDO",
                    duration    = SnackbarDuration.Indefinite,
                )
                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.undoDelete()
                }
            }
        } finally {
            viewModel.snackbarDismissed()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ScreenScaffold(onMenuClick = onMenuClick) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // ── Top bar ─────────────────────────────────────────
                BookingsTopBar(
                    isEditMode   = uiState.mode == BookingsMode.Edit,
                    onToggleEdit = { viewModel.toggleEditMode() },
                    onMenuClick  = onMenuClick,
                )

                Spacer(Modifier.height(Spacing.sm))

                // ── Event filter chips ────────────────────────────────────────
                if (uiState.events.isNotEmpty()) {
                    EventFilterChips(
                        events          = uiState.events,
                        bookings        = uiState.bookings,
                        selectedEventId = uiState.filterEventId,
                        onSelect        = { viewModel.filterByEvent(it) },
                    )
                    Spacer(Modifier.height(Spacing.md))
                }

                // â”€â”€ Booking list or empty state â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
                val filtered = uiState.filteredBookings
                if (filtered.isEmpty()) {
                    Box(
                        modifier         = Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text  = "No bookings yet", // TODO strings.xml
                                style = titleL,
                                color = AppColor.text2,
                            )
                            Spacer(Modifier.height(Spacing.sm))
                            Text(
                                text  = "Tap + to add your first booking", // TODO strings.xml
                                style = labelM,
                                color = AppColor.text3,
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier      = Modifier.weight(1f),
                        contentPadding = PaddingValues(bottom = 96.dp), // FAB clearance
                        verticalArrangement = Arrangement.spacedBy(Spacing.md),
                    ) {
                        itemsIndexed(
                            items = filtered,
                            key   = { _, booking -> booking.id },
                        ) { index, booking ->
                            val event = uiState.events.find { it.id == booking.eventId }
                            BookingCard(
                                booking       = booking,
                                slot          = index % 6,
                                editMode      = uiState.mode == BookingsMode.Edit,
                                onEditClick   = { viewModel.openEditSheet(booking) },
                                onDeleteClick = { viewModel.requestDelete(booking.id) },
                                event         = event,
                                modifier      = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            }
        }

        // ── FAB ──
        sabuj.m.myevents.core.ui.AppFab(
            onClick = { viewModel.openAddSheet() },
            icon    = Icons.Rounded.Add,
            contentDescription = "Add booking",
            enabled = uiState.mode != BookingsMode.Edit,
            modifier = Modifier.align(Alignment.BottomEnd),
        )

        // â”€â”€ Snackbar â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        AppSnackbar(
            hostState = snackbarHostState,
            modifier  = Modifier.align(Alignment.Center),
        )
    }

    // â”€â”€ Sheets and dialogs â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    when (val sheet = uiState.sheetState) {
        is BookingSheetState.Add -> AddEditBookingSheet(
            sheetState    = sheet,
            events        = uiState.events,
            onDismiss     = { viewModel.hideSheet() },
            onTypeChange  = { viewModel.updateAddSheetField { s -> s.copy(bookingType = it) } },
            onIconChange  = { viewModel.updateAddSheetField { s -> s.copy(bookingIcon = it) } },
            onEventChange = { viewModel.updateAddSheetField { s -> s.copy(eventId = it) } },
            onContactChange = { viewModel.updateAddSheetField { s -> s.copy(contactName = it) } },
            onPhoneChange = { viewModel.updateAddSheetField { s -> s.copy(phone = it) } },
            onTotalChange = { viewModel.updateAddSheetField { s -> s.copy(totalAmountPaise = it) } },
            onBookingAmountChange = { amount ->
                viewModel.updateAddSheetField { s ->
                    s.copy(
                        bookingAmountPaise = amount,
                        bookingMade        = if (amount > 0L) true else s.bookingMade,
                    )
                }
            },
            onBookingMadeChange = { viewModel.updateAddSheetField { s -> s.copy(bookingMade = it) } },
            onDueDateChange = { viewModel.updateAddSheetField { s -> s.copy(nextDueDateEpochDay = it) } },
            onSave        = { viewModel.saveBooking() },
            onAddPayment  = {},
            onDeletePayment = {},
        )
        is BookingSheetState.Edit -> AddEditBookingSheet(
            sheetState    = sheet,
            events        = uiState.events,
            onDismiss     = { viewModel.hideSheet() },
            onTypeChange  = { viewModel.updateEditSheetField { s -> s.copy(bookingType = it) } },
            onIconChange  = { viewModel.updateEditSheetField { s -> s.copy(bookingIcon = it) } },
            onEventChange = { viewModel.updateEditSheetField { s -> s.copy(eventId = it) } },
            onContactChange = { viewModel.updateEditSheetField { s -> s.copy(contactName = it) } },
            onPhoneChange = { viewModel.updateEditSheetField { s -> s.copy(phone = it) } },
            onTotalChange = { viewModel.updateEditSheetField { s -> s.copy(totalAmountPaise = it) } },
            onBookingMadeChange = { viewModel.updateEditSheetField { s -> s.copy(bookingMade = it) } },
            onDueDateChange = { viewModel.updateEditSheetField { s -> s.copy(nextDueDateEpochDay = it) } },
            onSave        = { viewModel.saveBooking() },
            onAddPayment  = { viewModel.openPaymentDialog(sheet.bookingId) },
            onDeletePayment = { viewModel.deletePaymentLocal(it) },
        )
        else -> Unit
    }

    // Payment dialog
    when (val dialog = uiState.paymentDialogState) {
        is PaymentDialogState.Add -> PaymentDialogFull(
            state         = dialog,
            onDismiss     = { viewModel.hidePaymentDialog() },
            onDateChange  = { viewModel.updatePaymentField { s -> s.copy(dateEpochDay = it) } },
            onAmountChange = { viewModel.updatePaymentField { s -> s.copy(amountPaise = it) } },
            onNoteChange  = { viewModel.updatePaymentField { s -> s.copy(note = it) } },
            onSave        = { viewModel.savePayment() },
        )
        else -> Unit
    }

    // Delete confirmation dialog
    val deleteId = uiState.deleteDialogBookingId
    if (deleteId != null) {
        val booking = uiState.bookings.find { it.id == deleteId }
        sabuj.m.myevents.core.ui.AppDeleteDialog(
            title     = "Delete booking?",
            message   = "\"${booking?.title ?: ""}\" and all its payments will be permanently removed.",
            onDismiss = { viewModel.dismissDeleteDialog() },
            onConfirm = { viewModel.confirmDelete() },
        )
    }
}

// â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
// Top bar
// â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

@Composable
private fun BookingsTopBar(
    isEditMode: Boolean,
    onToggleEdit: () -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    sabuj.m.myevents.core.ui.AppTopBar(
        titleText   = "Bookings",
        onMenuClick = onMenuClick,
        modifier    = modifier,
        actions     = {
            Text(
                text     = if (isEditMode) "Done" else "Delete",
                style    = labelL,
                color    = AppColor.primary,
                modifier = Modifier.clickable { onToggleEdit() }.padding(Spacing.sm),
            )
        },
    )
}

// â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
// Event filter chips
// â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

@Composable
internal fun EventFilterChips(
    events: List<Event>,
    bookings: List<Booking>,
    selectedEventId: String?,
    onSelect: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val totalAll = bookings.sumOf { it.totalAmountPaise }
    LazyRow(
        modifier            = modifier,
        contentPadding      = PaddingValues(horizontal = Spacing.lg),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        item {
            FilterChipItem(
                label      = "All (${sabuj.m.myevents.core.util.MoneyFormatter.format(totalAll)})",
                isSelected = selectedEventId == null,
                onClick    = { onSelect(null) },
            )
        }
        itemsIndexed(events) { _, event ->
            val totalForEvent = bookings.filter { it.eventId == event.id }.sumOf { it.totalAmountPaise }
            FilterChipItem(
                label      = "${event.title} (${sabuj.m.myevents.core.util.MoneyFormatter.format(totalForEvent)})",
                isSelected = selectedEventId == event.id,
                onClick    = { onSelect(event.id) },
                event      = event,
            )
        }
    }
}

@Composable
private fun FilterChipItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    event: Event? = null,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick    = onClick,
        modifier   = modifier.height(36.dp),
        shape      = RoundedCornerShape(Radius.pill),
        color      = if (isSelected) AppColor.primaryContainer else AppColor.surfaceVariant,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier         = Modifier.padding(horizontal = Spacing.md),
        ) {
            if (event != null) {
                EventIconDisplay(
                    icon               = event.icon,
                    contentDescription = event.title,
                    modifier           = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(Spacing.xs))
            }
            Text(
                text     = label,
                style    = labelM,
                color    = if (isSelected) AppColor.onPrimaryContainer else AppColor.text2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
