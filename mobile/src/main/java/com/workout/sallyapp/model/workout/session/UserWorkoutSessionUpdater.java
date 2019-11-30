package com.workout.sallyapp.model.workout.session;

import com.workout.sallyapp.model.entities.db.ChallengeEntity;
import com.workout.sallyapp.model.entities.db.UserEntity;
import com.workout.sallyapp.model.entities.view.WorkoutSessionScore;
import com.workout.sallyapp.model.utils.ScoreUtility;

/**
 * Created by Yoav
 */
public class UserWorkoutSessionUpdater {

    private ChallengeEntity mChallenge;

    public UserWorkoutSessionUpdater(ChallengeEntity mChallenge) {
        this.mChallenge = mChallenge;
    }

    public void userToggledState(WorkoutSessionScore session, int seconds, boolean isPaused, NumberOfActiveUsersChanged listener){

        if (!session.isFinished) {
            publishUserStateChange(session, seconds, isPaused, listener);
            session.isActive = !session.isActive;
        }
    }

    public void finished(WorkoutSessionScore session, int seconds ,NumberOfActiveUsersChanged listener){
        if (!session.isActive)
            return;

        session.score = ScoreUtility.createNow(mChallenge, session.user, seconds);
        session.isFinished = true;
        session.isActive = false;
        listener.userBecameInactive(session.user);
    }

    private void publishUserStateChange(WorkoutSessionScore session, int seconds, boolean isPaused, NumberOfActiveUsersChanged listener){
        if (!session.isActive) {
            listener.userBecameActive(session.user);
            return;
        }

        // Do not save scores when video is paused - Cause for bugs (incorrect mCurrentSeconds etc)
        if (!isPaused)
            session.score = ScoreUtility.createNow(mChallenge, session.user, seconds);

        listener.userBecameInactive(session.user);
    }

    public interface NumberOfActiveUsersChanged {
        void userBecameActive(UserEntity user);
        void userBecameInactive(UserEntity user);
    }
}
