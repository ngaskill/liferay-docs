# Creating iOS Screenlets (Advanced) [](id=creating-ios-screenlets-advanced)

If you followed our previous [creating ios screenlets tutorial](/develop/reference/-/knowledge_base/7-0/creating-ios-screenlets) you'll have a nice Screenlet to save bookmarks to a Liferay's Bookmarks Folder right in your app. But, that Screenlet, does not support multiview, or notifies its progress to the user and lots of things typically implemented in all Screenlets.

This tutorial explains how to improve your Screenlets with more advanced code. As an example, it references code from the sample [Add Bookmark Screenlet Advanced](https://github.com/liferay/liferay-screens/tree/master/ios/Samples/Bookmark/AddBookmarkAdvancedScreenlet).

If you didn't follow that tutorial or don't have the code right now, you can clone the code from the sample [Add Bookmark Screenlet](https://github.com/liferay/liferay-screens/tree/develop/ios/Samples/Bookmark/AddBookmarkBasicScreenlet). 

So... are you prepared to fully master your Screenlet?

## Enable multi-view support [](id=ios-enable-multiview-support)

One of the most typical cases is when you have a set of Screenlets that you create for some project and want to reuse them in another one. In that case, your Screenlets should support multi-view functionallity if you want to reuse all possible code from them.

In order to enable multi-view in a Screenlet you need to follow this steps:

1. Create a new protocol that specifies your Screenlet’s attributes. To easily know which properties add to the protocol, go to the Screenlet's class and look for used view properties, those are the attributes of your protocol. For example, the Add Bookmark Screenlet’s interface protocol `AddBookmarkViewModel` has the associated attributes URL and title:

        import UIKit
        
        @objc protocol AddBookmarkViewModel {
        
            var URL: String? {get}
        
            var title: String? {get}
        
        }

2. Conform your Screenlet's view to the protocol you just create. Make sure all protocol properties are correctly get/set. The Add Bookmark Screenlet's view should conform with the protocol without any changes:

        import UIKit
        import LiferayScreens
        
        class AddBookmarkView_default: BaseScreenletView {
        
            @IBOutlet weak var URLTextField: UITextField?
            @IBOutlet weak var titleTextField: UITextField?
        
            var URL: String? {
                return URLTextField?.text
            }
        
            var title: String? {
                return titleTextField?.text
            }
        
        }

3. Create a `viewModel` property in your Screenlet's class to ease the process of recovering data from the view. Use that reference for retrieving view data instead of using directly the view class.
    
    For example, in the `AddBookmarkScreenlet` class, first, create the `viewModel` property:

        //Screenlet ViewModel
        var viewModel: AddBookmarkViewModel {
            return self.screenletView as! AddBookmarkViewModel
        }

    And then, use it instead of the `screenletView` in the overriden `createInteractor`:

        override public func createInteractor(name name: String?, sender: AnyObject?) -> Interactor? {
        
            let interactor = AddBookmarkInteractor(screenlet: self,
                                                   folderId: folderId,
                                                   title: viewModel.title!,
                                                   url: viewModel.URL!)
            
            //Called when interactor finish succesfully
            interactor.onSuccess = {
                let bookmarkName = interactor.resultBookmarkInfo!["name"] as! String
                print("Bookmark \"\(bookmarkName)\" saved!")
            }
            
            //Called when interactor finish with error
            interactor.onFailure = { _ in
                print("An error occurred saving the bookmark")
            }
            
            return interactor
        }

Now you could follow the [Creating iOS Themes](/develop/tutorials/-/knowledge_base/7-0/creating-ios-themes) tutorial and add a secondary theme for your Screenlet.

## Adding Another Use Case [](id=ios-adding-another-use-case)

Is also typical to have a Screenlet which has many use cases. In order to handle this situation you'll need to create one `Interactor` class for each use case and return them in the `createInteractor` method of your Screenlet's class. Remember, each action name will be given by the `restorationId` of the UI components that trigger them. Also, as specified in the [Avoid Hard Coded Elements](/develop/tutorials/-/knowledge_base/7-0/ios-best-practices#avoid-hard-coded-elements) chapter of the [Best Practices](/develop/tutorials/-/knowledge_base/7-0/ios-best-practices), you should set this `restorationId` from code by using a constant defined in your Screenlet's class.

For example, if we want to add an action to our `AddBookmarkScreenlet` for retrieving the title of an URL inserted by the user we'll have to follow these steps:

1. Create two constants in the `AddBookmarkScreenlet` class to be the name of both your Screenlet's actions:

        static let AddBookmarkAction = "add-bookmark"
        static let GetTitleAction = "get-title"

2. In your view XIB file, add the new button for getting the title:

    ![Figure 1: The sample Add Bookmark Screenlet's XIB file with the "get-title" button.](../../../images/screens-ios-xcode-add-bookmark-advanced.png)

3. Wire the two buttons with the `AddBookmarkView_default` class
 via two `@IBOutlet`s. Use the `didSet` to set their `restorationIdentifier` to their related action:

        @IBOutlet weak var addBookmarkButton: UIButton? {
            didSet {
                addBookmarkButton?.restorationIdentifier = AddBookmarkScreenlet.AddBookmarkAction
            }
        }
        @IBOutlet weak var getTitleButton: UIButton? {
            didSet {
                getTitleButton?.restorationIdentifier = AddBookmarkScreenlet.GetTitleAction
            }
        }

4. Update the `AddBookmarkViewModel` class so it allows setting the title back:
        import UIKit
        
        @objc protocol AddBookmarkViewModel {
            var URL: String? {get}
            var title: String? {set get}
        }

5. Conform your view with the updated ViewModel:

        var title: String? {
            get {
                return titleTextField?.text
            }
            set {
                self.titleTextField?.text = newValue
            }
        }

6. Create a new `Interactor` class which will handle the get-title action:

        import UIKit
        import LiferayScreens
        
        public class GetWebTitleInteractor: Interactor {
        
            public var resultTitle: String?
            
            var url: String
            
            
            //MARK: Initializer
            
            public init(screenlet: BaseScreenlet, url: String) {
                self.url = url
                super.init(screenlet: screenlet)
            }
            
            override public func start() -> Bool {
                if let URL = NSURL(string: url) {
                
                    // Use the NSURLSession class to retrieve the HTML
                    NSURLSession.sharedSession().dataTaskWithURL(URL) {
                            (data, response, error) in
                    
                        if let errorValue = error {
                            self.callOnFailure(errorValue)
                        }
                        else {
                            if let data = data, html = NSString(data: data, encoding: NSUTF8StringEncoding) {
                                self.resultTitle = self.parseTitle(html)
                            }
                    
                            self.callOnSuccess()
                        }
                    }.resume()
                    
                    return true
                }
                
                return false
            }
            
            ///Parse the title from a webpage HTML
            private func parseTitle(html: NSString) -> String {
                let range1 = html.rangeOfString("<title>")
                let range2 = html.rangeOfString("</title>")
                
                let start = range1.location + range1.length
                
                return html.substringWithRange(NSMakeRange(start, range2.location - start))
            }
    
        }

7. And finally, update your `createInteractor` method in the Screenlet's class so it returns the correct interactor in each case:
    
        override public func createInteractor(name name: String, sender: AnyObject?) -> Interactor? {
            switch name {
            case AddBookmarkScreenlet.AddBookmarkAction:
                return createAddBookmarkInteractor()
            case AddBookmarkScreenlet.GetTitleAction:
                return createGetTitleInteractor()
            default:
                return nil
            }
        }
        
        private func createAddBookmarkInteractor() -> Interactor {
            let interactor = AddBookmarkInteractor(screenlet: self,
                                                   folderId: folderId,
                                                   title: viewModel.title!,
                                                   url: viewModel.URL!)
    
            //Called when interactor finish succesfully
            interactor.onSuccess = {
                let bookmarkName = interactor.resultBookmarkInfo!["name"] as! String
                print("Bookmark \"\(bookmarkName)\" saved!")
            }
    
            //Called when interactor finish with error
            interactor.onFailure = { _ in
                print("An error occurred saving the bookmark")
            }
    
            return interactor
        }
    
        private func createGetTitleInteractor() -> Interactor {
            let interactor = GetWebTitleInteractor(screenlet: self, url: viewModel.URL!)
    
            //Called when interactor finish succesfully
            interactor.onSuccess = {
                let title = interactor.resultTitle
                self.viewModel.title = title
            }
    
            //Called when interactor finish with error
            interactor.onFailure = { _ in
                print("An error occurred retrieving the title")
            }
    
            return interactor
        }

**A final tip:** you may find that you need to perform a particular action programatically. For this, the [`BaseScreenletView`](https://github.com/liferay/liferay-screens/blob/master/ios/Framework/Core/Base/BaseScreenletView.swift) class has a set of `userAction` methods which can be called to perform an action whenever we want.

For example, imagine we want to trigger the "get-title" action whenever the user leaves the URL TextField. Since [`BaseScreenletView`](https://github.com/liferay/liferay-screens/blob/master/ios/Framework/Core/Base/BaseScreenletView.swift) becomes the delegate of all `UITextField` by default, we'll just need to add the `textFieldEndEditing` and call the `userAction` method in its implementation:

    func textFieldDidEndEditing(textField: UITextField) {
        if textField == URLTextField {
            userAction(name: AddBookmarkScreenlet.GetTitleAction)
        }
    }

## Using Connector instead of Callback [](id=using-connector-instead-callback)

As written in the [architecture of iOS Liferay Screens](/develop/tutorials/-/knowledge_base/7-0/architecture-of-liferay-screens-for-ios) a Connector is a class that can interact with local and remote data sources and Liferay instances. 

Connectors provide cache, code synchronization, data validation, request queue and allow our Interactor to abstract great part of its logic.

In order to use Connectors in our Screenlet our Interactor will need to extend from [`ServerConnectorInteractor`](https://github.com/liferay/liferay-screens/blob/master/ios/Framework/Core/Base/BaseConnectors/ServerConnectorInteractor.swift) instead of `LRCallback`. Alternatively we could use one of its subclasses: [`ServerReadConnectorInteractor`](https://github.com/liferay/liferay-screens/blob/master/ios/Framework/Core/Base/BaseConnectors/ServerReadConnectorInteractor.swift) when you are implementing a "reading" use case and [`ServerWriteConnectorInteractor`](https://github.com/liferay/liferay-screens/blob/master/ios/Framework/Core/Base/BaseConnectors/ServerWriteConnectorInteractor.swift) when the use case is to write something. Once the preceding step is completed the only methods your Interactor should override are: `createConnector` (where you will return the Connector instance) and `completedConnector` (where you can recover the result). The name of the Connector class should follow the [Naming Convention](/develop/tutorials/-/knowledge_base/7-0/ios-best-practices#ios-naming-convention) specified in the [Best Practices](/develop/tutorials/-/knowledge_base/7-0/ios-best-practices).

Let's update our `AddBookmarkInteractor` so it use a Connector instead of Callbacks:

1. First create the connector class (`AddBookmarkLiferayConnector`) and extend it from [`ServerConnector`](https://github.com/liferay/liferay-screens/blob/master/ios/Framework/Core/Base/BaseConnectors/ServerConnector.swift).

        import UIKit
        import LiferayScreens
        
        public class AddBookmarkLiferayConnector: ServerConnector {
        }

2. Add the properties needed for the service and create an initializer with those properties.

        public let folderId: Int64
        public let title: String
        public let url: String
        
        public init(folderId: Int64, title: String, url: String) {
            self.folderId = folderId
            self.title = title
            self.url = url
            super.init()
        }

3. Override the `validateData` method and insert validations for each property that need it. Use the [`ValidationError`](https://github.com/liferay/liferay-screens/blob/develop/ios/Framework/Core/Extensions/NSError%2BScreens.swift) class to encapsulate the errors:

        override public func validateData() -> ValidationError? {
            let error = super.validateData()
            
            if error == nil {
            
                if folderId <= 0 {
                    return ValidationError("Undefined folderId")
                }
                
                if title.isEmpty {
                    return ValidationError("Title cannot be empty")
                }
                
                if url.isEmpty {
                    return ValidationError("URL cannot be empty")
                }
            }
            
            return error
        }

4. Override the `doRun` method where you'll call the Bookmark service. Get the result from the service and store it in a public property. Also, you need to control empty results as well as service errors:

        public var resultBookmarkInfo: [String:AnyObject]?
        
        override public func doRun(session session: LRSession) {

            let service = LRBookmarksEntryService_v7(session: session)

            do {
                let result = try service.addEntryWithGroupId(LiferayServerContext.groupId,
                                                             folderId: folderId,
                                                             name: title,
                                                             url: url,
                                                             description: "Added from Liferay Screens",
                                                             serviceContext: nil)
    
                if let result = result as? [String: AnyObject] {
                    resultBookmarkInfo = result
                    lastError = nil
                }
                else {
                    lastError = NSError.errorWithCause(.InvalidServerResponse)
                    resultBookmarkInfo = nil
                }
            }
            catch let error as NSError {
                lastError = error
                resultBookmarkInfo = nil
            }
        
        }

5. Change the superclass of `AddBookmarkInteractor` from `LRCallback` to `ServerWriteConnectorInteractor`, and remove `LRCallback` methods.

        import UIKit
        import LiferayScreens
        
        
        public class AddBookmarkInteractor: ServerWriteConnectorInteractor {
            
            public var resultBookmarkInfo: [String:AnyObject]?
        
            public let folderId: Int64
            public let title: String
            public let url: String
        
        
            //MARK: Initializer
        
            public init(screenlet: BaseScreenlet, folderId: Int64, title: String, url: String) {
                self.folderId = folderId
                self.title = title
                self.url = url
                super.init(screenlet: screenlet)
            }
        }
        
6. Override `createConnector` and return an instance of the `AddBookmarkLiferayConnector`:

        public override func createConnector() -> ServerConnector? {
            return AddBookmarkLiferayConnector(folderId: folderId, title: title, url: url)
        }
        
7. Finally, override `completedConnector`, get the result from the connector and store it in the `resultBookmarkInfo` property of your Interactor:

        override public func completedConnector(c: ServerConnector) {
            if let addCon = (c as? AddBookmarkLiferayConnector),
                bookmarkInfo = addCon.resultBookmarkInfo {
                self.resultBookmarkInfo = bookmarkInfo
            }
        }
        
That will cover the "add bookmark" action, but... ¿what about getting the title of the urls? Since the use case is getting the data from an URL, process it and extract its title we can use another subclass of `ServerConnector`: [`HttpConnector`](https://github.com/liferay/liferay-screens/blob/master/ios/Framework/Core/Base/BaseConnectors/HttpConnector.swift). Created for that specific action, retrieving the data from an URL encapsulated in a `NSData` object. Using this Connector, our `GetWebTitleInteractor` will be like this:

    import UIKit
    import LiferayScreens
    
    public class GetWebTitleInteractor: ServerReadConnectorInteractor {
    
        public let url: String?
    
        ///Resulted title from the webpage
        public var resultTitle: String?
    
    
        //MARK: Initializer
    
        public init(screenlet: BaseScreenlet, url: String) {
            self.url = url
            super.init(screenlet: screenlet)
        }
    
    
        //MARK: ServerConnectorInteractor
    
        public override func createConnector() -> ServerConnector? {
            if let url = url, URL = NSURL(string: url) {
                return HttpConnector(url: URL)
            }
    
            return nil
        }
    
        override public func completedConnector(c: ServerConnector) {
            if let httpCon = (c as? HttpConnector), data = httpCon.resultData,
                html = NSString(data: data, encoding: NSUTF8StringEncoding) {
                self.resultTitle = parseTitle(html)
            }
        }
    
    
        //MARK: Private methods
    
        ///Parse the title from a webpage HTML
        private func parseTitle(html: NSString) -> String {
            let range1 = html.rangeOfString("<title>")
            let range2 = html.rangeOfString("</title>")
    
            let start = range1.location + range1.length
    
            return html.substringWithRange(NSMakeRange(start, range2.location - start))
        }
    
    }

## Add Screenlet Delegate [](id=add-screenlet-delegate-ios)

Screenlet delegates are created to let other classes, especially classes outside the Screenlet, respond to your Screenlet’s events.

To add a Screenlet delegate, follow this steps:

1. Define a delegate protocol extending from class [`BaseScreenletDelegate`](https://github.com/liferay/liferay-screens/blob/develop/ios/Framework/Core/Base/BaseScreenlet.swift).
2. For each action, create at least a success and a failure method. Also, return the instance of the Screenlet in all methods so it knows who we are.
3. Declare a property in our Screenlet's class of that protocol type, using the base `delegate` property.
4. Invoke appropiate delegate methods in handling each Interactor’s closures. 

Classes conforming to the delegate protocol and registered as delegates can respond to the delegated events. Note that these methods are optional. This means that the delegate class doesn't have to implement them.

**Note:** every Liferay Screenlet’s reference documentation, specifies its delegate protocols.

For example, the `AddBookmarkScreenletDelegate` will be:

    @objc public protocol AddBookmarkScreenletDelegate: BaseScreenletDelegate {
    
        optional func screenlet(screenlet: AddBookmarkScreenlet,
                                onBookmarkAdded bookmark: [String: AnyObject])
    
        optional func screenlet(screenlet: AddBookmarkScreenlet,
                                onAddBookmarkError error: NSError)
	
    }

The delegate property:

    var addBookmarkDelegate: AddBookmarkScreenletDelegate? {
        return self.delegate as? AddBookmarkScreenletDelegate
    }
    
And, finally update the `AddBookmarkInteractor` closures:

    //Called when interactor finish succesfully
    interactor.onSuccess = {
        self.addBookmarkDelegate?.screenlet?(self, onBookmarkAdded: interactor.resultBookmarkInfo)
    }
    
    //Called when interactor finish with error
    interactor.onFailure = { error in
        self.addBookmarkDelegate?.screenlet?(self, onAddBookmarkError: error)
    }

**Final tip:** the [`BaseScreenletDelegate`](https://github.com/liferay/liferay-screens/blob/develop/ios/Framework/Core/Base/BaseScreenlet.swift) has a method called `customInteractorForAction` that a developer can implement to provide an alternative Interactor for a certain action of your Screenlet.

## Using Progress Presenters

One of the most common features of an app that gets data from a server is to show some progress (or a loading view) to the end user.

For iOS Screenlets this can be easily achieved by the use of [`ProgressPresenter`](https://github.com/liferay/liferay-screens/blob/master/ios/Framework/Core/Base/ProgressPresenter.swift). There are a two `ProgressPresenter` included in the Screens library:

- [`MBProgressHUDPresenter`](https://github.com/liferay/liferay-screens/blob/master/ios/Framework/Core/Base/MBProgressHUDPresenter.swift): This presenter shows a message with a spinner in the middle of the screen.
- [`NetworkActivityIndicatorPresenter`](https://github.com/liferay/liferay-screens/blob/master/ios/Framework/Core/Base/NetworkActivityIndicatorPresenter.swift): This shows the progress using the iOS `networkActivityIndicator`. This presenter doesn't support messages.

### Using Progress Presenters

If you want to use a different progress presenter in your Screenlet's View,  you only need to acomplish two steps:

1. Override the `createProgressPresenter` method and return an instance of the desired `ProgressPresenter` in it.
2. Override the `progressMessages` var and return the desired messages as its computed value. `progressMessages` are created in the form of a dictionary with the `actionName` as the key and a [`ProgressMessages`](https://github.com/liferay/liferay-screens/blob/master/ios/Framework/Core/Base/ProgressPresenter.swift) object as the value. This `ProgressMessages` its another dictionary with the progress type as the key and the actual message as the value. There are three types, depending on the moment of the interaction: `Working`, `Failure`, and `Success`.

For example, in our `AddBookmarkScreenlet`, if you want to use the described `NetworkActivityIndicatorPresenter` you will need to override the above method and return an instance of `NetworkActivityIndicatorPresenter`:

    override func createProgressPresenter() -> ProgressPresenter {
        return NetworkActivityIndicatorPresenter()
    }

Altough this presenter doesn't support messages, we still need to override the `progressMessages` property. This is because the Screenlet checks the presence of a message when it has to tells the Presenter to show progress for an action. Then, you could use the `NoProgressMessage` constant for those action/moment combination that need to show the indicator:

    override var progressMessages: [String : ProgressMessages] {
        return [
            AddBookmarkScreenlet.AddBookmarkAction : [.Working: NoProgressMessage],
            AddBookmarkScreenlet.GetTitleAction : [.Working: NoProgressMessage],
        ]
    }

On the other hand, if you just want to change the progress messages and stick with the default Presenter (or create a new one that uses messages) you could override the `progressMessages` property like this:

    override var progressMessages: [String : ProgressMessages] {
        return [
            AddBookmarkScreenlet.AddBookmarkAction : [
                .Working: "Saving bookmark...",
                .Success: "Bookmark saved!",
                .Failure: "An error occurred saving the bookmark"
            ],
            AddBookmarkScreenlet.GetTitleAction : [
                .Working: "Getting site title...",
                .Failure: "An error occurred retrieving the title"
            ],
        ]
    }

### Creating your own Progress Presenter

Creating your own Progress Presenter is an easy step. In short, you just need a class consistent with the [`ProgressPresenter`](https://github.com/liferay/liferay-screens/blob/master/ios/Framework/Core/Base/ProgressPresenter.swift) protocol. 

So, for example, imagine we want to change the Progress Presenter used by our `AddBookmarkScreenlet` so it behaves as the default one for the Add Bookmark action and performs another type of progress for the get-title one.

First thing to do, would be create the `AddBookmarkProgressPresenter`, and extend it from the `MBProgressHUDPresenter` class:

    import UIKit
    import LiferayScreens
    
    public class AddBookmarkProgressPresenter: MBProgressHUDPresenter {
    }

We have decided that the progress for the get-title interaction would be hidding the get-title button, and showing an `UIActivityIndicatorView`. So, next step would be creating the view and linking the indicator with the swift class:

![Figure 2: The updated Add Bookmark Screenlet's XIB file with the activity indicator view.](../../../images/screens-ios-xcode-add-bookmark-advanced-progress.png)

    @IBOutlet weak var activityIndicatorView: UIActivityIndicatorView?

Now, let's implement our Progress Presenter class. First, our presenter should receive the views it needs to work: the button and the activity indicator:

    let button: UIButton?
        
    let activityIndicator: UIActivityIndicatorView?
        
    public init(button: UIButton?, activityIndicator: UIActivityIndicatorView?) {
        self.button = button
        self.activityIndicator = activityIndicator
        super.init()
    }

Next override the `showHUDInView` and the `hideHUDFromView` methods. Both of them will have to change the views accordingly:

    public override func showHUDInView(view: UIView, message: String?, forInteractor interactor: Interactor) {
        guard interactor is GetWebTitleInteractor else {
            return super.showHUDInView(view, message: message, forInteractor: interactor)
        }
        
        button?.hidden = true
        activityIndicator?.startAnimating()
    }
        
    public override func hideHUDFromView(view: UIView?, message: String?, forInteractor interactor: Interactor, withError error: NSError?) {
        guard interactor is GetWebTitleInteractor else {
            return super.hideHUDFromView(view, message: message, forInteractor: interactor, withError: error)
        }
        
        activityIndicator?.stopAnimating()
        button?.hidden = false
    }

And finally, override the `createProgressPresenter` method in the Screenlet's view:

    override func createProgressPresenter() -> ProgressPresenter {
        return AddBookmarkProgressPresenter(button: getTitleButton, activityIndicator: activityIndicatorView)
    }

And now, your Screenlet can notify users of its progress!

## Creating the Model Class [](id=creating-the-model-class)

Each entity retrieved from Liferay typically returns from the server as 
`[String:AnyObject]`, where `String` is the matching Liferay entity's attribute, 
and `AnyObject` is the attribute's value. To work conveniently with these 
results in your Screenlet, you must create a model class that converts each 
entity into an object that represents the corresponding Liferay entity.

Liferay Screens already provides [this Models](https://github.com/liferay/liferay-screens/tree/master/ios/Framework/Core/Models).

For example, to represent bookmarks in Bookmark List Screenlet, you must create a 
class that creates a `Bookmark` objects for each `[String:AnyObject]` that comes 
back from the server. Create this class now: 

    @objc public class Bookmark : NSObject {

        public let attributes: [String:AnyObject]

        public var name: String {
            return attributes["name"] as! String
        }

        override public var description: String {
            return attributes["description"] as! String
        }

        public var url: String {
            return attributes["url"] as! String
        }

        public init(attributes: [String:AnyObject]) {
            self.attributes = attributes
        }

    }

This class defines computed properties to return the attribute values for each 
bookmark's name and URL.

And we just change the dictionary objects used for Bookmark objects. In the Connector:

    public var resultBookmarkInfo: Bookmark?

    ...
    
    if let result = result as? [String: AnyObject] { 
        resultBookmarkInfo = result 
        resultBookmarkInfo = Bookmark(attributes: result) 
        lastError = nil 
    }
    
    ...

In the Interactor:

    public var resultBookmark: Bookmark?
        
    override public func completedConnector(c: ServerConnector) { 
        if let addCon = (c as? AddBookmarkLiferayConnector), 
                bookmark = addCon.resultBookmarkInfo { 
            self.resultBookmark = bookmark 
        } 
    } 

And finally, in the Screenlet:

    optional func screenlet(screenlet: AddBookmarkScreenlet, 
                            onBookmarkAdded bookmark: Bookmark) 
    
    ...

    interactor.onSuccess = { 
        if let bookmark = interactor.resultBookmark { 
            self.addBookmarkDelegate?.screenlet?(self, onBookmarkAdded: bookmark) 
        } 
    }
    
    ...

And this is it! Your screenlet is now more prepared than ever to be used (and reused) in a real environment. Now more than ever you should [package](/develop/tutorials/-/knowledge_base/7-0/creating-ios-themes#publish-your-themes-using-cocoapods) it to contribute to the Screens project or distribute with CocoaPods.

## Related Topics [](id=related-topics)

[Creating iOS Screenlets](/develop/tutorials/-/knowledge_base/7-0/creating-ios-screenlets)

[Using Screenlets in iOS Apps](/develop/tutorials/-/knowledge_base/7-0/using-screenlets-in-ios-apps)

[Architecture of Liferay Screens for iOS](/develop/tutorials/-/knowledge_base/7-0/architecture-of-liferay-screens-for-ios)

[Creating iOS Themes](/develop/tutorials/-/knowledge_base/7-0/creating-ios-themes)

[Creating Android Screenlets](/develop/tutorials/-/knowledge_base/7-0/creating-android-screenlets)