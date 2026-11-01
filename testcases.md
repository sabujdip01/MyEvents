# Test Cases

## Sprint 1: App Shell & Security (V0.1.4)

| ID    | Area             | Steps                                                                 | Expected result                                              | Light | Dark | Status  |
| ----- | ---------------- | --------------------------------------------------------------------- | ------------------------------------------------------------ | ----- | ---- | ------- |
| S1-01 | First launch     | Install fresh; open app                                               | Splash shows ~500ms, then PIN setup screen appears           | [Works]   | [Fixed]  | Pass |
| S1-02 | PIN setup        | Enter 4 digits on setup screen                                        | 4 dots fill; screen transitions to "Confirm your PIN"        | [Works]   | [Works]  | Pass |
| S1-03 | PIN confirm match| Enter same 4 digits on confirm screen                                 | App unlocks, Home screen appears                             | [Works]   | [Works]  | Pass |
| S1-04 | PIN confirm mismatch | Enter different 4 digits on confirm screen                        | Dots shake, "PINs do not match" error, restarts from first entry | [Works] | [Works]  | Pass |
| S1-05 | Unlock correct   | Background app 60s+, reopen; enter correct PIN                        | Home screen appears                                          | [Works]   | [Works]  | Pass |
| S1-06 | Unlock incorrect | Enter wrong PIN 1Ã—                                                    | Dots shake, "Incorrect PIN. 2 attempts remaining."           | [Works]   | [Works]  | Pass |
| S1-07 | Lockout          | Enter wrong PIN 3Ã— in a row                                           | "Too many attempts. Try again in 30s." countdown shown       | [Works]   | [Works]  | Pass |
| S1-08 | Lockout expires  | Wait 30s after lockout                                                | Keypad becomes active again                                  | [Works]   | [Works]  | Pass |
| S1-09 | Backspace        | On PIN screen: type 2 digits, press backspace                         | Last dot clears                                              | [Works]   | [Works]  | Pass |
| S1-10 | Drawer opens     | On Home: tap menu button (â‰¡)                                          | Drawer slides in from left (300ms), scrim behind             | [Working]   | [Working]  | Pass |
| S1-11 | Drawer active    | Open drawer from Home                                                 | Home row is highlighted with primary bar at right edge       | [Working]   | [Working]  | Pass |
| S1-12 | Drawer navigate  | Tap "Bookings" in drawer                                              | Drawer closes, Bookings placeholder screen shows             | [Working]   | [Working]  | Pass |
| S1-13 | Drawer navigate back | From Bookings, tap â‰¡ â†’ Home                                      | Home screen shows                                            | [Working]   | [Working]  | Pass |
| S1-14 | Home empty state | First unlock with no events                                           | Upcoming Events card shows "No events yet" + "Add event" button | [Working] | [Working]  | Pass |
| S1-15 | Home cards       | Inspect Home screen                                                   | 7 cards visible: Events, Bookings Done, Booking Amount, Budget, Dues, Invitees, Items; title aligned with nav icon | [Header title aligned with nav icon] | [Works] | Pass |
| S1-16 | Settings screen  | Drawer → Settings                                                     | Settings screen with Appearance, Security, About sections    | [Working]   | [Working]  | Pass |
| S1-17 | Settings version | Go to Settings → About                                                | Version "V0.1.x" is shown                                    | [Working]   | [Working]  | Pass |
| S1-18 | Dark theme       | Change system to dark mode and reopen                                 | All screens use dark palette (Palette A dark)                | n/a   | [Working]  | Pass |

## Sprint 2: Data Layer & Events (V0.2.5)

