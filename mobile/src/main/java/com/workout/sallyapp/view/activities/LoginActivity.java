package com.workout.sallyapp.view.activities;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.BuildConfig;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.workout.sallyapp.R;
import com.workout.sallyapp.controller.retrofit.apis.UserAPI;
import com.workout.sallyapp.controller.retrofit.error_handling.APIError;
import com.workout.sallyapp.controller.retrofit.error_handling.ErrorUtils;
import com.workout.sallyapp.model.entities.db.UserEntity;
import com.workout.sallyapp.model.repository.entity_repositories.UserRepository;
import com.workout.sallyapp.model.repository.interfaces.RepositoryTransactionEvent;

import java.util.Arrays;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private static final int RC_SIGN_IN = 193;

    @Inject
    UserAPI mUserApi;
    @Inject
    ErrorUtils mErrorUtils;
    @Inject
    UserRepository mUserRepository;

    private FirebaseAuth mAuth;
    private UserEntity mSignedInUser;
    private boolean mIsDbGetUserAttempted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isGooglePlayServicesAvailable(this)) {
            return;
        }

        AndroidInjection.inject(this);

        boolean isSignInNeeded = false;

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            isSignInNeeded = true;
        } else {
            // DB
            mIsDbGetUserAttempted = true;
            mSignedInUser = mUserRepository.getUserbyFirebaseIdSync(user.getUid());

            if (mSignedInUser == null || !mSignedInUser.firebaseUId.equals(user.getUid())) {
                isSignInNeeded = true;
            }
        }

        if (!isSignInNeeded) {
            nextActivity();
        } else {
            startFireBaseAuthActivity();
        }
    }

    private void startFireBaseAuthActivity() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                        .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                        .setLogo(R.mipmap.ic_launcher)
                        .build(),
                RC_SIGN_IN);
    }

    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            // Successfully signed in
            if (resultCode == RESULT_OK) {
                FirebaseUser signedInFirebaseUser = mAuth.getCurrentUser();
                String currFireBaseID = mUserRepository.getCurrentFirebaseUserId();

                // Check if the user signed in is the same as in sharedpref
                if (currFireBaseID != null && currFireBaseID.equals(signedInFirebaseUser.getUid())) {
                    nextActivity();
                    return;
                }

                // If not attempted before in onCreate, attempt to get current user from DB
                if (mSignedInUser == null && !mIsDbGetUserAttempted) {
                    mSignedInUser = mUserRepository.getUserbyFirebaseIdSync(signedInFirebaseUser.getUid());
                }

                // The user is not the one in sharedpref but has logged in before on this device
                if (mSignedInUser != null && mSignedInUser.jwt != null){
                    switchUserEndActivity(mSignedInUser.serverId, mSignedInUser.firebaseUId, mSignedInUser.jwt);

                    return;
                }
                // We need to try and create a new user. We will get a server id for the user if he exists or was created
                else {
                    // Create user entity based on firebase user
                    final UserEntity newUser = UserEntity.userFromFirebaseUser(signedInFirebaseUser);

                    // Create user in server
                    Call<Long> call = mUserApi.createUser(newUser);
                    call.enqueue(new Callback<Long>() {
                        @Override
                        public void onResponse(Call<Long> call, Response<Long> response) {
                            if (response.isSuccessful()) {
                                // Extract from response
                                final String jwt = response.headers().get("Authorization");
                                Long userServerId = response.body();

                                // Update created user
                                newUser.serverId = userServerId;
                                newUser.jwt = jwt;
                                mUserRepository.saveUser(newUser, new RepositoryTransactionEvent() {
                                    @Override
                                    public void onSuccess() {
                                        switchUserEndActivity(newUser.serverId, newUser.firebaseUId, newUser.jwt);
                                    }

                                    @Override
                                    public void onError(Throwable error) {
                                        Toast.makeText(LoginActivity.this, R.string.error_saving_user, Toast.LENGTH_SHORT).show();

                                        startFireBaseAuthActivity();
                                    }
                                });
                            } else {
                                APIError error = mErrorUtils.parseError(response);
                                Timber.e(error.message());
                                Toast.makeText(LoginActivity.this, R.string.error_creating_user, Toast.LENGTH_SHORT).show();

                                // This can create a loop in case of google auto signing in
                                // startFireBaseAuthActivity();
                            }
                        }

                        @Override
                        public void onFailure(Call<Long> call, Throwable t) {
                            Timber.e(t.getMessage());
                            Toast.makeText(LoginActivity.this, R.string.error_creating_user, Toast.LENGTH_SHORT).show();
                            Log.e("Error in retrofit?", String.valueOf(t.getStackTrace()));

                            // This can create a loop in case of google auto signing in
                            // startFireBaseAuthActivity();
                        }
                    });

                    return;
                }
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Log.d(TAG, "onActivityResult: User pressed back button");
                    finish();
                }
                else if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Log.d(TAG, "onActivityResult: no_internet_connection");
                    Toast.makeText(LoginActivity.this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                    startFireBaseAuthActivity();
                }
                else if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Log.d(TAG, "onActivityResult: unknown_error");
                    Toast.makeText(LoginActivity.this, R.string.unkown_error, Toast.LENGTH_SHORT).show();
                    startFireBaseAuthActivity();
                }
            }
        }
    }

    private void updateFirebaseToken(Long userId) {
        // We switched users therefore need to update the server with this user's token
        String firebaseToken = FirebaseInstanceId.getInstance().getToken();

        if (firebaseToken != null) {
            Call<Void> call = mUserApi.refreshToken(userId, firebaseToken);
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

    private void switchUserEndActivity(Long userId, String firebaseUId, String jwt) {
        updateSharedPref(firebaseUId, userId, jwt);
        updateFirebaseToken(userId);
        nextActivity();
    }

    private void updateSharedPref(String firebaseUId, Long userId, String jwt) {
        mUserRepository.setCurrentFirebaseUserID(firebaseUId);
        mUserRepository.setCurrentUserID(userId);
        mUserRepository.setUserJWT(jwt);
    }

    private void nextActivity() {
        startActivity(MainActivity.createIntent(this));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        isGooglePlayServicesAvailable(this);
    }

    public boolean isGooglePlayServicesAvailable(final Activity activity) {
        final GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if(status != ConnectionResult.SUCCESS) {
            if(googleApiAvailability.isUserResolvableError(status)) {
                Dialog dialog = googleApiAvailability.getErrorDialog(activity, status, 2404);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        googleApiAvailability.makeGooglePlayServicesAvailable(activity);
                        finish();
                    }
                });
                dialog.show();
            }
            return false;
        }
        return true;
    }

}
