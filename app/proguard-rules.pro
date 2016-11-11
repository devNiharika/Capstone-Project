# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\SDK\AndroidSDK/tools/proguard/proguard-android.txt
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

-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

#-keep class com.squareup.okhttp3.** { *; }
#-keep interface com.squareup.okhttp3.** { *; }
#
#-keep class com.nineoldandroids.** { *; }
#-keep interface com.nineoldandroids.** { *; }

-keep class org.jsoup.** {
public *;
}

#-keep class dev.rg.** { *; }
#-keep interface dev.rg.** { *; }

#-keep class com.codetroopers.betterpickers.** { *; }
#-keep interface com.codetroopers.betterpickers.** { *; }

-keep class * extends android.webkit.WebChromeClient { *; }
-dontwarn im.delight.android.webview.**

-dontwarn okio.**

-dontwarn org.joda.convert.FromString
-dontwarn org.joda.convert.ToString