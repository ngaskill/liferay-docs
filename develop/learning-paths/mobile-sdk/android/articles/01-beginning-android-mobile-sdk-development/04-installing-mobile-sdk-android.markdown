# Installing the Liferay Mobile SDK for Android

The Liferay Mobile SDK you built must be installed in your Android app for it to 
interact with the Guestbook portlet. To install it, you'll copy the Mobile SDK's 
`jar` files into your Android Studio project and then tweak a couple build 
parameters. When you finish the steps in this article, you'll be ready to use 
the Mobile SDK to call the remote services of the Guestbook portlet. 

## Copying the Mobile SDK to Your Project

The Mobile SDK Builder generated two separate `jar` files in your 
`liferay-mobile-sdk` directory: 

1. `modules/guestbook-portlet/build/libs/liferay-guestbook-portlet-android-sdk-1.0.jar`

The first contains the classes and methods for calling the Guestbook portlet's 
remote services. The second contains the rest of the Mobile SDK. Navigate to 
your project's location on disk (`AndroidStudioProjects/LiferayGuestbook` by 
default) and copy the above `jar` files into your app's `app/libs` directory. 

Nick: you can install the mobile sdk as a gradle depedency:

https://github.com/brunofarache/liferay-mobile-sdk/tree/master/android#gradle

It will automatically download dependencies for you. You will still need to copy the guestbook jar to apps/libs though.

Next, you need to update your Android Studio project to pick up these additions. 

## Synchronizing Your Project

For your app to access the `jar` files you just installed in it, you must 
synchronize its Gradle files. Gradle is the build system used by Android Studio. 
In Android Studio, click the *Sync Project with Gradle Files* button in the 
toolbar. This button is outlined in red in the following screenshot: 

![Figure 1: Click this button for your app to recognize the installed Liferay Mobile SDK.](../../images/android-gradle-sync.png)

If you get errors such as `Duplicate files copied in APK META-INF/NOTICE` during 
this process, you need to edit your app module's `build.gradle` file. Note that 
your project has two `build.gradle` files: one for the project, and another for 
the app module. You can find them under *Gradle Scripts* in Android Studio's 
project view. The app module's `build.gradle` file is highlighted in this 
screenshot:

![Figure 2: The app module's `build.gradle` file.](../../images/android-build-gradle-app-module.png)

Open this file and put the following code inside the `android` element, at the 
first level:

    packagingOptions {
      exclude 'META-INF/LICENSE'
      exclude 'META-INF/NOTICE'
    }
    
For example, the entire `android` element may then look something like this:

    android {
      compileSdkVersion 21
      buildToolsVersion "21.1.2"

      defaultConfig {
        applicationId "com.liferay.docs.liferayguestbook"
        minSdkVersion 15
        targetSdkVersion 21
        versionCode 1
        versionName "1.0"
      }
      buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
      }
      packagingOptions {
        exclude 'META-INF/LICENSE'
        exclude 'META-INF/NOTICE'
      }
    }
    
    repositories {
        jcenter()
        mavenCentral()
    }
    
    dependencies {
        compile group: 'com.liferay.mobile', name: 'liferay-android-sdk', version: '6.2.0.+'
    }
    
After you add the `packagingOptions` element, click the 
*Sync Project with Gradle Files* button again. Your project should now 
synchronize without any errors. Next, you'll check to make sure that the Mobile 
SDK installed correctly. 

## Verifying the Mobile SDK Installation

To check your Mobile SDK installation, first open your project's `MainActivity` 
class in Android Studio. It's located in the `java` folder's 
`com.liferay.docs.liferayguestbook` package, in the project view. Add the 
following imports at the top of the file:

    import com.liferay.mobile.android.service.Session;
    import com.liferay.mobile.android.v62.entry.EntryService;
    import com.liferay.mobile.android.v62.guestbook.GuestbookService;

If Android Studio recognizes these imports, then you installed the Mobile SDK 
correctly. Congratulations! You now have a functional Liferay Mobile SDK in the 
Guestbook app! Now you can put it to use to retrieve the guestbooks and their 
entries from the Guestbook portlet. The next series of articles in this learning 
path walks you through this. 
