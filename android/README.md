# Android - Kotlin/Compose Application

## Setup

1. Open Android Studio
2. File → New → New Project
3. Select: Empty Activity (Compose)
4. Settings:
   - Name: ExpenseTracker
   - Package: com.expensetracker.android
   - Save location: This folder
   - Language: Kotlin
   - Minimum SDK: API 26 (Android 8.0)
   - Build configuration: Kotlin DSL

5. Wait for Gradle sync

6. Add dependencies in `app/build.gradle.kts`:
```kotlin
dependencies {
    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    
    // Hilt
    implementation("com.google.dagger:hilt-android:2.50")
    ksp("com.google.dagger:hilt-compiler:2.50")
    
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    
    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    
    // SQLCipher
    implementation("net.zetetic:android-database-sqlcipher:4.5.4")
}
```

7. Run on emulator or device