| ID    | Area              | Steps                                                                                           | Expected result                                                                               | Light | Dark | Status  |
| ----- | ----------------- | ----------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------- | ----- | ---- | ------- |
| S2-01 | Navigation        | Drawer → Events                                                                                 | Events calendar screen opens with current month                                               | [Working]   | [Working]  | Pass |
| S2-02 | Calendar display  | Open Events screen                                                                              | Current month shown; weekday labels Mon–Sun; date cells fill correctly                        | [Working]   | [Working]  | Pass |
| S2-03 | Today highlight   | Open Events screen                                                                              | Today's cell has a subtle primary-tinted outline/background                                   | [Working]   | [Working]  | Pass |
| S2-04 | Month pager       | Swipe up on the calendar                                                                        | Next month slides in with snap; month pill in top bar updates                                 | [Working]   | [Working]  | Pass |
| S2-05 | Month pager back  | Swipe down on the calendar                                                                      | Previous month slides in; pill updates                                                        | [Working]   | [Working]  | Pass |
| S2-06 | Tap empty date    | Tap any date with no event                                                                      | Nothing happens (no sheet opens)                                                              | [Working]   | [Working]  | Pass |
| S2-07 | Add event         | Long-press an empty date → fill Title, Event name, pick icon, enter Budget → tap Add           | Sheet closes; event icon appears on that date cell; dot below icon                            | [Working]   | [Working]  | Pass |
| S2-08 | Persistence       | Add event, then kill and reopen the app                                                         | Event is still on the calendar                                                                | [Working]   | [Working]  | Pass |
| S2-09 | Tap event date    | Tap a date that has an event                                                                    | Details sheet opens showing title, date, stats tiles, detail list, Edit/Delete buttons        | [Working]   | [Working]  | Pass |
| S2-10 | Edit event        | Details sheet → Edit Event → change date/title → Save                                          | Sheet closes; calendar shows updated event; date change supported in edit mode                | [Date edit supported in Edit sheet]   | [Works]  | Pass |
| S2-11 | Date conflict     | Add/Edit event → pick a date already taken by another event → tap Add/Save                     | "A event is already planned for that date" error snackbar; sheet stays open                   | [Working]   | [Working]  | Pass |
| S2-12 | Long-press event  | Long-press a date that already has an event                                                     | Edit event sheet opens pre-filled with existing data                                          | [Works]   | [Works]  | Pass |
| S2-13 | Delete event      | Details sheet → Delete → Delete confirm dialog → tap Delete                                    | Dialog closes; event removed from calendar immediately (no Undo)                              | [Works]   | [Works]  | Pass |
| S2-14 | Undo delete       | N/A — Undo removed per user request                                                            | N/A                                                                                           | [N/A]   | [N/A]  | Obsolete |
| S2-15 | Hard delete       | Delete an event, kill and reopen app                                                           | Event is gone permanently                                                                     | [Working]   | [Working]  | Pass |
| S2-16 | Empty form        | Long-press date → tap Add without filling title or event name                                  | Add button stays disabled                                                                     | [Works]   | [Works]  | Pass |
| S2-17 | Icon picker       | In Add/Edit sheet, tap each icon in the grid                                                   | Selected icon gets primary ring + primaryContainer fill; all others reset                     | [Works]   | [Works]  | Pass |
| S2-18 | Invitees toggle   | Add event with "Invitees required = No" → open Details                                         | Invitees tile shows "Not required" and "—" value                                              | [Works]   | [Works]  | Pass |
| S2-19 | Light/dark theme  | Toggle system dark mode; open Events                                                            | Calendar, sheets and dialogs all use dark palette tokens; no hardcoded colours visible        | n/a   | [Works]  | Pass |

## Sprint 3: Bookings (V0.3.9)

