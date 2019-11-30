package com.workout.sallyapp.controller.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Yoav on 04-May-17.
 */

public class CreateGroupResponse {
    @Expose
    @SerializedName("groupId")
    public Long groupServerId;

    @Expose
    @SerializedName("groupInviteUrl")
    public String groupInviteUrl;
}
