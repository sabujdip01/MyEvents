package sabuj.m.myevents.feature.expenses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.titleL
import sabuj.m.myevents.core.ui.AppButtonRow
import sabuj.m.myevents.domain.model.Event

/**
 * Add / Edit Expense Sheet — Item G7:
 * Fields: Purpose, For Event (single select, no auto-select), Amount.
 * Amount due, Item list, and All items collected toggle removed per specification.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditExpenseSheet(
    sheetState: ExpenseSheetState,
    events: List<Event>,
    onDismiss: () -> Unit,
    onPurposeChange: (String) -> Unit,
    onEventChange: (String) -> Unit,
    onTotalAmountChange: (Long) -> Unit,
    onSave: () -> Unit,
) {
    val isEdit = sheetState is ExpenseSheetState.Edit
    val purpose: String
    val eventId: String
    val totalAmountPaise: Long
    val purposeError: String?
    val eventError: String?
    val amountError: String?

    when (sheetState) {
        is ExpenseSheetState.Add -> {
            purpose = sheetState.purpose
            eventId = sheetState.eventId
            totalAmountPaise = sheetState.totalAmountPaise
            purposeError = sheetState.purposeError
            eventError = sheetState.eventError
            amountError = sheetState.amountError
        }
        is ExpenseSheetState.Edit -> {
            purpose = sheetState.purpose
            eventId = sheetState.eventId
            totalAmountPaise = sheetState.totalAmountPaise
            purposeError = sheetState.purposeError
            eventError = null
            amountError = sheetState.amountError
        }
        else -> return
    }

    val modalState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState       = modalState,
        containerColor   = AppColor.surface,
        shape            = RoundedCornerShape(topStart = Radius.sheet, topEnd = Radius.sheet),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            Text(
                text  = if (isEdit) "Edit Expense" else "Add Expense",
                style = titleL,
                color = AppColor.text,
            )

            // Purpose field
            ExpenseTextField(
                value          = purpose,
                onValueChange  = onPurposeChange,
                label          = "Purpose",
                placeholder    = "e.g. Flower decoration",
                isError        = purposeError != null,
                supportingText = purposeError,
            )

            // Single Select Event Dropdown (Item G7 — No auto-select)
            ExpenseEventDropdownSelector(
                events     = events,
                selectedId = eventId,
                onSelect   = onEventChange,
                isError    = eventError != null,
                errorText  = eventError,
            )

            // Total Amount (₹)
            val totalStr = if (totalAmountPaise == 0L) "" else formatIndianDigits((totalAmountPaise / 100L).toString())
            ExpenseMoneyTextField(
                value          = totalStr,
                onValueChange  = { input ->
                    val digits = input.filter { it.isDigit() }
                    onTotalAmountChange(digits.toLongOrNull()?.times(100L) ?: 0L)
                },
                label          = "Amount (₹)",
                placeholder    = "0",
                isError        = amountError != null,
                supportingText = amountError,
            )

            // Action buttons
            val isSaveEnabled = purpose.isNotBlank() && eventId.isNotBlank() && totalAmountPaise > 0
            AppButtonRow(
                positiveText    = if (isEdit) "Save" else "Add",
                onPositive      = onSave,
                negativeText    = "Cancel",
                onNegative      = onDismiss,
                positiveEnabled = isSaveEnabled,
            )

            Spacer(Modifier.height(Spacing.xl))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpenseEventDropdownSelector(
    events: List<Event>,
    selectedId: String,
    onSelect: (String) -> Unit,
    isError: Boolean = false,
    errorText: String? = null,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedEvent = events.find { it.id == selectedId }

    ExposedDropdownMenuBox(
        expanded         = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier         = Modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value              = selectedEvent?.title ?: "Select Event",
            onValueChange      = {},
            readOnly           = true,
            label              = { Text("For Event", style = bodyM) },
            trailingIcon       = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier           = Modifier.fillMaxWidth().menuAnchor(),
            isError            = isError,
            supportingText     = if (errorText != null) { { Text(errorText, style = bodyM) } } else null,
            shape              = RoundedCornerShape(Radius.field),
            colors             = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = AppColor.primary,
                unfocusedBorderColor = AppColor.outline,
                focusedLabelColor    = AppColor.primary,
                unfocusedLabelColor  = AppColor.text2,
                cursorColor          = AppColor.primary,
            ),
        )
        ExposedDropdownMenu(
            expanded         = expanded,
            onDismissRequest = { expanded = false },
        ) {
            events.forEach { event ->
                DropdownMenuItem(
                    text    = { Text(event.title, style = bodyM) },
                    onClick = { onSelect(event.id); expanded = false },
                )
            }
        }
    }
}

private fun formatIndianDigits(rawDigits: String): String {
    val clean = rawDigits.filter { it.isDigit() }
    val n = clean.toLongOrNull() ?: return clean
    if (n == 0L) return ""
    return java.text.NumberFormat.getNumberInstance(java.util.Locale("en", "IN")).format(n)
}

@Composable
private fun ExpenseTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isError: Boolean = false,
    supportingText: String? = null,
) {
    OutlinedTextField(
        value          = value,
        onValueChange  = onValueChange,
        label          = { Text(label, style = bodyM) },
        placeholder    = { Text(placeholder, style = bodyM, color = AppColor.text3) },
        modifier       = modifier.fillMaxWidth(),
        singleLine     = true,
        shape          = RoundedCornerShape(Radius.field),
        isError        = isError,
        supportingText = if (supportingText != null) { { Text(supportingText, style = bodyM) } } else null,
        colors         = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = AppColor.primary,
            unfocusedBorderColor = AppColor.outline,
            focusedLabelColor    = AppColor.primary,
            unfocusedLabelColor  = AppColor.text2,
            cursorColor          = AppColor.primary,
            errorBorderColor     = AppColor.danger,
        ),
    )
}

@Composable
private fun ExpenseMoneyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "0",
    isError: Boolean = false,
    supportingText: String? = null,
) {
    OutlinedTextField(
        value          = value,
        onValueChange  = onValueChange,
        label          = { Text(label, style = bodyM) },
        placeholder    = { Text(placeholder, style = bodyM, color = AppColor.text3) },
        prefix         = { Text("₹ ", style = bodyM, color = AppColor.text) },
        modifier       = modifier.fillMaxWidth(),
        singleLine     = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape          = RoundedCornerShape(Radius.field),
        isError        = isError,
        supportingText = if (supportingText != null) { { Text(supportingText, style = bodyM) } } else null,
        colors         = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = AppColor.primary,
            unfocusedBorderColor = AppColor.outline,
            focusedLabelColor    = AppColor.primary,
            unfocusedLabelColor  = AppColor.text2,
            cursorColor          = AppColor.primary,
            errorBorderColor     = AppColor.danger,
        ),
    )
}
