package sabuj.m.myevents.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import sabuj.m.myevents.core.icons.AppIcon
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.titleL
import sabuj.m.myevents.core.theme.titleS
import sabuj.m.myevents.core.ui.IconChip
import sabuj.m.myevents.core.ui.IconChipSize
import sabuj.m.myevents.core.ui.doodles.BgBlobs
import sabuj.m.myevents.core.ui.doodles.HeroIllustration

/**
 * App navigation drawer — design.md section 6.3.
 * Width = 82% of screen, surface colour, top-right radius 28.
 * Active item: primary container fill + 4×24 dp primary bar at right edge.
 */
@Composable
fun AppDrawer(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    content: @Composable () -> Unit,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope       = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState     = drawerState,
        scrimColor      = AppColor.scrim,
        gesturesEnabled = drawerState.isOpen, // Item C3: Prevent accidental swipes on main screen from opening drawer
        drawerContent   = {
            DrawerSheet(
                currentRoute = currentRoute,
                onNavigate   = { route ->
                    scope.launch { drawerState.close() }
                    onNavigate(route)
                },
            )
        },
        content = {
            // Provide open callback to content via LocalDrawerState
            androidx.compose.runtime.CompositionLocalProvider(
                LocalOpenDrawer provides { scope.launch { drawerState.open() } }
            ) {
                content()
            }
        },
    )
}

// ---------------------------------------------------------------------------
// Drawer sheet content
// ---------------------------------------------------------------------------

private data class DrawerItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

private val drawerItems = listOf(
    DrawerItem(Routes.HOME,      "Home",               AppIcon.Home.fallback),
    DrawerItem(Routes.EVENTS,    "Events",             AppIcon.Events.fallback),
    DrawerItem(Routes.BOOKINGS,  "Booking",            AppIcon.Bookings.fallback),
    DrawerItem(Routes.CHECKLIST, "Item Lists",         AppIcon.Checklist.fallback),
    DrawerItem(Routes.EXPENSES,  "Expenses",           AppIcon.Expenses.fallback),
    DrawerItem(Routes.INVITEES,  "Invitees",           AppIcon.Invitees.fallback),
    DrawerItem(Routes.SETTINGS,  "Settings",           AppIcon.SettingsIcon.fallback),
)

@Composable
private fun DrawerSheet(
    currentRoute: String,
    onNavigate: (String) -> Unit,
) {
    // 82% width achieved via fillMaxWidth(0.82f) on the ModalDrawerSheet
    ModalDrawerSheet(
        modifier        = Modifier.fillMaxWidth(0.82f).fillMaxHeight(),
        drawerShape     = RoundedCornerShape(topEnd = Radius.sheet, bottomEnd = 0.dp),
        drawerContainerColor = AppColor.surface,
    ) {
        Box(modifier = Modifier.fillMaxHeight()) {
            // Background blobs inside the drawer
            BgBlobs()

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .statusBarsPadding()
                    .padding(top = Spacing.xl2),
            ) {
                // Header: "My Events"
                Column(modifier = Modifier.padding(horizontal = Spacing.xl2)) {
                    Text(
                        text = buildAnnotatedString {
                            append("My ")
                            withStyle(SpanStyle(color = AppColor.primary)) { append("Events") }
                        },
                        style = titleL,
                        color = AppColor.text,
                    )
                }

                Spacer(Modifier.height(Spacing.xl3))

                // Nav items
                drawerItems.forEach { item ->
                    val selected = currentRoute == item.route
                    DrawerNavItem(
                        item     = item,
                        selected = selected,
                        onClick  = { onNavigate(item.route) },
                    )
                    Spacer(Modifier.height(Spacing.sm))
                }

                Spacer(Modifier.weight(1f))

                // Bottom hero illustration
                Box(
                    modifier = Modifier
                        .padding(start = Spacing.lg, bottom = Spacing.lg),
                ) {
                    HeroIllustration(size = 180.dp)
                }
            }
        }
    }
}

@Composable
private fun DrawerNavItem(
    item: DrawerItem,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md),
    ) {
        Surface(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(Radius.card),
            color = if (selected) AppColor.primaryContainer else AppColor.surface.copy(alpha = 0f),
        ) {
            Row(
                modifier          = Modifier.padding(start = Spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconChip(
                    icon               = item.icon,
                    contentDescription = item.label,
                    size               = IconChipSize.Sm,
                    containerColor     = if (selected) AppColor.primaryContainer else AppColor.primaryContainer.copy(alpha = 0.6f),
                    iconColor          = AppColor.primary,
                )
                Spacer(Modifier.width(Spacing.lg))
                Text(
                    text  = item.label,
                    style = titleS,
                    color = if (selected) AppColor.primary else AppColor.text,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // Active indicator bar — 4×24 dp at the right edge
        if (selected) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(width = 4.dp, height = 24.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(AppColor.primary),
            )
        }
    }
}

// ---------------------------------------------------------------------------
// CompositionLocal for the "open drawer" callback
// ---------------------------------------------------------------------------

val LocalOpenDrawer = androidx.compose.runtime.compositionLocalOf<() -> Unit> {
    { /* no-op default */ }
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@LightDarkPreviews
@Composable
private fun DrawerSheetPreview() {
    AppTheme {
        DrawerSheet(currentRoute = Routes.HOME, onNavigate = {})
    }
}
