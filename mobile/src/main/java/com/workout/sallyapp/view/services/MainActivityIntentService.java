package com.workout.sallyapp.view.services;

import android.app.IntentService;
import android.content.Intent;

import com.workout.sallyapp.controller.retrofit.apis.UserAPI;
import com.workout.sallyapp.controller.retrofit.error_handling.APIError;
import com.workout.sallyapp.controller.retrofit.error_handling.ErrorUtils;
import com.workout.sallyapp.model.entities.db.GroupEntity;
import com.workout.sallyapp.model.entities.db.ScoreEntity;
import com.workout.sallyapp.model.repository.entity_repositories.GroupRepository;
import com.workout.sallyapp.model.repository.entity_repositories.ScoreRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by Yoav on 06-Aug-17.
 */

public class MainActivityIntentService extends IntentService {

    public static final String REQUEST_TYPE = "NetworkIntentServiceType";
    public static final String USER_SERVER_ID = "NetworkIntentServiceUserServerId";

    public static final String REQUEST_TYPE_SCORES = "NetworkIntentService_GetScores";
    public static final String REQUEST_TYPE_GROUPS = "NetworkIntentService_GetGroups";

    @Inject
    UserAPI mUserApi;
    @Inject
    ErrorUtils mErrorUtils;
    @Inject
    GroupRepository mGroupRepository;
    @Inject
    ScoreRepository mScoreRepository;

    public MainActivityIntentService() {
        super("MainActivityIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidInjection.inject(this);
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        String type = workIntent.getStringExtra(REQUEST_TYPE);
        Long userServerID = workIntent.getLongExtra(USER_SERVER_ID, 0);

        switch (type) {
            case REQUEST_TYPE_SCORES:
                getScores(userServerID);
                break;
            case REQUEST_TYPE_GROUPS:
                getGroups(userServerID);
                break;
            default:
                Timber.e("Unknown request sent to MainActivityIntentService");
        }
    }

    private void getScores(final Long userServerId) {
        mScoreRepository.getLatestScore(userServerId, new ScoreRepository.onMaxScoreTimestampQueried() {
            @Override
            public void deliver(Long maxTimestamp) {
                Call<List<ScoreEntity>> call =
                        mUserApi.getUserScores(userServerId,
                                maxTimestamp != null ? maxTimestamp : 0L);

                call.enqueue(new Callback<List<ScoreEntity>>() {
                    @Override
                    public void onResponse(Call<List<ScoreEntity>> call, Response<List<ScoreEntity>> response) {
                        if (response.isSuccessful()) {
                            mScoreRepository.saveScores(response.body(), null);
                        } else {
                            APIError error = mErrorUtils.parseError(response);
                            Timber.e(error.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ScoreEntity>> call, Throwable t) {
                        Timber.e(t.toString());
                    }
                });
            }
        });
    }

    private void getGroups(Long userServerId) {
        Call<List<GroupEntity>> groupCall = mUserApi.getUserGroups(userServerId);
        groupCall.enqueue(new Callback<List<GroupEntity>>() {
            @Override
            public void onResponse(Call<List<GroupEntity>> call, Response<List<GroupEntity>> response) {
                if (response.isSuccessful()) {
                    mGroupRepository.saveGroups(response.body(), null);
                } else {
                    APIError error = mErrorUtils.parseError(response);
                    Timber.e(error.message());
                }
            }

            @Override
            public void onFailure(Call<List<GroupEntity>> call, Throwable t) {
                Timber.e(t.toString());
            }
        });
    }
}