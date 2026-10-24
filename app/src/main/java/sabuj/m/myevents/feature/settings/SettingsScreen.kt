package sabuj.m.myevents.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.labelS
import sabuj.m.myevents.core.theme.titleL
import sabuj.m.myevents.core.theme.titleM
import sabuj.m.myevents.core.ui.ScreenScaffold

/**
 * Settings screen skeleton — Phase 1.
 * Shows version, theme switch placeholder and Change PIN entry.
 * Fully implemented in Phase 8.
 */
@Composable
fun SettingsScreen(
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ScreenScaffold(modifier = modifier, onMenuClick = onMenuClick) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(top = 64.dp) // below menu button
                .verticalScroll(rememberScrollState()),
        ) {
            Text("Settings", style = titleL, color = AppColor.text)
            Spacer(Modifier.height(Spacing.xl2))

            SettingsSection("APPEARANCE") {
                SettingsRow("Theme", "Light / Dark / System")
                HorizontalDivider(color = AppColor.outlineVariant)
                SettingsRow("Colour scheme", "Toxic Violet")
            }

            Spacer(Modifier.height(Spacing.lg))

            SettingsSection("SECURITY") {
                SettingsRow("Change PIN", "")
                HorizontalDivider(color = AppColor.outlineVariant)
                SettingsRow("Fingerprint unlock", "On")
            }

            Spacer(Modifier.height(Spacing.lg))

            SettingsSection("ABOUT") {
                SettingsRow("Version", "V0.1.3") // TODO: read from BuildConfig.APP_VERSION
            }
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
    Column(modifier = Modifier.padding(vertical = Spacing.md)) {
        Text(label, style = titleM, color = AppColor.text)
        if (value.isNotEmpty()) {
            Text(value, style = bodyM, color = AppColor.text2)
        }
    }
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@LightDarkPreviews
@Composable
private fun SettingsScreenPreview() {
    AppTheme {
        SettingsScreen(onMenuClick = {})
    }
}
