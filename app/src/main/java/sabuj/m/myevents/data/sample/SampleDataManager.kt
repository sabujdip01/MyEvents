package sabuj.m.myevents.data.sample

import sabuj.m.myevents.core.icons.BookingIcon
import sabuj.m.myevents.core.icons.EventIcon
import sabuj.m.myevents.domain.model.Booking
import sabuj.m.myevents.domain.model.BookingPayment
import sabuj.m.myevents.domain.model.Event
import sabuj.m.myevents.domain.model.Expense
import sabuj.m.myevents.domain.model.Invitee
import sabuj.m.myevents.domain.model.SyncState
import sabuj.m.myevents.domain.model.Task
import sabuj.m.myevents.domain.repository.BookingRepository
import sabuj.m.myevents.domain.repository.EventRepository
import sabuj.m.myevents.domain.repository.ExpenseRepository
import sabuj.m.myevents.domain.repository.InviteeRepository
import sabuj.m.myevents.domain.repository.TaskRepository
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SampleDataManager @Inject constructor(
    private val eventRepo: EventRepository,
    private val bookingRepo: BookingRepository,
    private val expenseRepo: ExpenseRepository,
    private val inviteeRepo: InviteeRepository,
    private val taskRepo: TaskRepository,
) {
    suspend fun loadSampleData() {
        val now = System.currentTimeMillis()
        val eventId1 = "demo-evt-1"
        val eventId2 = "demo-evt-2"

        // 1. Events
        val e1 = Event(
            id = eventId1, title = "Riya & Raj Wedding", eventName = "Wedding",
            date = LocalDate.now().plusDays(45), icon = EventIcon.Wedding,
            budgetPaise = 5_00_000_00L, inviteesRequired = true, createdAt = now, updatedAt = now, syncState = SyncState.PENDING,
        )
        val e2 = Event(
            id = eventId2, title = "Engagement Ceremony", eventName = "Engagement",
            date = LocalDate.now().plusDays(10), icon = EventIcon.Engagement,
            budgetPaise = 2_00_000_00L, inviteesRequired = true, createdAt = now, updatedAt = now, syncState = SyncState.PENDING,
        )
        eventRepo.upsert(e1)
        eventRepo.upsert(e2)

        // 2. Bookings
        val bk1 = Booking(
            id = "demo-bk-1", eventId = eventId1, title = "Photography & Videography",
            bookingType = "Camera", bookingIcon = BookingIcon.Camera, contactName = "Studio Clicks",
            phone = "9876543210", totalAmountPaise = 80_000_00L, nextDueDate = LocalDate.now().plusDays(15),
            payments = listOf(
                BookingPayment("demo-pay-1", "demo-bk-1", LocalDate.now().minusDays(10), 20_000_00L, "Advance", now, now)
            ),
            createdAt = now, updatedAt = now, syncState = SyncState.PENDING, deleted = false,
        )
        bookingRepo.upsertBooking(bk1)

        // 3. Expenses
        val exp1 = Expense(
            id = "demo-exp-1", eventId = eventId1, groupId = "demo-grp-1", purpose = "Stage Flower Decor",
            amountPaise = 30_000_00L, amountDuePaise = 10_000_00L, items = listOf("Marigold", "Roses", "Backdrop"),
            allReceived = false, createdAt = now, updatedAt = now,
        )
        expenseRepo.upsertExpense(exp1)

        // 4. Invitees
        val inv1 = Invitee(
            id = "demo-inv-1", eventId = eventId1, groupId = "demo-grp-2", name = "Sharma Family",
            expectedCount = 4, address = "123 MG Road, Bengaluru", invited = true, note = "Needs hotel room",
            createdAt = now, updatedAt = now,
        )
        inviteeRepo.upsertInvitee(inv1)

        // 5. Tasks
        val t1 = Task(
            id = "demo-task-1", eventId = eventId1, title = "Finalize Mandap Decorator",
            dueDate = LocalDate.now().plusDays(5), completed = false, createdAt = now, updatedAt = now,
        )
        taskRepo.upsertTask(t1)
    }

    suspend fun clearSampleData() {
        eventRepo.hardDelete("demo-evt-1")
        eventRepo.hardDelete("demo-evt-2")
    }
}
