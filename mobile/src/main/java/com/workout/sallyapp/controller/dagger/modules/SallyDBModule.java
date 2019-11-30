package com.workout.sallyapp.controller.dagger.modules;

import android.app.Application;

import com.f2prateek.rx.preferences2.Preference;
import com.workout.sallyapp.model.repository.entity_repositories.ChallengeRepository;
import com.workout.sallyapp.model.repository.entity_repositories.GroupRepository;
import com.workout.sallyapp.model.repository.entity_repositories.GroupUserRepository;
import com.workout.sallyapp.model.repository.entity_repositories.ScoreRepository;
import com.workout.sallyapp.model.repository.entity_repositories.UserRepository;
import com.workout.sallyapp.model.repository.entity_repositories.WorkoutRepository;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Yoav on 20-Jul-17.
 */
@Module
public class SallyDBModule {

    @Provides
    @Singleton
    public ChallengeRepository createChallengeRepository(Application application) {
        return new ChallengeRepository(application);
    }

    @Provides
    @Singleton
    public GroupRepository createGroupRepository(UserRepository userRepository, GroupUserRepository groupUserRepository) {
        return new GroupRepository(userRepository, groupUserRepository);
    }

    @Provides
    @Singleton
    public GroupUserRepository createGroupUserRepository() {
        return new GroupUserRepository();
    }

    @Provides
    @Singleton
    public ScoreRepository createScoreRepository() {
        return new ScoreRepository();
    }

    @Provides
    @Singleton
    public UserRepository createUserRepository(@Named(SallySharedPrefModule.NAMED_USER_ID) Preference<Long> currentUserID,
                                               @Named(SallySharedPrefModule.NAMED_FIREBASE_USER_ID) Preference<String> currentFirebaseUserID,
                                               @Named(SallySharedPrefModule.NAMED_USER_JWT) Preference<String> currentUserJWT) {
        return new UserRepository(currentUserID, currentFirebaseUserID, currentUserJWT);
    }

    @Provides
    @Singleton
    public WorkoutRepository createWorkoutRepository() {
        return new WorkoutRepository();
    }
}
