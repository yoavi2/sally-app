package com.workout.sallyapp.view.loaders;

import android.content.Context;

import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.workout.sallyapp.model.entities.db.HighscoreQueryModel;
import com.workout.sallyapp.model.entities.db.ScoreEntity;
import com.workout.sallyapp.model.entities.db.ScoreEntity_Table;

/**
 * Created by Yoav on 05-May-17.
 */

public class HighScoreLoader extends BaseDbflowLoader<ScoreEntity, Integer> {

    private long mUserId;

    public HighScoreLoader(Context context, long userId){
        super(context, ScoreEntity.class);

        mUserId = userId;
    }


    @Override
    public Integer loadInBackground() {
        HighscoreQueryModel highscore = SQLite.select(Method.max(ScoreEntity_Table.durationInSec).as(HighscoreQueryModel.COLUMN_HIGESCORE))
                .from(ScoreEntity.class)
                .where(ScoreEntity_Table.user_serverId.is(mUserId))
                .queryCustomSingle(HighscoreQueryModel.class);

        return highscore.highscore;
    }
}
