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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import sabuj.m.myevents.core.ui.AppButtonTonal
import sabuj.m.myevents.core.ui.AppSnackbar
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

    var pendingExportEventId by remember { mutableStateOf<String?>(null) }

    val createDocumentLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.CreateDocument("text/csv")
    ) { uri: android.net.Uri? ->
        val eventId = pendingExportEventId
        if (uri != null && eventId != null) {
            viewModel.exportEventToUri(context, eventId, uri)
        }
        pendingExportEventId = null
    }

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
        onShowExport         = viewModel::showExportDialog,
        onDismissSnackbar    = viewModel::snackbarDismissed,
        onChangePinConfirm   = { currentPin, newPin -> viewModel.changePin(currentPin, newPin) },
        onChangePinDismiss   = viewModel::hideChangePinDialog,
        onExportConfirm      = { eventId, fileName ->
            pendingExportEventId = eventId
            createDocumentLauncher.launch(fileName)
        },
        onExportDismiss      = viewModel::hideExportDialog,
        onImportCsv          = { uri -> viewModel.importCsvData(context, uri) },
        onSignOut            = viewModel::signOut,
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
    onShowExport: () -> Unit,
    onDismissSnackbar: () -> Unit,
    onChangePinConfirm: (String, String) -> Boolean,
    onChangePinDismiss: () -> Unit,
    onExportConfirm: (String, String) -> Unit = { _, _ -> },
    onExportDismiss: () -> Unit = {},
    onImportCsv: (android.net.Uri) -> Unit = {},
    onSignOut: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        val msg = uiState.snackbarMessage ?: return@LaunchedEffect
        kotlinx.coroutines.withTimeoutOrNull(5000L) {
            snackbarHostState.showSnackbar(
                message  = msg,
                duration = androidx.compose.material3.SnackbarDuration.Indefinite,
            )
        }
        onDismissSnackbar()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ScreenScaffold(modifier = modifier, onMenuClick = null) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Fixed top bar
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

                        Text("Colour Scheme", style = bodyM, color = AppColor.text2)
                        Spacer(Modifier.height(Spacing.xs))

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

                    // ACCOUNT & CLOUD SYNC
                    SettingsSection("ACCOUNT & CLOUD SYNC") {
                        val currentUser = remember {
                            runCatching { com.google.firebase.auth.FirebaseAuth.getInstance().currentUser }.getOrNull()
                        }
                        val name = currentUser?.displayName?.ifBlank { null }
                            ?: currentUser?.email?.substringBefore("@")?.replaceFirstChar { it.uppercase() }
                            ?: "User"
                        val email = currentUser?.email?.ifBlank { null } ?: "Not signed in"

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.xs),
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = name, style = bodyM, color = AppColor.text, fontWeight = FontWeight.Bold)
                                Text(text = email, style = labelM, color = AppColor.text3)
                            }
                            AppButtonTonal("Sign Out", onClick = onSignOut)
                        }

                        Spacer(Modifier.height(Spacing.sm))
                        HorizontalDivider(color = AppColor.outlineVariant)
                        Spacer(Modifier.height(Spacing.sm))

                        val lastSync = uiState.settings.lastSyncTimestamp
                        val syncText = if (lastSync == 0L) "Real-time active" else formatSyncTime(lastSync)

                        val csvPickerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
                            contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
                        ) { uri: android.net.Uri? ->
                            uri?.let { onImportCsv(it) }
                        }

                        Column(modifier = Modifier.padding(vertical = Spacing.xs)) {
                            Text(text = "Sync status: $syncText", style = bodyM, color = AppColor.text2)
                            Spacer(Modifier.height(Spacing.md))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                AppButtonTonal("Export CSV", onClick = onShowExport, modifier = Modifier.weight(1f))
                                AppButtonTonal("Import CSV", onClick = { csvPickerLauncher.launch("text/*") }, modifier = Modifier.weight(1f))
                            }
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
                            label     = "Enable notifications",
                            subtext   = "Master switch for All Reminders",
                            isChecked = uiState.settings.notificationsMaster,
                            onToggle  = onNotificationsMaster,
                        )
                        if (uiState.settings.notificationsMaster) {
                            HorizontalDivider(color = AppColor.outlineVariant)
                            SettingsToggleRow("Event countdowns", "30, 7, and 1 day Reminders", uiState.settings.notifyEventCountdowns, onNotifyCountdowns)
                            HorizontalDivider(color = AppColor.outlineVariant)
                            SettingsToggleRow("Payment dues", "Booking and Expense Due Alerts", uiState.settings.notifyPaymentDues, onNotifyPaymentDues)
                            HorizontalDivider(color = AppColor.outlineVariant)
                            SettingsToggleRow("Task reminders", "Item list Due Date Reminders", uiState.settings.notifyTasks, onNotifyTasks)
                            HorizontalDivider(color = AppColor.outlineVariant)
                            SettingsToggleRow("Budget warnings", "Alerts at 80% and 100% Budget spent", uiState.settings.notifyBudgetAlerts, onNotifyBudgetAlerts)
                        }
                    }

                    Spacer(Modifier.height(Spacing.lg))

                    // ABOUT
                    SettingsSection("ABOUT") {
                        SettingsRow("Version", sabuj.m.myevents.BuildConfig.APP_VERSION)
                    }

                    Spacer(Modifier.height(8.dp))
                }
            }
        }

        AppSnackbar(
            hostState = snackbarHostState,
            modifier  = Modifier.align(Alignment.Center),
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
    Surface(
        shape = RoundedCornerShape(Radius.card),
        color = AppColor.surfaceVariant,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            content()
        }
    }
}

@Composable
private fun SettingsRow(
    label: String,
    value: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.xs),
    ) {
        Text(text = label, style = bodyM, color = AppColor.text, modifier = Modifier.weight(1f))
        Text(text = value, style = bodyM, color = AppColor.text2)
    }
}

@Composable
private fun SettingsActionRow(
    label: String,
    subtext: String,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.xs),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = bodyM, color = AppColor.text)
            Text(text = subtext, style = labelM, color = AppColor.text3)
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
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.xs),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = bodyM, color = AppColor.text)
            Text(text = subtext, style = labelM, color = AppColor.text3)
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor   = AppColor.onPrimary,
                checkedTrackColor   = AppColor.primary,
                uncheckedThumbColor = AppColor.text3,
                uncheckedTrackColor = AppColor.surface,
            ),
        )
    }
}
