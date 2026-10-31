package sabuj.m.myevents.domain.usecase

import sabuj.m.myevents.domain.model.BookingPayment
import sabuj.m.myevents.domain.repository.BookingRepository
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

/** Validation result for add payment. */
sealed class PaymentSaveResult {
    object Success : PaymentSaveResult()
    object AmountInvalid : PaymentSaveResult()
}

/**
 * Validates and persists a new payment record against a booking.
 * Amount must be > 0.
 */
class AddPaymentUseCase @Inject constructor(
    private val repo: BookingRepository,
) {
    suspend operator fun invoke(
        bookingId: String,
        date: LocalDate,
        amountPaise: Long,
        note: String,
    ): PaymentSaveResult {
        if (amountPaise <= 0) return PaymentSaveResult.AmountInvalid

        val now = System.currentTimeMillis()
        val payment = BookingPayment(
            id          = UUID.randomUUID().toString(),
            bookingId   = bookingId,
            date        = date,
            amountPaise = amountPaise,
            note        = note.trim(),
            createdAt   = now,
            updatedAt   = now,
        )
        repo.addPayment(payment)
        return PaymentSaveResult.Success
    }
}
