package com.workout.sallyapp.model.entities.db;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import android.os.Parcel;
import android.os.Parcelable;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.workout.sallyapp.model.databases.SallyDatabase;

import java.util.Date;

/**
 * Created by Yoav on 10/22/2016.
 */
@Table(database = SallyDatabase.class, name = ScoreEntity.TABLE_NAME, cachingEnabled = true)
public class ScoreEntity extends BaseModel implements Parcelable {

    public static final String TABLE_NAME = "Score";
    @PrimaryKey(autoincrement = true)
    long id; // package-private recommended, not required

    @Column
    @Unique(onUniqueConflict = ConflictAction.IGNORE)
    @Expose
    @SerializedName("id")
    public Long serverId;

    @ForeignKey
    @NotNull
    @Expose
    @SerializedName("challenge")
    public ChallengeEntity challenge;

    @ForeignKey
    @NotNull
    @Expose
    @SerializedName("user")
    public UserEntity user;

    @Column
    @Expose
    @SerializedName("durationInSec")
    public int durationInSec;

    @Column
    @NotNull
    @Expose
    @SerializedName("date")
    public Date date;

    public ScoreEntity() {
    }

    public ScoreEntity(ChallengeEntity challenge, UserEntity user, int durationInSec, Date date) {
        this.challenge = challenge;
        this.user = user;
        this.durationInSec = durationInSec;
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.serverId);
        dest.writeParcelable(this.challenge, flags);
        dest.writeParcelable(this.user, flags);
        dest.writeInt(this.durationInSec);
        dest.writeLong(this.date != null ? this.date.getTime() : -1);
    }

    protected ScoreEntity(Parcel in) {
        this.id = in.readLong();
        this.serverId = in.readLong();
        this.challenge = in.readParcelable(ChallengeEntity.class.getClassLoader());
        this.user = in.readParcelable(UserEntity.class.getClassLoader());
        this.durationInSec = in.readInt();
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
    }

    public static final Creator<ScoreEntity> CREATOR = new Creator<ScoreEntity>() {
        @Override
        public ScoreEntity createFromParcel(Parcel source) {
            return new ScoreEntity(source);
        }

        @Override
        public ScoreEntity[] newArray(int size) {
            return new ScoreEntity[size];
        }
    };
}
