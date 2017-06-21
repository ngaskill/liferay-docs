# Installing Liferay Screens and the Guestbook Mobile SDK

For your iOS app to work with the Guestbook portlet, you must install the 
following in your iOS project: 

- **Liferay's pre-built Mobile SDK:** This Mobile SDK contains the classes that 
  call Liferay Portal's core remote services. It also contains the framework
  necessary for any Mobile SDK to make server calls. 

- **The Guestbook Mobile SDK:** This Mobile SDK contains only the classes that 
  call the Guestbook portlet's remote services. 

- **Liferay Screens:** Screens contains the Screenlet framework and 
  [several built-in Screenlets](/develop/reference/-/knowledge_base/6-2/screenlets-in-liferay-screens-for-ios) 
  like Login Screenlet. Because these built-in Screenlets work with Liferay 
  Portal's core apps, they make their server calls with Liferay's pre-built
  Mobile SDK. Note that all Screenlets, including those that make server calls
  with a custom-built Mobile SDK, must use the framework in Liferay's pre-built
  Mobile SDK to issue their calls. 

Since Liferay's pre-built Mobile SDK is a dependency of Liferay Screens, 
installing Screens automatically installs this Mobile SDK. You must, however, 
install the Guestbook Mobile SDK manually. 

This article walks you through the installation of the Guestbook Mobile SDK and 
Liferay Screens. When you finish, you'll be ready to start developing the app. 

## Installing Liferay Screens

