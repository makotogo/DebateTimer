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

package com.makotojava.android.debate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makotojava.android.debate.SpeechCountDownTimer.SpeechCountDownTimerCallback;
import com.makotojava.android.debate.config.SystemOptions;
import com.makotojava.android.debate.config.SystemOptionsImpl;
import com.makotojava.android.debate.model.PolicySpeech;
import com.makotojava.android.debate.model.PolicySpeechFactory;
import com.makotojava.android.debate.model.PolicySpeechSingleton;

public class PolicySpeechFragment extends Fragment {
  
//  private static final boolean _testing = false;
  
  // Log TAGs
  private static final String TAG = PolicySpeechFragment.class.getName();

  private static final long NUMBER_OF_MILLIS_IN_ONE_MINUTE = 
      PolicySpeechFactory.NUMBER_OF_MILLIS_IN_ONE_MINUTE;

  // Property names used to store and retrieve state
  private static final String CLASS_PREFIX = PolicySpeechFragment.class.getName();
  public static final String ARG_INDEX = CLASS_PREFIX + ".ARG_INDEX";
//  public static final String CURRENT_STATE = CLASS_PREFIX + ".CURRENT_STATE";
  public static final String SPEECH_TIMER = CLASS_PREFIX + ".SPEECH_TIMER";
  public static final String CROSSEX_TIMER = CLASS_PREFIX + ".CROSSEX_TIMER";
  public static final String AFFPREP_TIMER = CLASS_PREFIX + ".AFFPREP_TIMER";
  public static final String NEGPREP_TIMER = CLASS_PREFIX + ".NEGPREP_TIMER";

  public static final String MINUTES_SECONDS_PICKER_TITLE = "Select Minutes/Seconds";

  public static final int RQID_SPEECHTIMER = 0;
  public static final int RQID_CROSSEXTIMER = 1;
  public static final int RQID_AFFPREPTIMER = 2;
  public static final int RQID_NEGPREPTIMER = 3;
  
  private enum STATE {
    THIS_IS_THE_ACTIVE_SPEECH,
    NO_ACTIVE_SPEECH,
    //NEW,
    //INITIAL,
    SPEECH_TIMER_RUNNING,
    SPEECH_TIMER_STOPPED,
    SPEECH_TIMER_EXHAUSTED,
    SPEECH_TIMER_SET,
    CROSSEX_TIMER_RUNNING,
    CROSSEX_TIMER_STOPPED,
    CROSSEX_TIMER_EXHAUSTED,
    CROSSEX_TIMER_SET,
    AFFPREP_TIMER_RUNNING,
    AFFPREP_TIMER_STOPPED,
    AFFPREP_TIMER_EXHAUSTED,
    AFFPREP_TIMER_SET,
    NEGPREP_TIMER_RUNNING,
    NEGPREP_TIMER_STOPPED,
    NEGPREP_TIMER_EXHAUSTED,
    NEGPREP_TIMER_SET,
    //
    SAVING_INSTANCE_STATE,
    INSTANCE_STATE_SAVED,
    INSTANCE_STATE_RESTORED,
    //
    TESTING,
  };
  
  private transient SystemOptions _systemOptions;
  private SystemOptions getSystemOptions() {
    if (_systemOptions == null) {
      _systemOptions = new SystemOptionsImpl(getActivity());
    }
    return _systemOptions;
  }
  
  private PolicySpeechSingleton getPolicySpeechSingleton() {
    return PolicySpeechSingleton.instance(getActivity());
  }
  
  private Bundle _savedInstanceState;//workaround
  
  @Override
  public void onSaveInstanceState(Bundle outState) {
    Log.d(TAG, "saveInstanceState(): Saving Fragment State for Speech => " + _speech);
    super.onSaveInstanceState(outState);
    outState.putSerializable(SPEECH_TIMER, _speechTimer);
    outState.putSerializable(CROSSEX_TIMER, _crossexTimer);
    outState.putSerializable(AFFPREP_TIMER, getAffirmativePrepTimer());
    outState.putSerializable(NEGPREP_TIMER, getNegativePrepTimer());
  }

  private int _speechIndex;
  private PolicySpeech _speech;
  
  // Default onSaveInstanceState() should take care of these
  private TextView _speechTimerTextView;
  private TextView _crossexTimerTextView;
  private TextView _affirmativePrepTimerTextView;
  private TextView _negativePrepTimerTextView;
  
//  // ValueAnimator
//  private ValueAnimator _valueAnimator;
  
