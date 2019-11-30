package com.workout.sallyapp.view.activities.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;

import com.pierfrancescosoffritti.youtubeplayer.YouTubePlayerView;
import com.workout.sallyapp.R;
import com.workout.sallyapp.model.repository.entity_repositories.WorkoutRepository;
import com.workout.sallyapp.view.video.VideoPlayer;
import com.workout.sallyapp.view.video.VideoPlayerListener;
import com.workout.sallyapp.view.video.YouTubeVideoPlayer;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import timber.log.Timber;

/**
 * Created by Yoav on 07-Apr-17.
 */
public abstract class BaseNewWorkoutActivity extends BaseSallyActivity
        implements VideoPlayerListener {

    @Inject
    protected WorkoutRepository mWorkoutRepository;

    protected YouTubePlayerView mYoutubePlayerView;
    protected VideoPlayer mPlayer;
    protected Button mBottomButton;

    protected boolean mIsLoaded = false;
    protected boolean mIsFinished = false;
    protected float mCurrentSecond;

    protected abstract void initializeComponents();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidInjection.inject(this);

        initializeActivity();
    }

    protected void initializeActivity() {
        initializeComponents();

        // Initialize player
        mPlayer = new YouTubeVideoPlayer(this, mYoutubePlayerView);

        // Loading
        mBottomButton.setVisibility(View.VISIBLE);

        // Play button
        mBottomButton.setBackgroundColor(
                ContextCompat.getColor(this, android.R.color.darker_gray));

    }

    protected void workoutEnded() {
        mPlayer.pause();

        mBottomButton.setVisibility(View.VISIBLE);
        mBottomButton.setText(R.string.save_results);
    }

    @Override
    protected void onDestroy() {
        mBottomButton.setOnClickListener(null);
        mYoutubePlayerView.release();
        super.onDestroy();
    }

    @Override
    public void onVideoLoaded() {
        mPlayer.pause();
        mIsLoaded = true;

        // Make play button work
        mBottomButton.setBackgroundColor(
                ContextCompat.getColor(this, R.color.colorPrimary));
        mBottomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsLoaded && mYoutubePlayerView != null) {
                    mPlayer.play();
                }
            }
        });
    }

    @Override
    public void onVideoStarted() {
        // Graphic changes
        mBottomButton.setVisibility(View.GONE);
        mBottomButton.setOnClickListener(null);
    }

    @Override
    public void onVideoEnded() {
        mIsFinished = true;
        workoutEnded();
    }

    //region Youtube methods
    @Override
    public void onReady() {
        mPlayer.loadVideo(getString(R.string.sally_up_youtube));
    }


//    /**
//     * On current second of video
//     *
//     * @param seconds - also fractions of second (not a whole number)
//     */
    @Override
    public void onSecondsChanged(float seconds) {
        mCurrentSecond = seconds;
    }

    @Override
    public void onVideoDurationChanged(float videoDuration) {}

    @Override
    public void onError(String message) {
        Timber.e(message);
    }

    //endregion
}
