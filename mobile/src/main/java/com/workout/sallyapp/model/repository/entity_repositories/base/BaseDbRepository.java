package com.workout.sallyapp.model.repository.entity_repositories.base;

import com.workout.sallyapp.model.repository.SallyDbRepositoryExecutor;

/**
 * Created by Yoav on 24-Apr-17.
 */

public class BaseDbRepository {
    protected final SallyDbRepositoryExecutor mDbExecutor;

    public BaseDbRepository() {
        mDbExecutor = new SallyDbRepositoryExecutor();
    }
}
