package com.makotojava.android.debate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;

import com.makotojava.android.debate.model.PolicySpeechFactory;

@SuppressLint("InflateParams")
public class TimePickerFragment extends DialogFragment {
  
  private static final String TAG = TimePickerFragment.class.getName();
  
  private static final long NUMBER_OF_MILLIS_IN_ONE_MINUTE = PolicySpeechFactory.NUMBER_OF_MILLIS_IN_ONE_MINUTE;
  private static final long NUMBER_OF_MILLIS_IN_ONE_SECOND = 1000;

  public static final String ARG_MAX_MINUTES = "MAX_MINUTES";
  public static final String ARG_MILLIS = "ARG_MILLIS";
  public static final String ARG_RQID = "ARG_RQID";
  public static final String RESULT_MILLIS = "RESULT_MILLIS";
  public static final String RESULT_RQID = "RESULT_RQID";
  
  private View _thisView;
  
  private NumberPicker getMinutesNumberPicker() {
    NumberPicker ret = null;
    if (_thisView != null) {
      ret = (NumberPicker)_thisView.findViewById(R.id.minutes);
      // Add any handlers, listeners, etc. here...
    } else {
      throw new RuntimeException("Parent view is null!");
    }
    return ret;
  }
  
  private NumberPicker getSecondsNumberPicker() {
    NumberPicker ret = null;
    if (_thisView != null) {
      ret = (NumberPicker)_thisView.findViewById(R.id.seconds);
      // Add any handlers, listeners, etc. here...
    } else {
      throw new RuntimeException("Parent view is null!");
    }
    return ret;
  }
  
  public static TimePickerFragment newInstance(long millis, int maxMinutes, int requestCode) {
    Bundle args = new Bundle();
    args.putInt(ARG_MAX_MINUTES, maxMinutes);
    args.putLong(ARG_MILLIS, millis);
    args.putInt(ARG_RQID, requestCode);
    
    TimePickerFragment ret = new TimePickerFragment();
    ret.setArguments(args);
    
    return ret;
  }
  
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    // Inflate the view for picking minutes and seconds
    _thisView = getActivity().getLayoutInflater().inflate(R.layout.minutes_seconds_picker, null);
    // Set minutes attributes
    NumberPicker minutesNumberPicker = getMinutesNumberPicker();
    // Retrieve the MAX_MINUTES as a Fragment argument
    int maxMinutes = getArguments().getInt(ARG_MAX_MINUTES);
    long millis = getArguments().getLong(ARG_MILLIS);
    Log.d(TAG, "Millis coming in is => " + millis);
    minutesNumberPicker.setMaxValue(maxMinutes);
    int minutes = computeMinutesFromMillis(millis);
    Log.d(TAG, "Setting minutes to => " + minutes);
    minutesNumberPicker.setValue(minutes);
    minutesNumberPicker.setMinValue(0);
    minutesNumberPicker.setWrapSelectorWheel(true);
    // Seconds
    NumberPicker secondsNumberPicker = getSecondsNumberPicker();
    secondsNumberPicker.setMaxValue(59);
    int seconds = computeSecondsFromMillis(millis);
    Log.d(TAG, "Setting seconds to => " + seconds);
    secondsNumberPicker.setValue(seconds);
    secondsNumberPicker.setMinValue(0);
    secondsNumberPicker.setWrapSelectorWheel(true);
    //
    final int requestCode = getArguments().getInt(ARG_RQID);
    AlertDialog ret = new AlertDialog.Builder(getActivity())
      // Add other attributes here
      //.setView(numberPicker)
      .setView(_thisView)
      // Add Title
      .setTitle(R.string.minutes_seconds_picker_title)
      // Positive (Set) button
      .setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          sendResult(Activity.RESULT_OK, requestCode);
        }
      })
      // Cancel button
      .setCancelable(true)
      // Now create the MF
      .create();
    return ret;
  }
  
  private void sendResult(int resultCode, int requestCode) {
    if (getTargetFragment() != null) {
      Intent data = new Intent();
      if (resultCode == Activity.RESULT_OK) {
        NumberPicker minutesNumberPicker = getMinutesNumberPicker();
        int minutes = minutesNumberPicker.getValue();
        NumberPicker secondsNumberPicker = getSecondsNumberPicker();
        int seconds = secondsNumberPicker.getValue();
        long millis = computeMillisFromMinutesAndSeconds(minutes, seconds);
        Log.d(TAG, "sendResult(): requestCode => " + requestCode + ": minutes => " + minutes + ", seconds => " + seconds + ", millis => " + millis);
        data.putExtra(RESULT_MILLIS, millis);
      }
      getTargetFragment().onActivityResult(requestCode, resultCode, data);
    }
  }
  
  private int computeMinutesFromMillis(long millis) {
    return (int)(millis / NUMBER_OF_MILLIS_IN_ONE_MINUTE) % 60;
  }
  
  private int computeSecondsFromMillis(long millis) {
    return (int)(millis / 1000) % 60;
  }

  private long computeMillisFromMinutesAndSeconds(int minutes, int seconds) {
    long ret = 0L;
    if (minutes > 0) {
      ret += minutes * NUMBER_OF_MILLIS_IN_ONE_MINUTE;
    }
    if (seconds > 0) {
      ret += seconds * NUMBER_OF_MILLIS_IN_ONE_SECOND;
    }
    return ret;
  }

//*************************************************
//*           A    T    T    I    C  
//*************************************************

}
