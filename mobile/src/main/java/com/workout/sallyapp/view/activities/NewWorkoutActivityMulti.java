package com.workout.sallyapp.view.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.workout.sallyapp.R;
import com.workout.sallyapp.controller.retrofit.apis.WorkoutAPI;
import com.workout.sallyapp.controller.retrofit.error_handling.APIError;
import com.workout.sallyapp.controller.retrofit.error_handling.ErrorUtils;
import com.workout.sallyapp.databinding.ActivityNewWorkoutMultiBinding;
import com.workout.sallyapp.model.entities.db.ScoreEntity;
import com.workout.sallyapp.model.entities.db.UserEntity;
import com.workout.sallyapp.model.entities.network.Workout;
import com.workout.sallyapp.model.entities.view.WorkoutSessionScore;
import com.workout.sallyapp.model.repository.interfaces.RepositoryTransactionEvent;
import com.workout.sallyapp.model.workout.session.UserWorkoutSessionUpdater;
import com.workout.sallyapp.utilities.ViewUtility;
import com.workout.sallyapp.view.activities.base.BaseNewWorkoutActivity;
import com.workout.sallyapp.view.adapters.UserWorkoutStateAdapter;
import com.workout.sallyapp.view.ui.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.firebase.ui.auth.util.Preconditions.checkNotNull;

public class NewWorkoutActivityMulti extends BaseNewWorkoutActivity implements UserWorkoutStateAdapter.OnUserCardClick {

    private static final String EXTRA_USER_LIST = "EXTRA_USER_LIST";

    @Inject
    WorkoutAPI mWorkoutApi;
    @Inject
    ErrorUtils mErrorUtils;

    private ActivityNewWorkoutMultiBinding bindingActivity;

    private RecyclerView mRecyclerView;
    private UserWorkoutStateAdapter mAdapter;
    private List<WorkoutSessionScore> mUserList;

    // Services
    private UserWorkoutSessionUpdater mUserWorkoutChangePublisher;

    private int mCurrentActiveUsers = 0;

    public static Intent createIntent(@NonNull Context context, ArrayList<UserEntity> users) {
        Intent intent = new Intent(checkNotNull(context, "context cannot be null"), NewWorkoutActivityMulti.class);
        intent.putParcelableArrayListExtra(EXTRA_USER_LIST, users);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!getIntent().hasExtra(EXTRA_USER_LIST)) {
            throw new RuntimeException("Cannot open ChooseGroupUsersActivity with not group extra");
        }

        AndroidInjection.inject(this);

        ArrayList<UserEntity> userEntities = getIntent().getParcelableArrayListExtra(EXTRA_USER_LIST);
        mUserList = new ArrayList<>(userEntities.size());
        for (UserEntity userEntity : userEntities) {
            mUserList.add(new WorkoutSessionScore(userEntity, null, false));
        }

        mUserWorkoutChangePublisher = new UserWorkoutSessionUpdater(mCurrentChallenge);

        // Users state recycler
        mRecyclerView = bindingActivity.contentNewWorkoutMulti.newWorkoutMultiRecyclerview;

        mAdapter = new UserWorkoutStateAdapter(this, mUserList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, ViewUtility.dpToPx(this, 4), true));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        // Loading
        mRecyclerView.setAlpha(0.8f);
    }

    @Override
    protected void initializeComponents() {
        // Data binding
        bindingActivity = DataBindingUtil.setContentView(this, R.layout.activity_new_workout_multi);

        setSupportActionBar(bindingActivity.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mYoutubePlayerView = bindingActivity.contentNewWorkoutMulti.newWorkoutMultiYoutubePlayer;
        mBottomButton = bindingActivity.contentNewWorkoutMulti.newWorkoutMultiBottomButton;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onVideoLoaded() {
        super.onVideoLoaded();
    }

    @Override
    public void onVideoStarted() {
        super.onVideoStarted();

        // Graphic changes
        mRecyclerView.setAlpha(1);

        // Set all users to active
        for (WorkoutSessionScore workoutSessionScore : mUserList) {
            workoutSessionScore.isActive = true;
        }

        mCurrentActiveUsers = mUserList.size();
        mAdapter.notifyDataSetChanged();

        // Listen for click on user cards
        mAdapter.setUserCardClickListener(this);
    }

    protected void workoutEnded() {
        super.workoutEnded();

        mBottomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveScores(mUserList);
                navigateUpToParent();
            }
        });
    }

    @Override
    public void onVideoEnded() {
            // Update all users - finished
        for (WorkoutSessionScore workoutSessionScore : mUserList) {
            mUserWorkoutChangePublisher.finished(workoutSessionScore, (int) mPlayer.getVideoLength(), new UserWorkoutSessionUpdater.NumberOfActiveUsersChanged() {
                @Override
                public void userBecameActive(UserEntity user) {
                }

                @Override
                public void userBecameInactive(UserEntity user) {
                    mCurrentActiveUsers--;
                }
            });

            mAdapter.notifyDataSetChanged();
        }

        super.onVideoEnded();
    }

    private void saveScores(List<WorkoutSessionScore> workoutSessionScores) {

        if (workoutSessionScores != null && workoutSessionScores.size() > 0) {

            // We only need the scores themselves
            ArrayList<ScoreEntity> scores = new ArrayList<>(workoutSessionScores.size());

            for (WorkoutSessionScore workoutSessionScore : workoutSessionScores) {
                if (workoutSessionScore.score != null) {
                    scores.add(workoutSessionScore.score);
                }
            }

            Workout workout = new Workout(mCurrentChallenge.serverId, mCurrentUser.serverId, new Date(), scores);

            // Send to server
            Call<Workout> call = mWorkoutApi.postWorkout(workout);
            call.enqueue(new Callback<Workout>() {
                @Override
                public void onResponse(Call<Workout> call, Response<Workout> response) {
                    if (response.isSuccessful()) {
                        // Save To db
                        mWorkoutRepository.saveWorkout(response.body(), new RepositoryTransactionEvent() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError(Throwable error) {
                            }
                        });
                    } else {
                        APIError error = mErrorUtils.parseError(response);
                        Timber.e(error.message());
                    }
                }

                @Override
                public void onFailure(Call<Workout> call, Throwable t) {
                    Timber.e(t.toString());
                }
            });
        }
    }

    @Override
    public void onUserCardClick(int position) {
        mUserWorkoutChangePublisher.userToggledState(mUserList.get(position), (int) mCurrentSecond, mPlayer.isPaused(), new UserWorkoutSessionUpdater.NumberOfActiveUsersChanged() {
            @Override
            public void userBecameActive(UserEntity user) {
                mCurrentActiveUsers++;
            }

            @Override
            public void userBecameInactive(UserEntity user) {

                mCurrentActiveUsers--;

                // Check if workout has ended
                if (mCurrentActiveUsers == 0) {
                    workoutEnded();
                }
            }
        });
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void onBackPressed() {
        navigateUpToParent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                navigateUpToParent();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void navigateUpToParent() {
        Intent intent = NavUtils.getParentActivityIntent(this);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        NavUtils.navigateUpTo(this, intent);
    }
}
