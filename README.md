# My Events 📅🎉

An offline-first, privacy-focused Android application for managing events, vendor bookings, expenses, guest lists, and item checklists. Built natively with modern Android architecture, Jetpack Compose, Material 3, Room, Hilt, and Firebase Sync.

![Android](https://img.shields.io/badge/Platform-Android-green.svg)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)
![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-blue.svg)
![Version](https://img.shields.io/badge/Version-1.0.0-brightgreen.svg)

---

## ✨ Features

- **📅 Event Management**: Create, edit, and navigate events with interactive monthly calendar pagers, category icon pickers, and countdown pills.
- **💰 Vendor Bookings & Advance Tracking**: Track vendor contracts (Photography, Catering, Venue, Décor), installment payment logs, and advance dues.
- **📊 Tabular Expense Overview**: Excel-style 4-column read-only expense overview automatically derived from Bookings and Item Lists with sticky total footers.
- **👥 Guest & Family Tracking**: Tabular guest list management with invitation statuses (Invited/Pending), invitation modes (Call/Card), extra card counters, and 2-row sticky summary footers.
- **📋 Item Checklists**: Event item lists with dynamic price inputs, checkbox collection toggles, and total cost calculation.
- **🔒 Security & Biometrics**: 4-digit PIN lock gate backed by Keystore HMAC-SHA256 hashing, Android BiometricPrompt fingerprint authentication, and 30s lockout protection.
- **☁️ Cloud Sync (Firebase)**: Anonymous Firebase Auth and background Cloud Firestore sync using WorkManager with exponential back-off and tombstone deletion.
- **📄 CSV Import & Export**: Export event expenses and guest lists to UTF-8 BOM CSV files compatible with Excel and File Manager save pickers.
- **🎨 Custom Theming**: 4 color scheme palettes (Toxic Violet, Machine Red, Hyper Cobalt, Dark Aubergine) supporting Light, Dark, and System modes.

---

## 🛠 Tech Stack & Architecture

'My Events' follows modern Android development best practices and **Clean Architecture** principles with Unidirectional Data Flow (UDF):

- **Language**: Kotlin 2.0+ (Coroutines, StateFlow, Flows)
- **UI Framework**: Jetpack Compose, Material 3, Compose BOM
- **Local Persistence**: Room Database (API Level 29+ compatible, version 9 with explicit migrations)
- **Dependency Injection**: Hilt (Dagger Hilt)
- **Background Work**: WorkManager (Hilt WorkManager)
- **Preferences**: Preferences DataStore & EncryptedSharedPreferences
- **Cloud Backend**: Firebase Anonymous Auth & Cloud Firestore
- **Security**: Android Keystore API & BiometricPrompt
- **Architecture**: MVVM + Repository pattern with pure Kotlin UseCases

---

## 📁 Project Structure

```
MyEvents/
├── app/
│   ├── src/main/java/sabuj/m/myevents/
│   │   ├── core/           # Shared UI tokens, components (AppButton, AppSheet, AppSnackbar, AppTopBar), utilities
│   │   ├── data/           # Room Database, DAOs, Entities, Repositories, Firestore Sync & Workers
│   │   ├── di/             # Hilt Dependency Injection modules
│   │   ├── domain/         # Pure Kotlin models, Repository interfaces, and UseCases
│   │   ├── feature/        # Feature screens (home, events, bookings, expenses, guests, itemlists, settings, lock)
│   │   └── navigation/     # AppNavHost & AppDrawer navigation
│   └── build.gradle.kts
├── architecture.md          # Technical architecture & project structure spec
├── design.md                # UI specs, tokens, typography, & tabular inventories
├── version.md               # Version history log
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Ladybug / Jellyfish (or newer)
- **JDK**: JDK 17 or higher
- **Android SDK**: Min SDK 29 (Android 10), Target SDK 37

### Building from Source

1. Clone the repository:
   ```bash
   git clone https://github.com/sabujdip01/MyEvents.git
   cd MyEvents
   ```

2. Build the Debug APK:
   ```bash
   ./gradlew assembleDebug
   ```

3. Build the Signed Release APK:
   ```bash
   ./gradlew assembleRelease
   ```

4. Install directly onto a connected device via ADB:
   ```bash
   adb install -r app/build/outputs/apk/release/MyEvents.apk
   ```

---

## 📜 Version History

Current Version: **`1.0.0`** (Initial Stable Release).  
For detailed version history and changelogs, see [`version.md`](version.md).

---

## Download Link

Download the 1st release app from [here](https://github.com/sabujdip01/MyEvents/releases/download/R1/MyEvents.V1.0.0.apk).

## 👨‍💻 Author

Made with ❤️ by **Sabuj Mondal**
