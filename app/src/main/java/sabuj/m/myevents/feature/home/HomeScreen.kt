package sabuj.m.myevents.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import sabuj.m.myevents.core.icons.AppIcon
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.labelL
import sabuj.m.myevents.core.theme.titleL
import sabuj.m.myevents.core.theme.titleM
import sabuj.m.myevents.core.theme.titleS
import sabuj.m.myevents.core.ui.AppButtonTonal
import sabuj.m.myevents.core.ui.Pill
import sabuj.m.myevents.core.ui.PillVariant
import sabuj.m.myevents.core.ui.doodles.BgBlobs
import sabuj.m.myevents.core.util.DateUtils
import sabuj.m.myevents.core.util.MoneyFormatter
import sabuj.m.myevents.domain.model.Event
import sabuj.m.myevents.domain.model.EventSummary
import sabuj.m.myevents.feature.events.EventDetailsSheet

/**
 * Home screen dashboard — design.md section 6.4 & Items B1–B4.
 */
@Composable
fun HomeScreen(
    onMenuClick: () -> Unit,
    onNavigateToEvents: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    var selectedEventSummary by remember { mutableStateOf<EventSummary?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        HomeContent(
            state               = state,
            onMenuClick         = onMenuClick,
            onNavigateToEvents  = onNavigateToEvents,
            onEventClick        = { eventSummary -> selectedEventSummary = eventSummary },
            modifier            = modifier,
        )

        // Item B3: View Event Popup on chevron click
        selectedEventSummary?.let { summary ->
            EventDetailsSheet(
                event = Event(
                    id               = summary.id,
                    title            = summary.title,
                    eventName        = summary.title,
                    date             = summary.date,
                    icon             = summary.icon,
                    budgetPaise      = 0L,
                    inviteesRequired = true,
                    createdAt        = 0L,
                    updatedAt        = 0L,
                    syncState        = sabuj.m.myevents.domain.model.SyncState.PENDING,
                    deleted          = false,
                ),
                onDismiss = { selectedEventSummary = null },
                onEdit    = { selectedEventSummary = null; onNavigateToEvents() },
                onDelete  = { selectedEventSummary = null; onNavigateToEvents() },
            )
        }
    }
}

@Composable
private fun HomeContent(
    state: HomeUiState,
    onMenuClick: () -> Unit,
    onNavigateToEvents: () -> Unit,
    onEventClick: (EventSummary) -> Unit,
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
            // Top Bar with Nav Icon & "My Events" title
            Box(modifier = Modifier.padding(horizontal = Spacing.lg)) {
                sabuj.m.myevents.core.ui.AppTopBar(
                    titleText   = "My Events",
                    onMenuClick = onMenuClick,
                    titleContent = {
                        Text(
                            text = buildAnnotatedString {
                                append("My ")
                                withStyle(SpanStyle(color = AppColor.primary, fontWeight = FontWeight.Bold)) {
                                    append("Events")
                                }
                            },
                            style = titleL,
                            color = AppColor.text,
                        )
                    },
                )
            }

            Spacer(Modifier.height(Spacing.sm))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                // 1. Upcoming Events
                UpcomingEventsCard(events = state.upcomingEvents, onAddEvent = onNavigateToEvents, onEventClick = onEventClick)

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
                    InviteesCard(
                        totalGuests     = state.totalGuests,
                        invitedGuests   = state.invitedGuests,
                        totalFamilies   = state.totalFamilies,
                        invitedFamilies = state.invitedFamilies,
                        modifier        = Modifier.weight(1f).fillMaxHeight(),
                    )
                }

                // 7. Items Pending
                ItemsPendingCard(count = state.itemsPending)

                // Navigation bar padding prevents overlap with Android system nav bar
                Spacer(Modifier.navigationBarsPadding().height(Spacing.xl2))
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Shared card shell — Item B2 (light border for light mode)
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
        shadowElevation = 2.dp, // 50% reduced shadow
        border          = BorderStroke(
            0.8.dp,
            if (AppColor.isDark) AppColor.outlineVariant else Color(0xFFE2E8F0), // light border for light mode
        ),
    ) {
        content()
    }
}

/** Circle icon chip matching sample. */
@Composable
private fun CardIcon(icon: ImageVector, contentDescription: String = "") {
    Box(
        modifier        = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(AppColor.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription, tint = AppColor.primary, modifier = Modifier.size(22.dp))
    }
}

// ---------------------------------------------------------------------------
// 1. Upcoming Events
// ---------------------------------------------------------------------------

