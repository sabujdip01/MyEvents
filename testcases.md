# Test Cases

## Sprint 1: App Shell & Security (V0.1.4)

| ID    | Area             | Steps                                                                 | Expected result                                              | Light | Dark | Status  |
| ----- | ---------------- | --------------------------------------------------------------------- | ------------------------------------------------------------ | ----- | ---- | ------- |
| S1-01 | First launch     | Install fresh; open app                                               | Splash shows ~500ms, then PIN setup screen appears           | [ ]   | [ ]  | Pending |
| S1-02 | PIN setup        | Enter 4 digits on setup screen                                        | 4 dots fill; screen transitions to "Confirm your PIN"        | [ ]   | [ ]  | Pending |
| S1-03 | PIN confirm match| Enter same 4 digits on confirm screen                                 | App unlocks, Home screen appears                             | [ ]   | [ ]  | Pending |
| S1-04 | PIN confirm mismatch | Enter different 4 digits on confirm screen                        | Dots shake, "PINs do not match" error, restarts from first entry | [ ] | [ ]  | Pending |
| S1-05 | Unlock correct   | Background app 60s+, reopen; enter correct PIN                        | Home screen appears                                          | [ ]   | [ ]  | Pending |
| S1-06 | Unlock incorrect | Enter wrong PIN 1×                                                    | Dots shake, "Incorrect PIN. 2 attempts remaining."           | [ ]   | [ ]  | Pending |
| S1-07 | Lockout          | Enter wrong PIN 3× in a row                                           | "Too many attempts. Try again in 30s." countdown shown       | [ ]   | [ ]  | Pending |
| S1-08 | Lockout expires  | Wait 30s after lockout                                                | Keypad becomes active again                                  | [ ]   | [ ]  | Pending |
| S1-09 | Backspace        | On PIN screen: type 2 digits, press backspace                         | Last dot clears                                              | [ ]   | [ ]  | Pending |
| S1-10 | Drawer opens     | On Home: tap menu button (≡)                                          | Drawer slides in from left (300ms), scrim behind             | [ ]   | [ ]  | Pending |
| S1-11 | Drawer active    | Open drawer from Home                                                 | Home row is highlighted with primary bar at right edge       | [ ]   | [ ]  | Pending |
| S1-12 | Drawer navigate  | Tap "Bookings" in drawer                                              | Drawer closes, Bookings placeholder screen shows             | [ ]   | [ ]  | Pending |
| S1-13 | Drawer navigate back | From Bookings, tap ≡ → Home                                      | Home screen shows                                            | [ ]   | [ ]  | Pending |
| S1-14 | Home empty state | First unlock with no events                                           | Upcoming Events card shows "No events yet" + "Add event" button | [ ] | [ ]  | Pending |
| S1-15 | Home cards       | Inspect Home screen                                                   | 7 cards visible: Events, Bookings Done, Booking Amount, Budget, Dues, Invitees, Items; all show 0/– | [ ] | [ ] | Pending |
| S1-16 | Settings screen  | Drawer → Settings                                                     | Settings screen with Appearance, Security, About sections    | [ ]   | [ ]  | Pending |
| S1-17 | Settings version | Go to Settings → About                                                | Version "V0.1.x" is shown                                    | [ ]   | [ ]  | Pending |
| S1-18 | Dark theme       | Change system to dark mode and reopen                                 | All screens use dark palette (Palette A dark)                | n/a   | [ ]  | Pending |

## Sprint 2: Data Layer & Events (V0.2.5)

| ID    | Area              | Steps                                                                                           | Expected result                                                                               | Light | Dark | Status  |
| ----- | ----------------- | ----------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------- | ----- | ---- | ------- |
| S2-01 | Navigation        | Drawer → Events                                                                                 | Events calendar screen opens with current month                                               | [ ]   | [ ]  | Pending |
| S2-02 | Calendar display  | Open Events screen                                                                              | Current month shown; weekday labels Mon–Sun; date cells fill correctly                        | [ ]   | [ ]  | Pending |
| S2-03 | Today highlight   | Open Events screen                                                                              | Today's cell has a subtle primary-tinted outline/background                                   | [ ]   | [ ]  | Pending |
| S2-04 | Month pager       | Swipe up on the calendar                                                                        | Next month slides in with snap; month pill in top bar updates                                 | [ ]   | [ ]  | Pending |
| S2-05 | Month pager back  | Swipe down on the calendar                                                                      | Previous month slides in; pill updates                                                        | [ ]   | [ ]  | Pending |
| S2-06 | Tap empty date    | Tap any date with no event                                                                      | Nothing happens (no sheet opens)                                                              | [ ]   | [ ]  | Pending |
| S2-07 | Add event         | Long-press an empty date → fill Title, Event name, pick icon, enter Budget → tap Add           | Sheet closes; event icon appears on that date cell; dot below icon                            | [ ]   | [ ]  | Pending |
| S2-08 | Persistence       | Add event, then kill and reopen the app                                                         | Event is still on the calendar                                                                | [ ]   | [ ]  | Pending |
| S2-09 | Tap event date    | Tap a date that has an event                                                                    | Details sheet opens showing title, date, stats tiles, detail list, Edit/Delete buttons        | [ ]   | [ ]  | Pending |
| S2-10 | Edit event        | Details sheet → Edit Event → change title → Save                                               | Sheet closes; calendar shows updated event; details shows new title                           | [ ]   | [ ]  | Pending |
| S2-11 | Date conflict     | Add/Edit event → pick a date already taken by another event → tap Add/Save                     | "A event is already planned for that date" error snackbar; sheet stays open                   | [ ]   | [ ]  | Pending |
| S2-12 | Long-press event  | Long-press a date that already has an event                                                     | Edit event sheet opens pre-filled with existing data                                          | [ ]   | [ ]  | Pending |
| S2-13 | Delete event      | Details sheet → Delete → Delete confirm dialog → tap Delete                                    | Dialog closes; event removed from calendar; "Undo" snackbar appears                          | [ ]   | [ ]  | Pending |
| S2-14 | Undo delete       | Delete an event → tap UNDO within 10 seconds                                                   | Event reappears on the calendar; nothing is permanently deleted                               | [ ]   | [ ]  | Pending |
| S2-15 | Hard delete       | Delete an event → wait 10 seconds without tapping UNDO → kill and reopen app                  | Event is gone permanently; does not reappear                                                  | [ ]   | [ ]  | Pending |
| S2-16 | Empty form        | Long-press date → tap Add without filling title or event name                                  | Add button stays disabled                                                                     | [ ]   | [ ]  | Pending |
| S2-17 | Icon picker       | In Add/Edit sheet, tap each icon in the grid                                                   | Selected icon gets primary ring + primaryContainer fill; all others reset                     | [ ]   | [ ]  | Pending |
| S2-18 | Invitees toggle   | Add event with "Invitees required = No" → open Details                                         | Invitees tile shows "Not required" and "—" value                                              | [ ]   | [ ]  | Pending |
| S2-19 | Light/dark theme  | Toggle system dark mode; open Events                                                            | Calendar, sheets and dialogs all use dark palette tokens; no hardcoded colours visible        | n/a   | [ ]  | Pending |
