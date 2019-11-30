package com.workout.sallyapp.view;

import com.google.firebase.crash.FirebaseCrash;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.support.multidex.MultiDex;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.raizlabs.android.dbflow.config.DatabaseConfig;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.list.FlowQueryList;
import com.raizlabs.android.dbflow.runtime.ContentResolverNotifier;
import com.squareup.leakcanary.LeakCanary;
import com.workout.sallyapp.BuildConfig;
import com.workout.sallyapp.controller.dagger.components.DaggerAppComponent;
import com.workout.sallyapp.model.databases.SallyDatabase;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasServiceInjector;
import dagger.android.support.HasSupportFragmentInjector;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

/**
 * Created by Yoav on 12/11/2016.
 */

public class MyApplication extends Application implements HasActivityInjector,
        HasSupportFragmentInjector,
        HasServiceInjector {

//    private static final String BASE_URL = "http://10.0.2.2:4567/"; // Debug URL
    private static final String BASE_URL = "http://sallyapp.westeurope.azurecontainer.io:4567/"; // prod URL

    @Inject
    DispatchingAndroidInjector<Activity> activityDispatchingAndroidInjector;
    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;
    @Inject
    DispatchingAndroidInjector<Service> serviceDispatchingAndroidInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        MultiDex.install(this);

        FlowManager.init(new FlowConfig.Builder(this)
                .addDatabaseConfig(new DatabaseConfig.Builder(SallyDatabase.class)
                        .modelNotifier(new ContentResolverNotifier(BuildConfig.APPLICATION_ID))
                        .build())
                .build());

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            Stetho.initializeWithDefaults(this);
        } else {
            Timber.plant(new FirebaseTree());
        }

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        DaggerAppComponent
                .builder()
                .withBaseUrl(BASE_URL)
                .application(this)
                .build()
                .inject(this);
    }

    private static class FirebaseTree extends Timber.Tree {

        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }

            Throwable throwable = t != null
                    ? t
                    : new Exception(message);

            // Firebase Crash Reporting
            FirebaseCrash.logcat(priority, tag, message);
            FirebaseCrash.report(throwable);
        }
    }
    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return activityDispatchingAndroidInjector;
    }

    @Override
    public AndroidInjector<Service> serviceInjector() {
        return serviceDispatchingAndroidInjector;
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
    }
}
