# Using Offline Mode in Android [](id=using-offline-mode-in-android)

Offline mode in Liferay Screens lets your apps function when connectivity is 
unavailable or intermittent. Even though the steady march of technology makes 
connections more stable and prevalent, there are still plenty of places the 
Internet has trouble reaching. Areas with complex terrain, including cities with 
large buildings, often lack stable connections. Remote areas typically don't 
have connections at all. Using Screens's offline mode in your apps gives your 
users flexibility in these situations. 

+$$$

**Note:** Not every Screenlet supports offline mode. For offline mode to work, 
the Screenlet's developer must add support for it. Most of the Screenlets 
included with Liferay Screens support offline mode. 
[The Screenlet reference documentation](/develop/reference/-/knowledge_base/7-0/screenlets-in-liferay-screens-for-android) 
for each of these built-in Screenlets includes details about the Screenlet's 
offline mode support. 

$$$

This tutorial shows you how to use offline mode in Screenlets. There are two 
steps to follow to use offline mode in a Screenlet that supports it: 

1. Configure the Screenlet for offline mode. To do this, you'll set the 
   `offlinePolicy` attribute in the Screenlet's XML. 

2. Set your app to handle data synchronization between the local cache and the 
   Liferay instance. You'll do this in your app's `AndroidManifest.xml` file. 

Note that this tutorial only shows you how to use offline mode. For a more 
detailed explanation of how offline mode works, see 
[offline mode's architecture tutorial](/develop/tutorials/-/knowledge_base/7-0/architecture-of-offline-mode-in-liferay-screens). 

## Configuring Screenlets for Offline Mode [](id=configuring-screenlets-for-offline-mode)

To use offline mode in a Screenlet that supports it, you must configure the 
`offlinePolicy` attribute when inserting the Screenlet's XML in a layout in your 
app. This attribute can take four possible values: 

- `REMOTE_ONLY`
- `CACHE_ONLY`
- `REMOTE_FIRST`
- `CACHE_FIRST`

For a description of these values, see the section 
[Using Policies with Offline Mode](/develop/tutorials/-/knowledge_base/7-0/architecture-of-offline-mode-in-liferay-screens#using-policies-with-offline-mode) 
in the offline mode architecture tutorial. Note that each Screenlet behaves a 
bit differently with offline mode. For specific details on how offline mode 
functions in the Screenlets that come with Liferay Screens, see the 
[Screenlet reference documentation](/develop/reference/-/knowledge_base/7-0/screenlets-in-liferay-screens-for-android). 

Next, you'll set your app to handle data synchronization between the local cache 
and the Liferay instance. 

## Handling Synchronization [](id=handling-synchronization)

For offline mode to work, data stored in the app's local cache must be 
synchronized with the Liferay instance. For your app to support this, it must 
use the 
[`CacheSyncService` class](https://github.com/liferay/liferay-screens/blob/master/android/library/src/main/java/com/liferay/mobile/screens/cache/CacheSyncService.java). 
This class sends information from the local cache to the Liferay instance. To 
use `CacheSyncService` with your app, add the following code to your app's 
`AndroidManifest.xml` file: 

    <receiver android:name=".CacheReceiver">
        <intent-filter>
            <action android:name="com.liferay.mobile.screens.auth.login.success"/>
            <action android:name="com.liferay.mobile.screens.cache.resync"/>
            <action android:name="android.net.conn.CONNECTIVITY_CHANGE"/>
        </intent-filter>
    </receiver>

    <service
        android:name=".CacheSyncService"
        android:exported="false"/>

This code registers the `CacheReceiver` and `CacheSyncService` components. The 
`CacheReceiver` is invoked in the following scenarios:

- When a connectivity change occurs (for example, when the network connection is 
restored).
- When Login Screenlet successfully completes the login.
- When a specific `resync` intent is broadcasted. In this case, use 
`context.sendBroadcast(new Intent("com.liferay.mobile.screens.cache.resync"));`.

The `CacheSyncService` performs the synchronization process when invoked from 
the above receiver. This is currently an unassisted process. Future versions 
will include some kind of control mechanism. 

## Related Topics [](id=related-topics)

[Architecture of Offline Mode in Liferay Screens](/develop/tutorials/-/knowledge_base/7-0/architecture-of-offline-mode-in-liferay-screens)

[Using Screenlets in Android Apps](/develop/tutorials/-/knowledge_base/7-0/using-screenlets-in-android-apps)

[Using Offline Mode in iOS](/develop/tutorials/-/knowledge_base/7-0/using-offline-mode-in-ios)

[Using Screenlets in iOS Apps](/develop/tutorials/-/knowledge_base/7-0/using-screenlets-in-ios-apps)
