# Creating Android List Screenlets [](id=creating-android-list-screenlets)

It's very common for mobile apps to display lists of entities. For example, 
Liferay Screens displays asset lists and DDL lists with 
[Asset List Screenlet](/develop/reference/-/knowledge_base/7-0/assetlistscreenlet-for-android) 
and 
[DDL List Screenlet](/develop/reference/-/knowledge_base/7-0/ddllistscreenlet-for-android), 
respectively. For your app to display a list of other entities from a Liferay 
instance, however, you must create your own *list Screenlet*. List Screenlets 
display a list of entities from a Liferay instance. This includes standard 
Liferay entities such as `User`, or custom entities that belong to custom 
Liferay plugins. 

This tutorial shows you how to create your own list Screenlet. As an example, it 
walks you through the creation of Bookmark List Screenlet. This list Screenlet 
displays a list of bookmarks from Liferay's Bookmarks portlet. You can find the 
Screenlet's finished code 
[here in GitHub](https://github.com/liferay/liferay-screens/tree/master/android/samples/listbookmarkscreenlet). 

The list Screenlet framework in Liferay Screens leverages the Screenlet 
framework. Therefore, this tutorial doesn't explain general Screenlet concepts 
and components in detail. Focus is instead placed on creating the list 
Screenlet. Before beginning, you should therefore read the 
[Screens architecture tutorial](/develop/tutorials/-/knowledge_base/7-0/architecture-of-liferay-screens-for-android), 
and the general 
[Screenlet creation tutorial](/develop/tutorials/-/knowledge_base/7-0/creating-android-screenlets). 

You'll create the list Screenlet by following these steps: 

1. Creating the Model Class

2. Creating the Screenlet's View

3. Creating the Screenlet's Interactor

4. Creating the Screenlet's Listener

5. Creating the Screenlet Class

First though, you should understand how pagination works in list Screenlets. 

## Pagination [](id=pagination)

To ensure that users can scroll smoothly through large lists, list Screenlets 
should support fluent pagination. In cases where you only have a small list of 
items, however, you can skip this. For example, if you want to list the days of 
the week, you don't need to implement fluent pagination. 

Liferay Screens gives you some tools to implement fluent pagination with 
configurable page size. Note that Asset List Screenlet and DDL List Screenlet 
also use this approach. To give you an example of this, the example Bookmark 
List Screenlet in this tutorial implements fluent pagination. 

Now you're ready to start creating your list Screenlet! 

## Creating the Model Class [](id=creating-the-model-class)

When you make a server call to retrieve entities from a Liferay instance, they 
come back as JSON. To work with these results efficiently in your app, you must 
convert them to model objects that represent the entity as it exists in the 
Liferay instance. Although the list Screenlet framework's 
[`BaseListCallback`](https://github.com/liferay/liferay-screens/blob/master/android/library/src/main/java/com/liferay/mobile/screens/base/list/interactor/BaseListCallback.java) 
transforms the JSON entities into `Map` objects for you, you must still convert 
these into proper entity objects for use in your app. You'll do this via a model 
class. 

For example, Bookmark List Screenlet needs `Bookmark` objects. The Screenlet's 
[`Bookmark` class](https://github.com/liferay/liferay-screens/blob/master/android/samples/listbookmarkscreenlet/src/main/java/com/liferay/mobile/screens/listbookmark/Bookmark.java) 
creates `Bookmark` objects that contain the bookmark's URL and other data. The 
constructor that takes the `Map<String, Object>` as an argument uses 
`get("url")` to retrieve the URL from the `Map`. Because you always need quick 
access to this value, the constructor sets it to the `_url` instance variable. 
In case you need access to the other values in the `Map`, the constructor then 
saves the entire `Map` to the `_values` instance variable. Besides the getters 
and setter, the remaining code in `Bookmark` implements Android's `Parcelable` 
interface. For more information on this, see 
[Android's documentation on `Parcelable`](https://developer.android.com/reference/android/os/Parcelable.html). 
For quick reference, here's the entire `Bookmark` class: 

    import android.os.Parcel;
    import android.os.Parcelable;

    import java.util.Map;

    public class Bookmark implements Parcelable {

        private String _url;
        private Map _values;

        public static final Creator<Bookmark> CREATOR = new Creator<Bookmark>() {
            @Override
            public Bookmark createFromParcel(Parcel in) {
                return new Bookmark(in);
            }

            @Override
            public Bookmark[] newArray(int size) {
                return new Bookmark[size];
            }
        };

        protected Bookmark(Parcel in) {
            _url = in.readString();
        }

        public Bookmark(Map<String, Object> stringObjectMap) {
            _url = (String) stringObjectMap.get("url");
            _values = stringObjectMap;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(_url);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public String getUrl() {
            return _url;
        }

        public Map getValues() {
            return _values;
        }

        public void setValues(Map values) {
            _values = values;
        }

    }

Now that you have your model class, you can create your Screenlet's View.

## Creating the Screenlet's View [](id=creating-the-screenlets-view)

Recall that a View defines a Screenlet's UI. When creating a list Screenlet's 
View, you should first define the layout to use for each row in the list. For 
example, Bookmark List Screenlet needs to display a bookmark in each row. Its 
row layout is therefore a `LinearLayout` that contains a single `TextView`. This 
`TextView` displays the bookmark's URL. Bookmark List Screenlet defines this 
layout in its 
[`res/layout/bookmark_row.xml` file](https://github.com/liferay/liferay-screens/blob/master/android/samples/listbookmarkscreenlet/src/main/res/layout/bookmark_row.xml): 

    <?xml version="1.0" encoding="utf-8"?>
    <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical">

        <TextView
            android:id="@+id/bookmark_url"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"/>

    </LinearLayout>

A list Screenlet's View must also contain an 
[Android adapter class](http://developer.android.com/guide/topics/ui/declaring-layout.html#AdapterViews) 
to fill each row with content. Your adapter class should also contain a standard 
[Android view holder](http://developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder) 
to make list scrolling smooth. To make this easier, the list Screenlet framework 
provides the 
[abstract class `BaseListAdapter`](https://github.com/liferay/liferay-screens/blob/master/android/library/src/main/java/com/liferay/mobile/screens/base/list/BaseListAdapter.java) 
that you can extend to create your own adapter class with a view holder. By 
extending this class, your adapter needs only two methods: 

- `createViewHolder`: Instantiates your view holder. 

- `fillHolder`: Fills each of your view holder's rows. 

Your view holder should also contain variables for any data each row needs to 
display. The view holder must assign these variables to the corresponding row 
layout elements, and set the appropriate data to them. 

For example, the 
[`BookmarkAdapter` class](https://github.com/liferay/liferay-screens/blob/master/android/samples/listbookmarkscreenlet/src/main/java/com/liferay/mobile/screens/listbookmark/BookmarkAdapter.java) 
defines Bookmark List Screenlet's adapter. This class's view holder is an inner 
class that extends `BaseListAdapter`'s view holder. Since Bookmark List 
Screenlet only needs to display a bookmark's URL in each row, the view holder 
only needs one variable: `_url`. The view holder's constructor assigns the 
`TextView` from `bookmark_row.xml` to this variable. The `bind` method then sets 
the URL as the `TextView`'s text. The other methods in `BookmarkAdapter` 
leverage the view holder. The `createViewHolder` method calls the view holder's 
constructor to create new `BookmarkViewHolder` instances. The `fillHolder` 
method calls the view holder's `bind` method to set the URL as the `_url` 
variable's text: 

    import android.support.annotation.NonNull;
    import android.view.View;
    import android.widget.TextView;

    import com.liferay.mobile.screens.base.list.BaseListAdapter;
    import com.liferay.mobile.screens.base.list.BaseListAdapterListener;

    public class BookmarkAdapter extends BaseListAdapter<Bookmark, BookmarkAdapter.BookmarkViewHolder> {

        public BookmarkAdapter(int layoutId, int progressLayoutId, BaseListAdapterListener listener) {
            super(layoutId, progressLayoutId, listener);
        }

        @NonNull
        @Override
        public BookmarkViewHolder createViewHolder(View view, BaseListAdapterListener listener) {
            return new BookmarkAdapter.BookmarkViewHolder(view, listener);
        }

        @Override
        protected void fillHolder(Bookmark entry, BookmarkViewHolder holder) {
            holder.bind(entry);
        }

        public class BookmarkViewHolder extends BaseListAdapter.ViewHolder {

            private final TextView _url;

            public BookmarkViewHolder(View view, BaseListAdapterListener listener) {
                super(view, listener);

                _url = (TextView) view.findViewById(R.id.bookmark_url);
            }

            public void bind(Bookmark entry) {
                _url.setText(entry.getUrl());
            }

        }

    }

Now that your adapter exists, you can create your list Screenlet's View class. 
Recall that a View class renders the Screenlet's UI, handles user interactions, 
and communicates with the Screenlet class. The list Screenlet framework's 
[`BaseListScreenletView` class](https://github.com/liferay/liferay-screens/blob/master/android/library/src/main/java/com/liferay/mobile/screens/base/list/BaseListScreenletView.java) 
provides most of this functionality for you. Your View class, however, must 
extend this class to provide your row layout ID and an instance of your adapter. 
You do this by overriding `BaseListScreenletView`'s `getItemLayoutId` and 
`createListAdapter` methods. If your View needs to support any other 
functionality, you can provide it in your View class by creating new methods or 
overriding other `BaseListScreenletView` methods. Also note that your View class 
must display your model entities in a view holder in your adapter. Your View 
class must therefore extend `BaseListScreenletView` with your model class, view 
holder, and adapter as type arguments. 

Recall that when creating a non-list Screenlet, you must create a View Model 
interface to define the methods the View class needs to control the UI. You 
don't need to do this when creating a list Screenlet. The list Screenlet 
framework provides the 
[`BaseListViewModel` interface](https://github.com/liferay/liferay-screens/blob/1.4.1/android/library/src/main/java/com/liferay/mobile/screens/base/list/view/BaseListViewModel.java) 
for you. You don't even need to implement this interface, because 
`BaseListScreenletView` does it for you. As you can see, the list Screenlet 
framework saves you a great deal of work! 

For example, 
[Bookmark List Screenlet's View class](https://github.com/liferay/liferay-screens/blob/master/android/samples/listbookmarkscreenlet/src/main/java/com/liferay/mobile/screens/listbookmark/BookmarkListView.java) 
(`BookmarkListView`) extends `BaseListScreenletView` with `Bookmark`, 
`BookmarkAdapter.BookmarkViewHolder`, and `BookmarkAdapter` as type arguments. 
The `BookmarkListView` class also overrides `createListAdapter` to return a 
`BookmarkAdapter` instance. The only other functionality that this View class 
must support is to get the layout for each row in the list. This is done by 
overriding the `getItemLayoutId` method to return the row layout 
(`bookmark_row`): 

    import android.content.Context;
    import android.util.AttributeSet;

    import com.liferay.mobile.screens.base.list.BaseListScreenletView;

    public class BookmarkListView
        extends BaseListScreenletView<Bookmark, BookmarkAdapter.BookmarkViewHolder, BookmarkAdapter> {

        public BookmarkListView(Context context) {
            super(context);
        }

        public BookmarkListView(Context context, AttributeSet attributes) {
            super(context, attributes);
        }

        public BookmarkListView(Context context, AttributeSet attributes, int defaultStyle) {
            super(context, attributes, defaultStyle);
        }

        @Override
        protected BookmarkAdapter createListAdapter(int itemLayoutId, int itemProgressLayoutId) {
            return new BookmarkAdapter(itemLayoutId, itemProgressLayoutId, this);
        }

        @Override
        protected int getItemLayoutId() {
            return R.layout.bookmark_row;
        }
    }

Lastly, you must define your View's layout XML. Although the row layout defines 
each row's layout, the list as a whole still needs a layout. This layout must 
reference the View class and define the list's components. Since a list 
Screenlet lets the user scroll through a multi-page list, its layout should 
contain an 
[Android `ProgressBar`](https://developer.android.com/reference/android/widget/ProgressBar.html) 
to show the user when it's loading more list items. The layout should also 
contain an 
[Android `RecyclerView`](https://developer.android.com/training/material/lists-cards.html) 
to ensure the list scrolls smoothly. Apart from the styling you choose, this 
layout's code is the same for all list Screenlets. For example, here's 
[Bookmark List Screenlet's layout](https://github.com/liferay/liferay-screens/blob/master/android/samples/listbookmarkscreenlet/src/main/res/layout/list_bookmarks.xml), 
`res/layout/list_bookmarks.xml`: 

    <com.liferay.mobile.screens.listbookmark.BookmarkListView
        android:id="@+id/liferay_list_screenlet"
        xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <ProgressBar
            android:id="@+id/liferay_progress"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:visibility="gone"/>

        <android.support.v7.widget.RecyclerView
            android:id="@+id/liferay_recycler_list"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:visibility="gone"/>
    </com.liferay.mobile.screens.listbookmark.BookmarkListView>

+$$$

**Warning:** The `android:id` values in your View's layout XML must exactly 
match the ones shown here. These values are hardcoded into the Screens framework 
and changing them will cause your app to crash. 

$$$

Nice work! Now you can create your list Screenlet's Interactor. 

## Creating the Screenlet's Interactor [](id=creating-the-screenlets-interactor)

Recall that Screenlets use Interactors to make server calls. Also recall from 
[the tutorial on creating Screenlets](/develop/tutorials/-/knowledge_base/7-0/creating-android-screenlets#creating-the-screenlets-Interactor-class) 
that the following components make up an Interactor: 

1. The event class: The list Screenlet framework provides 
   [the event class `BaseListEvent`](https://github.com/liferay/liferay-screens/blob/master/android/library/src/main/java/com/liferay/mobile/screens/base/list/interactor/BaseListEvent.java), 
   so you don't need to create an event class manually. 

2. The callback class: The list Screenlet framework provides 
   [the callback class `BaseListCallback`](https://github.com/liferay/liferay-screens/blob/master/android/library/src/main/java/com/liferay/mobile/screens/base/list/interactor/BaseListCallback.java), 
   so you don't need to create one manually. 

3. The listener interface: The list Screenlet framework provides two listeners: 

    - [`BaseListInteractorListener`](https://github.com/liferay/liferay-screens/blob/master/android/library/src/main/java/com/liferay/mobile/screens/base/list/interactor/BaseListInteractorListener.java): 
      Communicates results within the Screenlet. This listener is part of the 
      Screenlet implementation and isn't exposed to the app developer. 

    - [`BaseListListener`](https://github.com/liferay/liferay-screens/blob/master/android/library/src/main/java/com/liferay/mobile/screens/base/list/BaseListListener.java):
      Communicates results to the app activity or fragment that contains the 
      Screenlet. This lets the app developer respond to the Screenlet's actions.

    In most cases, you can use these listeners as-is. There's no need to extend 
    them unless your list Screenlet requires additional listener methods. 

4. The Interactor class. This class: 

    - implements the method that makes the server call
    - processes the event object that contains the call's results
    - notifies the listener of those results

    If you were creating a non-list Screenlet, you'd create the Interactor class 
    by creating and implementing an Interactor interface. Since you're creating 
    a list Screenlet, however, you can just extend 
    [the `BaseListInteractor` class](https://github.com/liferay/liferay-screens/blob/master/android/library/src/main/java/com/liferay/mobile/screens/base/list/interactor/BaseListInteractor.java) 
    the list Screenlet framework provides. 

Note that except for the Interactor class, the list Screenlet framework provides 
all these components for you. When creating a list Screenlet, you therefore only 
need to create its Interactor class. 

Your list Screenlet's Interactor class must extend the list Screenlet 
framework's `BaseListInteractor` class with your model class and 
`BaseListInteractorListener<YourModelClass>` as type arguments. Your Interactor 
class must also define the variables it needs to make the server call. For 
example, 
[Bookmark List Screenlet's Interactor class](https://github.com/liferay/liferay-screens/blob/master/android/samples/listbookmarkscreenlet/src/main/java/com/liferay/mobile/screens/listbookmark/BookmarkListInteractorImpl.java) 
(`BookmarkListInteractorImpl`) extends `BaseListInteractor` with `Bookmark` and 
`BaseListInteractorListener<Bookmark>` as type arguments. It also has variables 
for the group ID (the ID of the site containing the Bookmarks portlet) and 
folder ID (the ID of the bookmarks folder to retrieve bookmarks from): 

    public class BookmarkListInteractorImpl
        extends BaseListInteractor<Bookmark, BaseListInteractorListener<Bookmark>> {
        
        private long _groupId;
        private long _folderId;
        
        ...
        
    }

Your Interactor class must also contain the methods needed to make the server 
call and process the results. You'll do this by overriding the following 
methods: 

- `getCallback`: Creates a `BaseListCallback` callback instance that overrides 
  the `createEntity` method to create your model objects. For example, the 
  `getCallback` method in `BookmarkListInteractorImpl` extends 
  `BaseListCallback<Bookmark>` as an anonymous inner class that implements 
  `createEntity`. The `createEntity` implementation calls the `Bookmark` 
  constructor with a `Map<String, Object>` to create a `Bookmark` object from 
  each `Map`. 

        @Override
        protected BaseListCallback<Bookmark> getCallback(Pair<Integer, Integer> rowsRange, Locale locale) {
            return new BaseListCallback<Bookmark>(getTargetScreenletId(), rowsRange, locale) {
                @Override
                public Bookmark createEntity(Map<String, Object> stringObjectMap) {
                    return new Bookmark(stringObjectMap);
                }
            };
        }

- `getPageRowsRequest`: Makes the server call to get a page of entities. Note 
  that this is a page as represented in the Screenlet, not in the portlet. 
  Recall that to make a server call in any Screenlet, you must set a callback to 
  a session, use that session to create a service instance, and then use the 
  service instance method that makes your server call. When you make server 
  calls in a list Screenlet, however, you don't need to manually set a callback 
  to a session. The list Screenlet framework does this for you. For example, the 
  `getPageRowsRequest` method in `BookmarkListInteractorImpl` uses its `session` 
  argument to create a `BookmarksEntryService` instance. This service instance's 
  `getEntries` method then retrieves a page bookmarks from the specified site 
  (`_groupId`) and bookmarks folder (`_folderId`). The `startRow` and `endRow` 
  arguments define the page's beginning and end, respectively: 

        @Override
        protected void getPageRowsRequest(Session session, int startRow, int endRow, Locale locale) 
        throws Exception {
            new BookmarksEntryService(session).getEntries(_groupId, _folderId, startRow, endRow);
        }

- `getPageRowCountRequest`: Makes the server call to get the total number of 
  entities in the portlet. This method makes its server call the same way 
  `getPageRowsRequest` does. For example, `getPageRowCountRequest` in 
  `BookmarkListInteractorImpl` also uses its `session` argument to create a 
  `BookmarksEntryService` instance. This service instance's `getEntriesCount` 
  method then retrieves the number of bookmarks from the specified site 
  (`_groupId`) and bookmarks folder (`_folderId`): 

        @Override
        protected void getPageRowCountRequest(Session session) throws Exception {
            new BookmarksEntryService(session).getEntriesCount(_groupId, _folderId);
        }

The remaining code in the Interactor class supports 
[offline mode](/develop/tutorials/-/knowledge_base/7-0/architecture-of-offline-mode-in-liferay-screens). 
First, add an enumeration that contains values for each service method the 
Interactor calls. For example, `BookmarkListInteractorImpl` calls `getEntries` 
and `getEntriesCount`. Its enumeration is therefore defined as follows: 

    private enum BOOKMARK_LIST implements CachedType {
        BOOKMARK, BOOKMARK_COUNT
    }

Next, add the Interactor class's constructor. This constructor calls the 
superclass constructor and accounts for the 
[app developer's offline mode setting](/develop/tutorials/-/knowledge_base/7-0/using-offline-mode-in-android) 
via the `offlinePolicy` argument. For example, here's the constructor in 
`BookmarkListInteractorImpl`: 

    public BookmarkListInteractorImpl(int targetScreenletId, OfflinePolicy offlinePolicy) {
        super(targetScreenletId, offlinePolicy);
    }

If you don't need to support offline mode, you can use `null` or 
`OfflinePolicy.REMOTE_ONLY` in place of `offlinePolicy` in the superclass 
constructor. 

Your Interactor class must also have a `loadRows` method that can handle offline 
mode. This method should set any variables you need to make the service calls, 
and support offline mode by calling `processWithCache`. For example, 
the `loadRows` method in `BookmarkListInteractorImpl` sets the `_groupId` and 
`_folderId` variables, and calls `processWithCache`: 

    public void loadRows(int startRow, int endRow, Locale locale, long groupId, long folderId) 
        throws Exception {

            _groupId = groupId;
            _folderId = folderId;

            processWithCache(startRow, endRow, locale);
    }

When the Screenlet needs to retrieve data from the server instead of the local 
cache, this `processWithCache` call results in a call to `BaseListInteractor`'s 
`loadRows` method. This `loadRows` method makes the server call by calling your 
Interactor class's `getPageRowsRequest` and `getPageRowCountRequest` methods. 
Note that if you don't need to support offline mode, you should replace the 
`processWithCache` call in your Interactor class's `loadRows` method with a 
`super.loadRows` call that uses the same arguments as `processWithCache`. Since 
your Interactor class's superclass is `BaseListInteractor`, this calls 
`BaseListInteractor`'s `loadRows` method. For example, if Bookmark List 
Screenlet didn't support offline mode, the `processWithCache` call in its 
Interactor class's `loadRows` method could be replaced with 
`super.loadRows(startRow, endRow, locale)`. 

Because Screens stores offline mode data as JSON, your Interactor class must 
implement the `getContent` and `getElement` methods to convert your data to and 
from JSON. The `getContent` method takes your model object as its only argument. 
You can then create a `JSONObject` from your model object. The `getElement` 
method takes a Screens 
[`TableCache`](https://github.com/liferay/liferay-screens/blob/master/android/library/src/main/java/com/liferay/mobile/screens/cache/tablecache/TableCache.java) 
object that contains the data retrieved from the local cache. You can then 
create a new `JSONObject` from this data, convert it to a `Map`, and create a 
new model object from the `Map`. For example, here are the `getContent` and 
`getElement` methods for `BookmarkListInteractorImpl`: 

    @Override
    protected String getContent(Bookmark object) {
        return new JSONObject(object.getValues()).toString();
    }

    @Override
    protected Bookmark getElement(TableCache tableCache) throws JSONException {
        return new Bookmark(JSONUtil.toMap(new JSONObject(tableCache.getContent())));
    }

Lastly, implement the `storeToCache` and `cached` methods to store data to and 
recover data from the cache, respectively. These methods make use of the 
enumeration you created earlier. For example, here are the `storeToCache` and 
`cached` methods for `BookmarkListInteractorImpl`: 

    @Override
    protected void storeToCache(BaseListEvent event, Object... args) {
        storeRows(String.valueOf(_folderId), BOOKMARK_LIST.BOOKMARK,
            BOOKMARK_LIST.BOOKMARK_COUNT, _groupId, null, event);
    }

    @Override
    protected boolean cached(Object... args) throws Exception {
        final int startRow = (int) args[0];
        final int endRow = (int) args[1];
        final Locale locale = (Locale) args[2];

        return recoverRows(String.valueOf(_folderId), BOOKMARK_LIST.BOOKMARK,
            BOOKMARK_LIST.BOOKMARK_COUNT, _groupId, null, locale, startRow, endRow);
    }

Great! Your Interactor is finished. Next, you'll create a listener for your list 
Screenlet. 

## Creating the Screenlet's Listener [](id=creating-the-screenlets-listener)

Recall that listeners let the app developer respond to events that occur in your 
Screenlet. For example, an app developer using Login Screenlet in an activity 
must implement `LoginListener` in that activity to respond to login success or 
failure. When creating a list Screenlet, however, you don't have to create a 
separate listener. Developers can use your list Screenlet in an activity or 
fragment by implementing 
[the `BaseListListener` interface](https://github.com/liferay/liferay-screens/blob/master/android/library/src/main/java/com/liferay/mobile/screens/base/list/BaseListListener.java) 
with your model class as a type argument. For example, to use Bookmark List 
Screenlet, an app developer's activity declaration could look like this: 

    public class BookmarkListActivity extends AppCompatActivity 
        implements BaseListListener<Bookmark> {...

The `BaseListListener` interface defines the following methods that the app 
developer can implement in their activity or fragment: 

- `void onListPageFailed(BaseListScreenlet source, int startRow, int endRow, Exception e);`: 
  Called in response to the Screenlet's failure to retrieve entities from the 
  server. 

- `void onListPageReceived(BaseListScreenlet source, int startRow, int endRow, List<E> entries, int rowCount);`: 
  Called in response to the Screenlet's success to retrieve entities from the 
  server. 

- `void onListItemSelected(E element, View view)`: Called in response to a user 
  selection in the list. 

If these methods meet your list Screenlet's needs, then you can move on to the 
next section in this tutorial. If you want to let app developers respond to more 
actions, however, you must create your own listener that extends 
`BaseListListener` with your model class as a type argument. For example, 
Bookmark List Screenlet has 
[the example `BookmarkListListener`](https://github.com/liferay/liferay-screens/blob/master/android/samples/listbookmarkscreenlet/src/main/java/com/liferay/mobile/screens/listbookmark/BookmarkListListener.java) 
that defines an `interactorCalled()` method. By implementing this method, the 
app developer can respond when the Interactor is called: 

    public interface BookmarkListListener extends BaseListListener<Bookmark> {
        void interactorCalled();
    }

This is possible because the Screenlet class calls this method when the 
Interactor is called. Next, you'll create the Screenlet class. 

## Creating the Screenlet Class [](id=creating-the-screenlet-class)

Now you're ready to create your Screenlet class. Recall that the Screenlet class 
serves as your Screenlet's focal point. It governs the Screenlet's behavior and 
is the primary component the app developer interacts with. As with non-list 
Screenlets, you should first define any XML attributes that you want to make 
available to the app developer. You should do this in a resources XML file in 
your Android project's `res/values` folder. 

For example, Bookmark List Screenlet must make `groupId` and `folderId` 
available as attributes the developer can set in the Screenlet's XML. It also 
needs an attribute for setting the Screenlet's 
[offline policy](/develop/tutorials/-/knowledge_base/7-0/using-offline-mode-in-android). 
Bookmark List Screenlet does this in its 
[`res/values/bookmark_attrs.xml` file](https://github.com/liferay/liferay-screens/blob/master/android/samples/listbookmarkscreenlet/src/main/res/values/bookmark_attrs.xml): 

    <?xml version="1.0" encoding="utf-8"?>
        <resources>
            <declare-styleable name="BookmarkListScreenlet">
                <attr name="groupId"/>
                <attr name="folderId"/>
                <attr name="offlinePolicy"/>
            </declare-styleable>
    </resources>

As you've seen, the list Screenlet framework provides basic implementations of 
many list Screenlet components. This is also true of the Screenlet class. The 
list Screenlet framework's 
[`BaseListScreenlet` class](https://github.com/liferay/liferay-screens/blob/master/android/library/src/main/java/com/liferay/mobile/screens/base/list/BaseListScreenlet.java) 
provides much of your Screenlet class's code. This includes methods for 
pagination and other default behavior. Your Screenlet class must extend 
`BaseListScreenlet` with your model and Interactor classes as type arguments, 
and implement any functionality specific to your Screenlet. This includes 
creating instance variables for each of the XML attributes you just defined. For 
constructors, you can leverage the superclass constructors. For example, here's 
the first part of 
[Bookmark List Screenlet's Screenlet class](https://github.com/liferay/liferay-screens/blob/master/android/samples/listbookmarkscreenlet/src/main/java/com/liferay/mobile/screens/listbookmark/BookmarkListScreenlet.java): 

    public class BookmarkListScreenlet 
        extends BaseListScreenlet<Bookmark, BookmarkListInteractorImpl> {

        private long _groupId;
        private long _folderId;
        private OfflinePolicy _offlinePolicy;

        public BookmarkListScreenlet(Context context) {
            super(context);
        }

        public BookmarkListScreenlet(Context context, AttributeSet attributes) {
            super(context, attributes);
        }

        public BookmarkListScreenlet(Context context, AttributeSet attributes, int defaultStyle) {
            super(context, attributes, defaultStyle);
        }

        ...

Next, you should implement the listener methods required to support offline mode 
in your Screenlet: `loadingFromCache`, `retrievingOnline`, and `storingToCache`. 
These methods notify the listener when data is loaded from the cache, retrieved 
online, or stored to the cache, respectively. Each method retrieves a listener 
instance and then calls the listener method it shares a name with. For example, 
here are these methods for Bookmark List Screenlet: 

    @Override
    public void loadingFromCache(boolean success) {
        if (getListener() != null) {
            getListener().loadingFromCache(success);
        }
    }

    @Override
    public void retrievingOnline(boolean triedInCache, Exception e) {
        if (getListener() != null) {
            getListener().retrievingOnline(triedInCache, e);
        }
    }

    @Override
    public void storingToCache(Object object) {
        if (getListener() != null) {
            getListener().storingToCache(object);
        }
    }

Next, add the method that reads the values of the XML attributes you defined 
earlier and creates the Screenlet's View: the `createScreenletView` method. 
Recall that this method serves the same purpose in the Screenlet classes of 
non-list Screenlets. Also recall that this method retrieves the app developer's 
attribute settings with an 
[Android `TypedArray`](https://developer.android.com/reference/android/content/res/TypedArray.html), 
which it uses to set the corresponding instance variables. For example, the 
`createScreenletView` method in `BookmarkListScreenlet` assigns the `groupId`, 
`folderId`, and `offlinePolicy` attribute values to the `_groupId`, `_folderId`, 
and `_offlinePolicy` variables, respectively. The method finishes by calling 
`BaseListScreenlet`'s `createScreenletView` method 
(`super.createScreenletView`), which inflates and returns the View: 

    @Override
    protected View createScreenletView(Context context, AttributeSet attributes) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
            attributes, R.styleable.BookmarkListScreenlet, 0, 0);
        Integer offlinePolicy = typedArray.getInteger(
            R.styleable.BookmarkListScreenlet_offlinePolicy,
            OfflinePolicy.REMOTE_ONLY.ordinal());
        _offlinePolicy = OfflinePolicy.values()[offlinePolicy];
        _groupId = typedArray.getInt(R.styleable.BookmarkListScreenlet_groupId,
            (int) LiferayServerContext.getGroupId());
        _folderId = typedArray.getInt(R.styleable.BookmarkListScreenlet_folderId, 0);
        typedArray.recycle();

        return super.createScreenletView(context, attributes);
    }

Note that calling `BaseListScreenlet`'s `createScreenletView` method also gets 
you other default attributes for controlling pagination (`firstPageSize` and 
`pageSize`), loading the Screenlet automatically (`autoLoad`), and more. 

Lastly, you must add the `createInteractor` and `loadRows` methods to your 
Screenlet class. The former creates an Interactor instance. The latter initiates 
the server call by calling the Interactor's `loadRows` method. In your Screenlet 
class's `loadRows` method, you should also add any other functionality your 
Screenlet needs when it initiates the server call. For example, the `loadRows` 
method in `BookmarkListScreenlet` also retrieves a listener instance so it can 
call the listener's `interactorCalled()` method: 

    @Override
    protected void loadRows(BookmarkListInteractorImpl interactor, int startRow, 
        int endRow, Locale locale) throws Exception {

            ((BookmarkListListener) getListener()).interactorCalled();

            interactor.loadRows(startRow, endRow, locale, _groupId, _folderId);
    }

    @Override
    protected BookmarkListInteractorImpl createInteractor(String actionName) {
        return new BookmarkListInteractorImpl(getScreenletId(), _offlinePolicy);
    }

You're done! Your Screenlet is a ready-to-use component that you can use in an 
app. You can even 
[package your Screenlet](/develop/tutorials/-/knowledge_base/7-0/packaging-your-android-screenlets), 
contribute it to the Liferay Screens project, or distribute it in Maven Central 
or JCenter. 

## Using the Screenlet [](id=using-the-screenlet)

You can now use your new Screenlet 
[the same way you use any other Screenlet](/develop/tutorials/-/knowledge_base/7-0/using-screenlets-in-android-apps): 

1. Insert the Screenlet’s XML in the layout of the activity or fragment you want 
   to use the Screenlet in. Be sure to set any Screenlet attributes you need. 
   Note that you must set the `layoutId` attribute to the Screenlet's layout. 
   For example, here's Bookmark List Screenlet's XML: 

        <com.liferay.mobile.screens.listbookmark.BookmarkListScreenlet
            android:id="@+id/bookmarklist_screenlet"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            app:folderId="YOUR_FOLDER_ID"
            app:groupId="YOUR_GROUP_ID"
            app:layoutId="@layout/list_bookmarks"/>
    
    The `layoutId` setting specifies the layout defined in the Screenlet's 
    `res/layout/list_bookmarks.xml`. Also note that the values `YOUR_FOLDER_ID` 
    and `YOUR_GROUP_ID` must be replaced with values appropriate for the Liferay 
    instance the app communicates with. 

2. Implement the Screenlet’s listener in the activity or fragment class. If your 
   list Screenlet doesn't have a custom listener, then you can implement 
   `BaseListListener` with your model class as a type argument. For example: 

        public class YourActivity extends AppCompatActivity 
            implements BaseListListener<YourModelClass> {...

    If you created a custom listener for your list Screenlet, however, then your 
    activity or fragment must implement it instead. For example, recall that the 
    example Bookmark List Screenlet's listener is `BookmarkListListener`. To use 
    this Screenlet, you must therefore implement this listener in the activity 
    or fragment class: 

        public class ListBookmarksActivity extends AppCompatActivity 
            implements BookmarkListListener {...

    See the full example of this 
    [here in GitHub](https://github.com/liferay/liferay-screens/blob/master/android/samples/test-app/src/main/java/com/liferay/mobile/screens/testapp/ListBookmarksActivity.java). 

## Related Topics [](id=related-topics)

[Creating Android Screenlets](/develop/tutorials/-/knowledge_base/7-0/creating-android-screenlets)

[Architecture of Liferay Screens for Android](/develop/tutorials/-/knowledge_base/7-0/architecture-of-liferay-screens-for-android)

[Packaging Your Android Screenlets](/develop/tutorials/-/knowledge_base/7-0/packaging-your-android-screenlets)

[Using Views in Android Screenlets](/develop/tutorials/-/knowledge_base/7-0/using-views-in-android-screenlets)

[Using Screenlets in Android Apps](/develop/tutorials/-/knowledge_base/7-0/using-screenlets-in-android-apps)
