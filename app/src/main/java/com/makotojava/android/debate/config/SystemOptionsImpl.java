/*
 *     Copyright 2016 Makoto Consulting Group, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.makotojava.android.debate.config;

import com.makotojava.android.debate.SettingsActivity;
import com.makotojava.android.debate.model.PolicySpeechFactory;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;

public class SystemOptionsImpl implements SystemOptions {
  
  private Context _context;
  public SystemOptionsImpl(Context context) {
    _context = context;
  }

  @Override
  public Context getContext() {
    return _context;
  }

  private Integer _prepTimeInMinutes;
  @Override
  public int getPrepTimeInMinutes() {
    if (_prepTimeInMinutes == null) {
      SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
      String timeAsString =  sharedPreferences.getString(SettingsActivity.PREF_KEY_PREP_TIME, 
              Integer.toString(PolicySpeechFactory.PREP_TIME_IN_MINUTES));
      _prepTimeInMinutes = Integer.parseInt(timeAsString);
    }
    return _prepTimeInMinutes.intValue();
  }

  private Integer _crossExTimeInMinutes;
  @Override
  public int getCrossExTimeInMinutes() {
    if (_crossExTimeInMinutes == null) {
      SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
      String timeAsString =  sharedPreferences.getString(SettingsActivity.PREF_KEY_CROSSEX_TIME, 
              Integer.toString(PolicySpeechFactory.CROSSEX_TIME_IN_MINUTES));
      _crossExTimeInMinutes = Integer.parseInt(timeAsString);
    }
    return _crossExTimeInMinutes.intValue();
  }

  private Integer _constructiveTimeInMinutes;
  @Override
  public int getConstructiveTimeInMinutes() {
    if (_constructiveTimeInMinutes == null) {
      SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
      String timeAsString =  sharedPreferences.getString(SettingsActivity.PREF_KEY_CONSTRUCTIVE_SPEECH_TIME, 
              Integer.toString(PolicySpeechFactory.CONSTRUCTIVE_SPEECH_DURATION_IN_MINUTES));
      _constructiveTimeInMinutes = Integer.parseInt(timeAsString);
    }
    return _constructiveTimeInMinutes.intValue();
  }

  private Integer _rebuttalTimeInMinutes;
  @Override
  public int getRebuttalTimeInMinutes() {
    if (_rebuttalTimeInMinutes == null) {
      SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
      String timeAsString =  sharedPreferences.getString(SettingsActivity.PREF_KEY_REBUTTAL_TIME, 
              Integer.toString(PolicySpeechFactory.REBUTTAL_DURATION_IN_MINUTES));
      _rebuttalTimeInMinutes = Integer.parseInt(timeAsString);
    }
    return _rebuttalTimeInMinutes.intValue();
  }

  private Boolean _showTenthsOfSeconds;
  @Override
  public boolean showTenthsOfSeconds() {
    if (_showTenthsOfSeconds == null) {
      SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
      _showTenthsOfSeconds = sharedPreferences.getBoolean(SettingsActivity.PREF_KEY_SHOW_TENTHS_OF_SECONDS, true);
    }
    return _showTenthsOfSeconds.booleanValue();
  }

  private Boolean _animateWhenTimerBelowThreshold;
  @Override
  public boolean animateWhenTimerBelowThreshold() {
    if (_animateWhenTimerBelowThreshold == null) {
      SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
      _animateWhenTimerBelowThreshold = sharedPreferences.getBoolean(SettingsActivity.PREF_KEY_ANIMATE_TIMER_WHEN_BELOW_THRESHOLD, true);
    }
    return _animateWhenTimerBelowThreshold.booleanValue();
  }

  private Boolean _notifyAtTimerExpiration;
  @Override
  public boolean notifyAtTimerExpiration() {
    if (_notifyAtTimerExpiration == null) {
      SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
      _notifyAtTimerExpiration = sharedPreferences.getBoolean(SettingsActivity.PREF_KEY_ALARM_NOTIFY_AT_EXPIRATION, true);
    }
    return _notifyAtTimerExpiration.booleanValue();
  }

  private Ringtone _timerExpirationSound;
  @Override
  public Ringtone getTimerExpirationSound() {
    if (_timerExpirationSound == null) {
      SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
      String expirationSound = sharedPreferences.getString(SettingsActivity.PREF_KEY_ALARM_EXPIRATION_SOUND, "None");
      Uri ringtoneUri = Uri.parse(expirationSound);
      _timerExpirationSound = RingtoneManager.getRingtone(getContext(), ringtoneUri);
    }
    return _timerExpirationSound;
  }

}
