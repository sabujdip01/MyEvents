package sabuj.m.myevents.feature.settings

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
import sabuj.m.myevents.core.theme.labelS
import sabuj.m.myevents.core.theme.paletteColors
import sabuj.m.myevents.core.theme.titleL
import sabuj.m.myevents.core.theme.titleM
import sabuj.m.myevents.core.ui.AppButton
import sabuj.m.myevents.core.ui.AppButtonTonal
import sabuj.m.myevents.core.ui.ScreenScaffold
import sabuj.m.myevents.data.settings.ThemeMode

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
        onLoadSampleData     = viewModel::loadSampleData,
        onClearSampleData    = viewModel::clearSampleData,
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
    onLoadSampleData: () -> Unit,
    onClearSampleData: () -> Unit,
    onShowExport: () -> Unit,
    onDismissSnackbar: () -> Unit,
    onChangePinConfirm: (String, String) -> Boolean,
    onChangePinDismiss: () -> Unit,
    onExportConfirm: (String) -> Unit,
    onExportDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        val msg = uiState.snackbarMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        onDismissSnackbar()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ScreenScaffold(modifier = modifier, onMenuClick = onMenuClick) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = Spacing.lg)
                    .padding(top = 64.dp) // below menu button
                    .verticalScroll(rememberScrollState()),
            ) {
                Text("Settings", style = titleL, color = AppColor.text)
                Spacer(Modifier.height(Spacing.xl))

                // APPEARANCE
                SettingsSection("APPEARANCE") {
                    Text("Theme Mode", style = labelM, color = AppColor.text2)
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

                    Text("Colour Scheme", style = labelM, color = AppColor.text2)
                    Spacer(Modifier.height(Spacing.xs))

                    // 4 Palette Swatch Tiles
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        PaletteTileCard(
                            paletteId      = PaletteId.A,
                            name           = "Toxic Violet",
                            primaryColor   = paletteColors(PaletteId.A, isDark = false).primary,
                            containerColor = paletteColors(PaletteId.A, isDark = false).primaryContainer,
                            isSelected     = uiState.settings.paletteId == PaletteId.A,
                            onClick        = { onPaletteId(PaletteId.A) },
                            modifier       = Modifier.weight(1f),
                        )
                        PaletteTileCard(
                            paletteId      = PaletteId.B,
                            name           = "Machine Red",
                            primaryColor   = paletteColors(PaletteId.B, isDark = false).primary,
                            containerColor = paletteColors(PaletteId.B, isDark = false).primaryContainer,
                            isSelected     = uiState.settings.paletteId == PaletteId.B,
                            onClick        = { onPaletteId(PaletteId.B) },
                            modifier       = Modifier.weight(1f),
                        )
                        PaletteTileCard(
                            paletteId      = PaletteId.C,
                            name           = "Hyper Cobalt",
                            primaryColor   = paletteColors(PaletteId.C, isDark = false).primary,
                            containerColor = paletteColors(PaletteId.C, isDark = false).primaryContainer,
                            isSelected     = uiState.settings.paletteId == PaletteId.C,
                            onClick        = { onPaletteId(PaletteId.C) },
                            modifier       = Modifier.weight(1f),
                        )
                        PaletteTileCard(
                            paletteId      = PaletteId.D,
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
                        label     = "Fingerprint Unlock",
                        subtext   = "Use biometric authentication if available",
                        isChecked = uiState.settings.fingerprintEnabled,
                        onToggle  = onFingerprint,
                    )
                }

                Spacer(Modifier.height(Spacing.lg))

                // NOTIFICATIONS
                SettingsSection("NOTIFICATIONS") {
                    SettingsToggleRow(
                        label     = "Enable Notifications",
                        subtext   = "Master switch for all reminders",
                        isChecked = uiState.settings.notificationsMaster,
                        onToggle  = onNotificationsMaster,
                    )
                    if (uiState.settings.notificationsMaster) {
                        HorizontalDivider(color = AppColor.outlineVariant)
                        SettingsToggleRow("Event Countdowns", "30, 7, and 1 day reminders", uiState.settings.notifyEventCountdowns, onNotifyCountdowns)
                        HorizontalDivider(color = AppColor.outlineVariant)
                        SettingsToggleRow("Payment Dues", "Booking and expense due alerts", uiState.settings.notifyPaymentDues, onNotifyPaymentDues)
                        HorizontalDivider(color = AppColor.outlineVariant)
                        SettingsToggleRow("Task Reminders", "Checklist due date reminders", uiState.settings.notifyTasks, onNotifyTasks)
                        HorizontalDivider(color = AppColor.outlineVariant)
                        SettingsToggleRow("Budget Warnings", "Alerts at 80% and 100% budget spent", uiState.settings.notifyBudgetAlerts, onNotifyBudgetAlerts)
                    }
                }

                Spacer(Modifier.height(Spacing.lg))

                // DATA & EXPORT
                SettingsSection("DATA & EXPORT") {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                        modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.sm),
                    ) {
                        AppButtonTonal("Load Demo Data", onClick = onLoadSampleData, modifier = Modifier.weight(1f))
                        AppButtonTonal("Clear Demo Data", onClick = onClearSampleData, modifier = Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(Spacing.xs))
                    AppButton("Export Event Data (CSV)", onClick = onShowExport)
                }

                Spacer(Modifier.height(Spacing.lg))

                // ABOUT
                SettingsSection("ABOUT") {
                    SettingsRow("Version", sabuj.m.myevents.BuildConfig.APP_VERSION)
                }

                Spacer(Modifier.height(Spacing.xl2))
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier  = Modifier.align(Alignment.BottomCenter),
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

@Composable
private fun PaletteTileCard(
    paletteId: PaletteId,
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
                .width(72.dp)
                .height(96.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(
                    width = if (isSelected) 2.5.dp else 1.dp,
                    color = if (isSelected) AppColor.primary else AppColor.outlineVariant,
                    shape = RoundedCornerShape(20.dp),
                ),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top 56 dp is primary brand color
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(primaryColor),
                )
                // Bottom 40 dp is soft primaryContainer color
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .background(containerColor),
                )
            }

            if (isSelected) {
                Surface(
                    shape = CircleShape,
                    color = AppColor.primary,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(20.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = "Selected",
                            tint = AppColor.onPrimary,
                            modifier = Modifier.size(14.dp),
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

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Text(
        text     = title,
        style    = labelS,
        color    = AppColor.text3,
        modifier = Modifier.padding(bottom = Spacing.sm),
    )
    content()
    Spacer(Modifier.height(Spacing.md))
}

@Composable
private fun SettingsRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = Spacing.sm)) {
        Text(label, style = titleM, color = AppColor.text)
        if (value.isNotEmpty()) {
            Text(value, style = bodyM, color = AppColor.text2)
        }
    }
}

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
        Text(label, style = titleM, color = AppColor.primary)
        if (subtext.isNotEmpty()) {
            Text(subtext, style = bodyM, color = AppColor.text2)
        }
    }
}

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
            Text(label, style = titleM, color = AppColor.text)
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
            onLoadSampleData      = {},
            onClearSampleData     = {},
            onShowExport          = {},
            onDismissSnackbar     = {},
            onChangePinConfirm    = { _, _ -> true },
            onChangePinDismiss    = {},
            onExportConfirm       = {},
            onExportDismiss       = {},
        )
    }
}
