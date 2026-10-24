package sabuj.m.myevents.feature.home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import sabuj.m.myevents.core.icons.AppIcon
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.preview.PreviewData
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.display
import sabuj.m.myevents.core.theme.labelL
import sabuj.m.myevents.core.theme.numberM
import sabuj.m.myevents.core.theme.numberXl
import sabuj.m.myevents.core.theme.titleM
import sabuj.m.myevents.core.theme.titleS
import sabuj.m.myevents.core.util.DateUtils
import sabuj.m.myevents.core.util.MoneyFormatter
import sabuj.m.myevents.core.ui.IconChip
import sabuj.m.myevents.core.ui.IconChipSize
import sabuj.m.myevents.core.ui.MenuButton
import sabuj.m.myevents.core.ui.Pill
import sabuj.m.myevents.core.ui.PillVariant
import sabuj.m.myevents.core.ui.ScreenScaffold
import sabuj.m.myevents.core.ui.AppButtonTonal
import sabuj.m.myevents.core.ui.doodles.BgBlobs
import sabuj.m.myevents.core.ui.doodles.HeroIllustration

/**
 * Home screen — design.md section 6.4.
 * Phase 1: all cards show zero/empty state. Phase 6 wires real data.
 */
@Composable
fun HomeScreen(
    onMenuClick: () -> Unit,
    onNavigateToEvents: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    HomeContent(
        state          = state,
        onMenuClick    = onMenuClick,
        onNavigateToEvents = onNavigateToEvents,
        modifier       = modifier,
    )
}

@Composable
private fun HomeContent(
    state: HomeUiState,
    onMenuClick: () -> Unit,
    onNavigateToEvents: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppColor.background),
    ) {
        BgBlobs()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.lg)
                .padding(bottom = Spacing.xl3),
        ) {
            // ----------------------------------------------------------------
            // Header (220 dp total including status bar)
            // ----------------------------------------------------------------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(180.dp),
            ) {
                // Hero illustration — top-right
                HeroIllustration(
                    size     = 128.dp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = Spacing.lg),
                )

                // App name — bottom-left of header
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = Spacing.lg),
                ) {
                    Text(
                        text  = buildAnnotatedString {
                            append("My ")
                            withStyle(SpanStyle(color = AppColor.primary)) { append("Events") }
                        },
                        style = display,
                        color = AppColor.text,
                    )
                }
            }

            Spacer(Modifier.height(Spacing.md))

            // ----------------------------------------------------------------
            // Cards
            // ----------------------------------------------------------------

            // 1. Upcoming Events (full width, hero card)
            UpcomingEventsCard(
                events      = state.upcomingEvents,
                onAddEvent  = onNavigateToEvents,
            )

            Spacer(Modifier.height(Spacing.md))

            // 2 + 3. Bookings Done | Booking Amount (half width)
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                StatCard(
                    modifier   = Modifier.weight(1f),
                    icon       = AppIcon.Bookings.fallback,
                    label      = "Bookings Done",
                    value      = "${state.bookingsDone} of ${state.bookingsTotal}",
                    caption    = "bookings",
                    slot       = 0,
                )
                StatCard(
                    modifier   = Modifier.weight(1f),
                    icon       = AppIcon.Expenses.fallback,
                    label      = "Booking Amount",
                    value      = MoneyFormatter.format(state.bookingAmountPaid),
                    caption    = "paid\nof ${MoneyFormatter.format(state.bookingAmountTotal)}",
                    slot       = 1,
                )
            }

            Spacer(Modifier.height(Spacing.md))

            // 4. Budget vs Spent (full width)
            BudgetCard(
                spentPaise  = state.budgetSpentPaise,
                totalPaise  = state.budgetTotalPaise,
            )

            Spacer(Modifier.height(Spacing.md))

            // 5 + 6. Pending Dues | Invitees (half width)
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                StatCard(
                    modifier   = Modifier.weight(1f),
                    icon       = AppIcon.Dues.fallback,
                    label      = "Pending Dues",
                    value      = MoneyFormatter.format(state.pendingDuesPaise),
                    caption    = "unpaid",
                    valueColor = if (state.pendingDuesPaise > 0) AppColor.danger else AppColor.success,
                    slot       = 2,
                )
                StatCard(
                    modifier   = Modifier.weight(1f),
                    icon       = AppIcon.Invitees.fallback,
                    label      = "Invitees",
                    value      = "${state.totalGuests}",
                    caption    = "guests\n${state.totalFamilies} families",
                    slot       = 3,
                )
            }

            Spacer(Modifier.height(Spacing.md))

            // 7. Items Pending (full width)
            StatCard(
                modifier   = Modifier.fillMaxWidth(),
                icon       = AppIcon.Items.fallback,
                label      = "Items Pending",
                value      = "${state.itemsPending}",
                caption    = "items left",
                slot       = 4,
                minHeight  = 112.dp,
            )
        }

        // Floating menu button
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(start = Spacing.lg, top = Spacing.sm),
        ) {
            MenuButton(onClick = onMenuClick)
        }
    }
}