@Composable
private fun UpcomingEventsCard(
    events: List<EventSummary>,
    onAddEvent: () -> Unit,
    onEventClick: (EventSummary) -> Unit,
) {
    HomeCard {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CardIcon(icon = AppIcon.Events.fallback, contentDescription = "")
                Spacer(Modifier.width(Spacing.md))
                Text("Upcoming Events", style = titleM, color = AppColor.text, modifier = Modifier.weight(1f))
                Pill(text = "${events.size} events")
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.sm), color = AppColor.outlineVariant)

            if (events.isEmpty()) {
                Column(
                    modifier                = Modifier.fillMaxWidth().padding(vertical = Spacing.md),
                    horizontalAlignment     = Alignment.CenterHorizontally,
                ) {
                    Text("No events yet", style = titleS, color = AppColor.text2)
                    Spacer(Modifier.height(Spacing.sm))
                    AppButtonTonal("Add event", onClick = onAddEvent, leadingIcon = Icons.Rounded.Add)
                }
            } else {
                events.forEach { event ->
                    EventRow(event = event, onClick = { onEventClick(event) })
                    Spacer(Modifier.height(Spacing.xs))
                }
            }
        }
    }
}

@Composable
private fun EventRow(event: EventSummary, onClick: () -> Unit) {
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
        Row(modifier = Modifier.padding(horizontal = Spacing.md), verticalAlignment = Alignment.CenterVertically) {
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
            Icon(Icons.AutoMirrored.Rounded.ArrowForwardIos, "View Event Details", tint = AppColor.primary, modifier = Modifier.size(16.dp))
        }
    }
}

// ---------------------------------------------------------------------------
// 2. Bookings Done (Item B1 — Header row: Icon + Title in same horizontal line)
// ---------------------------------------------------------------------------

