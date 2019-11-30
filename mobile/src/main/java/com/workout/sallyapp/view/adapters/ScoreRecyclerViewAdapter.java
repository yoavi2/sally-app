package com.workout.sallyapp.view.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.raizlabs.android.dbflow.list.FlowCursorList;
import com.workout.sallyapp.R;
import com.workout.sallyapp.model.entities.db.ScoreEntity;
import com.workout.sallyapp.utilities.TimeUtility;

import java.text.DateFormat;

public class ScoreRecyclerViewAdapter extends FlowCursorListRecyclerAdapter<ScoreRecyclerViewAdapter.ViewHolder> {

    public interface OnScoreListInteraction {
        void onScoreLongClicked(int position, ScoreEntity item);
    }

    private FlowCursorList<ScoreEntity> mFlowCursorList;
    private OnScoreListInteraction onScoreListInteraction;
    private DateFormat mDateTimeInstance;
    private int mSongLength;

    public ScoreRecyclerViewAdapter(int songLength, OnScoreListInteraction listener) {
        mSongLength = songLength;
        onScoreListInteraction = listener;
        mDateTimeInstance = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
    }

    public void setListener(OnScoreListInteraction listener) {
        onScoreListInteraction = listener;
    }

    public void setCursorList(FlowCursorList<ScoreEntity> cursorList) {
        mFlowCursorList = cursorList;

        if (cursorList != null) {
            swapCursor(cursorList.cursor());
        } else {
            swapCursor(null);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_score, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolderCursor(final ViewHolder holder, final int position) {
        final ScoreEntity model = mFlowCursorList.getItem(position);

        holder.mItem = model;
        holder.mScoreView.setText(String.valueOf(TimeUtility.formatSeconds(holder.mItem.durationInSec)));
        holder.mDateView.setText(String.valueOf(mDateTimeInstance.format(holder.mItem.date)));

        if (holder.mItem.durationInSec < mSongLength) {
            // TODO: Represent in UI that finished
        }

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onScoreListInteraction != null) {
                    onScoreListInteraction.onScoreLongClicked(holder.getAdapterPosition(), mFlowCursorList.getItem(holder.getAdapterPosition()));
                }

                return true;
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView mScoreView;
        public TextView mDateView;
        public ScoreEntity mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mScoreView = (TextView) view.findViewById(R.id.item_score);
            mDateView = (TextView) view.findViewById(R.id.item_score_date);
        }
    }

}
