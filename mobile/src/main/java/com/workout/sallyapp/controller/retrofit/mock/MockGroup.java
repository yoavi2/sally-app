package com.workout.sallyapp.controller.retrofit.mock;

import com.workout.sallyapp.controller.retrofit.apis.GroupAPI;
import com.workout.sallyapp.controller.retrofit.responses.CreateGroupResponse;
import com.workout.sallyapp.model.entities.db.GroupEntity;
import com.workout.sallyapp.model.entities.db.UserEntity;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.Path;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.Calls;

import static com.workout.sallyapp.controller.retrofit.mock.MockDatabase.mGroupId;

/**
 * Created by Yoav on 04-May-17.
 */

public class MockGroup implements GroupAPI {

    private final BehaviorDelegate<GroupAPI> mDelegate;

    public MockGroup(BehaviorDelegate<GroupAPI> delegate) {
        this.mDelegate = delegate;
    }

    @Override
    public Call<CreateGroupResponse> createGroup(@Body GroupEntity group) {
        GroupEntity newGroup = new GroupEntity(group.name, group.createdBy);
        newGroup.serverId = mGroupId++;
        ArrayList<UserEntity> users = new ArrayList<>();
        users.add(group.createdBy);
        newGroup.setUsers(users);

        CreateGroupResponse response = new CreateGroupResponse();
        response.groupServerId = newGroup.serverId;
        response.groupInviteUrl = "WwW.gogo.com" + mGroupId;

        return mDelegate.returningResponse(response).createGroup(group);
    }

    @Override
    public Call<Void> joinGroup(@Path("groupId") Long groupId, @Field("userId") Long userId) {
        GroupEntity group = getGroupById(groupId);
        UserEntity user = getUserById(userId);

        if (group == null || user == null) {
            // TODO: Should not return that... but whatever
            return Calls.failure(new IOException("No such user or group"));
        }

        if (!group.getUsers().contains(user)) {
            group.getUsers().add(user);
        }

        return mDelegate.returningResponse(null).joinGroup(groupId, userId);

    }

    @Override
    public Call<GroupEntity> joinGroupLink(@Path("groupLink") String groupLink, @Field("userID") Long userId) {
        return null;
    }

    @Override
    public Call<Void> exitGroup(@Path("groupId") Long groupId, @Field("userId") Long userId) {
        GroupEntity group = getGroupById(groupId);
        UserEntity user = getUserById(userId);

        if (group == null || user == null) {
            // TODO: Should not return that... but whatever
            return Calls.failure(new IOException("No such user or group"));
        }

        group.getUsers().remove(user);

        return mDelegate.returningResponse(null).joinGroup(groupId, userId);
    }

    private UserEntity getUserById(Long userId) {
        for (UserEntity user : MockDatabase.mUsers) {
            if (user.serverId.equals(userId)) {
                return user;
            }
        }

        return null;
    }

    private GroupEntity getGroupById(Long groupID) {
        for (GroupEntity group : MockDatabase.mGroups) {
            if (group.serverId.equals(groupID)) {
                return group;
            }
        }

        return null;
    }
}
