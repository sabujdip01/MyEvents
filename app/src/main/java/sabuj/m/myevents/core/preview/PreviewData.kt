package sabuj.m.myevents.core.preview

import sabuj.m.myevents.core.icons.BookingIcon
import sabuj.m.myevents.core.icons.EventIcon
import java.time.LocalDate

/**
 * Fake data used exclusively in @Preview composables.
 * Never import this in production code.
 */
object PreviewData {

    val sampleEventName   = "Riya & Raj Wedding"
    val sampleEventDate   = LocalDate.now().plusDays(45)
    val sampleEventIcon   = EventIcon.Wedding
    val sampleBudgetPaise = 500_000_00L  // ₹5,00,000
    val sampleSpentPaise  = 312_500_00L  // ₹3,12,500

    val sampleBookingTitle = "Photography"
    val sampleBookingIcon  = BookingIcon.Camera
    val sampleTotalPaise   = 80_000_00L  // ₹80,000
    val samplePaidPaise    = 40_000_00L  // ₹40,000

    val sampleExpensePurpose = "Flower decoration"
    val sampleExpensePaise   = 25_000_00L

    val sampleGuestName      = "Sharma Family"
    val sampleGuestCount     = 6
    val sampleGuestAddress   = "123, MG Road, Bengaluru"

    val sampleTaskTitle = "Book mandap decorator"
    val sampleTaskDue   = LocalDate.now().plusDays(10)
}
