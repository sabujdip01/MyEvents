package sabuj.m.myevents.feature.events

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import sabuj.m.myevents.core.ui.AppSheet
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.ui.text.font.FontWeight
import sabuj.m.myevents.core.icons.AppIcon
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.labelL
import sabuj.m.myevents.core.theme.labelM
import sabuj.m.myevents.core.theme.titleL
import sabuj.m.myevents.core.theme.titleM
import sabuj.m.myevents.core.theme.titleS
import sabuj.m.myevents.core.ui.AppTopBar
import sabuj.m.myevents.core.ui.Pill
import sabuj.m.myevents.core.ui.PillVariant
import sabuj.m.myevents.core.ui.ScreenScaffold
import sabuj.m.myevents.core.util.DateUtils
import sabuj.m.myevents.domain.model.Event
import java.time.LocalDate
import java.time.YearMonth

private const val START_YEAR = 2026

private fun pageForMonth(month: YearMonth): Int =
    ((month.year - START_YEAR) * 12 + (month.monthValue - 1)).coerceAtLeast(0)

private fun monthForPage(page: Int): YearMonth =
    YearMonth.of(START_YEAR + page / 12, (page % 12) + 1)

private fun getTotalPageCount(): Int {
    val maxYear = maxOf(2028, LocalDate.now().year + 2)
    return (maxYear - START_YEAR + 1) * 12
}

/**
 * Events calendar screen — Items D1–D6.
 * Shows a monthly calendar with 20% increased cell heights, 50% reduced margins,
 * stronger current day tint, and a monthly events list below.
 */
@Composable
fun EventsScreen(
    onMenuClick: () -> Unit,
    viewModel: EventsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    EventsContent(
        uiState     = uiState,
        onMenuClick = onMenuClick,
        onEvent     = viewModel::onEvent,
    )
}

