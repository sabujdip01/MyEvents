# Architecture: Events Expense Tracker (Android)

How the app is built. Requirements live in `PRD.md`, visuals in `design.md`, build order in `phases.md`, agent rules in `Gemini.md`.

---

## 1. Principles

1. **Offline-first.** Room is the single source of truth. The UI never reads from the network.
2. **Single module, layered packages.** UI, domain and data are separate packages with a one-way dependency rule.
3. **Unidirectional data flow.** `ViewModel` exposes one `UiState` as a `StateFlow`; the UI sends events back up.
4. **Reuse first.** Shared UI lives in `core/ui`; features contain only screen-specific code.
5. **Tokens, not values.** Colours, sizes and strings come from theme tokens and resources.

## 2. Tech stack

| Area | Choice |
|---|---|
| Language | Kotlin (latest stable), coroutines and Flow |
| UI | Jetpack Compose, Material 3, Compose BOM |
| Min / target SDK | 29 / latest stable |
| Navigation | Navigation Compose, single `Activity` |
| Local database | Room (KSP), `exportSchema = true` |
| Dependency injection | Hilt |
| Preferences | DataStore (settings), EncryptedSharedPreferences (PIN data) |
| Background work | WorkManager |
| Cloud sync | Firebase Auth (anonymous) + Cloud Firestore |
| Biometrics | `androidx.biometric` (BiometricPrompt) |
| Splash | `androidx.core:core-splashscreen` |
| Widget | Jetpack Glance |
| Dates | `java.time` (available from API 26, so safe on API 29) |
| Build | Gradle Kotlin DSL, version catalog (`libs.versions.toml`) |
| Maps | Not used |

## 3. Layers and dependency rule

```
  UI (feature/*, core/ui)        Compose screens, ViewModels, UiState
        |  depends on
        v
  Domain (domain/*)              models, use cases, pure Kotlin
        |  depends on
        v
  Data (data/*)                  Room, repositories, Firestore, DataStore
```

- UI may use domain; domain never imports Android UI or data implementations (it sees repository **interfaces**).
- Data implements the interfaces defined in domain and maps entities to domain models.
- `core/` holds shared building blocks (theme, components, utilities) and may be used by any layer except domain (domain stays pure).

Data flow for a typical action:

```
Screen -> ViewModel.onEvent -> UseCase -> Repository -> Room DAO
                                                         |
Room Flow --------------> Repository --> UseCase --> ViewModel.uiState --> Screen
SyncWorker (background) pushes pending Room rows to Firestore
```

## 4. Project structure

Package root: `sabuj.m.myevents`.

