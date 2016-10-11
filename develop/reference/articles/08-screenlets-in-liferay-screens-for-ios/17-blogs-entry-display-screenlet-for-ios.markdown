# Blogs Entry Display Screenlet for iOS [](id=blogs-entry-display-screenlet-for-ios)

## Requirements [](id=requirements)

- Xcode 7.2
- iOS 9 SDK
- Liferay 7.0 CE, Liferay DXP 
- Liferay Screens Compatibility Plugin 
  ([CE](http://www.liferay.com/marketplace/-/mp/application/54365664) or 
  [EE](http://www.liferay.com/marketplace/-/mp/application/54369726), 
  depending on your portal edition). This app is preinstalled in Liferay 7.0 CE 
  and Liferay DXP instances. 

## Compatibility [](id=compatibility)

- iOS 8 and above

## Features [](id=features)

Blogs Entry Display Screenlet displays a single blog entry. The blog entry could have a header image. If this image is not empty, an `ImageDisplayScreenlet` will be rendered.

## Module [](id=module)

- None

## Themes [](id=themes)

The Default Theme uses different elements to show a `BlogsEntry` such as `UILabel`, `UserPortraitScreenlet`, etc. Other Views may use a different components to show the blog entry.

![Figure 1: Asset Display Screenlet using the Default (`default`) Theme.](../../images/screens-ios-blogsentrydisplay.png)

## Attributes [](id=attributes)
- `entryId`

Or...

- `className`
- `classPK`

| Attribute | Data type | Explanation |
|-----------|-----------|-------------|
| `assetEntryId` | `number` | The primary key parameter for displaying the `Asset`. | 
| `className` | `string` | The class name of the `Asset` that we want to render. It's always the same for `BlogsEntry` object, in Liferay 7.0 version, it's [`com.liferay.blogs.kernel.model.BlogsEntry`](https://github.com/liferay/liferay-portal/blob/master/portal-kernel/src/com/liferay/blogs/kernel/model/BlogsEntry.java). |
| `classPK` | `number` | This is the asset identifier and it's unique. This attribute is used only with `className`. |
| `autoLoad` | `boolean` | Whether the list should automatically load when the Screenlet appears in the app's UI. The default value is `true`. |
| `offlinePolicy` | `string` | The offline mode setting. The default value is `remote-first`. See the [Offline](#offline) section for details. |


## Delegate [](id=delegate)

`BlogsEntryDisplayScreenlet` delegates some events to an object that conforms to 
the `BlogsEntryDisplayScreenletDelegate` protocol. This protocol lets you implement 
the following methods:

- `- screenlet:onBlogEntryResponse:`: Called when the `BlogsEntry` object is received.

- `- screenlet:onBlogEntryError:`: Called when an error occurs in the process. The `NSError` object describes the error.