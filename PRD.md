# PRD: Events Expense Tracker (Android)

Status: Final (All Phases 0–10 Implemented in V0.10.1)

Related files: `architecture.md` (how it is built), `design.md` (how it looks), `phases.md` (build order), `Gemini.md` (agent rules).

---

## 1. Overview

A personal Android app to plan and track the money, guests and tasks for family events such as engagement, wedding, reception, honeymoon and anniversary. One dashboard shows budget, spending, dues, bookings and guest counts at a glance. Bookings, expenses, invitees, gifts and tasks are organised per event.

**User:** one person, one phone, personal use only.

## 2. Goals

- See budget, spending, dues, bookings and guest counts at a glance.
- Track every vendor booking (with part-payments) and every expense, per event.
- Manage guest lists per event, with total guests and families.
- Get timely reminders for events, payments, invitations, pending items, budgets and tasks.
- Work fully offline; sync automatically when the device is online.
- Protect the data with a PIN and fingerprint.

## 3. Non-goals (v1)

- Multiple users, sign-in or sharing.
- Multiple projects or event sets (one set only).
- Backup/restore files or moving to a new phone.
- Multi-currency or languages other than English.
- Maps or location picker (address is plain text).
- Food planning per invitee (veg/non-veg, adults/kids).
- Rejected ideas: call/WhatsApp buttons on bookings, receipt photo attachments, screenshot blocking.

## 4. Product-wide requirements

- **Platform:** Android 10 (API 29) and above.
- **Themes:** Light and Dark, plus a colour palette chosen in Settings.
- **Icons:** custom SVG icons throughout, with Material Icons as fallback.
- **Currency:** ₹ INR only, Indian digit grouping (1,50,000).
- **Language:** English only.
- **Security:** 4-digit PIN set on first launch, changeable in Settings; fingerprint unlock; if authentication fails the app closes.
- **Splash:** about 0.5 seconds.
- **Navigation:** floating three-line menu opens a drawer: Home, Events, Bookings, Expenses, Invitees, Checklist, Settings.
- **Data:** stored on the device and synced automatically to the cloud when online. No sign-in screen, no backup/restore feature.
- **Empty screens:** hint plus action button on every page; optional sample data in Settings.
- **Version:** shown on the splash screen and in Settings.

## 5. Business rules

- One event per date.
- Each event has its own budget.
- **Total budget** = sum of event budgets. **Spent** = booking payments + expenses paid. **Pending dues** = booking amounts left + expense dues. Gifts received reduce net spending where shown.
- **Guests** = sum of expected attendee counts. **Families** = number of invitee entries (one relative name = one family).
- Adding an expense or invitee for several events creates **separate entries per event**; editing or deleting one never affects the others.
- Deleting an event is always allowed: a confirmation lists everything that will be removed, followed by a 10-second Undo.

## 6. Features by screen

**Home**
- Dashboard cards: Upcoming Events (date and days left), Bookings Done (x of y, per event), Booking Amount, Budget vs Spent, Pending Dues, Invitees (guests and families), Items Pending.
- Values start at zero and fill in from the other pages.
- Same-type cards use different gradient colours. Long-press a card to open its page in edit mode.

**Events**
- Interactive calendar that scrolls month by month.
- Tap an empty date: nothing. Tap an event date: details popup (bookings count, invitees count, edit, delete).
- Long-press a date: add an event (title, event name, date, icon, budget, invitees required yes/no).
- Events appear on the calendar with their chosen icon.

**Bookings**
- Cards (own gradient each) showing title, event icon, paid amount against total, booking icon.
- Add booking: title, booking name and icon, event, contact name, phone, total amount, payments, amount left, next due date.
- A booking can have many payments (date, amount, note). Long-press to edit or delete.

**Expenses**
- Per-event tabs; table with purpose, amount, amount due, items list, all-items-received flag, and a total row.
- Booking amounts appear as read-only rows.
- Add (one or many events), edit (one or many rows) and delete (one or many rows).
- Search and filter chips (All, Due pending, Items pending). Expense payment detail is still open (see section 8).

**Invitees**
- Per-event tabs for events that need invitees; table with relative name, expected count, address (plain text), invited flag; footer with guest count and family count.
- Add (one or many events), edit and delete as in Expenses. Search and filter chips (All, Invited, Not invited).
- **Gifts:** record cash or gifts received (giver, amount or description, event, optional invitee link).

**Checklist**
- Per-event tasks with optional due date and a done checkbox.

**Settings**
- Theme (Light, Dark, System), colour palette, notification switches, change PIN, fingerprint on/off, load/clear sample data, version.

## 7. Notifications

Each type can be turned on or off in Settings:
- Event countdown
- Payment due
- Invitee follow-up (not yet invited)
- Pending items (not yet bought/received)
- Budget alerts at 80% and 100% of an event's budget, plus a visual warning
- Checklist due dates

## 8. Other requirements

- **Widget:** home-screen widget showing days left to the next event and budget spent vs total (no guest or vendor details).
- **Export:** Expenses and Invitees tables export as CSV/Excel files, one per event.

## 9. Resolved Decisions

1. **Expense Payments**: Implemented as Total Amount + Amount Due fields per expense row, with vendor booking payment logs integrated as read-only rows in expense tables.
2. **PIN Rules**: 3 failed attempts → 30-second lockout delay. Attempt counter and lockout timestamp are persisted in `PinStore` across process deaths and app restarts.
3. **Notification Schedule**: WorkManager `ReminderWorker` scheduled every 12 hours checking 30d/7d/1d countdowns, 3-day payment dues, overdue tasks, and 80%/100% budget alerts.
4. **Final Visual Style**: 4 swappable palettes (A: Toxic Violet, B: Machine Red, C: Hyper Cobalt, D: Dark Aubergine), Light/Dark/System modes, custom SVG icon set, and 7-card live dashboard.
