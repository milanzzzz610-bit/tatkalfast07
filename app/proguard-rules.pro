# Add project specific ProGuard rules here.
-dontwarn androidx.security.crypto.**
-keepattributes *Annotation*
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
