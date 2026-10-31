package sabuj.m.myevents.feature.events

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.labelL
import sabuj.m.myevents.core.theme.labelM
import sabuj.m.myevents.core.theme.titleL
import sabuj.m.myevents.core.ui.ScreenScaffold
import sabuj.m.myevents.core.util.DateUtils
import java.time.LocalDate
import java.time.YearMonth

/** Number of months rendered in the pager (200 before + 200 after = plenty). */
private const val PAGE_COUNT = 400
/** Centre page index maps to today's month. */
private const val INITIAL_PAGE = PAGE_COUNT / 2

/**
 * Events calendar screen.
 * Shows a vertically snapping monthly calendar; tap an event date for details,
 * long-press any date to add or edit.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun EventsScreen(
    onMenuClick: () -> Unit,
    viewModel: EventsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Consume one-shot effects
    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is EventsEffect.ShowUndoSnackbar -> {
                    scope.launch {
                        // Start hard-delete timer — 10 seconds
                        launch {
                            delay(10_000L)
                            viewModel.onUndoExpired(effect.eventId)
                        }
                        val result = snackbarHostState.showSnackbar(
                            message       = effect.message,
                            actionLabel   = "UNDO",
                            duration      = SnackbarDuration.Long,
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            viewModel.onEvent(EventsUiEvent.UndoDelete)
                        }
                    }
                }
                is EventsEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    ScreenScaffold(onMenuClick = onMenuClick) {
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AppColor.primary)
            }
            return@ScreenScaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // ── Top bar ──────────────────────────────────────────────
            EventsTopBar(
                currentMonth = uiState.currentMonth,
            )

            Spacer(Modifier.height(Spacing.md))

            // ── Calendar pager ───────────────────────────────────────
            CalendarPager(
                initialMonth    = uiState.currentMonth,
                eventsByDate    = uiState.eventsByDate,
                onMonthChanged  = { viewModel.onEvent(EventsUiEvent.MonthChanged(it)) },
                onDateTap       = { viewModel.onEvent(EventsUiEvent.DateTapped(it)) },
                onDateLongPress = { viewModel.onEvent(EventsUiEvent.DateLongPressed(it)) },
                modifier        = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
        }
    }
    // ── Bottom sheets ────────────────────────────────────────────────
    when (val mode = uiState.sheetMode) {
        SheetMode.None -> Unit
        is SheetMode.Details -> EventDetailsSheet(
            event     = mode.event,
            onDismiss = { viewModel.onEvent(EventsUiEvent.SheetDismissed) },
            onEdit    = { viewModel.onEvent(EventsUiEvent.EditRequested(mode.event)) },
            onDelete  = { viewModel.onEvent(EventsUiEvent.DeleteRequested(mode.event)) },
        )
        is SheetMode.Add -> AddEditEventSheet(
            prefilledDate     = mode.date,
            existingEvent     = null,
            onDismiss         = { viewModel.onEvent(EventsUiEvent.SheetDismissed) },
            onSave            = { title, name, date, icon, budget, inv ->
                viewModel.onEvent(
                    EventsUiEvent.SaveNew(title, name, date, icon, budget, inv)
                )
            },
        )
        is SheetMode.Edit -> AddEditEventSheet(
            prefilledDate     = mode.event.date,
            existingEvent     = mode.event,
            onDismiss         = { viewModel.onEvent(EventsUiEvent.SheetDismissed) },
            onSave            = { title, name, date, icon, budget, inv ->
                viewModel.onEvent(
                    EventsUiEvent.SaveEdit(title, name, date, icon, budget, inv)
                )
            },
        )
        is SheetMode.ConfirmDelete -> DeleteEventDialog(
            event     = mode.event,
            onDismiss = { viewModel.onEvent(EventsUiEvent.SheetDismissed) },
            onConfirm = { viewModel.onEvent(EventsUiEvent.DeleteConfirmed) },
        )
    }
}

// ────────────────────────────────────────────────────────────────────
// Top bar
// ────────────────────────────────────────────────────────────────────

@Composable
private fun EventsTopBar(
    currentMonth: YearMonth,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier          = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Spacer reserves room for the floating menu button drawn by ScreenScaffold
        Spacer(Modifier.width(56.dp))

        Text(
            text     = "Events",
            style    = titleL,
            color    = AppColor.text,
            modifier = Modifier.weight(1f),
        )

        // Month selector pill
        MonthSelectorPill(currentMonth = currentMonth)
    }
}

@Composable
private fun MonthSelectorPill(
    currentMonth: YearMonth,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier          = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(AppColor.primaryContainer)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text  = DateUtils.formatMonth(currentMonth),
            style = labelL,
            color = AppColor.onPrimaryContainer,
        )
        Icon(
            imageVector        = Icons.Rounded.KeyboardArrowDown,
            contentDescription = null,
            tint               = AppColor.onPrimaryContainer,
            modifier           = Modifier.size(20.dp),
        )
    }
}

// ────────────────────────────────────────────────────────────────────
// Calendar pager
// ────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarPager(
    initialMonth: YearMonth,
    eventsByDate: Map<Long, sabuj.m.myevents.domain.model.Event>,
    onMonthChanged: (YearMonth) -> Unit,
    onDateTap: (LocalDate) -> Unit,
    onDateLongPress: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val today = remember { LocalDate.now() }
    val pagerState = rememberPagerState(initialPage = INITIAL_PAGE) { PAGE_COUNT }
    val baseMonth  = remember { YearMonth.now() }

    // Report the current month whenever the page changes
    LaunchedEffect(pagerState.currentPage) {
        val month = baseMonth.plusMonths((pagerState.currentPage - INITIAL_PAGE).toLong())
        onMonthChanged(month)
    }

    VerticalPager(
        state    = pagerState,
        modifier = modifier,
    ) { page ->
        val month = baseMonth.plusMonths((page - INITIAL_PAGE).toLong())
        MonthGrid(
            month           = month,
            today           = today,
            eventsByDate    = eventsByDate,
            onDateTap       = onDateTap,
            onDateLongPress = onDateLongPress,
            modifier        = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg),
        )
    }
}

// ────────────────────────────────────────────────────────────────────
// Month grid
// ────────────────────────────────────────────────────────────────────

private val WEEKDAY_LABELS = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

@Composable
internal fun MonthGrid(
    month: YearMonth,
    today: LocalDate,
    eventsByDate: Map<Long, sabuj.m.myevents.domain.model.Event>,
    onDateTap: (LocalDate) -> Unit,
    onDateLongPress: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val cells = remember(month) { DateUtils.calendarGrid(month) }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(AppColor.surface)
            .padding(12.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            // Weekday header row
            Row(Modifier.fillMaxWidth()) {
                WEEKDAY_LABELS.forEach { label ->
                    Text(
                        text      = label,
                        style     = labelM,
                        color     = AppColor.text2,
                        textAlign = TextAlign.Center,
                        modifier  = Modifier.weight(1f).height(32.dp)
                            .padding(top = 8.dp),
                    )
                }
            }

            // Date rows (up to 6 rows of 7)
            cells.chunked(7).forEach { row ->
                Row(
                    modifier             = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    row.forEach { date ->
                        DateCell(
                            date            = date,
                            today           = today,
                            month           = month,
                            eventsByDate    = eventsByDate,
                            onTap           = onDateTap,
                            onLongPress     = onDateLongPress,
                            modifier        = Modifier.weight(1f),
                        )
                    }
                    // Pad trailing columns if row is short
                    repeat(7 - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}

@Composable
private fun DateCell(
    date: LocalDate?,
    today: LocalDate,
    month: YearMonth,
    eventsByDate: Map<Long, sabuj.m.myevents.domain.model.Event>,
    onTap: (LocalDate) -> Unit,
    onLongPress: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isInMonth = date != null && YearMonth.from(date) == month
    val event     = date?.let { eventsByDate[it.toEpochDay()] }
    val isToday   = date == today

    val bgColor = when {
        date == null || !isInMonth -> AppColor.surfaceVariant.copy(alpha = 0.25f)
        event != null              -> AppColor.surface
        else                       -> AppColor.surfaceVariant.copy(alpha = 0.55f)
    }
    val dayColor = when {
        !isInMonth -> AppColor.text3
        else       -> AppColor.text
    }

    val cellModifier = modifier
        .height(52.dp)
        .clip(RoundedCornerShape(14.dp))
        .background(bgColor)
        .then(
            if (date != null && isInMonth)
                Modifier.combinedClickable(
                    onClick      = { onTap(date) },
                    onLongClick  = { onLongPress(date) },
                )
            else Modifier
        )

    Box(
        modifier        = cellModifier,
        contentAlignment = Alignment.TopCenter,
    ) {
        if (date != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier            = Modifier
                    .fillMaxSize()
                    .padding(top = 6.dp),
            ) {
                Text(
                    text  = date.dayOfMonth.toString(),
                    style = labelL,
                    color = dayColor,
                )
                if (event != null) {
                    Spacer(Modifier.height(2.dp))
                    Icon(
                        imageVector        = event.icon.fallback,
                        contentDescription = event.title,
                        tint               = AppColor.primary,
                        modifier           = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(AppColor.primary)
                            .padding(bottom = 6.dp),
                    )
                }
            }
            // Today outline
            if (isToday) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(14.dp))
                        .background(AppColor.primary.copy(alpha = 0.08f)),
                )
            }
        }
    }
}

// ────────────────────────────────────────────────────────────────────
// Sheets (Details, Add/Edit, Delete)  — delegated to sub-files
// ────────────────────────────────────────────────────────────────────
// See EventDetailsSheet.kt, AddEditEventSheet.kt, DeleteEventDialog.kt