```
MyEvents/
├── .gitignore
├── .bin/                         # agent recycle bin (git-ignored)
├── Gemini.md
├── PRD.md
├── architecture.md
├── design.md
├── phases.md
├── version.md
├── testcases.md
├── local.properties.example      # fake values; real local.properties is git-ignored
├── build.gradle.kts
├── settings.gradle.kts
├── gradle/
│   └── libs.versions.toml        # version catalog
├── design/
│   └── svg/                      # source SVG files
└── app/
    ├── build.gradle.kts          # reads version.md -> BuildConfig.APP_VERSION
    ├── proguard-rules.pro
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml
        │   ├── java/sabuj/m/myevents/
        │   │   ├── MyEventsApp.kt              # @HiltAndroidApp, channels, WorkManager setup
        │   │   ├── MainActivity.kt             # splash, lock gate, root NavHost
        │   │   │
        │   │   ├── core/
        │   │   │   ├── theme/
        │   │   │   │   ├── AppColors.kt        # token data class + LocalAppColors
        │   │   │   │   ├── Palettes.kt         # 4 palettes x light/dark
        │   │   │   │   ├── Gradients.kt        # gradientFor(slot, palette, dark)
        │   │   │   │   ├── Type.kt             # Inter text styles
        │   │   │   │   ├── Shapes.kt           # radius tokens
        │   │   │   │   ├── Spacing.kt          # 4 dp grid tokens
        │   │   │   │   └── AppTheme.kt         # theme entry point
        │   │   │   ├── ui/                     # reusable components
        │   │   │   │   ├── AppButton.kt, AppFab.kt, AppField.kt, IconChip.kt, Pill.kt
        │   │   │   │   ├── AppSheet.kt, AppDialog.kt, AppDeleteDialog.kt, AppSnackbar.kt
        │   │   │   │   ├── SearchBar.kt, Segmented.kt, MenuButton.kt, ScreenScaffold.kt
        │   │   │   │   └── doodles/            # BgBlobs.kt, HeroIllustration.kt
        │   │   │   ├── icons/                  # AppIcons.kt, EventCategoryIcon.kt, EventIcon.kt, BookingIcon.kt
        │   │   │   ├── preview/                # LightDarkPreviews.kt, PreviewData.kt
        │   │   │   └── util/                   # MoneyFormatter.kt, DateUtils.kt, CsvWriter.kt, CsvReader.kt, Extensions.kt
        │   │   │
        │   │   ├── domain/
        │   │   │   ├── model/                  # Event, Booking, BookingPayment, Expense, Invitee, Task, AppSettings
        │   │   │   ├── repository/             # interfaces: EventRepository, BookingRepository, ExpenseRepository, InviteeRepository, TaskRepository
        │   │   │   └── usecase/                # ObserveHomeSummaryUseCase, AddEventUseCase, AddBookingUseCase, ...
        │   │   │
        │   │   ├── data/
        │   │   │   ├── db/
        │   │   │   │   ├── AppDatabase.kt
        │   │   │   │   ├── entity/             # EventEntity, BookingEntity, BookingPaymentEntity, ExpenseEntity, InviteeEntity, TaskEntity
        │   │   │   │   ├── dao/                # EventDao, BookingDao, ExpenseDao, InviteeDao, TaskDao
        │   │   │   │   └── Converters.kt       # LocalDate, items list, enums
        │   │   │   ├── repository/             # *RepositoryImpl.kt, mappers
        │   │   │   ├── remote/                 # FirestoreSyncRepository.kt, RemoteMappers.kt, FirebaseAuthManager.kt
        │   │   │   ├── settings/               # SettingsDataStore.kt
        │   │   │   ├── security/               # PinStore.kt, PinHasher.kt, BiometricHelper.kt
        │   │   │   └── worker/                 # SyncWorker.kt, NotificationScheduler.kt, ReminderWorker.kt
        │   │   │
        │   │   ├── feature/
        │   │   │   ├── lock/                   # PinScreen, LockViewModel
        │   │   │   ├── home/                   # HomeScreen, HomeViewModel, HomeUiState
        │   │   │   ├── events/                 # EventsScreen, AddEditEventSheet, EventDetailsSheet, EventsViewModel
        │   │   │   ├── bookings/               # BookingsScreen, BookingCard, AddEditBookingSheet, PaymentDialog, BookingsViewModel
        │   │   │   ├── expenses/               # ExpensesScreen, ExpensesViewModel, ExpensesUiState (read-only view)
        │   │   │   ├── guests/                 # GuestsScreen, AddEditGuestSheet, GuestsViewModel, GuestsUiState
        │   │   │   ├── itemlists/              # ItemListsScreen, AddEditTaskSheet, ItemListsViewModel, ItemListsUiState
        │   │   │   ├── placeholder/            # PlaceholderScreen
        │   │   │   └── settings/               # SettingsScreen, ChangePinDialog, ExportCsvDialog, SettingsViewModel
        │   │   │
        │   │   ├── navigation/                 # AppNavHost.kt, AppDrawer.kt
        │   │   └── di/                         # DatabaseModule, RepositoryModule, FirebaseModule, SecurityModule, SettingsModule, WorkModule
```
        │   │   ├── feature/
        │   │   │   ├── lock/                   # SplashGate, PinSetupScreen, UnlockScreen, LockViewModel
        │   │   │   ├── home/                   # HomeScreen, HomeViewModel, HomeUiState, cards/
        │   │   │   ├── events/                 # EventsScreen, CalendarPager, EventSheet, AddEventSheet, EventsViewModel
        │   │   │   ├── bookings/               # BookingsScreen, BookingCard, AddBookingSheet, PaymentLog, BookingsViewModel
        │   │   │   ├── expenses/               # ExpensesScreen, ExpenseTable, AddExpenseSheet, ExpensesViewModel
        │   │   │   ├── invitees/               # InviteesScreen, GuestTable, GiftsTable, AddInviteeSheet, InviteesViewModel
        │   │   │   ├── checklist/              # ChecklistScreen, TaskRow, ChecklistViewModel
        │   │   │   └── settings/               # SettingsScreen, PalettePicker, SettingsViewModel
        │   │   │
        │   │   ├── navigation/                 # Routes.kt, AppNavHost.kt, AppDrawer.kt
        │   │   ├── notifications/              # Channels.kt, NotificationWorker.kt, NotificationRules.kt, Notifier.kt
        │   │   ├── widget/                     # EventWidget.kt, EventWidgetReceiver.kt, WidgetDataProvider.kt
        │   │   └── di/                         # DatabaseModule, RepositoryModule, FirebaseModule, WorkModule
        │   │
        │   └── res/
        │       ├── drawable/                   # vector drawables from /design/svg (ic_, ev_, bk_, il_)
        │       ├── font/                       # Inter
        │       ├── values/                     # strings.xml, themes.xml
        │       ├── xml/                        # file_paths.xml (FileProvider), widget_info.xml
        │       └── mipmap-anydpi-v26/          # adaptive launcher icon (vector)
        ├── test/                               # unit tests: MoneyFormatter, use cases, PinHasher
        └── androidTest/                        # Room and migration tests (optional)
