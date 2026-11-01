# Version History

Current: V0.9.1

| Version | Phase | Build | Commit     | Summary                                                                                             |
| ------- | ----- | ----- |------------|-----------------------------------------------------------------------------------------------------|
| V0.9.1  | 9     | 1     | —          | feat(sync): Firebase anonymous auth, Firestore background sync worker, offline-first retry          |
| V0.8.1  | 8     | 1     | —          | feat(settings/export): theme/palette selector, Change PIN, notification toggles, sample data, CSV export |
| V0.7.1  | 7     | 1     | —          | feat(notifications/widget): notification channels, WorkManager reminder worker, Glance widget       |
| V0.6.1  | 6     | 1     | —          | feat(checklist/home): checklist per-event tasks, live Home dashboard 7-card wiring                   |
| V0.5.1  | 5     | 1     | —          | feat(invitees): guest list table, multi-event add, gifts sub-tab, counts summary                    |
| V0.4.1  | 4     | 1     | —          | feat(expenses): per-event expense tables, multi-event add, dynamic items, vendor rows, filters     |
| V0.3.9  | 3     | 9     | —          | fix(bookings): resolve sprint 1-3 test feedback (top bar alignment, date picker, contract/booking amounts, event chips) |
| V0.3.8  | 3     | 8     | —          | fix(bookings): replace unresolved title reference with bookingType in AddEditBookingSheet             |
| V0.3.7  | 3     | 7     | —          | fix(events/bookings): remove undo, clean booking sheet (no Title field), simplify delete |
| V0.3.6  | 3     | 6     | 1fc64a4    | fix(events): S1-15 header, S2-07 budget/switch, S2-08 cell colour, S2-09 detail sheet, S2-11 inline error |
| V0.3.5  | 3     | 5     | 7091e50    | fix(home): Sprint 1 fixes — splash light/dark, 60s re-lock, card heights, app name, dues colour    |
| V0.3.4b | 3     | 4b    | —          | fix(ui): make IconChip contentDescription nullable String? = null                                  |
| V0.3.4a | 3     | 4a    | 4602c98    | fix(bookings): fix rememberModalBottomSheetState parameter name skipPartiallyExpanded              |
| V0.3.4  | 3     | 4     | 3f43b47    | chore(phase): complete phase 3 — Bookings; testcases appended                                      |
| V0.3.3  | 3     | 3     | a2e7819    | BookingsScreen, BookingCard, AddEditBookingSheet, PaymentDialog, DeleteBookingDialog, NavHost wired |
| V0.3.2  | 3     | 2     | 889192a    | ObserveBookings, AddBooking, EditBooking, DeleteBooking, AddPayment use cases                       |
| V0.3.1  | 3     | 1     | b15697a    | BookingEntity, BookingPaymentEntity, BookingDao, AppDatabase v2 migration, RepositoryImpl           |
| V0.2.5  | 2     | 5     | —          | Wire EventsScreen into NavHost; update PreviewData; mark Phase 2 complete in phases.md              |
| V0.2.4  | 2     | 4     | 210e691    | EventsScreen: calendar pager, details sheet, add/edit form, delete dialog, ViewModel               |
| V0.2.3  | 2     | 3     | 6169408    | Use cases: ObserveEvents, AddEvent, EditEvent, SoftDelete/Restore/HardDelete, ObserveHomeSummary   |
| V0.2.2  | 2     | 2     | 7575420    | Hilt DatabaseModule + RepositoryModule; binds EventRepository                                      |
| V0.2.1  | 2     | 1     | 8795e3d    | Domain models (Event, HomeSummary, SyncState), Room entity, DAO, DB, repository impl               |
| V0.1.7  | 1     | 7     | bfafa4b    | fix(lock): add missing display typography import in PinScreen.kt                                    |
| V0.1.6  | 1     | 6     | 7561838    | Fix BiometricPrompt (FragmentActivity) + sequential shake animation on wrong PIN                    |
| V0.1.5h | 1     | 5h    | 27cd86b    | Read LocalOpenDrawer inside AppDrawer content lambda (S1-10 drawer fix)                             |
| V0.1.5g | 1     | 5g    | 1bff950    | Fix 11 Sprint 1 visual issues (theme, PIN, biometric, menu, Home cards)                             |
| V0.1.5  | 1     | 5     | —          | Use Icons.Rounded.Work as fallback icon for BookingIcon.Other                                       |
| V0.1.4h | 1     | 4h    | a87a31e    | Add missing `dp` import in BgBlobs.kt (unresolved reference fix)                                    |
| V0.1.4g | 1     | 4g    | 58ff9bb    | Change StatusLight/Dark from anonymous objects to StatusColors data class (unresolved danger fix)   |
| V0.1.4f | 1     | 4f    | 6b1e939    | Suppress KSP kotlin.sourceSets error on AGP 9 (gradle.properties flag)                              |
| V0.1.4e | 1     | 4e    | 511d810    | Replace default launcher icon with custom app icon from Sample UI                                   |
| V0.1.4d | 1     | 4d    | 53ca5fe    | Add .idea/ and .bin/ to .gitignore (were missing per rule 11/12)                                    |
| V0.1.4c | 1     | 4c    | 1f28e40    | Add Gradle wrapper, launcher icons, res files (project files commit)                                |
| V0.1.4  | 1     | 4     | 8072d02    | version.md drives versionName; BuildConfig.APP_VERSION in Settings; google-services.json gitignored |
| V0.1.3  | 1     | 3     | 8da17b5    | App shell and security complete: splash, PIN, biometric stub, drawer, Home, Settings skeleton       |
| V0.1.2  | 1     | 2     | d37e181    | Home 7-card layout, drawer, NavHost, splash→lock→app wiring                                         |
| V0.1.1  | 1     | 1     | a9858b0    | PIN setup/unlock, Keystore HMAC, 3-attempt lockout                                                  |
| V0.1.0  | 1     | 0     | b1cec71    | Shared UI component foundation                                                                      |
| V0.0.2  | 0     | 2     | 10505e2    | AppColors, palettes, gradients, Inter font, MoneyFormatter, DateUtils, icon enums, full strings.xml |
| V0.0.1  | 0     | 1     | (pre-git)  | Fix built-in Kotlin and Hilt plugin compatibility with AGP 9                                        |
