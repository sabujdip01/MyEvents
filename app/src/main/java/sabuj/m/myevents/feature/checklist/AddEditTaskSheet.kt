package sabuj.m.myevents.feature.checklist

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
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
import androidx.compose.ui.platform.LocalContext
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.titleL
import sabuj.m.myevents.core.ui.AppButtonRow
import sabuj.m.myevents.core.util.DateUtils
import sabuj.m.myevents.domain.model.Event
import java.time.LocalDate
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskSheet(
    sheetState: TaskSheetState,
    events: List<Event>,
    onDismiss: () -> Unit,
    onTitleChange: (String) -> Unit,
    onEventChange: (String) -> Unit,
    onDueDateChange: (Long?) -> Unit,
    onSave: () -> Unit,
) {
    val isEdit = sheetState is TaskSheetState.Edit
    val title: String
    val eventId: String
    val dueDateEpochDay: Long?
    val titleError: String?
    val eventError: String?

    when (sheetState) {
        is TaskSheetState.Add -> {
            title = sheetState.title
            eventId = sheetState.eventId
            dueDateEpochDay = sheetState.dueDateEpochDay
            titleError = sheetState.titleError
            eventError = sheetState.eventError
        }
        is TaskSheetState.Edit -> {
            title = sheetState.title
            eventId = sheetState.eventId
            dueDateEpochDay = sheetState.dueDateEpochDay
            titleError = sheetState.titleError
            eventError = null
        }
        else -> return
    }

    val context = LocalContext.current
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
                text  = if (isEdit) "Edit Task" else "Add Task",
                style = titleL,
                color = AppColor.text,
            )

            // Task title
            TaskTextField(
                value          = title,
                onValueChange  = onTitleChange,
                label          = "Task Title",
                placeholder    = "e.g. Book mandap decorator",
                isError        = titleError != null,
                supportingText = titleError,
            )

            // For Event Dropdown
            TaskEventDropdownSelector(
                events     = events,
                selectedId = eventId,
                onSelect   = onEventChange,
                isError    = eventError != null,
                errorText  = eventError,
            )

            // Due Date Picker
            val dueDateLabel = dueDateEpochDay?.let { DateUtils.formatDisplay(LocalDate.ofEpochDay(it)) } ?: "Not set"
            TaskTextField(
                value         = dueDateLabel,
                onValueChange = {},
                label         = "Due Date (Optional)",
                readOnly      = true,
                trailingIcon  = {
                    Icon(
                        imageVector        = Icons.Rounded.DateRange,
                        contentDescription = "Pick date",
                        tint               = AppColor.primary,
                        modifier           = Modifier.clickable {
                            val cal = Calendar.getInstance()
                            dueDateEpochDay?.let { epochDay ->
                                val d = LocalDate.ofEpochDay(epochDay)
                                cal.set(d.year, d.monthValue - 1, d.dayOfMonth)
                            }
                            DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    onDueDateChange(LocalDate.of(year, month + 1, day).toEpochDay())
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH),
                            ).show()
                        },
                    )
                },
            )

            val isSaveEnabled = title.isNotBlank() && eventId.isNotBlank()
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
private fun TaskEventDropdownSelector(
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

@Composable
private fun TaskTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    supportingText: String? = null,
) {
    OutlinedTextField(
        value          = value,
        onValueChange  = onValueChange,
        label          = { Text(label, style = bodyM) },
        placeholder    = { Text(placeholder, style = bodyM, color = AppColor.text3) },
        modifier       = modifier.fillMaxWidth(),
        readOnly       = readOnly,
        trailingIcon   = trailingIcon,
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
