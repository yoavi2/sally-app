package com.workout.sallyapp.view.loaders;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import com.raizlabs.android.dbflow.runtime.FlowContentObserver;
import com.raizlabs.android.dbflow.sql.language.SQLOperator;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.workout.sallyapp.BuildConfig;

/**
 * Created by Yoav on 05-May-17.
 */

public abstract class BaseDbflowLoader<T extends BaseModel, S> extends AsyncTaskLoader<S> {

    protected final Class<T> mType;
    protected S mData;
    protected boolean isLoadingData;
    protected FlowContentObserver mObserver;

    public BaseDbflowLoader(Context context, Class<T> type) {
        super(context);

        mType = type;
    }

    protected Class<T> getMyType() {
        return this.mType;
    }

    @Override
    public void deliverResult(S data){
        if (isReset()) {
            // The Loader has been reset; ignore the result and invalidate the data.
            onReleaseResources(data);
            return;
        }

        // Hold a reference to the old data so it doesn't get garbage collected.
        // We must protect it until the new data has been delivered.
        S oldData = mData;
        mData = data;

        if (isStarted()) {
            // If the Loader is in a started state, deliver the results to the
            // client. The superclass method does this for us.
            super.deliverResult(data);
        }

        // Invalidate the old data as we don't need it any more.
        if (oldData != null && oldData != data) {
            onReleaseResources(oldData);
        }
    }

    @Override
    protected void onStartLoading() {

        if (mData != null) {
            // Deliver any previously loaded data immediately.
            deliverResult(mData);
        }

        // Begin monitoring the underlying data source.
        if (mObserver == null) {
            mObserver = new FlowContentObserver(BuildConfig.APPLICATION_ID);
            mObserver.registerForContentChanges(getContext(), mType);
            mObserver.addOnTableChangedListener(new FlowContentObserver.ContentChangeListener() {
                @Override
                public void onModelStateChanged(@Nullable Class<?> table, BaseModel.Action action, @NonNull SQLOperator[] primaryKeyValues) {
                    onContentChanged();
                }

                @Override
                public void onTableChanged(@Nullable Class<?> tableChanged, @NonNull BaseModel.Action action) {
                    onContentChanged();
                }
            });
        }

        if (takeContentChanged() || mData == null) {
            // When the observer detects a change, it should call onContentChanged()
            // on the Loader, which will cause the next call to takeContentChanged()
            // to return true. If this is ever the case (or if the current data is
            // null), we force a new load.
            forceLoad();
        }

        super.onStartLoading();
    }

    protected void onReleaseResources(S data) {
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, we would close it here.

    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override public void onCanceled(S data) {
        super.onCanceled(data);

        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(data);
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (mData != null) {
            onReleaseResources(mData);
            mData = null;
        }

        // Stop monitoring for changes.
        if (mObserver != null) {
            mObserver.unregisterForContentChanges(getContext());
            mObserver = null;
        }
    }

    @Override
    public abstract S loadInBackground();

}