You'll use 
[CocoaPods](https://cocoapods.org/) 
to install Liferay Screens. 
[Click here](https://guides.cocoapods.org/using/getting-started.html) 
for instructions on installing CocoaPods. After installing CocoaPods, use these 
steps to install Screens: 

1.  In your project's root folder, create a file named `Podfile` that contains 
    the following: 

        source 'https://github.com/CocoaPods/Specs.git'

        platform :ios, '8.0'
        use_frameworks!

        target "Liferay Guestbook" do
            pod 'LiferayScreens', '2.1.2'
        end

        post_install do |installer|
            installer.pods_project.targets.each do |target|
                target.build_configurations.each do |config|
                    config.build_settings['CONFIGURATION_BUILD_DIR'] = '$PODS_CONFIGURATION_BUILD_DIR'
                end
            end
        end

2.  On the terminal, navigate to your project's root folder and run this 
    command: 

        pod repo update

    This ensures you have the latest version of the CocoaPods repository on your 
    machine. Note that this command can take a while to run. 

3.  Still in your project's root folder in the terminal, run this command: 

        pod install

    Once this completes, quit Xcode and reopen your project by using the 
    `LiferayGuestbook.xcworkspace` file in your project's directory. From now 
    on, you must use this file to open your project. 

Great! You just installed Liferay Screens and the Liferay Mobile SDK! Next, 
you'll install the Guestbook Mobile SDK. 

## Installing the Guestbook Mobile SDK

To install the Guestbook Mobile SDK, you must add its service classes to your 
project. Recall that these service classes are Objective-C. 
[To use them from your project's Swift code](https://developer.apple.com/library/ios/documentation/Swift/Conceptual/BuildingCocoaApps/MixandMatch.html), 
you must also add and configure an Objective-C bridging header. This section 
shows you how to do these things. 

1.  Recall that you created the following ZIP file for the Guestbook Mobile SDK: 

        modules/guestbook-portlet/build/liferay-guestbook-portlet-ios-sdk-1.0.zip

    Unzip this file to a location of your choosing on your machine. This creates 
    the following directory hierarchy: 

    ![Figure 1: The Guestbook Mobile SDK's Objective-C classes unzip to this folder structure.](../../../images/ios-lp-sdk-structure.png)

    This should look familiar. It's the same `Service` folder, contents and all, 
    from the Guestbook Mobile SDK you built in the previous article. 

2.  To install the service classes in your project, drag the `v62` folder from 
    your Finder into your Xcode project, directly under the top-level Liferay 
    Guestbook project. In the dialog that appears, make sure you select the 
    following items, and then click *Finish*: 

    ![Figure 2: When adding the Guestbook Mobile SDK to your project, select these options and then click *Finish*.](../../../images/ios-lp-add-sdk.png)

    ![Figure 3: Your project structure should look like this after adding the Guestbook SDK.](../../../images/ios-lp-post-sdk-install.png)

3.  The `v62` folder and its contents are now inside your Xcode project. Now you 
    must change each Objective-C class header file in the Guestbook Mobile SDK 
    to import the Liferay Mobile SDK framework. This is necessary because you 
    used `use_frameworks!` in your Podfile. In `LREntryService_v62.h` and 
    `LRGuestbookService_v62.h`, replace `#import "LRBaseService.h"` with 
    `@import LRMobileSDK;`. Don't worry if Xcode doesn't recognize this 
    import--you'll fix this by adding and configuring an Objective-C bridging 
    header in your project. 

To use the Guestbook Mobile SDK's Objective-C classes from Swift, you must add 
and configure an Objective-C bridging header in your project. Follow these 
instructions to do so: 

1.  In Xcode's project navigator, right-click the top-level project (Liferay 
    Guestbook) and select *New File*. In the window that appears, select *Header 
    File* from the *Source* section of the *iOS* tab, and click *Next*.

    ![Figure 4: Create a new iOS header file.](../../../images/ios-lp-header-file-01.png)

2.  Name the file `Liferay Guestbook-Bridging-Header.h` and make sure that 
    *Liferay Guestbook* with the blue icon is selected in the *Group* menu. To 
    finish creating the file, uncheck any items in *Targets* and click *Create*.

    ![Figure 5: Use these options to create the header file.](../../../images/ios-lp-header-file-02.png)

3.  Upon creating the header file, Xcode opens it in the editor. In this file, 
    you must import the Guestbook Mobile SDK's header files. Add these imports 
    immediately below the comments at the top of the file: 

        #import "LRGuestbookService_v62.h"
        #import "LREntryService_v62.h"

    Your bridging header file should now look like this: 

        #import "LRGuestbookService_v62.h"
        #import "LREntryService_v62.h"

        #ifndef Liferay_Guestbook_Bridging_Header_h
        #define Liferay_Guestbook_Bridging_Header_h


        #endif

4.  Now you must configure your project to use this file. Select the top-level 
    Liferay Guestbook project on the left and then click *Build Settings*. 
    Search for *Objective-C Bridging Header* in the search box. The only build 
    setting that appears is *Objective-C Bridging Header*, under the *Swift 
    Compiler - General* section. In the two Liferay Guestbook fields for this 
    build setting, enter the bridging header's file name. 

    ![Figure 6: The project is now configured to use your Objective-C bridging header.](../../../images/ios-lp-build-settings-header.png)

5.  Build the project. 

Awesome! You've successfully installed the Guestbook Mobile SDK. Next, you'll 
configure your app to communicate with your Liferay Portal installation. 

## Configuring Communication with Liferay Portal

For Liferay Screens to work with your app, you must configure it to communicate 
with your Liferay Portal installation. You'll do this by setting attributes in a 
`plist` file. Follow these steps to create this file and set the correct 
attributes: 

1.  In Xcode's project navigator, right-click the *Liferay Guestbook* folder 
    (not the top-level project) and select *New File*. In the dialog that 
    appears, select the *iOS* tab, then scroll down to the *Resource* section 
    and select *Property List*. Click *Next*.

    ![Figure 7: Use the Property List template to create a new `plist` file.](../../../images/ios-lp-plist-01.png)

2.  Name the file `liferay-server-context.plist`, and make sure you're creating 
    it in the *Liferay Guestbook* folder, which should also be selected in the 
    *Group* menu. Also make sure that *Liferay Guestbook* is selected in the 
    *Targets* menu. Then click *Create*. 

    ![Figure 8: Create the `plist` file as shown here.](../../../images/ios-lp-plist-02.png)

3.  The `plist` file now opens in the editor. Right-click the file in the 
    project navigator and select *Open As* &rarr; *Source Code*. Replace the 
    file's contents with this code: 

        <?xml version="1.0" encoding="UTF-8"?>
        <!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
        <plist version="1.0">
            <dict>
                <key>server</key>
                    <string>http://localhost:8080</string>
                <key>version</key>
                    <integer>62</integer>
                <key>companyId</key>
                    <real>20155</real>
                <key>groupId</key>
                    <real>20182</real>
            </dict>
        </plist>

    This `plist` file sets the server address (`http://localhost:8080`), Liferay 
    Portal version (`62` specifies Liferay Portal 6.2), `companyId` (Liferay 
    Portal instance ID), and `groupId` (site ID) the app retrieves data from. 

4.  Change the `companyId` and `groupId` to match those of your Liferay Portal 
    installation. You can find your company ID in your portal's *Control Panel* 
    &rarr; *Configuration* &rarr; *Portal Instances* menu. The company ID is in 
    the *Instance ID* column. You can find your site ID from the site you put 
    the Guestbook portlet on. Navigate to this site and select *Admin* &rarr; 
    *Site Administration* &rarr; *Configuration* from the Dockbar. The site ID 
    is listed on the *Site Settings* tab. 

Stupendous! You've successfully installed Liferay Screens and the Guestbook 
Mobile SDK, and configured your app to communicate with your Liferay Portal 
instance. 
