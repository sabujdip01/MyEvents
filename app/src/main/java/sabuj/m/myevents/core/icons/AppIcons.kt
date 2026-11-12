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
    Guests(Icons.Rounded.Groups),
    Invitees(Icons.Rounded.Groups),
    ItemLists(Icons.Rounded.Checklist),
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
    val displayName: String,
    val fallback: ImageVector,
    @DrawableRes val customRes: Int? = null,
) {
    Cake("Cake", Icons.Rounded.Celebration, R.drawable.icon_01_row1_col1),
    Sweets("Sweets", Icons.Rounded.Celebration, R.drawable.icon_02_row1_col2),
    Flowers("Flowers", Icons.Rounded.LocalFlorist, R.drawable.icon_03_row1_col3),
    Lighting("Lighting", Icons.Rounded.Star, R.drawable.icon_04_row1_col4),
    PandalStage("Pandal / Stage", Icons.Rounded.Favorite, R.drawable.icon_05_row1_col5),

    BandParty("Band Party", Icons.Rounded.MusicNote, R.drawable.icon_06_row1_col6),
    Caterer("Caterer", Icons.Rounded.Favorite, R.drawable.icon_07_row2_col1),
    Cook("Cook", Icons.Rounded.Favorite, R.drawable.icon_08_row2_col2),
    Waiter("Waiter", Icons.Rounded.Favorite, R.drawable.icon_09_row2_col3),
    VegetablesGroceries("Vegetables / Groceries", Icons.Rounded.Favorite, R.drawable.icon_10_row2_col4),

    Fishes("Fishes", Icons.Rounded.Favorite, R.drawable.icon_11_row2_col5),
    Meats("Meats", Icons.Rounded.Favorite, R.drawable.icon_12_row2_col6),
    PanditJi("Pandit Ji", Icons.Rounded.Favorite, R.drawable.icon_13_row3_col1),
    RitualGoods("Ritual Goods", Icons.Rounded.Favorite, R.drawable.icon_14_row3_col2),
    ClayItems("Clay Items", Icons.Rounded.Favorite, R.drawable.icon_15_row3_col3),

    HolyLeaves("Holy Leaves", Icons.Rounded.LocalFlorist, R.drawable.icon_16_row3_col4),
    Fruits("Fruits", Icons.Rounded.Favorite, R.drawable.icon_17_row3_col5),
    Drinks("Drinks", Icons.Rounded.Favorite, R.drawable.icon_18_row3_col6),
    TeaCoffee("Tea / Coffee", Icons.Rounded.Favorite, R.drawable.icon_19_row4_col1),
    IceCream("Ice Cream", Icons.Rounded.Favorite, R.drawable.icon_20_row4_col2),

    Water("Water", Icons.Rounded.Favorite, R.drawable.icon_21_row4_col3),
    Fuel("Fuel", Icons.Rounded.Favorite, R.drawable.icon_22_row4_col4),
    Jewellery("Jewellery", Icons.Rounded.Favorite, R.drawable.icon_23_row4_col5),
    Makeup("Makeup", Icons.Rounded.Favorite, R.drawable.icon_24_row4_col6),
    MehendiHaldi("Mehendi / Haldi", Icons.Rounded.Favorite, R.drawable.icon_25_row5_col1),

    InvitationCards("Invitation Cards", Icons.Rounded.CardGiftcard, R.drawable.icon_26_row5_col2),
    Clothes("Clothes", Icons.Rounded.Favorite, R.drawable.icon_27_row5_col3),
    MaleEthnicDress("Male Ethnic Dress", Icons.Rounded.Favorite, R.drawable.icon_28_row5_col4),
    MaleFormalDress("Male Formal Dress", Icons.Rounded.Favorite, R.drawable.icon_29_row5_col5),
    FemaleEthnic("Female Ethnic", Icons.Rounded.Favorite, R.drawable.icon_30_row5_col6),

    Cars("Cars", Icons.Rounded.Favorite, R.drawable.icon_31_row6_col1),
    Flight("Flight", Icons.Rounded.Favorite, R.drawable.icon_32_row6_col2),
    Tickets("Tickets", Icons.Rounded.Favorite, R.drawable.icon_33_row6_col3),
    Furniture("Furniture", Icons.Rounded.Favorite, R.drawable.icon_34_row6_col4),
    Cleaning("Cleaning", Icons.Rounded.Favorite, R.drawable.icon_35_row6_col5),

    PhotoVideography("Photo / Videography", Icons.Rounded.Favorite, R.drawable.icon_36_row6_col6),
    LaundryTailoring("Laundry / Tailoring", Icons.Rounded.Favorite, R.drawable.icon_37_row7_col1),
    Shoes("Shoes", Icons.Rounded.Favorite, R.drawable.icon_38_row7_col2),
    GiftDesign1("Gift - Design 1", Icons.Rounded.CardGiftcard, R.drawable.icon_39_row7_col3),
    GiftDesign2("Gift - Design 2", Icons.Rounded.CardGiftcard, R.drawable.icon_40_row7_col4),

    PlateCutlery("Plate / Cutlery", Icons.Rounded.Favorite, R.drawable.icon_41_row7_col5),
    Shopping("Shopping", Icons.Rounded.Favorite, R.drawable.icon_42_row7_col6),
    DiningTable("Dining Table", Icons.Rounded.Favorite, R.drawable.icon_43_row8_col1),
    DjMusic("DJ / Music", Icons.Rounded.MusicNote, R.drawable.icon_44_row8_col2),
    Decoration("Decoration", Icons.Rounded.LocalFlorist, R.drawable.icon_45_row8_col3),

    Paan("Paan", Icons.Rounded.Favorite, R.drawable.icon_46_paan),
    Panipuri("Panipuri", Icons.Rounded.Favorite, R.drawable.icon_47_panipuri),

    // Legacy enum entries for backward compatibility with database records
    Camera("Photo / Videography", Icons.Rounded.Favorite, R.drawable.icon_36_row6_col6),
    Flower("Flowers", Icons.Rounded.LocalFlorist, R.drawable.icon_03_row1_col3),
    Pandal("Pandal / Stage", Icons.Rounded.Favorite, R.drawable.icon_05_row1_col5),
    Chef("Cook", Icons.Rounded.Favorite, R.drawable.icon_08_row2_col2),
    Decorator("Decoration", Icons.Rounded.LocalFlorist, R.drawable.icon_45_row8_col3),
    Priest("Pandit Ji", Icons.Rounded.Favorite, R.drawable.icon_13_row3_col1),
    Venue("Pandal / Stage", Icons.Rounded.Favorite, R.drawable.icon_05_row1_col5),
    Music("DJ / Music", Icons.Rounded.MusicNote, R.drawable.icon_44_row8_col2),
    Sound("DJ / Music", Icons.Rounded.MusicNote, R.drawable.icon_44_row8_col2),
    Transport("Cars", Icons.Rounded.Favorite, R.drawable.icon_31_row6_col1),
    Clothing("Clothes", Icons.Rounded.Favorite, R.drawable.icon_27_row5_col3),
    Cards("Invitation Cards", Icons.Rounded.CardGiftcard, R.drawable.icon_26_row5_col2),
    Other("Cake", Icons.Rounded.Work, R.drawable.icon_01_row1_col1);

    companion object {
        val pickerEntries: List<BookingIcon> = listOf(
            Cake, Sweets, Flowers, Lighting, PandalStage,
            BandParty, Caterer, Cook, Waiter, VegetablesGroceries,
            Fishes, Meats, PanditJi, RitualGoods, ClayItems,
            HolyLeaves, Fruits, Drinks, TeaCoffee, IceCream,
            Water, Fuel, Jewellery, Makeup, MehendiHaldi,
            InvitationCards, Clothes, MaleEthnicDress, MaleFormalDress, FemaleEthnic,
            Cars, Flight, Tickets, Furniture, Cleaning,
            PhotoVideography, LaundryTailoring, Shoes, GiftDesign1, GiftDesign2,
            PlateCutlery, Shopping, DiningTable, DjMusic, Decoration,
            Paan, Panipuri,
        )
    }
}
