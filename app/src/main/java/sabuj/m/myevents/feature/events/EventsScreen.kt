package sabuj.m.myevents.feature.events

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.labelL
import sabuj.m.myevents.core.theme.labelM
import sabuj.m.myevents.core.theme.titleS
import sabuj.m.myevents.core.ui.AppTopBar
import sabuj.m.myevents.core.ui.ScreenScaffold
import sabuj.m.myevents.core.util.DateUtils
import sabuj.m.myevents.domain.model.Event
import java.time.LocalDate
import java.time.YearMonth

private const val PAGE_COUNT = 400
private const val INITIAL_PAGE = PAGE_COUNT / 2

/**
 * Events calendar screen — Items D1–D6.
 * Shows a monthly calendar with 20% increased cell heights, 50% reduced margins,
 * stronger current day tint, and a monthly events list below.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun EventsScreen(
    onMenuClick: () -> Unit,
    viewModel: EventsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    ScreenScaffold(onMenuClick = onMenuClick) {
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AppColor.primary)
            }
            return@ScreenScaffold
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar
            EventsTopBar(
                currentMonth = uiState.currentMonth,
                onMenuClick  = onMenuClick,
            )

            Spacer(Modifier.height(Spacing.xs))

            // Calendar pager
            CalendarPager(
                initialMonth    = uiState.currentMonth,
                eventsByDate    = uiState.eventsByDate,
                onMonthChanged  = { viewModel.onEvent(EventsUiEvent.MonthChanged(it)) },
                onDateTap       = { viewModel.onEvent(EventsUiEvent.DateTapped(it)) },
                onDateLongPress = { viewModel.onEvent(EventsUiEvent.DateLongPressed(it)) },
                modifier        = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(Spacing.sm))

            // Item D1: Monthly Events List under the calendar
            val currentMonthEvents = remember(uiState.currentMonth, uiState.eventsByDate) {
                uiState.eventsByDate.values
                    .filter { YearMonth.from(it.date) == uiState.currentMonth }
                    .sortedBy { it.date }
            }

            if (currentMonthEvents.isNotEmpty()) {
                Text(
                    text     = "Events in ${DateUtils.formatMonth(uiState.currentMonth)}",
                    style    = titleS,
                    color    = AppColor.text2,
                    modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.xs),
                )
                LazyColumn(
                    modifier       = Modifier.weight(1f),
                    contentPadding = PaddingValues(start = Spacing.lg, end = Spacing.lg, bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                ) {
                    itemsIndexed(currentMonthEvents, key = { _, ev -> ev.id }) { _, event ->
                        MonthlyEventRow(
                            event   = event,
                            onClick = { viewModel.onEvent(EventsUiEvent.DateTapped(event.date)) },
                        )
                    }
                }
            } else {
                Spacer(Modifier.weight(1f))
            }
        }
    }

    // Bottom sheets & Dialogs
    when (val mode = uiState.sheetMode) {
        SheetMode.None -> Unit
        is SheetMode.Details -> EventDetailsSheet(
            event     = mode.event,
            onDismiss = { viewModel.onEvent(EventsUiEvent.SheetDismissed) },
            onEdit    = { viewModel.onEvent(EventsUiEvent.EditRequested(mode.event)) },
            onDelete  = { viewModel.onEvent(EventsUiEvent.DeleteRequested(mode.event)) },
        )
        is SheetMode.Add -> AddEditEventSheet(
            prefilledDate = mode.date,
            existingEvent = null,
            saveError     = uiState.errorMessage,
            onDismiss     = { viewModel.onEvent(EventsUiEvent.SheetDismissed) },
            onSave        = { title, name, date, icon, budget, inv ->
                viewModel.onEvent(
                    EventsUiEvent.SaveNew(title, name, date, icon, budget, inv)
                )
            },
        )
        is SheetMode.Edit -> AddEditEventSheet(
            prefilledDate = mode.event.date,
            existingEvent = mode.event,
            saveError     = uiState.errorMessage,
            onDismiss     = { viewModel.onEvent(EventsUiEvent.SheetDismissed) },
            onSave        = { title, name, date, icon, budget, inv ->
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

@Composable
private fun EventsTopBar(
    currentMonth: YearMonth,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppTopBar(
        titleText   = "Events",
        onMenuClick = onMenuClick,
        modifier    = modifier,
        actions     = { MonthSelectorPill(currentMonth = currentMonth) },
    )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarPager(
    initialMonth: YearMonth,
    eventsByDate: Map<Long, Event>,
    onMonthChanged: (YearMonth) -> Unit,
    onDateTap: (LocalDate) -> Unit,
    onDateLongPress: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val today = remember { LocalDate.now() }
    val pagerState = rememberPagerState(initialPage = INITIAL_PAGE) { PAGE_COUNT }
    val baseMonth  = remember { YearMonth.now() }

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
                .padding(horizontal = Spacing.sm), // Item D1: 50% reduced horizontal padding
        )
    }
}

@Composable
internal fun MonthGrid(
    month: YearMonth,
    today: LocalDate,
    eventsByDate: Map<Long, Event>,
    onDateTap: (LocalDate) -> Unit,
    onDateLongPress: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val cells = remember(month) { DateUtils.calendarGrid(month) }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(AppColor.surface)
            .padding(6.dp), // Item D1: 50% reduced blank space around calendar
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            // Weekday header row
            Row(Modifier.fillMaxWidth()) {
                listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { label ->
                    Text(
                        text      = label,
                        style     = labelM,
                        color     = AppColor.text2,
                        textAlign = TextAlign.Center,
                        modifier  = Modifier.weight(1f).height(28.dp).padding(top = 4.dp),
                    )
                }
            }

            // Date rows (up to 6 rows of 7)
            cells.chunked(7).forEach { row ->
                Row(
                    modifier             = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
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
                    repeat(7 - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DateCell(
    date: LocalDate?,
    today: LocalDate,
    month: YearMonth,
    eventsByDate: Map<Long, Event>,
    onTap: (LocalDate) -> Unit,
    onLongPress: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isInMonth = date != null && YearMonth.from(date) == month
    val event     = date?.let { eventsByDate[it.toEpochDay()] }
    val isToday   = date == today

    val bgColor = when {
        date == null || !isInMonth -> AppColor.surfaceVariant.copy(alpha = 0.25f)
        isToday                    -> AppColor.primaryContainer.copy(alpha = 0.85f) // Item D2: Increased current day tint
        event != null              -> AppColor.primaryContainer.copy(alpha = 0.5f)
        else                       -> AppColor.surfaceVariant.copy(alpha = 0.55f)
    }
    val dayColor = when {
        !isInMonth -> AppColor.text3
        isToday    -> AppColor.primary
        else       -> AppColor.text
    }

    val cellModifier = modifier
        .height(62.dp) // Item D2: 20% cell height increase
        .clip(RoundedCornerShape(12.dp))
        .background(bgColor)
        .then(
            if (isToday) Modifier.border(1.5.dp, AppColor.primary, RoundedCornerShape(12.dp)) else Modifier
        )
        .then(
            if (date != null && isInMonth)
                Modifier.combinedClickable(
                    onClick     = { onTap(date) },
                    onLongClick = { onLongPress(date) },
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
                }
            }
        }
    }
}

/** Item D1: Row layout for monthly events list under the calendar */
@Composable
private fun MonthlyEventRow(event: Event, onClick: () -> Unit) {
    Surface(
        onClick  = onClick,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape    = RoundedCornerShape(14.dp),
        color    = AppColor.surface,
        border   = BorderStroke(0.8.dp, if (AppColor.isDark) AppColor.outlineVariant else Color(0xFFE2E8F0)),
    ) {
        Row(
            modifier          = Modifier.padding(horizontal = Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier        = Modifier.size(36.dp).clip(CircleShape).background(AppColor.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(event.icon.fallback, null, tint = AppColor.primary, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(Spacing.md))
            Text(
                text     = event.title,
                style    = titleS,
                color    = AppColor.text,
                modifier = Modifier.weight(1f),
            )
            Text(
                text  = DateUtils.formatDisplay(event.date),
                style = bodyM,
                color = AppColor.text2,
            )
        }
    }
}
