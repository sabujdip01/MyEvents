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
    Camera(Icons.Rounded.Favorite, R.drawable.icon_01_row1_col1),       // bk-camera
    Makeup(Icons.Rounded.Favorite, R.drawable.icon_02_row1_col2),       // bk-makeup
    Flower(Icons.Rounded.LocalFlorist, R.drawable.icon_03_row1_col3),   // bk-flower
    Pandal(Icons.Rounded.Favorite, R.drawable.icon_04_row1_col4),       // bk-pandal
    Chef(Icons.Rounded.Favorite, R.drawable.icon_05_row1_col5),         // bk-chef
    Catering(Icons.Rounded.Favorite, R.drawable.icon_06_row1_col6),     // bk-catering
    Decorator(Icons.Rounded.LocalFlorist, R.drawable.icon_07_row2_col1),// bk-decorator
    Priest(Icons.Rounded.Favorite, R.drawable.icon_08_row2_col2),       // bk-priest
    Venue(Icons.Rounded.Favorite, R.drawable.icon_09_row2_col3),        // bk-venue
    Music(Icons.Rounded.MusicNote, R.drawable.icon_10_row2_col4),       // bk-music
    Sound(Icons.Rounded.MusicNote, R.drawable.icon_11_row2_col5),       // bk-sound
    Transport(Icons.Rounded.Favorite, R.drawable.icon_12_row2_col6),    // bk-transport
    Jewellery(Icons.Rounded.Favorite, R.drawable.icon_13_row3_col1),    // bk-jewellery
    Clothing(Icons.Rounded.Favorite, R.drawable.icon_14_row3_col2),     // bk-clothing
    Cards(Icons.Rounded.CardGiftcard, R.drawable.icon_15_row3_col3),    // bk-cards
    Other(Icons.Rounded.Work, R.drawable.icon_16_row3_col4),            // bk-other

    Icon17(Icons.Rounded.Favorite, R.drawable.icon_17_row3_col5),
    Icon18(Icons.Rounded.Favorite, R.drawable.icon_18_row3_col6),
    Icon19(Icons.Rounded.Favorite, R.drawable.icon_19_row4_col1),
    Icon20(Icons.Rounded.Favorite, R.drawable.icon_20_row4_col2),
    Icon21(Icons.Rounded.Favorite, R.drawable.icon_21_row4_col3),
    Icon22(Icons.Rounded.Favorite, R.drawable.icon_22_row4_col4),
    Icon23(Icons.Rounded.Favorite, R.drawable.icon_23_row4_col5),
    Icon24(Icons.Rounded.Favorite, R.drawable.icon_24_row4_col6),
    Icon25(Icons.Rounded.Favorite, R.drawable.icon_25_row5_col1),
    Icon26(Icons.Rounded.Favorite, R.drawable.icon_26_row5_col2),
    Icon27(Icons.Rounded.Favorite, R.drawable.icon_27_row5_col3),
    Icon28(Icons.Rounded.Favorite, R.drawable.icon_28_row5_col4),
    Icon29(Icons.Rounded.Favorite, R.drawable.icon_29_row5_col5),
    Icon30(Icons.Rounded.Favorite, R.drawable.icon_30_row5_col6),
    Icon31(Icons.Rounded.Favorite, R.drawable.icon_31_row6_col1),
    Icon32(Icons.Rounded.Favorite, R.drawable.icon_32_row6_col2),
    Icon33(Icons.Rounded.Favorite, R.drawable.icon_33_row6_col3),
    Icon34(Icons.Rounded.Favorite, R.drawable.icon_34_row6_col4),
    Icon35(Icons.Rounded.Favorite, R.drawable.icon_35_row6_col5),
    Icon36(Icons.Rounded.Favorite, R.drawable.icon_36_row6_col6),
    Icon37(Icons.Rounded.Favorite, R.drawable.icon_37_row7_col1),
    Icon38(Icons.Rounded.Favorite, R.drawable.icon_38_row7_col2),
    Icon39(Icons.Rounded.Favorite, R.drawable.icon_39_row7_col3),
    Icon40(Icons.Rounded.Favorite, R.drawable.icon_40_row7_col4),
    Icon41(Icons.Rounded.Favorite, R.drawable.icon_41_row7_col5),
    Icon42(Icons.Rounded.Favorite, R.drawable.icon_42_row7_col6),
    Icon43(Icons.Rounded.Favorite, R.drawable.icon_43_row8_col1),
    Icon44(Icons.Rounded.Favorite, R.drawable.icon_44_row8_col2),
    Icon45(Icons.Rounded.Favorite, R.drawable.icon_45_row8_col3),
}
