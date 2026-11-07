package sabuj.m.myevents.feature.invitees

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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetValue
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
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.preview.PreviewData
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.labelL
import sabuj.m.myevents.core.theme.labelM
import sabuj.m.myevents.core.theme.titleL
import sabuj.m.myevents.core.ui.AppButtonRow
import sabuj.m.myevents.domain.model.Event

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditInviteeSheet(
    sheetState: InviteeSheetState,
    events: List<Event>,
    onDismiss: () -> Unit,
    onNameChange: (String) -> Unit,
    onToggleEventSelection: (String) -> Unit,
    onExpectedCountChange: (Int) -> Unit,
    onAddressChange: (String) -> Unit,
    onInvitedChange: (Boolean) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit,
) {
    val isEdit = sheetState is InviteeSheetState.EditInvitee
    val name: String
    val selectedEventIds: Set<String>
    val expectedCount: Int
    val address: String
    val invited: Boolean
    val note: String
    val nameError: String?
    val eventsError: String?

    val inviteeEvents = events.filter { it.inviteesRequired }

    when (sheetState) {
        is InviteeSheetState.AddInvitee -> {
            name = sheetState.name
            selectedEventIds = sheetState.selectedEventIds
            expectedCount = sheetState.expectedCount
            address = sheetState.address
            invited = sheetState.invited
            note = sheetState.note
            nameError = sheetState.nameError
            eventsError = sheetState.eventsError
        }
        is InviteeSheetState.EditInvitee -> {
            name = sheetState.name
            selectedEventIds = setOf(sheetState.eventId)
            expectedCount = sheetState.expectedCount
            address = sheetState.address
            invited = sheetState.invited
            note = sheetState.note
            nameError = sheetState.nameError
            eventsError = null
        }
        else -> return
    }

    val modalState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange    = { target -> target != SheetValue.Hidden },
    )

    ModalBottomSheet(
        onDismissRequest    = onDismiss,
        sheetState          = modalState,
        containerColor      = AppColor.surface,
        shape               = RoundedCornerShape(topStart = Radius.sheet, topEnd = Radius.sheet),
        dragHandle          = null,
        contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            Spacer(Modifier.height(4.dp))

            Text(
                text  = if (isEdit) "Edit Guest" else "Add Guest",
                style = titleL,
                color = AppColor.text,
            )

            Spacer(Modifier.height(8.dp))

            // Name
            InviteeTextField(
                value          = name,
                onValueChange  = onNameChange,
                label          = "Relative / Family Name",
                placeholder    = "e.g. Sharma Family",
                isError        = nameError != null,
                supportingText = nameError,
            )

            Spacer(Modifier.height(10.dp)) // field → multi-select section

            // For events multi-select
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "For Events (Invitees Required)", style = labelM, color = AppColor.text2)
                if (eventsError != null) {
                    Text(text = eventsError, style = bodyM, color = AppColor.danger)
                }
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement   = Arrangement.spacedBy(8.dp),
                ) {
                    inviteeEvents.forEach { event ->
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

            Spacer(Modifier.height(10.dp)) // multi-select → stepper section

            // Expected guests count stepper (Item H3)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Expected Guests Count", style = labelM, color = AppColor.text2)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Surface(
                        onClick  = { if (expectedCount > 1) onExpectedCountChange(expectedCount - 1) },
                        shape    = CircleShape,
                        color    = AppColor.primaryContainer,
                        modifier = Modifier.size(40.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("-", style = titleL, color = AppColor.primary)
                        }
                    }

                    Text(
                        text  = "$expectedCount",
                        style = titleL,
                        color = AppColor.text,
                    )

                    Surface(
                        onClick  = { onExpectedCountChange(expectedCount + 1) },
                        shape    = CircleShape,
                        color    = AppColor.primaryContainer,
                        modifier = Modifier.size(40.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("+", style = titleL, color = AppColor.primary)
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp)) // stepper → text field

            // Address
            InviteeTextField(
                value         = address,
                onValueChange = onAddressChange,
                label         = "Address",
                placeholder   = "e.g. 123, MG Road, Bengaluru",
            )

            Spacer(Modifier.height(8.dp)) // similar text fields

            // Note
            InviteeTextField(
                value         = note,
                onValueChange = onNoteChange,
                label         = "Note / Comments (Optional)",
                placeholder   = "e.g. Needs accommodation",
            )

            Spacer(Modifier.height(10.dp)) // field → toggle

            // Invited status switch
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text     = "Invited / Card sent",
                    style    = labelL,
                    color    = AppColor.text,
                    modifier = Modifier.weight(1f),
                )
                Switch(
                    checked         = invited,
                    onCheckedChange = onInvitedChange,
                    colors          = SwitchDefaults.colors(
                        checkedThumbColor    = AppColor.onPrimary,
                        checkedTrackColor    = AppColor.primary,
                        uncheckedThumbColor  = AppColor.text3,
                        uncheckedTrackColor  = AppColor.surfaceVariant,
                        uncheckedBorderColor = AppColor.outline,
                    ),
                )
            }

            Spacer(Modifier.height(16.dp)) // before buttons

            val isSaveEnabled = name.isNotBlank() && (isEdit || selectedEventIds.isNotEmpty())
            AppButtonRow(
                positiveText    = if (isEdit) "Save" else "Add",
                onPositive      = onSave,
                negativeText    = "Cancel",
                onNegative      = onDismiss,
                positiveEnabled = isSaveEnabled,
            )

            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun InviteeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
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
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
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

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@LightDarkPreviews
@Composable
private fun AddInviteeSheetPreview() {
    AppTheme {
        AddEditInviteeSheet(
            sheetState             = InviteeSheetState.AddInvitee(),
            events                 = PreviewData.sampleEvents,
            onDismiss              = {},
            onNameChange           = {},
            onToggleEventSelection = {},
            onExpectedCountChange  = {},
            onAddressChange        = {},
            onInvitedChange        = {},
            onNoteChange           = {},
            onSave                 = {},
        )
    }
}

@LightDarkPreviews
@Composable
private fun EditInviteeSheetPreview() {
    AppTheme {
        AddEditInviteeSheet(
            sheetState             = InviteeSheetState.EditInvitee(
                inviteeId     = "inv-1",
                eventId       = PreviewData.sampleEvents.first().id,
                name          = "Sharma Family",
                expectedCount = 4,
                address       = "123, MG Road",
                invited       = true,
                note          = "VIP guests",
            ),
            events                 = PreviewData.sampleEvents,
            onDismiss              = {},
            onNameChange           = {},
            onToggleEventSelection = {},
            onExpectedCountChange  = {},
            onAddressChange        = {},
            onInvitedChange        = {},
            onNoteChange           = {},
            onSave                 = {},
        )
    }
}
