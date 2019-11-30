package com.workout.sallyapp.view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;
import com.workout.sallyapp.R;
import com.workout.sallyapp.model.entities.db.UserEntity;
import com.workout.sallyapp.view.ui.CircleTransform;
import com.workout.sallyapp.view.ui.InitialsGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yoav on 04-May-17.
 */

public class UserSelectRecyclerViewAdapter extends RecyclerView.Adapter<UserSelectRecyclerViewAdapter.MyViewHolder>{

    private Context mContext;
    private List<UserEntity> mUserList;
    private boolean mIsUserSelectedDefault;
    private ArrayList<UserEntity> mSelectedUsers;
    private InitialsGenerator mInitialGenerator;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public boolean isSelected;
        public View layout;
        public TextView userName;
        public ImageView userPicture;
        public CheckBox checkBox;

        public MyViewHolder(View view) {
            super(view);
            isSelected = mIsUserSelectedDefault;
            layout = view;
            userName = (TextView) view.findViewById(R.id.item_user_name);
            userPicture = (ImageView) view.findViewById(R.id.item_user_image);
            checkBox = (CheckBox) view.findViewById(R.id.item_user_checkbox);
        }
    }

    public UserSelectRecyclerViewAdapter(Context mContext, List<UserEntity> userList, boolean usersSelected) {
        this.mContext = mContext;
        this.mUserList = userList;
        this.mInitialGenerator = new InitialsGenerator(true);

        mIsUserSelectedDefault = usersSelected;
        mSelectedUsers = new ArrayList<>(mUserList.size());
        if (usersSelected) {
            mSelectedUsers.addAll(mUserList);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_select, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final UserEntity user = mUserList.get(position);

        String displayName = user.displayName;

        holder.userName.setText(displayName);

        // Generate some sort of Initials
        TextDrawable initialsDrawable =
                mInitialGenerator.createCircle(displayName, user.serverId);

        // Check if picture available
        if (user.photoUrl != null) {
            Picasso.with(mContext).load(user.photoUrl).error(initialsDrawable).transform(new CircleTransform()).into(holder.userPicture);
        }
        else {
            holder.userPicture.setImageDrawable(initialsDrawable);
        }

        holder.checkBox.setSelected(holder.isSelected);

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectedChanged(holder);
            }
        });

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectedChanged(holder);
            }
        });
    }

    private void onSelectedChanged(MyViewHolder holder) {
        UserEntity clickedUser = mUserList.get(holder.getAdapterPosition());

        if (holder.isSelected) {
            mSelectedUsers.remove(clickedUser);
        } else {
            mSelectedUsers.add(clickedUser);
        }

        holder.isSelected = !holder.isSelected;
        holder.checkBox.setChecked(holder.isSelected);
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public ArrayList<UserEntity> getSelectedUsers() {
        return mSelectedUsers;
    }

    public int getSelectedUsersCount() {
        return mSelectedUsers.size();
    }
}
