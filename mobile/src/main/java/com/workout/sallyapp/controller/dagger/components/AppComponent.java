package com.workout.sallyapp.controller.dagger.components;

import android.app.Application;

import com.workout.sallyapp.controller.dagger.modules.AppModule;
import com.workout.sallyapp.controller.dagger.modules.ModuleBuilder;
import com.workout.sallyapp.controller.dagger.qualifiers.BaseUrlQualifier;
import com.workout.sallyapp.view.MyApplication;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

/**
 * Created by Yoav on 31-Jul-17.
 */
@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        AppModule.class,
        ModuleBuilder.class})
public interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);
        @BindsInstance
        Builder withBaseUrl(@BaseUrlQualifier String baseUrl);

        AppComponent build();
    }

    void inject(MyApplication app);
}
