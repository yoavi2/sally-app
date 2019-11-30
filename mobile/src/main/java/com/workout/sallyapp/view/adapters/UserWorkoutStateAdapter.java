package com.workout.sallyapp.view.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;
import com.workout.sallyapp.R;
import com.workout.sallyapp.model.entities.view.WorkoutSessionScore;
import com.workout.sallyapp.utilities.TimeUtility;
import com.workout.sallyapp.view.ui.InitialsGenerator;

import java.util.List;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

/**
 * Created by Yoav on 28-Mar-17.
 */

public class UserWorkoutStateAdapter extends RecyclerView.Adapter<UserWorkoutStateAdapter.MyViewHolder> {

    public interface OnUserCardClick {
        void onUserCardClick(int position);
    }

    private Context mContext;
    private OnUserCardClick mOnUserCardClick;
    private List<WorkoutSessionScore> mUserList;
    private InitialsGenerator mInitialGenerator;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public TextView userName;
        public ImageView userPicture;
        public Badge badge;

        public MyViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.card_view);
            userName = (TextView) view.findViewById(R.id.card_user_workout_state_name);
            userPicture = (ImageView) view.findViewById(R.id.card_user_workout_state_picture);
            badge = new QBadgeView(mContext).bindTarget(cardView);
        }
    }


    public UserWorkoutStateAdapter(Context mContext, List<WorkoutSessionScore> userList) {
        this.mContext = mContext;
        this.mUserList = userList;
        this.mInitialGenerator = new InitialsGenerator(true);
    }

    public UserWorkoutStateAdapter(Context mContext, List<WorkoutSessionScore> userList, OnUserCardClick onUserCardClick) {
        this(mContext, userList);
        setUserCardClickListener(onUserCardClick);
    }

    public void setUserCardClickListener(OnUserCardClick onUserCardClick) {
        mOnUserCardClick = onUserCardClick;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_user_workout_state, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final WorkoutSessionScore workoutSessionScore = mUserList.get(position);

        // TODO: Find a way to not initiate a new listener every time and still access user?
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnUserCardClick != null) {
                    mOnUserCardClick.onUserCardClick(holder.getAdapterPosition());
                }
            }
        });

        String displayName = workoutSessionScore.user.displayName;

        holder.userName.setText(displayName);

        // Generate some sort of Initials
        TextDrawable initialsDrawable =
                mInitialGenerator.createRect(displayName, workoutSessionScore.user.serverId);

        // Check if picture available
        if (workoutSessionScore.user.photoUrl != null) {
            Picasso.with(mContext).load(workoutSessionScore.user.photoUrl).error(initialsDrawable).into(holder.userPicture);
        }
        else {
            holder.userPicture.setImageDrawable(initialsDrawable);
        }

        // Check if user is actively working out
        if (workoutSessionScore.isActive) {
            holder.cardView.setAlpha(1f);
        }
        else {
            holder.cardView.setAlpha(0.5f);
        }

        // Check if user finished workout successfully
        if (workoutSessionScore.isFinished) {
            holder.badge
                    .setBadgeBackgroundColor(ContextCompat.getColor(mContext, R.color.youtube_red))
                    .setBadgeTextColor(ContextCompat.getColor(mContext, android.R.color.white))
                    .setBadgeTextSize(18, true)
                    .setBadgePadding(10, true)
                    .setBadgeGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP)
                    .setBadgeText(mContext.getString(R.string.finished));
        }
        else if (workoutSessionScore.score != null) {
                holder.badge
                        .setBadgeBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark))
                        .setBadgeTextColor(ContextCompat.getColor(mContext, android.R.color.white))
                        .setBadgePadding(10, true)
                        .setBadgeTextSize(18, true)
                        .setBadgeGravity(Gravity.END | Gravity.TOP)
                        .setGravityOffset(8, 4, true)
                        .setBadgeText(TimeUtility.formatSeconds(workoutSessionScore.score.durationInSec));
        }
        else {
             holder.badge.hide(true);
        }
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

}
