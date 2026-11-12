package sabuj.m.myevents.feature.itemlists

import android.app.DatePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import sabuj.m.myevents.core.icons.BookingIcon
import sabuj.m.myevents.core.icons.EventIconDisplay
import sabuj.m.myevents.feature.bookings.BookingIconPickerDialog
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
import sabuj.m.myevents.core.util.DateUtils
import sabuj.m.myevents.core.util.MoneyFormatter
import sabuj.m.myevents.domain.model.Event
import sabuj.m.myevents.domain.model.TaskItem
import java.time.LocalDate
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskSheet(
    sheetState: TaskSheetState,
    events: List<Event>,
    onDismiss: () -> Unit,
    onTitleChange: (String) -> Unit,
    onIconChange: (BookingIcon) -> Unit = {},
    onEventChange: (String) -> Unit,
    onDueDateChange: (Long?) -> Unit,
    onItemChange: (index: Int, name: String, pricePaise: Long, checked: Boolean) -> Unit = { _, _, _, _ -> },
    onAddItem: () -> Unit = {},
    onRemoveItem: (Int) -> Unit = {},
    onCompletedChange: (Boolean) -> Unit = {},
    onSave: () -> Unit,
) {
    val isEdit = sheetState is TaskSheetState.Edit
    val title: String
    val icon: BookingIcon
    val eventId: String
    val dueDateEpochDay: Long?
    val totalAmountPaise: Long
    val items: List<TaskItem>
    val completed: Boolean
    val titleError: String?
    val eventError: String?

    when (sheetState) {
        is TaskSheetState.Add -> {
            title = sheetState.title
            icon = sheetState.icon
            eventId = sheetState.eventId
            dueDateEpochDay = sheetState.dueDateEpochDay
            totalAmountPaise = sheetState.totalAmountPaise
            items = sheetState.items
            completed = sheetState.completed
            titleError = sheetState.titleError
            eventError = sheetState.eventError
        }
        is TaskSheetState.Edit -> {
            title = sheetState.title
            icon = sheetState.icon
            eventId = sheetState.eventId
            dueDateEpochDay = sheetState.dueDateEpochDay
            totalAmountPaise = sheetState.totalAmountPaise
            items = sheetState.items
            completed = sheetState.completed
            titleError = sheetState.titleError
            eventError = sheetState.eventError
        }
        else -> return
    }

    val context = LocalContext.current
    var showIconPicker by remember { mutableStateOf(false) }

    if (showIconPicker) {
        BookingIconPickerDialog(
            selectedIcon = icon,
            onSelect     = onIconChange,
            onDismiss    = { showIconPicker = false },
        )
    }

    val modalState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
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
                text  = if (isEdit) "Edit Item List" else "Add Item List",
                style = titleL,
                color = AppColor.text,
            )

            Spacer(Modifier.height(8.dp))

            // Item List Title (left) + Icon Picker (right 20%)
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TaskTextField(
                    value          = title,
                    onValueChange  = onTitleChange,
                    label          = "Item list title",
                    placeholder    = "e.g. Mandap decoration items",
                    isError        = titleError != null,
                    supportingText = titleError,
                    modifier       = Modifier.weight(1f),
                )

                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(Radius.field))
                        .background(AppColor.surfaceVariant.copy(alpha = 0.5f))
                        .clickable { showIconPicker = true },
                    contentAlignment = Alignment.Center,
                ) {
                    val selIcon = icon ?: BookingIcon.Cake
                    if (selIcon.customRes != null) {
                        Image(
                            painter            = painterResource(selIcon.customRes!!),
                            contentDescription = "Select icon",
                            modifier           = Modifier.size(38.dp),
                            contentScale       = ContentScale.Fit,
                        )
                    } else {
                        Icon(
                            imageVector        = selIcon.fallback,
                            contentDescription = "Select icon",
                            tint               = AppColor.primary,
                            modifier           = Modifier.size(38.dp),
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // For Event Dropdown
            TaskEventDropdownSelector(
                events     = events,
                selectedId = eventId,
                onSelect   = onEventChange,
                isError    = eventError != null,
                errorText  = eventError,
            )

            Spacer(Modifier.height(8.dp))

            // Complete by Date Picker
            val dueDateLabel = dueDateEpochDay?.let { DateUtils.formatDisplay(LocalDate.ofEpochDay(it)) } ?: "Not set"
            TaskTextField(
                value         = dueDateLabel,
                onValueChange = {},
                label         = "Complete by (Optional)",
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

            Spacer(Modifier.height(10.dp))

            // Dynamic List of items with price + checkbox
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "List of Items & Prices", style = labelM, color = AppColor.text2)

                items.forEachIndexed { index, item ->
                    val isItemBlank = item.name.isBlank() && item.pricePaise == 0L
                    Row(
                        modifier          = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Checkbox(
                            checked         = item.checked && !isItemBlank,
                            onCheckedChange = { checked ->
                                if (!isItemBlank) {
                                    onItemChange(index, item.name, item.pricePaise, checked)
                                }
                            },
                            enabled         = !isItemBlank,
                            colors          = CheckboxDefaults.colors(
                                checkedColor = AppColor.primary,
                                disabledUncheckedColor = AppColor.outline.copy(alpha = 0.3f),
                            ),
                        )

                        OutlinedTextField(
                            value         = item.name,
                            onValueChange = { newName ->
                                onItemChange(index, newName, item.pricePaise, item.checked)
                            },
                            placeholder   = { Text("Item name", style = labelM, color = AppColor.text3) },
                            textStyle     = labelM.copy(color = AppColor.text),
                            modifier      = Modifier.weight(1f).height(48.dp),
                            singleLine    = true,
                            shape         = RoundedCornerShape(Radius.field),
                            colors        = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor   = AppColor.primary,
                                unfocusedBorderColor = AppColor.outline,
                            ),
                        )

                        val priceRupees = if (item.pricePaise == 0L) "" else (item.pricePaise / 100L).toString()
                        OutlinedTextField(
                            value         = priceRupees,
                            onValueChange = { priceText ->
                                val pricePaise = (priceText.filter { it.isDigit() }.toLongOrNull() ?: 0L) * 100L
                                onItemChange(index, item.name, pricePaise, item.checked)
                            },
                            placeholder   = { Text("₹ Price", style = labelM, color = AppColor.text3) },
                            textStyle     = labelM.copy(color = AppColor.text),
                            modifier      = Modifier.width(100.dp).height(48.dp),
                            singleLine    = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape         = RoundedCornerShape(Radius.field),
                            colors        = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor   = AppColor.primary,
                                unfocusedBorderColor = AppColor.outline,
                            ),
                        )

                        if (items.size > 1) {
                            IconButton(onClick = { onRemoveItem(index) }) {
                                Icon(Icons.Rounded.Delete, "Remove item", tint = AppColor.danger)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // Total Amount
            val calculatedTotalPaise = items.sumOf { it.pricePaise }
            TaskTextField(
                value         = MoneyFormatter.format(calculatedTotalPaise),
                onValueChange = {},
                label         = "Total amount",
                readOnly      = true,
            )

            Spacer(Modifier.height(10.dp))

            // "All item(s) collected?" toggle
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text     = "All item(s) collected?",
                    style    = labelL,
                    color    = AppColor.text,
                    modifier = Modifier.weight(1f),
                )
                Switch(
                    checked         = completed,
                    onCheckedChange = onCompletedChange,
                    colors          = SwitchDefaults.colors(
                        checkedThumbColor    = AppColor.onPrimary,
                        checkedTrackColor    = AppColor.primary,
                        uncheckedThumbColor  = AppColor.text3,
                        uncheckedTrackColor  = AppColor.surfaceVariant,
                        uncheckedBorderColor = AppColor.outline,
                    ),
                )
            }

            Spacer(Modifier.height(16.dp))

            val editTaskId = (sheetState as? TaskSheetState.Edit)?.taskId
            val initialEditState = remember(editTaskId) {
                if (sheetState is TaskSheetState.Edit) sheetState else null
            }
            val cleanCurrentItems = items.filterNot { it.name.isBlank() && it.pricePaise == 0L }
            val cleanInitialItems = initialEditState?.items?.filterNot { it.name.isBlank() && it.pricePaise == 0L } ?: emptyList()

            val hasChanges = if (initialEditState != null) {
                title.trim() != initialEditState.title.trim() ||
                icon != initialEditState.icon ||
                eventId != initialEditState.eventId ||
                dueDateEpochDay != initialEditState.dueDateEpochDay ||
                completed != initialEditState.completed ||
                cleanCurrentItems != cleanInitialItems
            } else true

            val nonBlankItems = items.filter { it.name.isNotBlank() || it.pricePaise > 0L }
            val hasAtLeastOneValidItem = nonBlankItems.isNotEmpty()
            val allNonBlankAreValid = nonBlankItems.isNotEmpty() && nonBlankItems.all { it.name.isNotBlank() && it.pricePaise > 0L }

            val isSaveEnabled = title.isNotBlank() &&
                                eventId.isNotBlank() &&
                                hasAtLeastOneValidItem &&
                                allNonBlankAreValid &&
                                hasChanges

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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TaskEventDropdownSelector(
    events: List<Event>,
    selectedId: String,
    onSelect: (String) -> Unit,
    isError: Boolean = false,
    errorText: String? = null,
) {
    Column(
        modifier            = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(text = "For event", style = labelM, color = AppColor.text2)
        if (isError && errorText != null) {
            Text(text = errorText, style = bodyM, color = AppColor.danger)
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement   = Arrangement.spacedBy(8.dp),
        ) {
            events.forEach { event ->
                val isSelected = selectedId == event.id
                val chipBg = if (isSelected) AppColor.primaryContainer else AppColor.surfaceVariant
                val chipBorder = if (isSelected) AppColor.primary else AppColor.outline
                val chipTextColor = if (isSelected) AppColor.primary else AppColor.text

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(Radius.chip))
                        .background(chipBg)
                        .border(1.dp, chipBorder, RoundedCornerShape(Radius.chip))
                        .clickable { onSelect(event.id) }
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

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@LightDarkPreviews
@Composable
private fun AddTaskSheetPreview() {
    AppTheme {
        AddEditTaskSheet(
            sheetState      = TaskSheetState.Add(eventId = PreviewData.sampleEvents.first().id),
            events          = PreviewData.sampleEvents,
            onDismiss       = {},
            onTitleChange   = {},
            onEventChange   = {},
            onDueDateChange = {},
            onSave          = {},
        )
    }
}

@LightDarkPreviews
@Composable
private fun EditTaskSheetPreview() {
    AppTheme {
        AddEditTaskSheet(
            sheetState      = TaskSheetState.Edit(
                taskId          = "task-1",
                eventId         = PreviewData.sampleEvents.first().id,
                title           = "Mandap decoration items",
                items           = listOf(
                    TaskItem("Flowers", 5000_00L, true),
                    TaskItem("Lights", 3000_00L, false),
                ),
                dueDateEpochDay = LocalDate.now().plusDays(5).toEpochDay(),
                completed       = false,
            ),
            events          = PreviewData.sampleEvents,
            onDismiss       = {},
            onTitleChange   = {},
            onEventChange   = {},
            onDueDateChange = {},
            onSave          = {},
        )
    }
}
