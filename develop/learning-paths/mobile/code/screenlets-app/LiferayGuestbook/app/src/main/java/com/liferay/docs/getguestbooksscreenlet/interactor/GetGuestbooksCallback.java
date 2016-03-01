package com.liferay.docs.getguestbooksscreenlet.interactor;


import android.support.annotation.NonNull;

import com.liferay.docs.basescreenlet.ListCallback;
import com.liferay.docs.model.GuestbookModel;

import org.json.JSONException;
import org.json.JSONObject;

public class GetGuestbooksCallback extends ListCallback<GuestbookModel> {

    public GetGuestbooksCallback(int targetScreenletId) {
        super(targetScreenletId);
    }

    @NonNull
    @Override
    public GuestbookModel createInstance(JSONObject json) throws JSONException {
        return new GuestbookModel(json);
    }

}
