package com.makotojava.android.debate.model;

import java.util.List;

import android.content.Context;

public class PolicySpeechSingleton {
  
  /**
   * THE one and only instance of this class.
   */
  private static PolicySpeechSingleton policySpeechSingleton;
  
  public static final int NO_ACTIVE_SPEECH = -1;
  
  private int _activeSpeechIndex = NO_ACTIVE_SPEECH;
  public int getActiveSpeechIndex() {
    return _activeSpeechIndex;
  }
  public void setActiveSpeechIndex(int value) {
    _activeSpeechIndex = value;
  }
  
  private int numberOfSpeeches;
  public int getNumberOfSpeeches() {
    return numberOfSpeeches;
  }
  
  private List<PolicySpeech> speeches;
  public PolicySpeech getSpeechByIndex(int index) {
    if (index > (getNumberOfSpeeches() - 1)) {
      throw new IllegalArgumentException("The specified index: " + index + 
          " exceeds the number of speeches!");
    }
    if (index < 0) {
      throw new IllegalArgumentException("The specified index: " + index + 
          " must be greater than zero and less than " + (getNumberOfSpeeches()-1) + "!");
    }
    // I think we're cool. Return the index.
    return speeches.get(index);
  }
  
  private long _affirmativePrepRemainingTime;
  public long getAffirmativePrepRemainingTime() {
    return _affirmativePrepRemainingTime; 
  }
  public void setAffirmativePrepRemainingTime(long value) { _affirmativePrepRemainingTime = value; }
  
  private long _negativePrepRemainingTime;
  public long getNegativePrepRemainingTime() { 
    return _negativePrepRemainingTime;
  }
  public void setNegativePrepRemainingTime(long value) { _negativePrepRemainingTime = value; }

  
  private PolicySpeechSingleton(Context context) {
    speeches = PolicySpeechFactory.createAll(context);
    numberOfSpeeches = speeches.size();
  }
  
  public static PolicySpeechSingleton instance(Context context) {
    if (policySpeechSingleton == null) {
      policySpeechSingleton = new PolicySpeechSingleton(context);
    }
    return policySpeechSingleton;
  }
  
}
