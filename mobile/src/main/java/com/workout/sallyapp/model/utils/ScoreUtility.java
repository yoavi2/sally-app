package com.workout.sallyapp.model.utils;

import com.workout.sallyapp.model.entities.db.ChallengeEntity;
import com.workout.sallyapp.model.entities.db.ScoreEntity;
import com.workout.sallyapp.model.entities.db.UserEntity;

import java.util.Date;

/**
 * Created by Yoav
 */
public class ScoreUtility {
    public static ScoreEntity createNow(ChallengeEntity challenge, UserEntity user, int score) {
        return create(challenge, user, score, new Date());
    }

    public static ScoreEntity create(ChallengeEntity challenge, UserEntity user, int score, Date date) {
        return new ScoreEntity(challenge, user, score, date);
    }
}