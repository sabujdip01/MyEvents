# Design Spec: Events Expense Tracker (Android, Jetpack Compose)

Companion to `PRD.md` and `phases.md`. This file defines every screen's layout, sizes, fonts, colours, icons and background doodles, for **light and dark** themes, with a **palette picked in Settings**.

How to read this file:
- Units are **dp** for sizes and **sp** for text. Reference screen: **360 × 800 dp** (scale with weights, not fixed widths).
- Everything is built from reusable **component classes** (section 4), written like CSS classes (`.card`, `.chip`, `.fab`). Screens (section 6) only combine these classes, so a change to a class changes the whole app.
- Colours are always **tokens** (`primary`, `surface`, `success`), never raw hex inside a screen.
- The sample screens you shared are used only for structure. Colours, shapes and illustrations here are our own.

---

## 1. Design principles

1. **Soft and festive, not busy.** Large rounded surfaces, gentle tints, one strong accent per screen.
2. **Numbers first.** Money, counts and days-left are the biggest text on every card.
3. **One layout, many screens.** Cards, tables, tabs, sheets and FABs behave identically everywhere.
4. **Status colour is sacred.** Red, green and amber always mean the same thing, in every palette (section 3.3).
5. **Thumb-friendly.** Primary actions sit in the bottom 40% of the screen; touch targets are at least 48 × 48 dp.

---

## 2. Foundations

### 2.1 Typeface
- **Font family:** Inter (bundled). Fallback: Roboto. Inter has the ₹ glyph and clear tabular numbers.
- **Numerals:** use tabular figures (`fontFeatureSettings = "tnum"`) for every amount, count and table cell so columns line up.
- **Indian grouping:** `₹1,50,000` (format in code, see PRD).

| Style | Size / line | Weight | Letter spacing | Used for |
|---|---|---|---|---|
| `display` | 30 / 36 | 800 | -0.5 | App name on Home header |
| `title-l` | 22 / 28 | 700 | -0.2 | Screen titles, sheet titles |
| `title-m` | 18 / 24 | 700 | 0 | Card titles, dialog titles |
| `title-s` | 16 / 22 | 600 | 0 | Row titles, list item names |
| `body-l` | 16 / 24 | 400 | 0 | Form text, long text |
| `body-m` | 14 / 20 | 400 | 0 | Secondary text, table cells |
| `label-l` | 14 / 20 | 600 | 0.1 | Buttons, chips, tabs |
| `label-m` | 12 / 16 | 600 | 0.3 | Captions, pills, table headers |
| `label-s` | 11 / 14 | 600 | 0.5 | Section overlines (UPPERCASE) |
| `number-xl` | 30 / 34 | 800 | -0.5 | Big card values (₹85,000, 185) |
| `number-l` | 22 / 26 | 800 | -0.3 | Booking card amounts |
| `number-m` | 16 / 20 | 700 | 0 | Table totals |

### 2.2 Spacing, shape, elevation
- **Grid:** 4 dp. Tokens: `xs 4`, `sm 8`, `md 12`, `lg 16`, `xl 20`, `2xl 24`, `3xl 32`.
- **Screen side padding:** 16 dp. **Gap between cards:** 12 dp. **Gap inside a card:** 8 to 12 dp.
- **Corner radius tokens:** `r-chip 12`, `r-field 14`, `r-icon 14`, `r-card 20`, `r-hero 24`, `r-fab 18`, `r-sheet 28` (top corners), `r-dialog 28`, `r-pill 999`.
- **Elevation (light):** cards use a soft shadow `0 6 18 0 primary@8%`; sheets `0 -8 24 0 primary@10%`.
- **Elevation (dark):** no shadows. Instead a 1 dp border `outlineVariant` and a slightly lighter surface for higher layers.
- **Icon sizes:** 20 (inline), 24 (default), 28 (booking card), 32 (feature), 48+ (empty states).

### 2.3 Motion
- Standard: 200 ms ease-out. Sheets and drawer: 300 ms, `FastOutSlowIn`. Check-mark draw: 250 ms.
- Calendar month change: vertical slide 300 ms with 8% fade.
- Number change on Home cards: count-up 400 ms.
- Respect the system "remove animations" setting.

---

## 3. Colours

### 3.1 How palettes work
Settings > Colour scheme offers **4 palettes**. Each palette supplies one *brand* hue and one *soft* hue (from your swatches), and the app builds the full light and dark token set from them. Light/dark is chosen separately (Light, Dark, System).

Every palette defines these tokens (Material 3 names, plus a few extras):

| Token | Meaning |
|---|---|
| `primary` / `onPrimary` | Main brand colour; filled buttons, selected states, key numbers |
| `primaryContainer` / `onPrimaryContainer` | Tinted fills: icon chips, selected tab, tonal buttons |
| `accent` | Secondary hue for progress bars and charts (never red, green or amber) |
| `background` | Screen background |
| `surface` | Cards, sheets, fields |
| `surfaceVariant` | Table headers, keypad keys, inactive chips, segmented track |
| `outline` / `outlineVariant` | Borders / dividers |
| `text` / `text2` / `text3` | Primary / secondary / disabled text |
| `scrim` | Black at 40% behind sheets and dialogs |
| `doodle` | Colour for background blobs and watermarks (primary at low opacity) |

### 3.2 The four palettes

> Note on your swatch images: the swatch labelled "Toxic Violet #FF9030 / Soft Chrome #111827" displays a deep violet and a pale grey, so the printed hex codes do not match the shown colours. This spec uses the **colours as they appear**. The `#111827` code is used as the dark text colour in that palette, and `#FF9030` is **not** used (orange is too close to the amber warning colour).

**Palette A: Toxic Violet** (brand hue about 268°)

| Token | Light | Dark |
|---|---|---|
| primary | `#4A0E8F` | `#B79CFF` |
| onPrimary | `#FFFFFF` | `#2A0A55` |
| primaryContainer | `#E6DAFB` | `#3A1F7A` |
| onPrimaryContainer | `#2A0A55` | `#E6DAFB` |
| accent | `#8B5CF6` | `#A78BFA` |
| background | `#F4F6FB` | `#0E0A1A` |
| surface | `#FFFFFF` | `#171230` |
| surfaceVariant | `#E8EDF3` | `#221B44` |
| outline / outlineVariant | `#C9CFDB` / `#E1E5EE` | `#3A3266` / `#2A2350` |
| text / text2 / text3 | `#111827` / `#4B5563` / `#9CA3AF` | `#F1EEFA` / `#B8B0D4` / `#6F6894` |
| doodle | `#4A0E8F` | `#B79CFF` |

**Palette B: Machine Red** (brand hue about 357°, soft = Glacier Blue)

| Token | Light | Dark |
|---|---|---|
| primary | `#E5202E` | `#FF6B74` |
| onPrimary | `#FFFFFF` | `#4A0006` |
| primaryContainer | `#FFDADC` | `#7A1219` |
| onPrimaryContainer | `#5A0A10` | `#FFDADC` |
| accent | `#2A9FD6` | `#5CC3F0` |
| background | `#F2FBFF` | `#0C1519` |
| surface | `#FFFFFF` | `#141F25` |
| surfaceVariant | `#DBF6FF` | `#1E2E36` |
| outline / outlineVariant | `#B4D7E3` / `#D2EBF3` | `#2F4650` / `#243842` |
| text / text2 / text3 | `#14202B` / `#4A5C69` / `#93A5B0` | `#E8F6FB` / `#9FB8C2` / `#5F7983` |
| doodle | `#E5202E` | `#FF6B74` |