  // These, however, we have to save ourselves
  private SpeechCountDownTimerWithAlarm _speechTimer;
  private SpeechCountDownTimerWithAlarm _crossexTimer;
  private SpeechCountDownTimerWithAlarm _affirmativePrepTimer;
  private SpeechCountDownTimerWithAlarm getAffirmativePrepTimer() {
    if (_affirmativePrepTimer == null) {
      _affirmativePrepTimer = createAffPrepCountDownTimer(getPolicySpeechSingleton().getAffirmativePrepRemainingTime());
    }
    return _affirmativePrepTimer;
  }
  private SpeechCountDownTimerWithAlarm _negativePrepTimer;
  private SpeechCountDownTimerWithAlarm getNegativePrepTimer() {
    if (_negativePrepTimer == null) {
      _negativePrepTimer = createNegPrepCountDownTimer(getPolicySpeechSingleton().getNegativePrepRemainingTime());
    }
    return _negativePrepTimer;
  }
  
  private View _rootView;
  private View getRootView() {
    return _rootView;
  }
  
  
  private long countdownInterval;
  private long getCountdownInterval() {
    countdownInterval = SpeechCountDownTimer.DEFAULT_COUNTDOWN_INTERVAL;
    if (getSystemOptions().showTenthsOfSeconds()) {
      countdownInterval = 110;
    }
    return countdownInterval;
  }
  
  public static PolicySpeechFragment newInstance(int index) {
    Log.d(TAG, "newInstance(): index => " + index);
    PolicySpeechFragment ret = new PolicySpeechFragment();

    Bundle fragmentArgs = new Bundle();
    fragmentArgs.putInt(ARG_INDEX, index);
    ret.setArguments(fragmentArgs);
    
    return ret;
  }
  
  @Override
  public void onStop() {
    super.onStop();
    Log.d(TAG, "onStop() for Speech => " + _speech);
    // NO MEMORY LEAKS, YOU HEAR ME?!?!?
    if (_speechTimer.isRunning()) _speechTimer.stop();
    if (_crossexTimer.isRunning()) _crossexTimer.stop();
    if (getAffirmativePrepTimer().isRunning()) getAffirmativePrepTimer().stop();
    if (getNegativePrepTimer().isRunning()) getNegativePrepTimer().stop();
  }

