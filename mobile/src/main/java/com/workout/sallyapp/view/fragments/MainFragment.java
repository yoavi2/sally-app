package com.workout.sallyapp.view.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;
import com.workout.sallyapp.databinding.FragmentMainBinding;
import com.workout.sallyapp.utilities.TimeUtility;
import com.workout.sallyapp.view.activities.MainActivity;
import com.workout.sallyapp.view.loaders.HighScoreLoader;
import com.workout.sallyapp.view.ui.CircleTransform;
import com.workout.sallyapp.view.ui.InitialsGenerator;

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Integer> {

    public static final int HISHSCORE_CURSOR_LIST_LOADER_ID = 1;
    public static String ARG_USER_NAME = "main_fragment_user_name";
    public static String ARG_USER_PHOTO_URL = "arg_user_photo_url";

    private FragmentMainBinding binding;

    private String mUserName;
    private Uri mUserPhotoUrl;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MainFragment.
     */
    public static MainFragment newInstance(String userName, Uri userPhoto) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();

        args.putString(ARG_USER_NAME, userName);
        args.putParcelable(ARG_USER_PHOTO_URL, userPhoto);

        fragment.setArguments(args);

        return fragment;
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();

        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mUserName = getArguments().getString(ARG_USER_NAME);
            mUserPhotoUrl = getArguments().getParcelable(ARG_USER_PHOTO_URL);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mUserName != null) {
            binding.fragmentMainUserName.setText(mUserName);
        }
        else {
            binding.fragmentMainUserName.setText("Hello friend");
        }

        TextDrawable initialsDrawable =
                new InitialsGenerator(true).createCircle(mUserName, ((MainActivity)getActivity()).mCurrentUser.serverId);

        if (mUserPhotoUrl != null) {
            Picasso.with(getActivity()).load(mUserPhotoUrl).error(initialsDrawable).transform(new CircleTransform()).into(binding.fragmentMainUserImg);
        } else {
            binding.fragmentMainUserImg.setImageDrawable(initialsDrawable);
        }

        // db
        getLoaderManager().initLoader(HISHSCORE_CURSOR_LIST_LOADER_ID, null, this);
    }

    @Override
    public Loader<Integer> onCreateLoader(int id, Bundle args) {
        return new HighScoreLoader(getActivity(), ((MainActivity) getActivity()).mCurrentUser.serverId);
    }

    @Override
    public void onLoadFinished(Loader<Integer> loader, Integer data) {
        setHighscore(data);
    }

    @Override
    public void onLoaderReset(Loader<Integer> loader) {
        setHighscore(null);
    }

    private void setHighscore(Integer score) {
        if (score != null && score > 0) {
            binding.fragmentMainTrophy.setVisibility(View.VISIBLE);
            binding.fragmentMainHighscoreLabel.setVisibility(View.VISIBLE);
            binding.fragmentMainHighscoreText.setVisibility(View.VISIBLE);

            binding.fragmentMainHighscoreText.setText(TimeUtility.formatSeconds(score));
        } else {
            binding.fragmentMainTrophy.setVisibility(View.INVISIBLE);
            binding.fragmentMainHighscoreLabel.setVisibility(View.INVISIBLE);
            binding.fragmentMainHighscoreText.setVisibility(View.INVISIBLE);
        }
    }

}
