package com.workout.sallyapp.model.repository.entity_repositories;

import android.content.Context;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.workout.sallyapp.R;
import com.workout.sallyapp.model.entities.db.ChallengeEntity;
import com.workout.sallyapp.model.entities.db.ChallengeEntity_Table;

/**
 * Created by Yoav on 25-Apr-17.
 */
public class ChallengeRepository {

    private Context mContext;
    private ChallengeEntity mAppChallenge;

    public ChallengeRepository(Context mContext) {
        this.mContext = mContext;
    }

    // Synchronous
    public ChallengeEntity getAppChallengeSync(){
        if (mAppChallenge == null) {
            Long challengeId = (long) mContext.getResources().getInteger(R.integer.sally_up_challenge_id);
            mAppChallenge = SQLite.select().from(ChallengeEntity.class).where(ChallengeEntity_Table.serverId.is(challengeId)).querySingle();
        }

        return mAppChallenge;
    }
}
