package com.workout.sallyapp.model.repository.entity_repositories;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.sql.language.CursorResult;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;
import com.workout.sallyapp.model.databases.SallyDatabase;
import com.workout.sallyapp.model.entities.db.MaxScoreTimestampModel;
import com.workout.sallyapp.model.entities.db.ScoreEntity;
import com.workout.sallyapp.model.entities.db.ScoreEntity_Table;
import com.workout.sallyapp.model.repository.entity_repositories.base.BaseDbRepository;
import com.workout.sallyapp.model.repository.interfaces.DbTransactionCreator;
import com.workout.sallyapp.model.repository.interfaces.RepositoryTransactionEvent;

import java.util.List;

public class ScoreRepository extends BaseDbRepository {

    public interface onMaxScoreTimestampQueried {
        void deliver(Long maxTimestamp);
    }

    public ScoreRepository() {
        super();
    }

    public void saveScore(final ScoreEntity score, @Nullable final RepositoryTransactionEvent listener){
        mDbExecutor.execute(SallyDatabase.class, new DbTransactionCreator() {
            @Override
            public ITransaction create() {
                return new ITransaction() {
                    @Override
                    public void execute(DatabaseWrapper databaseWrapper) {
                        score.insert();
                    }
                };
            }
        }, listener);
    }

    public void saveScores(final List<ScoreEntity> scores, @Nullable final RepositoryTransactionEvent listener){
        mDbExecutor.execute(SallyDatabase.class, new DbTransactionCreator() {
            @Override
            public ITransaction create() {
                return new ProcessModelTransaction.Builder<>(
                        new ProcessModelTransaction.ProcessModel<ScoreEntity>() {
                            @Override
                            public void processModel(ScoreEntity score, DatabaseWrapper
                                    wrapper) {
                                saveScoreSync(score);
                            }
                        }).addAll(scores).build();
            }
        }, listener);
    }

    public void saveScoreSync(final ScoreEntity score) {
       score.save();
    }

    public void deleteScore(final ScoreEntity score, @Nullable final RepositoryTransactionEvent listener){
        mDbExecutor.execute(SallyDatabase.class, new DbTransactionCreator() {
            @Override
            public ITransaction create() {
                return new ITransaction() {
                    @Override
                    public void execute(DatabaseWrapper databaseWrapper) {
                        score.delete();
                    }
                };
            }
        }, listener);
    }

    public void getLatestScore(Long userID, final onMaxScoreTimestampQueried listener) {
        SQLite.select(Method.max(ScoreEntity_Table.date).as(MaxScoreTimestampModel.COLUMN_TIMESTAMP))
                .from(ScoreEntity.class)
                .where(ScoreEntity_Table.user_serverId.is(userID))
                .async()
                .queryResultCallback(new QueryTransaction.QueryResultCallback<ScoreEntity>() {
                    @Override
                    public void onQueryResult(QueryTransaction<ScoreEntity> transaction, @NonNull CursorResult<ScoreEntity> tResult) {
                        List<MaxScoreTimestampModel> list = tResult.toCustomListClose(MaxScoreTimestampModel.class);
                        listener.deliver(list == null || list.isEmpty() ? null : list.get(0).timestamp);
                    }
                }).execute();
    }


}
