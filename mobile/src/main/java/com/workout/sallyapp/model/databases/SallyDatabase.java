package com.workout.sallyapp.model.databases;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by Yoav on 23-Mar-17.
 */
@Database(name = SallyDatabase.NAME, version = SallyDatabase.VERSION)
public class SallyDatabase {
    public static final String NAME = "AppDatabase"; // we will add the .db extension
    public static final int VERSION = 1;
}
