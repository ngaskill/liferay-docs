# Screenlets in Liferay Screens for iOS [](id=screenlets-in-liferay-screens-for-ios)

Liferay Screens for iOS contains several Screenlets that you can use in your iOS 
apps. This section contains the reference documentation for each. If you're 
looking for instructions on using Screens, see the 
[Screens tutorials](/develop/tutorials/-/knowledge_base/7-0/mobile-apps-with-liferay-screens). 
The Screens tutorials contain instructions on 
[using Screenlets](/develop/tutorials/-/knowledge_base/7-0/using-screenlets-in-ios-apps) 
and 
[using Themes in Screenlets](/develop/tutorials/-/knowledge_base/7-0/using-themes-in-ios-screenlets). 
Each Screenlet reference document here lists the Screenlet's features, 
compatibility, its module (if any), available Themes, attributes, delegate 
methods, and more. The available Screenlets are listed here with links to their 
reference documents: 

- [**Login Screenlet:**](/develop/reference/-/knowledge_base/7-0/loginscreenlet-for-ios) 
  Signs users in to a Liferay instance.
  
- [**Sign Up Screenlet:**](/develop/reference/-/knowledge_base/7-0/signupscreenlet-for-ios) 
  Registers new users in a Liferay instance.
  
- [**Forgot Password Screenlet:**](/develop/reference/-/knowledge_base/7-0/forgotpasswordscreenlet-for-ios) 
  Sends emails containing a new password or password reset link to users.
  
- [**User Portrait Screenlet:**](/develop/reference/-/knowledge_base/7-0/userportraitscreenlet-for-ios) 
  Shows the user's portrait picture.
  
- [**DDL Form Screenlet:**](/develop/reference/-/knowledge_base/7-0/ddlformscreenlet-for-ios) 
  Presents dynamic forms to be filled out by users and submitted back to the server.
  
- [**DDL List Screenlet:**](/develop/reference/-/knowledge_base/7-0/ddllistscreenlet-for-ios) 
  Shows a list of records based on a pre-existing DDL in a Liferay instance.
  
- [**Asset List Screenlet:**](/develop/reference/-/knowledge_base/7-0/assetlistscreenlet-for-ios) 
  Shows a list of assets managed by 
  [Liferay's Asset Framework](/develop/tutorials/-/knowledge_base/7-0/asset-framework). 
  This includes web content, blog entries, documents, and more.
  
- [**Web Content Display Screenlet:**](/develop/reference/-/knowledge_base/7-0/webcontentdisplayscreenlet-for-ios) 
  Shows the web content's HTML or structured content. This Screenlet uses the 
  features available in 
  [Web Content Management](/discover/portal/-/knowledge_base/7-0/creating-web-content). 

- [**Web Content List Screenlet:**](/develop/reference/-/knowledge_base/7-0/web-content-list-screenlet-for-ios)
  Shows a list of web contents from a folder, usually based on a pre-existing 
  `DDMStructure`. 

- [**Gallery Screenlet:**](/develop/reference/-/knowledge_base/7-0/gallery-screenlet-for-ios) 
  Shows a list of images from a folder. This Screenlet also lets users upload 
  and delete images. 

- [**Rating Screenlet:**](/develop/reference/-/knowledge_base/7-0/rating-screenlet-for-ios) 
  Shows the rating for an asset. This Screenlet also lets the user update or 
  delete the rating. 

- [**Comment List Screenlet:**](/develop/reference/-/knowledge_base/7-0/comment-list-screenlet-for-ios) 
  Shows a list of comments for an asset. 

- [**Comment Display Screenlet:**](/develop/reference/-/knowledge_base/7-0/comment-display-screenlet-for-ios) 
  Shows a single comment for an asset. 

- [**Add Comment Screenlet:**](/develop/reference/-/knowledge_base/7-0/comment-add-screenlet-for-ios) 
  Lets the user comment on an asset. 

- [**Asset Display Screenlet:**](/develop/reference/-/knowledge_base/7-0/asset-display-screenlet-for-ios) 
  Displays an asset. Currently, this Screenlet can display `DLFileEntry` 
  (document library files), `BlogsEntry` (blogs articles), and `WebContent` (web 
  content articles). You can also use it to display custom assets. 

- [**File Display Screenlet:**](/develop/reference/-/knowledge_base/7-0/base-file-display-screenlet-for-ios) 
  Displays a `DLFileEntry` object. This screenlet cannot be used alone, but rather, it's the superclass for [`ImageDisplayScreenlet`](https://github.com/liferay/liferay-screens/blob/feature/master/ios/Framework/Core/FileDisplay/ImageDisplayScreenlet.swift), [`VideoDisplayScreenlet`](https://github.com/liferay/liferay-screens/blob/feature/master/ios/Framework/Core/FileDisplay/VideoDisplayScreenlet.swift), [`AudioDisplayScreenlet`](https://github.com/liferay/liferay-screens/blob/feature/master/ios/Framework/Core/FileDisplay/AudioDisplayScreenlet.swift) and [`PdfDisplayScreenlet`](https://github.com/liferay/liferay-screens/blob/feature/master/ios/Framework/Core/FileDisplay/PdfDisplayScreenlet.swift).
  
- [**Blogs Entry Display Screenlet:**](/develop/reference/-/knowledge_base/7-0/blogs-entry-display-screenlet-for-ios) Shows a single blog entry.