package com.makotojava.android.debate;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;

public class SettingsActivity extends Activity {
  
  //************************************
  //* K E Y S   L I S T E D    H E R E *
  //************************************
  public static final String PREF_CAT_KEY_SPEECH_TIMES = "pref_cat_key_speechTimes";
  public static final String PREF_KEY_CONSTRUCTIVE_SPEECH_TIME = "pref_key_constructiveSpeechTime";
  public static final String PREF_KEY_REBUTTAL_TIME = "pref_key_rebuttalTime";
  public static final String PREF_KEY_CROSSEX_TIME = "pref_key_crossexTime";
  public static final String PREF_KEY_PREP_TIME = "pref_key_prepTime";
  
  public static final String PREF_CAT_KEY_ALARM = "pref_cat_key_alarm";
  public static final String PREF_KEY_ALARM_NOTIFY_AT_EXPIRATION = "pref_key_alarm_notifyAtExpiration";
  public static final String PREF_KEY_ALARM_EXPIRATION_SOUND = "pref_key_alarm_expirationSound";
  
  public static final String PREF_CAT_GENERAL = "pref_cat_general";
  public static final String PREF_KEY_SHOW_TENTHS_OF_SECONDS = "pref_key_show_tenths_of_seconds";
  public static final String PREF_KEY_ANIMATE_TIMER_WHEN_BELOW_THRESHOLD = "pref_key_animateTimerWhenBelowThreshold";
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    FragmentManager fragMan = getFragmentManager();
    fragMan.beginTransaction()
        .replace(android.R.id.content, new SettingsFragment())
        .commit();

  }

}
