package com.workout.sallyapp.model.entities.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Yoav on 18-Aug-17.
 */
public class UserPref implements Parcelable {

    @Expose
    @SerializedName("userId")
    public long userId;

    @Expose
    @SerializedName("receiveHighscoreNotifications")
    public boolean receiveHighscoreNotifications;

    public UserPref(long userId, boolean receiveHighscoreNotifications) {
        this.userId = userId;
        this.receiveHighscoreNotifications = receiveHighscoreNotifications;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserPref userPref = (UserPref) o;

        if (userId != userPref.userId) return false;
        return receiveHighscoreNotifications == userPref.receiveHighscoreNotifications;
    }

    @Override
    public int hashCode() {
        int result = (int) (userId ^ (userId >>> 32));
        result = 31 * result + (receiveHighscoreNotifications ? 1 : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.userId);
        dest.writeByte(this.receiveHighscoreNotifications ? (byte) 1 : (byte) 0);
    }

    protected UserPref(Parcel in) {
        this.userId = in.readLong();
        this.receiveHighscoreNotifications = in.readByte() != 0;
    }

    public static final Creator<UserPref> CREATOR = new Creator<UserPref>() {
        @Override
        public UserPref createFromParcel(Parcel source) {
            return new UserPref(source);
        }

        @Override
        public UserPref[] newArray(int size) {
            return new UserPref[size];
        }
    };
}