/**
 * Events calendar content — Items D1–D6.
 * Shows a monthly calendar with 20% increased cell heights, 50% reduced margins,
 * stronger current day tint, and a monthly events list below.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun EventsContent(
    uiState: EventsUiState,
    onMenuClick: () -> Unit,
    onEvent: (EventsUiEvent) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var showMonthPicker by remember { mutableStateOf(false) }

    val pageCount = remember { getTotalPageCount() }
    val initialPage = remember { pageForMonth(uiState.currentMonth).coerceIn(0, pageCount - 1) }
    val pagerState = rememberPagerState(initialPage = initialPage) { pageCount }

    // Derived directly from calendar pager's current page — guaranteed 100% in sync
    val displayedMonth = remember(pagerState.currentPage) {
        monthForPage(pagerState.currentPage)
    }

    // Keep ViewModel/State in sync with displayed month
    LaunchedEffect(displayedMonth) {
        onEvent(EventsUiEvent.MonthChanged(displayedMonth))
    }

    // Filter events directly from uiState.events for the displayed month
    val currentMonthEvents = remember(displayedMonth, uiState.events) {
        uiState.events
            .filter { YearMonth.from(it.date) == displayedMonth }
            .sortedBy { it.date }
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
                .verticalScroll(rememberScrollState()),
        ) {
            // Top bar
            EventsTopBar(
                currentMonth = displayedMonth,
                onMenuClick  = onMenuClick,
                onReturnToCurrentMonth = {
                    val targetPage = pageForMonth(YearMonth.now()).coerceIn(0, pageCount - 1)
                    coroutineScope.launch { pagerState.animateScrollToPage(targetPage) }
                },
                onOpenMonthPicker = { showMonthPicker = true },
            )

            Spacer(Modifier.height(Spacing.xs))

            // Calendar pager
            CalendarPager(
                eventsByDate    = uiState.eventsByDate,
                onMonthChanged  = { },
                onDateTap       = { onEvent(EventsUiEvent.DateTapped(it)) },
                onDateLongPress = { onEvent(EventsUiEvent.DateLongPressed(it)) },
                pagerState      = pagerState,
                modifier        = Modifier
                    .fillMaxWidth()
                    .height(390.dp),
            )

            // Space between calendar and event list = 1/4 of current Calendar height (85dp)
            Spacer(Modifier.height(85.dp))

            // Item D1: Monthly Events List under the calendar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg)
                    .padding(bottom = 32.dp),
            ) {
                Text(
                    text       = "Event(s) in this month:",
                    style      = titleM,
                    fontWeight = FontWeight.Bold,
                    color      = AppColor.text,
                    modifier   = Modifier.padding(bottom = Spacing.md),
                )

                if (currentMonthEvents.isNotEmpty()) {
                    Column(
                        modifier            = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                    ) {
                        currentMonthEvents.forEach { event ->
                            MonthlyEventRow(
                                event   = event,
                                onClick = { onEvent(EventsUiEvent.DateTapped(event.date)) },
                            )
                        }
                    }
                } else {
                    Text(
                        text     = "No events planned for this month",
                        style    = bodyM,
                        color    = AppColor.text2,
                        modifier = Modifier.padding(vertical = Spacing.xs),
                    )
                }
            }
        }
    }

    if (showMonthPicker) {
        MonthPickerSheet(
            currentMonth = displayedMonth,
            onMonthSelected = { selectedMonth ->
                val targetPage = pageForMonth(selectedMonth).coerceIn(0, pageCount - 1)
                coroutineScope.launch { pagerState.animateScrollToPage(targetPage) }
            },
            onDismiss = { showMonthPicker = false },
        )
    }

    // Bottom sheets & Dialogs
    when (val mode = uiState.sheetMode) {
        SheetMode.None -> Unit
        is SheetMode.Details -> EventDetailsSheet(
            event     = mode.event,
            onDismiss = { onEvent(EventsUiEvent.SheetDismissed) },
            onEdit    = { onEvent(EventsUiEvent.EditRequested(mode.event)) },
            onDelete  = { onEvent(EventsUiEvent.DeleteRequested(mode.event)) },
        )
        is SheetMode.Add -> AddEditEventSheet(
            prefilledDate = mode.date,
            existingEvent = null,
            saveError     = uiState.errorMessage,
            onDismiss     = { onEvent(EventsUiEvent.SheetDismissed) },
            onSave        = { title, name, date, icon, budget, inv ->
                onEvent(
                    EventsUiEvent.SaveNew(title, name, date, icon, budget, inv)
                )
            },
        )
        is SheetMode.Edit -> AddEditEventSheet(
            prefilledDate = mode.event.date,
            existingEvent = mode.event,
            saveError     = uiState.errorMessage,
            onDismiss     = { onEvent(EventsUiEvent.SheetDismissed) },
            onSave        = { title, name, date, icon, budget, inv ->
                onEvent(
                    EventsUiEvent.SaveEdit(title, name, date, icon, budget, inv)
                )
            },
        )
        is SheetMode.ConfirmDelete -> DeleteEventDialog(
            event     = mode.event,
            onDismiss = { onEvent(EventsUiEvent.SheetDismissed) },
            onConfirm = { onEvent(EventsUiEvent.DeleteConfirmed) },
        )
    }
}

@Composable
private fun EventsTopBar(
    currentMonth: YearMonth,
    onMenuClick: () -> Unit,
    onReturnToCurrentMonth: () -> Unit,
    onOpenMonthPicker: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppTopBar(
        titleText   = "Events",
        onMenuClick = onMenuClick,
        modifier    = modifier,
        titleContent = {
            Text(
                text     = "Events",
                style    = titleL,
                color    = AppColor.text,
                modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = { onReturnToCurrentMonth() }
                    )
                },
            )
        },
        actions = {
            MonthSelectorPill(
                currentMonth = currentMonth,
                onClick      = onOpenMonthPicker,
            )
        },
    )
}

@Composable
private fun MonthSelectorPill(
    currentMonth: YearMonth,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick  = onClick,
        shape    = RoundedCornerShape(999.dp),
        color    = AppColor.primaryContainer,
        modifier = modifier,
    ) {
        Row(
            modifier          = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
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
                contentDescription = "Select Month",
                tint               = AppColor.onPrimaryContainer,
                modifier           = Modifier.size(20.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarPager(
    eventsByDate: Map<Long, Event>,
    onMonthChanged: (YearMonth) -> Unit,
    onDateTap: (LocalDate) -> Unit,
    onDateLongPress: (LocalDate) -> Unit,
    pagerState: androidx.compose.foundation.pager.PagerState,
    modifier: Modifier = Modifier,
) {
    val today = remember { LocalDate.now() }

    LaunchedEffect(pagerState.currentPage) {
        val month = monthForPage(pagerState.currentPage)
        onMonthChanged(month)
    }

    VerticalPager(
        state    = pagerState,
        modifier = modifier,
    ) { page ->
        val month = monthForPage(page)
        MonthGrid(
            month           = month,
            today           = today,
            eventsByDate    = eventsByDate,
            onDateTap       = onDateTap,
            onDateLongPress = onDateLongPress,
            modifier        = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.sm),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MonthPickerSheet(
    currentMonth: YearMonth,
    onMonthSelected: (YearMonth) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedYear by remember { mutableStateOf(currentMonth.year) }
    val endYear = maxOf(2028, LocalDate.now().year + 2)
    val years = remember { (START_YEAR..endYear).toList() }

    AppSheet(onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        ) {
            Text(
                text  = "Select Month & Year",
                style = titleS,
                color = AppColor.text,
            )
            Spacer(Modifier.height(Spacing.md))

            // Year Selector Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                modifier = Modifier.fillMaxWidth(),
            ) {
                years.forEach { year ->
                    val isSelected = year == selectedYear
                    Surface(
                        onClick = { selectedYear = year },
                        shape   = RoundedCornerShape(999.dp),
                        color   = if (isSelected) AppColor.primary else AppColor.primaryContainer,
                        modifier = Modifier.weight(1f).height(40.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text  = "$year",
                                style = labelL,
                                color = if (isSelected) AppColor.onPrimary else AppColor.onPrimaryContainer,
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(Spacing.lg))

            // Months Grid (4 rows of 3)
            val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
            monthNames.chunked(3).forEachIndexed { rowIndex, rowMonths ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                ) {
                    rowMonths.forEachIndexed { colIndex, name ->
                        val monthVal = rowIndex * 3 + colIndex + 1
                        val monthYr = YearMonth.of(selectedYear, monthVal)
                        val isSelected = monthYr == currentMonth

                        Surface(
                            onClick = {
                                onMonthSelected(monthYr)
                                onDismiss()
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) AppColor.primary else AppColor.surfaceVariant,
                            modifier = Modifier.weight(1f).height(44.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text  = name,
                                    style = labelL,
                                    color = if (isSelected) AppColor.onPrimary else AppColor.text,
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(Spacing.xl))
        }
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
        .height(54.dp)
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

/** Item D1: Row layout for monthly events list under the calendar — matching "Upcoming Events" card */
@Composable
private fun MonthlyEventRow(event: Event, onClick: () -> Unit) {
    val daysLeft = DateUtils.daysUntil(event.date)
    val pillVariant = when {
        daysLeft <= 0L -> PillVariant.Danger
        daysLeft <= 7L -> PillVariant.Warning
        else           -> PillVariant.Default
    }
    val pillText = when {
        daysLeft == 0L -> "Today"
        daysLeft < 0L  -> "Passed"
        else           -> "$daysLeft days left"
    }
    Surface(
        onClick  = onClick,
        modifier = Modifier.fillMaxWidth().height(64.dp),
        shape    = RoundedCornerShape(14.dp),
        color    = AppColor.surfaceVariant.copy(alpha = if (AppColor.isDark) 0.5f else 0.35f),
    ) {
        Row(
            modifier          = Modifier.padding(horizontal = Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier        = Modifier.size(40.dp).clip(CircleShape).background(AppColor.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(event.icon.fallback, null, tint = AppColor.primary, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(Spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(event.title, style = titleS, color = AppColor.text, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(Spacing.xs))
                Text(DateUtils.formatDisplay(event.date), style = bodyM, color = AppColor.text2)
            }
            Pill(text = pillText, icon = AppIcon.Clock.fallback, variant = pillVariant)
            Spacer(Modifier.width(Spacing.sm))
            Icon(
                imageVector        = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                contentDescription = "View Event Details",
                tint               = AppColor.primary,
                modifier           = Modifier.size(16.dp),
            )
        }
    }
}
