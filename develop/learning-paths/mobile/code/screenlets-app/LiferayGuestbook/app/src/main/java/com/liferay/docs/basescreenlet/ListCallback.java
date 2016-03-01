package com.liferay.docs.basescreenlet;

import android.support.annotation.NonNull;

import com.liferay.mobile.screens.base.interactor.BasicEvent;
import com.liferay.mobile.screens.base.interactor.InteractorAsyncTaskCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Javier Gamarra
 */
public abstract class ListCallback<E> extends InteractorAsyncTaskCallback<List<E>> {

    public ListCallback(int targetScreenletId) {
        super(targetScreenletId);
    }

    @Override
    public List<E> transform(Object obj) throws Exception {
        List<E> entities = new ArrayList<>();

        JSONArray jsonArray = (JSONArray) obj;

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);

            entities.add(createInstance(json));
        }

        return entities;
    }

    @NonNull
    public abstract E createInstance(JSONObject json) throws JSONException;

    @Override
    protected BasicEvent createEvent(int targetScreenletId, List<E> entities) {
        return new ListEvent<E>(targetScreenletId, entities);
    }

    @Override
    protected BasicEvent createEvent(int targetScreenletId, Exception e) {
        return new ListEvent<E>(targetScreenletId, e);
    }
}
