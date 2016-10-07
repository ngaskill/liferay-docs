# New Android Architecture

## New BaseRemoteInteractor

The 2.0 Android version of Liferay Screens introduces several architectural changes to simplify screenlet creation and create a more robust offline support.

The main difference between the previous architecture is that the interactors run in a background process, so it can be written in a synchronous style.

This is an example of the `execute` method in a synchronous interactor:

	long companyId = (long) args[0];
	String login = (String) args[1];
	BasicAuthMethod basicAuthMethod = (BasicAuthMethod) args[2];
	String userName = (String) args[3];
	String apiPassword = (String) args[4];

	validate(companyId, login, basicAuthMethod, userName, apiPassword);

	Authentication authentication = new BasicAuthentication(userName, apiPassword);
	Session anonymousSession = new SessionImpl(LiferayServerContext.getServer(), authentication);
	ForgotPasswordConnector connector = ServiceProvider.getInstance().getForgotPasswordConnector(anonymousSession);

	Boolean sent = getBasicEventNew(companyId, login, basicAuthMethod, connector);

	return new ForgotPasswordEvent(sent);
	
The `execute` method is the main entry point of our interactor and where all the business code and logic is going to be implemented. 

The method will be executed in a background thread when calling it from the screenlet with a `start` invocation and in the same thread when calling it via an `execute` method call.

When calling it in synchronous mode, you can write remote calls or heavy operations that won't block the UI thread but at the same time the results will be delivered to the UI thread by calling the attached listener.

The interactor receives the arguments as a varargs and returns an event that is going to be sent via an EventBus to the success and error callbacks.

The new architecture provides several event classes that you can use if you don't want to store specific information in your event class. For example you could store the `JSONObject` response on a `BasicEvent` class.

In this concrete case we want to store a boolean variable to represent the success or failure of the operation. So we've inherited from BasicEvent and defined a class with a default no-args constructor and the boolean field.

The code for those callbacks is the following:

	@Override
	public void onSuccess(ForgotPasswordEvent event) throws Exception {
		getListener().onForgotPasswordRequestSuccess(event.isPasswordSent());
	}

	@Override
	public void onFailure(ForgotPasswordEvent event) {
		getListener().onForgotPasswordRequestFailure(event.getException());
	}
	
We are just receiving the event generated in the execute method and notifying the correct listeners in each case.

Liferay Screens is decorating that event class and passing it through an EventBus to isolate the interactor of orientation changes or activity leaks. 

That's all the code needed to write an interactor that will be executed in a background thread.

## New BaseCacheReadInteractor

If you want to add cache support to your interactor you should inherit from `BaseCacheReadInteractor`. The full code for implementing an interactor that loads a comment entry is listed here:

	@Override
	public CommentEvent execute(Object... args) throws Exception {

		long commentId = (long) args[0];

		validate(commentId);

		ScreenscommentService commentService = new ScreenscommentService(getSession());
		JSONObject jsonObject = commentService.getComment(commentId);
		return new CommentEvent(jsonObject);
	}
	
In the `execute` method we use the commentId, an argument passed through a varargs, to do a request against a Portal API and return an event that stores the json information. `CommentEvent` is a POJO that inherits from `BasicEvent` and has a no-args constructor.

We use the event to notify the screenlet with a listener, with this code:

	@Override
	public void onSuccess(CommentEvent event) throws Exception {
		CommentEntry commentEntry = new CommentEntry(JSONUtil.toMap(event.getJSONObject()));
		getListener().onLoadCommentSuccess(commentEntry);
	}

	@Override
	public void onFailure(CommentEvent event) {
		getListener().error(event.getException(), CommentDisplayScreenlet.LOAD_COMMENT_ACTION);
	}
	
The only difference with the previous base interactor is that the listener has a default *error* method that receives an exception and a string signalling the user action that started the interactor.

When we inherit from `BaseCacheReadInteractor` we have to provide the implementation of a method returning an `id`, the key we are using the cache the information. In this case I want to index my cache entry based on the first argument, the commentId.

	@Override
	protected String getIdFromArgs(Object... args) {
		return String.valueOf((long) args[0]);
	}
	
By default, Liferay Screens is using SnappyDB in the offline layer, but it also provides a pluggable mechanism to contribute your preferred database solution.

## New BaseCacheWriteInteractor

The default base class for implementing interactors that perform write operations to Liferay Portal (add, update, delete...) follows the same pattern stablished in the `BaseCacheReadInteractor`.

The only difference is that the interactor expects an event class (instead of varargs) in the execute method. The event class acts as the interface of the interactor.

## New BaseListInteractor

The interactor that deals with remote operations that can be paginated expects several methods: `getPageRowsRequest` that will deal with the request to retrieve the rows, `getPageRowCountRequest` that will request the total number of objects to paginate, `getIdFromArgs` to return the cache key and `createEntity`.

`createEntity` is the method responsible of creating an event from the JSONObject result. Usually will create an business entity (normally a POJO) and an event wrapping that business entity.

The following example illustrate the code needed to do a paginated and cached request.

@Override
	protected JSONArray getPageRowsRequest(Query query, Object... args) throws Exception {
		long folderId = (long) args[0];
		
		...
	}

	@Override
	protected Integer getPageRowCountRequest(Object... args) throws Exception {
		
		...
	}

	@Override
	protected BookmarkEvent createEntity(Map<String, Object> stringObjectMap) {
		return new BookmarkEvent(new Bookmark(stringObjectMap));
	}

	@Override
	protected String getIdFromArgs(Object... args) {
		return String.valueOf(args[0]);
	}
	
The event class returned from `createEntity` will have to inherit from `ListEvent` and provide a cache key and a method unwrapping the business entity.





