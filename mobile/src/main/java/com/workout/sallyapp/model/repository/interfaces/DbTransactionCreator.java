package com.workout.sallyapp.model.repository.interfaces;

import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;

public interface DbTransactionCreator {
    ITransaction create();
}
