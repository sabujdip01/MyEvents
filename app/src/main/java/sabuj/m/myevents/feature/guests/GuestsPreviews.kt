package sabuj.m.myevents.feature.guests

import androidx.compose.runtime.Composable
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.preview.PreviewData
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.domain.model.InvitationType

@LightDarkPreviews
@Composable
private fun AddGuestSheetPreview() {
    AppTheme {
        AddEditGuestSheet(
            sheetState             = GuestSheetState.AddGuest(
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