| ID    | Area              | Steps                                                                                  | Expected result                                                                 | Light | Dark | Status  |
| ----- | ----------------- | -------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------- | ----- | ---- | ------- |
| S3-01 | Empty state       | Navigate to Bookings with no bookings added                                            | Top bar aligned with nav icon; event chips display name & total contract amount | [Top bar aligned, event chips show total contract amount] | [Works] | Pass |
| S3-02 | Add booking       | Tap FAB; fill Type, pick icon, select event, set Contract & Booking Amount; tap Add   | New booking card appears with ₹ symbol, Indian grouping, Contract & Remaining amounts | [Indian formatting, ₹ symbol, Contract/Booking/Remaining fields working] | [Works] | Pass |
| S3-03 | Validation        | N/A — Title field removed; bookingType used as title                                   | N/A                                                                             | [N/A] | [N/A] | Obsolete |
| S3-04 | Validation        | Tap FAB; fill Title; leave event unselected; tap Add                                   | Inline error message shown under field                                          | [Inline error shown] | [Works] | Pass |
| S3-05 | Add payment       | Tap booking card / Edit sheet; tap "Add payment"; enter amount & date; tap Save        | Payment row added to log; Paid so far & Remaining amount update                 | [Full payment log with + Add payment operational] | [Works] | Pass |
| S3-06 | Payment sum       | Add two payments of â‚¹5,000 and â‚¹15,000                                                 | Paid shows â‚¹20,000; Amount left shows correctly; progress bar updates            | [ ]   | [ ]  | Pending |
| S3-07 | Fully paid        | Add payments totalling the booking total                                               | Status pill shows green "Paid"; progress bar turns accent colour at 100%         | [ ]   | [ ]  | Pending |
| S3-08 | Status pill       | Set next due date to yesterday, booking partially paid                                  | Status pill shows red "Overdue"                                                  | [ ]   | [ ]  | Pending |
| S3-09 | Status pill       | Set next due date to 3 days from now, booking partially paid                           | Status pill shows amber "Due DD MMM"                                             | [ ]   | [ ]  | Pending |
| S3-10 | Edit booking      | Long-press a card; tap the card in edit mode                                           | Add/Edit sheet opens pre-filled with booking data                                | [ ]   | [ ]  | Pending |
| S3-11 | Edit mode         | Long-press a card                                                                      | All cards show a red delete badge at top-right; FAB icon changes to checkmark    | [ ]   | [ ]  | Pending |
| S3-12 | Delete booking    | Enter edit mode; tap delete badge on a card; confirm                                   | Card disappears; snackbar "X deleted Â· UNDO" shown for 10 s                     | [ ]   | [ ]  | Pending |
| S3-13 | Undo delete       | Delete a booking; tap UNDO within 10 s                                                  | Booking card reappears; data intact                                              | [ ]   | [ ]  | Pending |
| S3-14 | Permanent delete  | Delete a booking; let 10 s pass without tapping UNDO                                   | Booking is permanently gone; not visible after app restart                       | [ ]   | [ ]  | Pending |
| S3-15 | Delete payment    | Open edit sheet; tap the trash icon on a payment row                                   | Payment row removed; Paid so far updates; amount left updates                    | [ ]   | [ ]  | Pending |
| S3-16 | Filter chips      | With bookings for two events; tap one event chip                                       | Only that event's bookings visible                                               | [ ]   | [ ]  | Pending |
| S3-17 | Filter all        | After filtering; tap "All" chip                                                        | All bookings shown again                                                         | [Works] | [Works] | Pass |
| S3-18 | Gradient variety  | Add 3+ bookings                                                                        | Each card has a visibly different gradient colour                                 | [Works] | [Works] | Pass |
| S3-19 | Persistence       | Add a booking; close and reopen the app; unlock                                        | Booking still appears with correct amounts                                       | [Works] | [Works] | Pass |
| S3-20 | Dark theme        | Switch to dark theme in Settings; go to Bookings                                       | Cards readable, gradient visible, status pills correct in dark mode               | [Works] | [Works] | Pass |

## Sprint 4: Expenses (V0.4.1)

| ID    | Area              | Steps                                                                                  | Expected result                                                                 | Light | Dark | Status  |
| ----- | ----------------- | -------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------- | ----- | ---- | ------- |
| S4-01 | Empty state       | Navigate to Expenses with no expenses added                                            | Empty state "No expenses yet" shown, FAB visible                                | [Works] | [Works] | Pass |
| S4-02 | Dynamic top tabs  | Open Expenses screen with events created                                               | Top tabs show "All" + each event name                                           | [Works] | [Works] | Pass |
| S4-03 | Add expense       | Tap FAB; fill Purpose, select 2 events, enter Total & Due, add items; tap Add          | Two expense rows created with same groupId, dynamic item input appends new box   | [Works] | [Works] | Pass |
| S4-04 | Vendor rows       | View Expenses table                                                                    | Read-only vendor booking rows included in table with "Vendor" pill              | [Works] | [Works] | Pass |
| S4-05 | Table summary     | Check table footer                                                                     | TOTAL row calculates sum of total amount & sum of amount due                    | [Works] | [Works] | Pass |
| S4-06 | Search & filters  | Type in search box or tap "Due pending" / "Items pending" filter chips                 | Table filters rows dynamically                                                  | [Works] | [Works] | Pass |
| S4-07 | Edit expense      | Tap expense row; modify purpose/amount; tap Save                                       | Expense row updates; other event rows in same groupId remain unchanged          | [Works] | [Works] | Pass |
| S4-08 | Status toggle     | Tap status pill on expense row                                                         | Toggles between "Done" (green) and "Pending" (amber)                            | [Works] | [Works] | Pass |
| S4-09 | Delete mode       | Tap "Edit" in top bar; check expenses; tap FAB trash icon                             | Selected expenses soft-deleted, snackbar confirms count                         | [Works] | [Works] | Pass |
| S4-10 | Dark theme        | Switch to dark theme in Settings; open Expenses                                        | Table, tabs, search box and sheets use dark palette tokens                      | [Works] | [Works] | Pass |

## Sprint 5: Invitees & Gifts (V0.5.1)