The swatch red `#FF2E3A` is used for illustrations and gradient starts only, because white text on it is below 4.5:1.

**Palette C: Hyper Cobalt** (brand hue about 226°, soft = Powder Sky)

| Token | Light | Dark |
|---|---|---|
| primary | `#0038FF` | `#7C9BFF` |
| onPrimary | `#FFFFFF` | `#001040` |
| primaryContainer | `#D6E3FF` | `#1A34A8` |
| onPrimaryContainer | `#001A66` | `#D6E3FF` |
| accent | `#38BDF8` | `#67D0FA` |
| background | `#F3F7FF` | `#080E22` |
| surface | `#FFFFFF` | `#0F1733` |
| surfaceVariant | `#E3ECFF` | `#19244D` |
| outline / outlineVariant | `#B7C6EA` / `#D6E0F5` | `#2B3A70` / `#1F2C5C` |
| text / text2 / text3 | `#0B1530` / `#4A5878` / `#98A3C0` | `#E8EEFF` / `#A3B2DA` / `#66739E` |
| doodle | `#0038FF` | `#7C9BFF` |

**Palette D: Dark Aubergine** (brand hue about 305°, soft = Electric Pink)

| Token | Light | Dark |
|---|---|---|
| primary | `#9C2A8F` | `#FF7FEC` |
| onPrimary | `#FFFFFF` | `#2E0F35` |
| primaryContainer | `#FFD0F7` | `#5E2268` |
| onPrimaryContainer | `#2E0F35` | `#FFD9F9` |
| accent | `#C04BD6` | `#E08CF0` |
| background | `#FFF6FD` | `#1F0A25` |
| surface | `#FFFFFF` | `#2E0F35` |
| surfaceVariant | `#FFE3FB` | `#3F1B48` |
| outline / outlineVariant | `#E3B7DD` / `#F3D6EF` | `#5A2E66` / `#46224F` |
| text / text2 / text3 | `#2E0F35` / `#6B4A72` / `#B596BB` | `#FCE8FA` / `#D3A9CE` / `#8E6794` |
| doodle | `#9C2A8F` | `#FF7FEC` |

### 3.3 Fixed status colours (identical in every palette)

These never change with the palette.

| Role | Light: main / container / on-container | Dark: main / container / on-container | Used for |
|---|---|---|---|
| **danger** (red) | `#DC2626` / `#FEE2E2` / `#7F1D1D` | `#F87171` / `#3F1214` / `#FECACA` | Delete, errors, over-budget, overdue, "due" amounts |
| **success** (green) | `#16A34A` / `#DCFCE7` / `#14532D` | `#4ADE80` / `#0F3A22` / `#BBF7D0` | Invited check, items received, fully paid, done tasks |
| **warning** (amber) | `#D97706` / `#FEF3C7` / `#78350F` | `#FBBF24` / `#3D2A06` / `#FDE68A` | 80 to 99% of budget, due within 7 days, partial payment |

Rules:
- Status is never shown by colour alone. Pair it with an **icon and a word** (e.g. red pill with a warning icon and "Over by ₹5,000").
- **Machine Red palette caution:** the palette's red is close to danger red. In that palette, danger buttons are always **outlined or tonal with a trash icon and the word "Delete"**, and progress bars use `accent` (blue), never `primary`.
- Gradient hues avoid the amber (30° to 60°) and green (110° to 150°) zones so status colours stay unique.

### 3.4 Card gradients ("same type, different fill")
Every gradient card takes a **slot index** (0 to 5) by its position in a list (event 1, event 2, and so on), so cards of the same type always differ.

Each palette has 6 hues (degrees):

| Palette | Slot 0 | 1 | 2 | 3 | 4 | 5 |
|---|---|---|---|---|---|---|
| A Toxic Violet | 268 | 290 | 248 | 310 | 228 | 200 |
| B Machine Red | 357 | 340 | 12 | 200 | 185 | 320 |
| C Hyper Cobalt | 226 | 204 | 248 | 190 | 270 | 170 |
| D Dark Aubergine | 305 | 330 | 285 | 350 | 265 | 245 |

Gradient formula (HSL, direction 135° top-left to bottom-right):

| Theme | Start | End | Text on card |
|---|---|---|---|
| Light | `hsl(h, 85%, 93%)` | `hsl(h+15, 80%, 85%)` | `text` (dark) |
| Dark | `hsl(h, 45%, 24%)` | `hsl(h+15, 50%, 15%)` | `text` (light) |

- The **icon chip** on a gradient card uses `hsl(h, 70%, 45%)` (light) or `hsl(h, 80%, 70%)` (dark) as background at 20%, with the icon in that full colour.
- The **hero** cards (Upcoming Events, Budget vs Spent) use `surface` with a 1 dp `outlineVariant` border, not a gradient, so they read as the "main" blocks.

### 3.5 Background doodles
- **`.bg-blobs`:** two soft circles behind the top-right of a screen: 240 dp and 150 dp, offset so they overlap (second circle 60 dp left and 70 dp lower). Fill `doodle` at **8%** (light) or **14%** (dark). Blur 0 (flat), partly off-screen.
- **`.bg-watermark`:** a 72 × 72 dp pale line illustration at the bottom-right of a card, `doodle` at 14% (light) / 20% (dark), clipped by the card's radius.
- **Hero illustration** (Home header and drawer): a desk calendar, 3D-looking and softly shaded. Described in section 5.8.

---

## 4. Component classes (reuse everywhere)

Each class lists its size, colours and states. Screens refer to these by name.

### 4.1 Layout shells
| Class | Spec |
|---|---|
| `.screen` | Full size; `background`; `.bg-blobs` at top-right; content padded 16 dp sides; respects status and gesture-bar insets |
| `.screen-scroll` | Vertical scroll inside `.screen`; bottom padding 32 dp (96 dp if the screen has FABs) |
| `.topbar` | Standard top bar (`AppTopBar`); holds `.menu-btn` at left with screen/app title text immediately to its right (12 dp gap, vertically centered) |
| `.menu-btn` | 48 × 48 circle, transparent background, 0.8 dp thin border, 2 dp shadow elevation; icon: 2 centered horizontal lines, 15 dp wide (25% shorter), 3.45 dp stroke (15% thicker) in `primary` |

### 4.2 Cards
| Class | Spec |
|---|---|
| `.card` | `surface`, `r-card` 20, padding 16, 1 dp `outlineVariant` border (dark only), light shadow |
| `.card--hero` | `.card` with `r-hero` 24 and 1 dp `outlineVariant` border in both themes |
| `.card--gradient` | `.card` with the gradient from 3.4 instead of `surface`; includes optional `.bg-watermark` |
| `.card--tap` | Ripple in `primary@12%`; pressed scale 0.98; long-press haptic |
| `.stat` | A `.card--gradient` or `.card` showing: `.icon-chip` (top-left), label (`label-l`, `text2`), value (`number-xl`, `primary`/`text`), caption (`body-m`, `text2`). Min height 128 |

