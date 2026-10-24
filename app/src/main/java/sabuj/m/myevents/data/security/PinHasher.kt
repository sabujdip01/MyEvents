package sabuj.m.myevents.data.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Hashes and verifies PINs using HMAC-SHA256 with a key stored in Android Keystore.
 * The key never leaves the secure hardware (or software KeyStore on older devices).
 *
 * The stored value format: `base64(hmac)` — no salt needed because the Keystore
 * key itself is the secret and is unique per device/install.
 */
@Singleton
class PinHasher @Inject constructor() {

    private val keyAlias = "myevents_pin_key"
    private val keyStore = KeyStore.getInstance("AndroidKeyStore").also { it.load(null) }

    /** Returns a Base64-encoded HMAC of [pin]. */
    fun hash(pin: String): String {
        val key = getOrCreateKey()
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(key)
        val bytes = mac.doFinal(pin.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    /** Returns true if [pin] hashes to [storedHash]. */
    fun verify(pin: String, storedHash: String): Boolean =
        hash(pin) == storedHash

    // ---------------------------------------------------------------------------
    // Keystore helpers
    // ---------------------------------------------------------------------------

    private fun getOrCreateKey(): SecretKey {
        if (keyStore.containsAlias(keyAlias)) {
            val entry = keyStore.getEntry(keyAlias, null) as KeyStore.SecretKeyEntry
            return entry.secretKey
        }
        val spec = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY,
        ).setDigests(KeyProperties.DIGEST_SHA256).build()

        val kg = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_HMAC_SHA256, "AndroidKeyStore")
        kg.init(spec)
        return kg.generateKey()
    }
}
