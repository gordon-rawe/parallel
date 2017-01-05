# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:/android-sdk-windows/tools/proguard/proguard-android.txt
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


################################################
-dontshrink #for library must be
################################################
-keep public class javax.net.ssl.**
-keepclassmembers public class javax.net.ssl.** {*;}

#-keep public android.support.v4.**
#-keepclassmembers android.support.v4.** {*;}
#-keep public android.support.v7.**
#-keepclassmembers android.support.v7.** {*;}

-keep public class org.apache.http.**
-keepclassmembers public class org.apache.http.** {*;}

-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

################################################
# standard android proguard config, copied from android sdk

# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
-dontoptimize
-dontpreverify
# Note that if you want to enable optimization, you cannot just
# include optimization flags in your own project configuration file;
# instead you will need to point to the
# "proguard-android-optimize.txt" file instead of this one from your
# project.properties file.

-keepattributes *Annotation*
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**


# javascript callback
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-keepattributes JavascriptInterface
-keep public class com.mlibrary.**{*;}
-dontwarn com.mlibrary.**

# Square
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**
-keep class retrofit.** { *; }
-keep @interface retrofit.** { *; }
-dontwarn retrofit.**
-dontwarn rx.*
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

-keep class okio.** { *; }
-dontwarn okio.**

-keep class com.facebook.** { *; }
-dontwarn com.facebook.**

-keep class com.afollestad.** { *; }
-dontwarn com.afollestad.materialdialogs.**

-keep class me.zhanghai.** { *; }
-dontwarn me.zhanghai.**

-keep class android.** { *; }

-keep class com.mctrip.modules.hospital.HospitalFragment { *; }
-keep class com.mctrip.modules.mine.MineFragment { *; }
-keep class com.mctrip.modules.setting.SettingFragment { *; }
-keep class com.mctrip.modules.device.DeviceFragment { *; }
-keep class com.mctrip.modules.device.android.AndroidFragment { *; }
-keep class com.mctrip.modules.device.ios.IosFragment { *; }
-dontwarn com.mctrip.modules.hospital.**
-dontwarn com.mctrip.modules.mine.**
-dontwarn com.mctrip.modules.setting.**
-dontwarn com.mctrip.modules.device.**
-dontwarn com.mctrip.modules.device.android.**
-dontwarn com.mctrip.modules.device.ios.**

-dontwarn com.yalantis.ucrop.**
-keep class com.yalantis.ucrop.view.widget.HorizontalProgressWheelView
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }

-ignorewarnings

