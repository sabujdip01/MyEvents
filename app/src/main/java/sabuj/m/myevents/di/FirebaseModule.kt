package sabuj.m.myevents.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides Firebase singletons.
 * [FirebaseAuth] and [FirebaseFirestore] are provided here so they can be
 * injected anywhere in the app without referencing Firebase.getInstance() directly.
 */
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    /** Provides the default [FirebaseAuth] instance. */
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    /** Provides the default [FirebaseFirestore] instance. */
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
}

