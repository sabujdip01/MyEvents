package sabuj.m.myevents.core.preview

import sabuj.m.myevents.core.icons.BookingIcon
import sabuj.m.myevents.core.icons.EventIcon
import sabuj.m.myevents.domain.model.Booking
import sabuj.m.myevents.domain.model.BookingPayment
import sabuj.m.myevents.domain.model.Event
import sabuj.m.myevents.domain.model.SyncState
import java.time.LocalDate
import java.util.UUID

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

    /** A handful of sample [Event]s for calendar previews. */
    val sampleEvents: List<Event> = listOf(
        Event(
            id               = "evt-preview-1",
            title            = "Engagement",
            eventName        = "Engagement",
            date             = LocalDate.now().plusDays(10),
            icon             = EventIcon.Engagement,
            budgetPaise      = 2_00_000_00L,
            inviteesRequired = true,
            createdAt        = System.currentTimeMillis(),
            updatedAt        = System.currentTimeMillis(),
            syncState        = SyncState.PENDING,
        ),
        Event(
            id               = "evt-preview-2",
            title            = "Riya & Raj Wedding",
            eventName        = "Wedding",
            date             = LocalDate.now().plusDays(45),
            icon             = EventIcon.Wedding,
            budgetPaise      = 5_00_000_00L,
            inviteesRequired = true,
            createdAt        = System.currentTimeMillis(),
            updatedAt        = System.currentTimeMillis(),
            syncState        = SyncState.PENDING,
        ),
    )

    val sampleEventsByDate: Map<Long, Event> =
        sampleEvents.associateBy { it.date.toEpochDay() }

    /** Sample payments for the booking preview. */
    private val samplePayments: List<BookingPayment> = listOf(
        BookingPayment(
            id          = "pay-preview-1",
            bookingId   = "bk-preview-1",
            date        = LocalDate.now().minusDays(20),
            amountPaise = 20_000_00L,
            note        = "Advance",
            createdAt   = System.currentTimeMillis(),
            updatedAt   = System.currentTimeMillis(),
        ),
        BookingPayment(
            id          = "pay-preview-2",
            bookingId   = "bk-preview-1",
            date        = LocalDate.now().minusDays(5),
            amountPaise = 20_000_00L,
            note        = "Second instalment",
            createdAt   = System.currentTimeMillis(),
            updatedAt   = System.currentTimeMillis(),
        ),
    )

    /** A sample [Booking] with two payments for preview composables. */
    val sampleBooking: Booking = Booking(
        id               = "bk-preview-1",
        eventId          = "evt-preview-2",
        title            = "Photography",
        bookingType      = "Camera",
        bookingIcon      = BookingIcon.Camera,
        contactName      = "Rahul Sharma",
        phone            = "9876543210",
        totalAmountPaise = 80_000_00L,
        nextDueDate      = LocalDate.now().plusDays(15),
        payments         = samplePayments,
        createdAt        = System.currentTimeMillis(),
        updatedAt        = System.currentTimeMillis(),
        syncState        = SyncState.PENDING,
        deleted          = false,
    )
}
