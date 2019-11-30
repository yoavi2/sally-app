package com.workout.sallyapp.model.entities.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import android.os.Parcel;
import android.os.Parcelable;

import com.workout.sallyapp.model.entities.db.ScoreEntity;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Yoav on 23-Apr-17.
 */

public class Workout implements Parcelable {
    @Expose
    @SerializedName("challengeId")
    public long challengeId;

    @Expose
    @SerializedName("createdByUserId")
    public long createdByUserId;

    @Expose
    @SerializedName("date")
    public Date date;

    @Expose
    @SerializedName("scores")
    public ArrayList<ScoreEntity> scores;

    public Workout(long challengeId, long createdByUserId, Date date, ArrayList<ScoreEntity> scores) {
        this.challengeId = challengeId;
        this.createdByUserId = createdByUserId;
        this.date = date;
        this.scores = scores;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.challengeId);
        dest.writeLong(this.createdByUserId);
        dest.writeLong(this.date != null ? this.date.getTime() : -1);
        dest.writeTypedList(this.scores);
    }

    protected Workout(Parcel in) {
        this.challengeId = in.readLong();
        this.createdByUserId = in.readLong();
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
        this.scores = in.createTypedArrayList(ScoreEntity.CREATOR);
    }

    public static final Creator<Workout> CREATOR = new Creator<Workout>() {
        @Override
        public Workout createFromParcel(Parcel source) {
            return new Workout(source);
        }

        @Override
        public Workout[] newArray(int size) {
            return new Workout[size];
        }
    };
}
