package com.workout.sallyapp.controller.retrofit.mock;

import com.workout.sallyapp.model.entities.db.ChallengeEntity;
import com.workout.sallyapp.model.entities.db.GroupEntity;
import com.workout.sallyapp.model.entities.db.UserEntity;

import java.util.ArrayList;

/**
 * Created by Yoav on 08-May-17.
 */

public final class MockDatabase {

    public static ArrayList<UserEntity> mUsers;
    public static ChallengeEntity mChallenge;
    public static ArrayList<GroupEntity> mGroups;
    public static long mGroupId = 1;
    public static long mUserId = 1;

    static {
        mChallenge = new ChallengeEntity(1L, "https://www.youtube.com/watch?v=41N6bKO-NVI", "Sally", 233);

        UserEntity yoav = new UserEntity(1L, "6JLUfYgVsEY4Bx0yMKd6Oe8RNYu2",
                "yoav codish",
                "https://lh3.googleusercontent.com/-NZzvDxotKo8/AAAAAAAAAAI/AAAAAAAAAcQ/w8xCquTgAEI/photo.jpg?sz=64",
                "yoav.codish@gmail.com");
        yoav.serverId = mUserId++;

        UserEntity omer = new UserEntity(2L, "6JLUfYgVsEY4Bx0yMKd6Oe8RNYu3",
                "Omer Saraf",
                "https://scontent.fhfa1-1.fna.fbcdn.net/v/t1.0-9/557567_298065390269607_787676666_n.jpg?oh=307f6a044cae94adc78b807e3d5c9be4&oe=5997872F",
                "omersaraf@walla.com");
        omer.serverId = mUserId++;

        UserEntity tal = new UserEntity(100L, "6JLUfYgVsEY4Bx0yMKd6Oe8RNYu4",
                "Tal Gerbi",
                "https://scontent.ftlv2-1.fna.fbcdn.net/v/t1.0-9/12039191_10153611357049293_7816514652565141393_n.jpg?oh=cef4ec2d0a8fa9d5279e36db86e2fa4c&oe=597BBB0F",
                "talgerbi@walla.com");
        tal.serverId = mUserId++;

        UserEntity idan = new UserEntity(101L, "6JLUfYgVsEY4Bx0yMKd6Oe8RNYu5",
                "Idan Aizik Nissim",
                "https://scontent.ftlv2-1.fna.fbcdn.net/v/t1.0-9/248407_1839415710454_6745050_n.jpg?oh=126028e933062e7bc075670dae69fd03&oe=59C0C31C",
                "idanAizikinissim@gmail.com");
        idan.serverId = mUserId++;

        mUsers = new ArrayList<>();
        mUsers.add(yoav);
        mUsers.add(omer);
        mUsers.add(tal);
        mUsers.add(idan);

        mGroups = new ArrayList<>();

        GroupEntity homeGroup = new GroupEntity("Appartment", yoav);
        ArrayList<UserEntity> homeUsers = new ArrayList<>();
        homeUsers.add(yoav);
        homeUsers.add(omer);
        homeUsers.add(tal);
        homeGroup.setUsers(homeUsers);
        homeGroup.serverId = mGroupId++;
        mGroups.add(homeGroup);

        GroupEntity officeGroup = new GroupEntity("Office", idan);
        ArrayList<UserEntity> officeUsers = new ArrayList<>();
        officeUsers.add(yoav);
        officeUsers.add(idan);
        officeGroup.setUsers(officeUsers);
        officeGroup.serverId = mGroupId++;
        mGroups.add(officeGroup);
    }

    private MockDatabase() {
    }


}
