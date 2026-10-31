package sabuj.m.myevents.feature.bookings

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
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

    // Show snackbar with undo action whenever a booking is deleted
    LaunchedEffect(uiState.snackbarMessage) {
        val msg = uiState.snackbarMessage ?: return@LaunchedEffect
        val result = snackbarHostState.showSnackbar(
            message     = msg,
            actionLabel = "UNDO", // TODO strings.xml
            duration    = SnackbarDuration.Long,
        )
        if (result == SnackbarResult.ActionPerformed) {
            viewModel.undoDelete()
        }
        viewModel.snackbarDismissed()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ScreenScaffold(onMenuClick = onMenuClick) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
            ) {
                // ── Top bar ─────────────────────────────────────────────
                BookingsTopBar(
                    isEditMode = uiState.mode == BookingsMode.Edit,
                    onToggleEdit = { viewModel.toggleEditMode() },
                )

                Spacer(Modifier.height(Spacing.sm))

                // ── Event filter chips ───────────────────────────────────
                if (uiState.events.isNotEmpty()) {
                    EventFilterChips(
                        events         = uiState.events,
                        selectedEventId = uiState.filterEventId,
                        onSelect       = { viewModel.filterByEvent(it) },
                    )
                    Spacer(Modifier.height(Spacing.md))
                }

                // ── Booking list or empty state ──────────────────────────
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
                                onTap         = {
                                    if (uiState.mode == BookingsMode.Edit) {
                                        viewModel.openEditSheet(booking)
                                    }
                                },
                                onDeleteClick = { viewModel.requestDelete(booking.id) },
                                event         = event,
                                modifier      = Modifier
                                    .fillMaxWidth()
                                    .combinedClickable(
                                        onClick     = {
                                            if (uiState.mode == BookingsMode.Normal) {
                                                viewModel.openEditSheet(booking)
                                            }
                                        },
                                        onLongClick = { viewModel.toggleEditMode() },
                                    ),
                            )
                        }
                    }
                }
            }
        }

        // ── FAB ─────────────────────────────────────────────────────────
        FloatingActionButton(
            onClick           = {
                if (uiState.mode == BookingsMode.Edit) viewModel.toggleEditMode()
                else viewModel.openAddSheet()
            },
            modifier          = Modifier
                .align(Alignment.BottomEnd)
                .padding(Spacing.lg),
            shape             = RoundedCornerShape(Radius.fab),
            containerColor    = AppColor.primary,
            contentColor      = AppColor.onPrimary,
        ) {
            Icon(
                imageVector        = if (uiState.mode == BookingsMode.Edit) Icons.Rounded.Check else Icons.Rounded.Add,
                contentDescription = if (uiState.mode == BookingsMode.Edit) "Done" else "Add booking",
            )
        }

        // ── Snackbar ────────────────────────────────────────────────────
        SnackbarHost(
            hostState = snackbarHostState,
            modifier  = Modifier.align(Alignment.BottomCenter).padding(bottom = 88.dp),
        )
    }

    // ── Sheets and dialogs ───────────────────────────────────────────────
    when (val sheet = uiState.sheetState) {
        is BookingSheetState.Add -> AddEditBookingSheet(
            sheetState    = sheet,
            events        = uiState.events,
            onDismiss     = { viewModel.hideSheet() },
            onTitleChange = { newTitle -> viewModel.updateAddSheetField { s -> s.copy(title = newTitle) } },
            onTypeChange  = { viewModel.updateAddSheetField { s -> s.copy(bookingType = it) } },
            onIconChange  = { viewModel.updateAddSheetField { s -> s.copy(bookingIcon = it) } },
            onEventChange = { viewModel.updateAddSheetField { s -> s.copy(eventId = it) } },
            onContactChange = { viewModel.updateAddSheetField { s -> s.copy(contactName = it) } },
            onPhoneChange = { viewModel.updateAddSheetField { s -> s.copy(phone = it) } },
            onTotalChange = { viewModel.updateAddSheetField { s -> s.copy(totalAmountPaise = it) } },
            onDueDateChange = { viewModel.updateAddSheetField { s -> s.copy(nextDueDateEpochDay = it) } },
            onSave        = { viewModel.saveBooking() },
            onAddPayment  = {},
            onDeletePayment = {},
        )
        is BookingSheetState.Edit -> AddEditBookingSheet(
            sheetState    = sheet,
            events        = uiState.events,
            onDismiss     = { viewModel.hideSheet() },
            onTitleChange = { viewModel.updateEditSheetField { s -> s.copy(title = it) } },
            onTypeChange  = { viewModel.updateEditSheetField { s -> s.copy(bookingType = it) } },
            onIconChange  = { viewModel.updateEditSheetField { s -> s.copy(bookingIcon = it) } },
            onEventChange = { viewModel.updateEditSheetField { s -> s.copy(eventId = it) } },
            onContactChange = { viewModel.updateEditSheetField { s -> s.copy(contactName = it) } },
            onPhoneChange = { viewModel.updateEditSheetField { s -> s.copy(phone = it) } },
            onTotalChange = { viewModel.updateEditSheetField { s -> s.copy(totalAmountPaise = it) } },
            onDueDateChange = { viewModel.updateEditSheetField { s -> s.copy(nextDueDateEpochDay = it) } },
            onSave        = { viewModel.saveBooking() },
            onAddPayment  = { viewModel.openPaymentDialog(sheet.bookingId) },
            onDeletePayment = { viewModel.deletePayment(it) },
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
        DeleteBookingDialog(
            bookingTitle = booking?.title ?: "",
            onDismiss    = { viewModel.dismissDeleteDialog() },
            onConfirm    = { viewModel.confirmDelete() },
        )
    }
}

// ────────────────────────────────────────────────────────────────────────────
// Top bar
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun BookingsTopBar(
    isEditMode: Boolean,
    onToggleEdit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier          = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Reserve space for the floating menu button drawn by ScreenScaffold
        Spacer(Modifier.width(56.dp))
        Text(
            text     = "Bookings", // TODO strings.xml
            style    = titleL,
            color    = AppColor.text,
            modifier = Modifier.weight(1f),
        )
        if (isEditMode) {
            Text(
                text     = "Done", // TODO strings.xml
                style    = labelL,
                color    = AppColor.primary,
                modifier = Modifier.padding(Spacing.sm),
            )
        }
    }
}

// ────────────────────────────────────────────────────────────────────────────
// Event filter chips
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun EventFilterChips(
    events: List<Event>,
    selectedEventId: String?,
    onSelect: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier            = modifier,
        contentPadding      = PaddingValues(horizontal = Spacing.lg),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        item {
            FilterChipItem(
                label      = "All", // TODO strings.xml
                isSelected = selectedEventId == null,
                onClick    = { onSelect(null) },
            )
        }
        itemsIndexed(events) { _, event ->
            FilterChipItem(
                label      = event.title,
                isSelected = selectedEventId == event.id,
                onClick    = { onSelect(event.id) },
            )
        }
    }
}

@Composable
private fun FilterChipItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick    = onClick,
        modifier   = modifier.height(36.dp),
        shape      = RoundedCornerShape(Radius.pill),
        color      = if (isSelected) AppColor.primaryContainer else AppColor.surfaceVariant,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier         = Modifier.padding(horizontal = Spacing.md),
        ) {
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
