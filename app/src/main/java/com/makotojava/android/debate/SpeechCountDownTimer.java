package com.makotojava.android.debate;

import java.io.Serializable;

import android.os.CountDownTimer;

/**
 * A Simple Countdown Timer
 * 
 * You tell it how far in the future (in milliseconds) the countdown
 * will expire, how often to keep track of how many milliseconds remain
 * in the countdown, and provide a callback that gets called when
 * the timer expires.
 * 
 * This timer is pausable (by calling stop()). When go() is called,
 * a new CountDownTimer is simply created using the remaining time as
 * its countdown.
 * 
 * This implementation is naive and doesn't really worry about picking
 * up extra milliseconds between the last time onTick() was called and
 * the when the stop() is called. It just won't be noticeable in human
 * sensible time.
 * 
 * @author steve
 *
 */
public class SpeechCountDownTimer implements Serializable {
  
  /**
   * We want to save the state of a timer when its owning Fragment
   * gets paused. 
   */
  private static final long serialVersionUID = 1L;

  interface SpeechCountDownTimerCallback {
    void update(long millisUntilFinished);
    void done();
  }
  
  public static final long DEFAULT_COUNTDOWN_INTERVAL = 1000;
  
  private long _millisInFuture;
  
  private long _countDownInterval;
  private long _millisUntilFinished;
  public long getMillisUntilFinished() {
    return _millisUntilFinished;
  }
  private transient SpeechCountDownTimerCallback _callback;
  
  private transient CountDownTimer _timer;
  
  private boolean _running;
  public boolean isRunning() { return _running; }
  
  public SpeechCountDownTimer(long millisInFuture, SpeechCountDownTimerCallback callback) {
    this(millisInFuture, DEFAULT_COUNTDOWN_INTERVAL, callback);
  }

  public SpeechCountDownTimer(long millisInFuture, long countDownInterval, SpeechCountDownTimerCallback callback) {
    _millisInFuture = millisInFuture;
    _countDownInterval = countDownInterval;
    _millisUntilFinished = millisInFuture;
    _callback = callback;
  }
  
  public void go() {
    _timer = new CountDownTimer(_millisUntilFinished, _countDownInterval) {
      @Override
      public void onTick(long millisUntilFinished) {
        _millisUntilFinished = millisUntilFinished;
        _callback.update(millisUntilFinished);
      }
      @Override
      public void onFinish() {
        _callback.done();
      }
    };
    _timer.start();
    _running = true;
  }
  
  public void stop() {
    if (_timer != null) {
      _timer.cancel();
    }
    _running = false;
  }
  
  public void exhaust() {
    stop();
    _millisUntilFinished = 0;
  }
  
  public boolean isExhausted() {
    return _millisUntilFinished == 0;
  }
  
  public boolean hasEverBeenStarted() {
    return _millisInFuture > _millisUntilFinished;
  }
  
  public long reset() {
    stop();
    _millisUntilFinished = _millisInFuture;
    return _millisUntilFinished;
  }
  
  public void setCountdownInterval(long countDownInterval) {
    if (countDownInterval > 0) {
      if (isRunning()) {
        stop();
      }
      _countDownInterval = countDownInterval;
      go();
    }
  }
  
  public void setMillisUntilFinished(long millisUntilFinished) {
    if (millisUntilFinished > 0) {
      if (isRunning()) {
        stop();
      }
      _millisUntilFinished = millisUntilFinished;
    }
  }

}