### 4.3 Icon chip
| Class | Spec |
|---|---|
| `.icon-chip` | 44 × 44, `r-icon` 14, fill `primaryContainer`, icon 24 dp `primary` (on gradient cards use the chip colour from 3.4) |
| `.icon-chip--sm` | 36 × 36, radius 12, icon 20 |
| `.icon-chip--lg` | 64 × 64, radius 20, icon 32 |
| `.icon-chip--danger` / `--success` / `--warning` | Fill the status container, icon the status main colour |

### 4.4 Chips, pills, badges
| Class | Spec |
|---|---|
| `.pill` | Height 28, padding 10 horizontal, `r-pill`, `primaryContainer` fill, `onPrimaryContainer` text `label-m`, optional 16 dp leading icon |
| `.pill--success` / `--warning` / `--danger` | Status container fill, status on-container text, leading icon |
| `.chip` | Filter chip: height 36, padding 14, `r-chip` 12, inactive `surfaceVariant` + `text2`; selected `primaryContainer` + `onPrimaryContainer` with a 16 dp check icon |
| `.chip--event` | `.chip` with the event's 18 dp icon at left (used in multi-select events) |
| `.badge` | 18 dp circle count bubble, `primary`, `onPrimary` `label-s` |

### 4.5 Buttons
| Class | Spec |
|---|---|
| `.btn` | Height 52, `r-field` 14, padding 20, `label-l`, min width 96; pressed = ripple |
| `.btn--primary` | Fill `primary`, text `onPrimary` |
| `.btn--tonal` | Fill `primaryContainer`, text `onPrimaryContainer` |
| `.btn--text` | No fill, text `primary` |
| `.btn--danger` | Fill `danger-container`, text and trash icon `danger`; 1 dp danger border in Machine Red palette |
| `.btn--success` | Fill `success-container`, text `success` |
| `.btn-row` | Two buttons side by side, equal width, 12 dp gap |
| `.fab` | 56 × 56 circular `AppFab`, transparent background, 0.8 dp thin border matching `.menu-btn`, 2 dp shadow elevation, `navigationBarsPadding()` applied globally |
| `.fab--sm` | 48 × 48, radius 16, fill `primaryContainer`, icon `onPrimaryContainer` |
| `.fab--danger` | 48 × 48, fill `danger-container`, icon `danger` |
| `.fab-stack` | Vertical stack, right 16, bottom 24 (+ gesture inset), 12 dp gap; order top to bottom: Delete, Edit, Add |

### 4.6 Form fields
| Class | Spec |
|---|---|
| `.field` | Height 56, `r-field` 14, fill `surfaceVariant@60%`, 1 dp `outlineVariant`; focused: 2 dp `primary`; label `label-m` `text2` above value; value `body-l` `text`; error: 2 dp `danger` and 12 sp message below in `danger` |
| `.field--money` | `.field` with a fixed "₹" prefix (`title-s`, `text2`), numeric keypad, grouping as you type |
| `.field--dropdown` | `.field` + chevron 24 right; opens a menu (`surface`, radius 16, max height 280) |
| `.field--readonly` | Same fill, `text` colour, calendar icon, no focus ring |
| `.segmented` | Height 44, track `surfaceVariant`, `r-pill`; selected segment `primary` fill, `onPrimary` text |
| `.checkbox` | 24 × 24, radius 8, 2 dp `outline`; checked: `primary` fill, white check drawn |
| `.checkbox--success` | Checked fill `success` (used for "Invited", "Received") |
| `.switch` | 52 × 32; on: `primary` track, white thumb 24; off: `surfaceVariant` track, `outline` thumb |
| `.item-list` | Dynamic text boxes: stacked `.field` (height 48) with a small trash icon at right; a new blank `.field` appears automatically below when the last one is filled |

### 4.7 Sheets, dialogs, feedback
| Class | Spec |
|---|---|
| `.sheet` | Modal bottom sheet, `surface`, top radius 28, scrim 40%, drag handle 40 × 4 `outline` centred 12 dp from top; padding 20 |
| `.sheet--form` | `.sheet` with max height 92% of screen, scrolling body, sticky `.btn-row` at bottom (Cancel tonal, Save primary) |
| `.sheet--details` | `.sheet` sized to content (event details) |
| `.dialog` | Centred, width 312, `r-dialog` 28, padding 24; top `.icon-chip--lg` (status colour), title `title-m`, body `body-m` `text2`, `.btn-row` at bottom |
| `.snackbar` | Bottom 16, side 16, height 48, radius 14, fill inverse (`text` colour as fill, `background` as text), action "UNDO" in `primary` (light) / `primary` dark variant; shows a 24 dp countdown ring for 10 s |
| `.empty-state` | Centred column: illustration 160 dp, `title-m`, one-line hint `body-m` `text2` (max 2 lines, centred), `.btn--primary` below |
| `.progress` | Height 10, `r-pill`, track `primaryContainer`, bar colour by state (see 6.2) |
| `.divider` | 1 dp `outlineVariant` |

### 4.8 Tabs and tables
| Class | Spec |
|---|---|
| `.tabs` | Horizontally scrolling row, height 48, gap 8, side padding 16; no underline |
| `.tab` | `.pill`-shaped: height 40, padding 14, `r-pill`; inactive: `surfaceVariant`, `text2`; selected: `primaryContainer`, `onPrimaryContainer`, `label-l`; leading 18 dp event icon |
| `.searchbar` | Height 48, `r-field`, `surfaceVariant@60%`, search icon 20 left, hint `body-m` `text3`, clear icon at right when typing |
| `.table` | A `.card` with padding 0, horizontal scroll, sticky first column, rounded clip |
| `.table-head` | Height 44, `surfaceVariant`, `label-m` `text2` UPPERCASE not required |
| `.table-row` | Height 56 (taller if wrapped, max 88), `body-m`, bottom `.divider`; tap = no action; edit mode swaps cells to `.field` underline style |
| `.table-row--readonly` | Tinted `primary@6%`, small lock icon 14 next to the text, pill "Booking" |
| `.table-total` | Height 52, `primaryContainer`, `number-m` `onPrimaryContainer`, top border 1 dp `outline` |
| `.table-select` | Extra first column 48 wide holding a `.checkbox` (delete mode) |

---

## 5. Icons and illustrations

### 5.1 Icon style (all custom SVGs)
- ViewBox 24 × 24. **Line icons**: 2 px stroke, round caps and joins, no sharp corners.
- **Duotone**: stroke in `primary` (or the context colour) plus a fill of the same colour at **18% opacity** on the main shape. Inactive icons are `text2` stroke, no fill.
- Export as Android vector drawables. Material Icons are the fallback when a custom one is missing (names given below).
- Every drawer, card, form and status icon comes from this set so the look is uniform.

