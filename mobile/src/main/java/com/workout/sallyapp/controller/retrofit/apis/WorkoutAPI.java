package com.workout.sallyapp.controller.retrofit.apis;

import com.workout.sallyapp.model.entities.network.Workout;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Yoav on 13-Apr-17.
 */

public interface WorkoutAPI {
    @POST("workouts/")
    Call<Workout> postWorkout(@Body Workout workout);
}
