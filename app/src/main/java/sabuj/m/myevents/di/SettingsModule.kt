package sabuj.m.myevents.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt module for security-layer and settings bindings.
 * [PinHasher] and [PinStore] are @Singleton @Inject constructor classes,
 * so Hilt discovers them automatically — no explicit @Provides needed here.
 * This file is a placeholder for future bindings (e.g. BiometricHelper).
 */
@Module
@InstallIn(SingletonComponent::class)
object SettingsModule

