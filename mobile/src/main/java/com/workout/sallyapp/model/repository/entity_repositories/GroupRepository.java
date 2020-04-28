package com.workout.sallyapp.model.repository.entity_repositories;

import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.workout.sallyapp.model.databases.SallyDatabase;
import com.workout.sallyapp.model.entities.db.GroupEntity;
import com.workout.sallyapp.model.entities.db.GroupEntity_Table;
import com.workout.sallyapp.model.entities.db.GroupEntity_UserEntity;
import com.workout.sallyapp.model.entities.db.GroupEntity_UserEntity_Table;
import com.workout.sallyapp.model.entities.db.UserEntity;
import com.workout.sallyapp.model.repository.entity_repositories.base.BaseDbRepository;
import com.workout.sallyapp.model.repository.interfaces.DbTransactionCreator;
import com.workout.sallyapp.model.repository.interfaces.RepositoryTransactionEvent;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Yoav on 27-Apr-17.
 */

public class GroupRepository extends BaseDbRepository {

    UserRepository mUserRepository;
    GroupUserRepository mGroupUserRepository;

    @Inject
    public GroupRepository(UserRepository userRepository, GroupUserRepository groupUserRepository) {
        super();

        mUserRepository = userRepository;
        mGroupUserRepository = groupUserRepository;
    }

    public GroupEntity getByServerId(long groupServerId) {
        return SQLite.select()
                .from(GroupEntity.class)
                .where(GroupEntity_Table.serverId.is(groupServerId))
                .querySingle();
    }

    public void saveGroup(final GroupEntity group, @Nullable final RepositoryTransactionEvent listener) {
        mDbExecutor.execute(SallyDatabase.class, new DbTransactionCreator() {
            @Override
            public ITransaction create() {
                return new ITransaction() {
                    @Override
                    public void execute(DatabaseWrapper databaseWrapper) {
                        saveGroupSync(group);
                    }
                };
            }
        }, listener);
    }

    public void saveGroupSync(final GroupEntity group) {
        GroupEntity groupEntity = null;

        if (group.serverId != null) {
            groupEntity = getByServerId(group.serverId);
        }

        // Perform update or insert in order to not overwrite group id every time
        // TODO: This is a shitty way.. Check if we can make server id part of the primary key and check with exists
        if (groupEntity != null) {
            groupEntity.name = group.name;
            groupEntity.joinUrl = group.joinUrl;
            groupEntity.update();
        }
        else {
            groupEntity = group;
            groupEntity.insert();
        }

        GroupEntity_UserEntity groupEntityUserEntity = null;

        // Save all users in group and their GroupUsers
        for (UserEntity user : group.getUsers()) {
            mUserRepository.saveUserSync(user);

            groupEntityUserEntity = new GroupEntity_UserEntity();
            groupEntityUserEntity.setGroupEntity(groupEntity);
            groupEntityUserEntity.setUserEntity(user);
            mGroupUserRepository.saveGroupUserSync(groupEntityUserEntity);
        }

        // Delete all other nonexistent users (Probably left the group)
        deleteNonexistentGroupUsersSync(groupEntity, group);
    }

    public long getGroupCountSync(long userId) {
        return SQLite.selectCountOf()
                .from(GroupEntity.class)
                .leftOuterJoin(GroupEntity_UserEntity.class)
                .on(GroupEntity_UserEntity_Table.groupEntity_id.eq(GroupEntity_Table.id))
                .where(GroupEntity_UserEntity_Table.userEntity_serverId.is(userId))
                .count();
    }

    public void saveGroups(final List<GroupEntity> groups, @Nullable final RepositoryTransactionEvent listener) {
        mDbExecutor.execute(SallyDatabase.class, new DbTransactionCreator() {
            @Override
            public ITransaction create() {
                return new ProcessModelTransaction.Builder<>(
                        new ProcessModelTransaction.ProcessModel<GroupEntity>() {
                            @Override
                            public void processModel(GroupEntity group, DatabaseWrapper
                                    wrapper) {
                                saveGroupSync(group);
                            }
                        }).addAll(groups).build();
            }
        }, listener);
    }

    public void deleteGroup(final GroupEntity group, @Nullable final RepositoryTransactionEvent listener) {
        mDbExecutor.execute(SallyDatabase.class, new DbTransactionCreator() {
            @Override
            public ITransaction create() {
                return new ITransaction() {
                    @Override
                    public void execute(DatabaseWrapper databaseWrapper) {
                        mGroupUserRepository.deleteGroupSync(group);
                        group.delete();
                    }
                };
            }
        }, listener);
    }

    private void deleteNonexistentGroupUsersSync(GroupEntity dbGroup, GroupEntity serverGroup) {
        if (dbGroup.getUsers().size() <= serverGroup.getUsers().size()) {
            return;
        }

        List<UserEntity> dbGroupUsers = dbGroup.getUsers();
        ArrayList<Long> groupUserIdsToDelete = new ArrayList<>(dbGroupUsers.size());

        for (UserEntity user : dbGroupUsers) {
            groupUserIdsToDelete.add(user.serverId);
        }

        for (UserEntity user : serverGroup.getUsers()) {
            groupUserIdsToDelete.remove(user.serverId);
        }

        for (Long userId : groupUserIdsToDelete) {
            mGroupUserRepository.deleteGroupUser(dbGroup.id, userId);
        }
    }
}
