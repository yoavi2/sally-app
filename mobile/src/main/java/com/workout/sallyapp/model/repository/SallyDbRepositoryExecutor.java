package com.workout.sallyapp.model.repository;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;
import com.workout.sallyapp.model.repository.interfaces.DbTransactionCreator;
import com.workout.sallyapp.model.repository.interfaces.RepositoryTransactionEvent;

import timber.log.Timber;

/**
 * Created by Yoav
 */
public class SallyDbRepositoryExecutor {
    public void execute(@NonNull Class db, @NonNull final DbTransactionCreator dbTransactionCreator, @Nullable final RepositoryTransactionEvent listener){
        DatabaseDefinition database = FlowManager.getDatabase(db);
        database.beginTransactionAsync(dbTransactionCreator.create())
                .success(new Transaction.Success() {
                    @Override
                    public void onSuccess(Transaction transaction) {
                        if (listener != null) {
                            listener.onSuccess();
                        }
                    }
                })
                .error(new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {
                        if (listener != null) {
                            listener.onError(error);
                        }
                        Timber.e("Error: " + error.getMessage());
                    }
                })
                .build().execute();
    }
}

