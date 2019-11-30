package com.workout.sallyapp.controller.retrofit.apis;

import com.workout.sallyapp.controller.retrofit.responses.CreateGroupResponse;
import com.workout.sallyapp.model.entities.db.GroupEntity;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Yoav on 13-Apr-17.
 */
public interface GroupAPI {
    @POST("groups/")
    Call<CreateGroupResponse> createGroup(@Body GroupEntity group);

    @POST("groups/{groupId}/members")
    Call<Void> joinGroup(@Path("groupId") Long groupId, @Field("userID") Long userId);

    @FormUrlEncoded
    @POST("groups/url/{groupLink}")
    Call<GroupEntity> joinGroupLink(@Path("groupLink") String groupLink, @Field("userId") Long userId);

    @DELETE("groups/{groupId}/members/{userId}")
    Call<Void> exitGroup(@Path("groupId") Long groupId, @Path("userId") Long userId);
}
