package com.liferay.docs.basescreenlet;

import com.liferay.mobile.screens.base.interactor.BasicEvent;

import java.util.List;

/**
 * @author Javier Gamarra
 */
public class ListEvent<E> extends BasicEvent {

    private List<E> _entities;

    public ListEvent(int targetScreenletId, List<E> entities) {
        super(targetScreenletId);

        _entities = entities;
    }

    public ListEvent(int targetScreenletId, Exception exception) {
        super(targetScreenletId, exception);
    }

    public List<E> getEntities() {
        return _entities;
    }
}