// ---------------------------------------------------------------------------
// Upcoming Events card
// ---------------------------------------------------------------------------

@Composable
private fun UpcomingEventsCard(
    events: List<EventSummary>,
    onAddEvent: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(Radius.hero),
        color    = AppColor.surface,
        tonalElevation = if (AppColor.isDark) 0.dp else 1.dp,
        shadowElevation = if (AppColor.isDark) 0.dp else 4.dp,
        border  = if (AppColor.isDark)
            androidx.compose.foundation.BorderStroke(1.dp, AppColor.outlineVariant)
        else null,
    ) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconChip(
                    icon = AppIcon.Events.fallback,
                    contentDescription = "",
                )
                Spacer(Modifier.width(Spacing.md))
                Text("Upcoming Events", style = titleM, color = AppColor.text, modifier = Modifier.weight(1f))
                Pill(text = "${events.size} events")
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.md), color = AppColor.outlineVariant)

            if (events.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("No events yet", style = titleS, color = AppColor.text2)
                    Spacer(Modifier.height(Spacing.sm))
                    AppButtonTonal("Add event", onClick = onAddEvent, leadingIcon = Icons.Rounded.Add)
                }
            } else {
                events.forEach { event ->
                    EventRow(event = event)
                    Spacer(Modifier.height(Spacing.sm))
                }
            }
        }
    }
}

