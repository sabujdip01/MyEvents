# Add project specific ProGuard rules here.

# Keep Room generated code
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Keep Hilt generated classes
-keep class * extends dagger.hilt.internal.UnsafeCasts

# Keep Firebase models
-keepclassmembers class * {
    @com.google.firebase.firestore.IgnoreExtraProperties <fields>;
    @com.google.firebase.firestore.PropertyName <fields>;
}

# Keep BuildConfig
-keep class sabuj.m.myevents.BuildConfig { *; }
