package sabuj.m.myevents.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.labelL
import sabuj.m.myevents.core.theme.titleM
import sabuj.m.myevents.domain.model.Event

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportCsvDialog(
    events: List<Event>,
    onDismiss: () -> Unit,
    onExport: (eventId: String) -> Unit,
) {
    var selectedEventId by remember { mutableStateOf(events.firstOrNull()?.id ?: "") }
    var expanded by remember { mutableStateOf(false) }
    val selectedEvent = events.find { it.id == selectedEventId }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape  = RoundedCornerShape(Radius.dialog),
            colors = CardDefaults.cardColors(containerColor = AppColor.surface),
            modifier = Modifier.fillMaxWidth().padding(Spacing.md),
        ) {
            Column(
                modifier = Modifier.padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Text(text = "Export Event Data (CSV)", style = titleM, color = AppColor.text)
                Text(text = "Exports Expenses & Invitees for Excel in UTF-8 BOM CSV format.", style = bodyM, color = AppColor.text2)

                ExposedDropdownMenuBox(
                    expanded         = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier         = Modifier.fillMaxWidth(),
                ) {
                    OutlinedTextField(
                        value              = selectedEvent?.title ?: "Select Event",
                        onValueChange      = {},
                        readOnly           = true,
                        label              = { Text("Select Event", style = bodyM) },
                        trailingIcon       = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier           = Modifier.fillMaxWidth().menuAnchor(),
                        shape              = RoundedCornerShape(Radius.field),
                        colors             = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = AppColor.primary,
                            unfocusedBorderColor = AppColor.outline,
                        ),
                    )
                    ExposedDropdownMenu(
                        expanded         = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        events.forEach { ev ->
                            DropdownMenuItem(
                                text    = { Text(ev.title, style = bodyM) },
                                onClick = { selectedEventId = ev.id; expanded = false },
                            )
                        }
                    }
                }

                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", style = labelL, color = AppColor.text2)
                    }
                    Spacer(Modifier.height(Spacing.xs))
                    TextButton(
                        enabled = selectedEventId.isNotBlank(),
                        onClick = { onExport(selectedEventId) },
                    ) {
                        Text("Export", style = labelL, color = AppColor.primary)
                    }
                }
            }
        }
    }
}

@sabuj.m.myevents.core.preview.LightDarkPreviews
@Composable
private fun ExportCsvDialogPreview() {
    sabuj.m.myevents.core.theme.AppTheme {
        ExportCsvDialog(
            events    = sabuj.m.myevents.core.preview.PreviewData.sampleEvents,
            onDismiss = {},
            onExport  = {},
        )
    }
}
