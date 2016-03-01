package com.liferay.docs.basescreenlet;

import java.util.List;

/**
 * @author Javier Gamarra
 */
public interface ListListener<E> {

    void onGetEntitiesFailure(Exception e);

    void onGetEntitiesSuccess(List<E> entities);

    void onItemClicked(E entity);
}
