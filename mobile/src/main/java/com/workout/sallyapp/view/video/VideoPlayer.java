package com.workout.sallyapp.view.video;

public interface VideoPlayer {
    float getCurrentSecond();
    float getVideoLength();
    boolean isEnded();
    boolean isPaused();
    void setEventListener(VideoPlayerListener listener);
    void loadVideo(String video);
    void play();
    void pause();
}



