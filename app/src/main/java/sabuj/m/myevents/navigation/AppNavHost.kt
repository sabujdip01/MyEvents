package sabuj.m.myevents.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import sabuj.m.myevents.feature.bookings.BookingsScreen
import sabuj.m.myevents.feature.events.EventsScreen
import sabuj.m.myevents.feature.home.HomeScreen
import sabuj.m.myevents.feature.settings.SettingsScreen

/**
 * Root NavHost wrapped in [AppDrawer].
 * Each route is a full-screen destination; the drawer slides over the content.
 */
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route ?: Routes.HOME

    AppDrawer(
        currentRoute = currentRoute,
        onNavigate   = { route ->
            if (route == Routes.HOME) {
                navController.navigate(Routes.HOME) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            } else if (route != currentRoute) {
                navController.navigate(route) {
                    popUpTo(Routes.HOME) { saveState = true }
                    launchSingleTop = true
                    restoreState    = true
                }
            }
        },
    ) {
        // Read INSIDE the content lambda - AppDrawer provides LocalOpenDrawer here
        val openDrawer = LocalOpenDrawer.current

        NavHost(
            navController    = navController,
            startDestination = Routes.HOME,
            modifier         = modifier,
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    onMenuClick        = openDrawer,
                    onNavigateToEvents = { navController.navigate(Routes.EVENTS) },
                )
            }
            composable(Routes.EVENTS) {
                EventsScreen(onMenuClick = openDrawer)
            }
            composable(Routes.BOOKINGS) {
                BookingsScreen(onMenuClick = openDrawer)
            }
            composable(Routes.EXPENSES) {
                sabuj.m.myevents.feature.expenses.ExpensesScreen(onMenuClick = openDrawer)
            }
            composable(Routes.GUESTS) {
                sabuj.m.myevents.feature.guests.GuestsScreen(onMenuClick = openDrawer)
            }
            composable(Routes.ITEM_LISTS) {
                sabuj.m.myevents.feature.itemlists.ItemListsScreen(onMenuClick = openDrawer)
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(onMenuClick = openDrawer)
            }
        }
    }
}
