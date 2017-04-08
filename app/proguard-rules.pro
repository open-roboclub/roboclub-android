# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/iamareebjamal/Android/SDK/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class title to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontwarn com.squareup.okhttp.**
-dontwarn org.codehaus.mojo.**
-dontwarn okio.**

-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }

-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*

-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations

-keepattributes EnclosingMethod

-keep public enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-dontwarn java.lang.invoke.*

# This rule will properly ProGuard all the model classes in
# the package com.yourcompany.models. Modify to fit the structure
# of your app.
-keepclassmembers class amu.roboclub.models.** {
  *;
}

-keep class amu.roboclub.ui.viewholder.** { *; }

-keep class com.cloudinary.** { *; }