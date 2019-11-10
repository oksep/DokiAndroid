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
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-verbose #打出混淆log

-dontoptimize #防止复杂的方法在混淆是 抛出错误
-optimizationpasses 5 #混淆压缩比

-dontskipnonpubliclibraryclasses #混淆第三方库

#保留Annotation注解
-keepattributes *Annotation*

#减少jar的大小一般都压缩掉,为了增加反编译的难度也可以不压缩,,(这个会导致dexguard失败)
-dontshrink

#把混淆类中的方法名也混淆了，keep类中一些不需要keep的类的方法名也混淆了,需要
-useuniqueclassmembernames

#代码混淆采用的算法，一般不改变，用谷歌推荐算即可
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#------------------android 基本
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference

-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}
 # 保持自定义控件类不被混淆
-keepclasseswithmembers class * {                                               # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);     # 保持自定义控件类不被混淆
}
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-dontwarn android.support.**

-keep,allowshrinking class android.support.** { *; }

-keep class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#-------------------end android基本

#-------------------关闭错误警告
-dontwarn javax.**
-dontwarn java.awt.**
-dontwarn com.sun.jdmk.comm.**
-dontwarn javax.annotation.**
-dontwarn android.app.**
-dontwarn android.view.**
-dontwarn android.widget.**
-dontwarn com.google.common.primitives.**
-dontwarn **CompatHoneycomb
-dontwarn **CompatHoneycombMR2
-dontwarn **CompatCreatorHoneycombMR2
-dontwarn org.apache.**
-dontnote android.support.**
-dontwarn android.support.**.R
-dontwarn android.support.**.R$*
#-------------------end 关闭错误警告

-keep class org.apache.** { *;}

# For RxJava:
#-dontwarn org.mockito.**
#-dontwarn org.junit.**
#-dontwarn org.robolectric.**
#
#-dontwarn rx.**
#-keep class rx.schedulers.Schedulers {
#    public static <methods>;
#}
#-keep class rx.schedulers.ImmediateScheduler {
#    public <methods>;
#}
#-keep class rx.schedulers.TestScheduler {
#    public <methods>;
#}
#-keep class rx.schedulers.Schedulers {
#    public static ** test();
#}

# ndk
#-keep class com.dokiwa.dokidoki.NativeBridge {
#    public static java.lang.String nativeFun();
#}

# For Gson
-keep,allowobfuscation class com.google.gson.annotations.*
-dontnote com.google.gson.annotations.Expose
-keepclassmembers class * {
    @com.google.gson.annotations.Expose <fields>;
}

-dontnote com.google.gson.annotations.SerializedName
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# For retrofit2 okhttp3
-dontwarn okhttp3.**
-keep class okhttp3.** { *;}
-keep interface okhttp3.** { *; }
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# For retrofit1 和 okhttp 有些lib还是低版本
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**
-dontwarn retrofit.**
-keep class retrofit.** { *; }

-dontwarn okio.**
-dontwarn rx.**
-keep class rx.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

#for share sdk
-dontwarn cn.sharesdk.**
-keep class cn.sharesdk.** { *;}
-keep class com.sina.sso.** { *;}
-keep class m.framework.**{*;}

-keep class com.tencent.mm.opensdk.** {*;}
-keep class com.tencent.wxop.** {*;}
-keep class com.tencent.mm.sdk.** {*;}

-keep class com.dokiwa.dokidoki.wxapi.WXEntryActivity { public *;}

# for umeng
-keepclassmembers class * {
  public <init>(org.json.JSONObject);
}
-keep public class com.umeng.fb.ui.ThreadView {
}
-keep class com.umeng.** { *;}

# Avoid merging and inlining compatibility classes.
-keep,allowshrinking,allowobfuscation class android.support.**Compat* { *; }

# leakcanary
-keep class com.squareup.leakcanary.** { *; }

# for qiniu
-dontwarn com.qiniu.**
-keep class com.qiniu.** { *;}
-keep class com.qiniu.**{public <init>();}
-ignorewarnings

# retrofit 自动化映射
-keep class * implements com.dokiwa.dokidoki.center.api.model.IApiModel
-keepclassmembers class * implements com.dokiwa.dokidoki.center.api.model.IApiModel {
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
}
-keep public class com.dokiwa.dokidoki.center.api.model.IApiModel

# DWKeepable 自动化映射
-keep class * implements com.dokiwa.dokidoki.center.util.Keep
-keepclassmembers class * implements com.dokiwa.dokidoki.center.util.Keep {
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
}
-keep public class com.dokiwa.dokidoki.center.util.Keep

# for Web
-dontnote android.webkit.JavascriptInterface
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# api23 移除了 FloatMath, 添加以下确保最新sdk能够顺利编译混淆通过
-dontwarn android.util.FloatMath

-keepattributes Signature

-keepclasseswithmembers public class * {
    public static void main(java.lang.String[]);
}

# plugin
-keep class * implements com.dokiwa.dokidoki.center.plugin.FeaturePlugin

# glide
-dontwarn com.bumptech.glide.**
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder
-keep class com.bumptech.glide.GeneratedAppGlideModuleImpl

# NIM
-dontwarn com.netease.**
-keep class com.netease.** {*;}
#如果你使用全文检索插件，需要加入
-dontwarn org.apache.lucene.**
-keep class org.apache.lucene.** {*;}

-keepclassmembers class ** {
    public void onEvent*(**);
    void onEvent*(**);
}

# UMENG
-keep class com.umeng.** {*;}
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

# immersionbar
-keep class com.gyf.immersionbar.* {*;}
-dontwarn com.gyf.immersionbar.**

-keep class com.sina.weibo.sdk.** { *; }