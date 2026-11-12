# Version History

Current: V0.T.40

| Version  | Phase | Build | Commit    | Summary                                                                                             |
| -------- |-------|-------|-----------|-----------------------------------------------------------------------------------------------------|
| V0.T.40  | Test  | 40    | -         | fix(guests): shift guest count 20dp left, right-align status pill, 1-line total row, top-bar extra cards count |
| V0.T.39  | Test  | 39    | -         | feat(guests): footer redesign, top bar icons, sheet labels, single-select, extra cards dialog       |
| V0.T.38  | Test  | 38    | -         | feat(guests): excel-style 5-col card, 1-line layout, home card shows X/Y guests and families        |
| V0.T.37  | Test  | 37    | -         | docs(guests): add screen previews, testcases GT-01..GT-39, update design.md 6.8                     |
| V0.T.36  | Test  | 36    | -         | feat(guests): rewrite guests screen with expense-style layout and invitation type                   |
| V0.T.35  | Test  | 35    | -         | feat(guests): update state, viewmodel, home summary with invitation cards                           |
| V0.T.34  | Test  | 34    | -         | feat(guests): add InvitationType enum, DB v8 migration, extraCards DataStore                        |
| V0.T.33  | Test  | 33    | -         | fix(leftovers): theme undo toast, nav Guests rename, event undo, checklist paid sum & card row 3 |
| V0.T.32  | Test  | 32    | f042d55   | fix(expenses): title gap +20%, 'Total' label under total col, card padding 4dp                      |
| V0.T.31  | Test  | 31    | fa752d6   | fix(expenses): gap +20%, total col right, footer labels +25%, weights rebalanced                    |
| V0.T.30  | Test  | 30    | 79f4343   | fix(expenses): pill colors, font +25%, footer text, gap +20%, due pill warning                      |
| V0.T.29  | Test  | 29    | cc09521   | fix(expenses): full-width cards, icon shift left, due col shift, footer 6dp, total card text        |
| V0.T.28  | Test  | 28    | a194b98   | feat(expenses): actual booking icons on cards, sticky footer, column-aligned totals row             |
| V0.T.27  | Test  | 27    | b585ad4   | fix(expenses): read-only view, tabular Excel card layout, sum pills & footer position              |
| V0.T.26  | Test  | 26    | 3044439   | fix(home): card reorder & rename, spent calc, guest card 2-col layout & 36dp view event icon       |
| V0.T.25  | Test  | 25    | 51e709c   | fix(checklist): hide complete-by pill when all collected & ignore blank row deletions in save check |
| V0.T.24  | Test  | 24    | f465f93   | fix(checklist): initial edit save state, exclude blank items from count & Room v7 migration          |
| V0.T.23  | Test  | 23    | 5d0d764   | fix(checklist): item card styling, delete mode toggle & undoable task deletion                      |
| V0.T.22  | Test  | 22    | d769e07   | fix(bookings): add Delete mode toggle, disabled FAB in delete mode & single-card delete dialog     |
| V0.T.21  | Test  | 21    | ec6fa2b   | fix(bookings): event category icons on pill filters & swipable add/edit booking sheet                |
| V0.T.20  | Test  | 20    | 3a26903   | fix(events): match event card bg with calendar cell & circular 25% smaller view event icon           |
| V0.T.19  | Test  | 19    | -         | fix(sync): correct Firestore field names for Booking, Task, Invitee, Gift, Expense                  |
| V0.T.18  | Test  | 18    | -         | fix(cards): expense & invitee card -25% height, +40% gap, Total row above nav bar                   |
| V0.T.17  | Test  | 17    | -         | style(popups): standardize spacing, disable drag, add previews, fix nav bar overflow                 |
| V0.T.16  | Test  | 16    | 3053858   | fix(bookings): single-tap cancel, edit pills, renamed labels & 38dp zoomed icons                    |
| V0.T.15  | Test  | 15    | a0bc929   | fix(bookings): card 3rd row layout, green fully paid chip & blank vendor name                        |
| V0.T.14  | Test  | 14    | f966c13   | fix(bookings): eliminate sheet bounce with no-bounce nested scroll connection                       |
| V0.T.13  | Test  | 13    | 96e85c6   | fix(bookings): card layout 50% enlarged icons & 4-column relative positioning                        |
| V0.T.12  | Test  | 12    | 44d071f   | fix(bookings): icon dropdown popup, 47 icons grid & reorder confirmed toggle                        |
| V0.T.11  | Test  | 11    | af399e9   | fix(bookings): Add Booking title, 45 PNG icons integration & event dropdown date format              |
| V0.T.10 | Test  | 10    | 9e30347   | fix(home): filter passed events, remove chevron icon & make view event sheet read-only              |
| V0.T.9  | Test  | 9     | 62a14bf   | fix(events): trim event name, nav bar booking icon in view event sheet & delete dialog format       |
| V0.T.8  | Test  | 8     | 1efab92   | fix(ui): propagate event category PNG icons across booking, item list, expenses & invitee screens  |
| V0.T.7  | Test  | 7     | dcd8798   | fix(events): 10 event icons layout, budget field cursor & backspace, guest count sum                |
| V0.T.6  | Test  | 6     | 563f11e   | fix(events): view event popup layout, stat cards swap, label rename & calendar cell spacing         |
| V0.T.5  | Test  | 5     | bc7625f   | fix(events): add monthly events list section & align calendar grid cell height and spacing          |
| V0.T.4  | Test  | 4     | c35c310   | fix(ui): Budget card title alignment, monthly events list filtering & row styling                    |
| V0.T.3  | Test  | 3     | eb3e309   | fix(home): layout adjustments for Budget vs Spent, Items Pending position & Guests card title       |
| V0.T.2  | Test  | 2     | 2bb6584   | fix(home): align title font & spacing on 7 cards, center small card values, 40% font scaling on 'of'/'Invited' |
| V0.T.1  | Test  | 1     | 2a1e874   | fix(splash): bypass OS preview window & show custom artwork splash screen                           |
| V0.10.5 | 10    | 5     | 63f3d83   | chore(merge): resolve KSP build errors and merge fix/ksp-processing-error into main                 |
| V0.10.4 | 10    | 4     | ca79928   | feat(settings/sync): 56dp circular swatches, font size tuning, SYNC AND EXPORT section & 1h sync  |
| V0.10.3 | 10    | 3     | 6f74e01   | feat(ui): comprehensive UI/UX overhaul across splash, home, nav bar, events, bookings, item lists, invitees |
| V0.10.2 | 10    | 2     | c9d80e9   | fix(security): resolve all security audit findings (SEC-001 through SEC-010)                        |
| V0.10.1 | 10    | 1     | 5bc7e02   | chore(phase): complete phase 10 — UI/UX polish, accessibility, performance & release                |
| V0.9.1  | 9     | 1     | 0731313   | feat(sync): Firebase anonymous auth, Firestore background sync worker, offline-first retry          |
| V0.8.1  | 8     | 1     | c776125   | feat(settings/export): theme/palette selector, Change PIN, notification toggles, sample data, CSV export |
| V0.7.1  | 7     | 1     | 53716af   | feat(notifications/widget): notification channels, WorkManager reminder worker, Glance widget       |
| V0.6.1  | 6     | 1     | 86edd07   | feat(checklist/home): checklist per-event tasks, live Home dashboard 7-card wiring                   |
| V0.5.1  | 5     | 1     | 86edd07   | feat(invitees): guest list table, multi-event add, gifts sub-tab, counts summary                    |
| V0.4.1  | 4     | 1     | 86edd07   | feat(expenses): per-event expense tables, multi-event add, dynamic items, vendor rows, filters     |
| V0.3.9  | 3     | 9     | 86edd07   | fix(bookings): resolve sprint 1-3 test feedback (top bar alignment, date picker, contract/booking amounts, event chips) |
| V0.3.8  | 3     | 8     | 78981dc   | fix(bookings): replace unresolved title reference with bookingType in AddEditBookingSheet             |
| V0.3.7  | 3     | 7     | 78981dc   | fix(events/bookings): remove undo, clean booking sheet (no Title field), simplify delete |
| V0.3.6  | 3     | 6     | 1fc64a4   | fix(events): S1-15 header, S2-07 budget/switch, S2-08 cell colour, S2-09 detail sheet, S2-11 inline error |
| V0.3.5  | 3     | 5     | 7091e50   | fix(home): Sprint 1 fixes — splash light/dark, 60s re-lock, card heights, app name, dues colour    |
| V0.3.4b | 3     | 4b    | dc15ecd   | fix(ui): make IconChip contentDescription nullable String? = null                                  |
| V0.3.4a | 3     | 4a    | 4602c98   | fix(bookings): fix rememberModalBottomSheetState parameter name skipPartiallyExpanded              |
| V0.3.4  | 3     | 4     | 3f43b47   | chore(phase): complete phase 3 — Bookings; testcases appended                                      |
| V0.3.3  | 3     | 3     | a2e7819   | BookingsScreen, BookingCard, AddEditBookingSheet, PaymentDialog, DeleteBookingDialog, NavHost wired |
| V0.3.2  | 3     | 2     | 889192a   | ObserveBookings, AddBooking, EditBooking, DeleteBooking, AddPayment use cases                       |
| V0.3.1  | 3     | 1     | b15697a   | BookingEntity, BookingPaymentEntity, BookingDao, AppDatabase v2 migration, RepositoryImpl           |
| V0.2.5  | 2     | 5     | 712a5cb   | Wire EventsScreen into NavHost; update PreviewData; mark Phase 2 complete in phases.md              |
| V0.2.4  | 2     | 4     | 210e691   | EventsScreen: calendar pager, details sheet, add/edit form, delete dialog, ViewModel               |
| V0.2.3  | 2     | 3     | 6169408   | Use cases: ObserveEvents, AddEvent, EditEvent, SoftDelete/Restore/HardDelete, ObserveHomeSummary   |
| V0.2.2  | 2     | 2     | 7575420   | Hilt DatabaseModule + RepositoryModule; binds EventRepository                                      |
| V0.2.1  | 2     | 1     | 8795e3d   | Domain models (Event, HomeSummary, SyncState), Room entity, DAO, DB, repository impl               |
| V0.1.7  | 1     | 7     | bfafa4b   | fix(lock): add missing display typography import in PinScreen.kt                                    |
| V0.1.6  | 1     | 6     | 7561838   | Fix BiometricPrompt (FragmentActivity) + sequential shake animation on wrong PIN                    |
| V0.1.5h | 1     | 5h    | 27cd86b   | Read LocalOpenDrawer inside AppDrawer content lambda (S1-10 drawer fix)                             |
| V0.1.5g | 1     | 5g    | 1bff950   | Fix 11 Sprint 1 visual issues (theme, PIN, biometric, menu, Home cards)                             |
| V0.1.5  | 1     | 5     | —         | Use Icons.Rounded.Work as fallback icon for BookingIcon.Other                                       |
| V0.1.4h | 1     | 4h    | a87a31e   | Add missing `dp` import in BgBlobs.kt (unresolved reference fix)                                    |
| V0.1.4g | 1     | 4g    | 58ff9bb   | Change StatusLight/Dark from anonymous objects to StatusColors data class (unresolved danger fix)   |
| V0.1.4f | 1     | 4f    | 6b1e939   | Suppress KSP kotlin.sourceSets error on AGP 9 (gradle.properties flag)                              |
| V0.1.4e | 1     | 4e    | 511d810   | Replace default launcher icon with custom app icon from Sample UI                                   |
| V0.1.4d | 1     | 4d    | 53ca5fe   | Add .idea/ and .bin/ to .gitignore (were missing per rule 11/12)                                    |
| V0.1.4c | 1     | 4c    | 1f28e40   | Add Gradle wrapper, launcher icons, res files (project files commit)                                |
| V0.1.4  | 1     | 4     | 8072d02   | version.md drives versionName; BuildConfig.APP_VERSION in Settings; google-services.json gitignored |
| V0.1.3  | 1     | 3     | 8da17b5   | App shell and security complete: splash, PIN, biometric stub, drawer, Home, Settings skeleton       |
| V0.1.2  | 1     | 2     | d37e181   | Home 7-card layout, drawer, NavHost, splash→lock→app wiring                                         |
| V0.1.1  | 1     | 1     | a9858b0   | PIN setup/unlock, Keystore HMAC, 3-attempt lockout                                                  |
| V0.1.0  | 1     | 0     | b1cec71   | Shared UI component foundation                                                                      |
| V0.0.2  | 0     | 2     | 10505e2   | AppColors, palettes, gradients, Inter font, MoneyFormatter, DateUtils, icon enums, full strings.xml |
| V0.0.1  | 0     | 1     | (pre-git) | Fix built-in Kotlin and Hilt plugin compatibility with AGP 9                                        |
