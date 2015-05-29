# Mobile Apps with Liferay Screens [](id=mobile-apps-with-liferay-screens)

Liferay Screens speeds up and simplifies developing native mobile apps that use
Liferay. Its power lies in its *screenlets*. A screenlet is a visual component
that you insert into your native app to leverage Liferay Portal's content and
[services](/develop/tutorials/-/knowledge_base/6-2/invoking-remote-services). On
Android and iOS, screenlets are available that allow users to log in to your
portal, create accounts, submit forms, publish content, and more. You can use
any number of screenlets in your app; they're independent, so you can use them
in modular fashion. Screenlets also deliver UI flexibility with pluggable
*views* (Android) or *themes* (iOS). Each view or theme implements a specific
look and feel. Views and themes allow developers to create elegant user
interfaces. Liferay's reference documentation for
[Android](/develop/reference/-/knowledge_base/6-2/screenlets-in-liferay-screens-for-android) 
and
[iOS](/develop/reference/-/knowledge_base/6-2/screenlets-in-liferay-screens-for-ios)
screenlets describes each screenlet's features and views/themes. 

You might be thinking, "These Screenlets sound like the greatest thing since
taco Tuesdays, but what if they don't fit in with my app's UI? What if they
don't behave exactly how I want them to? What if there's no screenlet for what I
want to do?" Fret not! You can customize screenlets to fit your needs by
changing or extending their UI or behavior. You can even write your own
screenlets! What's more, Screens seamlessly integrates with Android and iOS
projects. 

![Figure 1: Here's an app that uses a Liferay Screens sign up screenlet.](../../images/screens-phone-intro.png)

Screenlets leverage the
[Liferay Mobile SDK](https://www.liferay.com/community/liferay-projects/liferay-mobile-sdk/overview)
to make server calls. The Mobile SDK is a low-level layer on top of the Liferay
JSON API. To write your own screenlets, you must familiarize yourself with
Liferay's
[remote services](/develop/tutorials/-/knowledge_base/6-2/invoking-remote-services).
If no existing screenlet meets your needs, consider customizing an existing
screenlet, creating a screenlet, or directly using the Mobile SDK. Creating a
screenlet involves writing Mobile SDK calls and constructing the screenlet; if
you don't plan to reuse or distribute the implementation then you may want to
forgo writing a screenlet and, instead, work with the
[Mobile SDK](/develop/tutorials/-/knowledge_base/6-2/mobile). When you integrate
an existing screenlet into your app, many details regarding use of the Mobile
SDK are hidden by a layer of abstraction. This is the main advantage provided by
integrating an existing screenlet into your app (instead of leveraging the
Mobile SDK directly or creating a screenlet).

These tutorials explain how to use, customize, create, and distribute screenlets
for iOS and Android. They also show you how to create views and themes. There
are even tutorials that explain the nitty-gritty details of the Liferay Screens
architecture. No matter how deep you want to go, you'll be using screenlets in
no time. Start by preparing your
[Android project](/develop/tutorials/-/knowledge_base/6-2/preparing-android-projects-for-liferay-screens)
or
[iOS project](/develop/tutorials/-/knowledge_base/6-2/preparing-ios-projects-for-liferay-screens)
to use Liferay Screens. 