@Composable
private fun BookingsDoneCard(done: Int, total: Int, modifier: Modifier) {
    HomeCard(modifier = modifier) {
        Box {
            Column(modifier = Modifier.fillMaxHeight().padding(Spacing.md)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CardIcon(icon = AppIcon.Bookings.fallback)
                    Spacer(Modifier.width(Spacing.md))
                    Text("Bookings Done", style = titleM, color = AppColor.text, modifier = Modifier.weight(1f))
                }
                Column(
                    modifier            = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold)) {
                                append("$done")
                            }
                            withStyle(SpanStyle(fontSize = 19.sp, fontWeight = FontWeight.Medium, color = AppColor.text2)) {
                                append(" of ")
                            }
                            withStyle(SpanStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold)) {
                                append("$total")
                            }
                        },
                        color     = AppColor.primary,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text      = "bookings made",
                        style     = bodyM,
                        color     = AppColor.text2,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            Box(
                modifier        = Modifier.align(Alignment.BottomEnd).size(48.dp).padding(Spacing.xs),
                contentAlignment = Alignment.Center,
            ) {
                Icon(AppIcon.Bookings.fallback, null, tint = AppColor.primary.copy(alpha = 0.08f), modifier = Modifier.size(40.dp))
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 3. Booking Amount (Item B1 — Header row: Icon + Title in same horizontal line)
// ---------------------------------------------------------------------------

@Composable
private fun BookingAmountCard(paidPaise: Long, totalPaise: Long, modifier: Modifier) {
    HomeCard(modifier = modifier) {
        Box {
            Column(modifier = Modifier.fillMaxHeight().padding(Spacing.md)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CardIcon(icon = AppIcon.Expenses.fallback)
                    Spacer(Modifier.width(Spacing.md))
                    Text("Booking Amount", style = titleM, color = AppColor.text, modifier = Modifier.weight(1f))
                }
                Column(
                    modifier            = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text      = MoneyFormatter.format(paidPaise),
                        style     = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold),
                        color     = AppColor.primary,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text      = "paid of ${MoneyFormatter.format(totalPaise)}",
                        style     = bodyM,
                        color     = AppColor.text2,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            Box(
                modifier        = Modifier.align(Alignment.BottomEnd).size(48.dp).padding(Spacing.xs),
                contentAlignment = Alignment.Center,
            ) {
                Icon(AppIcon.Expenses.fallback, null, tint = AppColor.primary.copy(alpha = 0.08f), modifier = Modifier.size(40.dp))
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
            Column(modifier = Modifier.padding(Spacing.md)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CardIcon(icon = AppIcon.Budget.fallback)
                    Spacer(Modifier.width(Spacing.md))
                    Text("Budget vs Spent", style = titleM, color = AppColor.text, modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(Spacing.sm))
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold, color = AppColor.primary)) {
                            append(MoneyFormatter.format(spentPaise))
                        }
                        withStyle(SpanStyle(fontSize = 19.sp, fontWeight = FontWeight.Medium, color = AppColor.text2)) {
                            append(" of ")
                        }
                        withStyle(SpanStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AppColor.primary)) {
                            append(MoneyFormatter.format(totalPaise))
                        }
                        withStyle(SpanStyle(fontSize = 19.sp, fontWeight = FontWeight.Medium, color = AppColor.text2)) {
                            append(" spent")
                        }
                    },
                )
                Spacer(Modifier.height(Spacing.sm))
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
            Box(
                modifier        = Modifier.align(Alignment.CenterEnd).size(72.dp).padding(end = Spacing.xs),
                contentAlignment = Alignment.Center,
            ) {
                Icon(AppIcon.Budget.fallback, null, tint = AppColor.primary.copy(alpha = 0.08f), modifier = Modifier.size(64.dp))
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 5. Pending Dues (Item B1 — Header row: Icon + Title in same horizontal line)
// ---------------------------------------------------------------------------

@Composable
private fun PendingDuesCard(paise: Long, modifier: Modifier) {
    HomeCard(modifier = modifier) {
        Box {
            Column(modifier = Modifier.fillMaxHeight().padding(Spacing.md)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CardIcon(icon = AppIcon.Dues.fallback)
                    Spacer(Modifier.width(Spacing.md))
                    Text("Pending Dues", style = titleM, color = AppColor.text, modifier = Modifier.weight(1f))
                }
                Column(
                    modifier            = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text      = MoneyFormatter.format(paise),
                        style     = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold),
                        color     = if (paise > 0) AppColor.danger else AppColor.success,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text      = "unpaid balance",
                        style     = bodyM,
                        color     = AppColor.text2,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            Box(
                modifier        = Modifier.align(Alignment.BottomEnd).size(48.dp).padding(Spacing.xs),
                contentAlignment = Alignment.Center,
            ) {
                Icon(AppIcon.Dues.fallback, null, tint = AppColor.primary.copy(alpha = 0.08f), modifier = Modifier.size(40.dp))
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 6. Invitees (Item B1 — Header row: Icon + Title in same horizontal line)
// ---------------------------------------------------------------------------

@Composable
private fun InviteesCard(
    totalGuests: Int,
    invitedGuests: Int,
    totalFamilies: Int,
    invitedFamilies: Int,
    modifier: Modifier,
) {
    HomeCard(modifier = modifier) {
        Box {
            Column(modifier = Modifier.fillMaxHeight().padding(Spacing.md)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CardIcon(icon = AppIcon.Invitees.fallback)
                    Spacer(Modifier.width(Spacing.md))
                    Text("Guests", style = titleM, color = AppColor.text, modifier = Modifier.weight(1f))
                }
                Column(
                    modifier            = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold, color = AppColor.primary)) {
                                append("$invitedGuests")
                            }
                            withStyle(SpanStyle(fontSize = 19.sp, fontWeight = FontWeight.Medium, color = AppColor.text2)) {
                                append(" of ")
                            }
                            withStyle(SpanStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold, color = AppColor.primary)) {
                                append("$totalGuests")
                            }
                            withStyle(SpanStyle(fontSize = 19.sp, fontWeight = FontWeight.Medium, color = AppColor.text2)) {
                                append(" Invited")
                            }
                        },
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text      = "($invitedFamilies of $totalFamilies families)",
                        style     = bodyM,
                        color     = AppColor.text2,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            Box(
                modifier        = Modifier.align(Alignment.BottomEnd).size(48.dp).padding(Spacing.xs),
                contentAlignment = Alignment.Center,
            ) {
                Icon(AppIcon.Invitees.fallback, null, tint = AppColor.primary.copy(alpha = 0.08f), modifier = Modifier.size(40.dp))
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
                    .padding(Spacing.md),
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    CardIcon(icon = AppIcon.Checklist.fallback)
                    Spacer(Modifier.width(Spacing.md))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Items Pending", style = titleM, color = AppColor.text)
                        Spacer(Modifier.height(Spacing.xs))
                        Text(
                            text  = "$count",
                            style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold),
                            color = AppColor.primary,
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text  = "items left",
                            style = bodyM,
                            color = AppColor.text2,
                        )
                    }
                }
            }
            Box(
                modifier        = Modifier.align(Alignment.CenterEnd).size(72.dp).padding(end = Spacing.xs),
                contentAlignment = Alignment.Center,
            ) {
                Icon(AppIcon.Checklist.fallback, null, tint = AppColor.primary.copy(alpha = 0.08f), modifier = Modifier.size(64.dp))
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
            state              = HomeUiState(),
            onMenuClick        = {},
            onNavigateToEvents = {},
            onEventClick       = {},
        )
    }
}

@LightDarkPreviews
@Composable
private fun HomeScreenFilledPreview() {
    AppTheme {
        HomeContent(
            state = HomeUiState(
                upcomingEvents     = sabuj.m.myevents.core.preview.PreviewData.sampleEvents.map {
                    EventSummary(id = it.id, title = it.title, date = it.date, icon = it.icon)
                },
                bookingsDone       = 4,
                bookingsTotal      = 7,
                bookingAmountPaid  = 85_000_00L,
                bookingAmountTotal = 150_000_00L,
                budgetSpentPaise   = 120_000_00L,
                budgetTotalPaise   = 300_000_00L,
                pendingDuesPaise   = 32_500_00L,
                totalGuests        = 185,
                invitedGuests      = 120,
                totalFamilies      = 42,
                invitedFamilies    = 30,
                itemsPending       = 8,
            ),
            onMenuClick        = {},
            onNavigateToEvents = {},
            onEventClick       = {},
        )
    }
}
