package com.workout.sallyapp.controller.dagger.modules;

import android.app.Application;
import android.content.SharedPreferences;

import com.f2prateek.rx.preferences2.RxSharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * Created by Yoav on 20-Jul-17.
 */
@Module
public class SharedPrefModule {

    @Provides
    @Singleton
    RxSharedPreferences provideSharedPref(Application application) {
        SharedPreferences preferences = getDefaultSharedPreferences(application);
        return RxSharedPreferences.create(preferences);
    }

}
