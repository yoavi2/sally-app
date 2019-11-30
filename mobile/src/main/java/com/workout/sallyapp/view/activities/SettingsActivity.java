package com.workout.sallyapp.view.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.workout.sallyapp.BuildConfig;
import com.workout.sallyapp.R;
import com.workout.sallyapp.databinding.ActivitySettingsBinding;
import com.workout.sallyapp.model.entities.network.UserPref;
import com.workout.sallyapp.view.activities.base.BaseSallyActivity;
import com.workout.sallyapp.view.services.SettingsIntentService;

/**
 * Created by Yoav on 09-Aug-17.
 */
public class SettingsActivity extends BaseSallyActivity {

    public static final String KEY_PREF_PUSH_NOTIFICATION = "pref_highscore_push_notification";

    private ActivitySettingsBinding bindingActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Data bindingActivity
        bindingActivity = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        setSupportActionBar(bindingActivity.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction().replace(R.id.fragment_container, new Prefs1Fragment()).commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    /**
     * This fragment shows the preferences for the first header.
     */
    public static class Prefs1Fragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        public static final String PREF_VERSION = "pref_version";

        private boolean hasNetworkPrefsChanged;
        private UserPref initialNetworkUserPref;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);

            // SETTING VERSION HERE
            Preference versionPreference = findPreference(PREF_VERSION);
            versionPreference.setSummary(BuildConfig.VERSION_NAME);

            // Get the initial state of network user preferences
            initialNetworkUserPref =
                    new UserPref(((SettingsActivity)getActivity()).mCurrentUser.serverId,
                            getPreferenceScreen().getSharedPreferences().getBoolean(KEY_PREF_PUSH_NOTIFICATION, true));
        }

        @Override
        public void onResume() {
            super.onResume();

            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(KEY_PREF_PUSH_NOTIFICATION)) {
                boolean isPushEnabled  = sharedPreferences.getBoolean(key, true);

                hasNetworkPrefsChanged = true;
            }
        }

        @Override
        public void onStop() {
            super.onStop();

            // Check if need to update network preferences
            if (hasNetworkPrefsChanged) {
                // Get the current userPref
                UserPref currUserPref =
                        new UserPref(((SettingsActivity)getActivity()).mCurrentUser.serverId,
                                getPreferenceScreen().getSharedPreferences().getBoolean(KEY_PREF_PUSH_NOTIFICATION, true));

                // There was an actual change
                if (!currUserPref.equals(initialNetworkUserPref)) {
                    // Send to server
                    sendUserPrefToServer(currUserPref);
                }

                hasNetworkPrefsChanged = false;
            }
        }

        private void sendUserPrefToServer(UserPref userPref) {
            Intent intent = new Intent(getActivity(), SettingsIntentService.class);
            intent.putExtra(SettingsIntentService.USER_PREFERENCES, userPref);
            getActivity().startService(intent);
        }
    }

}