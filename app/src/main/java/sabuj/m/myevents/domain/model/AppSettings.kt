package sabuj.m.myevents.domain.model

/**
 * User-facing application settings persisted via [SettingsDataStore].
 * Mirrors the fields in [sabuj.m.myevents.data.settings.UserSettings].
 */
data class AppSettings(
    /** Selected colour palette identifier. */
    val paletteId: String = "violet",
    /** Whether dark mode is forced, forced-off, or follows system. */
    val themeMode: String = "SYSTEM",
    /** Whether PIN lock is enabled. */
    val pinEnabled: Boolean = false,
    /** Whether Firebase sync is active. */
    val syncEnabled: Boolean = false,
)

