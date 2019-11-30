package com.workout.sallyapp.view.loaders;

import android.content.Context;

import com.raizlabs.android.dbflow.list.FlowCursorList;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Yoav on 27-Apr-17.
 */
public class BaseDbflowCursorListLoader<T extends BaseModel> extends BaseDbflowLoader<T, FlowCursorList<T>> {


    public BaseDbflowCursorListLoader(Context context, Class<T> type) {
        super(context, type);
    }

    @Override
    protected void onReleaseResources(FlowCursorList<T> data) {
        if (mData != null) {
            mData.close();
        }
    }

    @Override
    public FlowCursorList<T> loadInBackground() {
        mData = SQLite.select()
                .from(mType)
                .cursorList();

        return mData;
    }
}
