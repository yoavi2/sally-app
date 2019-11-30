package com.workout.sallyapp.view.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;

import com.kennyc.view.MultiStateView;
import com.raizlabs.android.dbflow.list.FlowCursorList;
import com.workout.sallyapp.R;
import com.workout.sallyapp.databinding.ActivityChooseGroupBinding;
import com.workout.sallyapp.model.entities.db.GroupEntity;
import com.workout.sallyapp.view.activities.base.BaseSallyActivity;
import com.workout.sallyapp.view.adapters.GroupRecyclerViewAdapter;
import com.workout.sallyapp.view.loaders.GroupsLoader;

public class ChooseGroupActivity extends BaseSallyActivity
        implements LoaderManager.LoaderCallbacks<FlowCursorList<GroupEntity>>,GroupRecyclerViewAdapter.OnGroupListInteraction {

    private static final int GROUP_FLOW_CURSOR_LIST_LOADER_ID = 3;

    private ActivityChooseGroupBinding bindingActivity;
    private RecyclerView mRecyclerView;
    private GroupRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Data bindingActivity
        bindingActivity = DataBindingUtil.setContentView(this, R.layout.activity_choose_group);
        setSupportActionBar(bindingActivity.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // List
        mRecyclerView = bindingActivity.contentChooseGroup.list;
        mAdapter = new GroupRecyclerViewAdapter(this, this, false);
        mRecyclerView.setAdapter(mAdapter);

        // db
        getSupportLoaderManager().initLoader(GROUP_FLOW_CURSOR_LIST_LOADER_ID, null, this);

    }

    @Override
    public Loader<FlowCursorList<GroupEntity>> onCreateLoader(int id, Bundle args) {
        return new GroupsLoader(this, mCurrentUser.serverId);
    }

    @Override
    public void onLoadFinished(Loader<FlowCursorList<GroupEntity>> loader, FlowCursorList<GroupEntity> data) {
        // List
        mAdapter.setCursorList(data);

        showOrHideViews();
    }

    @Override
    public void onLoaderReset(Loader<FlowCursorList<GroupEntity>> loader) {
        // List
        mAdapter.setCursorList(null);
    }

    private void showOrHideViews() {

        int count = mAdapter.getItemCount();

        // Check if whole view should be shown
        if (count <= 0) {
            bindingActivity.contentChooseGroup.multiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
        } else {
            bindingActivity.contentChooseGroup.multiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        }
    }

    @Override
    public void onGroupClicked(int position, GroupEntity group) {
        startActivity(ChooseGroupUsersActivity.createIntent(this, group));
    }

    @Override
    public void onGroupLongClicked(int position, final GroupEntity group) {

    }

    @Override
    public void onGroupButtonClicked(int position, GroupEntity group) {

    }

    @Override
    public void onDestroy() {
        mAdapter.setListener(null);

        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
