package sabuj.m.myevents.feature.expenses

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.labelL
import sabuj.m.myevents.core.theme.labelM
import sabuj.m.myevents.core.theme.titleL
import sabuj.m.myevents.core.ui.AppButtonRow
import sabuj.m.myevents.domain.model.Event

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditExpenseSheet(
    sheetState: ExpenseSheetState,
    events: List<Event>,
    onDismiss: () -> Unit,
    onPurposeChange: (String) -> Unit,
    onToggleEventSelection: (String) -> Unit,
    onTotalAmountChange: (Long) -> Unit,
    onAmountDueChange: (Long) -> Unit,
    onItemChange: (index: Int, text: String) -> Unit,
    onRemoveItem: (index: Int) -> Unit,
    onAllReceivedChange: (Boolean) -> Unit,
    onSave: () -> Unit,
) {
    val isEdit = sheetState is ExpenseSheetState.Edit
    val purpose: String
    val selectedEventIds: Set<String>
    val totalAmountPaise: Long
    val amountDuePaise: Long
    val items: List<String>
    val allReceived: Boolean
    val purposeError: String?
    val eventsError: String?
    val amountError: String?

    when (sheetState) {
        is ExpenseSheetState.Add -> {
            purpose = sheetState.purpose
            selectedEventIds = sheetState.selectedEventIds
            totalAmountPaise = sheetState.totalAmountPaise
            amountDuePaise = sheetState.amountDuePaise
            items = sheetState.items
            allReceived = sheetState.allReceived
            purposeError = sheetState.purposeError
            eventsError = sheetState.eventsError
            amountError = sheetState.amountError
        }
        is ExpenseSheetState.Edit -> {
            purpose = sheetState.purpose
            selectedEventIds = setOf(sheetState.eventId)
            totalAmountPaise = sheetState.totalAmountPaise
            amountDuePaise = sheetState.amountDuePaise
            items = sheetState.items
            allReceived = sheetState.allReceived
            purposeError = sheetState.purposeError
            eventsError = null
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

            // Multi-event selection chips
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(text = "For Events", style = labelM, color = AppColor.text2)
                if (eventsError != null) {
                    Text(text = eventsError, style = bodyM, color = AppColor.danger)
                }
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    verticalArrangement   = Arrangement.spacedBy(Spacing.sm),
                ) {
                    events.forEach { event ->
                        val isSelected = selectedEventIds.contains(event.id)
                        val chipBg = if (isSelected) AppColor.primaryContainer else AppColor.surfaceVariant
                        val chipBorder = if (isSelected) AppColor.primary else AppColor.outline
                        val chipTextColor = if (isSelected) AppColor.primary else AppColor.text

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(Radius.chip))
                                .background(chipBg)
                                .border(1.dp, chipBorder, RoundedCornerShape(Radius.chip))
                                .clickable { if (!isEdit) onToggleEventSelection(event.id) }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector        = Icons.Rounded.Check,
                                    contentDescription = null,
                                    tint               = AppColor.primary,
                                    modifier           = Modifier.size(16.dp),
                                )
                                Spacer(Modifier.width(4.dp))
                            }
                            Text(text = event.title, style = labelL, color = chipTextColor)
                        }
                    }
                }
            }

            // Total Amount (₹)
            val totalStr = if (totalAmountPaise == 0L) "" else formatIndianDigits((totalAmountPaise / 100L).toString())
            ExpenseMoneyTextField(
                value          = totalStr,
                onValueChange  = { input ->
                    val digits = input.filter { it.isDigit() }
                    onTotalAmountChange(digits.toLongOrNull()?.times(100L) ?: 0L)
                },
                label          = "Total Amount (₹)",
                placeholder    = "0",
                isError        = amountError != null,
                supportingText = amountError,
            )

            // Amount Due (₹)
            val dueStr = if (amountDuePaise == 0L) "" else formatIndianDigits((amountDuePaise / 100L).toString())
            ExpenseMoneyTextField(
                value         = dueStr,
                onValueChange = { input ->
                    val digits = input.filter { it.isDigit() }
                    onAmountDueChange(digits.toLongOrNull()?.times(100L) ?: 0L)
                },
                label         = "Amount Due / Pending (₹)",
                placeholder   = "0",
            )

            // Dynamic items list
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(text = "Items List", style = labelM, color = AppColor.text2)

                items.forEachIndexed { index, itemText ->
                    Row(
                        modifier          = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    ) {
                        ExpenseTextField(
                            value         = itemText,
                            onValueChange = { onTextChange -> onItemChange(index, onTextChange) },
                            label         = "Item ${index + 1}",
                            placeholder   = "e.g. Roses, Stage backdrop",
                            modifier      = Modifier.weight(1f),
                        )
                        if (items.size > 1 && itemText.isNotBlank()) {
                            IconButton(onClick = { onRemoveItem(index) }) {
                                Icon(
                                    imageVector        = Icons.Rounded.Close,
                                    contentDescription = "Remove item",
                                    tint               = AppColor.text3,
                                )
                            }
                        }
                    }
                }
            }

            // All items received toggle
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text     = "All items collected / received",
                    style    = labelL,
                    color    = AppColor.text,
                    modifier = Modifier.weight(1f),
                )
                Switch(
                    checked         = allReceived,
                    onCheckedChange = onAllReceivedChange,
                    colors          = SwitchDefaults.colors(
                        checkedThumbColor    = AppColor.onPrimary,
                        checkedTrackColor    = AppColor.primary,
                        uncheckedThumbColor  = AppColor.text3,
                        uncheckedTrackColor  = AppColor.surfaceVariant,
                        uncheckedBorderColor = AppColor.outline,
                    ),
                )
            }

            // Action buttons
            val isSaveEnabled = purpose.isNotBlank() && (isEdit || selectedEventIds.isNotEmpty()) && totalAmountPaise > 0
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
