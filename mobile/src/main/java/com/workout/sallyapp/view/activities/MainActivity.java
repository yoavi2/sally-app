package com.workout.sallyapp.view.activities;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.firebase.ui.auth.AuthUI;
import com.workout.sallyapp.R;
import com.workout.sallyapp.controller.retrofit.apis.GroupAPI;
import com.workout.sallyapp.controller.retrofit.error_handling.APIError;
import com.workout.sallyapp.controller.retrofit.error_handling.ErrorUtils;
import com.workout.sallyapp.databinding.ActivityMainBinding;
import com.workout.sallyapp.model.entities.db.GroupEntity;
import com.workout.sallyapp.model.repository.entity_repositories.GroupRepository;
import com.workout.sallyapp.model.repository.entity_repositories.GroupUserRepository;
import com.workout.sallyapp.model.repository.interfaces.RepositoryTransactionEvent;
import com.workout.sallyapp.view.activities.base.BaseSallyActivity;
import com.workout.sallyapp.view.adapters.ViewPagerAdapter;
import com.workout.sallyapp.view.fragments.GroupFragment;
import com.workout.sallyapp.view.fragments.MainFragment;
import com.workout.sallyapp.view.fragments.ScoreFragment;
import com.workout.sallyapp.view.services.MainActivityIntentService;
import com.workout.sallyapp.view.ui.TypefaceSpan;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.firebase.ui.auth.util.Preconditions.checkNotNull;

public class MainActivity extends BaseSallyActivity implements TabLayout.OnTabSelectedListener, GroupFragment.GroupsRefreshListener {

    @Inject
    GroupAPI mGroupApi;
    @Inject
    ErrorUtils mErrorUtils;
    @Inject
    GroupRepository mGroupRepository;
    @Inject
    GroupUserRepository mGroupUserRepository;

    private ActivityMainBinding bindingActivity;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private boolean mIsSignedIn;
    private int mGroupTabId;

    public static Intent createIntent(@NonNull Context context) {
        Intent intent = new Intent(checkNotNull(context, "context cannot be null"), MainActivity.class);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Data binding
        bindingActivity = DataBindingUtil.setContentView(this, R.layout.activity_main);

        AndroidInjection.inject(this);

        // Title with font
        SpannableString s = new SpannableString(getString(R.string.app_name));
        s.setSpan(new TypefaceSpan(this, "FrancoisOne-Regular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        bindingActivity.toolbar.setTitle(s);

        setSupportActionBar(bindingActivity.toolbar);

        handleDynamicLink();

        // Check if Signed in
        mIsSignedIn = mCurrentFireBaseUser != null;

        tabLayout = bindingActivity.tabLayout;
        viewPager = bindingActivity.content.pager;

        // Creating FABs (Based on whether a user is signed in)
        setUpFabs();

        //Creating our pager adapter (Based on whether a user is signed in)
        setupViewPager(viewPager);

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);

        //Adding onTabSelectedListener to swipe views
        tabLayout.addOnTabSelectedListener(this);

        MenuFabCreateCustomAnimation();
    }

    @Override
    public void onStart() {
        super.onStart();

        getGroups();
    }
    
    @Override
    protected void onResume() {
        super.onResume();

        getScores();
    }

    private void handleDynamicLink() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;

                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }

