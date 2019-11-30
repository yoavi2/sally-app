package com.workout.sallyapp.view.services;

import android.app.IntentService;
import android.content.Intent;

import com.workout.sallyapp.controller.retrofit.apis.UserAPI;
import com.workout.sallyapp.controller.retrofit.error_handling.APIError;
import com.workout.sallyapp.controller.retrofit.error_handling.ErrorUtils;
import com.workout.sallyapp.model.entities.network.UserPref;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by Yoav on 18-Aug-17.
 */

public class SettingsIntentService extends IntentService {

    public static final String USER_PREFERENCES = "SettingsNetworkIntentServiceUserPreferences";

    @Inject
    UserAPI mUserApi;
    @Inject
    ErrorUtils mErrorUtils;

    public SettingsIntentService() {
        super("SettingsIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidInjection.inject(this);
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        UserPref userPref = workIntent.getParcelableExtra(USER_PREFERENCES);
        sendUserPrefToServer(userPref);
    }

    private void sendUserPrefToServer(UserPref userPref) {
        Call<Void> call = mUserApi.updatePrefs(userPref.userId, userPref);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                } else {
                    APIError error = mErrorUtils.parseError(response);
                    Timber.e(error.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Timber.e(t.toString());
            }
        });
    }

}
