package com.workout.sallyapp.model.entities.view;

import com.workout.sallyapp.model.entities.db.ScoreEntity;
import com.workout.sallyapp.model.entities.db.UserEntity;

/**
 * Created by Yoav on 31-Mar-17.
 */

public class WorkoutSessionScore {
    public UserEntity user;
    public ScoreEntity score;
    public boolean isActive;
    public boolean isFinished = false;

    public WorkoutSessionScore(UserEntity user, ScoreEntity score, boolean isActive) {
        this.user = user;
        this.score = score;
        this.isActive = isActive;
    }
}
