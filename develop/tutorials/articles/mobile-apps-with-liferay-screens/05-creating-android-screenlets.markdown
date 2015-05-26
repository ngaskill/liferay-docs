# Creating Android Screenlets [](id=creating-android-screenlets)

The screenlets that come with Liferay Screens cover many common use cases in 
mobile apps that connect to Liferay. For example, you can use them to 
authenticate users, interact with Dynamic Data Lists, and view assets. However, 
what if there's not a screenlet for *your* specific use case? No sweat! You can 
write your own screenlet. Screenlets can also be written by others and 
contributed to the Screens project. Screenlets can also be added to jCenter and 
Maven Central for distribution. This extensibility is a key strength of Screens. 

This tutorial shows you how to create your own screenlets in Liferay Screens for 
Android. As an example, code is provided for a [sample bookmarks screenlet](https://github.com/liferay/liferay-screens/tree/master/android/samples/addbookmarkscreenlet) 
that lets the user enter the bookmark's URL and title in text boxes. When the 
user touches the submit button, the URL and title are sent to the Liferay 
instance's Bookmark service to be saved.  

To understand the components that comprise a screenlet, you first may want to 
read the tutorial [Architecture of Liferay Screens for Android](/develop/tutorials/-/knowledge_base/6-2/architecture-of-liferay-screens-for-android). 
You may also want to read the tutorial [Creating Android Views](/develop/tutorials/-/knowledge_base/6-2/creating-android-views) 
to learn how to support your new screenlet from the view you want to use. 
Without any further ado, let the screenlet creation begin! 

## Deciding Your Screenlet's Location [](id=deciding-your-screenlets-location)

You have a couple different options on where to create your screenlet, depending 
on how you plan on using it. If you don't plan to reuse your screenlet in 
another app, or if you don't want to redistribute it, the best place to create 
it is in a new package inside your project. This way you can reference and 
access Liferay Screens' core, in addition to all the view sets you've imported. 

If you want to reuse your screenlet in another app, you need to create it in a 
new Android application module. The steps for creating such a module are 
presented in the tutorial [Packaging Your Android Screenlets](http://www.liferay.com/).

## Creating the Screenlet's UI [](id=creating-the-screenlets-ui)

You should start by creating the screenlet's UI. This is done by creating a new 
interface for the view and then creating a new layout file. Use the following 
steps to do this for the bookmarks screenlet: 

1. Create a new view model interface that extends [`BaseViewModel`](https://github.com/liferay/liferay-screens/blob/master/android/library/core/src/main/java/com/liferay/mobile/screens/base/view/BaseViewModel.java). 
   This is for adding the attributes to show in the view. Specify the getters 
   and setters for these attributes in the interface. Any view classes you write 
   for your screenlet must implement this interface. In this example, the 
   interface is called `AddBookmarkViewModel`. Its attributes are `url` and 
   `title`. 

        public interface AddBookmarkViewModel extends BaseViewModel {
            String getURL();
            
            void setURL(String value);
            
            String getTitle();
            
            void setTitle(String value);
        }

2. Build your screenlet's UI by writing a layout XML file. Be sure to include 
   the UI elements you want to use for displaying the attributes you defined in 
   the previous step. This file's root element should be a custom view class 
   that you'll create in the next step. For the `AddBookmarkScreenlet` in this 
   example, this layout file is called `bookmark_default.xml`. It has two 
   `EditText` tags: one for the URL and another for the title. It also contains 
   a `Button` tag to let the user save the bookmark. 

        <?xml version="1.0" encoding="utf-8"?>
        <com.your.package.AddBookmarkView 
            xmlns:android="http://schemas.android.com/apk/res/android"
            style="@style/default_screenlet">
        
            <EditText
                android:id="@+id/url"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginBottom="15dp"
                android:hint="URL Address"
                android:inputType="textUri"/>
        
            <EditText
                android:id="@+id/title"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginBottom="15dp"
                android:hint="Title"/>
        
            <Button
                android:id="@+id/add_button"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="Add Bookmark"/>
        
        </com.your.package.AddBookmarkView>

At this point, the graphical layout viewer in Android Studio should reflect your 
UI. For example, the `AddBookmarkScreenlet` UI looks like this: 

![Figure 1: Android Studio's graphical layout viewer while creating your own screenlet.](../../images/screens-android-add-bookmark-view.png)

Next, you'll create a view class for the screenlet. 

## Creating the Screenlet's View Class [](id=creating-the-screenlets-view-class)

Your new screenlet now needs a view class to implement the UI you just created. 
This class should extend an Android layout class and implement the interface you 
wrote in this tutorial's previous section. Begin by calling the superclass 
constructors for the Android layout class that you're extending. Then override 
the `showStartOperation`, `showFinishOperation`, and `showFailedOperation` 
methods from [`BaseViewModel`](https://github.com/liferay/liferay-screens/blob/master/android/library/core/src/main/java/com/liferay/mobile/screens/base/view/BaseViewModel.java). 
You also need to override Android's `onFinishInflate` method to get references 
to the UI components in your layout XML. Then implement the getters and setters 
from your view model interface by using the components' inner values. As an 
example of these steps, the view class `AddBookmarkView` is shown here for the 
`AddBookmarkScreenlet`: 

    public class AddBookmarkView extends LinearLayout 
        implements AddBookmarkViewModel {
    
        public AddBookmarkView(Context context) {
            super(context);
        }
    
        public AddBookmarkView(Context context, AttributeSet attributes) {
            super(context, attributes);
        }
    
        public AddBookmarkView(Context context, AttributeSet attributes, int defaultStyle) {
            super(context, attributes, defaultStyle);
        }
        
        @Override
        public void showStartOperation(String actionName) {
        }
    
        @Override
        public void showFinishOperation(String actionName) {
        }
    
        @Override
        public void showFailedOperation(String actionName, Exception e) {
        }
        
        @Override
        protected void onFinishInflate() {
            super.onFinishInflate();
    
            _urlText = (EditText) findViewById(R.id.url);
            _titleText = (EditText) findViewById(R.id.title);
        }
    	
        public String getURL() {
            return _urlText.getText().toString();
        }
    
        public void setURL(String value) {
            _urlText.setText(value);
        }
    
        public String getTitle() {
            return _titleText.getText().toString();
        }
    
        public void setTitle(String value) {
            _titleText.setText(value);
        }
    
        private EditText _urlText;
        private EditText _titleText;
    
    }

Now you're ready to create your screenlet's interactor class.

## Creating the Screenlet's Interactor Class [](id=creating-the-screenlets-interactor-class)

Now you're ready to create your screenlet's interactor class. The interactor 
class is responsible for calling the Liferay remote service that you need. Note 
that it's a good practice to use [IoC](http://en.wikipedia.org/wiki/Inversion_of_control) 
in your interactor classes. This way, anyone can provide a different 
implementation without breaking the code. The `Interactor` base class also needs 
a parameter that represents the listener type to notify. Create an interactor 
interface that extends `Interactor<YourListenerType>`. In this interface, define 
any methods that call the Liferay remote services you need. For example, the 
interactor interface `AddBookmarkInteractor` for the example 
`AddBookmarkScreenlet` is shown here:

    public interface AddBookmarkInteractor extends Interactor<AddBookmarkListener> {
    
        void addBookmark(String url, String title, Integer folderId)
            throws Exception;
    
    }

Next, create a class that extends `BaseRemoteInteractor<YourListenerType>` and 
implements the interactor interface you just created. You can use the superclass 
constructor in this class. In the interface's method implementations, use the 
Liferay Mobile SDK to call the remote services that you need. When the request 
ends, make sure you post an event into the `EventBus` by using 
`EventBusUtil.post(event)`, where `event` is a `JSONObjectEvent` object 
containing the `targetScreenletId` together with either the result or the 
exception. Note that every interactor you write needs to implement the `onEvent` 
method. This method is invoked by the `EventBus` and calls the registered 
listener. A good example of this is the `AddBookmarkInteractorImpl` class for 
the `AddBookmarkScreenlet`:

    public class AddBookmarkInteractorImpl
        extends BaseRemoteInteractor<AddBookmarkListener>
        implements AddBookmarkInteractor {
    
        public AddBookmarkInteractorImpl(int targetScreenletId) {
            super(targetScreenletId);
        }
    
        public void addBookmark(String url, String title, Integer folderId) {
            // 1. Validate input
            if (url == null || url.isEmpty()) {
                throw new IllegalArgumentException("Invalid url");
            }
    
            // 2. Call the service asynchronously.
            // Notify when the request ends using the EventBus
        }
    
        public void onEvent(JSONObjectEvent event) {
            if (!isValidEvent(event)) {
                return;
            }
    
            if (event.isFailed()) {
                getListener().onAddBookmarkFailure(event.getException());
            }
            else {
                getListener().onAddBookmarkSuccess();
            }
        }
    }
    
Now you can create the `YourListenerType` interface that you used as the 
interactor's type in the previous steps. It's very simple, having only two 
methods: `onYourScreenletFailure` and `onYourScreenletSuccess`. The 
`AddBookmarkListener` interface serves as an example: 

    public interface AddBookmarkListener {
    
        void onAddBookmarkFailure(Exception exception);
    
        void onAddBookmarkSuccess();
    }

Next, you'll create the screenlet class. 

## Creating the Screenlet Class [](id=creating-the-screenlet-class)

Once your interactor is ready, you need to create the screenlet class. This is 
the cornerstone and entry point that the app developer sees and interacts with. 
Since the screenlet is notified by the interactor when the asynchronous 
operation ends, you must implement the listener interface used by the 
interactor. Create the screenlet class so that it implements your listener class 
and extends `BaseScreenlet`, with your view model interface and interactor class 
as type parameters. Again, you can use the superclass constructors in your 
screenlet class. Also, this class usually has another listener to notify the 
app. This listener can be the same one you used in the interactor, or a 
different one altogether (for example, if you want different methods or 
signatures). You can even notify the app using a different mechanism such as the 
Event Bus, Android's `BroadcastReceiver`, or others. Note that the implemented 
interface methods call the view to modify the UI and the app's listener. This 
allows your app to perform any action. 

Your screenlet class also needs to implement the screenlet's abstract methods. 
First up is the `createScreenletView` method. In this method, you get attributes 
from the XML definition and then either store them as class attributes or 
otherwise use them. Next, inflate the view using the layout specified in the 
`liferay:layoutId` attribute. You can even use the read attributes to configure 
the initial state of the view. 

The second abstract method to implement in your screenlet class is 
`createInteractor`. This is a factory method in which you must create the 
corresponding interactor for a specific action name. Note that a single 
screenlet may have several interactions (use cases). Each interaction should 
therefore be implemented in a separate interactor. In the example 
`AddBookmarkScreenlet` there is only one interactor, so the object is created in 
the method. Alternatively, you can retrieve the instance via your IoC 
framework. You also need to pass the `screenletId` (a number autogenerated by 
the `BaseScreenlet` class) to the constructor. 

The third and final abstract method to implement in your screenlet class is 
`onUserAction`. In this method, retrieve the data entered in the view and start 
the operation by using the data and the supplied interactor. The completed 
example screenlet class for the `AddBookmarkScreenlet` is shown here:

    public class AddBookmarkScreenlet
        extends BaseScreenlet<AddBookmarkViewModel, AddBookmarkInteractor>
        implements AddBookmarkListener {
    
        public AddBookmarkScreenlet(Context context) {
            super(context);
        }
    
        public AddBookmarkScreenlet(Context context, AttributeSet attributes) {
            super(context, attributes);
        }
    
        public AddBookmarkScreenlet(Context context, AttributeSet attributes, int defaultStyle) {
            super(context, attributes, defaultStyle);
        }
    
        public void onAddBookmarkSuccess() {
            // Invoked from the interactor:
            // Notify both the view and the app's listener
    
            getViewModel().showFinishOperation(null);
    
            if (_listener != null) {
                _listener.onAddBookmarkSuccess();
            }
        }
    
        public void onAddBookmarkFailure(Exception e) {
            getViewModel().showFinishOperation(null);
    
            if (_listener != null) {
                _listener.onAddBookmarkFailure(e);
            }
        }
    
        public void setListener(AddBookmarkListener listener) {
            _listener = listener;
        }
        
        @Override
        protected View createScreenletView(Context context, AttributeSet attributes) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attributes, 
                R.styleable.AddBookmarkScreenlet, 0, 0);
    
            int layoutId = typedArray.getResourceId(R.styleable.AddBookmarkScreenlet_layoutId, 0);
    
            View view = LayoutInflater.from(context).inflate(layoutId, null);
    
            String defaultTitle = typedArray.getString(R.styleable.AddBookmarkScreenlet_defaultTitle);
    
            _folderId = typedArray.getInteger(R.styleable.AddBookmarkScreenlet_folderId, 0);
    
            typedArray.recycle();
    
            AddBookmarkViewModel viewModel = (AddBookmarkViewModel) view;
            viewModel.setTitle(defaultTitle);
    
            return view;
        }
        
        protected AddBookmarkInteractor createInteractor(String actionName) {
            return new AddBookmarkInteractorImpl(getScreenletId());
        }
        
        @Override
        protected void onUserAction(String userActionName, 
            BookmarkInteractor interactor, Object... args) {
        
            AddBookmarkViewModel viewModel = (AddBookmarkViewModel) getScreenletView();
            String url = viewModel.getURL();	
            String title = viewModel.getTitle();
    
            try {
                interactor.addBookmark(url, title, _folderId);
            }
            catch (Exception e) {
                onAddBookmarkFailure(e);
            }
        }
    
        private AddBookmarkListener _listener;
        private Integer _folderId;
    
    }

Great! Now you know how to create a screenlet class. The final step is hooking 
it up to the rest of the screenlet's code. 

## Triggering the User Action [](id=triggering-the-user-action)

To be able to read the screenlet attributes, you need to add an XML file that 
defines those attributes. Here's an example for the `AddBookmarkScreenlet`: 

    <?xml version="1.0" encoding="utf-8"?>
    <resources>
        <declare-styleable name="AddBookmarkScreenlet">
            <attr name="layoutId"/>
            <attr name="folderId"/>
            <attr name="defaultTitle" format="string"/>
        </declare-styleable>
    </resources>

Next, you need to trigger the user action when the button is pressed. To do 
this, go back to your view class and add a listener to the button. To handle the 
button press, you also need to make your view class implement `OnClickListener`. 
This additional code is shown here for the example `AddBookmarkScreenlet`: 

    protected void onFinishInflate() {
        super.onFinishInflate();
        
        // same code as before
    
        Button addButton = (Button) findViewById(R.id.add_button);
        addButton.setOnClickListener(this);
    }
    
    public void onClick(View v) {
        AddBookmarkScreenlet screenlet = (AddBookmarkScreenlet) getParent();
    
        screenlet.performUserAction();
    }

Celebrate! You're done! Now you can use the screenlet as you would any other. 
Just remember to use the XML file you built your screenlet's UI in as the 
`liferay:layoutId` attribute's value (`bookmark_default` in this example). For 
example, here's the code to insert the `AddBookmarkScreenlet` in an Android 
activity: 

    <com.your.package.AddBookmarkScreenlet
        android:id="@+id/addbookmarks_screenlet"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        liferay:folderId="@integer/bookmark_folder"
        liferay:layoutId="@layout/bookmark_default"
    />

Another thing to keep in mind is additional settings in your app's 
`server_context.xml`, if your screenlet requires them. You can set any variables 
your screenlet needs by naming them in the XML that inserts your screenlet, and 
then using that name to set the variable in your app's `server_context.xml`. For 
example, this is done in the `AddBookmarkScreenlet` to set the folder's 
`folderId` in a Bookmarks portlet. This is the folder the `AddBookmarkScreenlet` 
saves bookmarks to. In the preceding code sample, the `folderId` property is 
named `bookmark_folder` by the `liferay:folderId` setting. In 
`server_context.xml`, `bookmark_folder` is then assigned a value that in turn 
sets the `folderId` variable: 

    <integer name="bookmark_folder">20676</integer>

Congratulations! Now you know how to create screenlets in Screens for Android. 

## Related Topics [](id=related-topics)

[Using Screenlets in Android Apps](/develop/tutorials/-/knowledge_base/6-2/using-screenlets-in-android-apps)

[Architecture of Liferay Screens for Android](/develop/tutorials/-/knowledge_base/6-2/architecture-of-liferay-screens-for-android)

[Creating Android Views](/develop/tutorials/-/knowledge_base/6-2/creating-android-views)
