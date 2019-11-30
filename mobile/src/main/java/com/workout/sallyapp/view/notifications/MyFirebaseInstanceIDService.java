package com.workout.sallyapp.view.notifications;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import android.content.Context;

import com.workout.sallyapp.controller.retrofit.apis.UserAPI;
import com.workout.sallyapp.controller.retrofit.error_handling.APIError;
import com.workout.sallyapp.controller.retrofit.error_handling.ErrorUtils;
import com.workout.sallyapp.model.repository.entity_repositories.UserRepository;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by Yoav on 21-May-17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Inject
    UserAPI mUserApi;
    @Inject
    ErrorUtils mErrorUtils;
    @Inject
    UserRepository mUserRepository;

    @Override
    public void onCreate() {
        super.onCreate();

        AndroidInjection.inject(this);
    }

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        sendRegistrationToServer(refreshedToken);
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        final Context context = getApplicationContext();

        Long currUserId = mUserRepository.getCurrentUserId();

        if (currUserId != null) {
            Call<Void> call = mUserApi.refreshToken(currUserId, token);
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
}
