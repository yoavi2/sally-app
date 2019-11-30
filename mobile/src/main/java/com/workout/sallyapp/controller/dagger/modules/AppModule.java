package com.workout.sallyapp.controller.dagger.modules;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Yoav on 19-Jul-17.
 */
@Module(includes = {SallySharedPrefModule.class,
        NetModule.class,
        SallyNetModule.class,
        ErrorUtilsModule.class,
        SharedPrefModule.class,
        SallySharedPrefModule.class,
        SallyDBModule.class})
public class AppModule {

    @Provides
    @Singleton
    Context providesApplication(Application application) {
        return application.getApplicationContext();
    }
}
