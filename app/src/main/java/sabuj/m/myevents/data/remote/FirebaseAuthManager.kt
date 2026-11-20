package sabuj.m.myevents.data.remote

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/** Manages Firebase Authentication for mandatory login and cloud sync. */
@Singleton
class FirebaseAuthManager @Inject constructor() {

    private val auth: FirebaseAuth get() = FirebaseAuth.getInstance()

    val currentUser: FirebaseUser? get() = auth.currentUser

    val currentUid: String? get() = auth.currentUser?.uid

    val isSignedIn: Boolean get() = auth.currentUser != null && auth.currentUser?.isAnonymous == false

    suspend fun ensureSignedIn(): String {
        val user = auth.currentUser
        if (user != null) return user.uid

        val result = auth.signInAnonymously().await()
        return result.user?.uid ?: throw IllegalStateException("Firebase user is null after anonymous sign-in")
    }

    suspend fun signInWithEmail(email: String, pass: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, pass).await()
            val user = result.user ?: error("Sign in failed")
            Result.success(user)
        } catch (e: Exception) {
            // Fallback: If user does not exist, create the account automatically
            try {
                val createResult = auth.createUserWithEmailAndPassword(email, pass).await()
                val user = createResult.user ?: error("Sign up failed")
                Result.success(user)
            } catch (createErr: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun signInWithCredential(credential: AuthCredential): Result<FirebaseUser> = runCatching {
        val result = auth.signInWithCredential(credential).await()
        result.user ?: error("Google Sign in failed")
    }

    fun signOut() {
        auth.signOut()
    }
}