```

Rules for the tree:
- A new screen goes in `feature/<name>/`; anything used by two or more screens moves to `core/ui/`.
- Each feature folder has its own `Screen`, `ViewModel`, `UiState` and screen-only composables.
- File names match the main class or composable inside.

## 5. App startup and navigation

1. System splash (about 500 ms hold with `setKeepOnScreenCondition`), shows the version text from `BuildConfig.APP_VERSION`.
2. `MainActivity` asks `LockViewModel` for the lock state:
   - no PIN stored: show **PIN setup**.
   - PIN stored: show **unlock** (fingerprint first, PIN pad as alternative).
   - authentication failure: `finishAffinity()` closes the app.
3. After success, the root `NavHost` shows the drawer scaffold.
4. Re-lock after the app has been in the background for more than 60 seconds (proposed).

Routes: `home`, `events`, `bookings`, `expenses`, `invitees`, `checklist`, `settings`. Drawer items map 1:1 to routes. Edit-mode entry from Home uses a route argument `?edit=true`. Notification taps use deep links to the same routes and still pass through the lock gate.

## 6. State management

- Each screen has `XxxUiState` (immutable data class), `XxxViewModel` (Hilt), and `onEvent(XxxEvent)`.
- ViewModels collect repository/use-case `Flow`s and combine them into one `StateFlow<XxxUiState>` with `stateIn(viewModelScope, WhileSubscribed(5000), initial)`.
- One-time effects (snackbar with Undo, navigation, share sheet) go through a `Channel` exposed as a `Flow`.
- Edit and delete modes are plain fields in `UiState` (`mode = Normal | Edit | Delete`, `selectedIds`).

## 7. Theming architecture

- `PaletteId { A, B, C, D }` and `ThemeMode { LIGHT, DARK, SYSTEM }` are stored in DataStore.
- `AppTheme` resolves the current `AppColors` (tokens from `design.md` section 3) and provides it through `LocalAppColors`; it also maps to a Material 3 `ColorScheme`.
- Status colours (`danger`, `success`, `warning`) are constants outside the palette.
- `gradientFor(slot, palette, isDark)` builds card gradients with `Color.hsl(...)`.
- Changing palette or mode recomposes the app without restarting.

## 8. Data layer

### 8.1 Entities and relationships

```
Event 1 ---- * Booking 1 ---- * BookingPayment
Event 1 ---- * Expense
Event 1 ---- * Invitee
Event 1 ---- * Gift          (Gift * ---- 0..1 Invitee)
Event 1 ---- * Task
```

All foreign keys use `ON DELETE CASCADE`, indexed on `eventId`.

| Entity | Fields |
|---|---|
| `Event` | id, title, eventName, date (unique), iconKey, budget, inviteesRequired |
| `Booking` | id, eventId, title, bookingType, bookingIconKey, contactName, phone, totalAmount, nextDueDate, bookingMade |
| `BookingPayment` | id, bookingId, date, amount, note |
| `Expense` | id, eventId, groupId, purpose, paid, due, items (list), allItemsReceived |
| `Invitee` | id, eventId, groupId, relativeName, expectedCount, address (text), invited, note |
| `Gift` | id, eventId, giverName, type (CASH or GIFT), amount, description, inviteeId? |
| `Task` | id, eventId, title, dueDate?, done |

Common columns on every table: `createdAt`, `updatedAt`, `syncState` (PENDING, SYNCED, FAILED), `deleted` (soft-delete flag).

### 8.2 Rules
- **Money** is stored as `Long` paise; only `MoneyFormatter` formats it (`en-IN`).
- `Event.date` has a unique index (one event per date).
- **Per-event independence:** an add for several events inserts one row per event sharing a `groupId`. Updates and deletes target a single row by `id`.
- **Delete with Undo:** set `deleted = true` on the event and its children, show the Undo snackbar for 10 seconds, then hard-delete (or restore if Undo is tapped). Queries always filter `deleted = 0`.
- `items` is stored as a JSON string through a Room `TypeConverter`. Dates use epoch day.
- Migrations are written explicitly; schemas are exported to `app/schemas/` and committed.

### 8.3 Derived values (domain use cases, never stored)
- Booking paid = sum of payments; amount left = total − paid.
- Event spent = booking payments + expense paid.
- Total budget = sum of event budgets; total spent = sum of event spent; net = spent − gifts received (cash).
- Pending dues = sum of booking amount left + expense due.
- Guests = sum of `expectedCount`; families = count of invitee rows.
- Budget status: normal below 80%, warning from 80% to 99%, over from 100%.

## 9. Security architecture

- PIN is 4 digits. Stored as an **HMAC-SHA256** hash via `PinHasher` (using Android Keystore key `myevents_pin_key`) inside `EncryptedSharedPreferences` (`PinStore`).
- Verification uses constant-time byte array comparison (`MessageDigest.isEqual`) to prevent timing side-channel attacks.
- Fingerprint through `BiometricPrompt` locked to `BIOMETRIC_STRONG`; PIN is the fallback.
- Failed attempt counter (`failedAttempts`) and lockout timestamp (`lockoutUntilEpochMs`) are persisted in `PinStore` across process deaths and app restarts. After 3 failed attempts, a 30-second lockout delay is enforced.
- Cloud Auto Backup is explicitly disabled via `data_extraction_rules.xml` and `backup_rules.xml` to prevent local DB/prefs extraction on Android 12+.
- No PIN recovery (no accounts). Clearing app data resets the app.
- Cloud data in Firestore is strictly protected by `firestore.rules` enforcing `request.auth != null && request.auth.uid == userId`.

## 10. Sync architecture (Firebase)

- **Auth:** Firebase Anonymous Auth runs silently at first launch. There is no login screen.
- **Firestore layout:** `users/{uid}/events/{id}`, `.../bookings/{id}`, `.../payments/{id}`, `.../expenses/{id}`, `.../invitees/{id}`, `.../gifts/{id}`, `.../tasks/{id}`.
- **SyncWorker** (WorkManager, `NetworkType.CONNECTED`, with exponential back-off):
  1. Push all rows with `syncState = PENDING`, in batches, then mark `SYNCED`.
  2. Pull remote changes newer than the last sync time and upsert them into Room.
  3. Conflicts: last write wins by `updatedAt`.
  4. Deletes sync as tombstones (`deleted = true`), so they are not resurrected.
- A one-time sync is enqueued after every local write (debounced), plus a periodic sync about every 6 hours.
- Firestore's own offline cache is disabled; Room is the only local store.
- **Security rules (outline):** allow read and write only if `request.auth.uid == userId` for `users/{userId}/**`.
- No backup/restore UI, no recovery code.

## 11. Notifications architecture

- Six channels created at app start (one per type in `PRD.md` section 7), each tied to its Settings switch.
- **Daily worker** (WorkManager, periodic, about 09:00) evaluates `NotificationRules` against Room: event countdown (proposed 30, 7, 1 day), payment due, invitee follow-up, pending items, checklist due. Each notification is de-duplicated with a "last notified" key in DataStore.
- **Budget alerts** run immediately after any write that changes spending (80% and 100% thresholds, once each per event).
- No exact-alarm permission is needed. WorkManager survives reboot.
- Android 13+ requests `POST_NOTIFICATIONS` on first use of the Settings switch.
- Tapping a notification opens the matching route via deep link.

## 12. Widget

- Jetpack Glance widget (2 × 2 and 4 × 2). `WidgetDataProvider` reads a small summary from Room (next event, days left, budget spent and total).
- Updated by `GlanceAppWidget.updateAll` after data changes and once a day. It shows no guest or vendor details; tapping opens the app through the lock gate.

## 13. Export

- `CsvWriter` builds one CSV per event for Expenses or Invitees (UTF-8 with BOM so ₹ and non-English names open correctly in Excel).
- The file is written to the cache directory and shared through `FileProvider` and the Android share sheet. No storage permission is needed.

## 14. Version handling

- `version.md` holds `Current: V0.x.y`.
- `app/build.gradle.kts` reads that line at configuration time and sets `buildConfigField("String", "APP_VERSION", ...)`.
- The splash screen and Settings > About show `BuildConfig.APP_VERSION`. Nothing else stores the version.

## 15. Dependency injection (Hilt)

| Module | Provides |
|---|---|
| `DatabaseModule` | `AppDatabase`, all DAOs |
| `RepositoryModule` | `@Binds` repository interfaces to implementations |
| `FirebaseModule` | `FirebaseAuth`, `FirebaseFirestore`, `FirestoreDataSource` |
| `WorkModule` | `WorkManager`, `SyncScheduler` |
| `SettingsModule` | DataStore, `PinStore`, `BiometricHelper` |

ViewModels use `@HiltViewModel`; workers use `@HiltWorker`.

## 16. Build, config and secrets

- Release builds enable R8 minification and resource shrinking; keep rules in `proguard-rules.pro`.
- Secrets and config (`google-services.json`, `local.properties`, keystores) are git-ignored; commit `*.example` files only (see `Gemini.md` rule 11).
- One build type per purpose: `debug` (default) and `release` (signed with a local keystore).

## 17. Testing strategy

- **Unit tests:** `MoneyFormatter`, derived-value use cases, `PinHasher`, notification rules, CSV output.
- **Room tests:** cascade deletes, unique date, soft-delete and Undo queries.
- **Previews:** every screen and component has light and dark `@Preview`s (see `Gemini.md` rule 3).
- **Manual tests:** `testcases.md`, appended and run after each sprint.

## 18. Error handling and logging

- Repositories return results as `Result<T>`; ViewModels turn failures into a snackbar message or inline field error.
- Sync failures set `syncState = FAILED` and retry with back-off; they never block the UI.
- No logging of PINs, amounts or personal data. Debug logs are removed before release.

## 19. UI Inventory

This section lists all **Screens**, **Bottom Sheets**, **Popups / Dialogs**, **Toasts / Snackbars**, and **Navigation Drawers** across the MyEvents codebase.

| # | Component Name | Type | Feature / Context | Description | Relative Path | Preview Available? |
|---|---|---|---|---|---|---|
| | **SCREENS** | | | | | |
| 1 | `SplashScreenContent` | Screen | App Lock / Shell | Custom artwork splash screen with smooth 1.2s fade-out. | `MainActivity.kt` | Yes |
| 2 | `PinScreen` | Screen | Security / Lock | 4-digit PIN setup and passcode unlock screen with biometric fingerprint fallback. | `feature/lock/PinScreen.kt` | Yes |
| 3 | `HomeScreen` | Screen | Home Dashboard | Main dashboard showing 7 overview cards (Budget, Spent, Events, Bookings, Guests, Tasks, Gifts). | `feature/home/HomeScreen.kt` | Yes |
| 4 | `EventsScreen` | Screen | Events | Interactive monthly calendar view, budget stat cards, and event list. | `feature/events/EventsScreen.kt` | Yes |
| 5 | `BookingsScreen` | Screen | Bookings | Vendor booking cards, payment progress bars, pill filters, and sticky totals footer. | `feature/bookings/BookingsScreen.kt` | Yes |
| 6 | `ItemListsScreen` | Screen | Item Lists | Task/item list per event with check toggles, estimated costs, paid sums, and delete mode. | `feature/itemlists/ItemListsScreen.kt` | Yes |
| 7 | `ExpensesScreen` | Screen | Expenses | Tabular Excel-style expense rows, event tabs, filter chips, and sticky totals footer. | `feature/expenses/ExpensesScreen.kt` | Yes |
| 8 | `GuestsScreen` | Screen | Guests | Single-row tabular guest list, invitation type icons, extra cards counter, and sticky totals footer. | `feature/guests/GuestsScreen.kt` | Yes |
| 9 | `SettingsScreen` | Screen | Settings | App theme mode, 5-swatch color palette selector, PIN settings, CSV export, and About version details. | `feature/settings/SettingsScreen.kt` | Yes |
| 10 | `PlaceholderScreen` | Screen | Navigation | Fallback placeholder screen for unbuilt routes. | `feature/placeholder/PlaceholderScreen.kt` | Yes |
| | **BOTTOM SHEETS** | | | | | |
| 11 | `AddEditEventSheet` | Bottom Sheet | Events | Sheet to create a new event or edit title, category, date range, budget, and guest count. | `feature/events/AddEditEventSheet.kt` | Yes |
| 12 | `EventDetailsSheet` | Bottom Sheet | Events | Read-only details sheet showing event summary, budget breakdown, and stats. | `feature/events/EventDetailsSheet.kt` | Yes |
| 13 | `MonthPickerSheet` | Bottom Sheet | Events | Bottom sheet to select month (Jan–Dec 3x4 grid) and year for calendar navigation. | `feature/events/EventsScreen.kt` | Yes |
| 14 | `AddEditBookingSheet` | Bottom Sheet | Bookings | Sheet to add/edit vendor bookings, total cost, advance payment, due date, and icon. | `feature/bookings/AddEditBookingSheet.kt` | Yes |
| 15 | `AddEditGuestSheet` | Bottom Sheet | Guests | Sheet to add/edit guest name, event, expected count, address, invited status, invitation via, and note. | `feature/guests/AddEditGuestSheet.kt` | Yes |
| 16 | `AddEditTaskSheet` | Bottom Sheet | Item Lists | Sheet to add/edit item list task title, event, vendor, estimated cost, paid amount, and due date. | `feature/itemlists/AddEditTaskSheet.kt` | Yes |
| 17 | `AppSheet` | Bottom Sheet (Core) | Core UI | Reusable bottom sheet wrapper with rounded top corners (`Radius.sheet`). | `core/ui/AppSheet.kt` | Yes |
| | **POPUPS & DIALOGS** | | | | | |
| 18 | `PaymentDialogFull` | Popup | Bookings | Popup dialog to record a payment against a vendor booking (Date, Amount, Note). | `feature/bookings/PaymentDialog.kt` | Yes |
| 19 | `BookingIconPickerDialog` | Popup | Bookings | 5-column grid popup dialog with 47 custom vendor icons for booking icon selection. | `feature/bookings/AddEditBookingSheet.kt` | Yes |
| 20 | `ExtraCardsDialog` | Popup | Guests | Popup dialog to set the number of extra physical invitation cards (with 12dp button margins). | `feature/guests/GuestsScreen.kt` | Yes |
| 21 | `ChangePinDialog` | Popup | Settings | Popup dialog to set, change, or disable the 4-digit security PIN. | `feature/settings/ChangePinDialog.kt` | Yes |
| 22 | `ExportCsvDialog` | Popup | Settings | Popup dialog to export app data into downloadable CSV files. | `feature/settings/ExportCsvDialog.kt` | Yes |
| 23 | `AppDialog` | Popup (Core) | Core UI | Generic app dialog wrapper component. | `core/ui/AppDialog.kt` | Yes |
| 24 | `AppDeleteDialog` | Popup (Core) | Core UI | Universal red deletion popup dialog used directly across Events, Bookings, Item Lists, and Guests screens. | `core/ui/AppDeleteDialog.kt` | Yes |
| | **TOASTS & SNACKBARS** | | | | | |
| 25 | `AppSnackbar` | Toast / Snackbar | Core UI | Universal floating snackbar container supporting 10s Undo actions and feedback toasts. | `core/ui/AppSnackbar.kt` | Yes |
| 26 | `AppSnackbarItem` | Toast / Snackbar | Core UI | Individual toast message visual item rendered inside `AppSnackbar`. | `core/ui/AppSnackbar.kt` | Yes |
| 27 | `SyncFeedbackToast` | Toast / Snackbar | Settings / Sync | Firebase Cloud Sync status feedback toasts ("Syncing with cloud...", "Sync completed successfully", "Sync failed. Check connection."). | `feature/settings/SettingsScreen.kt` | Yes |
| | **NAVIGATION DRAWER** | | | | | |
| 28 | `AppDrawer` | Navigation Drawer | Navigation | Sliding side navigation drawer with brand header, nav links + badges, theme switch, and version label. | `navigation/AppDrawer.kt` | Yes |

