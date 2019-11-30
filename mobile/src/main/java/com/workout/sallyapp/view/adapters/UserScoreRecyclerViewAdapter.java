package com.workout.sallyapp.view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;
import com.workout.sallyapp.R;
import com.workout.sallyapp.model.entities.db.ScoreEntity;
import com.workout.sallyapp.model.entities.db.UserEntity;
import com.workout.sallyapp.utilities.TimeUtility;
import com.workout.sallyapp.view.ui.CircleTransform;
import com.workout.sallyapp.view.ui.InitialsGenerator;

import java.util.List;

/**
 * Created by Yoav on 06-May-17.
 */

public class UserScoreRecyclerViewAdapter extends RecyclerView.Adapter<UserScoreRecyclerViewAdapter.MyViewHolder> {

    private Context mContext;
    private List<UserEntity> mUserList;
    private List<ScoreEntity> mScoreList;
    private InitialsGenerator mInitialGenerator;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView userName;
        public ImageView userPicture;
        public TextView userScore;

        public MyViewHolder(View view) {
            super(view);
            userName = (TextView) view.findViewById(R.id.item_user_name);
            userPicture = (ImageView) view.findViewById(R.id.item_user_image);
            userScore = (TextView) view.findViewById(R.id.item_user_score);
        }
    }

     public UserScoreRecyclerViewAdapter(Context mContext, List<UserEntity> users) {
         this.mContext = mContext;
         this.mUserList = users;
         this.mInitialGenerator = new InitialsGenerator(true);
    }

    public void setList(List<ScoreEntity> scoreList) {
        this.mScoreList = scoreList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_score, parent, false);

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

        if (mScoreList != null) {
            holder.userScore.setVisibility(View.VISIBLE);
            ScoreEntity score = mScoreList.get(holder.getAdapterPosition());

            if (score != null) {
                holder.userScore.setText(TimeUtility.formatSeconds(score.durationInSec));
            }
        }
        else {
            holder.userScore.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }



}
