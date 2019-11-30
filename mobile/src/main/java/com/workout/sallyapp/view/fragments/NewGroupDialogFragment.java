package com.workout.sallyapp.view.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.workout.sallyapp.R;
import com.workout.sallyapp.model.entities.db.GroupEntity;
import com.workout.sallyapp.model.entities.db.UserEntity;
import com.workout.sallyapp.view.activities.MainActivity;

import java.util.ArrayList;

/**
 * Created by Yoav on 10-May-17.
 */

public class NewGroupDialogFragment extends DialogFragment {

    public static final String TAG = "NewGroupDialogFragment";
    public static final String GROUP_TO_CREATE = "GroupToCreate";
    public static final int RESULT_CODE_OK = 1;

    private Context mContext;
    private EditText mGroupNameEdit;

    public NewGroupDialogFragment() {
    }

    @Override
    public void onAttach(Context context) {
        mContext = context;

        super.onAttach(context);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final MaterialDialog dialog =
                new MaterialDialog.Builder(mContext)
                        .customView(R.layout.fragment_dialog_new_group, false)
                        .title(getString(R.string.action_new_group))
                        .autoDismiss(false)
                        .positiveText(R.string.create)
                        .negativeText(R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                // Check name as at least 1 character
                                if (mGroupNameEdit.getText().toString().length() <= 0 ) {
                                    Toast.makeText(mContext, "Group name can't be empty", Toast.LENGTH_SHORT).show();
                                } else {
                                    final GroupEntity groupEntity = new GroupEntity(mGroupNameEdit.getText().toString(), ((MainActivity) mContext).mCurrentUser);
                                    ArrayList<UserEntity> users = new ArrayList<>();
                                    users.add(((MainActivity) mContext).mCurrentUser);
                                    groupEntity.setUsers(users);

                                    Intent intent = new Intent();
                                    intent.putExtra(GROUP_TO_CREATE, groupEntity);
                                    getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_CODE_OK, intent);
                                    dismiss();
                                }
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dismiss();
                            }
                        })
                        .show();

        mGroupNameEdit = (EditText) dialog.getCustomView().findViewById(R.id.new_group_name);

        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get keyboard to show
        mGroupNameEdit.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
}
