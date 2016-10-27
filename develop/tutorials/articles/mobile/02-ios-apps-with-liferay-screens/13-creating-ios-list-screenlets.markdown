# Creating iOS List Screenlets [](id=creating-ios-list-screenlets)

It's very common for mobile apps to display lists of entities. Liferay Screens 
lets you display the following lists:

- Asset lists: [Asset List Screenlet](/develop/reference/-/knowledge_base/7-0/assetlistscreenlet-for-ios) 
- DDL lists: [DDL List Screenlet](/develop/reference/-/knowledge_base/7-0/ddllistscreenlet-for-ios)
- Comment lists: [Comment List Screenlet](/develop/reference/-/knowledge_base/7-0/comment-list-screenlet-for-ios)
- Web Content lists: [Web Content List Screenelt](/develop/reference/-/knowledge_base/7-0/web-content-list-screenlet-for-ios)
- Gallery: [Image Gallery Screenlet](/develop/reference/-/knowledge_base/7-0/gallery-screenlet-for-ios)

For your app to display a list of other entities from a Liferay 
instance, however, you must create your own list Screenlet. You can create this 
Screenlet to display standard Liferay entities such as `User`, or custom 
entities that belong to custom Liferay plugins. 

This tutorial shows you how to create your own list Screenlet. As an example, 
you'll create a Screenlet that displays a list of bookmarks from Liferay's 
Bookmarks portlet--Bookmark List Screenlet. You can find the finished 
Screenlet's code 
[here in GitHub](https://github.com/liferay/liferay-screens/tree/master/ios/Samples/Bookmark/BookmarkListScreenlet). 

Note that this tutorial doesn't explain the general Screenlet concepts and 
components in detail. Focus is instead placed on creating a Screenlet that 
displays lists of entities. Before beginning, you should therefore perform both
the [basic](/develop/tutorials/-/knowledge_base/7-0/creating-ios-screenlets) and [advance](/develop/tutorials/-/knowledge_base/7-0/creating-ios-screenlets-advanced) tutorials. 

You'll create the list Screenlet by following these steps:

1. Creating the Screenlet's View.

2. Creating the Screenlet's Connector.

3. Creating the Screenlet's Interactor.

4. Creating the Screenlet class.

First though, you should understand how pagination works with list Screenlets. 

## Pagination [](id=pagination)

To ensure that users can scroll smoothly through large lists of items, list 
Screenlets support 
[fluent pagination](http://www.iosnomad.com/blog/2014/4/21/fluent-pagination).

Liferay Screens gives you some tools to implement fluent pagination with 
configurable page size, as described in the above link. All list Screenlets in
Screens use this approach.

Now you're ready to start creating your list Screenlet! 

## Creating the View [](id=creating-the-view)

Recall that each Screenlet requires a View to serve as its UI. In Xcode, first 
create a new XIB file called `BookmarkListView_default.xib`. Use Interface 
Builder to construct your Screenlet's UI in this file. Since the Screenlet must 
show a list of items, you should add `UITableView` to this XIB. For example, 
[Bookmark List Screenlet's XIB file](https://github.com/liferay/liferay-screens/blob/master/ios/Samples/Bookmark/BookmarkListScreenlet/Themes/Default/BookmarkListView_default.xib) 
uses a simple `UITableView` to show the list of bookmarks. The name of this XIB should follow the [Naming Convention](/develop/tutorials/-/knowledge_base/7-0/ios-best-practices#ios-naming-convention) specified in the [Best Practices](/develop/tutorials/-/knowledge_base/7-0/ios-best-practices).

Now create a new View class with a name that matches that of the XIB file's 
prefix. The name of this class should also follow the [Naming Convention](/develop/tutorials/-/knowledge_base/7-0/ios-best-practices#ios-naming-convention) specified in the [Best Practices](/develop/tutorials/-/knowledge_base/7-0/ios-best-practices).

Since the XIB uses `UITableView`, your View class must extend `BaseListTableView`. For example, this is Bookmark List Screenlet's View class declaration: 

    public class BookmarkListView_default: BaseListTableView {...

In Interface Builder, set this new class as your XIB's Custom Class, and assign 
the `tableView` outlet to your `UITableView` component. 

Next, you must override the methods in the View class that fill the table cells' 
contents. There are two methods for this, depending on the type of cell being 
filled: 

- **Normal cells:** the cells that show the entities. These cells typically use 
  `UILabel`, `UIImage`, or any other UI component to show the entity's 
  attributes. Override the `doFillLoadedCell` method to fill this type of cell. 
- **Progress cell:** the cell that show the entities that are not already loaded. Override the doFillInProgressCell method to fill this type of cell.

In Bookmark List Screenlet, you must override `doFillLoadedCell` to set each 
cell's `textLabel` to a bookmark's name. 
Add this method in the `BookmarkListView_default` class now: 

    override public func doFillLoadedCell(row row: Int, cell: UITableViewCell, object: AnyObject) {
        let bookmark = object as! Bookmark
    
        cell.textLabel?.text = bookmark.name
    }

Now you'll override Bookmark List Screenlet's `doFillInProgressCell` method. 
There's no need to do anything fancy here to indicate that content is loading. 
Simply set the cell's `textLabel` to the string `"Loading..."`:

    override public func doFillInProgressCell(row row: Int, cell: UITableViewCell) {
        cell.textLabel?.text = "Loading..."
    }

Now that your View is finished, you can create the Connector. 

## Creating the Connector [](id=creating-the-connector)

Recall that a Screenlet's Connector makes the call that retrieves data from the 
server. To make a Connector for a list Screenlet, your Connector 
class must extend `PaginationLiferayConnector`. Your Connector class should also 
contain any properties it requires to retrieve data. For example, the Bookmark 
List Screenlet must retrieve bookmarks from a Bookmarks portlet in a specific 
site. It must also retrieve bookmarks from a specific folder within that 
Bookmarks portlet. The Screenlet's Connector class must therefore contain 
properties for the `groupId` (site ID) and `folderId` (Bookmarks folder ID), and 
an initializer that sets them. Create this class now as follows:

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
    
    }

Next, override the `validateData` method and insert validations for each property that need it. Use the [`ValidationError`](https://github.com/liferay/liferay-screens/blob/develop/ios/Framework/Core/Extensions/NSError%2BScreens.swift) class to encapsulate the errors:

    override public func validateData() -> ValidationError? {
        let error = super.validateData()
            
        if error == nil {
            if folderId <= 0 {
                return ValidationError("Undefined folderId")
            }
        }
            
        return error
    }
    
Finally, you must override two methods in the Connector class: one to retrieve the 
page's rows, and the other to retrieve the row count:

    public override func doAddPageRowsServiceCall(session session: LRBatchSession, startRow: Int, endRow: Int, obc: LRJSONObjectWrapper?) {
        // Write the Liferay Mobile SDK service call to retrieve records from 
        // startRow and endRow.
    }

    override public func doAddRowCountServiceCall(session session: LRBatchSession) {
        // Write the Liferay Mobile SDK service call to retrieve the row count.
    }

In Bookmark List Screenlet, add these methods to the Connector class as follows: 

    public override func doAddPageRowsServiceCall(session session: LRBatchSession, startRow: Int, endRow: Int, obc: LRJSONObjectWrapper?) {
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

Recall that Screenlet Interactors respond to user actions. In list Screenlets, 
loading entities is usually the only action a user can take.

The Interactor class of a list Screenlet that implements fluent pagination 
must extend `BaseListPageLoadInteractor`. Your Interactor class must also 
contain any properties required for the Screenlet to function, and an 
initializer that sets them. Note that this initializer takes `BaseListScreenlet` 
as an argument. In a couple of steps, you'll create your Screenlet class to 
extend `BaseListScreenlet`. 

As an example, you'll now create Bookmark List Screenlet's Interactor class. 
This class must contain the same `groupId` and `folderId` properties as the 
Connector, and an initializer that sets them. Note that this initializer uses 
`LiferayServerContext` to ensure that it contains a valid `groupId`. Create 
the Interactor class as follows:

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
    }

The Interactor class must also initiate the server request via the Connector, 
and convert the results into model objects. You'll override the 
`createListPageConnector` method to create a Connector instance that initiates the 
request. Your `createListPageConnector` method must convert the page number (the 
Interactor's `page` attribute) to the page's start and end record numbers. Use 
the `firstRowForPage` method to do this. Add the following `createConnector` 
method to the `BookmarkListPageLoadInteractor`:

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
