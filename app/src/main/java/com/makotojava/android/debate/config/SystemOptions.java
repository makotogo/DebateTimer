package com.makotojava.android.debate.config;

import android.content.Context;
import android.media.Ringtone;

public interface SystemOptions {
  
  Context getContext();
  
  int getPrepTimeInMinutes();
  
  int getCrossExTimeInMinutes();

  int getConstructiveTimeInMinutes();
  
  int getRebuttalTimeInMinutes();
  
  boolean showTenthsOfSeconds();
  
  boolean animateWhenTimerBelowThreshold();
  
  boolean notifyAtTimerExpiration();
  
  Ringtone getTimerExpirationSound();
  
}
