package com.makotojava.android.debate;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.RingtonePreference;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makotojava.android.debate.config.SystemOptions;
import com.makotojava.android.debate.config.SystemOptionsImpl;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
  
  private static final String TAG = SettingsFragment.class.getName();
  
  private transient SystemOptions _systemOptions;
  private SystemOptions getSystemOptions() {
    if (_systemOptions == null) {
      _systemOptions = new SystemOptionsImpl(getActivity());
    }
    return _systemOptions;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    addPreferencesFromResource(R.xml.preferences);
    
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View ret = super.onCreateView(inflater, container, savedInstanceState);
    // Set the Summary values of the various fields according to the
    /// Android guidelines
    // Constructive Speech Time
    EditTextPreference editTextPreference = (EditTextPreference)findPreference(SettingsActivity.PREF_KEY_CONSTRUCTIVE_SPEECH_TIME);
    editTextPreference.setSummary(
        Integer.toString(getSystemOptions().getConstructiveTimeInMinutes())
    );
    // Rebuttal Time
    editTextPreference = (EditTextPreference)findPreference(SettingsActivity.PREF_KEY_REBUTTAL_TIME);
    editTextPreference.setSummary(
        Integer.toString(getSystemOptions().getRebuttalTimeInMinutes())
    );
    // Cross Examination Time
    editTextPreference = (EditTextPreference)findPreference(SettingsActivity.PREF_KEY_CROSSEX_TIME);
    editTextPreference.setSummary(
        Integer.toString(getSystemOptions().getCrossExTimeInMinutes())
    );
    // Prep Time
    editTextPreference = (EditTextPreference)findPreference(SettingsActivity.PREF_KEY_PREP_TIME);
    editTextPreference.setSummary(
        Integer.toString(getSystemOptions().getPrepTimeInMinutes())
    );
    // Notify at Expiration
    CheckBoxPreference checkboxPreference = (CheckBoxPreference)findPreference(SettingsActivity.PREF_KEY_ALARM_NOTIFY_AT_EXPIRATION);
    checkboxPreference.setSummary(
        toNiceBooleanSummary(
            getSystemOptions().notifyAtTimerExpiration()
        )
    );
    
    RingtonePreference ringtonePreference = (RingtonePreference)findPreference(SettingsActivity.PREF_KEY_ALARM_EXPIRATION_SOUND);
    Ringtone ringtone = getSystemOptions().getTimerExpirationSound();
    ringtonePreference.setSummary(ringtone.getTitle(getActivity()));
    
    // Show Tenths of Seconds
    checkboxPreference = (CheckBoxPreference)findPreference(SettingsActivity.PREF_KEY_SHOW_TENTHS_OF_SECONDS);
    checkboxPreference.setSummary(
        toNiceBooleanSummary(
            getSystemOptions().showTenthsOfSeconds()
        )
    );
    
    // Animate Timer when Below 1 Minute
    checkboxPreference = (CheckBoxPreference)findPreference(SettingsActivity.PREF_KEY_ANIMATE_TIMER_WHEN_BELOW_THRESHOLD);
    checkboxPreference.setSummary(
        toNiceBooleanSummary(
            getSystemOptions().animateWhenTimerBelowThreshold()
        )
    );
    
    return ret;
  }
  
  @Override
  public void onStart() {
    super.onResume();
    getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
  }

//  @Override
//  public void onResume() {
//    super.onResume();
//    getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
//  }

//  @Override
//  public void onPause() {
//    super.onPause();
//    getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
//  }
  
  @Override
  public void onStop() {
    super.onPause();
    getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
  }
  
  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    // Crack the keys and update the Summary attribute of the key that changed
    Log.d(TAG, "Shared preference changed: key => " + key);
    Preference preference = findPreference(key);
    if (isStringTypeKey(key)) {
      String summary = sharedPreferences.getString(key, "");
      Log.d(TAG, "New value => " + summary);
      preference.setSummary(summary);
    } else if (isIntegerTypeKey(key)) {
      int summary = sharedPreferences.getInt(key, 0);
      Log.d(TAG, "New value => " + summary);
      preference.setSummary(Integer.toString(summary));
    } else if (isBooleanTypeKey(key)) {
      String summary = toNiceBooleanSummary(sharedPreferences.getBoolean(key, false));
      Log.d(TAG, "New value => " + summary);
      preference.setSummary(summary);
    } else if (isRingtoneTypeKey(key)) {
      String expirationSound = sharedPreferences.getString(key, "None");
      Uri ringtoneUri = Uri.parse(expirationSound);
      Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), ringtoneUri);
      Log.d(TAG, "Expiration sound => " + ringtone.getTitle(getActivity()));
      preference.setSummary(ringtone.getTitle(getActivity()));
    } else {
      Log.w(TAG, "Could not decipher the type of key '" + key + "'...");
    }
  }
  
  //*****
  // TODO: Potential Framework Methods?
  //*****
  protected String toNiceBooleanSummary(Boolean value) {
    return (value == true)
        ? "Yes"
        : "No"
          ;
  }
  
  protected boolean isStringTypeKey(String key) {
    return 
        key.equals(SettingsActivity.PREF_KEY_CONSTRUCTIVE_SPEECH_TIME) ||
        key.equals(SettingsActivity.PREF_KEY_CROSSEX_TIME) ||
        key.equals(SettingsActivity.PREF_KEY_PREP_TIME) ||
        key.equals(SettingsActivity.PREF_KEY_REBUTTAL_TIME)
        ;
  }
  
  protected boolean isIntegerTypeKey(String key) {
    return
        false // There are no Integer keys at the moment
        ;
  }
  
  protected boolean isBooleanTypeKey(String key) {
    return
        key.equals(SettingsActivity.PREF_KEY_ALARM_NOTIFY_AT_EXPIRATION) ||
        key.equals(SettingsActivity.PREF_KEY_ANIMATE_TIMER_WHEN_BELOW_THRESHOLD) ||
        key.equals(SettingsActivity.PREF_KEY_SHOW_TENTHS_OF_SECONDS)
        ;
  }
  
  protected boolean isRingtoneTypeKey(String key) {
    return
        key.equals(SettingsActivity.PREF_KEY_ALARM_EXPIRATION_SOUND)
    ;
  }

}
