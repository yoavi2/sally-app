package com.workout.sallyapp.view.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.raizlabs.android.dbflow.list.FlowCursorList;
import com.workout.sallyapp.R;
import com.workout.sallyapp.model.entities.db.GroupEntity;
import com.workout.sallyapp.view.ui.InitialsGenerator;

/**
 * Created by Yoav on 27-Apr-17.
 */

public class GroupRecyclerViewAdapter extends FlowCursorListRecyclerAdapter<GroupRecyclerViewAdapter.ViewHolder> {

    public interface OnGroupListInteraction {
        void onGroupClicked(int position, GroupEntity group);

        void onGroupLongClicked(int position, GroupEntity group);

        void onGroupButtonClicked(int position, GroupEntity group);
    }

    private Context mContext;
    private FlowCursorList<GroupEntity> mFlowCursorList;
    private OnGroupListInteraction onGroupListInteraction;
    private boolean mIsShowButton;
    private InitialsGenerator mInitialGenerator;

    public GroupRecyclerViewAdapter(Context context, OnGroupListInteraction listener, boolean isShowButton) {
        mContext = context;
        onGroupListInteraction = listener;
        mIsShowButton = isShowButton;
        mInitialGenerator = new InitialsGenerator(true);
    }

    public void setListener(OnGroupListInteraction listener) {
        onGroupListInteraction = listener;
    }

    public void setCursorList(FlowCursorList<GroupEntity> cursorList) {
        mFlowCursorList = cursorList;

        if (cursorList != null) {
            swapCursor(cursorList.cursor());
        } else {
            swapCursor(null);
        }
    }

    @Override
    public GroupRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group, parent, false);
        return new GroupRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolderCursor(final GroupRecyclerViewAdapter.ViewHolder holder, final int position) {
        final GroupEntity model = mFlowCursorList.getItem(position);

        holder.mItem = model;

        holder.mImage.setImageDrawable(mInitialGenerator.createCircle(model.name, model.name));
        holder.mName.setText(model.name);

        int usersCount = model.getUsers().size();
        holder.mMembers.setText(usersCount == 1 ?
                String.format(mContext.getString(R.string.group_member_count), usersCount) :
                String.format(mContext.getString(R.string.group_members_count), usersCount));


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onGroupListInteraction != null) {
                    onGroupListInteraction.onGroupClicked(holder.getAdapterPosition(), mFlowCursorList.getItem(holder.getAdapterPosition()));
                }
            }
        });

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onGroupListInteraction != null) {
                    onGroupListInteraction.onGroupLongClicked(holder.getAdapterPosition(), mFlowCursorList.getItem(holder.getAdapterPosition()));
                }
                return true;
            }
        });

        if (mIsShowButton) {
            holder.mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onGroupListInteraction != null) {
                        onGroupListInteraction.onGroupButtonClicked(holder.getAdapterPosition(), mFlowCursorList.getItem(holder.getAdapterPosition()));
                    }
                }
            });
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public ImageView mImage;
        public TextView mName;
        public TextView mMembers;
        public ImageButton mButton;
        public GroupEntity mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImage = (ImageView) view.findViewById(R.id.item_group_imageGroup);
            mName = (TextView) view.findViewById(R.id.item_group_name);
            mMembers = (TextView) view.findViewById(R.id.item_group_members);

            if (mIsShowButton) {
                mButton = (ImageButton) view.findViewById(R.id.item_group_play);
                mButton.setVisibility(View.VISIBLE);
                mButton.setColorFilter(ContextCompat.getColor(mContext, R.color.colorAccent));
            }
        }
    }


}