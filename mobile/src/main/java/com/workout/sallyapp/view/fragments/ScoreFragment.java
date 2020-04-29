package com.workout.sallyapp.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.kennyc.view.MultiStateView;
import com.raizlabs.android.dbflow.list.FlowCursorList;
import com.workout.sallyapp.R;
import com.workout.sallyapp.controller.retrofit.apis.UserAPI;
import com.workout.sallyapp.controller.retrofit.error_handling.APIError;
import com.workout.sallyapp.controller.retrofit.error_handling.ErrorUtils;
import com.workout.sallyapp.databinding.FragmentScoreListBinding;
import com.workout.sallyapp.model.entities.db.ScoreEntity;
import com.workout.sallyapp.model.repository.entity_repositories.ScoreRepository;
import com.workout.sallyapp.model.repository.entity_repositories.UserRepository;
import com.workout.sallyapp.model.repository.interfaces.RepositoryTransactionEvent;
import com.workout.sallyapp.view.activities.MainActivity;
import com.workout.sallyapp.view.adapters.ScoreRecyclerViewAdapter;
import com.workout.sallyapp.view.chart.XAxisDateFormatter;
import com.workout.sallyapp.view.chart.YAxisTimeFormatter;
import com.workout.sallyapp.view.loaders.ScoresLoader;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ScoreFragment extends Fragment implements LoaderManager.LoaderCallbacks<FlowCursorList<ScoreEntity>>, ScoreRecyclerViewAdapter.OnScoreListInteraction {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final int SCORE_FLOW_CURSOR_LIST_LOADER_ID = 2;
    private static final int MINIMUM_SCORES_FOR_CHART = 2;

    @Inject
    UserAPI mUserApi;
    @Inject
    ErrorUtils mErrorUtils;
    @Inject
    ScoreRepository mScoreRepository;
    @Inject
    UserRepository mUserRepository;

    private int mSongLength;
    private LineChart mChart;
    private RecyclerView mRecyclerView;
    private ScoreRecyclerViewAdapter mAdapter;
    private int mColumnCount = 1;

    private FragmentScoreListBinding binding;
    private LineData mLineData;
    private XAxisDateFormatter mXAxisFormatter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ScoreFragment() {
    }

    @SuppressWarnings("unused")
    public static ScoreFragment newInstance(int columnCount) {
        ScoreFragment fragment = new ScoreFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);

        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentScoreListBinding.inflate(inflater, container, false);

        mSongLength = ((MainActivity) getActivity()).mCurrentChallenge.length;

        // Chart
        mChart = binding.chart;
        setUpChart();

        // List
        mRecyclerView = binding.list;
        mAdapter = new ScoreRecyclerViewAdapter(mSongLength, this);

        if (mColumnCount <= 1) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(binding.getRoot().getContext(), mColumnCount));
        }

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setNestedScrollingEnabled(false); // Enables smooth scrolling


        // db
        getLoaderManager().initLoader(SCORE_FLOW_CURSOR_LIST_LOADER_ID, null, this);

        return binding.getRoot();
    }

    private void setUpChart() {
        // Chart
        mChart.getDescription().setEnabled(false);
        mChart.setDrawGridBackground(false);
        mChart.setDrawBorders(false);
        mChart.setAutoScaleMinMaxEnabled(true);

        mChart.setPinchZoom(false);
        mChart.setDoubleTapToZoomEnabled(false);
        mChart.setTouchEnabled(false);
        mChart.getLegend().setEnabled(false);

        // Data
        mLineData = new LineData();
        mLineData.setDrawValues(false);

        // X axis
        mXAxisFormatter = new XAxisDateFormatter();
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(2);
        xAxis.setValueFormatter(mXAxisFormatter);

        // Y axis
        IAxisValueFormatter yAxisFormatter = new YAxisTimeFormatter();
        YAxis yAxis = mChart.getAxisLeft();
        yAxis.setAxisMinimum(0);
        yAxis.setAxisMaximum(mSongLength);
        yAxis.setLabelCount(3, false);
        yAxis.setDrawGridLines(false);
        yAxis.setValueFormatter(yAxisFormatter);
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        mChart.getAxisRight().setEnabled(false);

        mChart.setData(mLineData);
    }

    private void showOrHideViews() {

        int count = mAdapter.getItemCount();

        // Check if whole view should be shown
        if (count <= 0) {
            binding.multiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
        } else {
            binding.multiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);

            // Check if graph should be shown
            if (count < MINIMUM_SCORES_FOR_CHART) {
                mChart.setVisibility(View.GONE);
            } else {
                mChart.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public Loader<FlowCursorList<ScoreEntity>> onCreateLoader(final int id, final Bundle args) {
        return new ScoresLoader(getActivity(), mUserRepository.getCurrentUserId());
    }

    @Override
    public void onLoadFinished(Loader<FlowCursorList<ScoreEntity>> loader, FlowCursorList<ScoreEntity> data) {

        // List
        mAdapter.setCursorList(data);

        // Chart
        if (data.getCount() > 0) {
            LineDataSet dataSet = setUpLineDataSet(data);
            mLineData = new LineData(dataSet);
            mChart.setData(mLineData);
            mChart.invalidate();
        }

        showOrHideViews();
    }

    @NonNull
    private LineDataSet setUpLineDataSet(FlowCursorList<ScoreEntity> data) {
        LineDataSet dataSet = new LineDataSet(getChartEntitiesFromScores(data), getString(R.string.graph_scores_label));
        dataSet.setDrawValues(false);
        dataSet.setDrawFilled(true);
        dataSet.setColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        dataSet.setCircleColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        dataSet.setFillColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        dataSet.setFillAlpha(70);
        return dataSet;
    }

    private List<Entry> getChartEntitiesFromScores(FlowCursorList<ScoreEntity> data) {

        ArrayList<Entry> entries = new ArrayList<>((int)data.getCount());

        long first = mXAxisFormatter.getValuefromDate(data.getItem(0).date);
        mXAxisFormatter.setMinValue(first);

        for (ScoreEntity score : data) {
            // We normalize the values by decreasing the oldest timestamp
            // We don't care about milliseconds so we can decrease the number and prevent float point errors
            entries.add(new Entry(mXAxisFormatter.getValuefromDate(score.date) - first, score.durationInSec)); //TODO: Data is converted to date and then back to long. hmmm
        }

        return entries;
    }

    @Override
    public void onLoaderReset(Loader<FlowCursorList<ScoreEntity>> loader) {
        // List
        mAdapter.setCursorList(null);

        // Chart
        mLineData.clearValues();
        mChart.invalidate();
    }

    @Override
    public void onScoreLongClicked(int position, final ScoreEntity score) {

        // Show delete dialog
        new MaterialDialog.Builder(getActivity())
                .title(R.string.delete_score_title)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .icon(ContextCompat.getDrawable(getActivity(), R.drawable.trash_24_black))
                .autoDismiss(true)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        mScoreRepository.deleteScore(score, new RepositoryTransactionEvent() {
                            @Override
                            public void onSuccess() {
                                Call<Void> call = mUserApi.
                                        deleteUserScore(score.user.serverId, score.serverId);
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

                            @Override
                            public void onError(Throwable error) {
                            }
                        });
                    }
                })
                .show();
    }

    @Override
    public void onDestroyView() {
        mAdapter.setListener(null);

        super.onDestroyView();
    }
}
