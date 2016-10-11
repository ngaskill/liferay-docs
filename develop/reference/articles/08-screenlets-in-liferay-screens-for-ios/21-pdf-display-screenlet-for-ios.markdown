# Pdf Display Screenlet for iOS [](id=pdf-display-screenlet-for-ios)

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

Pdf Display Screenlet can display a `DLFileEntry` of type pdf from a
Liferay instance. You can specify which extensions are allowed in the screenlet.

This screenlet has a base class called `FileDisplayScreenlet`. All the screenlets which works with `DLFileEntry` should extend this class although it can't be used on its own.

## Module [](id=module)

- None

## Themes [](id=themes)

- Default

![Figure 1: Pdf Display Screenlet using the Default View.](../../images/screens-ios-pdfdisplay.png)

The Default View uses an `UIWebView` for displaying the PDF file.

## Attributes [](id=attributes)

| Attribute | Data type | Explanation |
|-----------|-----------|-------------|
| `assetEntryId` | `number` | The primary key parameter for displaying the `Asset`. | 
| `className` | `string` | The asset's fully qualified class name. For PDFs, the `className` is [`com.liferay.document.library.kernel.model.DLFileEntry`](https://docs.liferay.com/portal/7.0/javadocs/portal-kernel/com/liferay/document/library/kernel/model/DLFileEntry.html). The `className` and `classPK` attributes are required to instantiate the Screenlet. |
| `classPK` | `number` | This is the asset identifier and it's unique. This attribute is used only with `className`. |
| `autoLoad` | `boolean` | Whether the list should automatically load when the Screenlet appears in the app's UI. The default value is `true`. |
| `offlinePolicy` | `string` | The offline mode setting. The default value is `remote-first`. See the [Offline](#offline) section for details. |


## Delegate [](id=delegate)

`PdfDisplayScreenlet` delegates some events to an object that conforms to 
the `FileDisplayScreenletDelegate` protocol. This protocol lets you implement 
the following methods:

- `- screenlet:onAssetResponse:`: Called when the `DLFileEntry` asset are received.

- `- screenlet:onFileAssetError:`: Called when an error occurs in the process. The `NSError` object describes the error.