# Using Screenlets in Android Apps [](id=using-screenlets-in-android-apps)

You can start using Screenlets once you've
[installed Liferay Screens in your Android project](/develop/tutorials/-/knowledge_base/7-0/preparing-android-projects-for-liferay-screens). 
The Screenlets that come with every Screens installation are described in the 
[Screenlet reference documentation](/develop/reference/-/knowledge_base/7-0/screenlets-in-liferay-screens-for-android). 
You may also have custom Screenlets that you or others wrote. To use any 
Screenlet, regardless of its origin, you must follow two steps: 

1. Insert the Screenlet's XML in the layout of the activity or fragment where 
   you want the Screenlet to appear. 

2. Implement the Screenlet's listener in the activity or fragment class. 

As an example of these steps, this tutorial shows you how to use 
[Login Screenlet](/develop/reference/-/knowledge_base/7-0/loginscreenlet-for-android). 

<iframe width="560" height="315" src="https://www.youtube.com/embed/TZ09fbV9UuU" frameborder="0" allowfullscreen></iframe>

## Inserting the Screenlet XML

First, open the layout file of the fragment or activity you want to use the 
Screenlet in. Then insert the Screenlet's XML in the location in the layout file 
where you want the Screenlet to appear. When inserting a Screenlet's XML, you 
must also set any attributes that control the Screenlet's behavior. 

As an example, the following layout file shows Login Screenlet's XML inside a 
`RelativeLayout`: 

    <?xml version="1.0" encoding="utf-8"?>
    <RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:tools="http://schemas.android.com/tools"
        xmlns:liferay="http://schemas.android.com/apk/res-auto"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:paddingBottom="@dimen/activity_vertical_margin"
        android:paddingLeft="@dimen/activity_horizontal_margin"
        android:paddingRight="@dimen/activity_horizontal_margin"
        android:paddingTop="@dimen/activity_vertical_margin"
        tools:context="com.liferay.docs.liferayguestbook.MainActivity">

        <com.liferay.mobile.screens.auth.login.LoginScreenlet
            android:id="@+id/login_screenlet"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            liferay:basicAuthMethod="screen_name"
            liferay:layoutId="@layout/login_default"
            />
    </RelativeLayout>

The `RelativeLayout` contains no other UI elements besides Login Screenlet, and 
`android:layout_width` and `android:layout_height` are set to `match_parent` in
Login Screenlet's XML. This sets Login Screenlet to take up the entire screen, 
which is appropriate for logging in. Next, look at the `liferay` attributes in 
Login Screenlet's XML. The `basicAuthMethod` attribute is unique to Login 
Screenlet and sets its authentication method (`screen_name`, `email`, or 
`user_id`), which must match that of the Liferay instance. The `layoutId` 
attribute is available in every Screenlet because it sets the UI used to render 
the Screenlet (its View). You can read more about Screenlet Views in the 
[tutorial on using Views](/develop/tutorials/-/knowledge_base/7-0/using-views-in-android-screenlets). 
The attributes available for each Screenlet included in Liferay Screens are 
listed in the 
[Screenlet reference documentation](/develop/reference/-/knowledge_base/7-0/screenlets-in-liferay-screens-for-android). 

Great! Now you know how to insert a Screenlet's XML and set its attributes. 
Next, you'll implement the Screenlet's listener in the layout's activity or 
fragment class. 

## Implementing the Screenlet's Listener

To use a Screenlet, you must also implement its listener interface in the 
layout's activity or fragment class. This lets the class listen for, and 
optionally respond to, actions that occur in the Screenlet. You must also 
register the class as the Screenlet's listener. 

Each Screenlet has a unique listener. 
[The Screenlet reference documentation](/develop/reference/-/knowledge_base/7-0/screenlets-in-liferay-screens-for-android) 
documents the listener of each Screenlet that comes with Liferay Screens. For 
example, 
[click here](https://dev.liferay.com/develop/reference/-/knowledge_base/7-0/loginscreenlet-for-android#listener) 
to view the documentation for Login Screenlet's listener: `LoginListener`. To 
use Login Screenlet, you must therefore implement the `LoginListener` methods 
`onLoginSuccess` and `onLoginFailure`. Screens calls these methods when login 
succeeds or fails, respectively. Here's an example implementation of these 
methods: 

    @Override
    public void onLoginSuccess(User user) {
        Intent intent = new Intent(this, YourActivity.class);
        startActivity(intent);
    }

    @Override
    public void onLoginFailure(Exception e) {
        Toast.makeText(this, "Couldn't log in " + e.getMessage(), Toast.LENGTH_LONG).show();
    }

Just replace `YourActivity` with the activity in your app that you want to 
launch upon successful login. 

Now you must get a Screenlet instance and set the activity or fragment class as 
its listener. To get this instance, you'll use the value of the `android:id` 
attribute from the Screenlet's XML. You'll then use the Screenlet instance's 
`setListener` method to set the activity or fragment class as the Screenlet's 
listener. Where you put this code is also important. In activities, it should go 
at the end of the `onCreate` method. In fragments, you can put it at the end of 
either the `onCreate` or `onCreateView` methods. For example, since Login 
Screenlet is typically in an app's first activity, you should include this code 
at the end of the activity's `onCreate` method. Here's a simple example 
implementation for an activity called `MainActivity`: 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoginScreenlet loginScreenlet = (LoginScreenlet) findViewById(R.id.login_screenlet);
        loginScreenlet.setListener(this);
    }

That's all there is to it! Awesome! Now you know how to use Screenlets in your 
Android apps. 

## Related Topics

[Preparing Android Projects for Liferay Screens](/develop/tutorials/-/knowledge_base/7-0/preparing-android-projects-for-liferay-screens)

[Using Views in Android Screenlets](/develop/tutorials/-/knowledge_base/7-0/using-views-in-android-screenlets)

[Creating Android Screenlets](/develop/tutorials/-/knowledge_base/7-0/creating-android-screenlets)

[Using Screenlets in iOS apps](/develop/tutorials/-/knowledge_base/7-0/using-screenlets-in-ios-apps)
