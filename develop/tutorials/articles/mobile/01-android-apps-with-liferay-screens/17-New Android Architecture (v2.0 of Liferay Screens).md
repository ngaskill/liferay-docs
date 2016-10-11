# New Android Architecture

## New BaseRemoteInteractor

Liferay Screens for Android version 2.0 introduces several architectural changes 
to simplify Screenlet creation and offer more robust offline support. The main 
difference between the previous architecture is that the Interactors run in a 
background process, so you don't have to worry about manually creating or 
setting callback classes; you can write synchronous server calls. 

The `execute` method is an Interactor's focal point; it's where the Interactor's 
business logic resides. Invoking the 
[`BaseInteractor` class's](https://github.com/liferay/liferay-screens/blob/master/android/library/src/main/java/com/liferay/mobile/screens/base/interactor/BaseInteractor.java) 
`start` method from the Screenlet class causes `execute` to run in a background 
thread. Alternatively, you can call `execute` directly if you want it to run in 
the current thread. 

Here's an example `execute` method: 

	@Override
	public ForgotPasswordEvent execute(Object[] args) throws Exception {

		long companyId = (long) args[0];
		String login = (String) args[1];
		BasicAuthMethod basicAuthMethod = (BasicAuthMethod) args[2];
		String anonymousApiUserName = (String) args[3];
		String anonymousApiPassword = (String) args[4];

		validate(companyId, login, basicAuthMethod, anonymousApiUserName, anonymousApiPassword);

		Authentication authentication = new BasicAuthentication(anonymousApiUserName, anonymousApiPassword);
		Session anonymousSession = new SessionImpl(LiferayServerContext.getServer(), authentication);
		ForgotPasswordConnector connector = ServiceProvider.getInstance().getForgotPasswordConnector(anonymousSession);

		Boolean sent = getBasicEventNew(companyId, login, basicAuthMethod, connector);

		return new ForgotPasswordEvent(sent);
	}

When calling it in synchronous mode, you can write remote calls or heavy 
operations that won't block the UI thread but at the same time the results will 
be delivered to the UI thread by calling the attached listener.

<!-- 
When you say "synchronous mode" here, do you mean making the server call in 
the execute method without manually setting a callback? Also, provide example 
code of this. 

Yes, callbacks are not needed anymore.
-->

For example, this is the full code of the *execute* method for adding an comment:

	@Override
	public CommentEvent execute(CommentEvent event) throws Exception {

		String className = event.getClassName();
		long classPK = event.getClassPK();
		String body = event.getBody();

		validate(className, classPK, body);

		JSONObject jsonObject = new ScreenscommentService(getSession()).addComment(className, classPK, body);

		event.setCommentEntry(new CommentEntry(JSONUtil.toMap(jsonObject)));
		return event;
	}
	
Notice that we do the the request to the portal without adding a callback to the Liferay Session, callbacks are not needed anymore.


The interactor receives the arguments as a varargs and returns an event that is 
going to be sent via an EventBus to the success and error callbacks.

Analysing the previous code, this segment is responsible of casting the parameters from the varargs:

	long companyId = (long) args[0];
	String login = (String) args[1];
	BasicAuthMethod basicAuthMethod = (BasicAuthMethod) args[2];
	String anonymousApiUserName = (String) args[3];
	String anonymousApiPassword = (String) args[4];
	
And this one creates an event to return it:

	return new ForgotPasswordEvent(sent);
	
Note that you can define whatever arguments you want to receive in your interactor and pass a DTO or a POJO as your interface. That way you won't have to cast each parameter.

The new architecture provides several event classes that you can use if you 
don't want to store specific information in your event class. For example you 
could store the `JSONObject` response on a `BasicEvent` class.

In this concrete case we want to store a boolean variable to represent the 
success or failure of the operation. So we've inherited from BasicEvent and 
defined a class with a default no-args constructor and the boolean field.

The code for those callbacks is the following:

    @Override
    public void onSuccess(ForgotPasswordEvent event) throws Exception {
        getListener().onForgotPasswordRequestSuccess(event.isPasswordSent());
    }

    @Override
    public void onFailure(ForgotPasswordEvent event) {
        getListener().onForgotPasswordRequestFailure(event.getException());
    }

We are just receiving the event generated in the execute method and notifying 
the correct listeners in each case.

Liferay Screens is decorating that event class and passing it through an 
EventBus to isolate the interactor of orientation changes or activity leaks. 

That's all the code needed to write an interactor that will be executed in a 
background thread.

## New BaseCacheReadInteractor

If you want to add cache support to your interactor you should inherit from 
`BaseCacheReadInteractor`. The full code for implementing an interactor that 
loads a comment entry is listed here:

    @Override
    public CommentEvent execute(Object... args) throws Exception {

        long commentId = (long) args[0];

        validate(commentId);

        ScreenscommentService commentService = new ScreenscommentService(getSession());
        JSONObject jsonObject = commentService.getComment(commentId);
        return new CommentEvent(jsonObject);
    }
	
In the `execute` method we use the commentId, an argument passed through a 
varargs, to do a request against a Portal API and return an event that stores 
the JSON information. `CommentEvent` is a POJO that inherits from `BasicEvent` 
and has a no-args constructor.

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
	
The only difference with the previous base interactor is that the listener has a 
default *error* method that receives an exception and a string signalling the 
user action that started the interactor.

When we inherit from `BaseCacheReadInteractor` we have to provide the 
implementation of a method returning an `id`, the key we are using the cache the 
information. In this case I want to index my cache entry based on the first 
argument, the commentId.

    @Override
    protected String getIdFromArgs(Object... args) {
        return String.valueOf((long) args[0]);
    }

By default, Liferay Screens is using SnappyDB in the offline layer, but it also 
provides a pluggable mechanism to contribute your preferred database solution.

## New BaseCacheWriteInteractor

The default base class for implementing interactors that perform write 
operations to Liferay Portal (add, update, delete...) follows the same pattern 
stablished in the `BaseCacheReadInteractor`.

The only difference is that the interactor expects an event class (instead of 
varargs) in the execute method. The event class acts as the interface of the 
interactor.

Here is a complete example of a write operation, the code responsible of deleting a rating:

	@Override
	public RatingEvent execute(RatingEvent event) throws Exception {

		ScreensratingsentryService ratingsEntryService =
			new ScreensratingsentryService(SessionContext.createSessionFromCurrentSession());

		JSONObject jsonObject = ratingsEntryService.deleteRatingsEntry(event.getClassPK(), event.getClassName(),
			event.getRatingGroupCounts());

		event.setJSONObject(jsonObject);
		return event;
	}

	@Override
	public void onSuccess(RatingEvent event) throws Exception {
		JSONObject result = event.getJSONObject();
		AssetRating assetRating = new AssetRating(result.getLong("classPK"), result.getString("className"),
			toIntArray(result.getJSONArray("ratings")), result.getDouble("average"), result.getDouble("userScore"),
			result.getDouble("totalScore"), result.getInt("totalCount"));
		getListener().onRatingOperationSuccess(assetRating);
	}

	@Override
	public void onFailure(RatingEvent event) {
		getListener().error(event.getException(), RatingScreenlet.DELETE_RATING_ACTION);
	}

## New BaseListInteractor

The interactor that deals with remote operations that can be paginated expects 
several methods: `getPageRowsRequest` that will deal with the request to 
retrieve the rows, `getPageRowCountRequest` that will request the total number 
of objects to paginate, `getIdFromArgs` to return the cache key and 
`createEntity`.

`createEntity` is the method responsible of creating an event from the 
JSONObject result. Usually will create an business entity (normally a POJO) and 
an event wrapping that business entity.

The following example illustrate the code needed to do a paginated and cached 
request.

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

The event class returned from `createEntity` will have to inherit from 
`ListEvent` and provide a cache key and a method unwrapping the business entity.

Here is the full example of the code responsible of listing web contents:

	public class WebContentListInteractor
		extends BaseListInteractor<BaseListInteractorListener<WebContent>, WebContentDisplayEvent> {

		@Override
		protected String getIdFromArgs(Object... args) {
			long folderId = (long) args[0];
	
			return String.valueOf(folderId);
		}
	
		@Override
		protected JSONArray getPageRowsRequest(Query query, Object... args) throws Exception {
	
			long folderId = (long) args[0];
	
			JournalContentConnector journalContentConnector =
				ServiceProvider.getInstance().getJournalContentConnector(getSession());
			return journalContentConnector.getJournalArticles(groupId, folderId, query.getStartRow(), query.getEndRow(),
				query.getComparatorJSONWrapper());
		}
	
		@Override
		protected Integer getPageRowCountRequest(Object... args) throws Exception {
	
			long folderId = (long) args[0];
	
			JournalContentConnector journalContentConnector =
				ServiceProvider.getInstance().getJournalContentConnector(getSession());
			return journalContentConnector.getJournalArticlesCount(groupId, folderId);
		}
	
		@Override
		protected WebContentDisplayEvent createEntity(Map<String, Object> stringObjectMap) {
			return new WebContentDisplayEvent(new WebContent(stringObjectMap, locale));
		}
	}
