package sabuj.m.myevents.core.util

import sabuj.m.myevents.core.icons.BookingIcon
import sabuj.m.myevents.core.icons.EventIcon
import sabuj.m.myevents.domain.model.Booking
import sabuj.m.myevents.domain.model.BookingPayment
import sabuj.m.myevents.domain.model.Event
import sabuj.m.myevents.domain.model.InvitationType
import sabuj.m.myevents.domain.model.Invitee
import sabuj.m.myevents.domain.model.SyncState
import sabuj.m.myevents.domain.model.Task
import sabuj.m.myevents.domain.model.TaskItem
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.time.LocalDate
import java.util.Calendar
import java.util.UUID

data class ImportedEventData(
    val event: Event,
    val bookings: List<Booking>,
    val tasks: List<Task>,
    val invitees: List<Invitee>,
    val extraCards: Int = 0,
)

/** Utility for parsing CSV files exported by MyEvents and restoring them into Room DB. */
object CsvReader {

    fun parse(inputStream: InputStream): ImportedEventData? {
        val reader = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8))
        val lines = reader.readLines().map { it.trim() }.filter { it.isNotBlank() }

        var currentSection = ""

        // Event fields
        var eventName = ""
        var eventId = UUID.randomUUID().toString()
        var addedOnMs = System.currentTimeMillis()
        var eventDate: LocalDate = LocalDate.now()
        var budgetPaise = 0L
        var inviteesRequired = true
        var eventIcon = EventIcon.Wedding

        // Bookings
        val bookings = mutableListOf<Booking>()
        var curBookingName = ""
        var curBookingId = ""
        var curContactName = ""
        var curPhone = ""
        var curTotalAmountPaise = 0L
        var curAdvanceAmountPaise = 0L
        var curConfirmed = true
        var curNextDueDate: LocalDate? = null
        var curBookingIcon = BookingIcon.Other

        // Item Lists
        val tasks = mutableListOf<Task>()
        var curTaskTitle = ""
        var curTaskId = ""
        var curTaskDueDate: LocalDate? = null
        var curTaskTotalPaise = 0L
        var curTaskIcon = BookingIcon.Cake
        val curTaskItems = mutableListOf<TaskItem>()
        var curItemName = ""
        var curItemPricePaise = 0L
        var curItemCollected = false

        // Guests
        val invitees = mutableListOf<Invitee>()
        var extraCards = 0

        fun flushCurrentBooking() {
            if (curBookingName.isNotBlank()) {
                val bId = if (curBookingId.isNotBlank()) curBookingId else UUID.randomUUID().toString()
                val now = System.currentTimeMillis()
                val payments = if (curAdvanceAmountPaise > 0L) {
                    listOf(
                        BookingPayment(
                            id          = UUID.randomUUID().toString(),
                            bookingId   = bId,
                            date        = LocalDate.now(),
                            amountPaise = curAdvanceAmountPaise,
                            note        = "Imported Advance",
                            createdAt   = now,
                            updatedAt   = now,
                        )
                    )
                } else emptyList()

                bookings.add(
                    Booking(
                        id               = bId,
                        eventId          = eventId,
                        title            = curBookingName,
                        bookingType      = curBookingIcon.name,
                        bookingIcon      = curBookingIcon,
                        contactName      = curContactName,
                        phone            = curPhone,
                        totalAmountPaise = curTotalAmountPaise,
                        nextDueDate      = curNextDueDate,
                        bookingMade      = curConfirmed,
                        payments         = payments,
                        createdAt        = now,
                        updatedAt        = now,
                        syncState        = SyncState.PENDING,
                        deleted          = false,
                    )
                )
                curBookingName = ""
                curBookingId = ""
                curContactName = ""
                curPhone = ""
                curTotalAmountPaise = 0L
                curAdvanceAmountPaise = 0L
                curConfirmed = true
                curNextDueDate = null
                curBookingIcon = BookingIcon.Other
            }
        }

        fun flushCurrentItem() {
            if (curItemName.isNotBlank()) {
                curTaskItems.add(TaskItem(curItemName, curItemPricePaise, curItemCollected))
                curItemName = ""
                curItemPricePaise = 0L
                curItemCollected = false
            }
        }

        fun flushCurrentTask() {
            flushCurrentItem()
            if (curTaskTitle.isNotBlank()) {
                val tId = if (curTaskId.isNotBlank()) curTaskId else UUID.randomUUID().toString()
                val now = System.currentTimeMillis()
                val itemsList = curTaskItems.toList()
                val totalPaise = if (curTaskTotalPaise > 0L) curTaskTotalPaise else itemsList.sumOf { it.pricePaise }
                tasks.add(
                    Task(
                        id               = tId,
                        eventId          = eventId,
                        title            = curTaskTitle,
                        icon             = curTaskIcon,
                        dueDate          = curTaskDueDate,
                        totalAmountPaise = totalPaise,
                        items            = itemsList,
                        completed        = itemsList.isNotEmpty() && itemsList.all { it.checked },
                        createdAt        = now,
                        updatedAt        = now,
                        syncState        = SyncState.PENDING,
                        deleted          = false,
                    )
                )
                curTaskTitle = ""
                curTaskId = ""
                curTaskDueDate = null
                curTaskTotalPaise = 0L
                curTaskIcon = BookingIcon.Cake
                curTaskItems.clear()
            }
        }

        for (rawLine in lines) {
            val line = rawLine.replace("\uFEFF", "").trim()
            if (line.startsWith("===") && line.endsWith("===")) {
                if (currentSection == "=== Bookings ===") flushCurrentBooking()
                if (currentSection == "=== Item List ===") flushCurrentTask()
                currentSection = line
                continue
            }

            when (currentSection) {
                "=== Event Details ===" -> {
                    val kv = line.split(":", limit = 2)
                    if (kv.size == 2) {
                        val k = kv[0].trim()
                        val v = kv[1].trim()
                        when (k) {
                            "Event Name" -> eventName = v
                            "Event ID" -> if (v.isNotBlank()) eventId = v
                            "Added On" -> parseDate(v)?.let {
                                val cal = Calendar.getInstance().apply { set(it.year, it.monthValue - 1, it.dayOfMonth) }
                                addedOnMs = cal.timeInMillis
                            }
                            "Event Date" -> parseDate(v)?.let { eventDate = it }
                            "Budget" -> budgetPaise = parseCurrencyPaise(v)
                            "Invite Guest?" -> inviteesRequired = v.lowercase().contains("yes")
                            "Event Icon" -> eventIcon = runCatching { EventIcon.valueOf(v) }.getOrDefault(EventIcon.Wedding)
                        }
                    }
                }

                "=== Bookings ===" -> {
                    if (line.startsWith("Serial No:")) {
                        flushCurrentBooking()
                    } else {
                        val kv = line.split(":", limit = 2)
                        if (kv.size == 2) {
                            val k = kv[0].trim()
                            val v = kv[1].trim()
                            when (k) {
                                "Booking Name" -> curBookingName = v
                                "Booking ID" -> if (v.isNotBlank()) curBookingId = v
                                "Contact Person" -> curContactName = v
                                "Phone Number" -> curPhone = v
                                "Total Amount" -> curTotalAmountPaise = parseCurrencyPaise(v)
                                "Advance Amount" -> curAdvanceAmountPaise = parseCurrencyPaise(v)
                                "Booking Confirmed?" -> curConfirmed = v.lowercase().contains("yes")
                                "Next Due" -> parseDate(v)?.let { curNextDueDate = it }
                                "Booking Icon" -> curBookingIcon = runCatching { BookingIcon.valueOf(v) }.getOrDefault(BookingIcon.Other)
                            }
                        }
                    }
                }

                "=== Item List ===" -> {
                    if (line.startsWith("Serial No:")) {
                        flushCurrentTask()
                    } else if (line == "---") {
                        flushCurrentItem()
                    } else {
                        val kv = line.split(":", limit = 2)
                        if (kv.size == 2) {
                            val k = kv[0].trim()
                            val v = kv[1].trim()
                            when (k) {
                                "Item List Name" -> curTaskTitle = v
                                "Item List ID" -> if (v.isNotBlank()) curTaskId = v
                                "Complete By" -> parseDate(v)?.let { curTaskDueDate = it }
                                "Item Name" -> curItemName = v
                                "Item Price" -> curItemPricePaise = parseCurrencyPaise(v)
                                "Item Collected?" -> curItemCollected = v.lowercase().contains("yes")
                                "Total Amount" -> curTaskTotalPaise = parseCurrencyPaise(v)
                                "Item List Icon" -> curTaskIcon = runCatching { BookingIcon.valueOf(v) }.getOrDefault(BookingIcon.Cake)
                            }
                        }
                    }
                }

                "=== Guests ===" -> {
                    if (line.startsWith("Extra Cards:")) {
                        val v = line.removePrefix("Extra Cards:").trim()
                        extraCards = v.toIntOrNull() ?: 0
                    } else if (!line.startsWith("Relative / Family Name") && !line.startsWith("---") && !line.contains("Total Families")) {
                        val cols = line.split(",")
                        if (cols.size >= 2) {
                            val name = cols[0].trim()
                            if (name.isNotBlank()) {
                                val count = cols[1].trim().toIntOrNull() ?: 1
                                val address = if (cols.size >= 3) cols[2].trim() else ""
                                val note = if (cols.size >= 4) cols[3].trim() else ""
                                val modeStr = if (cols.size >= 5) cols[4].trim() else "None"
                                val statusStr = if (cols.size >= 6) cols[5].trim() else "Not Invited"

                                val now = System.currentTimeMillis()
                                invitees.add(
                                    Invitee(
                                        id             = UUID.randomUUID().toString(),
                                        eventId        = eventId,
                                        groupId        = "",
                                        name           = name,
                                        expectedCount  = count,
                                        address        = address,
                                        invited        = statusStr.lowercase().contains("invited"),
                                        note           = note,
                                        createdAt      = now,
                                        updatedAt      = now,
                                        syncState      = SyncState.PENDING,
                                        deleted        = false,
                                        invitationType = runCatching { InvitationType.valueOf(modeStr) }.getOrDefault(InvitationType.None),
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        flushCurrentBooking()
        flushCurrentTask()

        if (eventName.isBlank()) return null

        val now = System.currentTimeMillis()
        val event = Event(
            id               = eventId,
            title            = eventName,
            eventName        = eventName,
            date             = eventDate,
            icon             = eventIcon,
            budgetPaise      = budgetPaise,
            inviteesRequired = inviteesRequired,
            createdAt        = addedOnMs,
            updatedAt        = now,
            syncState        = SyncState.PENDING,
            deleted          = false,
        )

        return ImportedEventData(
            event      = event,
            bookings   = bookings,
            tasks      = tasks,
            invitees   = invitees,
            extraCards = extraCards,
        )
    }

    private fun parseCurrencyPaise(str: String?): Long {
        if (str.isNullOrBlank()) return 0L
        val clean = str.replace("₹", "").replace(",", "").trim()
        val rupees = clean.toLongOrNull() ?: 0L
        return rupees * 100L
    }

    private fun parseDate(str: String?): LocalDate? {
        if (str.isNullOrBlank()) return null
        val clean = str.trim()
        val monthMap = mapOf(
            "jan" to 1, "feb" to 2, "mar" to 3, "apr" to 4, "may" to 5, "jun" to 6,
            "jul" to 7, "aug" to 8, "sep" to 9, "oct" to 10, "nov" to 11, "dec" to 12
        )
        return try {
            val parts = clean.split(" ", "-")
            if (parts.size == 3) {
                val day = parts[0].toIntOrNull()
                val month = monthMap[parts[1].lowercase()] ?: parts[1].toIntOrNull()
                val year = parts[2].toIntOrNull()
                if (day != null && month != null && year != null) {
                    LocalDate.of(year, month, day)
                } else null
            } else {
                LocalDate.parse(clean)
            }
        } catch (e: Exception) {
            null
        }
    }
}
