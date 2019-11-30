package com.workout.sallyapp.model.entities.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.QueryModel;
import com.raizlabs.android.dbflow.structure.BaseQueryModel;
import com.workout.sallyapp.model.databases.SallyDatabase;

/**
 * Created by Yoav on 06-May-17.
 */
@QueryModel(database = SallyDatabase.class)
public class HighscoreQueryModel extends BaseQueryModel {

    public static String COLUMN_HIGESCORE = "highscore";

    @Column
    public Integer highscore;
}