### 5.2 Navigation and UI icons
| Name | Custom shape | Material fallback |
|---|---|---|
| `ic-menu` | Two full lines and one half line (the "2.5 lines") | Menu |
| `ic-home` | House with rounded roof, small heart on the door | Home |
| `ic-events` | Calendar page with two rings and four dots | CalendarMonth |
| `ic-bookings` | Clipboard with a tick | AssignmentTurnedIn |
| `ic-expenses` | Wallet with a coin clasp | AccountBalanceWallet |
| `ic-invitees` | Three people (one centre, two behind) | Groups |
| `ic-checklist` | Three rows: dot plus line | Checklist |
| `ic-settings` | Gear with 8 rounded teeth | Settings |
| `ic-budget` | Three bars rising with an arrow | BarChart |
| `ic-dues` | Receipt with a ₹ and a folded corner | ReceiptLong |
| `ic-items` | Box with a small tick | Inventory2 |
| `ic-gift` | Gift box with bow | CardGiftcard |
| `ic-clock` | Circle clock | Schedule |
| `ic-add`, `ic-edit`, `ic-delete`, `ic-check`, `ic-close`, `ic-search`, `ic-chevron`, `ic-lock`, `ic-fingerprint`, `ic-backspace`, `ic-tag` (budget label) | Standard rounded line versions | Add, Edit, Delete, Check, Close, Search, ChevronRight, Lock, Fingerprint, Backspace, Sell |

### 5.3 Event icons (picker, calendar, cards)
| Key | Description | Material fallback |
|---|---|---|
| `ev-engagement` | Two interlocked rings with a small diamond on top | Favorite |
| `ev-wedding` | Phool mala: a drooping garland of five flowers | LocalFlorist |
| `ev-reception` | Two champagne glasses touching | Celebration |
| `ev-honeymoon` | Palm tree on a small island, sun behind | BeachAccess |
| `ev-anniversary` | Heart inside a circle with a ribbon | Favorite |
| `ev-haldi` | Bowl with yellow paste and a leaf | Spa |
| `ev-mehendi` | Open palm with a paisley pattern | PanTool |
| `ev-sangeet` | Dhol drum with sticks | MusicNote |
| `ev-other` | Star with sparkles | Star |

### 5.4 Booking icons
`bk-camera` (camera with a heart flash), `bk-makeup` (lipstick and brush), `bk-flower` (marigold flower), `bk-pandal` (tent with a pennant), `bk-chef` (chef hat), `bk-priest` (diya lamp), `bk-venue` (building with arch), `bk-music` (speaker), `bk-transport` (car), `bk-jewellery` (necklace), `bk-clothing` (saree/hanger), `bk-cards` (envelope with a heart), `bk-other` (briefcase).

### 5.5 Reference SVG (copy as the style template)
Menu (the 2.5 lines):
```svg
<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
  <path d="M4 7h16"/><path d="M4 12h16"/><path d="M4 17h8"/>
</svg>
```
Engagement (rings with diamond):
```svg
<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
  <circle cx="9" cy="15" r="5"/><circle cx="15" cy="15" r="5"/>
  <path d="M12 3l2 2.5-2 2.5-2-2.5z"/>
</svg>
```
Wedding (phool mala garland):
```svg
<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
  <path d="M3 6C5 16 19 16 21 6"/>
  <circle cx="6.4" cy="11.6" r="1.8"/><circle cx="12" cy="13.5" r="1.8"/><circle cx="17.6" cy="11.6" r="1.8"/>
  <circle cx="3" cy="6" r="1.2"/><circle cx="21" cy="6" r="1.2"/>
</svg>
```

### 5.6 Watermark illustrations (one per card type, line style, 72 dp)
- Bookings Done: clipboard with three ticked lines.
- Booking Amount: stack of three coins, slightly offset.
- Budget vs Spent: three rising bars with a curved arrow above.
- Pending Dues: receipt with a ₹ symbol and a tear line.
- Invitees: a large person silhouette with two smaller ones behind.
- Items Pending: clipboard with a bold clip and three lines.

### 5.7 Splash and launcher mark
- Foreground: the desk calendar illustration (5.8) at 112 × 112 dp, centred.
- Background: `primaryContainer` (Android 12 splash icon background) in the active palette; if the palette is not yet known at first launch, use Palette A light.

### 5.8 The desk-calendar illustration (hero)
Drawn in words so any designer can recreate it:
- A **table calendar** viewed slightly from the front-left, standing upright, about 120 × 120 dp on Home and 180 × 180 dp in the drawer and empty states.
- The **top strip** is a darker tone of the brand colour (`primary`), with **three round binder rings** poking out above it, each a small rounded loop in `primary` with a lighter highlight on the left edge.
- The **page** is a rounded white (`surface`) rectangle with a soft inner shadow on the bottom edge. In its centre is a **plump heart** in `primary` with a lighter top-left highlight.
- A **thin side edge** on the right, in a darker shade of `primary`, gives depth.
- Around it: **two leafy sprigs** (five slim leaves each, `accent` at 70%) leaning in from bottom-left and bottom-right, and **two or three small hearts** floating above (`primary` at 60% and 35%, sizes 14 to 22 dp).
- A faint oval **ground shadow** under it (`doodle` at 12%).
- In dark theme use lighter `primary` tones and lower the white page to `primaryContainer`.

---

## 6. Screens

For each screen: structure (ASCII wireframe), then exact elements with size, position, text style, colours and icons. Colours are tokens. "Both themes" is implied since tokens change per theme.

### 6.0 Global navigation behaviour
- `.menu-btn` is always visible at top-left on Home, Events, Bookings, Expenses, Invitees, Checklist, Settings.
- Back from any root screen returns to Home, then exits (after unlock, exiting does not require a PIN again until the app is backgrounded for more than 60 s, proposed).

### 6.1 Splash (about 500 ms)
```
+------------------------+
|                        |
|        [calendar       |
|         heart 120]     |
|                        |
|     My Events          |
|   Made with ❤️ by Sabuj |
|                        |
|   Version X.Y.Z        |
+------------------------+
```
- Background `background`. Logo centred vertically with `"My Events"` title and `"Made with ❤️ by Sabuj"` credit text below it in `primary`.
- Bottom: `"Version ${BuildConfig.APP_VERSION}"` label.
- No progress indicator. Fade out into the unlock or Home screen.

### 6.2 PIN setup and unlock
```
+------------------------+
|                        |
|   Create your PIN      |
|   4 digits to protect  |
|   your data            |
|                        |
|      o  o  o  o        |
|                        |
|    [1]   [2]   [3]    |
|    [4]   [5]   [6]    |
|    [7]   [8]   [9]    |
|    [fp]  [0]   [<-]   |
+------------------------+
```
- `.screen`, `.bg-blobs`. Title `title-l` centred at 18% from top; helper `body-m` `text2` below (max 2 lines).
- **PIN dots:** four circles 16 dp, 20 dp gap, centred. Empty: 2 dp `outline` ring. Filled: `primary`. Error: all turn `danger` and shake 6 dp left-right three times; message under dots in `danger` `body-m`.
- **Keypad:** 3 × 4 grid at the bottom; keys 72 × 72 circle, `surfaceVariant`, digit `number-l`... use 24 sp w600 `text`; gap 16 dp horizontal, 12 vertical; bottom padding 32. Pressed: `primaryContainer`.
- **Bottom-left key:** fingerprint icon 28 dp `primary` (unlock only; empty on setup). **Bottom-right:** backspace 24 dp `text2`.
- **Setup flow:** enter PIN, then "Confirm your PIN" with the same layout; mismatch = error state and restart.
- **Unlock flow:** the system fingerprint prompt appears first; the PIN pad stays underneath as the alternative. Wrong PIN limit is in the PRD; on failure the app closes after a "Too many attempts" `.dialog` (danger chip, one OK button).

