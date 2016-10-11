# Migrating to Liferay Screens 2.0 [](id=migrating-to-liferay-screens-2.0)

There are a few things you should be aware of when migrating your apps to 
Screens 2.0 from previous versions of Screens. This document provides a list of 
these items. 

## Android SDK [](id=android-sdk)

We recommend using 23 as your app's `compileSdkVersion` and `targetSdkVersion`. 
This means you should use 23.4.0 or later for any Android support libraries you 
require (AppCompat, Design, etc...). For example, the following `compile` 
statement in your app's `build.gradle` file adds the latest version of v7 
AppCompat 23 to your app: 

    compile 'com.android.support:appcompat-v7:23.+'

## Liferay Screens API Changes [](id=liferay-screens-api-changes)

### Renamed Methods [](id=renamed-methods)

- The attribute `offlinePolicy` has been renamed to `cachePolicy` to be consistent with the other methods and the name of the strategies. 
- Error listeners in interactors (`onDDLFormLoadFailed`, `onDDLFormRecordLoadFailed`...) have been removed and replaced for a generic error listener with the signature `public void error(Exception e, String userAction)`
- The signature of `onDDLFormRecordLoaded` has been extended with one additional parameter and now receives a map of the attributes receives from the server. The current signature is this one: `public void onDDLFormRecordLoaded(Record record, Map<String, Object> valuesAndAttributes)`
- The methods of the listener dealing with cache operations (`loadingFromCache(boolean success)`, `retrievingOnline(boolean triedInCache, Exception e)` and `storingToCache(Object object)`) have been moved to their own listener, to don't force the developer to implement them when adding a screenlet listener. If you want to implement them, you should add an explicit cache listener:

		screenlet.setCacheListener(this);
	
- The screenlet listeners have removed the `BaseListScreenlet source` parameter (you can monitor the screenlet by other attributes and were polluting the API).
- The list listener API has changed to send the startRow and endRow of the request instead of the page requested. The API was:

		public void onListPageFailed(BaseListScreenlet source, int page, Exception e)
		
		public void onListPageReceived(BaseListScreenlet source, int page, List<Record> entries, int rowCount)
	
	And now is:
		
		public void onListPageFailed(int startRow, Exception e)
	
		public void onListPageReceived(int startRow, int endRow, List<Record> entries, int rowCount)
	
- AssetList screenlet now depends of the package `com.liferay.mobile.screens.asset.list` instead of `com.liferay.mobile.screens.assetlist`, to acomodate the new `AssetDisplayScreenlet` and have a more consistent package convention. 
- `record.getModelValues()` now returns a `Map` instead of a `HashMap` to follow general Java conventions.
- Private and protected fields in the Screenlets now follow general Java naming conventions and are NOT prefixed by _.

### Feature Changes [](id=feature-changes)

- The most important feature change is documented on [New Android Architecture](/develop/tutorials/-/knowledge_base/7-0/17-New Android Architecture (v2.0 of Liferay Screens)).

	Interactors now run in a background thread and are executed through an `start` call. So a previous call like this:
	
	 	try {
            LiferayScreensContext.init(this);
            LoginBasicInteractor loginInteractor = new LoginBasicInteractor();
            loginInteractor.onScreenletAttached(this);

            loginInteractor.start(BasicAuthMethod.EMAIL, "user", "password");
        } catch (Exception e) {
            Snackbar.make(_content, "Login failed! :(", Snackbar.LENGTH_SHORT).show();
        }
        
	Can be simplified to:
	
		LoginBasicInteractor loginInteractor = new LoginBasicInteractor();
		loginInteractor.onScreenletAttached(this);

		loginInteractor.start(BasicAuthMethod.EMAIL, "user", "password");
  
	You don't have to deal with the exception any more, because the interactor will capture any exceptions generated inside the `start` call and notify them through the listeners. The screenletId also isn't needed any more, the library will decorate the event with a generated screenletId.

- You don't have to call `LiferayScreensContext.init(this)` to initialise the library if you are using a screenlet without view,  it is called automatically.
- If you want to inherit the default styles of a screenlet, your activity or application should inherit from the *default_theme*, *material_theme* or *westeros_theme* respectively.