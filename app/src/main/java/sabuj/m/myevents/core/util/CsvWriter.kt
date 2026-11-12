package sabuj.m.myevents.core.util

import android.content.Context
import android.net.Uri
import sabuj.m.myevents.domain.model.Booking
import sabuj.m.myevents.domain.model.Event
import sabuj.m.myevents.domain.model.Expense
import sabuj.m.myevents.domain.model.Invitee
import sabuj.m.myevents.domain.model.Task
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Utility for exporting complete Event, Bookings, Item Lists, Expenses, and Guests data to UTF-8 BOM CSV files. */
object CsvWriter {

    fun generateFileName(eventTitle: String): String {
        val now = Date()
        val timestampFileFmt = SimpleDateFormat("dd_MMM_yyyy_hh-mm-ss_a", Locale.getDefault())
        val fileTimestamp = timestampFileFmt.format(now)
        val safeEventTitle = eventTitle.replace("\\s+".toRegex(), "_")
        return "${safeEventTitle}_Export_$fileTimestamp.csv"
    }

    fun generateCsvString(
        event: Event,
        bookings: List<Booking>,
        invitees: List<Invitee>,
        tasks: List<Task>,
        expenses: List<Expense>,
        extraCards: Int,
    ): String {
        val now = Date()
        val timestampFooterFmt = SimpleDateFormat("dd MMM yyyy hh:mm:ss a", Locale.getDefault())
        val footerTimestamp = timestampFooterFmt.format(now)

        val guestCount = invitees.sumOf { it.expectedCount }

        val sb = StringBuilder()
        sb.append("\uFEFF") // UTF-8 BOM for Excel compatibility

        // === Event Details ===
        sb.append("=== Event Details ===\n")
        sb.append("Event Name: ${event.title}\n")
        sb.append("Event ID: ${event.id}\n")
        sb.append("Added On: ${formatDateFromMs(event.createdAt)}\n")
        sb.append("Event Date: ${formatDate(event.date.toString())}\n")
        sb.append("Budget: ${formatCurrency(event.budgetPaise)}\n")
        sb.append("Invite Guest?: ${if (event.inviteesRequired) "Yes" else "No"}\n")
        sb.append("Booking Count: ${bookings.size}\n")
        sb.append("Guests Count: $guestCount\n")
        sb.append("Event Icon: ${event.icon.name}\n\n")

        // === Bookings ===
        sb.append("=== Bookings ===\n")
        bookings.forEachIndexed { index, b ->
            val contactName = if (b.contactName.isNotBlank()) b.contactName else ""
            val phone = if (b.phone.isNotBlank()) b.phone else ""
            val advance = formatCurrency(b.paidPaise)
            val due = formatCurrency(b.amountLeftPaise)
            val nextDue = formatDate(b.nextDueDate?.toString())

            sb.append("Serial No: #${index + 1}\n")
            sb.append("Booking Name: ${b.title}\n")
            sb.append("Booking ID: ${b.id}\n")
            sb.append("Contact Person: $contactName\n")
            sb.append("Phone Number: $phone\n")
            sb.append("Total Amount: ${formatCurrency(b.totalAmountPaise)}\n")
            sb.append("Advance Amount: $advance\n")
            sb.append("Due Amount: $due\n")
            sb.append("Booking Confirmed?: ${if (b.bookingMade) "Yes" else "No"}\n")
            sb.append("Next Due: $nextDue\n")
            sb.append("Booking Icon: ${b.bookingIcon.name}\n\n")
        }

        // === Item List ===
        sb.append("=== Item List ===\n")
        tasks.forEachIndexed { index, task ->
            val validItems = task.items.filter { it.name.isNotBlank() }
            val completeBy = formatDate(task.dueDate?.toString())
            val taskTotalPaise = if (task.totalAmountPaise > 0L) task.totalAmountPaise else validItems.sumOf { it.pricePaise }
            val allCollected = if (task.completed || (validItems.isNotEmpty() && validItems.all { it.checked })) "Yes" else "No"

            sb.append("Serial No: #${index + 1}\n")
            sb.append("Item List Name: ${task.title}\n")
            sb.append("Item List ID: ${task.id}\n")
            sb.append("Complete By: $completeBy\n")
            sb.append("Total Items in This List: ${validItems.size}\n")

            validItems.forEachIndexed { itemIndex, item ->
                sb.append(" ---\n")
                sb.append("Item Number: #${itemIndex + 1}\n")
                sb.append("Item Name: ${item.name}\n")
                sb.append("Item Price: ${formatCurrency(item.pricePaise)}\n")
                sb.append("Item Collected?: ${if (item.checked) "Yes" else "No"}\n")
            }
            sb.append(" ---\n")
            sb.append("Total Amount: ${formatCurrency(taskTotalPaise)}\n")
            sb.append("All Items Collected?: $allCollected\n")
            sb.append("Item List Icon: ${task.icon.name}\n\n")
        }

        // === Expenses ===
        sb.append("=== Expenses ===\n")
        sb.append("Expense Name,Expense Type,Total Amount,Paid Amount,Remaining Amount,Payment Status\n")

        var totalSpendingPaise = 0L
        var totalPaidPaise = 0L
        var totalDuePaise = 0L
        var totalExpenseItemsCount = 0

        bookings.forEach { b ->
            val name = b.title.replace(",", " ")
            val type = "Booking"
            val total = formatCurrency(b.totalAmountPaise)
            val paid = formatCurrency(b.paidPaise)
            val due = formatCurrency(b.amountLeftPaise)
            val status = if (b.isFullyPaid) "Paid" else "Due"

            totalSpendingPaise += b.totalAmountPaise
            totalPaidPaise += b.paidPaise
            if (b.amountLeftPaise > 0L) totalDuePaise += b.amountLeftPaise
            totalExpenseItemsCount++

            sb.append("$name,$type,$total,$paid,$due,$status\n")
        }

        tasks.forEach { t ->
            val validItems = t.items.filter { it.name.isNotBlank() }
            val taskTotalPaise = if (t.totalAmountPaise > 0L) t.totalAmountPaise else validItems.sumOf { it.pricePaise }
            val name = t.title.replace(",", " ")
            val type = "Item List"
            val total = formatCurrency(taskTotalPaise)
            val paid = formatCurrency(taskTotalPaise)
            val due = "₹0"
            val status = "Paid"

            totalSpendingPaise += taskTotalPaise
            totalPaidPaise += taskTotalPaise
            totalExpenseItemsCount++

            sb.append("$name,$type,$total,$paid,$due,$status\n")
        }

        expenses.forEach { exp ->
            val name = exp.purpose.replace(",", " ")
            val type = "Expense"
            val total = formatCurrency(exp.amountPaise)
            val duePaise = exp.amountDuePaise
            val paidPaise = (exp.amountPaise - duePaise).coerceAtLeast(0L)
            val paid = formatCurrency(paidPaise)
            val due = formatCurrency(duePaise)
            val status = if (exp.allReceived || duePaise <= 0L) "Paid" else "Due"

            totalSpendingPaise += exp.amountPaise
            totalPaidPaise += paidPaise
            if (duePaise > 0L) totalDuePaise += duePaise
            totalExpenseItemsCount++

            sb.append("$name,$type,$total,$paid,$due,$status\n")
        }

        sb.append("   ---------------------    \n")

        val budgetStr = "Budget: " + formatCurrency(event.budgetPaise)
        val itemsStr = "Total Items: " + totalExpenseItemsCount
        val spendingStr = "Total Spending: " + formatCurrency(totalSpendingPaise)
        val paidStr = "Total Paid: " + formatCurrency(totalPaidPaise)
        val dueStr = "Total Due: " + formatCurrency(totalDuePaise)

        sb.append("$budgetStr,$itemsStr,$spendingStr,$paidStr,$dueStr\n\n")

        // === Guests ===
        sb.append("=== Guests ===\n")
        sb.append("Extra Cards: $extraCards\n")
        sb.append("Relative / Family Name,Guests Count,Address,Note,Invitation Mode,Invitation Status\n")

        var totalInvitedFamilies = 0
        var totalGuestsCount = 0
        var totalInvitedGuestsCount = 0

        invitees.forEach { inv ->
            val name = inv.name.replace(",", " ")
            val count = inv.expectedCount
            val address = if (inv.address.isNotBlank()) inv.address.replace(",", " ") else ""
            val note = if (inv.note.isNotBlank()) inv.note.replace(",", " ") else ""
            val mode = inv.invitationType.name
            val status = if (inv.invited) "Invited" else "Not Invited"

            if (inv.invited) {
                totalInvitedFamilies++
                totalInvitedGuestsCount += count
            }
            totalGuestsCount += count

            sb.append("$name,$count,$address,$note,$mode,$status\n")
        }

        sb.append("   ---------------------    \n")

        val totalFamiliesStr = "${invitees.size} Total Families"
        val invitedFamiliesStr = "$totalInvitedFamilies Invited Families"
        val totalGuestsStr = "$totalGuestsCount Total Guests"
        val cardsStr = "$extraCards Cards"
        val invitedGuestsStr = "$totalInvitedGuestsCount Already Invited Guests"

        sb.append("$totalFamiliesStr,$invitedFamiliesStr,$totalGuestsStr,$cardsStr,$invitedGuestsStr\n\n")

        // Footer Timestamp
        sb.append("===== Generated on $footerTimestamp =====\n")

        return sb.toString()
    }

    fun writeToUri(context: Context, uri: Uri, csvContent: String): Boolean {
        return try {
            context.contentResolver.openOutputStream(uri)?.use { out ->
                out.write(csvContent.toByteArray(Charsets.UTF_8))
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun formatDate(dateStr: String?): String {
        if (dateStr.isNullOrBlank()) return ""
        return try {
            val date = java.time.LocalDate.parse(dateStr)
            val fmt = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            fmt.format(java.sql.Date.valueOf(date.toString()))
        } catch (e: Exception) {
            val monthNames = arrayOf("", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
            val parts = dateStr.split("-")
            if (parts.size == 3) {
                val day = parts[2].toIntOrNull() ?: parts[2]
                val monthIdx = parts[1].toIntOrNull() ?: 0
                val monthStr = if (monthIdx in 1..12) monthNames[monthIdx] else parts[1]
                "$day $monthStr ${parts[0]}"
            } else dateStr
        }
    }

    private fun formatDateFromMs(epochMs: Long): String {
        val dateFmt = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return dateFmt.format(Date(epochMs))
    }

    private fun formatCurrency(paise: Long): String {
        val rupees = paise / 100L
        return "₹$rupees"
    }
}

