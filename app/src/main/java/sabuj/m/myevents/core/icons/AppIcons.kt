package sabuj.m.myevents.core.icons

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.AssignmentTurnedIn
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.BeachAccess
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.Celebration
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.LocalFlorist
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PanTool
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.Star
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Navigation and UI icons. Each entry carries a Material fallback [ImageVector]
 * used until the custom SVG drawable is ready (see design.md section 5.2).
 * Replace [customRes] with the actual drawable resource once the SVG is converted.
 */
enum class AppIcon(
    val fallback: ImageVector,
    @DrawableRes val customRes: Int? = null, // TODO custom svg
) {
    Menu(Icons.Rounded.Menu),
    Home(Icons.Rounded.Home),
    Events(Icons.Rounded.CalendarMonth),
    Bookings(Icons.Rounded.AssignmentTurnedIn),
    Expenses(Icons.Rounded.AccountBalanceWallet),
    Invitees(Icons.Rounded.Groups),
    Checklist(Icons.Rounded.Checklist),
    SettingsIcon(Icons.Rounded.Settings),
    Budget(Icons.Rounded.BarChart),
    Dues(Icons.Rounded.ReceiptLong),
    Items(Icons.Rounded.Inventory2),
    Gift(Icons.Rounded.CardGiftcard),
    Clock(Icons.Rounded.Schedule),
}

/**
 * Event type icons shown on the calendar, cards and picker.
 * See design.md section 5.3 & Item E3.
 */
enum class EventIcon(
    val fallback: ImageVector,
    @DrawableRes val customRes: Int? = null,
) {
    Wedding(Icons.Rounded.LocalFlorist),
    Engagement(Icons.Rounded.Favorite),
    Birthday(Icons.Rounded.Celebration),
    Reception(Icons.Rounded.Celebration),
    Puja(Icons.Rounded.Spa),
    Christmas(Icons.Rounded.CardGiftcard),
    Vacation(Icons.Rounded.BeachAccess),
    Party(Icons.Rounded.Celebration),
    Festival(Icons.Rounded.Star),
    Anniversary(Icons.Rounded.Favorite),
    Honeymoon(Icons.Rounded.BeachAccess),
    Haldi(Icons.Rounded.Spa),
    Mehendi(Icons.Rounded.PanTool),
    Sangeet(Icons.Rounded.MusicNote),
    Dinner(Icons.Rounded.Celebration),
    Cocktail(Icons.Rounded.Celebration),
    Rituals(Icons.Rounded.Spa),
    Other(Icons.Rounded.Star),
}

/**
 * Booking category icons shown on booking cards and the picker.
 * See design.md section 5.4 & Item F3.
 */
enum class BookingIcon(
    val fallback: ImageVector,
    @DrawableRes val customRes: Int? = null,
) {
    Camera(Icons.Rounded.Favorite),       // bk-camera
    Makeup(Icons.Rounded.Favorite),       // bk-makeup
    Flower(Icons.Rounded.LocalFlorist),   // bk-flower
    Pandal(Icons.Rounded.Favorite),       // bk-pandal
    Chef(Icons.Rounded.Favorite),         // bk-chef
    Catering(Icons.Rounded.Favorite),     // bk-catering
    Decorator(Icons.Rounded.LocalFlorist),// bk-decorator
    Priest(Icons.Rounded.Favorite),       // bk-priest
    Venue(Icons.Rounded.Favorite),        // bk-venue
    Music(Icons.Rounded.MusicNote),       // bk-music
    Sound(Icons.Rounded.MusicNote),       // bk-sound
    Transport(Icons.Rounded.Favorite),    // bk-transport
    Jewellery(Icons.Rounded.Favorite),    // bk-jewellery
    Clothing(Icons.Rounded.Favorite),     // bk-clothing
    Cards(Icons.Rounded.CardGiftcard),    // bk-cards
    Other(Icons.Rounded.Work),            // bk-other
}
