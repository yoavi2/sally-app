# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Gson
##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

##---------------End: proguard configuration for Gson  ----------

# AndroidYoutubePlayer
-keep public class com.pierfrancescosoffritti.youtubeplayer.** {
   public *;
}
-keepnames class com.pierfrancescosoffritti.youtubeplayer.*

# Picasso
-dontwarn com.squareup.okhttp.**

# Dbflow
-keep class * extends com.raizlabs.android.dbflow.config.DatabaseHolder { *; }

# OkHttp
-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.ParametersAreNonnullByDefault

# MpAndroidCharts
-keep class com.github.mikephil.charting.** { *; }

# Firebase-auth
# Recommended flags for Firebase Auth
-keepattributes Signature
-keepattributes *Annotation*

# Retrofit
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

# Okio
-dontwarn okio.**
-keep class okio.** { *; }
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-keep class com.google.** { *; }

-dontwarn javax.annotation.**
# Don't warn about retrofit or okio classes
-dontwarn retrofit2.Call
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

# Dagger
-dontwarn com.google.errorprone.annotations.*

#Stetho
-keep class com.facebook.stetho.** { *; }
-dontwarn com.facebook.stetho.**

# Debug TODO: Remove this
-printconfiguration "build/outputs/mapping/configuration.txt"