                        // Valid
                        if (deepLink != null) {
                            String groupLink = deepLink.getLastPathSegment();

                            Call<GroupEntity> call =
                                    mGroupApi
                                            .joinGroupLink(groupLink, mCurrentUser.serverId);

                            call.enqueue(new Callback<GroupEntity>() {
                                @Override
                                public void onResponse(Call<GroupEntity> call, Response<GroupEntity> response) {
                                    if (response.isSuccessful()) {
                                        final GroupEntity group = response.body();

                                        if (group != null) {

                                            // Check if already a member
                                            if (mGroupUserRepository.isUserMemberSync(group.serverId, mCurrentUser.serverId)) {
                                                Snackbar snackbar = Snackbar
                                                        .make(bindingActivity.fabMenu,
                                                                getString(R.string.already_group_member, group.name), Snackbar.LENGTH_LONG);

                                                // If the user is not in the groups tab, add an action to navigate there
                                                addSnackbarGroupsTabAction(snackbar);
                                                snackbar.show();
                                            } else {
                                                mGroupRepository.saveGroup(group, new RepositoryTransactionEvent() {
                                                    @Override
                                                    public void onSuccess() {
                                                        Snackbar snackbar = Snackbar
                                                                .make(bindingActivity.fabMenu,
                                                                        getString(R.string.join_group_success, group.name), Snackbar.LENGTH_LONG);

                                                        // If the user is not in the groups tab, add an action to navigate there
                                                        addSnackbarGroupsTabAction(snackbar);
                                                        snackbar.show();
                                                    }

                                                    @Override
                                                    public void onError(Throwable error) {
                                                    }
                                                });
                                            }
                                        } else {
                                            Snackbar.make(bindingActivity.fabMenu,
                                                    R.string.error_join_group, Snackbar.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        APIError error = mErrorUtils.parseError(response);
                                        Timber.e(error.message());

                                        Snackbar.make(bindingActivity.fabMenu,
                                                R.string.error_join_group, Snackbar.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<GroupEntity> call, Throwable t) {
                                    Timber.e(t.toString());
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Timber.w("getDynamicLink:onFailure", e);
                    }
                });
    }

    private void addSnackbarGroupsTabAction(Snackbar snackbar) {
        if (viewPager.getCurrentItem() != mGroupTabId) {
            snackbar.setAction(R.string.show, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewPager.setCurrentItem(mGroupTabId, true);
                }
            });
        }
    }

    private void setUpFabs() {

        if (mIsSignedIn) {
            bindingActivity.fabMenu.setClosedOnTouchOutside(true);

            bindingActivity.fabMulti.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // Check if there are groups
                    // TODO: This is sync. Maybe user loader to follow this number
                    if (mGroupRepository.getGroupCountSync(mCurrentUser.serverId) > 0) {
                        openActivity(ChooseGroupActivity.class);
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(bindingActivity.fabMenu,
                                        R.string.no_groups_msg, Snackbar.LENGTH_LONG);

                        // If the user is not in the groups tab, add an action to navigate there
                        addSnackbarGroupsTabAction(snackbar);

                        bindingActivity.fabMenu.close(true);
                        snackbar.show();
                    }
                }
            });

            bindingActivity.fabSingle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openActivity(NewWorkoutActivitySingle.class);
                }
            });
        }

    }

    private void openActivity(Class<?> cls) {
        Intent myIntent = new Intent(this, cls);
        this.startActivity(myIntent);
        bindingActivity.fabMenu.close(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        } else if (id == R.id.action_logout) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();
                        }
                    });

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        GroupFragment groupFragment = null;

        // Signed in user layout
        if (mIsSignedIn) {
            // Main fragment
            adapter.addFragment(MainFragment.newInstance(mCurrentFireBaseUser.getDisplayName(), mCurrentFireBaseUser.getPhotoUrl()),
                    getResources().getString(R.string.main_tab_name));

            // Scores fragment
            ScoreFragment scoreFragment = ScoreFragment.newInstance(1);
            adapter.addFragment(scoreFragment, getResources().getString(R.string.score_tab_name));

            // Groups fragment
            groupFragment = GroupFragment.newInstance();
            adapter.addFragment(groupFragment, getResources().getString(R.string.group_tab_name));
            mGroupTabId = 2;
        }
        // Anonymous user
        else {

        }

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    protected void onDestroy() {
        tabLayout.removeOnTabSelectedListener(this);
        bindingActivity.fabSingle.setOnClickListener(null);
        bindingActivity.fabMulti.setOnClickListener(null);

        super.onDestroy();
    }

    private void MenuFabCreateCustomAnimation() {
        AnimatorSet set = new AnimatorSet();

        ObjectAnimator scaleOutX = ObjectAnimator.ofFloat(bindingActivity.fabMenu.getMenuIconView(), "scaleX", 1.0f, 0.2f);
        ObjectAnimator scaleOutY = ObjectAnimator.ofFloat(bindingActivity.fabMenu.getMenuIconView(), "scaleY", 1.0f, 0.2f);

        ObjectAnimator scaleInX = ObjectAnimator.ofFloat(bindingActivity.fabMenu.getMenuIconView(), "scaleX", 0.2f, 1.0f);
        ObjectAnimator scaleInY = ObjectAnimator.ofFloat(bindingActivity.fabMenu.getMenuIconView(), "scaleY", 0.2f, 1.0f);

        scaleOutX.setDuration(50);
        scaleOutY.setDuration(50);

        scaleInX.setDuration(150);
        scaleInY.setDuration(150);

        scaleInX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                bindingActivity.fabMenu.getMenuIconView().setImageResource(bindingActivity.fabMenu.isOpened()
                        ? R.drawable.ic_play_36dp : R.drawable.vector_drawable_close_button);
            }
        });

        set.play(scaleOutX).with(scaleOutY);
        set.play(scaleInX).with(scaleInY).after(scaleOutX);
        set.setInterpolator(new OvershootInterpolator(2));

        bindingActivity.fabMenu.setIconToggleAnimatorSet(set);
    }

    private void getScores() {
        getFromNetworkIntentService(MainActivityIntentService.REQUEST_TYPE_SCORES);
    }

    private void getGroups() {
        getFromNetworkIntentService(MainActivityIntentService.REQUEST_TYPE_GROUPS);
    }

    private void getFromNetworkIntentService(String type) {
        Intent intent = new Intent(this, MainActivityIntentService.class);
        intent.putExtra(MainActivityIntentService.REQUEST_TYPE, type);
        intent.putExtra(MainActivityIntentService.USER_SERVER_ID, mCurrentUser.serverId);
        startService(intent);
    }

    @Override
    public void onGroupsRefresh() {
        getGroups();
    }
}
