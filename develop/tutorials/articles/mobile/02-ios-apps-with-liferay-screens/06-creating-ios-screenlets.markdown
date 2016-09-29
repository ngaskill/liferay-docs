# Creating iOS Screenlets [](id=creating-ios-screenlets)

The built-in
[Screenlets](/develop/reference/-/knowledge_base/7-0/screenlets-in-liferay-screens-for-ios)
cover common use cases for mobile apps that use Liferay. They
authenticate users, interact with Dynamic Data Lists, view assets, and more.
What if, however, there's no Screenlet for *your* use case? No problem! You can
create your own. Extensibility is a key strength of Liferay Screens. 

This tutorial explains how to create your own Screenlets. As an example, it
references code from the sample 
[Add Bookmark Screenlet](https://github.com/liferay/liferay-screens/tree/master/ios/Samples/Bookmark), 
that saves bookmarks to Liferay's Bookmarks portlet. 

In general, you use the following steps to create Screenlets: 

1. Plan Your Screenlet: Your Screenlet's features and use cases determine where 
   you'll create it, and the Liferay remote services you'll call. 

2. Create Your Screenlet's UI (it's View): Although this tutorial presents all 
   the information you need to create a View for your Screenlet, you may first 
   want to learn the general steps for 
   [creating a View](/develop/tutorials/-/knowledge_base/7-0/creating-ios-themes). 
   For more information on View in general, see 
   [the tutorial on using Views with Screenlets](https://dev.liferay.com/develop/tutorials/-/knowledge_base/7-0/using-themes-in-ios-screenlets). 

3. Create the Screenlet's Interactor. Interactors are Screenlet components that 
   make server calls. 

4. Create the Screenlet class. The Screenlet class is the Screenlet's central 
   component. It controls the Screenlet's behavior, and is the component the app 
   developer interacts with when inserting a Screenlet. 

To understand the components that make up a Screenlet, you might want to first
analyze the
[architecture of Liferay Screens for iOS](/develop/tutorials/-/knowledge_base/7-0/architecture-of-liferay-screens-for-ios). 
Without further ado, let the Screenlet creation begin! 

## Planning Your Screenlet

Before creating your Screenlet, you must determine what it needs to do and how 
you want developers to use it. This determines where you'll create your 
Screenlet and its functionality. 

Where you should create your Screenlet depends on how you plan to use it. If you
want to reuse or redistribute it, you should create it in an empty Cocoa Touch
Framework project in Xcode. You can then use CocoaPods to publish it. The
section
[Publish Your Themes Using CocoaPods](/develop/tutorials/-/knowledge_base/7-0/creating-ios-themes#publish-your-themes-using-cocoapods)
in the tutorial
[Creating iOS Themes](/develop/tutorials/-/knowledge_base/7-0/creating-ios-themes)
explains how to publish an iOS Screenlet. Even though that section refers to
Themes, the steps for preparing Screenlets for publishing are the same. If you
don't plan to reuse or redistribute your Screenlet, create it in your app's
Xcode project. 

You must also determine your Screenlet's functionality, and what data your 
Screenlet requires. This determines the actions your Screenlet must support and 
the Liferay remote services it must call. For example, Add Bookmark Screenlet 
only needs to respond to one action: adding a bookmark to Liferay's Bookmarks 
portlet. To add a bookmark, this Screenlet must call the Liferay instance's 
`add-entry` service for `BookmarksEntry`. If you're running a Liferay instance 
locally on port 8080, 
[click here](http://localhost:8080/api/jsonws?contextName=bookmarks&signature=%2Fbookmarks.bookmarksentry%2Fadd-entry-6-groupId-folderId-name-url-description-serviceContext) 
to see this service. To add a bookmark, this service requires the following 
parameters: 

- `groupId`: The ID of the site in the Liferay instance that contains the 
  Bookmarks portlet. 

- `folderId`: The ID of the Bookmarks portlet folder that receives the new 
  bookmark. 

- `name`: The new bookmark's title. 

- `url`: The new bookmark's URL. 

- `description`: The new bookmark's description. 

- `serviceContext`: A Liferay `ServiceContext` object. 

Add Bookmark Screenlet must therefore account for each of these parameters. When 
saving a bookmark, the Screenlet asks the user to enter the bookmark's URL and 
name. The user isn't required, however, to enter any other parameters. This is 
because the app developer sets the `groupId` and `folderId` via the app's code. 
Also, the Screenlet's code automatically populates the `description` and 
`serviceContext`. 

Great! Now you're ready to create your Screenlet's Theme! 

## Creating the Screenlet's UI

In Liferay Screens for iOS, a Screenlet's UIs is called a View. Every Screenlet 
must have at least one View. A View consists of the following components: 

1. An XIB file: defines the UI components that the View presents to the end 
   user. 

2. A View class: renders the UI, handles user interactions, and communicates 
   with the Screenlet class. 

First, create a new XIB file and use Interface Builder to construct your 
Screenlet's UI. In many cases, the actions performed by the Screenlet must be 
triggered from the View. To achieve this, make sure to use a 
`restorationIdentifier` property to assign a unique ID to each UI component that 
triggers an action. This ID is the action name recovered in the Screenlet class. 
The user triggers the action by interacting with the UI component. If the action 
only changes the UI's state (that is, changes the UI component's properties), 
then you can associate that component's event to an `IBAction` method as usual. 
Actions using `restorationIdentifier` are intended for use by actions that need 
an Interactor (such as actions that make server requests or retrieve data from a 
database). 

For example, Add Bookmark Screenlet's UI needs text boxes for entering a 
bookmark's URL and title. This UI also needs a button to support the Screenlet 
action: sending the bookmark to a Liferay instance. The 
[XIB file `AddBookmarkView_default.xib`](https://github.com/liferay/liferay-screens/tree/master/ios/Samples/Bookmark/AddBookmarkScreenlet/Basic/Themes/AddBookmarkView_default.xib) 
defines this UI. Because the button triggers the Screenlet's action, it contains 
`restorationIdentifier="add-bookmark"`. 

![Figure 1: Here's the sample Add Bookmark Screenlet's XIB file rendered in Interface Builder.](../../../images/screens-ios-xcode-add-bookmark.png)

Now you must create your Screenlet's View class. This class controls the UI you 
just defined. Screens provides the default functionality required by all View 
classes in the 
[`BaseScreenletView` class](https://github.com/liferay/liferay-screens/blob/master/ios/Framework/Core/Base/BaseScreenletView.swift). 
Your View class must therefore extend `BaseScreenletView` to provide the 
functionality unique to your Screenlet's View. Name your View class according to 
[the naming convention](/develop/tutorials/-/knowledge_base/7-0/ios-best-practices#ios-naming-convention) 
in the 
[iOS best practices tutorial](/develop/tutorials/-/knowledge_base/7-0/ios-best-practices). 
To support your UI, use standard `@IBOutlet`s and `@IBAction`s to connect all 
your XIB's UI components and events to your View class. You should also 
implement getters and setters to get values from and set values to the UI 
components. Your View class should also implement any required animations or 
front-end logic. 

For example, 
[`AddBookmarkView_default`](https://github.com/liferay/liferay-screens/tree/master/ios/Samples/Bookmark/AddBookmarkScreenlet/Basic/Themes/AddBookmarkView_default.swift) 
is Add Bookmark Screenlet's View class. This class extends `BaseScreenletView` 
and contains `@IBOutlet` references to the XIB's text fields. The getters for 
these references let the View retrieve the data the user enters into the 
corresponding text field:

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

In Interface Builder, you must now specify your View class as your XIB file's 
custom class. In Add Bookmark Screenlet, for example, `AddBookmarkView_default` 
is set as the `AddBookmarkView_default.xib` file's custom class in Interface 
Builder. 

If you're using CocoaPods, make sure to explicitly specify a valid module for 
the custom class--the grayed-out *Current* default value only suggests a module. 

![Figure 2: This XIB file's custom class's module is NOT specified.](../../../images/screens-ios-theme-custom-module-wrong.png)

![Figure 3: The XIB file is bound to the custom class name, with the specified module.](../../../images/screens-ios-theme-custom-module-right.png)

Next, you'll create your Screenlet's Interactor. 

## Creating the Interactor

Create an Interactor class for each use case of your Screenlet. Each Interactor 
must extend the Liferay Screens 
[`Interactor`](https://github.com/liferay/liferay-screens/blob/master/ios/Framework/Core/Base/Interactor.swift) 
class. The name of this class should follow the 
[Naming Convention](/develop/tutorials/-/knowledge_base/7-0/ios-best-practices#ios-naming-convention) 
specified in the 
[Best Practices](/develop/tutorials/-/knowledge_base/7-0/ios-best-practices).

Interactors work synchronously, but, you can use Callbacks (delegates) or 
Connectors to run their operations in the background. Here's what you need to 
know to implement an Interactor: 

- You must receive all needed properties, along with the Screenlet in the 
  Interactor initializer.
- You must override `Interactor`'s `start` method to perform operations (e.g., 
  invoke a Liferay portal operation via a Liferay Mobile SDK service). 
- You can save a response to a property and make its value accessible. 
- You must invoke methods `callOnSuccess` or `callOnFailure` to execute closures 
  `onSuccess` or `onFailure`, respectively. 

The sample Add Bookmark Screenlet's Interactor class 
[`AddBookmarkInteractor`](https://github.com/liferay/liferay-screens/tree/master/ios/Samples/Bookmark/AddBookmarkScreenlet/Basic/Interactor/AddBookmarkInteractor.swift) 
adds a bookmark to the Liferay instance:

    import UIKit
    import LiferayScreens
    
    
    public class AddBookmarkInteractor: Interactor, LRCallback {
    	
    	public var resultBookmarkInfo: [String:AnyObject]?
    
    	var folderId: Int64
    	var title: String
    	var url: String
    
    
    	//MARK: Initializer
    
    	public init(screenlet: BaseScreenlet, folderId: Int64, title: String, url: String) {
    		self.folderId = folderId
    		self.title = title
    		self.url = url
    		super.init(screenlet: screenlet)
    	}
    
    
    	//MARK: Interactor
    
    	override public func start() -> Bool {
    		let session = SessionContext.createSessionFromCurrentSession()
    		session?.callback = self
    
    		let service = LRBookmarksEntryService_v7(session: session)
    
    		do {
    			try service.addEntryWithGroupId(LiferayServerContext.groupId,
    			                                folderId: folderId,
    			                                name: title,
    			                                url: url,
    			                                description: "Added from Liferay Screens",
    			                                serviceContext: nil)
    			
    			return true
    		}
    		catch {
    			return false
    		}
    	}
    
    
    	//MARK: LRCallback
    
    	public func onFailure(error: NSError!) {
    		self.callOnFailure(error)
    	}
    
    	public func onSuccess(result: AnyObject!) {
    		//Save result bookmark info
    		resultBookmarkInfo = (result as! [String:AnyObject])
    
    		self.callOnSuccess()
    	}
    
    }

In this example, we extend the Interactor from `LRCallback` class in order to 
make the interaction run asynchronously. This is the method provided by the 
Mobile SDK to run asyncronous services. Please refer to 
[this](/develop/tutorials/-/knowledge_base/7-0/creating-ios-screenlets-advanced#using-connector-instead-callback) 
chapter of the [advanced tutorial](/develop/tutorials/-/knowledge_base/7-0/creating-ios-screenlets-advanced) 
if you want to use Connectors instead of Callbacks.

## Creating the Screenlet Class

The Screenlet class is the central hub of a Screenlet. It contains the 
Screenlet's properties, a reference to the Screenlet's View class, methods for 
invoking Interactor operations, and more. When using a Screenlet, app developers 
primarily interact with its Screenlet class. In other words, if a Screenlet were 
to become self-aware, it would happen in its Screenlet class (though we're 
reasonably confident this won't happen). 

[Screens's `BaseScreenlet` class](https://github.com/liferay/liferay-screens/blob/master/ios/Framework/Core/Base/BaseScreenlet.swift) 
is a base Screenlet class implementation. Since `BaseScreenlet` provides most of 
a Screenlet class's required functionality, your Screenlet class should extend 
`BaseScreenlet`. This lets you focus on your Screenlet's unique functionality. 
You should name your Screenlet class according to the 
[naming conventions](/develop/tutorials/-/knowledge_base/7-0/ios-best-practices#ios-naming-convention) 
specified in the 
[iOS best practices tutorial](/develop/tutorials/-/knowledge_base/7-0/ios-best-practices). 
Your Screenlet class must also include any `@IBInspectable` properties your 
Screenlet requires, a reference to your Screenlet's View class, and a 
`createInteractor*` method for each action your Screenlet performs. Each 
`createInteractor*` method should create an instance of that Interactor and then 
call the Interactor's `onSuccess` and `onFailure` closures to define what 
happens when the server call succeeds or fails, respectively. 

For example, the 
[`AddBookmarkScreenlet` class](https://github.com/liferay/liferay-screens/blob/master/ios/Samples/Bookmark/AddBookmarkScreenlet/Basic/AddBookmarkScreenlet.swift) 
is the Screenlet class in Add Bookmark Screenlet. This class extends 
`BaseScreenlet` and contains an `@IBInspectable` varaible for the bookmark 
folder's ID (`folderId`). Since this Screenlet only needs one Interactor for 
adding a bookmark, the Screenlet class only needs one `createInteractor` method. 
This method first gets a reference to the View class 
(`AddBookmarkView_default`). In then creates an `AddBookmarkInteractor` instance 
with this Screenlet class (`self`), the `folderId`, the bookmark's title, and 
the bookmark's URL. Note that the View class reference contains the bookmark 
title and URL that the user entered into the UI. The `createInteractor` method 
then calls the Interactor's `onSuccess` closure to print a success message when 
the server call succeeds. Likewise, the Interactor's `onFailure` closure prints 
an error message when the server call fails. Note that you're not restricted to 
just printing messages here: you should use these closures to do whatever is 
best for your Screenlet. The `createInteractor` method finishes by returning the 
Interactor instance. Here's the complete `AddBookmarkScreenlet` class: 

    import UIKit
    import LiferayScreens


    public class AddBookmarkScreenlet: BaseScreenlet {

        //MARK: Inspectables

        @IBInspectable var folderId: Int64 = 0

        //MARK: BaseScreenlet

        override public func createInteractor(name name: String?, sender: AnyObject?) -> Interactor? {

            let view = self.screenletView as! AddBookmarkView_default

            let interactor = AddBookmarkInteractor(screenlet: self,
                                               folderId: folderId,
                                               title: view.title!,
                                               url: view.URL!)

            //Called when the Interactor's server call finishes succesfully
            interactor.onSuccess = {
                let bookmarkName = interactor.resultBookmarkInfo!["name"] as! String
                print("Bookmark \"\(bookmarkName)\" saved!")
            }

            //Called when the Interactor's server call fails
            interactor.onFailure = { _ in
                print("An error occurred saving the bookmark")
            }

            return interactor
        }

    }

<!-- 
The section on creating the View says this:

"To achieve this, make sure to use a `restorationIdentifier` property to assign 
a unique ID to each UI component that triggers an action. This ID is the action 
name recovered in the Screenlet class."

Where is this happening in the Screenlet class? I don't see any reference to the 
restorationIdentifier in AddBookmarkScreenlet or BaseScreenlet.
-->

For reference, the sample Add Bookmark Screenlet's final code is 
[here on GitHub](https://github.com/liferay/liferay-screens/tree/master/ios/Samples/Bookmark/AddBookmarkScreenlet/Basic/AddBookmarkScreenlet.swift). 

You're done! Your Screenlet is a ready-to-use component that you can add to your 
storyboard. You can even [package](/develop/tutorials/-/knowledge_base/7-0/creating-ios-themes#publish-your-themes-using-cocoapods) 
it to contribute to the Screens project or distribute with CocoaPods. Now you 
know how to create Screenlets for iOS.

If you want to go deeper into the development of a Screenlet, please refer to 
our 
[advanced tutorial](/develop/tutorials/-/knowledge_base/7-0/creating-ios-screenlets-advanced), 
where we teach cool things like: multiview support, event notification from your 
Screenlet, and much more! 

## Related Topics [](id=related-topics)

[Creating iOS Screenlets (Advanced)](/develop/tutorials/-/knowledge_base/7-0/creating-ios-screenlets-advanced)

[Using Screenlets in iOS Apps](/develop/tutorials/-/knowledge_base/7-0/using-screenlets-in-ios-apps)

[Architecture of Liferay Screens for iOS](/develop/tutorials/-/knowledge_base/7-0/architecture-of-liferay-screens-for-ios)

[Creating iOS Themes](/develop/tutorials/-/knowledge_base/7-0/creating-ios-themes)

[Creating Android Screenlets](/develop/tutorials/-/knowledge_base/7-0/creating-android-screenlets)
