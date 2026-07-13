# Freetime SDK ProGuard Rules

# Keep public API classes
-keep class com.freetime.sdk.FreetimePay { *; }
-keep class com.freetime.sdk.DeveloperConfig { *; }
-keep class com.freetime.sdk.PaymentRequest { *; }
-keep class com.freetime.sdk.PaymentResult { *; }
-keep interface com.freetime.sdk.PaymentProvider { *; }

# Keep all providers
-keep class com.freetime.sdk.providers.** { *; }

# Keep Activity and its resources
-keep class com.freetime.sdk.PaymentSelectionActivity { *; }
-keepclassmembers class com.freetime.sdk.R$* {
    public static <fields>;
}

# Keep Coroutines and other dependencies
-keep class kotlinx.coroutines.** { *; }
-keep class kotlinx.datetime.** { *; }

# General Android support
-keep class androidx.browser.customtabs.** { *; }
-keep class androidx.appcompat.** { *; }