@Composable
private fun EventRow(event: EventSummary) {
    val daysLeft   = DateUtils.daysUntil(event.date)
    val pillVariant = when {
        daysLeft <= 0L -> PillVariant.Danger
        daysLeft <= 7L -> PillVariant.Warning
        else           -> PillVariant.Default
    }
    val pillText = when {
        daysLeft == 0L  -> "Today"
        daysLeft < 0L   -> "Passed"
        else            -> "${daysLeft}d left"
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        shape = RoundedCornerShape(16.dp),
        color = AppColor.surfaceVariant.copy(alpha = 0.5f),
    ) {
        Row(
            modifier          = Modifier.padding(horizontal = Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconChip(
                icon               = AppIcon.Events.fallback,
                contentDescription = event.name,
                size               = IconChipSize.Sm,
            )
            Spacer(Modifier.width(Spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(event.name, style = titleS, color = AppColor.text)
                Text(DateUtils.formatDisplay(event.date), style = bodyM, color = AppColor.text2)
            }
            Pill(text = pillText, icon = AppIcon.Clock.fallback, variant = pillVariant)
        }
    }
}

// ---------------------------------------------------------------------------
// Stat card (gradient, half or full width)
// ---------------------------------------------------------------------------

@Composable
private fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    caption: String,
    slot: Int,
    modifier: Modifier = Modifier,
    valueColor: androidx.compose.ui.graphics.Color = AppColor.primary,
    minHeight: androidx.compose.ui.unit.Dp = 128.dp,
) {
    Surface(
        modifier = modifier.height(minHeight),
        shape    = RoundedCornerShape(Radius.card),
        color    = AppColor.surfaceVariant.copy(alpha = 0.6f),
        border   = if (AppColor.isDark)
            androidx.compose.foundation.BorderStroke(1.dp, AppColor.outlineVariant)
        else null,
    ) {
        Box(modifier = Modifier.padding(Spacing.lg)) {
            Column {
                IconChip(icon = icon, contentDescription = "")
                Spacer(Modifier.height(Spacing.sm))
                Text(label, style = labelL, color = AppColor.text2)
                Spacer(Modifier.height(Spacing.xs))
                Text(value, style = numberXl, color = valueColor)
                Text(caption, style = bodyM, color = AppColor.text2)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Budget vs Spent card
// ---------------------------------------------------------------------------

@Composable
private fun BudgetCard(spentPaise: Long, totalPaise: Long) {
    val fraction = if (totalPaise > 0) (spentPaise.toFloat() / totalPaise).coerceIn(0f, 1f) else 0f
    val pct      = (fraction * 100).toInt()
    val barColor = when {
        pct >= 100 -> AppColor.danger
        pct >= 80  -> AppColor.warning
        else       -> AppColor.accent
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(Radius.hero),
        color    = AppColor.surface,
        tonalElevation  = if (AppColor.isDark) 0.dp else 1.dp,
        shadowElevation = if (AppColor.isDark) 0.dp else 4.dp,
        border  = if (AppColor.isDark)
            androidx.compose.foundation.BorderStroke(1.dp, AppColor.outlineVariant)
        else null,
    ) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconChip(icon = AppIcon.Budget.fallback, contentDescription = "")
                Spacer(Modifier.width(Spacing.md))
                Text("Budget vs Spent", style = titleM, color = AppColor.text, modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(Spacing.sm))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(MoneyFormatter.format(spentPaise), style = numberXl, color = AppColor.text)
                Spacer(Modifier.width(Spacing.xs))
                Text("spent", style = bodyM, color = AppColor.text2)
            }
            Text("${MoneyFormatter.format(totalPaise)} budget", style = bodyM, color = AppColor.text2)
            Spacer(Modifier.height(Spacing.md))
            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress         = { fraction },
                    modifier         = Modifier.weight(1f).height(10.dp).clip(RoundedCornerShape(999.dp)),
                    color            = barColor,
                    trackColor       = AppColor.primaryContainer,
                    strokeCap        = StrokeCap.Round,
                )
                Spacer(Modifier.width(Spacing.sm))
                Text("$pct%", style = labelL, color = AppColor.text2)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@LightDarkPreviews
@Composable
private fun HomeScreenEmptyPreview() {
    AppTheme {
        HomeContent(
            state             = HomeUiState(),
            onMenuClick       = {},
            onNavigateToEvents = {},
        )
    }
}

@LightDarkPreviews
@Composable
private fun HomeScreenFilledPreview() {
    AppTheme {
        HomeContent(
            state = HomeUiState(
                upcomingEvents       = listOf(
                    EventSummary(1, "Engagement", PreviewData.sampleEventDate, "Engagement"),
                    EventSummary(2, "Wedding", PreviewData.sampleEventDate.plusDays(80), "Wedding"),
                ),
                bookingsDone         = 4,
                bookingsTotal        = 7,
                bookingAmountPaid    = 8_500_000L,
                bookingAmountTotal   = 15_000_000L,
                budgetSpentPaise     = 12_000_000L,
                budgetTotalPaise     = 30_000_000L,
                pendingDuesPaise     = 3_250_000L,
                totalGuests          = 185,
                totalFamilies        = 42,
                itemsPending         = 8,
            ),
            onMenuClick       = {},
            onNavigateToEvents = {},
        )
    }
}
