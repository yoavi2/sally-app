package com.workout.sallyapp.model.entities.db;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ManyToMany;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.workout.sallyapp.model.databases.SallyDatabase;

import java.util.List;

/**
 * Created by Yoav on 27-Apr-17.
 */
@Table(database = SallyDatabase.class, name = GroupEntity.TABLE_NAME, cachingEnabled = true)
@ManyToMany(referencedTable = UserEntity.class)
public class GroupEntity extends BaseModel implements Parcelable {
    public static final String TABLE_NAME = "Group";

    @PrimaryKey(autoincrement = true)
    public long id; // package-private recommended, not required

    @Column
    @Unique(onUniqueConflict = ConflictAction.REPLACE)
    @Expose
    @SerializedName("id")
    public Long serverId;

    @Column
    @NotNull
    @Expose
    @SerializedName("name")
    public String name;

    @ForeignKey(stubbedRelationship = true)
    @NotNull
    @Expose
    @SerializedName("createdBy")
    public UserEntity createdBy;

    @Column
    @Nullable
    @Expose
    @SerializedName("joinUrl")
    public String joinUrl;

    @Expose
    @SerializedName("users")
    List<UserEntity> users;

    @OneToMany(methods = {OneToMany.Method.LOAD}, variableName = "users")
    public List<UserEntity> getUsers() {
        if (users == null || users.isEmpty()) {
            users = SQLite.select()
                    .from(UserEntity.class)
                    .leftOuterJoin(GroupEntity_UserEntity.class)
                    .on(GroupEntity_UserEntity_Table.userEntity_serverId.eq(UserEntity_Table.serverId))
                    .where(GroupEntity_UserEntity_Table.groupEntity_id.is(id))
                    .queryList();
        }
        return users;
    }

    public void setUsers(List<UserEntity> users) {
        this.users = users;
    }

    public GroupEntity() {
    }

    public GroupEntity(String name, UserEntity createdBy) {
        this.name = name;
        this.createdBy = createdBy;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeValue(this.serverId);
        dest.writeString(this.name);
        dest.writeParcelable(this.createdBy, flags);
        dest.writeString(this.joinUrl);
        dest.writeTypedList(this.users);
    }

    protected GroupEntity(Parcel in) {
        this.id = in.readLong();
        this.serverId = (Long) in.readValue(Long.class.getClassLoader());
        this.name = in.readString();
        this.createdBy = in.readParcelable(UserEntity.class.getClassLoader());
        this.joinUrl = in.readString();
        this.users = in.createTypedArrayList(UserEntity.CREATOR);
    }

    public static final Creator<GroupEntity> CREATOR = new Creator<GroupEntity>() {
        @Override
        public GroupEntity createFromParcel(Parcel source) {
            return new GroupEntity(source);
        }

        @Override
        public GroupEntity[] newArray(int size) {
            return new GroupEntity[size];
        }
    };
}
