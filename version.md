# Version History

Current: V0.2.3

| Version | Phase | Build | Commit     | Summary                                                                                             |
| ------- | ----- | ----- |------------|-----------------------------------------------------------------------------------------------------|
| V0.2.3  | 2     | 3     | —          | Use cases: ObserveEvents, AddEvent, EditEvent, SoftDelete/Restore/HardDelete, ObserveHomeSummary   |
| V0.2.2  | 2     | 2     | 7575420    | Hilt DatabaseModule + RepositoryModule; binds EventRepository                                      |
| V0.2.1  | 2     | 1     | 8795e3d    | Domain models (Event, HomeSummary, SyncState), Room entity, DAO, DB, repository impl               |
| V0.1.7  | 1     | 7     | bfafa4b    | fix(lock): add missing display typography import in PinScreen.kt                                    |
| V0.1.6  | 1     | 6     | 7561838    | Fix BiometricPrompt (FragmentActivity) + sequential shake animation on wrong PIN                    |
| V0.1.5h | 1     | 5h    | 27cd86b    | Read LocalOpenDrawer inside AppDrawer content lambda (S1-10 drawer fix)                             |
| V0.1.5g | 1     | 5g    | 1bff950    | Fix 11 Sprint 1 visual issues (theme, PIN, biometric, menu, Home cards)                             |
| V0.1.5  | 1     | 5     | —          | Use Icons.Rounded.Work as fallback icon for BookingIcon.Other                                       |
| V0.1.4h | 1     | 4h    | a87a31e    | Add missing `dp` import in BgBlobs.kt (unresolved reference fix)                                    |
| V0.1.4g | 1     | 4g    | 58ff9bb    | Change StatusLight/Dark from anonymous objects to StatusColors data class (unresolved danger fix)   |
| V0.1.4f | 1     | 4f    | 6b1e939    | Suppress KSP kotlin.sourceSets error on AGP 9 (gradle.properties flag)                              |
| V0.1.4e | 1     | 4e    | 511d810    | Replace default launcher icon with custom app icon from Sample UI                                   |
| V0.1.4d | 1     | 4d    | 53ca5fe    | Add .idea/ and .bin/ to .gitignore (were missing per rule 11/12)                                    |
| V0.1.4c | 1     | 4c    | 1f28e40    | Add Gradle wrapper, launcher icons, res files (project files commit)                                |
| V0.1.4  | 1     | 4     | 8072d02    | version.md drives versionName; BuildConfig.APP_VERSION in Settings; google-services.json gitignored |
| V0.1.3  | 1     | 3     | 8da17b5    | App shell and security complete: splash, PIN, biometric stub, drawer, Home, Settings skeleton       |
| V0.1.2  | 1     | 2     | d37e181    | Home 7-card layout, drawer, NavHost, splash→lock→app wiring                                         |
| V0.1.1  | 1     | 1     | a9858b0    | PIN setup/unlock, Keystore HMAC, 3-attempt lockout                                                  |
| V0.1.0  | 1     | 0     | b1cec71    | Shared UI component foundation                                                                      |
| V0.0.2  | 0     | 2     | 10505e2    | AppColors, palettes, gradients, Inter font, MoneyFormatter, DateUtils, icon enums, full strings.xml |
| V0.0.1  | 0     | 1     | (pre-git)  | Fix built-in Kotlin and Hilt plugin compatibility with AGP 9                                        |
