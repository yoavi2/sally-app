package com.workout.sallyapp.view.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.workout.sallyapp.R;
import com.workout.sallyapp.databinding.ActivityChooseGroupUsersBinding;
import com.workout.sallyapp.model.entities.db.GroupEntity;
import com.workout.sallyapp.view.adapters.UserSelectRecyclerViewAdapter;

import static com.firebase.ui.auth.util.Preconditions.checkNotNull;

public class ChooseGroupUsersActivity extends AppCompatActivity {

    private static final String EXTRA_GROUP_ENTITY = "EXTRA_GROUP_ENTITY";

    private ActivityChooseGroupUsersBinding bindingActivity;
    private GroupEntity mGroup;
    private RecyclerView mRecyclerView;
    private UserSelectRecyclerViewAdapter mAdapter;

    public static Intent createIntent(@NonNull Context context, GroupEntity group) {
        Intent intent = new Intent(checkNotNull(context, "context cannot be null"), ChooseGroupUsersActivity.class);
        intent.putExtra(EXTRA_GROUP_ENTITY, group);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!getIntent().hasExtra(EXTRA_GROUP_ENTITY)) {
            throw new RuntimeException("Cannot open ChooseGroupUsersActivity with not group extra");
        }

        mGroup = getIntent().getParcelableExtra(EXTRA_GROUP_ENTITY);

        // Data bindingActivity
        bindingActivity = DataBindingUtil.setContentView(this, R.layout.activity_choose_group_users);
        setSupportActionBar(bindingActivity.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // List
        mRecyclerView = bindingActivity.contentChooseGroupUsers.list;
        mAdapter = new UserSelectRecyclerViewAdapter(this, mGroup.getUsers(), true);
        mRecyclerView.setAdapter(mAdapter);

        // Fab click
        bindingActivity.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter.getSelectedUsersCount() > 0) {
                    Intent intent = NewWorkoutActivityMulti.createIntent(ChooseGroupUsersActivity.this, mAdapter.getSelectedUsers());
                    startActivity(intent);
                } else {
                    Snackbar.make(bindingActivity.coordinatorLayout, R.string.atleast_one_friend, Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onDestroy() {
        bindingActivity.fab.setOnClickListener(null);
        super.onDestroy();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
