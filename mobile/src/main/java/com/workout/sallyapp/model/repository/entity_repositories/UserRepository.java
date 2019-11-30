package com.workout.sallyapp.model.repository.entity_repositories;

import android.support.annotation.Nullable;

import com.f2prateek.rx.preferences2.Preference;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLOperator;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Update;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.workout.sallyapp.controller.dagger.modules.SallySharedPrefModule;
import com.workout.sallyapp.model.databases.SallyDatabase;
import com.workout.sallyapp.model.entities.db.UserEntity;
import com.workout.sallyapp.model.entities.db.UserEntity_Table;
import com.workout.sallyapp.model.repository.entity_repositories.base.BaseDbRepository;
import com.workout.sallyapp.model.repository.interfaces.DbTransactionCreator;
import com.workout.sallyapp.model.repository.interfaces.RepositoryTransactionEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by Yoav on 24-Apr-17.
 */
public class UserRepository extends BaseDbRepository {

    Preference<Long> mCurrentUserID;
    Preference<String> mCurrentFirebaseUserID;
    Preference<String> mCurrentUserJWT;

//    private final Context mContext;
    private UserEntity mCurrentUser;

    @Inject
    public UserRepository(@Named(SallySharedPrefModule.NAMED_USER_ID) Preference<Long> currentUserID,
                          @Named(SallySharedPrefModule.NAMED_FIREBASE_USER_ID) Preference<String> currentFirebaseUserID,
                          @Named(SallySharedPrefModule.NAMED_USER_JWT) Preference<String> currentUserJWT) {
        super();
        this.mCurrentUserID = currentUserID;
        this.mCurrentFirebaseUserID = currentFirebaseUserID;
        this.mCurrentUserJWT = currentUserJWT;
//        mContext = context;

//        ((MyApplication) application).getSallySharedPrefComponent().inject(this);
    }

    public void saveUser(final UserEntity user, @Nullable final RepositoryTransactionEvent listener) {
        mDbExecutor.execute(SallyDatabase.class, new DbTransactionCreator() {
            @Override
            public ITransaction create() {
                return new ITransaction() {
                    @Override
                    public void execute(DatabaseWrapper databaseWrapper) {
                        saveUserSync(user);
                    }
                };
            }
        }, listener);
    }

    public void saveUserSync(final UserEntity user) {

        // Perform update or insert in order to not overwrite JWT
        if (user.exists()) {
            OperatorGroup primaryConditions = FlowManager.getModelAdapter(UserEntity.class).getPrimaryConditionClause(user);

            Update update = SQLite.update(UserEntity.class);

            final List<SQLOperator> sqlOperators = new ArrayList<SQLOperator>(
                    Arrays.asList(
                            UserEntity_Table.displayName.eq(user.displayName),
                            UserEntity_Table.serverId.eq(user.serverId),
                            UserEntity_Table.email.eq(user.email),
                            UserEntity_Table.firebaseUId.eq(user.firebaseUId),
                            UserEntity_Table.photoUrl.eq(user.photoUrl)));

            if (user.jwt != null) {
                sqlOperators.add(UserEntity_Table.jwt.eq(user.jwt));
            }

            update.set((SQLOperator[]) sqlOperators.toArray(new SQLOperator[sqlOperators.size()]))
                    .where(primaryConditions)
                    .execute();
        } else {
            user.insert();
        }
    }

    public Long getCurrentUserId() {
        return mCurrentUserID.get();
    }

    public void setCurrentUserID(Long id) {
        mCurrentUserID.set(id);
    }

    public String getCurrentFirebaseUserId() {
        return mCurrentFirebaseUserID.get();
    }

    public void setCurrentFirebaseUserID(String id) {
        mCurrentFirebaseUserID.set(id);
    }

    public String getUserJWT() {
        return mCurrentUserJWT.get();
    }

    public void setUserJWT(String jwt) {
        mCurrentUserJWT.set(jwt);
    }

    // Synchronous
    public UserEntity getUserSync(Long id) {
        return SQLite.select().from(UserEntity.class).where(UserEntity_Table.serverId.is(id)).querySingle();
    }

    // Synchronous
    public UserEntity getUserbyFirebaseIdSync(String firebaseId) {
        return SQLite.select().from(UserEntity.class).where(UserEntity_Table.firebaseUId.is(firebaseId)).querySingle();
    }

    // Synchronous
    public UserEntity getCurrentUserSync() {
        Long currID = getCurrentUserId();

        if (mCurrentUser == null || !mCurrentUser.serverId.equals(currID)) {
            mCurrentUser = getUserSync(currID);
        }

        return mCurrentUser;
    }


}