### 6.3 Navigation drawer
```
+--------------------+----+
| App Name           |dim |
| Goes Here          |    |
|                    |    |
| [ic] Home      |   |    |
| [ic] Events        |    |
| [ic] Bookings      |    |
| [ic] Expenses      |    |
| [ic] Invitees      |    |
| [ic] Checklist     |    |
| [ic] Settings      |    |
|                    |    |
|   (calendar art)   |    |
+--------------------+----+
```
- Width **82% of screen** (about 296 dp), `surface`, top-right radius 28. Scrim 40% over the rest.
- **Header:** padding 24 dp; "App Name" `title-l` `text`, "Goes Here" `title-l` `primary` on the next line. Top at status bar + 24.
- **Items:** start 32 dp below the header. Each `.drawer-item`: height 56, margin 0 12 dp, radius 20, padding left 12; `.icon-chip--sm`-style 44 × 44 chip at left, label `title-s` at x = 12 + 44 + 16.
  - Inactive: chip `primaryContainer@60%`, icon `primary`, label `text`.
  - **Active:** whole row fill `primaryContainer`, label `primary`, a 4 × 24 dp `primary` bar at the right edge (radius 2).
- Items (top to bottom): `#1 Home`, `#2 Events`, `#3 Booking / Contracts`, `#4 Item Lists`, `#5 Expenses`, `#6 Invitees`, `#7 Settings`. Gap between rows 8 dp.
- **Bottom illustration:** the desk calendar art (5.8) 180 × 180 at the bottom-left, partly overlapping two large `.bg-blobs` circles (200 and 120 dp, `doodle` at 8% / 14%).
- Opens with a 300 ms slide; swipe from the left edge or tap `.menu-btn`.

### 6.4 Home
```
+------------------------------+
| (=)                (blob) .  |
|                       [cal]  |
| App Name              [art]  |
| Goes Here                    |
| +--------------------------+ |
| | [ic] Upcoming Events 2ev | |
| | ---------------------    | |
| | [ic] Engagement 12d  >   | |
| | [ic] Wedding   120d  >   | |
| +--------------------------+ |
| +-----------+ +-----------+  |
| | Bookings  | | Booking   |  |
| | Done      | | Amount    |  |
| | 4 of 7    | | Rs 85,000 |  |
| +-----------+ +-----------+  |
| +--------------------------+ |
| | Budget vs Spent  [bars]  | |
| | Rs 1,20,000 spent        | |
| | [=====-----------]  40%  | |
| +--------------------------+ |
| +-----------+ +-----------+  |
| | Pending   | | Invitees  |  |
| | Dues      | | 185       |  |
| +-----------+ +-----------+  |
| +--------------------------+ |
| | Items Pending   8        | |
| +--------------------------+ |
+------------------------------+
```
**Header (height 220 dp including status bar):**
- `.menu-btn` top-left.
- "App Name" / "Goes Here" at left 16, baseline 56 dp above the bottom of the header: `display` 30/36, line 1 `text`, line 2 `primary`.
- Hero illustration 128 × 128 dp at the right, top = status bar + 24, partly over `.bg-blobs`.

**Cards (all `.card--tap`; long-press opens the matching page in edit mode):**

1. **Upcoming Events** (`.card--hero`, full width)
   - Header row (height 44): `.icon-chip` with `ic-events`; title "Upcoming Events" `title-m`; right `.pill` "2 events".
   - `.divider` 12 dp below the header.
   - **Event rows** (height 64 each, gap 8, radius 16, fill `surfaceVariant@50%`, padding 12): left `.icon-chip--sm` with the event's icon; two text lines: event name `title-s` `text`, date "15 Dec 2024, Sun" `body-m` `text2`; right `.pill` with `ic-clock` and "12 days left" (becomes `.pill--warning` when 7 days or fewer, `.pill--danger` and "Today" or "Passed" on or after the date); a 20 dp chevron at far right.
   - This is the PRD's Event Dates card: one row per event, date as the main line, days left as the secondary pill. Tap or long-press opens Events.
2. **Bookings Done** (`.stat`, `.card--gradient`, half width, slot = event index)
   - `.icon-chip` with `ic-bookings`; label "Bookings Done" `label-l`; value "4 of 7" `number-xl` (the "of 7" part is `title-m`, `text2`); caption "bookings" `body-m` `text2`; watermark clipboard at bottom-right.
   - One card per event when there are several events (caption shows the event name with its small icon); grid fills two per row.
3. **Booking Amount** (`.stat`, half width)
   - Label "Booking Amount"; value "₹85,000" `number-xl`; caption "paid" and "of ₹1,50,000" on a second line (`body-m`, `text2`); coin-stack watermark.
4. **Budget vs Spent** (`.card--hero`, full width, height 140)
   - `.icon-chip` `ic-budget` at left; title `title-m`; value "₹1,20,000" `number-xl` `text` with the word "spent" `body-m` `text2` at its baseline; below it "₹3,00,000 budget" `body-m` `text2`.
   - `.progress` full width at 16 dp from the bottom; percentage label `label-l` right-aligned above the bar's right end.
   - **Bar colour states:** 0 to 79% `accent`; 80 to 99% `warning` with `ic-warning` and "Almost at budget"; 100% or more `danger` with "Over by ₹X" (bar full, striped overflow). Chart watermark at right.
   - Long-press opens Events (budgets are set per event).
5. **Pending Dues** (`.stat`, half width): value "₹32,500" in `danger` when above 0, `success` "₹0" with `ic-check` when 0; caption "unpaid"; receipt watermark. Long-press opens Bookings.
6. **Invitees** (`.stat`, half width): value "185" `number-xl`, caption "guests", second caption "42 families"; people watermark. Long-press opens Invitees.
7. **Items Pending** (`.stat`, full width, height 112): value "8" `number-xl`, caption "items left"; clipboard watermark. Long-press opens Expenses.

**Empty state (all zeros):** values show "0" or "—"; the Upcoming Events card shows `.empty-state` compact version (icon `ic-events` 48, text "No events yet", `.btn--tonal` "Add event" that opens Events). Dismissable banner under the header: "Try with sample data" (`.pill`-style card with `ic-gift`, `.btn--text`).

Spacing: 12 dp between cards, 16 dp sides, 32 dp bottom padding.

### 6.5 Events (calendar)
```
+------------------------------+
| (=)  Events     [cal April v]|
| +--------------------------+ |
| | Sun Mon Tue Wed Thu Fri  | |
| | [ ][ ][1][2][3 *][4][5]  | |
| | [6*][7]...               | |
| |  ...  [16 sel] ...       | |
| +--------------------------+ |
|  ======= (bottom sheet) =====|
|  [icon] Mom's Birthday       |
|  [Invitees 12] [Bookings 4]  |
|  Event Name | Date | Budget  |
|  [Edit Event] [Delete Event] |
+------------------------------+
```
**Top row:** `.menu-btn`; title "Events" `title-l` next to it; right **month selector** pill: height 40, padding 14, `r-pill`, `primaryContainer`, `ic-events` 20 + "April 2025" `label-l` + chevron-down 20, all `onPrimaryContainer`. Tapping it opens a month and year picker (`.sheet` with a 3 × 4 month grid and year stepper).

