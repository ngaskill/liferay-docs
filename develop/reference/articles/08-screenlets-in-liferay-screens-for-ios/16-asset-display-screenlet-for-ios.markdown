# Asset Display Screenlet for iOS [](id=asset-display-screenlet-for-ios)

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

Asset Display Screenlet can render an `Asset` without knowing what kind of `Asset` is. Nowadays, the screenlet displays `DLFileEntry` (image, video, audio and PDF), `BlogsEntry` and `WebContent`. You can display your custom `Asset` with `AssetDisplayScreenlet` because exists a listener method for it (see [Delegates](#listener) section).

## Module [](id=module)

- None

## Themes [](id=themes)

The Default Theme uses different element to show an `Asset` depending on its type. For example, for images, the screenlet uses `UIImageView`, for blogs entry uses `UILabel`, `UserPortraitScreenlet` and others.

This screenlet renders another screenlet:

- Images: `ImageDisplayScreenlet`.
- Videos: `VideoDisplayScreenlet`.
- Audios: `AudioDisplayScreenlet`.
- PDFs: `PdfDisplayScreenlet`.
- `BlogsEntry`: `BlogsEntryDisplayScreenlet`.
- `WebContent`: `WebContentDisplayScreenlet`.

For images, videos, audios and PDFs see [BaseFileDisplayScreenlet](../base-file-display-screenlet-for-ios).

These screenlets can be used alone without `AssetDisplayScreenlet`.

![Figure 1: Asset Display Screenlet using the Default (`default`) Theme.](../../images/screens-ios-assetdisplay.png)

## Attributes [](id=attributes)

| Attribute | Data type | Explanation |
|-----------|-----------|-------------|
| `assetEntryId` | `number` | The primary key parameter for displaying the `Asset`. | 
| `className` | `string` | The class name of the `Asset` that we want to display. It's required when we are instantiating the screenlet with `className` and `classPK`. For example, if we want to display a blog entry, for `BlogsEntry` object in Liferay 7.0 version, its `className` is [`com.liferay.blogs.kernel.model.BlogsEntry`](https://github.com/liferay/liferay-portal/blob/master/portal-kernel/src/com/liferay/blogs/kernel/model/BlogsEntry.java). | 
| `classPK` | `number` | This is the asset identifier and it's unique. This attribute is used only with `className`. |
| `assetEntry` | `Asset` | You can load an `Asset` with this attribute without calling the server method. |
| `autoLoad` | `boolean` | Whether the list should automatically load when the Screenlet appears in the app's UI. The default value is `true`. |
| `offlinePolicy` | `string` | The offline mode setting. The default value is `remote-first`. See the [Offline](#offline) section for details. |


## Delegate [](id=delegate)

`AssetDisplayScreenlet` delegates some events to an object that conforms to 
the `AssetDisplayScreenletDelegate` protocol. This protocol lets you implement 
the following methods:

- `- screenlet:onAssetResponse:`: Called when the asset are received.

- `- screenlet:onAssetError:`: Called when an error occurs in the process. The `NSError` object describes the error.
   
- `- screenlet:onConfigureScreenlet:`: Called when the inner screenlet has been created successfully and we want to configure or customize it. For example:

	```
	func screenlet(screenlet: AssetDisplayScreenlet, onConfigureScreenlet, 
		childScreenlet: BaseScreenlet?, onAsset asset: Asset) {
		if childScreenlet is BlogsEntryDisplayScreenlet {
			childScreenlet?.screenletView?.backgroundColor = UIColor.grayColor()
		}
	}

- `- screenlet:onAsset:`: Called when we want to render a custom `Asset`. For example:

	```
	func screenlet(screenlet: AssetDisplayScreenlet, onAsset asset: Asset)
		-> 	UIView? {
		if let type = asset.attributes["object"]?.allKeys.first as? String {
			if type == "user" {
				let vc = self.storyboard?
					.instantiateViewControllerWithIdentifier("UserDisplay")
					as? UserDisplayViewController
						
				if let userVc = vc {
					self.addChildViewController(userVc)
					screenlet.addSubview(userVc.view)
					userVc.view.frame = screenlet.bounds
					userVc.user = User(attributes: asset.attributes)
				}
			}
		}
		return nil
	}