  @Override
  public void onPause() {
    super.onPause();
    Log.d(TAG, "onPause() for Speech => " + _speech);
    _savedInstanceState = new Bundle();
    // This is gross. But unfortunately, onSaveInstanceState() is
    /// not called for Fragments like it is for Activities
    onSaveInstanceState(_savedInstanceState);
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate() for Speech => " + _speech + ", savedInstanceState => " + savedInstanceState);
    super.onCreate(savedInstanceState);
    //setRetainInstance(true);// This Fragment is retained
    setHasOptionsMenu(true);
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    Log.d(TAG, "onCreateView() for Speech => " + _speech + ", savedInstanceState => " + savedInstanceState);
    _rootView = inflater.inflate(R.layout.fragment_policy_speech, container, false);
    //
    // Enable the app icon
    getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
    //
    // 
    Bundle fragmentArgs = getArguments();
    _speechIndex = fragmentArgs.getInt(ARG_INDEX);
    _speech = getPolicySpeechSingleton().getSpeechByIndex(_speechIndex);
    Log.d(TAG, "Speech => " + _speech.getName());
    TextView speechTitle = (TextView)getRootView().findViewById(R.id.speechTitle);
    speechTitle.setText("Speech: " + _speech.getName());
    //
    _speechTimerTextView = createSpeechTimerTextView(getRootView());
    //
    _crossexTimerTextView = createCrossExTimerTextView(getRootView());
    if (_speech.getCrossExDurationInMinutes() == 0) {
      _crossexTimerTextView.setVisibility(View.INVISIBLE);
      TextView label = (TextView)_rootView.findViewById(R.id.crossExTimerLabel);
      label.setVisibility(View.INVISIBLE);
    }
    //
    _affirmativePrepTimerTextView = createAffirmativePrepTimerTextView(getRootView());
    getAffirmativePrepTimer().setMillisUntilFinished(getPolicySpeechSingleton().getAffirmativePrepRemainingTime());
    //
    _negativePrepTimerTextView = createNegativePrepTimerTextView(getRootView());
    getNegativePrepTimer().setMillisUntilFinished(getPolicySpeechSingleton().getNegativePrepRemainingTime());
    // Optimization
    getSystemOptions().showTenthsOfSeconds();// init
    //
    //**********
    // Now that everything has been set up, let's check and see if
    /// we are actually recreating an instance of this Fragment, and can
    /// use savedInstanceState to restore its state
    // 
    // This is the initial state of a newly created Activity
    if (_savedInstanceState != null) {
      restoreInstanceState(_savedInstanceState);
    }
    // Manage State
    manageState();
    //
    return getRootView();
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case android.R.id.home:
      //
      if (NavUtils.getParentActivityName(getActivity()) != null) {
        NavUtils.navigateUpFromSameTask(getActivity());
      }
      return true;
    default:
      return super.onOptionsItemSelected(item);
    }
  }
  
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    // 
    if (resultCode == Activity.RESULT_OK) {
      long millisUntilFinished = data.getLongExtra(TimePickerFragment.RESULT_MILLIS, 0);
      Log.d(TAG, "Returned from TimePickerDialog... millis => " + millisUntilFinished);
      if (requestCode == RQID_SPEECHTIMER) {
        Log.d(TAG, "Speech Timer");
        _speechTimer.setMillisUntilFinished(millisUntilFinished);;
      } else if (requestCode == RQID_CROSSEXTIMER) {
        Log.d(TAG, "CrossEx Timer");
        _crossexTimer.setMillisUntilFinished(millisUntilFinished);
      } else if (requestCode == RQID_AFFPREPTIMER) {
        Log.d(TAG, "Aff Prep Timer");
        getAffirmativePrepTimer().setMillisUntilFinished(millisUntilFinished);
        getPolicySpeechSingleton().setAffirmativePrepRemainingTime(millisUntilFinished);
      } else if (requestCode == RQID_NEGPREPTIMER) {
        Log.d(TAG, "Neg Prep Timer");
        getNegativePrepTimer().setMillisUntilFinished(millisUntilFinished);
        getPolicySpeechSingleton().setNegativePrepRemainingTime(millisUntilFinished);
      }
      manageState();
    }
  }

  /**
   * New method to handle state management from outside. AFAIK Android
   * does not offer a method that is invoked when a Fragment inside a
   * ViewPager becomes the visible page. Not that we would know inside
   * this class how it is being used, of course. :)
   */
  public void manageState() {
    Log.d(TAG, "manageState(): HELLO from speech => " + _speech);
    List<STATE> currentState = computeCurrentState();
    for (STATE state : currentState) {
      adjustAppBasedOnSpecifiedState(state);
    }
    updateTimerTextView(_affirmativePrepTimerTextView, getPolicySpeechSingleton().getAffirmativePrepRemainingTime());
    updateTimerTextView(_negativePrepTimerTextView, getPolicySpeechSingleton().getNegativePrepRemainingTime());
    updateTimerTextView(_speechTimerTextView, _speechTimer.getMillisUntilFinished());
    updateTimerTextView(_crossexTimerTextView, _crossexTimer.getMillisUntilFinished());
  }
  
  private void restoreInstanceState(Bundle savedInstanceState) {
    Log.d(TAG, "Restoring instance state for Speech => " + _speech);
    if (savedInstanceState != null) {
      _speechTimer = (SpeechCountDownTimerWithAlarm)savedInstanceState.getSerializable(SPEECH_TIMER);
      _crossexTimer = (SpeechCountDownTimerWithAlarm)savedInstanceState.getSerializable(CROSSEX_TIMER);
      _affirmativePrepTimer = (SpeechCountDownTimerWithAlarm)savedInstanceState.getSerializable(AFFPREP_TIMER);
      _affirmativePrepTimer.setMillisUntilFinished(getPolicySpeechSingleton().getAffirmativePrepRemainingTime());
      _negativePrepTimer = (SpeechCountDownTimerWithAlarm)savedInstanceState.getSerializable(NEGPREP_TIMER);
      _negativePrepTimer.setMillisUntilFinished(getPolicySpeechSingleton().getNegativePrepRemainingTime());
      getSystemOptions();// re-initialize
      adjustAppBasedOnSpecifiedState(STATE.INSTANCE_STATE_RESTORED);
      manageState();
    } else {
      Log.i(TAG, "SAVED INSTANCE STATE IS NULL!!");
    }
  }
  
  private TextView createSpeechTimerTextView(View rootView) {
    TextView speechTimer = (TextView)rootView.findViewById(R.id.speechTimer);
    PolicySpeech speech = getPolicySpeechSingleton().getSpeechByIndex(_speechIndex);
    long durationInMillis = speech.getDurationInMinutes() * NUMBER_OF_MILLIS_IN_ONE_MINUTE;
    createSpeechCountDownTimer(durationInMillis);
    createSpeechTimerOnClickListener(speechTimer);
    createSpeechTimerOnLongClickListener(speechTimer);
    return speechTimer;
  }

  private TextView createCrossExTimerTextView(View rootView) {
    TextView speechTimer = (TextView)rootView.findViewById(R.id.crossExTimer);
    PolicySpeech speech = getPolicySpeechSingleton().getSpeechByIndex(_speechIndex);
    long durationInMillis = speech.getCrossExDurationInMinutes() * NUMBER_OF_MILLIS_IN_ONE_MINUTE;
    createCrossExCountDownTimer(durationInMillis);
    createCrossExTimerOnClickListener(speechTimer);
    createCrossExTimerOnLongClickListener(speechTimer);
    return speechTimer;
  }

  private TextView createAffirmativePrepTimerTextView(View rootView) {
    TextView affirmativePrepTimer = (TextView)rootView.findViewById(R.id.affirmativePrepTimer);
    createAffPrepTimerOnClickListener(affirmativePrepTimer);
    createAffPrepTimerOnLongClickListener(affirmativePrepTimer);
    return affirmativePrepTimer;
  }

  private TextView createNegativePrepTimerTextView(View rootView) {
    TextView negativePrepTimer = (TextView)rootView.findViewById(R.id.negativePrepTimer);
    createNegPrepTimerOnClickListener(negativePrepTimer);
    createNegPrepTimerOnLongClickListener(negativePrepTimer);
    return negativePrepTimer;
  }

  //**************************************
  //* Create Timers
  //**************************************
  private void createSpeechCountDownTimer(long durationInMillis) {
    _speechTimer = new SpeechCountDownTimerWithAlarm(
        durationInMillis,
        getCountdownInterval(),
        new SpeechCountDownTimerCallback() {
          //private static final long serialVersionUID = 1L;
          @Override
          public void update(long millisUntilFinished) {
            updateTimerTextView(_speechTimerTextView, millisUntilFinished);
            if (getSystemOptions().animateWhenTimerBelowThreshold()) {
              if (millisUntilFinished < NUMBER_OF_MILLIS_IN_ONE_MINUTE) {
                if (((millisUntilFinished / 1000) % 60 % 2) == 0)
                  _speechTimerTextView.setTextColor(Color.RED);
                else
                  _speechTimerTextView.setTextColor(Color.BLACK);
              }
            }
          }
          @Override
          public void done() {
            Log.i(TAG, "Speech Timer is done!");
            // Beep or flash or something??
            _speechTimer.exhaust();
            _speechTimerTextView.setTextColor(Color.RED);
            if (getSystemOptions().notifyAtTimerExpiration()) {
              _speechTimer.playNotification(getActivity());
            }
            manageState();
          }
        }
        );
  }
  
  private void createCrossExCountDownTimer(long durationInMillis) {
    _crossexTimer = new SpeechCountDownTimerWithAlarm(
        durationInMillis,
        getCountdownInterval(),
        new SpeechCountDownTimerCallback() {
          //private static final long serialVersionUID = 1L;
          @Override
          public void update(long millisUntilFinished) {
            updateTimerTextView(_crossexTimerTextView, millisUntilFinished);
            // Animate the timer when < 1 minute to go
            if (getSystemOptions().animateWhenTimerBelowThreshold()) {
              if (millisUntilFinished < NUMBER_OF_MILLIS_IN_ONE_MINUTE) {
                if (((millisUntilFinished / 1000) % 60 % 2) == 0)
                  _crossexTimerTextView.setTextColor(Color.RED);
                else
                  _crossexTimerTextView.setTextColor(Color.BLACK);
              }
            }
          }
          @Override
          public void done() {
            Log.i(TAG, "Speech Timer is done!");
            // Beep or flash or something??
            _crossexTimer.exhaust();
            if (getSystemOptions().notifyAtTimerExpiration()) {
              _crossexTimer.playNotification(getActivity());
            }
            _crossexTimerTextView.setTextColor(Color.RED);
            manageState();
          }
        }
        );
  }
  
  private SpeechCountDownTimerWithAlarm createAffPrepCountDownTimer(final long durationInMillis) {
    Log.i(TAG, "Creating Affirmative Prep Timer...");
    SpeechCountDownTimerWithAlarm ret = new SpeechCountDownTimerWithAlarm(
        durationInMillis,
        getCountdownInterval(),
        new SpeechCountDownTimerCallback() {
          //private static final long serialVersionUID = 1L;
          @Override
          public void update(long millisUntilFinished) {
            updateTimerTextView(_affirmativePrepTimerTextView, millisUntilFinished);
            getPolicySpeechSingleton().setAffirmativePrepRemainingTime(millisUntilFinished);
            //Log.i(TAG, "Aff Prep Timer, millis to go => " + millisUntilFinished);
            // Animate the timer when < 1 minute to go
            if (getSystemOptions().animateWhenTimerBelowThreshold()) {
              if (millisUntilFinished < NUMBER_OF_MILLIS_IN_ONE_MINUTE) {
                if (((millisUntilFinished / 1000) % 60 % 2) == 0)
                  _affirmativePrepTimerTextView.setTextColor(Color.RED);
                else
                  _affirmativePrepTimerTextView.setTextColor(Color.BLACK);
              }
            }
          }
          @Override
          public void done() {
            Log.i(TAG, "Affirmative Prep Timer is done!");
            getPolicySpeechSingleton().setAffirmativePrepRemainingTime(0);
            getAffirmativePrepTimer().exhaust();
            if (getSystemOptions().notifyAtTimerExpiration()) {
              getAffirmativePrepTimer().playNotification(getActivity());
            }
            _affirmativePrepTimerTextView.setTextColor(Color.RED);
            manageState();
          }
        }
        );
    return ret;
  }

  private SpeechCountDownTimerWithAlarm createNegPrepCountDownTimer(final long durationInMillis) {
    Log.i(TAG, "Creating Negative Prep Timer...");
    SpeechCountDownTimerWithAlarm ret = new SpeechCountDownTimerWithAlarm(
        durationInMillis,
        getCountdownInterval(),
        new SpeechCountDownTimerCallback() {
          //private static final long serialVersionUID = 1L;
          @Override
          public void update(long millisUntilFinished) {
            updateTimerTextView(_negativePrepTimerTextView, millisUntilFinished);
            getPolicySpeechSingleton().setNegativePrepRemainingTime(millisUntilFinished);
            // Animate the timer when < 1 minute to go
            if (getSystemOptions().animateWhenTimerBelowThreshold()) {
              if (millisUntilFinished < NUMBER_OF_MILLIS_IN_ONE_MINUTE) {
                if (((millisUntilFinished / 1000) % 60 % 2) == 0)
                  _negativePrepTimerTextView.setTextColor(Color.RED);
                else
                  _negativePrepTimerTextView.setTextColor(Color.BLACK);
              }
            }
          }
          @Override
          public void done() {
            Log.i(TAG, "Negative Prep Timer is done!");
            getPolicySpeechSingleton().setNegativePrepRemainingTime(0);
            getNegativePrepTimer().exhaust();
            if (getSystemOptions().notifyAtTimerExpiration()) {
              getNegativePrepTimer().playNotification(getActivity());
            }
            _negativePrepTimerTextView.setTextColor(Color.RED);
            manageState();
          }
        }
        );
    return ret;
  }

  //**************************************
  //* Create onClickListeners
  //**************************************
  private void createSpeechTimerOnClickListener(TextView speechTimer) {
    speechTimer.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        // Toggle Timer state
        Log.i(TAG, "Speech Timer: Click, speech => " + _speech);
        if (!_speechTimer.isExhausted()) {
          if (_speechTimer.isRunning()) {
            _speechTimer.stop();
            manageState();
          } else {
            _speechTimer.go();
            manageState();
          }
        } else {
          if (_speechTimer.isPlayingNotification()) {
            _speechTimer.stopNotification(getActivity());
          }
        }
      }
    });
  }

  private void createCrossExTimerOnClickListener(TextView speechTimer) {
    speechTimer.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        // Toggle Timer state
        Log.i(TAG, "Crossex Timer: Click, speech => " + _speech);
        if (!_crossexTimer.isExhausted()) {
          if (_crossexTimer.isRunning()) {
            _crossexTimer.stop();
            manageState();
          } else {
            _crossexTimer.go();
            manageState();
          }
        }
      }
    });
  }

  private void createAffPrepTimerOnClickListener(TextView affirmativePrepTimer) {
    affirmativePrepTimer.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        // Toggle Timer state
        Log.i(TAG, "Affirmative Prep Timer: Click, speech => " + _speech);
        if (!getAffirmativePrepTimer().isExhausted()) {
          if (getAffirmativePrepTimer().isRunning()) {
            getAffirmativePrepTimer().stop();
            manageState();
          } else {
            getAffirmativePrepTimer().setMillisUntilFinished(getPolicySpeechSingleton().getAffirmativePrepRemainingTime());
            getAffirmativePrepTimer().go();
            manageState();
          }
        }
      }
    });
  }
  
  private void createNegPrepTimerOnClickListener(TextView negativePrepTimer) {
    negativePrepTimer.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        // Toggle Timer state
        Log.i(TAG, "Negative Prep Timer: Click from speech " + _speech);
        if (!getNegativePrepTimer().isExhausted()) {
          if (getNegativePrepTimer().isRunning()) {
            getNegativePrepTimer().stop();
            manageState();
          } else {
            getNegativePrepTimer().setMillisUntilFinished(getPolicySpeechSingleton().getNegativePrepRemainingTime());
            getNegativePrepTimer().go();
            manageState();
          }
        }
      }
    });
  }

  //**************************************
  //* Create onLongClickListeners
  //**************************************
  private void createSpeechTimerOnLongClickListener(TextView speechTimer) {
    speechTimer.setOnLongClickListener(new OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        getActivity().startActionMode(new ActionMode.Callback() {
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
              return false;
            }
            @Override
            public void onDestroyActionMode(ActionMode mode) {
              // Nothing to do
            }
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
              // Inflate the menu
              MenuInflater inflater = mode.getMenuInflater();
              inflater.inflate(R.menu.speech_timer_context_menu, menu);
              return true;
            }
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
              boolean ret = false;
              switch (item.getItemId()) {
              case R.id.speech_context_menu_item_reset:
                Log.i(TAG, "CONTEXT MENU: Speech Timer **RESET**");
                long originalDurationInMillis = _speechTimer.reset();
                updateTimerTextView(_speechTimerTextView, originalDurationInMillis);
                _speechTimerTextView.setTextColor(Color.BLACK);
                ret = true;
                break;
              case R.id.speech_context_menu_item_set:
                Log.i(TAG, "CONTEXT MENU: Speech Timer **SET**");
                // Display dialog to set Timer
                FragmentManager fragMan = getActivity().getFragmentManager();
                TimePickerFragment dialog = 
                    TimePickerFragment.newInstance(
                        _speechTimer.getMillisUntilFinished(), 
                        _speech.getDurationInMinutes(),
                        RQID_SPEECHTIMER);
                dialog.setTargetFragment(PolicySpeechFragment.this, 0);
                dialog.show(fragMan, MINUTES_SECONDS_PICKER_TITLE);
                _speechTimerTextView.setTextColor(Color.BLACK);
                ret = true;
                break;
              }
              return ret;
            }
          });
        return true;
      }
    });
  }

  private void createCrossExTimerOnLongClickListener(TextView speechTimer) {
    speechTimer.setOnLongClickListener(new OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        getActivity().startActionMode(new ActionMode.Callback() {
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
              return false;
            }
            @Override
            public void onDestroyActionMode(ActionMode mode) {
              // Nothing to do
            }
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
              // Inflate the menu
              MenuInflater inflater = mode.getMenuInflater();
              inflater.inflate(R.menu.crossex_timer_context_menu, menu);
              return true;
            }
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
              boolean ret = false;
              switch (item.getItemId()) {
              case R.id.crossex_context_menu_reset:
                Log.i(TAG, "CONTEXT MENU: CrossEx Timer **RESET**");
                long originalDurationInMillis = _crossexTimer.reset();
                updateTimerTextView(_crossexTimerTextView, originalDurationInMillis);
                _crossexTimerTextView.setTextColor(Color.BLACK);
                mode.finish();
                ret = true;
                break;
              case R.id.crossex_context_menu_set:
                Log.i(TAG, "CONTEXT MENU: CrossEx Timer **SET**");
                // Display dialog to set Timer
                FragmentManager fragMan = getActivity().getFragmentManager();
                TimePickerFragment dialog = 
                    TimePickerFragment.newInstance(
                        _crossexTimer.getMillisUntilFinished(), 
                        _speech.getCrossExDurationInMinutes(),
                        RQID_CROSSEXTIMER);
                dialog.setTargetFragment(PolicySpeechFragment.this, 0);
                dialog.show(fragMan, MINUTES_SECONDS_PICKER_TITLE);
                _crossexTimerTextView.setTextColor(Color.BLACK);
                mode.finish();
                ret = true;
                break;
              }
              return ret;
            }
          });
        return true;
      }
    });
  }
  
  private void createAffPrepTimerOnLongClickListener(TextView speechTimer) {
    speechTimer.setOnLongClickListener(new OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        getActivity().startActionMode(new ActionMode.Callback() {
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
              return false;
            }
            @Override
            public void onDestroyActionMode(ActionMode mode) {
              // Nothing to do
            }
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
              // Inflate the menu
              MenuInflater inflater = mode.getMenuInflater();
              inflater.inflate(R.menu.affprep_timer_context_menu, menu);
              return true;
            }
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
              boolean ret = false;
              switch (item.getItemId()) {
              case R.id.affprep_context_menu_reset:
                Log.i(TAG, "CONTEXT MENU: Aff Prep Timer **RESET**");
                long originalDurationInMillis = getAffirmativePrepTimer().reset();
                updateTimerTextView(_affirmativePrepTimerTextView, originalDurationInMillis);
                getPolicySpeechSingleton().setAffirmativePrepRemainingTime(originalDurationInMillis);
                _affirmativePrepTimerTextView.setTextColor(Color.BLACK);
                ret = true;
                break;
              case R.id.affprep_context_menu_set:
                Log.i(TAG, "CONTEXT MENU: Aff Prep Timer **SET**");
                // Display dialog to set Timer
                FragmentManager fragMan = getActivity().getFragmentManager();
                TimePickerFragment dialog = 
                    TimePickerFragment.newInstance(
                        getAffirmativePrepTimer().getMillisUntilFinished(), 
                        PolicySpeechFactory.PREP_TIME_IN_MINUTES,
                        RQID_AFFPREPTIMER);
                dialog.setTargetFragment(PolicySpeechFragment.this, 0);
                dialog.show(fragMan, MINUTES_SECONDS_PICKER_TITLE);
                _affirmativePrepTimerTextView.setTextColor(Color.BLACK);
                ret = true;
                break;
              }
              return ret;
            }
          });
        return true;
      }
    });
  }

  private void createNegPrepTimerOnLongClickListener(TextView speechTimer) {
    speechTimer.setOnLongClickListener(new OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        getActivity().startActionMode(new ActionMode.Callback() {
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
              return false;
            }
            @Override
            public void onDestroyActionMode(ActionMode mode) {
              // Nothing to do
            }
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
              // Inflate the menu
              MenuInflater inflater = mode.getMenuInflater();
              inflater.inflate(R.menu.negprep_timer_context_menu, menu);
              return true;
            }
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
              boolean ret = false;
              switch (item.getItemId()) {
              case R.id.negprep_context_menu_reset:
                Log.i(TAG, "CONTEXT MENU: Neg Prep Timer **RESET**");
                long originalDurationInMillis = getNegativePrepTimer().reset();
                updateTimerTextView(_negativePrepTimerTextView, originalDurationInMillis);
                getPolicySpeechSingleton().setNegativePrepRemainingTime(originalDurationInMillis);
                _negativePrepTimerTextView.setTextColor(Color.BLACK);
                ret = true;
                break;
              case R.id.negprep_context_menu_set:
                Log.i(TAG, "CONTEXT MENU: Neg Prep Timer **SET**");
                // Display dialog to set Timer
                FragmentManager fragMan = getActivity().getFragmentManager();
                TimePickerFragment dialog = 
                    TimePickerFragment.newInstance(
                        getNegativePrepTimer().getMillisUntilFinished(), 
                        PolicySpeechFactory.PREP_TIME_IN_MINUTES,
                        RQID_NEGPREPTIMER);
                dialog.setTargetFragment(PolicySpeechFragment.this, 0);
                dialog.show(fragMan, MINUTES_SECONDS_PICKER_TITLE);
                _negativePrepTimerTextView.setTextColor(Color.BLACK);
                ret = true;
                break;
              }
              return ret;
            }
          });
        return true;
      }
    });
  }

  private void updateTimerTextView(TextView textView, long durationInMillis) {
    long seconds = (durationInMillis / 1000) % 60;
    long minutes = (durationInMillis / NUMBER_OF_MILLIS_IN_ONE_MINUTE) % 60;
    String value;
    if (getSystemOptions().showTenthsOfSeconds()) {
      long tenths = (durationInMillis / 100) % 10;
      value = String.format(Locale.getDefault(), "%d:%02d.%d", minutes, seconds, tenths);
    } else {
      value = String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
    }
    textView.setText(value);
  }
  
  private List<STATE> computeCurrentState() {
    List<STATE> states = new ArrayList<>();
    // TESTING
//    if (_testing) states.add(STATE.TESTING);// TODO: REMOVE ME
    computeBasicStates(states);
    computeHigherStates(states);
    return states;
  }

  /**
   * Compute the most basic states based on direct attribute values.
   * 
   * @param states
   */
  private void computeBasicStates(List<STATE> states) {
    // Aff Prep Timer states
    if (getAffirmativePrepTimer().isRunning()) 
      states.add(STATE.AFFPREP_TIMER_RUNNING);
    if (getAffirmativePrepTimer().hasEverBeenStarted() && !getAffirmativePrepTimer().isRunning())
      states.add(STATE.AFFPREP_TIMER_STOPPED);
    if (getAffirmativePrepTimer().isExhausted()) 
      states.add(STATE.AFFPREP_TIMER_EXHAUSTED);
    // Neg Prep Timer states
    if (getNegativePrepTimer().isRunning()) 
      states.add(STATE.NEGPREP_TIMER_RUNNING);
    if (getNegativePrepTimer().hasEverBeenStarted() && !getNegativePrepTimer().isRunning())
      states.add(STATE.NEGPREP_TIMER_STOPPED);
    if (getNegativePrepTimer().isExhausted())
      states.add(STATE.NEGPREP_TIMER_EXHAUSTED);
    // Speech Timer states
    if (_speechTimer.isRunning()) 
      states.add(STATE.SPEECH_TIMER_RUNNING);
    if (_speechTimer.hasEverBeenStarted() && !_speechTimer.isRunning())
      states.add(STATE.SPEECH_TIMER_STOPPED);
    if (_speechTimer.isExhausted())
      states.add(STATE.SPEECH_TIMER_EXHAUSTED);
    // Cross Ex Timer states
    if (_crossexTimer.isRunning()) 
      states.add(STATE.CROSSEX_TIMER_RUNNING);
    if (_crossexTimer.hasEverBeenStarted() && !_crossexTimer.isRunning())
      states.add(STATE.CROSSEX_TIMER_STOPPED);
    if (_crossexTimer.isExhausted())
      states.add(STATE.CROSSEX_TIMER_EXHAUSTED);
  }
  
  /**
   * Compute higher states based on basic states already in the List
   * 
   * @param states
   */
  private void computeHigherStates(List<STATE> states) {
    if (_speechIndex == getPolicySpeechSingleton().getActiveSpeechIndex()) {
      states.add(STATE.THIS_IS_THE_ACTIVE_SPEECH);
    } 
    if (states.contains(STATE.THIS_IS_THE_ACTIVE_SPEECH) && !anyTimerActive()) {
      // This is the active speech and no timers are running. There is no
      /// active speech in that case. It's wide open.
      states.add(STATE.NO_ACTIVE_SPEECH);
    }
  }
  
  private boolean anyTimerActive() {
    return _speechTimer.isRunning() ||
        _crossexTimer.isRunning() ||
        getAffirmativePrepTimer().isRunning() ||
        getNegativePrepTimer().isRunning()
        ;
  }
  
  private void adjustAppBasedOnSpecifiedState(STATE state) {
    enableTextViewsBasedOnState(state);
    setLabelTextBasedOnState(state);
  }
  
  private void enableTextViewsBasedOnState(STATE state) {
    // WIDE OPEN
    _speechTimerTextView.setEnabled(true);
    _crossexTimerTextView.setEnabled(true);
    _affirmativePrepTimerTextView.setEnabled(true);
    _negativePrepTimerTextView.setEnabled(true);
  }
  
  private void setLabelTextBasedOnState(STATE state) {
    if (state == STATE.SPEECH_TIMER_RUNNING) {
      TextView label = (TextView)getRootView().findViewById(R.id.speechTimerLabel);
      label.setText(getActivity().getResources().getString(R.string.label_speech_timer) + " (Touch to stop)");
    } else if (state == STATE.SPEECH_TIMER_STOPPED) {
      TextView label = (TextView)getRootView().findViewById(R.id.speechTimerLabel);
      label.setText(getActivity().getResources().getString(R.string.label_speech_timer) + " (Touch to start)");
    } else if (state == STATE.SPEECH_TIMER_EXHAUSTED) {
      TextView label = (TextView)getRootView().findViewById(R.id.speechTimerLabel);
      label.setText(getActivity().getResources().getString(R.string.label_speech_timer));
    } else if (state == STATE.CROSSEX_TIMER_RUNNING) {
      TextView label = (TextView)getRootView().findViewById(R.id.crossExTimerLabel);
      label.setText(getActivity().getResources().getString(R.string.label_crossex_timer) + " (Touch to stop)");
    } else if (state == STATE.CROSSEX_TIMER_STOPPED) {
      TextView label = (TextView)getRootView().findViewById(R.id.crossExTimerLabel);
      label.setText(getActivity().getResources().getString(R.string.label_crossex_timer) + " (Touch to start)");
    } else if (state == STATE.CROSSEX_TIMER_EXHAUSTED){
      TextView label = (TextView)getRootView().findViewById(R.id.crossExTimerLabel);
      label.setText(getActivity().getResources().getString(R.string.label_crossex_timer));
    } else if (state == STATE.AFFPREP_TIMER_RUNNING) {
      TextView label = (TextView)getRootView().findViewById(R.id.affPrepTimerLabel);
      label.setText(getActivity().getResources().getString(R.string.label_affprep_timer) + " (Touch to stop)");
    } else if (state == STATE.AFFPREP_TIMER_STOPPED) {
      TextView label = (TextView)getRootView().findViewById(R.id.affPrepTimerLabel);
      label.setText(getActivity().getResources().getString(R.string.label_affprep_timer) + " (Touch to start)");
    } else if (state == STATE.AFFPREP_TIMER_EXHAUSTED) {
      TextView label = (TextView)getRootView().findViewById(R.id.affPrepTimerLabel);
      label.setText(getActivity().getResources().getString(R.string.label_affprep_timer));
    } else if (state == STATE.NEGPREP_TIMER_RUNNING) {
      TextView label = (TextView)getRootView().findViewById(R.id.negPrepTimerLabel);
      label.setText(getActivity().getResources().getString(R.string.label_negprep_timer) + " (Touch to stop)");
    } else if (state == STATE.NEGPREP_TIMER_STOPPED) {
      TextView label = (TextView)getRootView().findViewById(R.id.negPrepTimerLabel);
      label.setText(getActivity().getResources().getString(R.string.label_negprep_timer) + " (Touch to start)");
    } else if (state == STATE.NEGPREP_TIMER_EXHAUSTED) {
      TextView label = (TextView)getRootView().findViewById(R.id.negPrepTimerLabel);
      label.setText(getActivity().getResources().getString(R.string.label_negprep_timer));
    }
  }
  
  @Override
  public void onAttach(Activity activity) {
    // TODO Auto-generated method stub
    super.onAttach(activity);
    Log.d(TAG, "onAttach() for Speech => " + _speech);
  }
  @Override
  public void onDestroyView() {
    // TODO Auto-generated method stub
    super.onDestroyView();
    Log.d(TAG, "onDestroyView() for Speech => " + _speech);
  }
  @Override
  public void onDetach() {
    // TODO Auto-generated method stub
    super.onDetach();
    Log.d(TAG, "onDetach() for Speech => " + _speech);
  }
  @Override
  public void onResume() {
    // TODO Auto-generated method stub
    super.onResume();
    Log.d(TAG, "onResume() for Speech => " + _speech);
  }
  
  @Override
  public void onStart() {
    // TODO Auto-generated method stub
    super.onStart();
    Log.d(TAG, "onStart() for Speech => " + _speech);
  }
  //****************************************
  //*           A   T   T   I   C
  //****************************************

}
