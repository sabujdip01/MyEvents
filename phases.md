# Build Phases: Events Expense Tracker

Companion to `PRD.md`. Each phase ends with a working, testable app. Tick the boxes as you go.

## Overview

| Phase | Name                          | Main outcome                                            |
| ----- | ----------------------------- | ------------------------------------------------------- |
| 0     | Foundation                    | Project, theme tokens, icons pipeline, shared utilities |
| 1     | App shell and security        | Splash, PIN, fingerprint, drawer, empty Home            |
| 2     | Data layer and Events         | Room database, calendar, add/edit/delete events         |
| 3     | Bookings                      | Booking cards, payment log, forms                       |
| 4     | Expenses                      | Per-event tables, add/edit/delete, search and filters   |
| 5     | Invitees and Gifts            | Guest tables, gifts tab                                 |
| 6     | Checklist and live Home       | Tasks, Home cards wired to real data                    |
| 7     | Notifications and widget      | Six reminder types, home-screen widget                  |
| 8     | Settings, export, sample data | Settings complete, CSV export, demo data                |
| 9     | Firebase sync                 | Offline-first sync with anonymous auth                  |
| 10    | UI/UX polish and release      | Final visuals, QA, performance                          |

Dependencies: 0 → 1 → 2 → (3, 4, 5 in order) → 6 → 7 → 8 → 9 → 10. Phases 3 to 5 each need Events from Phase 2.

---

## Phase 0: Foundation

**Goal:** a clean project everything else builds on.

- [x] Create project (Kotlin, Compose, Material 3, min SDK 29)
- [x] Set up Hilt, Room, Navigation Compose, DataStore, WorkManager dependencies
- [x] Package structure: `data`, `domain`, `ui`, `core`
- [x] Theme tokens: light and dark `ColorScheme`, typography, shapes, spacing; accent swappable
- [x] Gradient palette helper (list of gradient brushes, picked by index so same-type cards differ)
- [x] Icon pipeline: folder for SVG-to-vector drawables, `AppIcon` / `EventIcon` / `BookingIcon` enums with Material fallback
- [x] `strings.xml` for all UI text
- [x] `MoneyFormatter` (paise `Long` to ₹ with Indian grouping), unit-tested
- [x] Date helpers (days left, month grids)

**Done when:** an empty themed app runs in light and dark, and the money formatter tests pass.

---

## Phase 1: App shell and security

**Goal:** the app opens safely and you can move between empty pages.

- [x] Splash screen held about 500 ms
- [x] First-launch PIN setup (4 digits, enter twice)
- [x] PIN storage as salted hash with Keystore protection
- [x] Unlock screen: fingerprint prompt, with PIN as the alternative
- [x] Failure rule: 3 attempts → 30-second lockout (GEMINI.md decision)
- [x] Floating three-line menu button (top-left) and navigation drawer
- [x] Drawer items with icons: Home, Events, Bookings, Expenses, Invitees, Checklist, Settings
- [x] Placeholder screens with empty states (icon, hint, button)
- [x] Home screen with the seven cards at 0 or blank values, each with its own gradient
- [x] Settings skeleton: theme switch (Light/Dark/System), Change PIN, fingerprint toggle, version

**Done when:** cold start goes splash → unlock → Home; wrong auth closes the app; theme switch works.

---

## Phase 2: Data layer and Events

**Goal:** the core database and the calendar.

**2A. Data layer**

- [x] Room entities and DAOs for all tables in the PRD data model
- [x] Common fields: `id`, `createdAt`, `updatedAt`, `syncState`, soft-delete flag
- [x] Repositories exposing `Flow`s; ViewModels with UI state
- [x] Derived calculations (spent, dues, guests, families) as tested use cases

**2B. Events screen**

- [x] Vertically scrolling, snap-paged month calendar (reels style)
- [x] Event icon drawn on dated cells
- [x] Tap blank date: nothing
- [x] Tap event date: details popup (bookings count, invitees count, Edit, Delete)
- [x] Long-press empty date: Add Event popup (title, event name, date, icon, budget, invitees required)
- [x] Long-press date with event: opens its details/edit
- [x] One event per date enforced
- [x] Delete: confirm dialog listing linked data, 10-second Undo, then permanent removal

**Done when:** you can add, edit and delete events, and they persist after restarting the app.

---

## Phase 3: Bookings

**Goal:** vendor bookings with part-payments.

- [x] Booking cards: title (top 10%), event icon (left 15% of remainder), amount/budget, booking icon (right 10%), per-card gradients
- [x] FAB opens Add Booking popup: title, booking name, booking icon, for-event dropdown (name and icon), contact person, phone, total, paid, amount left, next due date
- [x] Payment log per booking (date, amount, note); paid = sum; amount left = total − paid
- [x] Event dropdown reads from Events
- [x] Long-press card: edit mode with delete icons
- [x] Booking count per event feeds the Events details popup

**Done when:** bookings can be added, paid in parts, edited and deleted, and amounts update everywhere they are shown.

---

## Phase 4: Expenses

**Goal:** per-event expense tables.

