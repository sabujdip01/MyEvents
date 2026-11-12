package sabuj.m.myevents.feature.invitees

import androidx.compose.runtime.Composable
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.preview.PreviewData
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.domain.model.InvitationType

// ---------------------------------------------------------------------------
// Add/Edit Sheet previews
// ---------------------------------------------------------------------------

@LightDarkPreviews
@Composable
private fun AddInviteeSheetPreview() {
    AppTheme {
        AddEditInviteeSheet(
            sheetState             = InviteeSheetState.AddInvitee(
                name             = "Sharma Family",
                selectedEventIds = setOf(PreviewData.sampleEvents.first().id),
                expectedCount    = 4,
                address          = "123 MG Road, Bengaluru",
                invited          = true,
                invitationType   = InvitationType.Card,
                note             = "Needs hotel room",
            ),
            events                 = PreviewData.sampleEvents,
            onDismiss              = {},
            onNameChange           = {},
            onToggleEventSelection = {},
            onExpectedCountChange  = {},
            onAddressChange        = {},
            onInvitedChange        = {},
            onInvitationTypeChange = {},
            onNoteChange           = {},
            onSave                 = {},
        )
    }
}
