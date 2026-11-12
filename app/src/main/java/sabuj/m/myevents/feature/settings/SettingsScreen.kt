package sabuj.m.myevents.feature.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import sabuj.m.myevents.core.ui.AppSnackbar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.PaletteId
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.labelL
import sabuj.m.myevents.core.theme.labelM
import sabuj.m.myevents.core.theme.paletteColors
import sabuj.m.myevents.core.ui.AppButton
import sabuj.m.myevents.core.ui.AppButtonTonal
import sabuj.m.myevents.core.ui.AppTopBar
import sabuj.m.myevents.core.ui.ScreenScaffold
import sabuj.m.myevents.data.settings.ThemeMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsScreen(
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    SettingsScreenContent(
        uiState              = uiState,
        onMenuClick          = onMenuClick,
        onThemeMode          = viewModel::setThemeMode,
        onPaletteId          = viewModel::setPaletteId,
        onShowChangePin      = viewModel::showChangePinDialog,
        onFingerprint        = viewModel::setFingerprintEnabled,
        onNotificationsMaster = viewModel::setNotificationsMaster,
        onNotifyCountdowns   = viewModel::setNotifyEventCountdowns,
        onNotifyPaymentDues  = viewModel::setNotifyPaymentDues,
        onNotifyTasks        = viewModel::setNotifyTasks,
        onNotifyBudgetAlerts = viewModel::setNotifyBudgetAlerts,
        onSyncNow            = viewModel::syncNow,
        onShowExport         = viewModel::showExportDialog,
        onDismissSnackbar    = viewModel::snackbarDismissed,
        onChangePinConfirm   = { currentPin, newPin -> viewModel.changePin(currentPin, newPin) },
        onChangePinDismiss   = viewModel::hideChangePinDialog,
        onExportConfirm      = { eventId -> viewModel.exportEventData(context, eventId) },
        onExportDismiss      = viewModel::hideExportDialog,
        modifier             = modifier,
    )
}

