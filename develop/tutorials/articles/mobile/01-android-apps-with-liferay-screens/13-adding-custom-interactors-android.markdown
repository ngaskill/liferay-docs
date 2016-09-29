# Adding Custom Interactors to Android Screenlets [](id=adding-custom-interactors-to-android-screenlets)

Interactors are Screenlet components that implement server communication for a 
specific use case. For example, Login Screenlet's Interactor calls the Liferay 
Mobile SDK service that authenticates a user to a Liferay instance. Similarly, 
the Interactor for 
[the example Add Bookmark Screenlet](/develop/tutorials/-/knowledge_base/7-0/creating-android-screenlets) 
calls the Liferay Mobile SDK service that adds a bookmark to the Bookmarks 
portlet. 

That's all fine and well, but what if you want to customize a Screenlet's server 
call? What if you want to use a different back-end with a Screenlet? No problem! 
You can implement a custom Interactor for the Screenlet. You can plug in a 
different Interactor that makes its server call by using custom logic or network 
code. To do this, you must extend an existing Interactor class and then pass 
your implementation to the Screenlet you want to use it with. You should do this 
inside your app's code, in either an inner class of the activity or fragment 
class that contains the Screenlet, or in a separate class. 

In this tutorial, you'll see an example custom Interactor that overrides 
[Login Screenlet's](/develop/reference/-/knowledge_base/7-0/loginscreenlet-for-android) 
Interactor to always log in the same user, without a password. You'll also see 
an example custom Interactor that accesses a non-Liferay backend. Before you 
begin, make sure that you've read the following tutorials:

- [Using Screenlets in Android Apps](/develop/tutorials/-/knowledge_base/7-0/using-screenlets-in-android-apps)

- [Architecture of Liferay Screens for Android](/develop/tutorials/-/knowledge_base/7-0/architecture-of-liferay-screens-for-android)

- [Creating Android Screenlets](/develop/tutorials/-/knowledge_base/7-0/creating-android-screenlets)

## Implementing a Custom Interactor [](id=implementing-a-custom-interactor)

Your custom Interactor class must extend a Screenlet's existing Interactor 
class and override the Screenlet behavior you want to modify. For example, the 
`login()` method in Login Screenlet's 
[`LoginBasicInteractor` class](https://github.com/liferay/liferay-screens/blob/master/android/library/src/main/java/com/liferay/mobile/screens/auth/login/interactor/LoginBasicInteractor.java) 
authenticates the user. To change how authentication occurs, you can extend 
`LoginBasicInteractor` and override `login()` to implement your custom 
authentication logic. 

To use your custom Interactor class, you must pass it to its Screenlet in the 
activity or fragment class that uses the Screenlet. Therefore, it's usually 
simpler to implement a custom Interactor as an inner class in that activity or 
fragment class. 

As an example, the following steps show you how to create and use a custom 
Interactor for 
[Login Screenlet](/develop/reference/-/knowledge_base/7-0/loginscreenlet-for-android). 
This custom Interactor, `CustomLoginInteractor`, overrides Login Screenlet's 
Interactor to always log in the same user, without a password. You can find the 
complete example code in the 
[Liferay Screens Test App on GitHub](https://github.com/liferay/liferay-screens/blob/master/android/samples/test-app/src/main/java/com/liferay/mobile/screens/testapp/CustomInteractorActivity.java). 
Note that this example implements and uses the custom Interactor in an inner 
class of an activity (`CustomInteractorActivity`) that uses Login Screenlet. 

1. Create your custom Interactor class. This class must extend the original 
   Interactor's interface. In your Interactor's custom logic, you must call the 
   Screenlet's listener methods where appropriate. 
   
    For example, `CustomLoginInteractor` overrides Login Screenlet's Interactor, 
    so it must extend the 
    [`LoginBasicInteractor` class](https://github.com/liferay/liferay-screens/blob/master/android/library/src/main/java/com/liferay/mobile/screens/auth/login/interactor/LoginBasicInteractor.java). 
    It also overrides the `login()` method to call the `LoginListener` methods 
    `onLoginSuccess` and `onLoginFailure` where necessary: 

        private class CustomLoginInteractor extends LoginBasicInteractor {

            public CustomLoginInteractor(int targetScreenletId) {
                super(targetScreenletId);
            }

            @Override
            public void login() throws Exception {
                //custom implementation

                String username = "test";

                if (username.equals(_login) && username.equals(_password)) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("emailAddress", "test@liferay.com");
                    jsonObject.put("userId", "0");
                    jsonObject.put("firstName", username);
                    jsonObject.put("lastName", username);
                    jsonObject.put("screenName", username);

                    User fakeUser = new User(jsonObject);

                    SessionContext.setCurrentUser(fakeUser);
                    SessionContext.createBasicSession(username, username);

                    getListener().onLoginSuccess(fakeUser);
                }
                else {
                    getListener().onLoginFailure(new AuthenticationException("bad login"));
                }
            }
        }

2. In the activity or fragment class that uses the Screenlet, override the 
   `createInteractor` method to return an instance of your custom Interactor. 
   For example, the `createInteractor` method in `CustomInteractorActivity` 
   returns a new `CustomLoginInteractor` instance: 

        @Override
        public LoginInteractor createInteractor(String actionName) {
            return new CustomLoginInteractor(_loginScreenlet.getScreenletId());
        }

3. Set the custom Interactor to the Screenlet. Recall that to 
   [use a Screenlet](/develop/tutorials/-/knowledge_base/7-0/using-screenlets-in-android-apps), 
   you must set the class of the activity or fragment that contains it as its 
   listener. You do this by getting a reference to the Screenlet and then 
   calling `setListener(this)` on the Screenlet reference. In activities, you do 
   this at the end of the `onCreate` method. In fragments, you do this at the 
   end of either the `onCreate` or `onCreateView` method. When using a custom 
   Interactor, you must also set the activity or fragment class to listen for 
   the custom Interactor. You do this by calling 
   `setCustomInteractorListener(this)` on the Screenlet reference. This 
   effectively registers the custom Interactor with the Screenlet being used in 
   that class. 

    For example, the `onCreate` method in `CustomInteractorActivity` gets a 
    reference to `LoginScreenlet`, calls `setListener(this)` to set the activity 
    as the Screenlet's listener, and then calls 
    `setCustomInteractorListener(this)` to set the Screenlet's custom 
    Interactor: 

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.content_custom_interactor);

            _loginScreenlet = (LoginScreenlet) findViewById(R.id.login_screenlet_custom_interactor);
            _loginScreenlet.setListener(this);
            _loginScreenlet.setCustomInteractorListener(this);
        }

Great! Now you know how to implement and use a custom Interactor for a 
Screenlet. The next example shows you how to access non-Liferay backends with a 
custom Interactor. 

## Using Custom Interactors to Access Other Backends [](id=using-custom-interactors-to-access-other-backends)

Liferay Screens is so great (and modest), that it can use custom Interactors to 
communicate with non-Liferay backends. The following example illustrates this by 
creating a custom Interactor for the 
[example Add Bookmark Screenlet](https://dev.liferay.com/develop/tutorials/-/knowledge_base/7-0/creating-android-screenlets) 
that can store bookmarks at 
[Delicious](https://delicious.com). 
You can find this example's complete code in `AddDeliciousInteractorImpl` 
[at this gist](https://gist.github.com/nhpatt/7cbeb0df6f39ec8a9176). 
<!-- We should create this gist in the liferay-screens repo -->

1. Create a new custom Interactor class. This class should extend the 
   [`BaseRemoteInteractor` class](https://github.com/liferay/liferay-screens/blob/master/android/library/src/main/java/com/liferay/mobile/screens/base/interactor/BaseRemoteInteractor.java) 
   (the base class of all Interactor classes) with the Screenlet's listener as a 
   type argument. It should also implement the Screenlet's Interactor interface. 
   For a constructor, you can call the superclass constructor. 

    For example, `AddDeliciousInteractorImpl` extends `BaseRemoteInteractor` 
    with `AddBookmarkListener` as a type argument, and implements the 
    `AddBookmarkInteractor` interface: 

        public class AddDeliciousInteractorImpl extends BaseRemoteInteractor<AddBookmarkListener>
            implements AddBookmarkInteractor {

            public AddDeliciousInteractorImpl(int targetScreenletId) {
                super(targetScreenletId);
            }

            ...
        }

2. In this class, implement your Interactor's custom logic. To do this, you must 
   implement the same methods as the Interactor class that your custom 
   Interactor replaces. You can also add any new methods or code that your 
   custom Interactor requires. Since Liferay Screens uses the 
   [EventBus library](http://greenrobot.org/eventbus/) 
   to communicate results, be sure to post the server call's results to 
   EventBus. 

    For example, `AddDeliciousInteractorImpl` must use the Delicious API to add 
    a new bookmark to Delicious. This class implements the `addBookmark` and 
    `onEvent` methods originally implemented by the 
    [`AddBookmarkInteractorImpl` class](https://github.com/liferay/liferay-screens/blob/master/android/samples/addbookmarkscreenlet/src/main/java/com/liferay/mobile/screens/bookmark/interactor/AddBookmarkInteractorImpl.java). 
    Note that `AddDeliciousInteractorImpl` also contains the inner class 
    `BookmarkAdded`. Although this additional class isn't required, it makes 
    modeling the server call's results simpler. The `addBookmark` implementation 
    uses the 
    [OkHttp library](http://square.github.io/okhttp/) 
    to send a bookmark's URL and description to the Delicious API. The server 
    call's results are then posted to EventBus with `EventBusUtil.post`. Note 
    that this all happens in a new thread. This is becuase Android doesn't allow 
    network communication to occur in its main thread. Also note that the 
    `AddDeliciousInteractorImpl`'s `onEvent` implementation is similar to that 
    of `AddBookmarkInteractorImpl`. Both retrieve the server call's results and 
    then call the appropriate listener method. The implementation in 
    `AddDeliciousInteractorImpl`, however, retrieves the results from a 
    `BookmarkAdded` object instead of an event object: 

        public void addBookmark(final String url, final String title, long folderId) throws Exception {
            // Make the server call in a new thread
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        // Use OkHttp to make the server call
                        Headers headers = Headers.of("Authorization", 
                            "Bearer 11336429-c2378f3c44a29f593e31aa4f5521e4dd");

                        OkHttpClient client = new OkHttpClient();

                        Request add = new Request.Builder()
                            .url("https://api.del.icio.us/api/v1/posts/add?url=" + url + "&description=" + title)
                            .headers(headers)
                            .build();

                        client.newCall(add).execute();

                        Request get = new Request.Builder()
                            .url("https://api.del.icio.us/api/v1/posts/get")
                            .headers(headers)
                            .build();

                        com.squareup.okhttp.Response response = client.newCall(get).execute();

                        String text = response.body().string();

                        // Post the server call's results to EventBus
                        if (text.contains(url)) {
                            LiferayLogger.i(text);
                            EventBusUtil.post(new BookmarkAdded(text));
                        }
                        else {
                            EventBusUtil.post(new BookmarkAdded(new Exception("sth went wrong")));
                        }

                    }
                    catch (IOException e) {
                        // Log the error and post it to EventBus
                        LiferayLogger.e("Error sending", e);
                        EventBusUtil.post(new BookmarkAdded(e));
                    }
                }
            }).start();
        }

        public void onEvent(BookmarkAdded bookmarkAdded) {
            if (bookmarkAdded.hasError()) {
                getListener().onAddBookmarkFailure(bookmarkAdded.getE());
            }
            else {
                getListener().onAddBookmarkSuccess();
            }
        }

        class BookmarkAdded {

            public BookmarkAdded(String text) {
                this.text = text;
            }

            public BookmarkAdded(Exception e) {
                this._e = e;
            }

            public boolean hasError() {
                return _e != null;
            }

            public Exception getE() {
                return _e;
            }

            private String text;
            private Exception _e;
        }

3. Implement the 
   [`CustomInteractorListener` interface](https://github.com/liferay/liferay-screens/blob/master/android/library/src/main/java/com/liferay/mobile/screens/base/interactor/CustomInteractorListener.java) 
   in the class of the activity or fragment that contains the Screenlet. You 
   must implement `CustomInteractorListener`'s `createInteractor` method to 
   return an instance of your custom Interactor class: 

        @Override
        public Interactor createInteractor(String actionName) {
            return new AddDeliciousInteractorImpl(_screenlet.getScreenletId());
        }

4. Set the custom Interactor to the Screenlet. Do this the exact same way as 
   instructed in the last step of the preceding section. 

Awesome! Now that's a tasty custom Interactor! Now you know how to create a 
custom Interactor that can communicate with a non-Liferay backend. This opens up 
even more possibilities for your apps. 

## Related Topics [](id=related-topics)

[Architecture of Liferay Screens for Android](/develop/tutorials/-/knowledge_base/7-0/architecture-of-liferay-screens-for-android)

[Creating Android Screenlets](/develop/tutorials/-/knowledge_base/7-0/creating-android-screenlets)
