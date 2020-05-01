package com.workout.sallyapp.model.entities.db;

import com.google.firebase.auth.FirebaseUser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.workout.sallyapp.model.databases.SallyDatabase;

/**
 * Created by Yoav on 28-Mar-17.
 */
@Table(database = SallyDatabase.class, name = UserEntity.TABLE_NAME, cachingEnabled = true)
public class UserEntity extends BaseModel implements Parcelable {
    public static final String TABLE_NAME = "User";

    @PrimaryKey()
    @Expose
    @SerializedName("id")
    public Long serverId;

    @Column
    @NotNull
    @SerializedName("firebaseId")
    @Expose
    public String firebaseUId;

    @Column
    @NotNull
    @SerializedName("displayName")
    @Expose
    public String displayName;

    @Column
    @Nullable
    @SerializedName("photoUrl")
    @Expose
    public String photoUrl;

    @Nullable
    @SerializedName("email")
    @Expose
    public String email;

    @Column
    @Nullable
    public String jwt;

    public UserEntity() {
    }

    public UserEntity(String firebaseUId, String displayName, String photoUrl, String email) {
        this(null, firebaseUId, displayName, photoUrl, email);
    }

    public UserEntity(Long serverId, String firebaseUId, String displayName, String photoUrl, String email) {
        this.serverId = serverId;
        this.firebaseUId = firebaseUId;
        this.displayName = displayName;
        this.photoUrl = photoUrl;
        this.email = email;
    }

    public static UserEntity userFromFirebaseUser(@NonNull FirebaseUser fbUser) {
        return new UserEntity(fbUser.getUid(), fbUser.getDisplayName(), fbUser.getPhotoUrl() != null ? fbUser.getPhotoUrl().toString() : null, fbUser.getEmail());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.serverId);
        dest.writeString(this.firebaseUId);
        dest.writeString(this.displayName);
        dest.writeString(this.photoUrl);
        dest.writeString(this.email);
        dest.writeString(this.jwt);
    }

    protected UserEntity(Parcel in) {
        this.serverId = (Long) in.readValue(Long.class.getClassLoader());
        this.firebaseUId = in.readString();
        this.displayName = in.readString();
        this.photoUrl = in.readString();
        this.email = in.readString();
        this.jwt = in.readString();
    }

    public static final Creator<UserEntity> CREATOR = new Creator<UserEntity>() {
        @Override
        public UserEntity createFromParcel(Parcel source) {
            return new UserEntity(source);
        }

        @Override
        public UserEntity[] newArray(int size) {
            return new UserEntity[size];
        }
    };
}