**Calendar card (`.card`, `r-hero`):** padding 12.
- Weekday row: 7 equal columns, `label-m` `text2`, height 32.
- Date grid: 7 columns, up to 6 rows, gap 6 dp. Cell width is flexible (about 38 dp), **cell height 52**, radius 14.
- **Cell content (top to bottom, centred):** day number `label-l` (13 sp) at top 6 dp; event icon 18 dp below it; a 4 dp dot in `primary` at 6 dp from the bottom (only when an event exists).
- **Cell fills:** empty day `surfaceVariant@55%`; day outside the month `text3` text on `surfaceVariant@25%`; **today** adds a 1.5 dp `primary` outline; **event day** `surface` fill with `primary` icon and dot; **selected** day fills `primary` with `onPrimary` number and white icon (dot hidden).
- **Reels-style scrolling:** swipe vertically to the next or previous month. Each month is a full page that snaps; the page behind peeks 8 dp. The month pill updates as you scroll.
- **Gestures:** tap an empty day: nothing. Tap an event day: opens the details sheet. **Long-press** any day: empty opens the Add Event sheet with that date; a day with an event opens that event's details (one event per day).

**Event details (`.sheet--details`)**
- Handle, then header row: `.icon-chip--lg` (64) with the event's icon at left; at right of it the event title `title-l` and the date "Wed, 16 Apr 2025" `body-m` `text2`.
- Two `.card--gradient` stat tiles side by side (gap 12, height 84, padding 12, slot 0 and 1): left tile `ic-invitees` chip, "Invitees" `label-m`, value "12" `number-xl` with "people" `body-m`; right tile `ic-bookings` chip, "Bookings", "4" with "bookings". The tiles show "Not required" `text2` with a dash if Invitees required is No.
- **Detail list (`.card`, rows 52 high, `.divider` between):** each row has an `.icon-chip--sm` at left, a label `body-m` `text2`, and the value right-aligned `title-s` `text`. Rows: Event Name, Date, Budget (₹), Invitees Required (Yes/No).
- `.btn-row` at the bottom: `Edit Event` (`.btn--tonal`, `ic-edit`) and `Delete Event` (`.btn--danger`, `ic-delete`).

**Add / Edit Event (`.sheet--form`)**
- Title "Add event" / "Edit event" `title-l`.
- Fields in order (16 dp apart): Title (`.field`), Event name (`.field--dropdown` with suggestions: Engagement, Wedding, Reception, Honeymoon, Anniversary, Haldi, Mehendi, Sangeet, Other), Date (`.field--readonly` with `ic-events`), **Icon picker** (label `label-m`; a grid of 5 columns, each 52 × 52 `.icon-chip` with the icon; selected has a 2 dp `primary` ring and `primaryContainer` fill; rows scroll if more than 10), Budget (`.field--money`), Invitees required (`.segmented` Yes/No).
- Sticky `.btn-row`: Cancel (`.btn--tonal`) and Add (`.btn--primary`). Add is disabled until title, event name, date and icon exist. If the date already has an event, show an inline `.pill--warning` "This date already has an event".

**Delete confirm (`.dialog`)**
- `.icon-chip--lg` danger with `ic-delete`; title "Delete event?"; body: "This will also delete 4 bookings, 12 expenses and 85 invitees." with the counts bold; `.btn-row`: Cancel (`.btn--tonal`), Delete (`.btn--danger`).
- After deletion a `.snackbar` "Event deleted · UNDO" with a 10 s countdown ring.

