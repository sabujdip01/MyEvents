package sabuj.m.myevents.data.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import sabuj.m.myevents.core.theme.PaletteId
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "settings_prefs")

enum class ThemeMode { System, Light, Dark }

data class UserSettings(
    val themeMode: ThemeMode = ThemeMode.System,
    val paletteId: PaletteId = PaletteId.A,
    val notificationsMaster: Boolean = true,
    val notifyEventCountdowns: Boolean = true,
    val notifyPaymentDues: Boolean = true,
    val notifyTasks: Boolean = true,
    val notifyBudgetAlerts: Boolean = true,
    val fingerprintEnabled: Boolean = true,
)

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val keyTheme = stringPreferencesKey("theme_mode")
    private val keyPalette = stringPreferencesKey("palette_id")
    private val keyMasterNotif = booleanPreferencesKey("notif_master")
    private val keyEventNotif = booleanPreferencesKey("notif_events")
    private val keyPaymentNotif = booleanPreferencesKey("notif_payments")
    private val keyTaskNotif = booleanPreferencesKey("notif_tasks")
    private val keyBudgetNotif = booleanPreferencesKey("notif_budget")
    private val keyBiometric = booleanPreferencesKey("biometric_enabled")

    val userSettingsFlow: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        UserSettings(
            themeMode             = try { ThemeMode.valueOf(prefs[keyTheme] ?: "System") } catch (e: Exception) { ThemeMode.System },
            paletteId             = try { PaletteId.valueOf(prefs[keyPalette] ?: "A") } catch (e: Exception) { PaletteId.A },
            notificationsMaster   = prefs[keyMasterNotif] ?: true,
            notifyEventCountdowns = prefs[keyEventNotif] ?: true,
            notifyPaymentDues     = prefs[keyPaymentNotif] ?: true,
            notifyTasks           = prefs[keyTaskNotif] ?: true,
            notifyBudgetAlerts    = prefs[keyBudgetNotif] ?: true,
            fingerprintEnabled    = prefs[keyBiometric] ?: true,
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[keyTheme] = mode.name }
    }

    suspend fun setPaletteId(palette: PaletteId) {
        context.dataStore.edit { it[keyPalette] = palette.name }
    }

    suspend fun setNotificationsMaster(enabled: Boolean) {
        context.dataStore.edit { it[keyMasterNotif] = enabled }
    }

    suspend fun setNotifyEventCountdowns(enabled: Boolean) {
        context.dataStore.edit { it[keyEventNotif] = enabled }
    }

    suspend fun setNotifyPaymentDues(enabled: Boolean) {
        context.dataStore.edit { it[keyPaymentNotif] = enabled }
    }

    suspend fun setNotifyTasks(enabled: Boolean) {
        context.dataStore.edit { it[keyTaskNotif] = enabled }
    }

    suspend fun setNotifyBudgetAlerts(enabled: Boolean) {
        context.dataStore.edit { it[keyBudgetNotif] = enabled }
    }

    suspend fun setFingerprintEnabled(enabled: Boolean) {
        context.dataStore.edit { it[keyBiometric] = enabled }
    }
}
