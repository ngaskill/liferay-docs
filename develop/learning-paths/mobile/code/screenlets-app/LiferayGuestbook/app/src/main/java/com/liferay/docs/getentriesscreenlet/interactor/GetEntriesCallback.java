package com.liferay.docs.getentriesscreenlet.interactor;

import android.support.annotation.NonNull;

import com.liferay.docs.basescreenlet.ListCallback;
import com.liferay.docs.model.EntryModel;

import org.json.JSONException;
import org.json.JSONObject;

public class GetEntriesCallback extends ListCallback<EntryModel> {

    public GetEntriesCallback(int targetScreenletId) {
        super(targetScreenletId);
    }

    @NonNull
    @Override
    public EntryModel createInstance(JSONObject json) throws JSONException {
        return new EntryModel(json);
    }
}