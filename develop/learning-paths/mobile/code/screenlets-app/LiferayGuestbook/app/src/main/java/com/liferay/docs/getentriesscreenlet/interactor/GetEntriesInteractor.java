package com.liferay.docs.getentriesscreenlet.interactor;

import com.liferay.docs.basescreenlet.ListListener;
import com.liferay.docs.model.EntryModel;
import com.liferay.mobile.screens.base.interactor.Interactor;

public interface GetEntriesInteractor extends Interactor<ListListener<EntryModel>> {

    void getEntries(long groupId, long guestbookId);
}