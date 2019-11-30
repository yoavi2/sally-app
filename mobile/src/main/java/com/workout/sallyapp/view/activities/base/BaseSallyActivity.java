package com.workout.sallyapp.view.activities.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.workout.sallyapp.model.entities.db.ChallengeEntity;
import com.workout.sallyapp.model.entities.db.UserEntity;
import com.workout.sallyapp.model.repository.entity_repositories.ChallengeRepository;
import com.workout.sallyapp.model.repository.entity_repositories.UserRepository;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

/**
 * Created by Yoav on 24-Apr-17.
 */

public class BaseSallyActivity extends BaseFireBaseActivity {

    @Inject
    public ChallengeRepository mChallengeRepository;
    @Inject
    public UserRepository mUserRepository;

    public ChallengeEntity mCurrentChallenge;
    public UserEntity mCurrentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidInjection.inject(this);

        mCurrentChallenge = mChallengeRepository.getAppChallengeSync(); // TODO: This needs to happen once in application not every activity
        mCurrentUser = mUserRepository.getCurrentUserSync(); // TODO: This needs to happen once in application not every activity
    }
}