### 6.6 Bookings
```
+------------------------------+
| (=)  Bookings                |
|                              |
| +--------------------------+ |
| | Photographer             | |
| | [ev] Rs 25,000 of 40,000 [bk] |
| |      [=====-----] Due 12 Dec  |
| +--------------------------+ |
| (more cards)            [+]  |
+------------------------------+
```
- **Screen:** `.screen-scroll` with `.topbar`; title "Bookings" `title-l`. Optional chip row under the title: `.chip` per event (All, Engagement, Wedding) height 36, filters the list.
- **Booking card (`.card--gradient`, `.card--tap`, full width, height 148, slot = index):**
  - **Title band** (top, height 32): title (e.g. "Photographer") `title-s` `text`, single line, padding 16. (This is the PRD's "top 10%"; it is 22% of the card height so the text stays readable. To make it exact, raise the card height to 280, which is not recommended.)
  - **Body** (remaining 116): three columns by width: left **15%** holds the event icon in an `.icon-chip` (44), vertically centred; middle **75%** holds the money block; right **10%** holds the booking-type icon, 28 dp, `primary`, aligned to the top.
  - **Money block:** `number-l` "₹25,000" in `text`, followed by "of ₹40,000" `body-m` `text2`; below it a `.progress` (height 8, paid ÷ total, `accent`; turns `success` at 100%); below the bar a row with the contact name `body-m` `text2` at left and a status pill at right: `.pill--success` "Paid", `.pill--warning` "Due 12 Dec" (due within 7 days or part paid), `.pill--danger` "Overdue" (past due date), neutral `.pill` "₹15,000 left".
- **Empty:** `.empty-state` with the calendar art, "No bookings yet", hint "Add your photographer, caterer and more", `.btn--primary` "Add booking".
- **FAB:** one `.fab` (`ic-add`) bottom-right; label on long-press tooltip "Add Booking/Expense".
- **Edit mode (long-press a card):** all cards gently wobble 1.5° and each gets a 32 dp `.icon-chip--danger` circle with `ic-delete` at its top-right corner (8 dp inside); the FAB becomes `.fab` with `ic-check` (Done). Tapping the trash opens the delete `.dialog` ("Delete booking? Its payments will be removed too."). Tapping a card in this mode opens the booking sheet for editing.

**Add / Edit Booking (`.sheet--form`)**
- Fields in order: Title, Booking name (`.field--dropdown` with suggestions Camera, Makeup, Flowers, Pandal, Catering, Priest, Venue, Music, Other), **Booking icon picker** (5-column grid, same as the event icon picker, using booking icons), For event (`.field--dropdown` where each item shows the event's icon chip 32 and name), Contact person, Phone number (numeric, 10 digits, `ic-phone` prefix), Total amount (`.field--money`), **Paid so far** (read-only `.field--money`, equals the payment log sum), **Amount left** (read-only, `text` coloured `danger` if above 0, `success` if 0), Next due date (`.field--readonly` calendar).
- **Payment log** (section label `label-s` "PAYMENTS" in `primary`): rows height 52 with date `body-m`, amount `title-s`, note `body-m` `text2`, and a 20 dp trash at right; a `.btn--text` "Add payment" opens a small `.dialog` with Date, Amount, Note.
- Sticky `.btn-row`: Cancel, Save.

### 6.7 Expenses
```
+----------------------------------------------+
| (=)  Expenses                                |
| [All Events][Engagement][Wedding ▶]          |
| [search................................]      |
| (All)(Bookings)(Item Lists)(Due)(Paid)        |
|                                              |
| [icon] Photography (Booking)   ₹25,000  ₹18k  ₹7k  [Due]  |
|        Wedding                           paid   due         |
| [icon] Flowers (Item List)     ₹8,000   ₹8,000      [Paid] |
|        Engagement                        paid               |
|                                              |
| Total items 2    ₹33,000  ₹26,000  ₹7,000   |
| Budget ₹50,000     Total    Total    Total   |
|                             paid     due     |
+----------------------------------------------+
```
- `.topbar` with title "Expenses". Under it: `.tabs` (one pill per event with event icon + total amount, "All" pill first; `primaryContainer` when selected). Then `.searchbar` (height 48, `Radius.field`). Then a horizontally scrolling chip row: **All · Bookings · Item Lists · Due · Paid** (single-select, `primaryContainer` when active).
- **Expense cards (`.expense-row-card`)** — full-width (`fillMaxWidth`, no outer padding, same as booking cards), `surface` background, 0.8 dp `outlineVariant` border, `shadowElevation = 1 dp`, duotone gradient overlay, `padding(vertical = 4 dp, horizontal = 4 dp)` inner. Four relative-weight columns:
  - **9% Icon col** (`CenterStart`): 32 dp circle `primaryContainer` background containing the actual `BookingIcon` chosen for the booking/task (PNG via `Image` if `customRes != null`, else vector `fallback`); direct expenses show generic category icon.
  - **35% Title col**: Booking/task title (`labelL`, `text`) on line 1; 1 dp spacer; event name (`bodyM` 12 sp, `text2`) on line 2; both `maxLines = 1, overflow = Ellipsis`.
  - **44% Amounts col** (3 equal sub-cols, right-aligned):
    - **Total**: amount (`bodyM` 17.5 sp Bold, `text`)
    - **Paid**: amount (`bodyM` 13 sp SemiBold, `onSuccessContainer`) + 1 dp spacer + "paid" label (`bodyM` 12.5 sp, `onSuccessContainer`); blank when paid = 0
    - **Due**: amount (`bodyM` 13 sp SemiBold, `onWarningContainer`) + 1 dp spacer + "due" label (`bodyM` 12.5 sp, `onWarningContainer`); `padding(end = 8 dp)` shifts column slightly left; blank when due = 0
  - **12% Pill col** (`CenterEnd`): `Pill(Success)` "Paid" or `Pill(Warning)` "Due"
- **Summary footer (`.summary-footer`)** — sticky at screen bottom above Android nav bar (`navigationBarsPadding + padding(bottom = 6 dp)`), full-width `primaryContainer` Surface with `Radius.field`. Two rows, same 4-column weight structure as card (9%+35% = 44% | 44% | 12%):
  - **Row 1 (values):** "Total items N" (`labelL Bold`, `onPrimaryContainer`) spanning 44% | Total sum (`labelL Bold`) · Paid sum (`labelL`, `onSuccessContainer`) · Due sum (`labelL`, `onWarningContainer`) in 44% | 12% spacer. Due value has `padding(end = 8 dp)`.
  - **4 dp spacer** between rows.
  - **Row 2 (labels):** "Budget ₹X" (`labelL`, `onPrimaryContainer`) spanning 44% | "Total" · "Total paid" · "Total due" (`bodyM` 12.5 sp) in 44% | 12% spacer. Colors: Total = `text2`, Paid = `onSuccessContainer`, Due = `onWarningContainer`.
  - Over-budget warning: "⚠️ Over Budget by ₹X" (`titleS`, `danger`) appears above Row 1 when `grandTotal > budget`.
- **Empty state:** `titleL` "No expenses found" centred; shown when filtered list is empty.
- **Read-only cards:** Bookings → `sourceIcon = booking.bookingIcon`; Item Lists → `sourceIcon = task.icon`; Direct expenses → `sourceIcon = null` (category icon fallback).
- **No FABs / edit / delete modes** on this screen — all data is read-only, sourced from Bookings and Item Lists.

### 6.8 Guests (Invitees)
```
+-----------------------------------------------------------+
| (=)  Guests                                [🃏 10]  [Add] |
| [All (185)][Engagement (45)][Wedding (140)]               |
| [search guest or address................................] |
| (All)(Invited)(Not invited)                               |
| +-------------------------------------------------------+ |
| | Sharma Family  | Engagement |  4  | ✉ | [Invited]     | |
| | Verma Uncle    | Engagement |  2  | 📞| [Pending]     | |
| +-------------------------------------------------------+ |
| | 38 Families | 30 Invited families | 185 Guests | 42 Cards | 120 Invited guests |
+-----------------------------------------------------------+
```
- **Top bar:** Title "Guests". Actions: **Extra Cards icon** (`ic-mail` with small `extraCards` count badge text underneath if > 0, blank if 0; tap opens `ExtraCardsDialog`) + **"Add" text button** (tap opens `AddEditInviteeSheet`).
- **Event filter tabs:** One tab per event where `inviteesRequired = true`. Each tab shows `Event name (N)` where N = total expected guest count for that event. "All (Total)" tab at the left sums all events.
- **Search bar:** `OutlinedTextField` filtering guest name or address.
- **Filter chips row:** All / Invited / Not invited (single-select).

#### Tabular Excel-style Guest Row Card (`GuestRowCard`)
Single-line full-width card with 5 weight-based columns (`horizontal = Spacing.xs, vertical = 4 dp`):

| Column | Weight | Content & Alignment |
|---|---|---|
| **Col 1: Guest Name** | 0.26 | Guest family/person name (`labelL`, `text`, left-aligned, 4 dp start padding) |
| **Col 2: Event Name** | 0.22 | Event title (`bodyM` 12 sp, `text2`, left-aligned) |
| **Col 3: Guest Count** | 0.14 | Expected guests number (`bodyM` 13 sp SemiBold, right-aligned, 20 dp end padding inset) |
| **Col 4: Invite Via** | 0.14 | Call icon (`ic-phone`), Card icon (`ic-mail`), or blank cell for None (center-aligned) |
| **Col 5: Status Pill** | 0.20 | `Pill(Success)` = "Invited"; `Pill(Warning)` = "Pending" (right-aligned, `CenterEnd`) |

- **Tap card** → Opens `AddEditInviteeSheet` for editing.
- **Long-press card** → Triggers `AppDeleteDialog` to confirm single-guest deletion.

#### Sticky Single-Line Summary Footer (`GuestSummaryFooter`)
Sticky full-width `primaryContainer` card positioned above the Android navigation bar (`navigationBarsPadding + padding(bottom = 6 dp)`). Layout is a **single horizontal line** matching card column alignment:

| Column | Weight | Content & Alignment |
|---|---|---|
| **Col 1** | 0.22 | `"<Count of Rows> Families"` (e.g. "38 Families", left-aligned, 2 dp start inset) |
| **Col 2** | 0.26 | `"<Count of Rows where invited> Invited families"` (e.g. "30 Invited families", left-aligned) |
| **Col 3** | 0.15 | `"<Sum of Guests> Guests"` (e.g. "185 Guests", right-aligned, 20 dp end padding inset) |
| **Col 4** | 0.13 | `"<Count where invitation through = card> Cards"` (e.g. "42 Cards", center-aligned) |
| **Col 5** | 0.24 | `"<Sum of Guests where invited> Invited guests"` (e.g. "120 Invited guests", right-aligned, `CenterEnd`) |

- Font: `bodyM` 10 sp SemiBold, `onPrimaryContainer` color.

#### Extra Cards popup (`ExtraCardsDialog`)
- `AlertDialog` with title "Extra invitation cards".
- Body: `OutlinedTextField` for entering number of extra (non-tracked) physical invitation cards.
- Layout: Exactly **12 dp whitespace** before and after `AppButtonRow` (Cancel / Save).
- Persisted to DataStore `extra_invitation_cards`.

#### Add / Edit Guest Sheet (`AddEditInviteeSheet`)
- Fields: Guest Name, Event Selection (single-select event choice), Expected Guest Count (stepper/number), Address, Invited toggle, Invitation Type chips (None / Call / Card), Note.
- Validation: Name required.

#### InvitationType enum
`None` (default) · `Call` (verbal/phone) · `Card` (physical card, counted in total Cards).

### 6.9 Checklist
```
+------------------------------+
| (=)  Checklist               |
| [Engagement][Wedding]        |
| [ ] Finalise venue    12 Dec |
| [x] Book DJ                  |
|                        [ + ] |
+------------------------------+
```
- `.topbar`, then `.tabs` per event. A `.card` with progress: "5 of 12 done" `title-s`, `.progress` below (`success` bar).
- **Task row** (`.card`, height 64, margin 8): `.checkbox--success` 24 at left (16 dp from edge); title `title-s` `text`; due date at right as a `.pill`: neutral (`text2`) with `ic-clock`; `.pill--warning` if due in 3 days or fewer; `.pill--danger` if overdue. Done tasks: title strikethrough in `text3`, pill removed.
- Swipe left on a row reveals a `danger-container` area with `ic-delete` (72 wide); tap to edit opens a small `.sheet--form` with Title and optional Due date.
- `.fab` `ic-add` bottom-right; empty state with `ic-checklist`.

### 6.10 Settings
```
+------------------------------+
| (=)  Settings                |
| APPEARANCE                   |
| +--------------------------+ |
| | [ Light | Dark | System ]| |
| | Colour scheme            | |
| | (A)(B)(C)(D)             | |
| +--------------------------+ |
| NOTIFICATIONS                |
| ... switches ...             |
| SECURITY                     |
| Change PIN > | Fingerprint o |
| DATA                         |
| Load sample data             |
| ABOUT   Version 1.0.0        |
+------------------------------+
```
- Section label: `label-s` UPPERCASE, `primary`, letter-spacing 0.8, margin 24 top and 8 bottom, left padding 8.
- Section headers: font size increased by 40% (15 sp bold uppercase). Item titles: font size decreased by 20% (14 sp).
- **Appearance:** a `.segmented` for Light / Dark / System. Below it "Colour scheme": a row of 4 circular swatches (56 dp): 50%-50% split with 135 degree linear gradient (brand primary top-left, soft primaryContainer bottom-right). Selected swatch has 2.5 dp primary ring and checkmark badge.
- **Security:** Change PIN (title color `AppColor.text`), Fingerprint unlock (`.switch`).
- **Sync and Export:** Last synced on timestamp, "Sync Now" button (triggers Firebase background sync worker, periodic 1 hr schedule), "Export Data (CSV)" button.
- **About:** Version row with the version number right-aligned `body-m` `text2`.

### 6.11 Cross-screen feedback
- **Snackbar:** see `.snackbar`; always above FABs (bottom margin 96 dp when a FAB stack is present).
- **Validation:** red `.field` state plus message; a shake of 6 dp on the Save button when pressed with errors.
- **Loading:** sync never blocks; a 2 dp `accent` linear indicator appears under the `.topbar` while syncing, hidden otherwise.

### 6.12 Home-screen widget
- **2 × 2:** `surface` card, radius 24; `.icon-chip--sm` with the event icon top-left; the event name `label-m`; "12" `number-xl` `primary` with "days left" `label-m` `text2`.
- **4 × 2:** adds a right half with "Spent ₹1,20,000" `title-s` and `.progress` (40%), "of ₹3,00,000" `label-m` `text2`.
- No guest names or vendor details. Tapping opens the unlock screen.

### 6.13 Notifications (system tray)
- Small icon: monochrome `ic-events`. Accent colour = palette `primary`. Title `title-s`, body one line; one channel per type (6). Budget alerts use the warning wording (80%) and danger wording (100%) in the text, never relying on colour.

---

## 7. Theme behaviour summary

| Area | Light | Dark |
|---|---|---|
| Background | palette `background` (very pale tint) | palette `background` (deep tinted dark, never pure black) |
| Cards | white with soft shadow | `surface` with 1 dp border, no shadow |
| Gradient cards | pale tints with dark text | deep tints with light text |
| Doodles | `doodle` at 8% | `doodle` at 14% |
| Status colours | main colours on pale containers | lighter main colours on deep containers |
| Illustrations | full palette colours | lighter `primary`, page shown in `primaryContainer` |
| Status bar | dark icons | light icons; both transparent over `.screen` |

Switching Light, Dark, System and palette must not restart the app and must preserve scroll positions.

---

## 8. Accessibility checklist
- Text contrast at least **4.5:1** for body text and **3:1** for icons and large numbers, in all 4 palettes and both themes. Check `onPrimary` on `primary` and text on every gradient slot.
- Minimum touch target 48 × 48 dp (the 36 and 40 dp chips have invisible padding to 48).
- Support font scaling up to 200%: cards grow in height, tables keep their column widths and scroll.
- Content descriptions on every icon-only button (menu, FABs, delete icons, checkmarks).
- Status is never colour only (section 3.3).
- Haptics: light tick on toggles, medium on long-press and delete.

---

## 9. Compose implementation notes
- Build one `AppColors` data class with the tokens in 3.1 plus `success`, `warning`, `danger` (each main/container/onContainer) and expose it through a `CompositionLocal`.
- `PaletteId { A, B, C, D }` and `ThemeMode { LIGHT, DARK, SYSTEM }` live in DataStore; the root composable picks `AppColors` from them.
- Create reusable composables named after the classes: `AppCard`, `GradientCard(slot)`, `IconChip`, `Pill`, `AppChip`, `AppButton(variant)`, `AppFab`, `FabStack`, `AppField`, `MoneyField`, `Segmented`, `AppSheet`, `AppDialog`, `AppSnackbar`, `EmptyState`, `AppTabs`, `DataTable`. Screens must not hardcode sizes or colours.
- `gradientFor(slot, palette, isDark)` implements section 3.4 with `Color.hsl(...)`.

---

## 10. Notes and open points
1. **Checklist in the drawer:** your sample drawer lists Checklist as a navigation item, so this spec treats it as a seventh drawer item (this resolves the PRD's open decision on placement). `PRD.md` and `phases.md` still say six drawer items; update them if you agree.
2. **Home events:** the sample groups events into one "Upcoming Events" list card. This spec does the same, with date as the title and days left as the pill. Bookings Done is per event as in the PRD.
3. **Bookings card title band:** 22% of height instead of 10% for legibility (section 6.6).
4. **Machine Red palette** needs the extra danger-button rule in 3.3.
5. **Swatch labels** in your image do not match the displayed colours (section 3.2); confirm the exact hex codes if you want the original values.
6. Final icon artwork and the calendar illustration are to be drawn from the descriptions above.