- [x] Dynamic top tabs from the events list
- [x] Table: Purpose, Amount, Amount due, Items list, All-items-received flag, total row
- [x] Booking amounts shown as read-only rows
- [x] Add popup: purpose, multi-select events, paid, due, dynamic item boxes (new blank box when the last is filled), all-collected flag
- [x] Multi-event add creates one row per event sharing a `groupId`
- [x] Edit mode: editable cells, one or many rows, only the selected event's row changes
- [x] Delete mode: checkboxes, delete one or many rows
- [x] Search bar and filter chips (All, Due pending, Items pending)
- [x] Empty state per tab

**Done when:** adding an expense for two events creates two rows, editing one leaves the other unchanged, and totals are correct.

---

## Phase 5: Invitees and Gifts

**Goal:** guest lists and gifts received.

**5A. Invitees**

- [x] Tabs only for events with "Invitees required" = Yes
- [x] Table: relative name, expected count, address, invited flag (green check)
- [x] Footer: invitee count (sum) and family count (rows)
- [x] Add popup: name, multi-select events, expected count, address (plain text), invited flag, note
- [x] One row per event with shared `groupId`; independent edits
- [x] Edit and delete modes; search and filter chips (All, Invited, Not invited)
- [x] Invitee count feeds the Events details popup

**5B. Gifts tab**

- [x] Per-event gifts log: giver, amount or gift description, optional invitee link
- [x] Gift totals reduce net spending where shown

**Done when:** guest tables and footers work, and gifts are recorded per event.

---

## Phase 6: Checklist and live Home

**Goal:** tasks plus a fully wired dashboard.

**6A. Checklist**

- [x] Per-event task list: title, optional due date, done checkbox
- [x] Placement decided (drawer item or tab), then built
- [x] Add, edit, delete tasks; empty state

**6B. Home wiring**

- [x] Event Dates card per event (date, days left)
- [x] Bookings Done card per event (done / total)
- [x] Booking Amount card
- [x] Budget vs Spent card (progress bar turns red when over)
- [x] Pending Dues card
- [x] Invitees card (guests and families)
- [x] Items Pending card
- [x] Long-press each card opens its page in edit mode

**Done when:** every number on Home matches the underlying pages and updates live.

---

## Phase 7: Notifications and widget

**Goal:** reminders and glanceable data.

- [ ] Notification channels and permission request (Android 13+)
- [ ] Event countdown (proposed 30, 7, 1 day before)
- [ ] Payment due (booking next due date, expense dues)
- [ ] Invitee follow-up (not yet invited)
- [ ] Pending items (not yet bought/received)
- [ ] Budget alerts at 80% and 100%, plus the red "over by ₹X" warning
- [ ] Checklist due reminders
- [ ] WorkManager scheduling that survives reboot and reschedules after data edits
- [ ] Glance widget: days left to the next event and budget spent vs total only
- [ ] Tapping a notification opens the right page (after unlock)

**Done when:** each reminder fires on test data and each can be switched off individually.

---

## Phase 8: Settings, export and sample data

**Goal:** finish everything the user can configure.

- [ ] Colour scheme picker
- [ ] Per-type notification toggles and master switch
- [ ] Change PIN flow (asks for the current PIN)
- [ ] Load sample data and Clear sample data (demo rows tagged so real data is never touched)
- [ ] CSV export for Expenses and Invitees, one file per event, UTF-8 with BOM, via share sheet
- [ ] About and version

**Done when:** every Settings item works and exported CSVs open correctly in Excel.

---

## Phase 9: Firebase sync

**Goal:** quietly back up and sync data when online.

- [ ] Firebase project, Anonymous Auth, Firestore rules locked to the signed-in anonymous user
- [ ] Sync worker: upload pending local changes, pull remote changes
- [ ] Conflict rule: last write wins by `updatedAt`
- [ ] Sync state per row (pending, synced, failed) with retry and back-off
- [ ] Offline test: add data in airplane mode, reconnect, confirm upload
- [ ] No backup/restore UI (single phone)

**Done when:** offline edits reach Firestore after reconnecting, with no user action and no duplicates.

---

## Phase 10: UI/UX polish and release

**Goal:** make it look and feel finished.

- [ ] Final visual style and palette (decided here)
- [ ] Replace placeholder icons with the final SVG set
- [ ] Animations (drawer, calendar snap, card transitions)
- [ ] Accessibility: contrast in both themes, content descriptions, large font support
- [ ] Performance: large guest lists (hundreds of rows), calendar scrolling
- [ ] Edge cases: leap days, events in the past, very large amounts, long names
- [ ] Test pass on a few devices and Android versions (10 to latest)
- [ ] Release build, signing, app icon

**Done when:** a full end-to-end run (set PIN, add events, bookings, expenses, invitees, receive reminders, export) works without issues.

---

## Decisions needed before each phase

| Before phase | Decide                                                |
| ------------ | ----------------------------------------------------- |
| 1            | PIN attempt limit, lockout, and no-recovery policy    |
| 3            | (none beyond PRD)                                     |
| 4            | Expense payments: simple paid + due, or a payment log |
| 6            | Checklist placement: drawer item or tab               |
| 7            | Notification timing (days before, time of day)        |
| 10           | Visual style and colour direction                     |
