package com.workout.sallyapp.controller.dagger.modules;

import com.workout.sallyapp.view.activities.ChooseGroupActivity;
import com.workout.sallyapp.view.activities.ChooseGroupUsersActivity;
import com.workout.sallyapp.view.activities.LoginActivity;
import com.workout.sallyapp.view.activities.MainActivity;
import com.workout.sallyapp.view.activities.NewWorkoutActivityMulti;
import com.workout.sallyapp.view.activities.NewWorkoutActivitySingle;
import com.workout.sallyapp.view.activities.SettingsActivity;
import com.workout.sallyapp.view.activities.base.BaseNewWorkoutActivity;
import com.workout.sallyapp.view.fragments.GroupFragment;
import com.workout.sallyapp.view.fragments.GroupUsersDialogFragment;
import com.workout.sallyapp.view.fragments.ScoreFragment;
import com.workout.sallyapp.view.notifications.MyFirebaseInstanceIDService;
import com.workout.sallyapp.view.services.MainActivityIntentService;
import com.workout.sallyapp.view.services.SettingsIntentService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Yoav on 31-Jul-17.
 */

@Module
public abstract class ModuleBuilder {

    @ContributesAndroidInjector()
    abstract MainActivity bindMainActivity();
     @ContributesAndroidInjector()
    abstract ChooseGroupActivity bindChooseGroupActivity();
    @ContributesAndroidInjector()
    abstract ChooseGroupUsersActivity bindChooseGroupUsersActivity();
    @ContributesAndroidInjector()
    abstract BaseNewWorkoutActivity bindBaseNewWorkoutActivity();
     @ContributesAndroidInjector()
    abstract NewWorkoutActivityMulti bindNewWorkoutActivityMulti();
     @ContributesAndroidInjector()
    abstract NewWorkoutActivitySingle bindNewWorkoutActivitySingle();
     @ContributesAndroidInjector()
    abstract LoginActivity bindLoginActivity();
     @ContributesAndroidInjector()
    abstract MyFirebaseInstanceIDService bindMyFirebaseInstanceIDService();
     @ContributesAndroidInjector()
    abstract GroupFragment bindGroupFragment();
     @ContributesAndroidInjector()
    abstract ScoreFragment bindScoreFragment();
     @ContributesAndroidInjector()
    abstract GroupUsersDialogFragment bindGroupUsersDialogFragment();
     @ContributesAndroidInjector()
    abstract MainActivityIntentService bindNetworkIntentService();
    @ContributesAndroidInjector()
    abstract SettingsIntentService bindSettingsIntentServicee();
    @ContributesAndroidInjector()
    abstract SettingsActivity bindSettingsActivity();



}
