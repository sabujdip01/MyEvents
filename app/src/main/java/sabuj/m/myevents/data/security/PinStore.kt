package sabuj.m.myevents.data.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Persists the PIN hash and fingerprint-enabled flag using [EncryptedSharedPreferences].
 * The data is encrypted with the device's MasterKey (AES-256-GCM).
 */
@Singleton
class PinStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "myevents_pin_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    /** True if a PIN hash is stored (setup is complete). */
    val isPinSet: Boolean get() = prefs.contains(KEY_PIN_HASH)

    /** Stores the PIN hash. Replaces any existing hash. */
    fun savePin(hash: String) = prefs.edit().putString(KEY_PIN_HASH, hash).apply()

    /** Returns the stored hash, or null if no PIN is set. */
    fun getHash(): String? = prefs.getString(KEY_PIN_HASH, null)

    /** Clears the stored PIN (used during Change PIN flow). */
    fun clearPin() = prefs.edit().remove(KEY_PIN_HASH).apply()

    /** Whether biometric (fingerprint) unlock is enabled. */
    var isBiometricEnabled: Boolean
        get() = prefs.getBoolean(KEY_BIOMETRIC, true) // default on
        set(value) { prefs.edit().putBoolean(KEY_BIOMETRIC, value).apply() }

    // -----------------------------------------------------------------------
    // Background timestamp — used to re-lock after 60 s in background
    // -----------------------------------------------------------------------

    /**
     * Records the epoch-millisecond time when the app went to background.
     * Pass -1 to clear (app is in foreground).
     */
    fun saveBackgroundTime(epochMs: Long) =
        prefs.edit().putLong(KEY_BG_TIME, epochMs).apply()

    /**
     * Returns the epoch-millisecond time saved by [saveBackgroundTime],
     * or -1 if not set.
     */
    fun getBackgroundTime(): Long = prefs.getLong(KEY_BG_TIME, -1L)

    companion object {
        private const val KEY_PIN_HASH  = "pin_hash"
        private const val KEY_BIOMETRIC = "biometric_enabled"
        private const val KEY_BG_TIME   = "background_time"
    }
}