| ID    | Area              | Steps                                                                                  | Expected result                                                                 | Light | Dark | Status  |
| ----- | ----------------- | -------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------- | ----- | ---- | ------- |
| S5-01 | Empty state       | Navigate to Invitees with no guests added                                              | Empty state "No guests added yet" shown, FAB visible                            | [Works] | [Works] | Pass |
| S5-02 | Required tabs     | Open Invitees screen                                                                   | Top tabs show only events with "Invitees required = Yes"                        | [Works] | [Works] | Pass |
| S5-03 | Add guest         | Tap FAB; enter Guest Name, select events, enter expected count & address; tap Add     | Guest row added per event with same groupId, status "Pending"                   | [Works] | [Works] | Pass |
| S5-04 | Table footer      | Check table footer summary                                                             | Footer displays Total Families count & Total Guests count                       | [Works] | [Works] | Pass |
| S5-05 | Search & filters  | Search relative/address or tap "Invited" / "Not invited" filter chips                  | Table updates dynamically                                                       | [Works] | [Works] | Pass |
| S5-06 | Status toggle     | Tap status pill on guest row                                                           | Toggles between "Invited" (green) and "Pending" (amber)                         | [Works] | [Works] | Pass |
| S5-07 | Gifts tab         | Switch to "Gifts Received" sub-tab                                                     | Shows gifts list or empty state "No gifts recorded yet"                         | [Works] | [Works] | Pass |
| S5-08 | Record gift       | Tap FAB (+ Record Gift); enter Giver Name, select Event, Amount / Item; tap Save       | Gift row appears in gifts table; total gifts value updates                      | [Works] | [Works] | Pass |
| S5-09 | Delete mode       | Tap "Edit" in top bar; select guest rows; tap FAB trash icon                           | Selected guests soft-deleted, snackbar confirms count                           | [Works] | [Works] | Pass |
| S5-10 | Dark theme        | Switch to dark theme in Settings; open Invitees                                        | Table, tabs, search box, sub-tabs and sheets use dark palette tokens            | [Works] | [Works] | Pass |

## Sprint 6: Checklist & Live Home (V0.6.1)

| ID    | Area              | Steps                                                                                  | Expected result                                                                 | Light | Dark | Status  |
| ----- | ----------------- | -------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------- | ----- | ---- | ------- |
| S6-01 | Empty state       | Navigate to Checklist with no tasks added                                              | Empty state "No tasks yet" shown, FAB visible                                   | [Works] | [Works] | Pass |
| S6-02 | Event tabs        | Open Checklist screen                                                                  | Top tabs show "All" + each event name                                           | [Works] | [Works] | Pass |
| S6-03 | Add task          | Tap FAB; enter Task Title, select Event, pick optional Due Date; tap Add               | Task row created with checkbox and due date pill                                | [Works] | [Works] | Pass |
| S6-04 | Task toggle       | Tap checkbox on task row                                                               | Task toggles completed (strikethrough text), live dashboard count updates       | [Works] | [Works] | Pass |
| S6-05 | Edit/Delete task  | Tap task row to edit title/date, or enter edit mode to batch delete                    | Task updates or soft-deleted, snackbar confirms count                           | [Works] | [Works] | Pass |
| S6-06 | Live Home dashboard| Add/edit events, bookings, expenses, invitees & tasks; return to Home                 | All 7 cards update live with exact values (Events, Bookings, Budget, Dues, etc)| [Works] | [Works] | Pass |
| S6-07 | Dark theme        | Switch to dark theme in Settings; view Home and Checklist                              | Dashboard cards, checklist and sheets render cleanly in dark palette tokens     | [Works] | [Works] | Pass |

## Sprint 7: Notifications & Widget (V0.7.1)

| ID    | Area              | Steps                                                                                  | Expected result                                                                 | Light | Dark | Status  |
| ----- | ----------------- | -------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------- | ----- | ---- | ------- |
| S7-01 | Channels          | Launch app                                                                             | Notification channels created (`reminders`, `budget_alerts`, `dues`)            | [Works] | [Works] | Pass |
| S7-02 | Countdowns        | Add event with date in 30, 7, or 1 day                                                 | ReminderWorker posts countdown notification                                     | [Works] | [Works] | Pass |
| S7-03 | Payment dues      | Add booking with due date within 3 days                                                 | ReminderWorker posts payment due notification                                   | [Works] | [Works] | Pass |
| S7-04 | Task due          | Add task with due date today or past                                                   | ReminderWorker posts task due notification                                      | [Works] | [Works] | Pass |
| S7-05 | Budget alerts     | Spending reaches 80% or 100% of budget                                                 | ReminderWorker posts budget alert / warning notification                        | [Works] | [Works] | Pass |
| S7-06 | WorkManager       | App running in background / reboot                                                     | Periodic WorkManager worker scheduled every 12h                                 | [Works] | [Works] | Pass |
| S7-07 | Glance Widget     | Add "My Events Widget" to device Home screen                                           | Widget renders next upcoming event and active events count                      | [Works] | [Works] | Pass |
| S7-08 | Deep link         | Tap reminder notification                                                              | Opens app to main activity                                                      | [Works] | [Works] | Pass |
