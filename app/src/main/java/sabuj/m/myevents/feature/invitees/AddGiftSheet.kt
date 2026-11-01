package sabuj.m.myevents.feature.invitees

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
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
import sabuj.m.myevents.domain.model.Invitee

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGiftSheet(
    sheetState: InviteeSheetState.AddGift,
    events: List<Event>,
    invitees: List<Invitee>,
    onDismiss: () -> Unit,
    onGiverNameChange: (String) -> Unit,
    onEventChange: (String) -> Unit,
    onAmountChange: (Long) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onInviteeChange: (String?) -> Unit,
    onSave: () -> Unit,
) {
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
                text  = "Record Gift",
                style = titleL,
                color = AppColor.text,
            )

            // Giver Name
            GiftTextField(
                value          = sheetState.giverName,
                onValueChange  = onGiverNameChange,
                label          = "Giver Name",
                placeholder    = "e.g. Ramesh Kumar",
                isError        = sheetState.giverError != null,
                supportingText = sheetState.giverError,
            )

            // Event Selection Dropdown
            EventDropdownSelector(
                events     = events,
                selectedId = sheetState.eventId,
                onSelect   = onEventChange,
                isError    = sheetState.eventError != null,
                errorText  = sheetState.eventError,
            )

            // Amount (₹)
            val amountStr = if (sheetState.amountPaise == 0L) "" else formatIndianDigits((sheetState.amountPaise / 100L).toString())
            GiftMoneyTextField(
                value         = amountStr,
                onValueChange = { input ->
                    val digits = input.filter { it.isDigit() }
                    onAmountChange(digits.toLongOrNull()?.times(100L) ?: 0L)
                },
                label         = "Amount / Cash Gift (₹)",
                placeholder   = "0",
            )

            // Gift Description
            GiftTextField(
                value         = sheetState.description,
                onValueChange = onDescriptionChange,
                label         = "Item Description / Gift details",
                placeholder   = "e.g. Silver Lamp, Watch",
            )

            // Optional Invitee Link
            InviteeDropdownSelector(
                invitees   = invitees.filter { it.eventId == sheetState.eventId },
                selectedId = sheetState.inviteeId,
                onSelect   = onInviteeChange,
            )

            val isSaveEnabled = sheetState.giverName.isNotBlank() && sheetState.eventId.isNotBlank()
            AppButtonRow(
                positiveText    = "Save Gift",
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventDropdownSelector(
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
            value              = selectedEvent?.title ?: "",
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InviteeDropdownSelector(
    invitees: List<Invitee>,
    selectedId: String?,
    onSelect: (String?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedInvitee = invitees.find { it.id == selectedId }

    ExposedDropdownMenuBox(
        expanded         = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier         = Modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value              = selectedInvitee?.name ?: "None (General Giver)",
            onValueChange      = {},
            readOnly           = true,
            label              = { Text("Linked Guest (Optional)", style = bodyM) },
            trailingIcon       = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier           = Modifier.fillMaxWidth().menuAnchor(),
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
            DropdownMenuItem(
                text    = { Text("None (General Giver)", style = bodyM) },
                onClick = { onSelect(null); expanded = false },
            )
            invitees.forEach { inv ->
                DropdownMenuItem(
                    text    = { Text(inv.name, style = bodyM) },
                    onClick = { onSelect(inv.id); expanded = false },
                )
            }
        }
    }
}

@Composable
private fun GiftTextField(
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
private fun GiftMoneyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "0",
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
        colors         = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = AppColor.primary,
            unfocusedBorderColor = AppColor.outline,
            focusedLabelColor    = AppColor.primary,
            unfocusedLabelColor  = AppColor.text2,
            cursorColor          = AppColor.primary,
        ),
    )
}
