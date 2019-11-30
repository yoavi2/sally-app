package com.workout.sallyapp.view.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.CircleProgress;
import com.workout.sallyapp.R;
import com.workout.sallyapp.controller.retrofit.apis.WorkoutAPI;
import com.workout.sallyapp.controller.retrofit.error_handling.APIError;
import com.workout.sallyapp.controller.retrofit.error_handling.ErrorUtils;
import com.workout.sallyapp.databinding.ActivityNewWorkoutSingleBinding;
import com.workout.sallyapp.model.entities.db.ScoreEntity;
import com.workout.sallyapp.model.entities.network.Workout;
import com.workout.sallyapp.model.utils.ScoreUtility;
import com.workout.sallyapp.utilities.TimeUtility;
import com.workout.sallyapp.view.activities.base.BaseNewWorkoutActivity;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import q.rorbin.badgeview.QBadgeView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class NewWorkoutActivitySingle extends BaseNewWorkoutActivity {

    private static final String TAG = "NewWorkoutActivitySingle";

    @Inject
    WorkoutAPI mWorkoutAPI;
    @Inject
    ErrorUtils mErrorUtils;

    private ActivityNewWorkoutSingleBinding bindingActivity;
    private CircleProgress mCircleProgress;
    private TextView mTextProgress;
    private TextView mTextStop;
    private int mVideoLength;
    private boolean mHasEnded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidInjection.inject(this);
    }

    @Override
    protected void initializeComponents() {
        // Data binding
        bindingActivity = DataBindingUtil.setContentView(this, R.layout.activity_new_workout_single);

        setSupportActionBar(bindingActivity.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mYoutubePlayerView = bindingActivity.contentNewWorkoutSingle.newWorkoutSingleYoutubePlayer;
        mBottomButton = bindingActivity.contentNewWorkoutSingle.newWorkoutSingleBottomButton;
        mCircleProgress = bindingActivity.contentNewWorkoutSingle.newWorkoutSingleProgress;
        mTextProgress = bindingActivity.contentNewWorkoutSingle.newWorkoutSingleProgressText;
        mTextStop = bindingActivity.contentNewWorkoutSingle.newWorkoutSingleProgressStopText;
    }

    @Override
    protected void onDestroy() {
        mCircleProgress.setOnClickListener(null);

        super.onDestroy();
    }

    @Override
    public void onVideoLoaded() {
        super.onVideoLoaded();
    }

    @Override
    public void onVideoStarted() {
        super.onVideoStarted();

        mTextStop.setVisibility(View.VISIBLE);

        mCircleProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Show some visual indication that there is a saved score?
                workoutEnded();
            }
        });
    }

    protected void workoutEnded() {
        super.workoutEnded();

        if (!mHasEnded) {
            mHasEnded = true;

            mTextStop.setVisibility(View.INVISIBLE);
            mBottomButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int seconds = mIsFinished ? mVideoLength : (int) mCurrentSecond;
                    ScoreEntity score = ScoreUtility.createNow(mCurrentChallenge, mCurrentUser, seconds);
                    saveScore(score);

                    finish();
                }
            });
        }
    }

    @Override
    public void onVideoEnded() {
        setDonutProgressMax();

        new QBadgeView(this).bindTarget(mCircleProgress)
                .setBadgeBackgroundColor(ContextCompat.getColor(this, R.color.youtube_red))
                .setBadgeTextColor(ContextCompat.getColor(this, android.R.color.white))
                .setBadgeTextSize(24, true)
                .setBadgePadding(12, true)
                .setBadgeGravity(Gravity.CENTER)
                .setGravityOffset(0, true)
                .setBadgeText(this.getString(R.string.finished));

        super.onVideoEnded();
    }

    private void setDonutProgressMax() {
        setDonutProgress(mVideoLength);
    }

    private void setDonutProgress(int progress) {
        mCircleProgress.setProgress(progress);
        mTextProgress.setText(TimeUtility.formatSeconds(progress));
    }

    @Override
    public void onSecondsChanged(float second) {
        super.onSecondsChanged(second);

        if (!mIsFinished) {
            setDonutProgress((int) mCurrentSecond);
        }
    }

    @Override
    public void onVideoDurationChanged(float v) {
        mVideoLength = (int) v;
        mCircleProgress.setMax(mVideoLength);
    }

    private void saveScore(final ScoreEntity score) {
        Workout workout = new Workout(mCurrentChallenge.serverId, score.user.serverId, new Date(), new ArrayList<ScoreEntity>() {{
            add(score);
        }});

        // Send to server
        Call<Workout> call = mWorkoutAPI.postWorkout(workout);
        call.enqueue(new Callback<Workout>() {
            @Override
            public void onResponse(Call<Workout> call, Response<Workout> response) {
                if (response.isSuccessful()) {
                    // Save To db
                    mWorkoutRepository.saveWorkout(response.body(), null);
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
