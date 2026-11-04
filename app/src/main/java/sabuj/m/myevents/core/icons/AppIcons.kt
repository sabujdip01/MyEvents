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
import sabuj.m.myevents.R

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
    Wedding(Icons.Rounded.LocalFlorist, R.drawable.ic_category_marriage),
    Engagement(Icons.Rounded.Favorite, R.drawable.ic_category_engagement),
    Birthday(Icons.Rounded.Celebration, R.drawable.ic_category_birthday),
    Reception(Icons.Rounded.Celebration, R.drawable.ic_category_reception),
    Puja(Icons.Rounded.Spa, R.drawable.ic_category_puja),
    Christmas(Icons.Rounded.CardGiftcard, R.drawable.ic_category_childhood),
    Vacation(Icons.Rounded.BeachAccess, R.drawable.ic_category_honeymoon),
    Party(Icons.Rounded.Celebration, R.drawable.ic_category_traditional),
    Festival(Icons.Rounded.Star, R.drawable.ic_category_festivals),
    Anniversary(Icons.Rounded.Favorite, R.drawable.ic_category_community),
    Honeymoon(Icons.Rounded.BeachAccess, R.drawable.ic_category_honeymoon),
    Haldi(Icons.Rounded.Spa, R.drawable.ic_category_traditional),
    Mehendi(Icons.Rounded.PanTool, R.drawable.ic_category_traditional),
    Sangeet(Icons.Rounded.MusicNote, R.drawable.ic_category_reception),
    Dinner(Icons.Rounded.Celebration, R.drawable.ic_category_reception),
    Cocktail(Icons.Rounded.Celebration, R.drawable.ic_category_community),
    Rituals(Icons.Rounded.Spa, R.drawable.ic_category_puja),
    Other(Icons.Rounded.Star, R.drawable.ic_category_community),
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
