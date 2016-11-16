# Liferay Screens for Android Troubleshooting and FAQs [](id=liferay-screens-for-android-troubleshooting-and-faqs)

Even though Liferay developed Screens for Android with great care, you may still
run into some common issues. Here are solutions and tips for solving these
issues. You'll also find answers to common questions about Screens for Android. 

## Best Practices [](id=best-practices)

Before investigating specific issues, you should first make sure that you have 
the latest tools installed and know where to get additional help if you need it. 
You should use the latest Android API level, with the latest version of Android 
Studio. Although Screens *can* work with Eclipse ADT or manual Gradle builds, 
Android Studio is the preferred IDE. 

If you're having trouble with Liferay Screens, it may help to investigate the 
sample apps developed by Liferay. Both serve as good examples of how to use 
Screenlets and Views: 

- [Westeros Bank](https://github.com/liferay/liferay-screens/tree/master/android/samples/bankofwesteros) 
- [Test App](https://github.com/liferay/liferay-screens/tree/master/android/samples/test-app) 

If you get stuck, you can post your question on our 
[forum](https://www.liferay.com/community/forums/-/message_boards/category/42706063).
We're happy to assist you! If you found a bug or want to suggest an improvement, 
file a ticket in our 
[Jira](https://issues.liferay.com/browse/LMW/). 
Note that you must 
[log in](https://issues.liferay.com/login.jsp?os_destination=%2Fbrowse%2F) 
first to be able to see the project.

### Naming Conventions

Using the naming conventions described here leads to consistency and a better 
understanding of the Screens library. This makes working with your Screenlets 
much simpler. 

Also note that Liferay Screens follows 
[Square's Java conventions](https://github.com/square/java-code-styles) 
for Android, with tabs as separator. The configuration for IDEA, findbugs, PMD, 
and checkstyle is available in the project's source code. 

#### Screenlet Folder

Your Screenlet folder's name should indicate your Screenlet's functionality. For 
example, 
[Login Screenlet's folder is named `login`](https://github.com/liferay/liferay-screens/tree/master/android/library/src/main/java/com/liferay/mobile/screens/auth/login). 

If you have multiple Screenlets that operate on the same entity, you can place 
them inside a folder named for that entity. For example, 
[Asset Display Screenlet](/develop/reference/-/knowledge_base/7-0/asset-display-screenlet-for-android) 
and 
[Asset List Screenlet](/develop/reference/-/knowledge_base/7-0/assetlistscreenlet-for-android) 
both work with Liferay assets. They're therefore in the Screens library's 
[`asset` folder](https://github.com/liferay/liferay-screens/tree/master/android/library/src/main/java/com/liferay/mobile/screens/asset). 

#### Screenlets

Naming Screenlets properly is very important; they're the main focus of Liferay 
Screens. Your Screenlet should be named with its principal action first, 
followed by *Screenlet*. Its Screenlet class should also follow this pattern. 
For example, 
[Login Screenlet's](/develop/reference/-/knowledge_base/7-0/loginscreenlet-for-android) 
principal action is to log users into a Liferay instance. Its Screenlet class is 
`LoginScreenlet`. 

#### View Models

Name your View Models after your Screenlet, substituting `ViewModel` for 
`Screenlet`. Also, place your View Models in a `view` folder in your Screenlet's 
root folder. For example, Login Screenlet's View Model is in the 
[`login/view` folder](https://github.com/liferay/liferay-screens/tree/master/android/library/src/main/java/com/liferay/mobile/screens/auth/login/view) 
and is named `LoginViewModel`. 

#### Interactors

Place your Screenlet's Interactors in a folder named `interactor` in your 
Screenlet's root folder. Name each Interactor for the object it operates on, its 
action, and *Interactor*. If you wish, you can also put each interactor in its 
own folder named after its action. For example, 
[Rating Screenlet](/develop/reference/-/knowledge_base/7-0/rating-screenlet-for-android) 
has three Interactors. Each is in its own folder inside 
[the `interactor` folder](https://github.com/liferay/liferay-screens/tree/master/android/library/src/main/java/com/liferay/mobile/screens/rating/interactor): 

- `delete/RatingDeleteInteractor`: Deletes an asset's ratings
- `load/RatingLoadInteractor`: Loads an asset's ratings
- `update/RatingUpdateInteractor`: Updates an asset's ratings

#### Views

Place Views in a `view` folder in the Screenlet's root folder. If you're 
creating a Viewset, however, then you can place its Views in a separate 
`viewsets` folder outside of your Screenlets' root folders. This is what the 
Screens Library does for 
[its Material and Westeros Viewsets](https://github.com/liferay/liferay-screens/tree/master/android/viewsets). 
The `material` and `westeros` folders contain those Viewsets, respectively. Also 
note that each Screenlet's View class is in its own folder. For example, the 
View class for 
[Forgot Password Screenlet's](/develop/reference/-/knowledge_base/7-0/forgotpasswordscreenlet-for-android) 
Material View is in 
[the folder `viewsets/material/src/main/java/com/liferay/mobile/screens/viewsets/material/auth/forgotpassword`](https://github.com/liferay/liferay-screens/tree/master/android/viewsets/material/src/main/java/com/liferay/mobile/screens/viewsets/material/auth/forgotpassword). 
Note that the `auth` folder is the Screenlet's module. Creating your Screenlets 
and Views in modules isn't required. Also note that the 
[View's layout file `forgotpassword_material.xml`](https://github.com/liferay/liferay-screens/blob/master/android/viewsets/material/src/main/res/layout/forgotpassword_material.xml) 
is in `viewsets/material/src/main/res/layout`. 

Name a View's layout XML and View class after your Screenlet, substituting 
*View* for *Screenlet* where necessary. The layout's filename should also be 
suffixed with `_yourViewName`. For example, the XIB file and View class for 
Forgot Password Screenlet's Material View are `forgotpassword_material.xml` and 
`ForgotPasswordView.java`, respectively. 

### Avoid Hard Coded Elements

Using constants instead of hard coded elements is a simple way to avoid bugs. 
Constants reduce the likelihood that you'll make a typo when referring to common 
elements. They also gather these elements in a single location. 
<!-- Examples? -->

### Avoid State in Interactors

Liferay Screens uses 
[EventBus](http://greenrobot.org/eventbus/) 
to ensure that the network or background operation isn't lost when the device 
changes orientation. For this to work, however, you must ensure that your 
Interactor's request is stateless. Interactors are also meant to be run as a 
background operation and could be run in parallel. 
<!-- Don't Interactors automatically run in the background, in parallel? -->

If an Interactor needs some piece of information, you should pass it to the 
Interactor via your `start` call and attach it to the current event. 
<!-- Example? -->

### Stay in Your Layer

When accessing variables that belong to other Screenlet components, you should 
avoid those outside your current Screenlet layer. This achieves better 
decoupling between the layers, which tends to reduce bugs and simplify 
maintenance. For an explanation of the layers in Liferay Screens, see 
[the architecture tutorial](/develop/tutorials/-/knowledge_base/7-0/architecture-of-liferay-screens-for-android). 
For example, don't directly access View variables from an Interactor. Instead, 
pass data from the View Model to the Interactor by calling the Interactor's 
`start` method in the Screenlet class. You can see an example 
of this in the sample Add Bookmark Screenlet from 
[the Screenlet creation tutorial](/develop/tutorials/-/knowledge_base/7-0/creating-android-screenlets#creating-the-screenlet-class). 
The `onUserAction` method in `AddBookmarkScreenlet` passes a Bookmark's URL and 
title from the View Model to the Interactor via the Interactor's `start` method: 

    @Override
    protected void onUserAction(String userActionName, AddBookmarkInteractor interactor, 
        Object... args) {
            AddBookmarkViewModel viewModel = getViewModel();
            String url = viewModel.getURL();
            String title = viewModel.getTitle();

            interactor.start(url, title, folderId);
    }

<!-- 
Should we also list any exceptions to this rule, like at the end of this section 
in the iOS best practices tutorial?

https://dev.liferay.com/develop/tutorials/-/knowledge_base/7-0/ios-best-practices#stay-in-your-layer
-->

## Troubleshooting [](id=troubleshooting)

This section contains information on common issues that can occur when using 
Liferay Screens. 

1.  Could not find `com.liferay.mobile:liferay-screens`

    This error occurs when Gradle isn't able to find Liferay Screens or the 
    repository. First, check that the Screens version number you're trying to 
    use exists in jCenter. You can use this
    [link](https://bintray.com/liferay/liferay-mobile/liferay-screens/view) 
    to see all uploaded versions.

    It's also possible that you're using an old Gradle plugin that doesn't use 
    jCenter as the default repository. Screens uses version 1.2.3 and later. You 
    can add jCenter as a new repository by placing this code in your project's 
    `build.gradle` file: 

        repositories {
            jcenter()
        }

2.  Failed to resolve: `com.android.support:appcompat-v7`

    Liferay Screens uses the appcompat library from Google, as do all new 
    Android projects created with the latest Android Studio. The appcompat 
    library uses a custom repository maintained by Google, so it must be updated 
    manually using the Android SDK Manager. 

    In the Android SDK Manager (located at *Tools* &rarr; *Android* &rarr; *SDK
    Manager* in Android Studio), you must install at least version 14 of the 
    Android Support Repository (in the *Extras* menu), and version 22.1.1 of the
    Android Support Library.

3.  Duplicate files copied in `APK META-INF`...

    This is a common Android error when using libraries. It occurs because 
    Gradle can't merge duplicated files such as license or notice files. To 
    prevent this error, add the following code to your `build.gradle` file:

        android {
            ...
            packagingOptions {
                exclude 'META-INF/LICENSE'
                exclude 'META-INF/NOTICE'
            }
            ...
        }

    This error may not happen right away, but may only appear later on in the
    development process. For this reason, it's recommended that you put the
    above code in your `build.gradle` file after creating your project. 

4.  Connect failed: ECONNREFUSED (Connection refused), or 
   `org.apache.http.conn.HttpHostConnectException`

    This error occurs when Liferay Screens and the underlying Liferay Mobile 
    SDK can't connect to the Liferay Portal instance. If you get this error, 
    you should first check the IP address of the server to make sure it's 
    available. If you've 
    [overridden the default IP address in `server_context.xml`](/develop/tutorials/-/knowledge_base/7-0/preparing-android-projects-for-liferay-screens#configuring-communication-with-liferay), 
    you should check to make sure that you've set it to the correct IP. Also, if 
    you're using the Genymotion emulator, you must use `192.168.56.1` instead of 
    localhost for your app to communicate with a local Liferay instance. 

5.  `java.io.IOException`: open failed: EACCES (Permission denied)

    Some Screenlets use temporary files to store information, such as when the
    User Portrait Screenlet uploads a new portrait, or the DDL Form Screenlet
    uploads new files to the portal. Your app needs to have the necessary
    permissions to use a specific screenlet's functionality. In this case, add
    the following line to your `AndroidManifest.xml`: 

        <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>

    If you're using the device's camera, you also need to add the following 
    permission:

        <uses-permission android:name="android.permission.CAMERA"/>

6.  No JSON web service action with path ...

    This error most commonly occurs if you haven't installed the 
    [Liferay Screens Compatibility Plugin](https://github.com/liferay/liferay-screens/tree/master/portal). 
    This plugin adds new API calls to Liferay Portal.

## FAQs [](id=faqs)

1.  Do I have to use Android Studio?

    No, Liferay Screens also works with Eclipse ADT. You can also compile your 
    project manually with Gradle or another build system. Just make sure you use 
    the compiled `aar` in your project's `lib` folder.

    However, it's *strongly* recommended that you use Android Studio. Android 
    Studio is the official IDE for developing Android apps. Using Eclipse ADT or 
    compiling manually may cause unexpected issues that are difficult to 
    overcome. 

2.  How does Screens handle orientation changes?

    Liferay Screens uses an event bus, 
    [the `EventBus` library](http://greenrobot.github.io/EventBus/), 
    to notify activities when the interactor has finished its work. 

3.  How can I use a Liferay feature not available in Screens?

    There are several ways you can use Liferay features not currently available 
    in Screens. The 
    [Liferay Mobile SDK](https://github.com/liferay/liferay-mobile-sdk) 
    gives you access to all of Liferay's remote APIs. You can also create a 
    custom Screenlet to support any features not included in Screens by default. 

4.  How do I create a new Screenlet?

    Screenlet creation is explained in detail 
    [here](/develop/tutorials/-/knowledge_base/7-0/creating-android-screenlets).

5.  How can I customize a screenlet?

    You can customize Screenlets by creating new Views. Fortunately, there's a 
    [tutorial](/develop/tutorials/-/knowledge_base/7-0/creating-android-views) 
    for this! 

6.  Does Screens have offline support?

    Yes, since Liferay Screens 1.3!

## Related Topics [](id=related-topics)

[Preparing Android Projects for Liferay Screens](/develop/tutorials/-/knowledge_base/7-0/preparing-android-projects-for-liferay-screens)

[Creating Android Screenlets](/develop/tutorials/-/knowledge_base/7-0/creating-android-screenlets)

[Creating Android Views](/develop/tutorials/-/knowledge_base/7-0/creating-android-views)

[Mobile SDK](/develop/tutorials/-/knowledge_base/7-0/mobile-sdk)
