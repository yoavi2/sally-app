package com.workout.sallyapp.view.video;

public interface VideoPlayerListener {
    void onVideoLoaded();
    void onVideoStarted();
    void onVideoEnded();
    void onReady();
    void onError(String message);
    void onSecondsChanged(float seconds);
    void onVideoDurationChanged(float videoDuration);
}
