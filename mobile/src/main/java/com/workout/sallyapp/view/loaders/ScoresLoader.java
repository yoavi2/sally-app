package com.workout.sallyapp.view.loaders;

import android.content.Context;

import com.raizlabs.android.dbflow.list.FlowCursorList;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.workout.sallyapp.model.entities.db.ScoreEntity;
import com.workout.sallyapp.model.entities.db.ScoreEntity_Table;

/**
 * Created by Yoav on 17-Apr-17.
 */

public class ScoresLoader extends BaseDbflowCursorListLoader<ScoreEntity> {

    private long mUserId;

    public ScoresLoader(Context context, long userId){
        super(context, ScoreEntity.class);

        mUserId = userId;
    }

    @Override
    public FlowCursorList<ScoreEntity> loadInBackground() {
        mData = SQLite.select()
                .from(ScoreEntity.class)
                .where(ScoreEntity_Table.user_serverId.is(mUserId))
                .orderBy(ScoreEntity_Table.date, true)
                .cursorList();

        return mData;
    }


}
