package com.workout.sallyapp.view.video;

import android.view.View;

import com.pierfrancescosoffritti.youtubeplayer.YouTubePlayer;
import com.pierfrancescosoffritti.youtubeplayer.YouTubePlayerView;

import timber.log.Timber;

/**
 * Created by Yoav
 */
public class YouTubeVideoPlayer implements VideoPlayer {

    private boolean mWasPlayed;
    private VideoPlayerListener mListener;
    private float mCurrentNumberOfSeconds;
    private float mVideoLength;
    private boolean mIsEnded;
    private boolean mIsPaused;
    private boolean mIsLoaded;
    private YouTubePlayerView mYouTubePlayerView;

    public YouTubeVideoPlayer(VideoPlayerListener listener, YouTubePlayerView youTubePlayerView) {
        mListener = listener;
        mCurrentNumberOfSeconds = 0;
        mVideoLength = 0;
        mIsEnded = false;
        mIsPaused = true;
        mIsLoaded = false;
        mWasPlayed = false;
        mYouTubePlayerView = youTubePlayerView;

        YouTubePlayer.YouTubeListener youTubeListener = createYoutubteListener(mListener);
        mYouTubePlayerView.initialize(youTubeListener, true);
        mYouTubePlayerView.onFullScreenButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do nothing
            }
        });
        mYouTubePlayerView.showTitle(false);
    }

    private YouTubePlayer.YouTubeListener createYoutubteListener(final VideoPlayerListener listener){
        return new YouTubePlayer.YouTubeListener() {
            @Override
            public void onReady() {
                listener.onReady();
            }

            @Override
            public void onStateChange(int i) {
                switch (i) {
                    case (YouTubePlayer.State.BUFFERING):
                        break;
                    case (YouTubePlayer.State.UNSTARTED):
                        break;
                    case (YouTubePlayer.State.PLAYING):
                        mIsEnded = false;
                        mIsPaused = false;

                        if(!mIsLoaded) {
                            listener.onVideoLoaded();
                            mIsLoaded = true;
                        }
                        else if(!mWasPlayed) {
                            listener.onVideoStarted();
                            mWasPlayed = true;
                        }
                        break;

                    case (YouTubePlayer.State.PAUSED):
                        mIsPaused = true;
                        break;
                    case (YouTubePlayer.State.ENDED):
                        mIsEnded = true;
                        mIsPaused = false;
                        // Check if started playing
                        if(mWasPlayed)
                            listener.onVideoEnded();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPlaybackQualityChange(int i) {}

            @Override
            public void onPlaybackRateChange(double v) {}

            @Override
            public void onError(int i) {
                listener.onError(String.valueOf(i));
            }

            @Override
            public void onApiChange() {}

            @Override
            public void onCurrentSecond(float v) {
                mCurrentNumberOfSeconds = v;
                listener.onSecondsChanged(v);
            }

            @Override
            public void onVideoDuration(float v) {
                mVideoLength = v;
                listener.onVideoDurationChanged(v);
            }

            @Override
            public void onLog(String s) {}

            @Override
            public void onVideoTitle(String s) {}

            @Override
            public void onVideoId(String s) {

            }
        };
    }

    @Override
    public float getCurrentSecond() {
        return mCurrentNumberOfSeconds;
    }

    @Override
    public float getVideoLength() {
        return mVideoLength;
    }

    @Override
    public boolean isEnded() {
        return mIsEnded;
    }

    @Override
    public boolean isPaused() {
        return mIsPaused;
    }

    @Override
    public void setEventListener(VideoPlayerListener listener) {
        mListener = listener;
    }

    @Override
    public void loadVideo(String video) {
        //TODO: Change start second if needed
        mYouTubePlayerView.loadVideo(video, 0);
    }

    @Override
    public void play() {
        mYouTubePlayerView.playVideo();
        mIsPaused = false;
    }

    @Override
    public void pause() {
        mYouTubePlayerView.pauseVideo();
        mIsPaused = true;
    }
}

