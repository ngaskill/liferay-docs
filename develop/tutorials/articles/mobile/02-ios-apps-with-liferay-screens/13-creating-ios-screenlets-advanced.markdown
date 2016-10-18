# Creating iOS Screenlets (Advanced) [](id=creating-ios-screenlets-advanced)

If you followed our previous 
[creating ios screenlets tutorial](/develop/reference/-/knowledge_base/7-0/creating-ios-screenlets) 
you'll have a nice Screenlet to save bookmarks to a Liferay's Bookmarks Folder 
right in your app. But, that Screenlet, does not support multiview, or notifies 
its progress to the user and lots of things typically implemented in all 
Screenlets.

This tutorial explains how to improve your Screenlets with more advanced code. 
As an example, it references code from the sample 
[Add Bookmark Screenlet Advanced](https://github.com/liferay/liferay-screens/tree/master/ios/Samples/Bookmark/AddBookmarkAdvancedScreenlet).

If you didn't follow that tutorial or don't have the code right now, you can 
clone the code from the sample 
[Add Bookmark Screenlet](https://github.com/liferay/liferay-screens/tree/develop/ios/Samples/Bookmark/AddBookmarkBasicScreenlet). 

So... are you prepared to fully master your Screenlet?

1. Supporting Multiple Themes

2. Using Multiple Interactors

3. Using a Connector Instead of a Callback

4. Creating a Custom Screenlet Delegate

5. Presenting Progress to the User

6. Modeling Custom Entities

## Supporting Multiple Themes

Using different Themes with Screenlets is one of the key advantages of Liferay 
Screens. Themes let you present Screenlets with a unique look and feel. For 
example, you may have a Screenlet in one project that you want to reuse in 
another project, and show that Screenlet in the new project's Theme. For your 
Screenlet to support this, however, there are a few extra steps you must take 
when creating your Screenlet. 

These steps center around the creation of a *View Model*. A View Model abstracts 
the Theme used to display the Screenlet, thus allowing other Themes to be used. 
For example, note that the Screenlet class's `createInteractor` method in the 
[basic Screenlet creation tutorial](/develop/tutorials/-/knowledge_base/7-0/creating-ios-screenlets) 
accesses the View class (`AddBookmarkView_default`) directly when getting a 
reference to the View class:

    let view = self.screenletView as! AddBookmarkView_default

This is all fine and well, except you can only use the Theme defined by 
`AddBookmarkView_default`! If you wanted to use a different Theme, you'd have to 
rewrite this line of code to use that Theme's View class. This isn't very 
flexible! So instead of making your Screenlet take expensive yoga classes, you 
can abstract the Theme's View class via a View Model protocol. 

Follow these steps to support multiple Themes in your Screenlet:

1. Create a *View Model* protocol that specifies your Screenlet’s attributes. 
   These are the View class properties used by your Screenlet class. For 
   example, the 
   [Screenlet class in Add Bookmark Screenlet](https://github.com/liferay/liferay-screens/blob/develop/ios/Samples/Bookmark/AddBookmarkScreenlet/Basic/AddBookmarkScreenlet.swift) 
   uses the View class properties `title` and `URL`. Add Bookmark Screenlet's 
   View Model protocol, `AddBookmarkViewModel`, therefore defines variables for 
   these properties: 

        import UIKit

        @objc protocol AddBookmarkViewModel {

            var URL: String? {get}

            var title: String? {get}

        }

2. In your View class, conform your Screenlet's View Model protocol. Make sure 
   to get/set all the protocol's properties. For example, Add Bookmark 
   Screenlet's View Class, `AddBookmarkView_default`, conforms 
   `AddBookmarkViewModel`: 

        import UIKit
        import LiferayScreens

        class AddBookmarkView_default: BaseScreenletView, AddBookmarkViewModel {
        
            @IBOutlet weak var URLTextField: UITextField?
            @IBOutlet weak var titleTextField: UITextField?
        
            var URL: String? {
                return URLTextField?.text
            }
        
            var title: String? {
                return titleTextField?.text
            }
        
        }

3. In your Screenlet class, create a View Model reference and use it instead of 
   the View class to retrieve the data your Interactor needs. By retrieving data 
   from your View Model, you abstract away the View class. You should also 
   delete any references to your View class, if they exist. For example, the 
   `viewModel` property in `AddBookmarkScreenlet` is the `AddBookmarkViewModel` 
   reference. The `AddBookmarkScreenlet` class's `createInteractor` method 
   then uses this property to get the `title` and `URL` properties. Also note 
   that there are no direct references to the View class: 

        ...
        //View Model reference
        var viewModel: AddBookmarkViewModel {
            return self.screenletView as! AddBookmarkViewModel
        }

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
        ...

That's it! Now your Screenlet is ready to use other Themes that you create for 
it. See the tutorial 
[Creating iOS Themes](/develop/tutorials/-/knowledge_base/7-0/creating-ios-themes) 
for instructions on creating a Theme. 

## Using Multiple Interactors

With multiple Interactors, it's possible for a Screenlet to support multiple 
actions. You must create an Interactor class for each of your Screenlet's 
actions. For example, if your Screenlet needs to make two server calls, then you 
need two Interactors: one for each call. Your Screenlet class's 
`createInteractor` method must return an instance of each Interactor. Also, 
recall that each action name is given by the `restorationId` of the UI 
components that trigger them. You should set this `restorationId` to a constant 
in your Screenlet. 

Use the following steps to add an action to your your Screenlet. As an example, 
an action is added to Add Bookmark Screenlet for retrieving the title of a URL 
inserted by the user: 

1. Create a constant in your Screenlet class for each of your Screenlet's 
   actions. For example, here are the constants in `AddBookmarkScreenlet`: 

        static let AddBookmarkAction = "add-bookmark"
        static let GetTitleAction = "get-title"

2. In your Theme's XIB file, add any new UI components that you need to perform 
   the action. For example, Add Boookmark Screenlet's XIB file needs a new 
   button for getting the URL's title: 

    ![Figure 1: The sample Add Bookmark Screenlet's XIB file contains a new button next to the *Title* field for retrieving the URL's title.](../../../images/screens-ios-xcode-add-bookmark-advanced.png)

3. Wire the UI components in your XIB file to your View class. In your View 
   class, you must also set each component's `restorationIdentifier` to its 
   related action. For example, the following code in the Add Bookmark 
   Screenlet's View class (`AddBookmarkView_default`) specifies an `@IBOutlet` 
   for each button. The `didSet` observer for each property sets the 
   `restorationIdentifier` to the appropriate constant you created in the first 
   step: 

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

4. Update your View class or View Model protocol to account for the new action. 
   For example, Add Bookmark Screenlet contains a View Model 
   (`AddBookmarkViewModel`) so it can support multiple Themes. This View Model 
   must allow the new action, adding a title, to set its `title` variable: 

        import UIKit

        @objc protocol AddBookmarkViewModel {
            var URL: String? {get}
            var title: String? {set get}
        }

5. If your Screenlet uses a View Model, conform your View class to the updated 
   View Model. For example, the `title` variable in Add Bookmark Screenlet's 
   View class must implement the setter from the previous step: 

        var title: String? {
            get {
                return titleTextField?.text
            }
            set {
                self.titleTextField?.text = newValue
            }
        }

6. Create a new Interactor class for the new action. To do this, use the same 
   steps detailed in the 
   [basic Screenlet creation tutorial](/develop/tutorials/-/knowledge_base/7-0/creating-ios-screenlets).
   For example, here's the Interactor class for Add Bookmark Screenlet's 
   `get-title` action: 

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

            // Parse the title from a webpage HTML
            private func parseTitle(html: NSString) -> String {
                let range1 = html.rangeOfString("<title>")
                let range2 = html.rangeOfString("</title>")

                let start = range1.location + range1.length

                return html.substringWithRange(NSMakeRange(start, range2.location - start))
            }

        }

7. Update your Screenlet class's `createInteractor` method so it returns the 
   correct Interactor for each action. For example, the `createInteractor` 
   method in `AddBookmarkScreenlet` contains a `switch` statement that returns 
   an `AddBookmarkInteractor` or `GetWebTitleInteractor` instance when the add 
   bookmark or get title action is called, respectively. Note that the 
   `createAddBookmarkInteractor()` and `createGetTitleInteractor()` methods 
   create these instances. Although you don't have to create your Interactor 
   instances in separate methods, doing so leads to cleaner code: 

       ... 
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
        ...

Great! Now you know how to use multiple Interactors in your Screenlets. The next 
section shows you how to trigger actions programmatically. 

## Triggering the Action Programmatically

<!-- Should you call the userAction methods in your View class? -->
In the preceding example, the user triggers the actions when they press buttons 
in the UI. What if you need to trigger the action programmatically? No problem! 
The 
[`BaseScreenletView` class](https://github.com/liferay/liferay-screens/blob/master/ios/Framework/Core/Base/BaseScreenletView.swift) 
contains a set of `userAction` methods that you can call in your View class to 
perform actions programmatically. For example, you may decide to trigger the 
`get-title` action whenever the user leaves the `URLTextField`. Since 
`BaseScreenletView` is the delegate for all `UITextField` objects by default, 
you can do this by implementing the 
[`textFieldDidEndEditing` method](https://developer.apple.com/reference/uikit/uitextfielddelegate/1619591-textfielddidendediting) 
to call the `userAction` method with the action name: 

    func textFieldDidEndEditing(textField: UITextField) {
        if textField == URLTextField {
            userAction(name: AddBookmarkScreenlet.GetTitleAction)
        }
    }

## Using a Connector Instead of a Callback

<!-- 
Why would you use a Connector instead of a callback? In other words, what 
advantages do Connectors bring over callbacks?
-->
In Liferay Screens, a Connector is a class that can interact with local and 
remote data sources and Liferay instances. For more information on Connectors, 
see the 
[tutorial on the architecture of Liferay Screens for iOS](/develop/tutorials/-/knowledge_base/7-0/architecture-of-liferay-screens-for-ios). 
To use a Connector, your Screenlet's Interactor must extend the 
[`ServerConnectorInteractor` class](https://github.com/liferay/liferay-screens/blob/master/ios/Framework/Core/Base/BaseConnectors/ServerConnectorInteractor.swift) 
instead of `LRCallback`. Alternatively, you could extend one of 
`ServerConnectorInteractor`'s subclasses, depending on your use case:

- [`ServerReadConnectorInteractor`:](https://github.com/liferay/liferay-screens/blob/master/ios/Framework/Core/Base/BaseConnectors/ServerReadConnectorInteractor.swift)
  Your Interactor can extend this class when implementing an action that 
  retrieves information from a server or data source. 

- [`ServerWriteConnectorInteractor`:](https://github.com/liferay/liferay-screens/blob/master/ios/Framework/Core/Base/BaseConnectors/ServerWriteConnectorInteractor.swift)
  Your Interactor can extend this class when implementing an action that writes 
  information to a server or data source. 

When extending `ServerConnectorInteractor` or one of its subclasses, your 
Interactor only needs to override the `createConnector` and `completedConnector` 
methods. These methods create a Connector instance and recover the Connector's 
result, respectively. When you create your Connector class, be sure to follow 
the 
[naming convention](/develop/tutorials/-/knowledge_base/7-0/ios-best-practices#ios-naming-convention) 
specified in the 
[best practices tutorial](/develop/tutorials/-/knowledge_base/7-0/ios-best-practices). 
<!-- 
Is there a default Connector implementation, or will developers always have to 
create one manually? For example, when using a callback, developers can use 
LRCallback without having to create a separate callback manually. Is there a 
similar Connector?
-->

As an example, this section shows you how to create and use a Connector class 
with the sample Add Bookmark Screenlet. First, you'll create the Connector class 
with the following steps: 

1. Create your Connector class by extending the 
   [`ServerConnector` class](https://github.com/liferay/liferay-screens/blob/master/ios/Framework/Core/Base/BaseConnectors/ServerConnector.swift). 
   For example, here's the class declaration for Add Bookmark Screenlet's 
   Connector class, `AddBookmarkLiferayConnector`:

        public class AddBookmarkLiferayConnector: ServerConnector {
            ...
        }

2. Add the properties needed to call the Mobile SDK service and create an 
   initializer with those properties. For example, `AddBookmarkLiferayConnector` 
   needs properties for the bookmark's folder ID, title, and URL. It also needs 
   an initializer to set those properties: 

        public let folderId: Int64
        public let title: String
        public let url: String

        public init(folderId: Int64, title: String, url: String) {
            self.folderId = folderId
            self.title = title
            self.url = url
            super.init()
        }

3. Override the `validateData` method to implement validation for each property 
   that needs it. You can use the 
   [`ValidationError` class](https://github.com/liferay/liferay-screens/blob/develop/ios/Framework/Core/Extensions/NSError%2BScreens.swift) 
   to encapsulate the errors. For example, the following `validateData` 
   implementation in `AddBookmarkLiferayConnector` ensures that `folderId` is 
   greater than `0`, and `title` and `url` contain values. This method also uses 
   `ValidationError` to represent the error: 

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

4. Override the `doRun` method to call the Mobile SDK service that you need to 
   call. In this method, retrieve the result from the service and store 
   it in a public property. Also be sure to handle errors and empty results.
   For example, the following code defines the `resultBookmarkInfo` property for 
   saving the service's results retrieved in the `doRun` method. This method's 
   service call is identical to the one in the `AddBookmarkInteractor` class's 
   `start` method from the 
   [basic Screenlet creation tutorial](/develop/tutorials/-/knowledge_base/7-0/creating-ios-screenlets). 
   The `doRun` method, however, takes the additional step of saving the result 
   to the `resultBookmarkInfo` property. Also note that this `doRun` method 
   handles errors as `NSError` objects:

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

Well done! Now you have your Connector class. To see the finished example 
`AddBookmarkLiferayConnector` class, 
[click here](https://github.com/liferay/liferay-screens/blob/master/ios/Samples/Bookmark/AddBookmarkScreenlet/Advanced/Connector/AddBookmarkLiferayConnector.swift). 

Now you must change your Interactor to use your new Connector. To do so, follow 
these steps: 

1. Change your Interactor class's superclass to `ServerConnectorInteractor` or 
   one of its subclasses, and remove any code that conforms a callback protocol. 
   For example, the `AddBookmarkInteractor` class in the 
   [basic Screenlet creation tutorial](/develop/tutorials/-/knowledge_base/7-0/creating-ios-screenlets) 
   extends `Interactor` and conforms the `LRCallback` protocol. To use a 
   connector, `AddBookmarkInteractor` should instead extend 
   `ServerWriteConnectorInteractor` because it writes data to a Liferay 
   instance. It's also unneccesary for `AddBookmarkInteractor` to conform the 
   `LRCallback` protocol. You can therefore remove the Interactor's `start`, 
   `onFailure`, and `onSuccess` methods. At this point, your Interactor should 
   contain only the properties and initializer that it requires:

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

2. Override the `createConnector` method to return an instance of your 
   Connector. For example, the `createConnector` method in 
   `AddBookmarkInteractor` returns a `AddBookmarkLiferayConnector` instance 
   created with the `folderId`, `title`, and `url` properties: 

        public override func createConnector() -> ServerConnector? {
            return AddBookmarkLiferayConnector(folderId: folderId, title: title, url: url)
        }

3. Override the `completedConnector` method to get the result from the Connector 
   and store it in the appropriate property. For example, the 
   `completedConnector` method in `AddBookmarkInteractor` first casts its 
   `ServerConnector` argument to `AddBookmarkLiferayConnector`. It then gets 
   the Connector's `resultBookmarkInfo` property and sets it to the Interactor's 
   property of the same name: 

        override public func completedConnector(c: ServerConnector) {
            if let addCon = (c as? AddBookmarkLiferayConnector),
                bookmarkInfo = addCon.resultBookmarkInfo {
                self.resultBookmarkInfo = bookmarkInfo
            }
        }

That's it! To see the complete example `AddBookmarkInteractor`, 
[click here](https://github.com/liferay/liferay-screens/blob/master/ios/Samples/Bookmark/AddBookmarkScreenlet/Advanced/Interactor/AddBookmarkInteractor.swift). 

If your Screenlet uses multiple Interactors, you can use the same steps to 
change them to use Connectors. Also, Screens provides 
[the ready-to-use `HttpConnector`](https://github.com/liferay/liferay-screens/blob/master/ios/Framework/Core/Base/BaseConnectors/HttpConnector.swift) 
for interacting with non-Liferay URL's. To use this Connector, change your 
Interactor to use `HttpConnector`. For example, the Add Bookmark Screenlet 
action that retrieves a URL's title doesn't interact with a Liferay instance; it 
retrieves the title directly from the URL. Because this action's Interactor 
class (`GetWebTitleInteractor`) retrieves data, it extends 
`ServerReadConnectorInteractor`. It also overrides the `createConnector` and 
`completedConnector` methods to use `HttpConnector`. Here's the complete 
`GetWebTitleInteractor`:

    import UIKit
    import LiferayScreens

    public class GetWebTitleInteractor: ServerReadConnectorInteractor {

        public let url: String?

        // title from the webpage
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

        // Parse the title from the webpage's HTML
        private func parseTitle(html: NSString) -> String {
            let range1 = html.rangeOfString("<title>")
            let range2 = html.rangeOfString("</title>")

            let start = range1.location + range1.length

            return html.substringWithRange(NSMakeRange(start, range2.location - start))
        }

    }

Awesome! Now you know how to create and use Connectors in your Screenlets. 

## Add a Custom Screenlet Delegate

Screenlet delegates let other classes, especially those outside your Screenlet, 
respond to your Screenlet's events. The Screenlets included with Liferay 
Screens have one or more delegates to let the app developer respond to the 
Screenlet's events. For example, 
[Login Screenlet's delegate](/develop/reference/-/knowledge_base/7-0/loginscreenlet-for-ios#delegate) 
lets the app developer implement methods that respond to login success or 
failure. 

To add a delegate to your Screenlet, follow these steps: 

1. Define a delegate protocol that extends the 
   [`BaseScreenletDelegate` class](https://github.com/liferay/liferay-screens/blob/master/ios/Framework/Core/Base/BaseScreenlet.swift).

2. Create at least a success and a failure method. Also, return the instance of 
   the Screenlet in all methods so it knows who we are.
<!-- 
Where should the success and failure method be created? 

Also, this sentence makes no sense:
"... return the instance of the Screenlet in all methods so it knows who we are"

What is "it", and who is "we"?
-->
   
3. Declare a property in our Screenlet's class of that protocol type, using the 
   base `delegate` property.
<!-- 
Is this the delegate property you refer to below? If so, what base delegate 
property are you talking about? I only see an AddBookmarkScreenletDelegate 
property.
-->

4. Invoke appropiate delegate methods in handling each Interactor’s closures. 

Classes conforming to the delegate protocol and registered as delegates can 
respond to the delegated events. Note that these methods are optional. This 
means that the delegate class doesn't have to implement them.

**Note:** every Liferay Screenlet’s reference documentation, specifies its 
delegate protocols.

For example, the `AddBookmarkScreenletDelegate` will be:

    @objc public protocol AddBookmarkScreenletDelegate: BaseScreenletDelegate {
    
        optional func screenlet(screenlet: AddBookmarkScreenlet,
                                onBookmarkAdded bookmark: [String: AnyObject])
    
        optional func screenlet(screenlet: AddBookmarkScreenlet,
                                onAddBookmarkError error: NSError)
	
    }

The delegate property:
<!-- Where is this done? In the Screenlet class? -->

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

**Final tip:** the [`BaseScreenletDelegate`](https://github.com/liferay/liferay-screens/blob/develop/ios/Framework/Core/Base/BaseScreenlet.swift) 
has a method called `customInteractorForAction` that a developer can implement 
to provide an alternative Interactor for a certain action of your Screenlet.

## Using and Creating Progress Presenters

Displaying progress is a common feature of apps that retrieve data from a 
server. For example, you've likely seen the spinners in iOS apps that let you 
know the app is performing some kind of work before showing you the results. For 
more information, see 
[the iOS Human Interface Guidelines article on Progress Indicators](https://developer.apple.com/ios/human-interface-guidelines/ui-controls/progress-indicators/). 

You can display these in Screenlets by using classes that conform the 
[`ProgressPresenter` protocol](https://github.com/liferay/liferay-screens/blob/master/ios/Framework/Core/Base/ProgressPresenter.swift). 
Liferay Screens includes two such classes: 

- [`MBProgressHUDPresenter`](https://github.com/liferay/liferay-screens/blob/master/ios/Framework/Core/Base/MBProgressHUDPresenter.swift): 
  Shows a message with a spinner in the middle of the screen. 

- [`NetworkActivityIndicatorPresenter`](https://github.com/liferay/liferay-screens/blob/master/ios/Framework/Core/Base/NetworkActivityIndicatorPresenter.swift): 
  Shows the progress using the iOS network activity indicator. This presenter 
  doesn't support messages. 

<!-- How do you use these? -->

If you want to use a different progress presenter in your Screenlet's View, you 
only need to acomplish two steps: 

1. Override the `createProgressPresenter` method to return an instance of the 
   desired presenter. 
<!-- 
Where does the createProgressPresenter method come from, and how does it relate 
to presenters? What does it do? Seeing this method without an explanation is 
very confusing. 
-->

2. Override the `progressMessages` var and return the desired messages as its 
   computed value. `progressMessages` are created in the form of a dictionary 
   with the `actionName` as the key and a 
   [`ProgressMessages`](https://github.com/liferay/liferay-screens/blob/master/ios/Framework/Core/Base/ProgressPresenter.swift) 
   object as the value. This `ProgressMessages` its another dictionary with the 
   progress type as the key and the actual message as the value. There are three 
   types, depending on the moment of the interaction: `Working`, `Failure`, and 
   `Success`.
<!-- 
This paragraph doesn't make sense...

Where do progressMessages and actionName come from? They aren't explained 
anywhere. 

What if the presenter doesn't need to return any messages?

Is there another class named ProgressMessages, or are you still talking about 
the variable? Note that the link for ProgressMessages goes to the 
ProgressPresenter protocol. 

At the end, are you trying to say that Working, Failure, and Success are the 
progress types? And what do you mean by "the moment of interaction"?
-->

For example, in our `AddBookmarkScreenlet`, if you want to use the described 
`NetworkActivityIndicatorPresenter` you will need to override the above method 
and return an instance of `NetworkActivityIndicatorPresenter`:
<!-- 
Above, you say that the reader only needs to override createProgressPresenter if 
they want to use progress presenters other than MBProgressHUDPresenter and 
NetworkActivityIndicatorPresenter. Now you're saying that the reader needs to 
override createProgressPresenter to use NetworkActivityIndicatorPresenter. Which 
is correct? 

Also, where should this method be overridden? In the Screenlet class?
-->

    override func createProgressPresenter() -> ProgressPresenter {
        return NetworkActivityIndicatorPresenter()
    }

Although this presenter doesn't support messages, we still need to override the 
`progressMessages` property. This is because the Screenlet checks the presence 
of a message when it has to tells the Presenter to show progress for an action. 
Then, you could use the `NoProgressMessage` constant for those action/moment 
combination that need to show the indicator: 
<!-- 
Where should progressMessages be overridden?

Does NoProgressMessage tell it to display the progress indicator, but no 
message? 
-->

    override var progressMessages: [String : ProgressMessages] {
        return [
            AddBookmarkScreenlet.AddBookmarkAction : [.Working: NoProgressMessage],
            AddBookmarkScreenlet.GetTitleAction : [.Working: NoProgressMessage],
        ]
    }

On the other hand, if you just want to change the progress messages and stick 
with the default Presenter (or create a new one that uses messages) you could 
override the `progressMessages` property like this:

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

### Creating Progress Presenters

Creating your own Progress Presenter is an easy step. In short, you just need a 
class consistent with the 
[`ProgressPresenter`](https://github.com/liferay/liferay-screens/blob/master/ios/Framework/Core/Base/ProgressPresenter.swift) 
protocol. 
<!-- What do you mean by "consistent with"? -->

So, for example, imagine we want to change the Progress Presenter used by our 
`AddBookmarkScreenlet` so it behaves as the default one for the Add Bookmark 
action and performs another type of progress for the get-title one.

First thing to do, would be create the `AddBookmarkProgressPresenter`, and 
extend it from the `MBProgressHUDPresenter` class:
<!-- 
Why extend MBProgressHUDPresenter? Why not extend 
NetworkActivityIndicatorPresenter instead?
-->

    import UIKit
    import LiferayScreens
    
    public class AddBookmarkProgressPresenter: MBProgressHUDPresenter {
    }

We have decided that the progress for the get-title interaction would be hidding 
the get-title button, and showing an `UIActivityIndicatorView`. So, next step 
would be creating the view and linking the indicator with the swift class:
<!-- 
When you say "creating the view", do you mean creating an entire View like in 
the tutorial on creating themes? 

https://dev.liferay.com/develop/tutorials/-/knowledge_base/7-0/creating-ios-themes
-->

![Figure 2: The updated Add Bookmark Screenlet's XIB file with the activity indicator view.](../../../images/screens-ios-xcode-add-bookmark-advanced-progress.png)

    @IBOutlet weak var activityIndicatorView: UIActivityIndicatorView?
<!-- What file do you add this code to? -->

Now, let's implement our Progress Presenter class. First, our presenter should 
receive the views it needs to work: the button and the activity indicator:

    let button: UIButton?

    let activityIndicator: UIActivityIndicatorView?

    public init(button: UIButton?, activityIndicator: UIActivityIndicatorView?) {
        self.button = button
        self.activityIndicator = activityIndicator
        super.init()
    }

Next override the `showHUDInView` and the `hideHUDFromView` methods. Both of 
them will have to change the views accordingly:
<!-- 
What do you mean by "change the views accordingly"? These methods need to be 
explained. 
-->

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

And finally, override the `createProgressPresenter` method in the Screenlet's 
view:
<!-- So this method gets overridden in the View class? -->

    override func createProgressPresenter() -> ProgressPresenter {
        return AddBookmarkProgressPresenter(button: getTitleButton, activityIndicator: activityIndicatorView)
    }

And now, your Screenlet can notify users of its progress! 

## Creating the Model Class

Liferay Screens typically receives entities from a Liferay instance as 
`[String:AnyObject]`, where `String` is the entity's attribute and `AnyObject` 
is the attribute's value. Although you can use these dictionary objects 
throughout your Screenlet, it's often easier to create a model class that 
converts each into an object that represents the corresponding Liferay entity. 
This is especially convenient for entities that are composed of many 
attribute-value pairs. Note that Liferay Screens already provides several model 
classes that you can use. 
[Click here](https://github.com/liferay/liferay-screens/tree/master/ios/Framework/Core/Models) 
to see them. 

At this point, you might be saying, "Ugh! I have complex entities and Screens 
doesn't provide a model class for them! I'm just going to give up and watch 
football." Fret not! Although we'd never come between you and football, creating 
your own model class is straightforward. For example, the sample Add Bookmark 
Screenlet retrieves bookmarks from a Liferay instance's Bookmarks portlet. Since 
each bookmark comes back from the server as `[String:AnyObject]`, the Screenlet 
needs to convert it into an object that represents bookmarks. It does so with 
its `Bookmark` model class. This class extends `NSObject` and defines computed 
properties that return the attribute values for each bookmark's name and URL: 

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

Now that your model class exists, you can use your model objects throughout your 
Screenlet. For example, Add Bookmark Screenlet's Connector, Interactor, 
delegate, and Screenlet class all need to communicate `Bookmark` results. 

Since the `[String: AnyObject]` results initially come in to the Connector, this 
is where the `Bookmark` objects are created. The following code in 
`AddBookmarkLiferayConnector` is responsible for this. Note that this is only a 
snippet. 
[Click here to see the complete `AddBookmarkLiferayConnector`](https://github.com/liferay/liferay-screens/blob/master/ios/Samples/Bookmark/AddBookmarkScreenlet/Advanced/Connector/AddBookmarkLiferayConnector.swift): 

    ...
    // Public property for the results
    public var resultBookmarkInfo: Bookmark?

    ...
    // Creates the Bookmark objects from the results inside the doRun method
    if let result = result as? [String: AnyObject] {  
        resultBookmarkInfo = Bookmark(attributes: result) 
        lastError = nil 
    }

    ...

The Interactor processes the Connector's results, so it must also handle 
`Bookmark` objects. The following code in `AddBookmarkInteractor` does this. 
[Click here to see the complete `AddBookmarkInteractor`](https://github.com/liferay/liferay-screens/blob/master/ios/Samples/Bookmark/AddBookmarkScreenlet/Advanced/Interactor/AddBookmarkInteractor.swift): 

    ...
    // Public property for the results
    public var resultBookmark: Bookmark?

    ...

    // The completedConnector method gets the results from the Connector
    override public func completedConnector(c: ServerConnector) { 
        if let addCon = (c as? AddBookmarkLiferayConnector), 
                bookmark = addCon.resultBookmarkInfo { 
            self.resultBookmark = bookmark 
        } 
    } 

Add Bookmark Screenlet's delegate also communicates `Bookmark` objects: 

    @objc public protocol AddBookmarkScreenletDelegate: BaseScreenletDelegate {

        optional func screenlet(screenlet: AddBookmarkScreenlet,
                            onBookmarkAdded bookmark: Bookmark)

        optional func screenlet(screenlet: AddBookmarkScreenlet,
                            onAddBookmarkError error: NSError)

    }

The `interactor.onSuccess` closure in `AddBookmarkScreenlet`'s 
`createAddBookmarkInteractor` method handles the `Bookmark` results via the 
delegate. The following code snippet shows this. 
[Click here to see the complete `AddBookmarkScreenlet`](https://github.com/liferay/liferay-screens/blob/master/ios/Samples/Bookmark/AddBookmarkScreenlet/Advanced/AddBookmarkScreenlet.swift): 

    ...

    interactor.onSuccess = { 
        if let bookmark = interactor.resultBookmark { 
            self.addBookmarkDelegate?.screenlet?(self, onBookmarkAdded: bookmark) 
        } 
    }

    ...

And this is it! Your screenlet is now more prepared than ever to be used (and 
reused) in a real environment. Now more than ever you should 
[package](/develop/tutorials/-/knowledge_base/7-0/creating-ios-themes#publish-your-themes-using-cocoapods) 
it to contribute to the Screens project or distribute with CocoaPods.

## Related Topics [](id=related-topics)

[Creating iOS Screenlets](/develop/tutorials/-/knowledge_base/7-0/creating-ios-screenlets)

[Using Screenlets in iOS Apps](/develop/tutorials/-/knowledge_base/7-0/using-screenlets-in-ios-apps)

[Architecture of Liferay Screens for iOS](/develop/tutorials/-/knowledge_base/7-0/architecture-of-liferay-screens-for-ios)

[Creating iOS Themes](/develop/tutorials/-/knowledge_base/7-0/creating-ios-themes)

[Creating Android Screenlets](/develop/tutorials/-/knowledge_base/7-0/creating-android-screenlets)