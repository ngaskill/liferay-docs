# Creating iOS List Screenlets [](id=creating-ios-list-screenlets)

It's very common for mobile apps to display lists of entities. Liferay Screens 
lets you display asset lists and DDL lists in your iOS app by using 
[Asset List Screenlet](/develop/reference/-/knowledge_base/7-0/assetlistscreenlet-for-ios) 
and 
[DDL List Screenlet](/develop/reference/-/knowledge_base/7-0/ddllistscreenlet-for-ios), 
respectively. Screens also includes list Screenlets for other Liferay entities. 
See the 
[Screenlet reference documentation](/develop/reference/-/knowledge_base/7-0/screenlets-in-liferay-screens-for-ios) 
for a list of all the Screenlets included with Liferay Screens. 

For your app to display a list of other entities from a Liferay instance, 
however, you must create your own list Screenlet. You can create this Screenlet 
to display standard Liferay entities such as `User`, or custom entities that 
belong to custom Liferay apps. 

This tutorial shows you how to create your own list Screenlet. As an example, 
you'll create a Screenlet that displays a list of bookmarks from Liferay's 
Bookmarks portlet--Bookmark List Screenlet. You can find the finished 
Screenlet's code 
[here in GitHub](https://github.com/liferay/liferay-screens/tree/master/ios/Samples/Bookmark/BookmarkListScreenlet). 

Note that this tutorial doesn't explain general Screenlet concepts and 
components in detail. Focus is instead placed on creating a Screenlet that 
displays lists of entities. Before beginning, you should therefore read the 
following tutorials: 

- [Creating iOS Screenlets](/develop/tutorials/-/knowledge_base/7-0/creating-ios-screenlets)
- [Supporting Multiple Themes in Your Screenlet](/develop/tutorials/-/knowledge_base/7-0/supporting-multiple-themes-in-your-screenlet)
- [Create and Use a Connector with Your Screenlet ](/develop/tutorials/-/knowledge_base/7-0/create-and-use-a-connector-with-your-screenlet)
- [Add a Screenlet Delegate](/develop/tutorials/-/knowledge_base/7-0/add-a-screenlet-delegate)
- [Creating and Using Your Screenlet's Model Class](/develop/tutorials/-/knowledge_base/7-0/creating-and-using-your-screenlets-model-class)

You'll create the list Screenlet by following these steps:

1. Creating the Screenlet's Model class.

2. Creating the Screenlet's Theme.

3. Creating the Screenlet's Connector.

4. Creating the Screenlet's Interactor.

5. Creating the Screenlet class.

First though, you should understand how pagination works with list Screenlets. 

## Pagination [](id=pagination)

To ensure that users can scroll smoothly through large lists of items, list 
Screenlets support 
[fluent pagination](http://www.iosnomad.com/blog/2014/4/21/fluent-pagination). 
Liferay Screens gives you some tools to implement fluent pagination with 
configurable page size, as described in the above link. This tutorial shows you 
how to use them. All list Screenlets in Screens use this approach. 

Now you're ready to start creating your list Screenlet! 

## Creating the Model Class

Recall that a model class transforms each `[String:AnyObject]` entity Screens 
receives into a model object representing the Liferay instance's entity. For 
instructions on creating your model class, see the tutorial 
[Creating and Using Your Screenlet's Model Class](/develop/tutorials/-/knowledge_base/7-0/creating-and-using-your-screenlets-model-class). 
The model class that tutorial uses as an example is the same one used here by 
Bookmark List Screenlet. 

Next, you'll create your Screenlet's Theme. 

## Creating the Theme [](id=creating-the-view)

Recall that each Screenlet requires a Theme to serve as its UI. In Xcode, first 
create a new XIB file for your Theme and name it according to the 
[naming conventions in the iOS best practices tutorial](ios-best-practices). 
For example, the XIB for Bookmark List Screenlet's Default Theme is 
`BookmarkListView_default.xib`. Use Interface Builder to construct your 
Screenlet's UI in your Theme's XIB. Since the Screenlet must show a list of 
items, you should add `UITableView` to this XIB. For example, 
[Bookmark List Screenlet's XIB file](https://github.com/liferay/liferay-screens/blob/master/ios/Samples/Bookmark/BookmarkListScreenlet/Themes/Default/BookmarkListView_default.xib) 
uses a `UITableView` to show the list of bookmarks. 

Now create your Theme's View class, also named according to the 
[naming conventions in the iOS best practices tutorial](ios-best-practices). 
Since the XIB uses `UITableView`, your View class must extend 
`BaseListTableView`. For example, this is Bookmark List Screenlet's View class 
declaration: 

    public class BookmarkListView_default: BaseListTableView {...

In Interface Builder, set this new class as your XIB's Custom Class, and assign 
the `tableView` outlet to your `UITableView` component. 

Next, you must override the View class methods that fill the table cells' 
contents. There are two methods for this, depending on the type of cell being 
filled: 

- **Normal cells:** the cells that show the entities. These cells typically use 
  `UILabel`, `UIImage`, or another UI component to show the entity's attributes. 
  Override the `doFillLoadedCell` method to fill this type of cell. For example, 
  Bookmark List Screenlet's View class overrides `doFillLoadedCell` to set each 
  cell's `textLabel` to a bookmark's name:

        override public func doFillLoadedCell(row row: Int, cell: UITableViewCell, object: AnyObject) {
            let bookmark = object as! Bookmark

            cell.textLabel?.text = bookmark.name
        }

- **Progress cell:** the cell shown at the bottom of the list to indicate that 
  the list is loading the next page of items. Override the 
  `doFillInProgressCell` method to fill this type of cell. For example, Bookmark 
  List Screenlet's View class overrides this method to set the cell's 
  `textLabel` to the string `"Loading..."`: 

        override public func doFillInProgressCell(row row: Int, cell: UITableViewCell) {
            cell.textLabel?.text = "Loading..."
        }

That's it! Now that your Theme is finished, you can create the Connector. 

## Creating the Connector [](id=creating-the-connector)

Recall that a Screenlet's Connector makes the server call to retrieve data. To 
support pagination, a List Screenlet's Connector class must extend 
`PaginationLiferayConnector`. The Connector class must also contain any 
properties it needs to retrieve data. For example, Bookmark List Screenlet must 
retrieve bookmarks from a Bookmarks portlet in a specific site. It must also 
retrieve bookmarks from a specific folder in that Bookmarks portlet. The 
Screenlet's Connector class must therefore contain properties for the `groupId` 
(site ID) and `folderId` (Bookmarks folder ID), and an initializer that sets 
them: 

    import UIKit
    import LiferayScreens

    public class BookmarkListPageLiferayConnector: PaginationLiferayConnector {

        public let groupId: Int64
        public let folderId: Int64

        //MARK: Initializer

        public init(startRow: Int, endRow: Int, computeRowCount: Bool, groupId: Int64, folderId: Int64) {
            self.groupId = groupId
            self.folderId = folderId

            super.init(startRow: startRow, endRow: endRow, computeRowCount: computeRowCount)
        }
        ...

Next, override the `validateData` method as described in 
[the tutorial on creating Connectors](create-and-use-a-connector-with-your-screenlet#creating-connectors). 
Note that Bookmark List Screenlet only needs to validate the `folderId`: 

    override public func validateData() -> ValidationError? {
        let error = super.validateData()

        if error == nil {
            if folderId <= 0 {
                return ValidationError("Undefined folderId")
            }
        }

        return error
    }

Lastly, you must override the following two methods in the Connector class: 

- `doAddPageRowsServiceCall`: calls the Liferay Mobile SDK service method that 
  retrieves a page of entities. The `doAddPageRowsServiceCall` method's 
  `startRow` and `endRow` arguments define the page's first and last entities, 
  respectively. Make the service call just as you would in any other Screenlet. 
  For example, the `doAddPageRowsServiceCall` method in 
  `BookmarkListPageLiferayConnector` calls `LRBookmarksEntryService_v7`'s 
  `getEntriesWithGroupId` method to retrieve a page of bookmarks from the folder 
  specified by `folderId`: 

        public override func doAddPageRowsServiceCall(session session: LRBatchSession, 
            startRow: Int, endRow: Int, obc: LRJSONObjectWrapper?) {
                let service = LRBookmarksEntryService_v7(session: session)

            do {
                try service.getEntriesWithGroupId(groupId,
                                                  folderId: folderId,
                                                  start: Int32(startRow),
                                                  end: Int32(endRow))
            }
            catch  {
                // ignore error: the method returns nil (converted to an error) because
                // the request is not actually sent
            }
        }

- `doAddRowCountServiceCall`: calls the Liferay Mobile SDK service method that 
  retrieves the total number of entities. This number is required to support 
  pagination. Make the service call just as you would in any other Screenlet. 
  For example, the `doAddRowCountServiceCall` in 
  `BookmarkListPageLiferayConnector` calls `LRBookmarksEntryService_v7`'s 
  `getEntriesCountWithGroupId` method to retrieve the total number of bookmarks 
  in the folder specified by `folderId`: 

        override public func doAddRowCountServiceCall(session session: LRBatchSession) {
            let service = LRBookmarksEntryService_v7(session: session)

            do {
                try service.getEntriesCountWithGroupId(groupId, folderId: folderId)
            }
            catch  {
                // ignore error: the method returns nil (converted to an error) because
                // the request is not actually sent
            }
        }

Now that you have your Connector class, you're ready to create the Screenlet's 
Interactor. 

## Creating the Interactor [](id=creating-the-interactor)

Recall that Screenlet Interactors implement your Screenlet's actions. In list 
Screenlets, loading entities is usually the only action a user can take. The 
Interactor class of a list Screenlet that implements fluent pagination 
must extend `BaseListPageLoadInteractor`. Your Interactor class must also 
contain any properties the Screenlet needs, and an initializer that sets them. 
This initializer must also take `BaseListScreenlet` as an argument so the 
Interactor always has a Screenlet reference. The Interactor class must also 
initiate the server request via the Connector, and convert the results into 
model objects. 

For example, Bookmark List Screenlet's Interactor class must contain the same 
`groupId` and `folderId` properties as the Connector, and an initializer that 
sets them. Note that this initializer also takes a `BaseListScreenlet` argument: 
<!-- What does computeRowCount do? -->

    public class BookmarkListPageLoadInteractor : BaseListPageLoadInteractor {

        private let groupId: Int64
        private let folderId: Int64

        init(screenlet: BaseListScreenlet,
            page: Int,
            computeRowCount: Bool,
            groupId: Int64,
            folderId: Int64) {

                self.groupId = (groupId != 0) ? groupId : LiferayServerContext.groupId
                self.folderId = folderId

                super.init(screenlet: screenlet, page: page, computeRowCount: computeRowCount)
        }
        ...

To create a Connector instance that initiates the request, override the 
`createListPageConnector` method. This method must first get a reference to the 
Screenlet via the `screenlet` property. In your Connector's initializer, use 
`screenlet.firstRowForPage` to convert the page number (`page`) to the page's 
start and end indices. You must also pass your initializer any other properties 
it needs, like the `groupId`. For example, `BookmarkListPageLoadInteractor`'s 
`createListPageConnector` method does this to create a 
`BookmarkListPageLiferayConnector` instance: 

    public override func createListPageConnector() -> PaginationLiferayConnector {
        let screenlet = self.screenlet as! BaseListScreenlet

        return BookmarkListPageLiferayConnector(
            startRow: screenlet.firstRowForPage(self.page),
            endRow: screenlet.firstRowForPage(self.page + 1),
            computeRowCount: self.computeRowCount,
            groupId: groupId,
            folderId: folderId)
    }

Similarly, you'll override the `convertResult` method in the Interactor class to 
convert each result into a model object. The Screenlet calls this method once 
for each entity retrieved from the server, with an entity as the method's only 
argument. For Bookmark List Screenlet, if you followed the [advance tutorial](/develop/tutorials/-/knowledge_base/7-0/creating-ios-screenlets-advanced)
you should have a `Bookmark` model that you can use in this case. You can
therefore override this method to create a `Bookmark` instance for each entity:

    override public func convertResult(serverResult: [String:AnyObject]) -> AnyObject {
        return Bookmark(attributes: serverResult)
    }

There's one more thing you may want to add to your Interactor class before 
moving on: support for offline mode. To support offline mode, the Interactor 
must return a cache key unique to your Screenlet. You'll do this by overriding 
the `cacheKey` method in the Interactor class. Since this Screenlet shows 
bookmarks that correspond to a `groupId` and `folderId`, the cache key must 
include these values. Override this method now: 

    override public func cacheKey(op: PaginationLiferayConnector) -> String {
        return "\(groupId)-\(folderId)"
    }

Nice work! Next, you'll create the Screenlet class. 

## Creating the Screenlet Class [](id=creating-the-screenlet-class)

Now that your Screenlet's other components exist, you can create the Screenlet 
class. A list Screenlet's Screenlet class must extend `BaseListScreenlet`.
Your Screenlet class must also define the configuration properties required
for the Screenlet to work. You should define these as `IBInspectable` properties. 

Bookmark List Screenlet's Screenlet class requires properties for the `groupId` 
and `folderId`. If you want to support offline mode, you should also add an 
`offlinePolicy` property. Create the `BookmarkListScreenlet` class as follows: 

    public class BookmarkListScreenlet: BaseListScreenlet {

        @IBInspectable public var groupId: Int64 = 0
        @IBInspectable public var folderId: Int64 = 0
        @IBInspectable public var offlinePolicy: String? = CacheStrategyType.RemoteFirst.rawValue
        
    }

Next, override the method that creates the Interactor for a specific page. In 
Bookmark List Screenlet, this is the `createPageLoadInteractor` method. The 
Screenlet calls this method when it needs to load a page. If your Screenlet 
supports offline mode, you should also pass a `CacheStrategyType` object to
the interactor, using the value of `offlinePolicy`.

Add this method to Bookmark List Screenlet's Screenlet class as follows:

    override public func createPageLoadInteractor(
        page page: Int, 
        computeRowCount: Bool) -> BaseListPageLoadInteractor {

        let interactor = BookmarkListPageLoadInteractor(screenlet: self,
                                                        page: page,
                                                        computeRowCount: computeRowCount,
                                                        groupId: self.groupId,
                                                        folderId: self.folderId)

        interactor.cacheStrategy = CacheStrategyType(rawValue: self.offlinePolicy ?? "") ?? .RemoteFirst

        return interactor
    }

Next, you must create the delegate for your Screenlet. In order to create this
delegate, follow the steps described in [this](/develop/tutorials/-/knowledge_base/7-0/creating-ios-screenlets-advanced#add-screenlet-delegate-ios)
chapter of the [advanced tutorial](/develop/tutorials/-/knowledge_base/7-0/creating-ios-screenlets-advanced).
You will need delegate methods for success/failure and for row selection.
For example, for our `BookmarkListScreenlet`:

    @objc public protocol BookmarkListScreenletDelegate : BaseScreenletDelegate {
        
        optional func screenlet(screenlet: BookmarkListScreenlet,
                                onBookmarkListResponse bookmarks: [Bookmark])
            
        optional func screenlet(screenlet: BookmarkListScreenlet,
                                onBookmarkListError error: NSError)
            
        optional func screenlet(screenlet: BookmarkListScreenlet,
                                onBookmarkSelected bookmark: Bookmark)
        
    }

Once your delegate is created, you'll first need a reference to this delegate.
The class `BaseScreenlet`, which `BaseListScreenlet` extends, already defines
the `delegate` property to store the delegate object. Any list Screenlet
therefore has this property, and any app developer using the Screenlet can
assign an object to the property. To avoid casting this `delegate` property to `BookmarkListScreenletDelegate` every time you use it, you can add a computed
property that does this just once: 

    public var bookmarkListDelegate: BookmarkListScreenletDelegate? {
        return delegate as? BookmarkListScreenletDelegate
    }

Now you must override the `BaseListScreenlet` methods that the Screenlet calls 
to handle events. Because these events correspond to the events your delegate 
methods handle, you'll call your delegate methods in these `BaseListScreenlet` 
methods: 

- `onLoadPageResult`: Called when the Screenlet loads a page successfully. 
  Override this method to call your delegate's 
  `screenlet(_:onBookmarkListResponse:)` method. 

- `onLoadPageError`: Called when the Screenlet fails to load a page. Override 
  this method to call your delegate's `screenlet(_:onBookmarkListError:)` 
  method. 

- `onSelectedRow`: Called when the user selects an item in the list. Override 
  this method to call your delegate's `screenlet(_:onBookmarkSelected:)` method. 

Override these methods now: 

    override public func onLoadPageResult(page page: Int, rows: [AnyObject], rowCount: Int) {
        super.onLoadPageResult(page: page, rows: rows, rowCount: rowCount)

        bookmarkListDelegate?.screenlet?(self, onBookmarkListResponse: rows as! [Bookmark])
    }

    override public func onLoadPageError(page page: Int, error: NSError) {
        super.onLoadPageError(page: page, error: error)

        bookmarkListDelegate?.screenlet?(self, onBookmarkListError: error)
    }

    override public func onSelectedRow(row: AnyObject) {
        bookmarkListDelegate?.screenlet?(self, onBookmarkSelected: row as! Bookmark)
    }

Awesome! You're done! Your list Screenlet, like any other Screenlet, is a 
ready-to-use component that you can add to your storyboard. You can even
[package it](/develop/tutorials/-/knowledge_base/7-0/creating-ios-themes#publish-your-themes-using-cocoapods)
to contribute to the Liferay Screens project, or distribute it with CocoaPods.

If you want to go deeper into the development of a list Screenlet, please refer to 
our 
[advanced tutorial](/develop/tutorials/-/knowledge_base/7-0/creating-ios-list-screenlets-advanced), 
where you can learn to: create custom cells, add sorted lists, add sections and much more!

## Related Topics [](id=related-topics)

[Creating iOS List Screenlets (Advanced)](/develop/tutorials/-/knowledge_base/7-0/creating-ios-list-screenlets-advanced)

[Creating iOS Screenlets](/develop/tutorials/-/knowledge_base/7-0/creating-ios-screenlets)

[Creating iOS Screenlets (Advanced)](/develop/tutorials/-/knowledge_base/7-0/creating-ios-screenlets-advanced)

[Architecture of Liferay Screens for iOS](/develop/tutorials/-/knowledge_base/7-0/architecture-of-liferay-screens-for-ios)
