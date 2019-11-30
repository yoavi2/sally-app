package com.workout.sallyapp.view.fragments;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.workout.sallyapp.R;
import com.workout.sallyapp.controller.retrofit.apis.UserAPI;
import com.workout.sallyapp.controller.retrofit.error_handling.APIError;
import com.workout.sallyapp.controller.retrofit.error_handling.ErrorUtils;
import com.workout.sallyapp.model.entities.db.GroupEntity;
import com.workout.sallyapp.model.entities.db.ScoreEntity;
import com.workout.sallyapp.model.entities.db.UserEntity;
import com.workout.sallyapp.view.adapters.UserScoreRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.workout.sallyapp.R.id.fragment_dialog_group_users_url;

/**
 * Created by Yoav on 06-May-17.
 */
public class GroupUsersDialogFragment extends DialogFragment implements View.OnClickListener {

    public  static final String TAG = "GroupUsersDialogFragment";
    private static final String ARG_PARAM_GROUP = "ARG_PARAM_GROUP";

    @Inject
    UserAPI mUserApi;
    @Inject
    ErrorUtils mErrorUtils;

    private GroupEntity mGroup;
    private List<ScoreEntity> mUserScores;
    private RecyclerView mRecyclerView;
    private UserScoreRecyclerViewAdapter mAdapter;

    public static GroupUsersDialogFragment newInstance(GroupEntity group) {
        GroupUsersDialogFragment fragment = new GroupUsersDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM_GROUP, group);
        fragment.setArguments(args);
        return fragment;
    }

    public GroupUsersDialogFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGroup = getArguments().getParcelable(ARG_PARAM_GROUP);
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);

        super.onAttach(context);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final MaterialDialog dialog =
                new MaterialDialog.Builder(getActivity())
                        .customView(R.layout.fragment_dialog_group_users, false)
                        .title(mGroup.name)
                        .show();
        
        if (mGroup.joinUrl != null) {
            View urlLayout = dialog.getCustomView().findViewById(R.id.fragment_dialog_group_users_url_layout);
            urlLayout.setVisibility(View.VISIBLE);

            TextView urlTextView = (TextView) dialog.getCustomView().findViewById(fragment_dialog_group_users_url);
            View urlIamgeButton = dialog.getCustomView().findViewById(R.id.fragment_dialog_group_users_share_url);

            urlTextView.setText(mGroup.joinUrl);
            urlTextView.setSelected(true);

            urlTextView.setOnClickListener(this);
            urlIamgeButton.setOnClickListener(this);
        }

        mRecyclerView = (RecyclerView) dialog.getCustomView().findViewById(R.id.fragment_dialog_group_users_list);
        mAdapter = new UserScoreRecyclerViewAdapter(getContext(), mGroup.getUsers());
        mRecyclerView.setAdapter(mAdapter);

        Call<List<ScoreEntity>> call = mUserApi.getUsersHighscores(usersToUserIds(mGroup.getUsers()));
        call.enqueue(new Callback<List<ScoreEntity>>() {
            @Override
            public void onResponse(Call<List<ScoreEntity>> call, Response<List<ScoreEntity>> response) {
                if (response.isSuccessful()) {
                    mUserScores = response.body();

                    if (mUserScores != null && mUserScores.size() == mAdapter.getItemCount()) {
                        mAdapter.setList(mUserScores);
                        mAdapter.notifyDataSetChanged();
                    }
                } else {
                    APIError error = mErrorUtils.parseError(response);
                    Timber.e(error.message());
                }
            }

            @Override
            public void onFailure(Call<List<ScoreEntity>> call, Throwable t) {
                Timber.e(t.toString());
            }
        });

        return dialog;
    }

    private List<Long> usersToUserIds(List<UserEntity> users) {
        ArrayList<Long> userIds = new ArrayList<>();

        for (UserEntity userEntity : users) {
            userIds.add(userEntity.serverId);
        }

        return userIds;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fragment_dialog_group_users_url){
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Group url", mGroup.joinUrl);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(getActivity(), R.string.group_url_copied, Toast.LENGTH_SHORT).show();
        }
        else if (v.getId() == R.id.fragment_dialog_group_users_share_url) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.group_share));
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.group_share_text, mGroup.name, mGroup.joinUrl));
            shareIntent.setType("text/plain");
            startActivity(shareIntent);
        }
    }
}
