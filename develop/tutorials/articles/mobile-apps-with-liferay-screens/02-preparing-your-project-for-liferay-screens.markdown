# Preparing Your Project for Liferay Screens [](id=preparing-your-project-for-liferay-screens)

Your Android or iOS project must be set up for use with Liferay Screens. This 
consists of installing Screens in your project and then configuring its 
dependencies. This tutorial's first section shows you how to do this in your 
Android project by using Gradle, Maven, or manual configuration. This tutorial's 
second section shows you how to use CocoaPods to configure Screens in your iOS 
project. Let the configuration begin! 

## Preparing Your Android Project for Screens [](id=preparing-your-android-project-for-screens)

Liferay Screens is released as an [AAR file](http://tools.android.com/tech-docs/new-build-system/aar-format) 
that is currently hosted in jCenter. The following sections describe how to set 
up Screens with Gradle and Maven. While instructions are also provided for 
manual setup, it's strongly recommended that you use Gradle or Maven to set and 
download your dependencies. 

### Using Gradle to Configure Your Project [](id=using-gradle-to-configure-your-project)

Use the following steps to configure your project with Gradle:

1. Add jCenter to your repositories:

        repositories {
            jcenter()
        }

2. Include the SDK as a dependency:

        dependencies {
            compile 'com.liferay.mobile:liferay-screens:0.3.0'
        }

Gradle downloads all the necessary dependencies before building your project. If 
you get errors such as `Duplicate files copied in APK META-INF/NOTICE` when 
building with Gradle, add this to your `build.gradle` file:

    android {
        ...
        packagingOptions {	
            exclude 'META-INF/LICENSE'
            exclude 'META-INF/NOTICE'
        }
        ...
    }
    
Next, the steps for configuring your project with Maven are shown.

### Using Maven to Configure Your Project [](id=using-maven-to-configure-your-project)

Use the following steps to configure your project with Maven:

1. Add the following dependency to your `pom.xml`:

        <dependency>
            <groupId>com.liferay.mobile</groupId>
            <artifactId>liferay-screens</artifactId>
            <version>LATEST</version>
        </dependency>

2. Force a Maven update to download all the dependencies.

Note that if Maven can't locate the artifact, you need to add jCenter as a new 
repository in your maven settings (`.m2/settings.xml`):

    <profiles>
        <profile>
            <repositories>
                <repository>
                    <id>bintray-liferay-liferay-mobile</id>
                    <name>bintray</name>
                    <url>http://dl.bintray.com/liferay/liferay-mobile</url>
                </repository>
            </repositories>
            <pluginRepositories>
                <pluginRepository>
                    <id>bintray-liferay-liferay-mobile</id>
                    <name>bintray-plugins</name>
                    <url>http://dl.bintray.com/liferay/liferay-mobile</url>
                </pluginRepository>
            </pluginRepositories>
	    <id>bintray</id>
        </profile>
    </profiles>
    <activeProfiles>
        <activeProfile>bintray</activeProfile>
    </activeProfiles>

Even though using Gradle or Maven to fully configure your project is strongly 
recommended, the manual configuration steps are shown next. You should note that 
the manual configuration still requires that you use Gradle to point to the 
manually installed components.

### Configuring Your Project Manually [](id=configuring-your-project-manually)

If you're using Gradle, you can use the following steps to manually install 
Screens and its dependencies: 

1. [Download](https://github.com/liferay/liferay-screens/releases) the latest 
   version of Liferay Screens for Android.

2. Copy the contents of `Android/library` in a folder next to your project.

3. Configure a `settings.gradle` file in your project with the paths to the 
   library folders:

        include ':core'
        include ':themes'
        project(':core').projectDir = new File(settingsDir, '../../library/core')
        project(':themes').projectDir = new File(settingsDir, '../../library/themes')

4. Include the required dependencies in your `build.gradle` file: 

        compile project (':core')
        compile project (':themes')

You can also configure the `.aar` binary files (in `Android/dist`) as local 
`.aar` dependencies.

Super! Your Android project should now be ready for Liferay Screens. The next 
section shows you how to configure your iOS project to use Screens.

## Preparing Your iOS Project for Screens [](id=preparing-your-ios-project-for-screens)

Liferay Screens for iOS is released as a plain source code library. As soon as 
CocoaPods supports Swift libraries ([1](https://github.com/CocoaPods/CocoaPods/pull/2222), [2](https://github.com/CocoaPods/CocoaPods/issues/2272)), 
you'll be able to set up your project by simply adding a single line to your 
`Podfile`. 

In the meantime, there are a few things you need to manually set up in your app 
to prepare it for Liferay Screens. First, you need to download the 
[Liferay Screens source code](https://github.com/liferay/liferay-screens/releases) 
and include it in your project. The steps for doing this are shown here:

1. Create a folder at the root of your project called `Liferay-Screens`.

2. Copy the folders `Library/Core` and `Library/Themes` from the downloaded 
   source code into this new folder. After this, you'll have two subdirectories 
   in your project's `Liferay-Screens` directory: `Core` and `Themes`.
   
3. In the Finder, drag and drop `Liferay-Screens` into your Xcode project.

![Figure 1: This Xcode project includes Liferay Screens.](../../images/screens-ios-project-setup.png)

Next, set up [CocoaPods](http://cocoapods.org) for your project if you haven't 
done so already. Add the dependencies to your `Podfile` and then execute 
`pod install`. Use this [Podfile](https://github.com/liferay/liferay-screens/tree/master/ios/Library/Podfile) 
as a template. Note that you should consider using the 
[CocoaPods for Xcode plugin](https://github.com/kattrali/cocoapods-xcode-plugin). 
You can install it through the [Alcatraz package manager](http://alcatraz.io/) 
for Xcode. This way, you can perform these tasks from Xcode. 

![Figure 2: The CocoaPods for Xcode plugin.](../../images/screens-ios-xcode-cocoapods.png)

In your project's build settings, you also need to edit the 
*Objective-C bridging header* to include 
`${SRCROOT}/Liferay-Screens/Core/liferay-screens-bridge.h`. This is shown in 
the following screenshot:

![Figure 3: The Objective-C bridging header.](../../images/screens-ios-project-header.png)

There's just one more thing to take care of to ensure that your project is ready 
for Liferay Screens. Create a new property list file called 
`liferay-server-context.plist`. You'll use this file to configure the settings 
for your Liferay Portal instance. Use [`liferay-server-context-sample.plist`](https://github.com/liferay/liferay-screens/tree/master/ios/Library/Core/liferay-server-context-sample.plist) 
as a template. This screenshot shows such a file being browsed:

![Figure 4: The `liferay-context.plist` file.](../../images/screens-ios-liferay-context.png)

Great! Your iOS project should now be ready for Liferay Screens. 

## Related Topics [](id=related-topics)

[Using Screenlets](/tutorials/-/knowledge_base/6-2/using-screenlets)

[Using Views and Themes](/tutorials/-/knowledge_base/6-2/using-views-and-themes)

[Architecture of Liferay Screens](/tutorials/-/knowledge_base/6-2/architecture-of-liferay-screens)
