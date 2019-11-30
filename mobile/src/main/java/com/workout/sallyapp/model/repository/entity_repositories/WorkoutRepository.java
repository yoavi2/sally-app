package com.workout.sallyapp.model.repository.entity_repositories;

import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.workout.sallyapp.model.databases.SallyDatabase;
import com.workout.sallyapp.model.entities.db.ScoreEntity;
import com.workout.sallyapp.model.entities.network.Workout;
import com.workout.sallyapp.model.repository.entity_repositories.base.BaseDbRepository;
import com.workout.sallyapp.model.repository.interfaces.DbTransactionCreator;
import com.workout.sallyapp.model.repository.interfaces.RepositoryTransactionEvent;

/**
 * Created by Yoav on 23-Apr-17.
 */
public class WorkoutRepository extends BaseDbRepository {

    public WorkoutRepository() {
        super();
    }

    public void saveWorkout(final Workout workout, @Nullable final RepositoryTransactionEvent listener){
        mDbExecutor.execute(SallyDatabase.class, new DbTransactionCreator() {
            @Override
            public ITransaction create() {
                return createTransaction(workout);
            }
        }, listener);
    }

    private ITransaction createTransaction(Workout workout){
        return new ProcessModelTransaction.Builder<>(
                new ProcessModelTransaction.ProcessModel<ScoreEntity>() {
                    @Override
                    public void processModel(ScoreEntity score, DatabaseWrapper
                            wrapper) {
                        if (score != null) {
                            score.save();
                        }
                    }
                }).addAll(workout.scores).build();
    }

}
