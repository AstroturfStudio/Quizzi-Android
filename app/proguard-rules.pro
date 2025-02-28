# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# Crash reporting için
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

# Kotlin serialization için
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# Coroutines için
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Reflection kullanan sınıflar için
-keepclassmembers class * {
    @com.squareup.moshi.* <methods>;
    @kotlinx.serialization.* <methods>;
}
# Keep all classes with @Serializable or @SerialName annotations
-keepattributes *Annotation*
-keep,allowobfuscation class kotlinx.serialization.json.** { *; }

# Keep all classes with @Serializable
-keep,includedescriptorclasses @kotlinx.serialization.Serializable class * {
    *;
}

# Keep all classes with @SerialName
-keep,includedescriptorclasses @kotlinx.serialization.SerialName class * {
    *;
}

# Keep serializer classes
-keepclassmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
    static kotlinx.serialization.KSerializer serializer(...);
}

# Keep properties annotated with @SerialName
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}