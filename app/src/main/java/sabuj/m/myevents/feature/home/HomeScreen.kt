package sabuj.m.myevents.feature.home

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowForwardIos
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import sabuj.m.myevents.core.icons.AppIcon
import sabuj.m.myevents.domain.model.EventSummary
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.preview.PreviewData
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.labelL
import sabuj.m.myevents.core.theme.numberXl
import sabuj.m.myevents.core.theme.titleL
import sabuj.m.myevents.core.theme.titleM
import sabuj.m.myevents.core.theme.titleS
import sabuj.m.myevents.core.util.DateUtils
import sabuj.m.myevents.core.util.MoneyFormatter
import sabuj.m.myevents.core.ui.AppButtonTonal
import sabuj.m.myevents.core.ui.MenuButton
import sabuj.m.myevents.core.ui.Pill
import sabuj.m.myevents.core.ui.PillVariant
import sabuj.m.myevents.core.ui.doodles.BgBlobs

/**
 * Home screen â€” design.md section 6.4.
 * Matches Home-Light.png sample exactly.
 */
@Composable
fun HomeScreen(
    onMenuClick: () -> Unit,
    onNavigateToEvents: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    HomeContent(state = state, onMenuClick = onMenuClick, onNavigateToEvents = onNavigateToEvents, modifier = modifier)
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
                .verticalScroll(rememberScrollState()),
        ) {
            // ----------------------------------------------------------------
            // Header: "My Events" in same horizontal line as menu button
            // ----------------------------------------------------------------
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Reserve space for the floating menu button drawn by ScreenScaffold
                Spacer(Modifier.width(56.dp))
                Text(
                    text = buildAnnotatedString {
                        append("My ")
                        withStyle(SpanStyle(color = AppColor.primary, fontWeight = FontWeight.Bold)) {
                            append("Events")
                        }
                    },
                    style = titleL,
                    color = AppColor.text,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(Spacing.md))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                // 1. Upcoming Events
                UpcomingEventsCard(events = state.upcomingEvents, onAddEvent = onNavigateToEvents)

                // 2 + 3. Bookings Done | Booking Amount
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                    modifier = Modifier.height(IntrinsicSize.Min),
                ) {
                    BookingsDoneCard(
                        done    = state.bookingsDone,
                        total   = state.bookingsTotal,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                    )
                    BookingAmountCard(
                        paidPaise  = state.bookingAmountPaid,
                        totalPaise = state.bookingAmountTotal,
                        modifier   = Modifier.weight(1f).fillMaxHeight(),
                    )
                }

                // 4. Budget vs Spent
                BudgetCard(spentPaise = state.budgetSpentPaise, totalPaise = state.budgetTotalPaise)

                // 5 + 6. Pending Dues | Invitees
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                    modifier = Modifier.height(IntrinsicSize.Min),
                ) {
                    PendingDuesCard(paise = state.pendingDuesPaise, modifier = Modifier.weight(1f).fillMaxHeight())
                    InviteesCard(guests = state.totalGuests, families = state.totalFamilies, modifier = Modifier.weight(1f).fillMaxHeight())
                }

                // 7. Items Pending
                ItemsPendingCard(count = state.itemsPending)

                Spacer(Modifier.height(Spacing.xl2))
            }
        }

        // Floating menu button â€” top-left above everything
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
// Shared card shell
// ---------------------------------------------------------------------------

@Composable
private fun HomeCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier        = modifier.fillMaxWidth(),
        shape           = RoundedCornerShape(Radius.hero),
        color           = AppColor.surface,
        tonalElevation  = if (AppColor.isDark) 0.dp else 1.dp,
        shadowElevation = if (AppColor.isDark) 0.dp else 6.dp,
        border          = if (AppColor.isDark)
            androidx.compose.foundation.BorderStroke(1.dp, AppColor.outlineVariant)
        else null,
    ) {
        content()
    }
}

/** Large circle icon chip matching sample â€” primaryContainer bg, primary icon. */
@Composable
private fun CardIcon(icon: ImageVector, contentDescription: String = "") {
    Box(
        modifier        = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(AppColor.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription, tint = AppColor.primary, modifier = Modifier.size(28.dp))
    }
}

// ---------------------------------------------------------------------------
// 1. Upcoming Events
// ---------------------------------------------------------------------------

@Composable
private fun UpcomingEventsCard(events: List<EventSummary>, onAddEvent: () -> Unit) {
    HomeCard {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CardIcon(icon = AppIcon.Events.fallback, contentDescription = "")
                Spacer(Modifier.width(Spacing.md))
                Text("Upcoming Events", style = titleM, color = AppColor.text, modifier = Modifier.weight(1f))
                Pill(text = "${events.size} events")
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.md), color = AppColor.outlineVariant)

            if (events.isEmpty()) {
                Column(
                    modifier                = Modifier.fillMaxWidth().padding(vertical = Spacing.lg),
                    horizontalAlignment     = Alignment.CenterHorizontally,
                ) {
                    Text("No events yet", style = titleS, color = AppColor.text2)
                    Spacer(Modifier.height(Spacing.sm))
                    AppButtonTonal("Add event", onClick = onAddEvent, leadingIcon = Icons.Rounded.Add)
                }
            } else {
                events.forEach { event -> EventRow(event = event); Spacer(Modifier.height(Spacing.sm)) }
            }
        }
    }
}

