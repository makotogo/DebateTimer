package com.makotojava.android.debate;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

public class SpeechCountDownTimerWithAlarm extends SpeechCountDownTimer {
  
  private static final long serialVersionUID = -8697031701525710649L;

  private static final String TAG = SpeechCountDownTimerWithAlarm.class.getName();

  public SpeechCountDownTimerWithAlarm(long millisInFuture, long countDownInterval, SpeechCountDownTimerCallback callback) {
    super(millisInFuture, countDownInterval, callback);
  }

  public SpeechCountDownTimerWithAlarm(long millisInFuture, SpeechCountDownTimerCallback callback) {
    super(millisInFuture, callback);
  }
  
  private transient Ringtone _notification;
  
  private boolean _playingNotification;
  public boolean isPlayingNotification() {
    return _playingNotification;
  }
  
  public void playNotification(Context context) {
    Log.d(TAG, "Playing Notification: " );
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    Uri ringtoneUri = Uri.parse(sharedPreferences.getString(SettingsActivity.PREF_KEY_ALARM_EXPIRATION_SOUND, ""));
    _notification = RingtoneManager.getRingtone(context, ringtoneUri);
    //_notification = RingtoneManager.getRingtone(context, notification);
    _notification.play();
    _playingNotification = true;
  }
  
  public void stopNotification(Context context) {
    Log.d(TAG, "Stopping Notification: " );
    if (_notification != null) {
      _notification.stop();
    }
    _playingNotification = false;
  }

}
