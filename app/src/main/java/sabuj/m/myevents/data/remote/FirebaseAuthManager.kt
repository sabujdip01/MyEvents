package sabuj.m.myevents.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/** Manages Firebase Anonymous Authentication for offline-first sync. */
@Singleton
class FirebaseAuthManager @Inject constructor() {

    private val auth: FirebaseAuth get() = FirebaseAuth.getInstance()

    val currentUser: FirebaseUser? get() = auth.currentUser

    val currentUid: String? get() = auth.currentUser?.uid

    suspend fun ensureSignedIn(): String {
        val user = auth.currentUser
        if (user != null) return user.uid

        val result = auth.signInAnonymously().await()
        return result.user?.uid ?: throw IllegalStateException("Firebase user is null after anonymous sign-in")
    }
}
