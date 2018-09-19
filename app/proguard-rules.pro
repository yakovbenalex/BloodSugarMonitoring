# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\Programms\Android\sdk/tools/proguard/proguard-android.txt
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


#-keep class com.futurice.project.MyClass { *; }
#-keepnames class com.example.jason.bloodGlucoseMonitoring.* { *; }

#
###---------------Begin: proguard configuration common for all Android apps ----------
-optimizationpasses 5
#-dontusemixedcaseclassnames
#-dontskipnonpubliclibraryclasses
#-dontskipnonpubliclibraryclassmembers
#-dontpreverify
#-verbose
#-dump class_files.txt
#-printseeds seeds.txt
#-printusage unused.txt
#-printmapping mapping.txt
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#
#-allowaccessmodification
#-keepattributes *Annotation*
#-renamesourcefileattribute SourceFile
#-keepattributes SourceFile,LineNumberTable
#-repackageclasses ''
#
#-dontwarn android.support.**
#-dontwarn org.apache.harmony.**
#-dontwarn com.tonicsystems.**
#-dontwarn org.firebirdsql.**
#-dontwarn org.antlr.**
#-dontwarn org.apache.**
#
#-keep public class * extends android.app.Activity
#-keep public class * extends android.app.Application
#-keep public class * extends android.app.Service
#-keep public class * extends android.content.BroadcastReceiver
#-keep public class * extends android.content.ContentProvider
#-keep public class * extends android.app.backup.BackupAgentHelper
#-keep public class * extends android.preference.Preference
#-keep public class com.android.vending.licensing.ILicensingService
#-dontnote com.android.vending.licensing.ILicensingService
#
## Explicitly preserve all serialization members. The Serializable interface
## is only a marker interface, so it wouldn't save them.
#-keepclassmembers class * implements java.io.Serializable {
#static final long serialVersionUID;
#private static final java.io.ObjectStreamField[] serialPersistentFields;
#private void writeObject(java.io.ObjectOutputStream);
#private void readObject(java.io.ObjectInputStream);
#java.lang.Object writeReplace();
#java.lang.Object readResolve();
#}
#
## Preserve all native method names and the names of their classes.
#-keepclasseswithmembernames class * {
#native <methods>;
#}
#
#-keepclasseswithmembernames class * {
#public <init>(android.content.Context, android.util.AttributeSet);
#}
#
#-keepclasseswithmembernames class * {
#public <init>(android.content.Context, android.util.AttributeSet, int);
#}
#
## Preserve static fields of inner classes of R classes that might be accessed
## through introspection.
#-keepclassmembers class **.R$* {
#public static <fields>;
#}
#
## Preserve the special static methods that are required in all enumeration classes.
#-keepclassmembers enum * {
#public static **[] values();
#public static ** valueOf(java.lang.String);
#}
#
#-keep public class * {
#public protected *;
#}
#
#-keep class * implements android.os.Parcelable {
#public static final android.os.Parcelable$Creator *;
#}##---------------End: proguard configuration common for all Android apps ----------
