package com.workout.sallyapp.view.activities.base;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import timber.log.Timber;

/**
 * Created by Yoav on 26-Mar-17.
 */
public class BaseFireBaseActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public FirebaseUser mCurrentFireBaseUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        mCurrentFireBaseUser = mAuth.getCurrentUser();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mAuth = firebaseAuth;
                mCurrentFireBaseUser = firebaseAuth.getCurrentUser();
                if (mCurrentFireBaseUser != null) {
                    // User is signed in
                } else {
                    // User is signed out
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
