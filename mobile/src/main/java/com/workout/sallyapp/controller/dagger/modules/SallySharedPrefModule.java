package com.workout.sallyapp.controller.dagger.modules;

import android.app.Application;

import com.f2prateek.rx.preferences2.Preference;
import com.f2prateek.rx.preferences2.RxSharedPreferences;
import com.workout.sallyapp.R;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Yoav on 20-Jul-17.
 */

@Module
public class SallySharedPrefModule {

    public static final String NAMED_USER_ID = "userId";
    public static final String NAMED_FIREBASE_USER_ID = "firebaseUserId";
    public static final String NAMED_USER_JWT = "userJwt";

    @Provides
    @Singleton
    @Named(NAMED_USER_ID)
    public Preference<Long> provideUserId(RxSharedPreferences rxSharedPreferences, Application application) {
        return rxSharedPreferences.getLong(application.getString(R.string.shared_pref_user_id), null);
    }

    @Provides
    @Singleton
    @Named(NAMED_FIREBASE_USER_ID)
    public Preference<String> provideFirebaseUserId(RxSharedPreferences rxSharedPreferences, Application application) {
        return rxSharedPreferences.getString(application.getString(R.string.shared_pref_firebase_user_id), null);
    }

    @Provides
    @Singleton
    @Named(NAMED_USER_JWT)
    public Preference<String> provideUserJwt(RxSharedPreferences rxSharedPreferences, Application application) {
        return rxSharedPreferences.getString(application.getString(R.string.shared_pref_user_jwt), null);
    }
}
