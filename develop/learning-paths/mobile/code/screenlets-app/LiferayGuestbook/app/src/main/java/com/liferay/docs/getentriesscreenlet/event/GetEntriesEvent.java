package com.liferay.docs.getentriesscreenlet.event;

import com.liferay.docs.basescreenlet.ListEvent;
import com.liferay.docs.model.EntryModel;

import java.util.List;

public class GetEntriesEvent extends ListEvent<EntryModel> {

    public GetEntriesEvent(int targetScreenletId, List<EntryModel> entities) {
        super(targetScreenletId, entities);
    }

    public GetEntriesEvent(int targetScreenletId, Exception exception) {
        super(targetScreenletId, exception);
    }
}