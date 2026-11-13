# My Events — Version 1.0.0 Release Notes 🚀📱

**Release Date:** November 13, 2026  
**Version:** `1.0.0` (Build 1)  
**Target Platform:** Android (Min SDK 29 / Target SDK 37)  
**Binary Artifact:** `app/build/outputs/apk/release/MyEvents.apk`  
**License / Author:** Made with ❤️ by **Sabuj Mondal**

---

## 🌟 Executive Summary

**My Events v1.0.0** is the official first stable release of an **offline-first, privacy-focused Android application** designed for managing event lifecycles, vendor bookings, financial budgets, guest lists, and item checklists. 

Built natively with modern Android architecture (**Jetpack Compose**, **Material 3**, **Room Database v9**, **Hilt Dependency Injection**, and **Firebase Cloud Sync**), My Events delivers a fluid, responsive, and secure experience for organizing weddings, receptions, engagements, birthdays, and community gatherings.

---

## 🎉 Key Highlights & Core Capabilities

### 1. 🔒 Security & App Protection
- **4-Digit PIN Lock Gate**: App lock screen backed by **Android Keystore HMAC-SHA256** hash verification (`PinHasher`).
- **Biometric Authentication**: Seamless fingerprint unlock integration via `androidx.biometric.BiometricPrompt`.
- **Lockout & Safety**: Automated 30-second lockout enforcement after 3 consecutive failed PIN attempts. Cloud backup explicitly restricted via custom extraction rules for maximum local privacy.

### 2. 📊 Home Dashboard
- **7 Summary Overview Cards**:
  - **Upcoming Events**: Interactive list of active upcoming events with days-left countdown pills and navigation affordances.
  - **Booking(s) Made**: Progress tracker for vendor contracts booked.
  - **Booking Advance**: Total advance paid versus total contracted vendor amounts.
  - **Pending Dues**: Real-time total unpaid balance across vendor contracts and expenses.
  - **Pending Item(s)**: Total uncollected checklist items.
  - **Budget vs Spent**: Real-time spending progress bar with dynamic color indicators (Primary < 80%, Warning 80-99%, Danger ≥ 100%).
  - **Guests Overview**: Single-row 3-column summary showing Guests Invited (`X of Y`), Families Invited (`X of Y`), and Physical Cards Required (`X`).

### 3. 📅 Interactive Events Calendar
- **Reels-Style Monthly Calendar Pager**: Vertical snapping calendar pager displaying event dates, category icons, today highlights, and date selection bounds.
- **10 Category Icons**: Two-tone custom XML vector icons (Wedding, Engagement, Reception, Birthday, Haldi, Mehendi, Sangeet, Honeymoon, Anniversary, Puja, Other).
- **Event Details & Sheet Management**: Modal bottom sheet (`AddEditEventSheet`) for creating and editing events with Indian rupee budget formatters and guest invitation toggles.

### 4. 📸 Vendor Bookings & Payment Log
- **47 Custom Vendor Icons**: 5-column grid picker (`BookingIconPickerDialog`) with vendor artwork (Camera/Studio, Catering, Décor, Venue, Music, Priest, Clothing, Transport, etc.).
- **Installment Payment Log**: Log multiple payment installments (Date, Amount, Note) per vendor contract with automatic calculation of remaining due balances.
- **Single-Select Event Selector**: `FlowRow` chip selector for assigning vendor bookings to active events.

### 5. 👥 Guest & Family Management
- **Single-Row Tabular Guest Directory**: Excel-style single-line guest rows (`GuestRowCard`) displaying Guest/Family Name, Event, Guest Count, Invitation Mode (Call/Card), and Status Pill (Invited/Pending).
- **Extra Cards Counter**: Dedicated `ExtraCardsDialog` popup to record physical non-tracked invitation cards with instant total calculation.
- **2-Row Sticky Summary Footer**: Bottom summary footer displaying total family counts, invited families, total guests, physical cards required, and invited guest sums.

### 6. 📋 Item List & Checklist Management
- **Dynamic Task Item Rows**: Create custom item lists per event with dynamic item rows (Item Name, Price in ₹, Checkbox collection toggle, and item removal).
- **Auto-Calculated Totals**: Real-time calculation of total list costs and completion progress.

### 7. 💵 Tabular Expense Audit (Read-Only)
- **Derived Expense Table**: 4-column Excel-style expense overview (`ExpenseRowCard`) automatically consolidated from Bookings and Item Lists.
- **Over-Budget Warning Banner**: Sticky 2-row footer (`SummaryFooter`) displaying total budget, total spent, total paid, and total due, complete with an inline warning banner when spending exceeds budget limits.

### 8. 🎨 Appearance, Cloud Sync & Data Export
- **4 Color Scheme Palettes**: Toxic Violet, Machine Red, Hyper Cobalt, and Dark Aubergine supporting Light, Dark, and System modes with 50-50 sharp color swatches.
- **Firebase Cloud Sync**: Anonymous Firebase Authentication and background Cloud Firestore sync using WorkManager (`SyncWorker`) with exponential back-off and tombstone soft-delete handling.
- **CSV Data Export & Import**: Export event data (Expenses, Bookings, Guests, Item Lists) to UTF-8 BOM CSV files compatible with Excel and File Manager save pickers via `CsvWriter`.

---

## 🏗 Technical Stack & Architecture

| Layer | Architecture Component |
|---|---|
| **Architecture Pattern** | MVVM + Repository Pattern + Clean Architecture UseCases |
| **UI Framework** | Jetpack Compose (Compose BOM), Material 3, Material Icons Extended |
| **Local Database** | Room Database v9 (KSP), `exportSchema = true`, Explicit Migrations (`MIGRATION_8_9`) |
| **Dependency Injection** | Dagger Hilt (`@HiltAndroidApp`, `@HiltViewModel`, `@HiltWorker`) |
| **Background Scheduling**| WorkManager (Periodic SyncWorker & NotificationScheduler) |
| **Preferences & Storage** | Preferences DataStore & EncryptedSharedPreferences (`PinStore`) |
| **Cloud Synchronization** | Firebase Anonymous Auth & Cloud Firestore |
| **Release Minification** | R8 / ProGuard rules enabled (`isMinifyEnabled = true`) |

---

## 🛠 Installation & Deployment

### Installing via ADB
To install the release APK directly onto an Android device connected via USB or Wireless ADB:

```powershell
adb install -r app/build/outputs/apk/release/MyEvents.apk
```

---

## 📜 Version & Commit Details

- **Release Commit Hash**: `14d4bd9` / `d04d4f6` / `6fc9be6`
- **Database Schema**: Version 9 (Room)
- **Documentation**: Merged layout specs in [`design.md`](design.md) and architectural details in [`architecture.md`](architecture.md).

---

*My Events v1.0.0 — Organized events, simplified budgets, effortless celebrations.*