@Composable
fun SettingsScreenContent(
    uiState: SettingsUiState,
    onMenuClick: () -> Unit,
    onThemeMode: (ThemeMode) -> Unit,
    onPaletteId: (PaletteId) -> Unit,
    onShowChangePin: () -> Unit,
    onFingerprint: (Boolean) -> Unit,
    onNotificationsMaster: (Boolean) -> Unit,
    onNotifyCountdowns: (Boolean) -> Unit,
    onNotifyPaymentDues: (Boolean) -> Unit,
    onNotifyTasks: (Boolean) -> Unit,
    onNotifyBudgetAlerts: (Boolean) -> Unit,
    onSyncNow: () -> Unit,
    onShowExport: () -> Unit,
    onDismissSnackbar: () -> Unit,
    onChangePinConfirm: (String, String) -> Boolean,
    onChangePinDismiss: () -> Unit,
    onExportConfirm: (String) -> Unit,
    onExportDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        val msg = uiState.snackbarMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        onDismissSnackbar()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ScreenScaffold(modifier = modifier, onMenuClick = null) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Fixed top bar (does not move while scrolling)
                AppTopBar(
                    titleText   = "Settings",
                    onMenuClick = onMenuClick,
                )
                Spacer(Modifier.height(Spacing.sm))

                // Scrollable content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .navigationBarsPadding(),
                ) {
                    // APPEARANCE
                    SettingsSection("APPEARANCE") {
                        Text("Theme mode", style = bodyM, color = AppColor.text2)
                        Spacer(Modifier.height(Spacing.xs))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            ThemeOptionChip(
                                label      = "System",
                                isSelected = uiState.settings.themeMode == ThemeMode.System,
                                onClick    = { onThemeMode(ThemeMode.System) },
                                modifier   = Modifier.weight(1f),
                            )
                            ThemeOptionChip(
                                label      = "Light",
                                isSelected = uiState.settings.themeMode == ThemeMode.Light,
                                onClick    = { onThemeMode(ThemeMode.Light) },
                                modifier   = Modifier.weight(1f),
                            )
                            ThemeOptionChip(
                                label      = "Dark",
                                isSelected = uiState.settings.themeMode == ThemeMode.Dark,
                                onClick    = { onThemeMode(ThemeMode.Dark) },
                                modifier   = Modifier.weight(1f),
                            )
                        }

                        Spacer(Modifier.height(Spacing.md))
                        HorizontalDivider(color = AppColor.outlineVariant)
                        Spacer(Modifier.height(Spacing.md))

                        Text("Colour scheme", style = bodyM, color = AppColor.text2)
                        Spacer(Modifier.height(Spacing.xs))

                        // Item I2: 56 dp 50%-50% circular swatches with 135 degree sharp split (no gradient)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            PaletteCircleSwatch(
                                name           = "Toxic Violet",
                                primaryColor   = paletteColors(PaletteId.A, isDark = false).primary,
                                containerColor = paletteColors(PaletteId.A, isDark = false).primaryContainer,
                                isSelected     = uiState.settings.paletteId == PaletteId.A,
                                onClick        = { onPaletteId(PaletteId.A) },
                                modifier       = Modifier.weight(1f),
                            )
                            PaletteCircleSwatch(
                                name           = "Machine Red",
                                primaryColor   = paletteColors(PaletteId.B, isDark = false).primary,
                                containerColor = paletteColors(PaletteId.B, isDark = false).primaryContainer,
                                isSelected     = uiState.settings.paletteId == PaletteId.B,
                                onClick        = { onPaletteId(PaletteId.B) },
                                modifier       = Modifier.weight(1f),
                            )
                            PaletteCircleSwatch(
                                name           = "Hyper Cobalt",
                                primaryColor   = paletteColors(PaletteId.C, isDark = false).primary,
                                containerColor = paletteColors(PaletteId.C, isDark = false).primaryContainer,
                                isSelected     = uiState.settings.paletteId == PaletteId.C,
                                onClick        = { onPaletteId(PaletteId.C) },
                                modifier       = Modifier.weight(1f),
                            )
                            PaletteCircleSwatch(
                                name           = "Dark Aubergine",
                                primaryColor   = paletteColors(PaletteId.D, isDark = false).primary,
                                containerColor = paletteColors(PaletteId.D, isDark = false).primaryContainer,
                                isSelected     = uiState.settings.paletteId == PaletteId.D,
                                onClick        = { onPaletteId(PaletteId.D) },
                                modifier       = Modifier.weight(1f),
                            )
                        }
                    }

                    Spacer(Modifier.height(Spacing.lg))

                    // SECURITY
                    SettingsSection("SECURITY") {
                        SettingsActionRow("Change PIN", "Update your 4-digit security PIN") {
                            onShowChangePin()
                        }
                        HorizontalDivider(color = AppColor.outlineVariant)
                        SettingsToggleRow(
                            label     = "Fingerprint unlock",
                            subtext   = "Use biometric authentication if available",
                            isChecked = uiState.settings.fingerprintEnabled,
                            onToggle  = onFingerprint,
                        )
                    }

                    Spacer(Modifier.height(Spacing.lg))

                    // NOTIFICATIONS
                    SettingsSection("NOTIFICATIONS") {
                        SettingsToggleRow(
                            label     = "Enable notifications",
                            subtext   = "Master switch for all reminders",
                            isChecked = uiState.settings.notificationsMaster,
                            onToggle  = onNotificationsMaster,
                        )
                        if (uiState.settings.notificationsMaster) {
                            HorizontalDivider(color = AppColor.outlineVariant)
                            SettingsToggleRow("Event countdowns", "30, 7, and 1 day reminders", uiState.settings.notifyEventCountdowns, onNotifyCountdowns)
                            HorizontalDivider(color = AppColor.outlineVariant)
                            SettingsToggleRow("Payment dues", "Booking and expense due alerts", uiState.settings.notifyPaymentDues, onNotifyPaymentDues)
                            HorizontalDivider(color = AppColor.outlineVariant)
                            SettingsToggleRow("Task reminders", "Item list due date reminders", uiState.settings.notifyTasks, onNotifyTasks)
                            HorizontalDivider(color = AppColor.outlineVariant)
                            SettingsToggleRow("Budget warnings", "Alerts at 80% and 100% budget spent", uiState.settings.notifyBudgetAlerts, onNotifyBudgetAlerts)
                        }
                    }

                    Spacer(Modifier.height(Spacing.lg))

                    // SYNC AND EXPORT (Items I5, I6, I7)
                    SettingsSection("SYNC AND EXPORT") {
                        val lastSync = uiState.settings.lastSyncTimestamp
                        val syncText = if (lastSync == 0L) "Never synced" else formatSyncTime(lastSync)

                        Column(modifier = Modifier.padding(vertical = Spacing.xs)) {
                            Text(text = "Last synced on: $syncText", style = bodyM, color = AppColor.text2)
                            Spacer(Modifier.height(Spacing.md))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                AppButtonTonal("Sync Now", onClick = onSyncNow, modifier = Modifier.weight(1f))
                                AppButton("Export Data (CSV)", onClick = onShowExport, modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    Spacer(Modifier.height(Spacing.lg))

                    // ABOUT
                    SettingsSection("ABOUT") {
                        SettingsRow("Version", sabuj.m.myevents.BuildConfig.APP_VERSION)
                    }

                    // Item 5: 8 dp gap after the end of the settings page
                    Spacer(Modifier.height(8.dp))
                }
            }
        }

        // Item 3: Sync toast positioned and designed same as the Undo toast
        AppSnackbar(
            hostState = snackbarHostState,
            modifier  = Modifier.align(Alignment.BottomCenter).padding(bottom = 88.dp),
        )
    }

    // Change PIN Dialog
    if (uiState.isChangePinOpen) {
        ChangePinDialog(
            onDismiss = onChangePinDismiss,
            onConfirm = onChangePinConfirm,
        )
    }

    // Export CSV Dialog
    if (uiState.isExportDialogOpen) {
        ExportCsvDialog(
            events    = uiState.events,
            onDismiss = onExportDismiss,
            onExport  = onExportConfirm,
        )
    }
}

