package com.workout.sallyapp.controller.dagger.modules;

import com.workout.sallyapp.controller.retrofit.apis.GroupAPI;
import com.workout.sallyapp.controller.retrofit.apis.UserAPI;
import com.workout.sallyapp.controller.retrofit.apis.WorkoutAPI;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public class SallyNetModule {

    @Provides
    @Singleton
    public UserAPI createUserService(Retrofit retrofit) {
        return retrofit.create(UserAPI.class);
    }

    @Provides
    @Singleton
    public GroupAPI createGroupService(Retrofit retrofit) {
        return retrofit.create(GroupAPI.class);
    }

    @Provides
    @Singleton
    public WorkoutAPI createWorkoutService(Retrofit retrofit) {
        return retrofit.create(WorkoutAPI.class);
    }

}
