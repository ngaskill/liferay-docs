package com.liferay.docs.getguestbooksscreenlet.interactor;

import com.liferay.docs.basescreenlet.ListListener;
import com.liferay.docs.model.GuestbookModel;
import com.liferay.mobile.screens.base.interactor.Interactor;

public interface GetGuestbooksInteractor extends Interactor<ListListener<GuestbookModel>> {

    void getGuestbooks(long groupId);
}