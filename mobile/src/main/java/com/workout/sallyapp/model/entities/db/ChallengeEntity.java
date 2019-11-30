package com.workout.sallyapp.model.entities.db;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import android.os.Parcel;
import android.os.Parcelable;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.workout.sallyapp.model.databases.SallyDatabase;

/**
 * Created by Yoav on 23-Apr-17.
 */

@Table(database = SallyDatabase.class, name = ChallengeEntity.TABLE_NAME, cachingEnabled = true)
public class ChallengeEntity extends BaseModel implements Parcelable {
    public static final String TABLE_NAME = "Challenge";

    @PrimaryKey()
    @Expose
    @SerializedName("id")
    public Long serverId;

    @Column
    @Unique(onUniqueConflict = ConflictAction.IGNORE)
    @NotNull
    @Expose
    @SerializedName("videoUrl")
    public String videoUrl;

    @Column
    @NotNull
    @Expose
    @SerializedName("title")
    public String title;

    @Column
    @Expose
    @SerializedName("length")
    public Integer length;

    @Column
    @Expose
    @SerializedName("startAt")
    public Integer startAt;

    @Column
    @Expose
    @SerializedName("endAt")
    public Integer endAt;

    public ChallengeEntity() {
    }

    public ChallengeEntity(long serverId, String videoUrl, String title) {
        this(serverId, videoUrl, title, null);
    }

    public ChallengeEntity(long serverId, String videoUrl, String title, Integer length) {
        this(serverId, videoUrl, title, length, null, null);
    }

    public ChallengeEntity(Long serverId, String videoUrl, String title, Integer length, Integer startAt, Integer endAt) {
        this.serverId = serverId;
        this.videoUrl = videoUrl;
        this.title = title;
        this.length = length;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.serverId);
        dest.writeString(this.videoUrl);
        dest.writeString(this.title);
        dest.writeValue(this.startAt);
        dest.writeValue(this.endAt);
    }

    protected ChallengeEntity(Parcel in) {
        this.serverId = in.readLong();
        this.videoUrl = in.readString();
        this.title = in.readString();
        this.startAt = (Integer) in.readValue(Integer.class.getClassLoader());
        this.endAt = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<ChallengeEntity> CREATOR = new Creator<ChallengeEntity>() {
        @Override
        public ChallengeEntity createFromParcel(Parcel source) {
            return new ChallengeEntity(source);
        }

        @Override
        public ChallengeEntity[] newArray(int size) {
            return new ChallengeEntity[size];
        }
    };
}
