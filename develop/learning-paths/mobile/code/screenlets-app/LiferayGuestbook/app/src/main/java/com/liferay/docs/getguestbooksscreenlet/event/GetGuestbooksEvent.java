package com.liferay.docs.getguestbooksscreenlet.event;

import com.liferay.docs.basescreenlet.ListEvent;
import com.liferay.docs.model.GuestbookModel;

import java.util.List;

public class GetGuestbooksEvent extends ListEvent<GuestbookModel> {

    public GetGuestbooksEvent(int targetScreenletId, List<GuestbookModel> entities) {
        super(targetScreenletId, entities);
    }

    public GetGuestbooksEvent(int targetScreenletId, Exception exception) {
        super(targetScreenletId, exception);
    }
}