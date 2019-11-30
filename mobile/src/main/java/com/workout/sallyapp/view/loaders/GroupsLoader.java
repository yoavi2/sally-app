package com.workout.sallyapp.view.loaders;

import android.content.Context;

import com.raizlabs.android.dbflow.list.FlowCursorList;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.workout.sallyapp.model.entities.db.GroupEntity;
import com.workout.sallyapp.model.entities.db.GroupEntity_Table;
import com.workout.sallyapp.model.entities.db.GroupEntity_UserEntity;
import com.workout.sallyapp.model.entities.db.GroupEntity_UserEntity_Table;

/**
 * Created by Yoav on 27-Apr-17.
 */
public class GroupsLoader extends BaseDbflowCursorListLoader<GroupEntity> {

    private final Long mUserID;

    public GroupsLoader(Context context, Long userID) {
        super(context, GroupEntity.class);
        mUserID = userID;
    }

    @Override
    public FlowCursorList<GroupEntity> loadInBackground() {
        mData = SQLite.select()
                .from(GroupEntity.class)
                .leftOuterJoin(GroupEntity_UserEntity.class)
                .on(GroupEntity_UserEntity_Table.groupEntity_id.eq(GroupEntity_Table.id))
                .where(GroupEntity_UserEntity_Table.userEntity_serverId.is(mUserID))
                .cursorList();


        return mData;
    }
}