@Composable
private fun EventRow(event: EventSummary) {
    val daysLeft = DateUtils.daysUntil(event.date)
    val pillVariant = when {
        daysLeft <= 0L -> PillVariant.Danger
        daysLeft <= 7L -> PillVariant.Warning
        else           -> PillVariant.Default
    }
    val pillText = when {
        daysLeft == 0L -> "Today"
        daysLeft < 0L  -> "Passed"
        else           -> "${daysLeft} days left"
    }
    Surface(
        modifier = Modifier.fillMaxWidth().height(72.dp),
        shape    = RoundedCornerShape(16.dp),
        color    = AppColor.surfaceVariant.copy(alpha = if (AppColor.isDark) 0.5f else 0.35f),
    ) {
        Row(modifier = Modifier.padding(horizontal = Spacing.md), verticalAlignment = Alignment.CenterVertically) {
            // Event type icon
            Box(
                modifier        = Modifier.size(44.dp).clip(CircleShape).background(AppColor.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(event.icon.fallback, null, tint = AppColor.primary, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(Spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(event.title, style = titleS, color = AppColor.text, fontWeight = FontWeight.SemiBold)
                Text(DateUtils.formatDisplay(event.date), style = bodyM, color = AppColor.text2)
            }
            Pill(text = pillText, icon = AppIcon.Clock.fallback, variant = pillVariant)
            Spacer(Modifier.width(Spacing.sm))
            Icon(Icons.Rounded.ArrowForwardIos, null, tint = AppColor.text3, modifier = Modifier.size(14.dp))
        }
    }
}

// ---------------------------------------------------------------------------
// 2. Bookings Done
// ---------------------------------------------------------------------------

@Composable
private fun BookingsDoneCard(done: Int, total: Int, modifier: Modifier) {
    HomeCard(modifier = modifier) {
        Box {
            Column(modifier = Modifier.fillMaxHeight().padding(Spacing.lg)) {
                CardIcon(icon = AppIcon.Bookings.fallback)
                Spacer(Modifier.height(Spacing.sm))
                Text("Bookings Done", style = labelL, color = AppColor.text2)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, color = AppColor.primary)) { append("$done") }
                        append(" of $total")
                    },
                    style = titleL,
                    color = AppColor.text,
                )
                Text("bookings", style = bodyM, color = AppColor.text2)
                Spacer(Modifier.weight(1f)) // flexible spacer â€” both cards reach same height
            }
            // Faint clipboard art â€” bottom-right
            Box(
                modifier        = Modifier.align(Alignment.BottomEnd).size(64.dp).padding(Spacing.sm),
                contentAlignment = Alignment.Center,
            ) {
                Icon(AppIcon.Bookings.fallback, null, tint = AppColor.primary.copy(alpha = 0.10f), modifier = Modifier.size(56.dp))
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 3. Booking Amount
// ---------------------------------------------------------------------------

@Composable
private fun BookingAmountCard(paidPaise: Long, totalPaise: Long, modifier: Modifier) {
    HomeCard(modifier = modifier) {
        Box {
            Column(modifier = Modifier.fillMaxHeight().padding(Spacing.lg)) {
                CardIcon(icon = AppIcon.Expenses.fallback)
                Spacer(Modifier.height(Spacing.sm))
                Text("Booking Amount", style = labelL, color = AppColor.text2)
                Spacer(Modifier.height(4.dp))
                Text(MoneyFormatter.format(paidPaise), style = numberXl, color = AppColor.primary, fontWeight = FontWeight.Bold)
                Text("paid", style = bodyM, color = AppColor.text2)
                Text("of ${MoneyFormatter.format(totalPaise)}", style = bodyM, color = AppColor.text2)
                Spacer(Modifier.weight(1f)) // flexible spacer â€” both cards reach same height
            }
            Box(
                modifier        = Modifier.align(Alignment.BottomEnd).size(64.dp).padding(Spacing.sm),
                contentAlignment = Alignment.Center,
            ) {
                Icon(AppIcon.Expenses.fallback, null, tint = AppColor.primary.copy(alpha = 0.10f), modifier = Modifier.size(56.dp))
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 4. Budget vs Spent
// ---------------------------------------------------------------------------

@Composable
private fun BudgetCard(spentPaise: Long, totalPaise: Long) {
    val fraction = if (totalPaise > 0) (spentPaise.toFloat() / totalPaise).coerceIn(0f, 1f) else 0f
    val pct      = (fraction * 100).toInt()
    val barColor = when {
        pct >= 100 -> AppColor.danger
        pct >= 80  -> AppColor.warning
        else       -> AppColor.primary
    }

    HomeCard {
        Box {
            Column(modifier = Modifier.padding(Spacing.lg)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CardIcon(icon = AppIcon.Budget.fallback)
                    Spacer(Modifier.width(Spacing.md))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Budget vs Spent", style = titleM, color = AppColor.text)
                        Text("${MoneyFormatter.format(totalPaise)} budget", style = bodyM, color = AppColor.text2)
                    }
                }
                Spacer(Modifier.height(Spacing.md))
                // Spent amount inline with label
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(MoneyFormatter.format(spentPaise), style = numberXl, color = AppColor.text, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(Spacing.xs))
                    Text("spent", style = bodyM, color = AppColor.text2)
                }
                Spacer(Modifier.height(Spacing.md))
                // Progress bar + pct
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
            // Faint bar chart art â€” right side
            Box(
                modifier        = Modifier.align(Alignment.CenterEnd).size(80.dp).padding(end = Spacing.sm),
                contentAlignment = Alignment.Center,
            ) {
                Icon(AppIcon.Budget.fallback, null, tint = AppColor.primary.copy(alpha = 0.08f), modifier = Modifier.size(72.dp))
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 5. Pending Dues
// ---------------------------------------------------------------------------

@Composable
private fun PendingDuesCard(paise: Long, modifier: Modifier) {
    HomeCard(modifier = modifier) {
        Box {
            Column(modifier = Modifier.fillMaxHeight().padding(Spacing.lg)) {
                CardIcon(icon = AppIcon.Dues.fallback)
                Spacer(Modifier.height(Spacing.sm))
                Text("Pending Dues", style = labelL, color = AppColor.text2)
                Spacer(Modifier.height(4.dp))
                Text(
                    text       = MoneyFormatter.format(paise),
                    style      = numberXl,
                    color      = if (paise > 0) AppColor.danger else AppColor.success,
                    fontWeight = FontWeight.Bold,
                )
                Text("unpaid", style = bodyM, color = AppColor.text2)
                Spacer(Modifier.weight(1f)) // flexible spacer
            }
            Box(
                modifier        = Modifier.align(Alignment.BottomEnd).size(64.dp).padding(Spacing.sm),
                contentAlignment = Alignment.Center,
            ) {
                Icon(AppIcon.Dues.fallback, null, tint = AppColor.primary.copy(alpha = 0.10f), modifier = Modifier.size(56.dp))
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 6. Invitees
// ---------------------------------------------------------------------------

@Composable
private fun InviteesCard(guests: Int, families: Int, modifier: Modifier) {
    HomeCard(modifier = modifier) {
        Box {
            Column(modifier = Modifier.fillMaxHeight().padding(Spacing.lg)) {
                CardIcon(icon = AppIcon.Invitees.fallback)
                Spacer(Modifier.height(Spacing.sm))
                Text("Invitees", style = labelL, color = AppColor.text2)
                Spacer(Modifier.height(4.dp))
                Text("$guests", style = numberXl, color = AppColor.primary, fontWeight = FontWeight.Bold)
                Text("guests", style = bodyM, color = AppColor.text2)
                Text("$families families", style = bodyM, color = AppColor.text2)
                Spacer(Modifier.weight(1f)) // flexible spacer
            }
            Box(
                modifier        = Modifier.align(Alignment.BottomEnd).size(64.dp).padding(Spacing.sm),
                contentAlignment = Alignment.Center,
            ) {
                Icon(AppIcon.Invitees.fallback, null, tint = AppColor.primary.copy(alpha = 0.10f), modifier = Modifier.size(56.dp))
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 7. Items Pending
// ---------------------------------------------------------------------------

@Composable
private fun ItemsPendingCard(count: Int) {
    HomeCard {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg),
            ) {
                // Header row â€” icon + labels fill left side; art overlay is on the right
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier          = Modifier.fillMaxWidth().padding(end = 80.dp), // keep away from art
                ) {
                    CardIcon(icon = AppIcon.Checklist.fallback)
                    Spacer(Modifier.width(Spacing.md))
                    Column {
                        Text("Items Pending", style = titleM, color = AppColor.text)
                        Text("items left", style = bodyM, color = AppColor.text2)
                    }
                }
                Spacer(Modifier.height(Spacing.sm))
                // Count: full width, no right padding â€” safe because art sits at CenterEnd
                Text("$count", style = numberXl, color = AppColor.primary, fontWeight = FontWeight.Bold)
            }
            // Faint checklist art â€” right side, non-interactive
            Box(
                modifier        = Modifier.align(Alignment.CenterEnd).size(96.dp).padding(end = Spacing.md),
                contentAlignment = Alignment.Center,
            ) {
                Icon(AppIcon.Checklist.fallback, null, tint = AppColor.primary.copy(alpha = 0.08f), modifier = Modifier.size(80.dp))
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
    AppTheme { HomeContent(state = HomeUiState(), onMenuClick = {}, onNavigateToEvents = {}) }
}

@LightDarkPreviews
@Composable
private fun HomeScreenFilledPreview() {
    AppTheme {
        HomeContent(
            state = HomeUiState(
                upcomingEvents       = listOf(
                    EventSummary("1", "Engagement", PreviewData.sampleEventDate, sabuj.m.myevents.core.icons.EventIcon.Engagement),
                    EventSummary("2", "Wedding",    PreviewData.sampleEventDate.plusDays(120), sabuj.m.myevents.core.icons.EventIcon.Wedding),
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