private fun formatSyncTime(epochMs: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date(epochMs))
}

/** Item I2: 56 dp circular swatch with 135 degree primary/soft split */
@Composable
private fun PaletteCircleSwatch(
    name: String,
    primaryColor: Color,
    containerColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.xs),
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .border(
                    width = if (isSelected) 2.5.dp else 1.dp,
                    color = if (isSelected) AppColor.primary else AppColor.outlineVariant,
                    shape = CircleShape,
                )
                .drawBehind {
                    drawRect(
                        brush = Brush.linearGradient(
                            0.5f to primaryColor,
                            0.5f to containerColor,
                            start = Offset(0f, 0f),
                            end   = Offset(size.width, size.height),
                        ),
                    )
                },
        ) {
            if (isSelected) {
                Surface(
                    shape = CircleShape,
                    color = AppColor.primary,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(2.dp)
                        .size(16.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = "Selected",
                            tint = AppColor.onPrimary,
                            modifier = Modifier.size(12.dp),
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(Spacing.xs))
        Text(
            text = name,
            style = labelM,
            color = if (isSelected) AppColor.primary else AppColor.text2,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ThemeOptionChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick  = onClick,
        shape    = RoundedCornerShape(Radius.chip),
        color    = if (isSelected) AppColor.primaryContainer else AppColor.surfaceVariant,
        modifier = modifier,
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 10.dp)) {
            Text(text = label, style = labelL, color = if (isSelected) AppColor.primary else AppColor.text2)
        }
    }
}

/** Item I1: Section Header font size increased by 40% (15 sp bold uppercase) */
@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Text(
        text     = title,
        style    = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp),
        color    = AppColor.text3,
        modifier = Modifier.padding(bottom = Spacing.sm),
    )
    content()
    Spacer(Modifier.height(Spacing.md))
}

/** Item I1: Item title font size decreased by 20% (bodyM) */
@Composable
private fun SettingsRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = Spacing.sm)) {
        Text(label, style = bodyM, color = AppColor.text, fontWeight = FontWeight.SemiBold)
        if (value.isNotEmpty()) {
            Text(value, style = bodyM, color = AppColor.text2)
        }
    }
}

/** Item I1 & I3: Universal title color (AppColor.text) */
@Composable
private fun SettingsActionRow(
    label: String,
    subtext: String,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = Spacing.sm),
    ) {
        Text(label, style = bodyM, color = AppColor.text, fontWeight = FontWeight.SemiBold)
        if (subtext.isNotEmpty()) {
            Text(subtext, style = bodyM, color = AppColor.text2)
        }
    }
}

/** Item I1: Item title font size decreased by 20% (bodyM) */
@Composable
private fun SettingsToggleRow(
    label: String,
    subtext: String,
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    Row(
        modifier          = Modifier.fillMaxWidth().padding(vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = bodyM, color = AppColor.text, fontWeight = FontWeight.SemiBold)
            if (subtext.isNotEmpty()) {
                Text(subtext, style = bodyM, color = AppColor.text2)
            }
        }
        Switch(
            checked         = isChecked,
            onCheckedChange = onToggle,
            colors          = SwitchDefaults.colors(
                checkedThumbColor    = AppColor.onPrimary,
                checkedTrackColor    = AppColor.primary,
                uncheckedThumbColor  = AppColor.text3,
                uncheckedTrackColor  = AppColor.surfaceVariant,
                uncheckedBorderColor = AppColor.outline,
            ),
        )
    }
}

@LightDarkPreviews
@Composable
private fun SettingsScreenPreview() {
    AppTheme {
        SettingsScreenContent(
            uiState               = SettingsUiState(),
            onMenuClick           = {},
            onThemeMode           = {},
            onPaletteId           = {},
            onShowChangePin       = {},
            onFingerprint         = {},
            onNotificationsMaster = {},
            onNotifyCountdowns    = {},
            onNotifyPaymentDues   = {},
            onNotifyTasks         = {},
            onNotifyBudgetAlerts  = {},
            onSyncNow             = {},
            onShowExport          = {},
            onDismissSnackbar     = {},
            onChangePinConfirm    = { _, _ -> true },
            onChangePinDismiss    = {},
            onExportConfirm       = {},
            onExportDismiss       = {},
        )
    }
}
