package sabuj.m.myevents.feature.events

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import sabuj.m.myevents.core.icons.EventCategoryIcon
import sabuj.m.myevents.core.icons.EventIcon
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.preview.PreviewData
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.ui.AppButtonRow
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.labelL
import sabuj.m.myevents.core.theme.labelM
import sabuj.m.myevents.core.theme.titleL
import sabuj.m.myevents.core.util.DateUtils
import sabuj.m.myevents.domain.model.Event
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale

/**
 * Bottom sheet for adding or editing an event.
 *
 * Simplifications per user feedback:
 *  - No separate "Title" field — event name is used as both title and name.
 *  - In Add mode: date is fixed from the calendar long-press (shown read-only).
 *  - In Edit mode: a DatePicker button lets the user change the date.
 *  - Budget is formatted with Indian grouping as the user types (₹ prefix shown).
 *  - [saveError] non-null renders an inline red error box above the buttons.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditEventSheet(
    prefilledDate: LocalDate,
    existingEvent: Event?,
    onDismiss: () -> Unit,
    saveError: String? = null,
    onSave: (title: String, eventName: String, date: LocalDate, icon: EventIcon, budgetPaise: Long, inviteesRequired: Boolean) -> Unit,
) {
    val isEdit = existingEvent != null
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange    = { target -> target != SheetValue.Hidden },
    )
    val context = LocalContext.current

    // In Edit mode the date can be changed; in Add mode it is fixed from the long-press
    var selectedDate by remember { mutableStateOf(if (existingEvent != null) existingEvent.date else prefilledDate) }

    // Form state
    var eventName by remember { mutableStateOf(existingEvent?.eventName ?: "") }
    var selectedIcon by remember { mutableStateOf(existingEvent?.icon ?: EventIcon.Wedding) }
    var budgetRaw by remember {                                         // digits only, no commas
        mutableStateOf(
            if (existingEvent != null && existingEvent.budgetPaise > 0)
                (existingEvent.budgetPaise / 100L).toString()
            else ""
        )
    }
    var inviteesRequired by remember { mutableStateOf(existingEvent?.inviteesRequired ?: false) }

    val isFormValid = eventName.isNotBlank()
    val budgetPaise = budgetRaw.toLongOrNull()?.let { it * 100L } ?: 0L

    /** Format raw digits (no commas) as Indian grouping for display while typing. */
    fun formatIndian(raw: String): String {
        val n = raw.toLongOrNull() ?: return raw
        return NumberFormat.getNumberInstance(Locale("en", "IN")).format(n)
    }

    var budgetTextFieldValue by remember {
        val formatted = if (budgetRaw.isNotEmpty()) formatIndian(budgetRaw) else ""
        mutableStateOf(TextFieldValue(text = formatted, selection = TextRange(formatted.length)))
    }

    /** Open system DatePickerDialog; updates selectedDate on confirm. */
    fun showDatePicker() {
        val cal = Calendar.getInstance().apply {
            set(selectedDate.year, selectedDate.monthValue - 1, selectedDate.dayOfMonth)
        }
        DatePickerDialog(
            context,
            { _, year, month, day ->
                selectedDate = LocalDate.of(year, month + 1, day)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH),
        ).show()
    }

    ModalBottomSheet(
        onDismissRequest    = onDismiss,
        sheetState          = sheetState,
        containerColor      = AppColor.surface,
        shape               = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
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

            // Sheet title
            Text(
                text  = if (isEdit) "Edit Event" else "Add Event",
                style = titleL,
                color = AppColor.text,
            )

            Spacer(Modifier.height(8.dp))

            // Event name — single field, used as both title and eventName on save
            AppTextField(
                value         = eventName,
                onValueChange = { eventName = it },
                label         = "Event Name",
                placeholder   = "e.g. Priya & Raj Wedding",
            )

            Spacer(Modifier.height(8.dp))

            // Date — read-only in Add mode; editable (via DatePicker) in Edit mode
            if (isEdit) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    AppTextField(
                        value         = DateUtils.formatDisplay(selectedDate),
                        onValueChange = {},
                        label         = "Event Date",
                        readOnly      = true,
                        modifier      = Modifier.weight(1f),
                    )
                    Button(
                        onClick  = { showDatePicker() },
                        modifier = Modifier.height(56.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = AppColor.primaryContainer,
                            contentColor   = AppColor.primary,
                        ),
                        shape    = RoundedCornerShape(14.dp),
                    ) {
                        Icon(
                            imageVector        = Icons.Rounded.DateRange,
                            contentDescription = "Event date",
                            modifier           = Modifier.size(20.dp),
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Change", style = labelM)
                    }
                }
            } else {
                AppTextField(
                    value         = DateUtils.formatDisplay(selectedDate),
                    onValueChange = {},
                    label         = "Event  Date",
                    readOnly      = true,
                )
            }

            Spacer(Modifier.height(10.dp))

            // Event category icon picker — 10 two-tone XML category icons in 2 rows of 5
            val categoryIconPairs = remember {
                listOf(
                    EventCategoryIcon.MarriageWedding to EventIcon.Wedding,
                    EventCategoryIcon.EngagementPreWedding to EventIcon.Engagement,
                    EventCategoryIcon.BirthdayPersonal to EventIcon.Birthday,
                    EventCategoryIcon.ReceptionPostWedding to EventIcon.Reception,
                    EventCategoryIcon.PujaReligious to EventIcon.Puja,
                    EventCategoryIcon.BirthChildhood to EventIcon.Christmas,
                    EventCategoryIcon.HoneymoonRomantic to EventIcon.Vacation,
                    EventCategoryIcon.TraditionalFamilyRituals to EventIcon.Party,
                    EventCategoryIcon.ReligiousCulturalFestivals to EventIcon.Festival,
                    EventCategoryIcon.SocialCommunity to EventIcon.Anniversary,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Event icon", style = labelM, color = AppColor.text2)
                categoryIconPairs.chunked(5).forEach { rowPairs ->
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        rowPairs.forEach { (category, icon) ->
                            val isSelected = icon == selectedIcon
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (isSelected) AppColor.primaryContainer else AppColor.surfaceVariant
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else 0.dp,
                                        color = if (isSelected) AppColor.primary else AppColor.surface,
                                        shape = RoundedCornerShape(14.dp),
                                    )
                                    .clickable { selectedIcon = icon },
                                contentAlignment = Alignment.Center,
                            ) {
                                Image(
                                    painter            = painterResource(category.drawableRes),
                                    contentDescription = category.title,
                                    modifier           = Modifier.size(32.dp),
                                    contentScale       = ContentScale.Fit,
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // Budget field — shows Indian-formatted number; keeps cursor at end
            OutlinedTextField(
                value         = budgetTextFieldValue,
                onValueChange = { input ->
                    val newDigits = input.text.filter { it.isDigit() }.take(10)
                    budgetRaw = newDigits
                    val formatted = if (newDigits.isNotEmpty()) formatIndian(newDigits) else ""
                    budgetTextFieldValue = TextFieldValue(
                        text      = formatted,
                        selection = TextRange(formatted.length)
                    )
                },
                label         = { Text("Budget (₹)", style = bodyM) },
                placeholder   = { Text("0", style = bodyM, color = AppColor.text3) },
                prefix        = { Text("₹ ", style = bodyM, color = AppColor.text2) },
                modifier      = Modifier.fillMaxWidth(),
                singleLine    = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape         = RoundedCornerShape(14.dp),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = AppColor.primary,
                    unfocusedBorderColor = AppColor.outline,
                    focusedLabelColor    = AppColor.primary,
                    unfocusedLabelColor  = AppColor.text2,
                    cursorColor          = AppColor.primary,
                ),
            )

            Spacer(Modifier.height(10.dp))

            // Invitees required toggle
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text     = "Invite Guests?",
                    style    = labelL,
                    color    = AppColor.text,
                    modifier = Modifier.weight(1f),
                )
                Switch(
                    checked         = inviteesRequired,
                    onCheckedChange = { inviteesRequired = it },
                    colors          = SwitchDefaults.colors(
                        checkedThumbColor    = AppColor.onPrimary,
                        checkedTrackColor    = AppColor.primary,
                        uncheckedThumbColor  = AppColor.text3,
                        uncheckedTrackColor  = AppColor.surfaceVariant,
                        uncheckedBorderColor = AppColor.outline,
                    ),
                )
            }

            // Inline error (e.g. date already taken)
            if (saveError != null) {
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AppColor.dangerContainer)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Text(text = saveError, style = bodyM, color = AppColor.danger)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Action buttons
            AppButtonRow(
                positiveText    = if (isEdit) "Save" else "Add",
                onPositive      = {
                    if (isFormValid) {
                        val cleanName = eventName.trim()
                        onSave(cleanName, cleanName, selectedDate, selectedIcon, budgetPaise, inviteesRequired)
                    }
                },
                negativeText    = "Cancel",
                onNegative      = onDismiss,
                positiveEnabled = isFormValid,
            )

            Spacer(Modifier.height(10.dp))
        }
    }
}

