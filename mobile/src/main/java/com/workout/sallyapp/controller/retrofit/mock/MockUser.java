package com.workout.sallyapp.controller.retrofit.mock;

import android.support.annotation.Nullable;

import com.workout.sallyapp.controller.retrofit.apis.UserAPI;
import com.workout.sallyapp.model.entities.db.GroupEntity;
import com.workout.sallyapp.model.entities.db.ScoreEntity;
import com.workout.sallyapp.model.entities.db.UserEntity;
import com.workout.sallyapp.model.entities.network.UserPref;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.Calls;

import static com.workout.sallyapp.controller.retrofit.mock.MockDatabase.mUsers;

/**
 * Created by Yoav on 24-Apr-17.
 */
public class MockUser implements UserAPI {

    private final BehaviorDelegate<UserAPI> mDelegate;

    public MockUser(BehaviorDelegate<UserAPI> delegate) {
        this.mDelegate = delegate;
    }

    @Override
    public Call<UserEntity> getUser(@Path("id") Long userId) {
        for (UserEntity user : MockDatabase.mUsers) {
            if (user.serverId.equals(userId)) {
                return mDelegate.returningResponse(user).getUser(userId);
            }
        }

        // TODO: Should not return that... but whatever
        return Calls.failure(new IOException("No such user"));
    }

    @Override
    public Call<Long> createUser(@Body UserEntity user) {

        // Search first in users
        for (UserEntity mUser : mUsers) {
            if (mUser.firebaseUId.equals(user.firebaseUId)) {
                return mDelegate.returningResponse(mUser.serverId).createUser(user);
            }
        }

        // Not found
        UserEntity newUser = new UserEntity(user.firebaseUId, user.displayName, user.photoUrl, user.email);
        newUser.serverId = MockDatabase.mUserId++;

        Call<Long> call = mDelegate.returningResponse(newUser.serverId).createUser(user);
        return call;
    }

    @Override
    public Call<List<GroupEntity>> getUserGroups(@Path("id") Long userId) {
        ArrayList<GroupEntity> groups = new ArrayList<>();

        for (GroupEntity group : MockDatabase.mGroups) {
            for (UserEntity user : group.getUsers()) {
                if (user.serverId.equals(userId)) {
                    groups.add(group);
                    break;
                }
            }
        }

        return mDelegate.returningResponse(groups).getUserGroups(userId);
    }

    @Override
    public Call<List<ScoreEntity>> getUsersHighscores(@Query("id") List<Long> userIds) {
        ArrayList<ScoreEntity> scores = new ArrayList<>(userIds.size());

        ScoreEntity score = null;
        UserEntity user = null;

        for (Long userId : userIds) {

            for (UserEntity mockUser : MockDatabase.mUsers) {
                if (mockUser.serverId.equals(userId)) {
                    user = mockUser;
                    break;
                }
            }

            score = new ScoreEntity(MockDatabase.mChallenge, user, getRandonScore(0, MockDatabase.mChallenge.length), new Date());
            scores.add(score);
        }

        return mDelegate.returningResponse(scores).getUsersHighscores(userIds);
    }

    private int getRandonScore(int min, int max) {
        Random rand = new Random();

        return rand.nextInt((max - min) + 1) + min;
    }

    @Override
    public Call<List<ScoreEntity>> getUserScores(@Path("id") Long userId, @Nullable @Query("timestamp") Long fromTimestamp) {
        return null;
    }

    @Override
    @Deprecated
    public Call<Void> deleteUserScore(@Path("id") Long userId, @Query("ScoreId") Long scoreId) {
        return null;
    }

    @Override
    public Call<Void> refreshToken(@Path("id") Long userId, @Body String token) {
        return null;
    }

    @Override
    public Call<Void> updatePrefs(@Path("id") Long userId, @Body UserPref userPref) {
        return null;
    }
}
