package com.workout.sallyapp.view.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.kennyc.view.MultiStateView;
import com.raizlabs.android.dbflow.list.FlowCursorList;
import com.workout.sallyapp.R;
import com.workout.sallyapp.controller.retrofit.apis.GroupAPI;
import com.workout.sallyapp.controller.retrofit.error_handling.APIError;
import com.workout.sallyapp.controller.retrofit.error_handling.ErrorUtils;
import com.workout.sallyapp.controller.retrofit.responses.CreateGroupResponse;
import com.workout.sallyapp.databinding.FragmentGroupBinding;
import com.workout.sallyapp.model.entities.db.GroupEntity;
import com.workout.sallyapp.model.repository.entity_repositories.GroupRepository;
import com.workout.sallyapp.model.repository.interfaces.RepositoryTransactionEvent;
import com.workout.sallyapp.view.activities.ChooseGroupUsersActivity;
import com.workout.sallyapp.view.activities.MainActivity;
import com.workout.sallyapp.view.adapters.GroupRecyclerViewAdapter;
import com.workout.sallyapp.view.loaders.GroupsLoader;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupFragment extends Fragment implements LoaderManager.LoaderCallbacks<FlowCursorList<GroupEntity>>, GroupRecyclerViewAdapter.OnGroupListInteraction {

    private static final int GROUP_FLOW_CURSOR_LIST_LOADER_ID = 3;
    private static final int CREATE_GROUP_REQUEST_CODE = 1;

    @Inject
    GroupAPI mGroupApi;
    @Inject
    ErrorUtils mErrorUtils;
    @Inject
    GroupRepository mGroupRepository;

    private FragmentGroupBinding binding;
    private RecyclerView mRecyclerView;
    private GroupRecyclerViewAdapter mAdapter;

    public GroupFragment() {
        // Required empty public constructor
    }

    public static GroupFragment newInstance() {
        GroupFragment fragment = new GroupFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);

        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGroupBinding.inflate(inflater, container, false);

        setHasOptionsMenu(true);

        // List
        mRecyclerView = binding.list;
        mAdapter = new GroupRecyclerViewAdapter(getActivity(), this, true);
        mRecyclerView.setAdapter(mAdapter);

        // db
        getLoaderManager().initLoader(GROUP_FLOW_CURSOR_LIST_LOADER_ID, null, this);

        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_groups, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_new_group) {

            NewGroupDialogFragment dialogFrag = new NewGroupDialogFragment();
            // This is the requestCode that you are sending.
            dialogFrag.setTargetFragment(this, CREATE_GROUP_REQUEST_CODE);
            dialogFrag.show(getFragmentManager(), NewGroupDialogFragment.TAG);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<FlowCursorList<GroupEntity>> onCreateLoader(int id, Bundle args) {
        return new GroupsLoader(getActivity(), ((MainActivity) getActivity()).mCurrentUser.serverId);
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
            binding.multiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
        } else {
            binding.multiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CREATE_GROUP_REQUEST_CODE) {
            // This is the return result from DialogFragment
            if(resultCode == NewGroupDialogFragment.RESULT_CODE_OK) {
                GroupEntity group = data.getParcelableExtra(NewGroupDialogFragment.GROUP_TO_CREATE);
                createGroup(group);
            }
        }
    }

    @Override
    public void onGroupClicked(int position, GroupEntity group) {
        GroupUsersDialogFragment dialog = GroupUsersDialogFragment.newInstance(group);
        dialog.show(getFragmentManager(), GroupUsersDialogFragment.TAG);
    }

    @Override
    public void onGroupLongClicked(int position, final GroupEntity group) {
        // Show delete dialog
        new MaterialDialog.Builder(getActivity())
                .title(R.string.exit_group_title)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .icon(ContextCompat.getDrawable(getActivity(), R.drawable.trash_24_black))
                .autoDismiss(true)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        mGroupRepository.deleteGroup(group, new RepositoryTransactionEvent() {
                            @Override
                            public void onSuccess() {
                                if (group.serverId != null) {
                                    Call<Void> call = mGroupApi.
                                            exitGroup(group.serverId, ((MainActivity) getActivity()).mCurrentUser.serverId);
                                    call.enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                            if (response.isSuccessful()) {

                                            } else {
                                                APIError error = mErrorUtils.parseError(response);
                                                Timber.e(error.message());
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {
                                            Timber.e(t.toString());
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onError(Throwable error) {
                            }
                        });
                    }
                })
                .show();
    }

    @Override
    public void onGroupButtonClicked(int position, GroupEntity group) {
        startActivity(ChooseGroupUsersActivity.createIntent(getActivity(), group));
    }

    @Override
    public void onDestroyView() {
        mAdapter.setListener(null);

        super.onDestroyView();
    }


    public void createGroup(final GroupEntity group) {
        Call<CreateGroupResponse> call = mGroupApi.createGroup(group);

        call.enqueue(new Callback<CreateGroupResponse>() {
            @Override
            public void onResponse(Call<CreateGroupResponse> call, Response<CreateGroupResponse> response) {
                if (response.isSuccessful()) {
                    group.serverId = response.body().groupServerId;
                    group.joinUrl = response.body().groupInviteUrl;

                    mGroupRepository.saveGroup(group, new RepositoryTransactionEvent() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError(Throwable error) {
                        }
                    });
                } else {
                    APIError error = mErrorUtils.parseError(response);
                    Timber.e(error.message());
                }
            }

            @Override
            public void onFailure(Call<CreateGroupResponse> call, Throwable t) {
                Timber.e(t.toString());
            }
        });
    }
}
