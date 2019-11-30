package com.workout.sallyapp.controller.retrofit.apis;

import android.support.annotation.Nullable;

import com.workout.sallyapp.model.entities.db.GroupEntity;
import com.workout.sallyapp.model.entities.db.ScoreEntity;
import com.workout.sallyapp.model.entities.db.UserEntity;
import com.workout.sallyapp.model.entities.network.UserPref;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Yoav on 13-Apr-17.
 */
public interface UserAPI {

    @GET("users/{id}")
    Call<UserEntity> getUser(@Path("id") Long userId);

    @POST("users/")
    Call<Long> createUser(@Body UserEntity user);

    @GET("users/{id}/groups")
    Call<List<GroupEntity>> getUserGroups(@Path("id") Long userId);

    @GET("users/highscores")
    Call<List<ScoreEntity>> getUsersHighscores(@Query("id") List<Long> userIds);

    @GET("users/{id}/scores")
    Call<List<ScoreEntity>> getUserScores(@Path("id") Long userId, @Nullable @Query("timestamp") Long fromTimestamp);

    @DELETE("users/{id}/scores/{scoreId}")
    Call<Void> deleteUserScore(@Path("id") Long userId, @Path("scoreId") Long scoreId);

    @POST("users/{id}/token")
    Call<Void> refreshToken(@Path("id") Long userId, @Body String token);

    @POST("users/{id}/pref")
    Call<Void> updatePrefs(@Path("id") Long userId, @Body UserPref userPref);
}
