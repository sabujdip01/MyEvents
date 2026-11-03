package sabuj.m.myevents.core.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import sabuj.m.myevents.domain.model.Expense
import sabuj.m.myevents.domain.model.Invitee
import java.io.File
import java.io.FileOutputStream

/** Utility for exporting Expenses and Invitees to UTF-8 BOM CSV files. */
object CsvWriter {

    fun exportAndShare(
        context: Context,
        eventTitle: String,
        expenses: List<Expense>,
        invitees: List<Invitee>,
    ) {
        val fileName = "${eventTitle.replace("\\s+".toRegex(), "_")}_Export.csv"
        val exportDir = File(context.cacheDir, "csv_exports").apply { mkdirs() }
        val file = File(exportDir, fileName)

        val bom = "\uFEFF"
        val sb = StringBuilder()
        sb.append(bom)

        sb.append("=== EXPENSES ===\n")
        sb.append("Purpose,Amount (Rs),Due (Rs),Items,Status\n")
        expenses.forEach { exp ->
            val purpose = exp.purpose.replace(",", " ")
            val items = exp.items.joinToString("; ").replace(",", " ")
            val status = if (exp.allReceived) "Received" else "Pending"
            sb.append("\"$purpose\",${exp.amountPaise / 100L},${exp.amountDuePaise / 100L},\"$items\",$status\n")
        }

        sb.append("\n=== INVITEES ===\n")
        sb.append("Name,Expected Guests,Address,Invited Status,Note\n")
        invitees.forEach { inv ->
            val name = inv.name.replace(",", " ")
            val address = inv.address.replace(",", " ")
            val note = inv.note.replace(",", " ")
            val status = if (inv.invited) "Invited" else "Pending"
            sb.append("\"$name\",${inv.expectedCount},\"$address\",$status,\"$note\"\n")
        }

        FileOutputStream(file).use { out ->
            out.write(sb.toString().toByteArray(Charsets.UTF_8))
        }

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "$eventTitle Export")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Export CSV"))
    }
}

