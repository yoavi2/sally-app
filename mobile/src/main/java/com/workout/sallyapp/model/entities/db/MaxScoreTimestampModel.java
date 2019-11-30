package com.workout.sallyapp.model.entities.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.QueryModel;
import com.raizlabs.android.dbflow.structure.BaseQueryModel;
import com.workout.sallyapp.model.databases.SallyDatabase;

/**
 * Created by Yoav on 09-May-17.
 */
@QueryModel(database = SallyDatabase.class)
public class MaxScoreTimestampModel extends BaseQueryModel {

    public static String COLUMN_TIMESTAMP = "timestamp";

    @Column
    public long timestamp;
}