/** Reusable styled text field for the Add/Edit sheet. */
@Composable
private fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    readOnly: Boolean = false,
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        label         = { Text(label, style = bodyM) },
        placeholder   = { Text(placeholder, style = bodyM, color = AppColor.text3) },
        modifier      = modifier.fillMaxWidth(),
        readOnly      = readOnly,
        singleLine    = true,
        shape         = RoundedCornerShape(14.dp),
        colors        = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = AppColor.primary,
            unfocusedBorderColor = AppColor.outline,
            focusedLabelColor    = AppColor.primary,
            unfocusedLabelColor  = AppColor.text2,
            cursorColor          = AppColor.primary,
            disabledBorderColor  = AppColor.outlineVariant,
            disabledLabelColor   = AppColor.text3,
        ),
    )
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@LightDarkPreviews
@Composable
private fun AddEventSheetPreview() {
    AppTheme {
        AddEditEventSheet(
            prefilledDate = LocalDate.now(),
            existingEvent = null,
            onDismiss     = {},
            onSave        = { _, _, _, _, _, _ -> },
        )
    }
}

@LightDarkPreviews
@Composable
private fun EditEventSheetPreview() {
    AppTheme {
        AddEditEventSheet(
            prefilledDate = LocalDate.now(),
            existingEvent = PreviewData.sampleEvents.first(),
            onDismiss     = {},
            onSave        = { _, _, _, _, _, _ -> },
        )
    }
}
