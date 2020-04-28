package com.workout.sallyapp.model.repository.entity_repositories;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.workout.sallyapp.model.entities.db.GroupEntity;
import com.workout.sallyapp.model.entities.db.GroupEntity_Table;
import com.workout.sallyapp.model.entities.db.GroupEntity_UserEntity;
import com.workout.sallyapp.model.entities.db.GroupEntity_UserEntity_Table;

/**
 * Created by Yoav on 29-Apr-17.
 */
public class GroupUserRepository {

    public void saveGroupUserSync(GroupEntity_UserEntity groupEntityUserEntity) {
        // Check if needed before saving
        // TODO: see if can be replaced with ConflictAction.REPLACE
        if(!isUserMemberSync(groupEntityUserEntity.getGroupEntity().serverId,
                            groupEntityUserEntity.getUserEntity().serverId)) {
            groupEntityUserEntity.save();
        }
    }

    public void deleteGroupSync(GroupEntity group) {
        SQLite.delete(GroupEntity_UserEntity.class)
                .where(GroupEntity_UserEntity_Table.groupEntity_id.is(group.id))
                .execute();
    }

    public void deleteGroupUser(long groupId, long userId) {
        SQLite.delete(GroupEntity_UserEntity.class)
                .where(GroupEntity_UserEntity_Table.groupEntity_id.is(groupId))
                .and(GroupEntity_UserEntity_Table.userEntity_serverId.is(userId))
                .execute();
    }

    public boolean isUserMemberSync(long groupId, long userId) {
        return SQLite.selectCountOf()
                .from(GroupEntity_UserEntity.class)
                .leftOuterJoin(GroupEntity.class)
                .on(GroupEntity_Table.id.eq(GroupEntity_UserEntity_Table.groupEntity_id))
                .where(GroupEntity_UserEntity_Table.userEntity_serverId.is(userId))
                .and(GroupEntity_Table.serverId.is(groupId))
                .hasData();
    }
}
