package com.workout.sallyapp.model.repository.interfaces;

/**
 * Created by Yoav
 */
public interface RepositoryTransactionEvent {
    void onSuccess();
    void onError(Throwable error);
}
