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

package com.makotojava.android.debate.model;

import java.util.ArrayList;
import java.util.List;

import com.makotojava.android.debate.SettingsActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class PolicySpeechFactory {
  
  private static final String TAG = PolicySpeechFactory.class.getName();
  
  // Default Values
  public static final int CROSSEX_TIME_IN_MINUTES = 3;
  public static final int PREP_TIME_IN_MINUTES = 8;
  public static final int CONSTRUCTIVE_SPEECH_DURATION_IN_MINUTES = 8;
  public static final int REBUTTAL_DURATION_IN_MINUTES = 5;
  
  public static final long NUMBER_OF_MILLIS_IN_ONE_MINUTE = 60000L;
  
  private PolicySpeechFactory() {
    // Hands off, Jack
  }
  
  public static List<PolicySpeech> createAll(Context context) {
    List<PolicySpeech> ret = new ArrayList<>();
    // 
    ret.add(createFirstAffirmativeConstructive(context));
    ret.add(createFirstNegativeConstructive(context));
    ret.add(createSecondAffirmativeConstructive(context));
    ret.add(createSecondNegativeConstructive(context));
    //
    ret.add(createFirstNegativeRebuttal(context));
    ret.add(createFirstAffirmativeRebuttal(context));
    ret.add(createSecondNegativeRebuttal(context));
    ret.add(createSecondAffirmativeRebuttal(context));
    //
    return ret;
  }
  
  private static int getSharedProperty(Context context, String key, int defaultValue) {
    int ret;// Return value
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    String defaultValueAsString = Integer.toString(defaultValue);
    String value = sharedPreferences.getString(key, defaultValueAsString);
    Log.d(TAG, "Retrieving int property => " + key + "=" + value);
    ret = Integer.parseInt(value);
    return ret;
  }
  
  private static PolicySpeech createFirstAffirmativeConstructive(final Context context) {
    return new PolicySpeech() {
      private static final long serialVersionUID = 9138516054966245216L;
      @Override public String getName() { return "1AC"; }
      @Override public int getDurationInMinutes() { 
        return getSharedProperty(context, 
            SettingsActivity.PREF_KEY_CONSTRUCTIVE_SPEECH_TIME, 
            CONSTRUCTIVE_SPEECH_DURATION_IN_MINUTES); 
      }
      @Override public int getCrossExDurationInMinutes() { 
        return getSharedProperty(context, 
            SettingsActivity.PREF_KEY_CROSSEX_TIME, 
            CROSSEX_TIME_IN_MINUTES); 
      }
      @Override public boolean isFirst() { return true; }
      @Override public boolean isAffirmative() { return true; }
      @Override public boolean isConstructive() { return true; }
      @Override public String toString() { return getName(); }
      //@Override public int getPrepTimeInMinutes() { get }
    };
  }

  private static PolicySpeech createFirstNegativeConstructive(final Context context) {
    return new PolicySpeech() {
      private static final long serialVersionUID = 4346225386915597461L;
      @Override public String getName() { return "1NC"; }
      @Override public int getDurationInMinutes() { 
        return getSharedProperty(context, 
            SettingsActivity.PREF_KEY_CONSTRUCTIVE_SPEECH_TIME, 
            CONSTRUCTIVE_SPEECH_DURATION_IN_MINUTES); 
      }
      @Override public int getCrossExDurationInMinutes() { 
        return getSharedProperty(context, 
            SettingsActivity.PREF_KEY_CROSSEX_TIME, 
            CROSSEX_TIME_IN_MINUTES); 
      }
      @Override public boolean isFirst() { return true; }
      @Override public boolean isAffirmative() { return false; }
      @Override public boolean isConstructive() { return true; }
      @Override public String toString() { return getName(); }
//      @Override public int getPrepTimeInMinutes() { 
//        return getSharedProperty(context, 
//            SettingsActivity.PREF_KEY_PREP_TIME, 
//            PREP_TIME_IN_MINUTES); 
//      }
    };
  }

  private static PolicySpeech createSecondAffirmativeConstructive(final Context context) {
    return new PolicySpeech() {
      private static final long serialVersionUID = 7884747107789927406L;
      @Override public String getName() { return "2AC"; }
      @Override public int getDurationInMinutes() { 
        return getSharedProperty(context, 
            SettingsActivity.PREF_KEY_CONSTRUCTIVE_SPEECH_TIME, 
            CONSTRUCTIVE_SPEECH_DURATION_IN_MINUTES); 
      }
      @Override public int getCrossExDurationInMinutes() { 
        return getSharedProperty(context, 
            SettingsActivity.PREF_KEY_CROSSEX_TIME, 
            CROSSEX_TIME_IN_MINUTES); 
      }
      @Override public boolean isFirst() { return false; }
      @Override public boolean isAffirmative() { return true; }
      @Override public boolean isConstructive() { return true; }
      @Override public String toString() { return getName(); }
//      @Override public int getPrepTimeInMinutes() { 
//        return getSharedProperty(context, 
//            SettingsActivity.PREF_KEY_PREP_TIME, 
//            PREP_TIME_IN_MINUTES); 
//      }
    };
  }

  private static PolicySpeech createSecondNegativeConstructive(final Context context) {
    return new PolicySpeech() {
      private static final long serialVersionUID = 1695064715243196014L;
      @Override public String getName() { return "2NC"; }
      @Override public int getDurationInMinutes() { 
        return getSharedProperty(context, 
            SettingsActivity.PREF_KEY_CONSTRUCTIVE_SPEECH_TIME, 
            CONSTRUCTIVE_SPEECH_DURATION_IN_MINUTES); 
      }
      @Override public int getCrossExDurationInMinutes() { 
        return getSharedProperty(context, 
            SettingsActivity.PREF_KEY_CROSSEX_TIME, 
            CROSSEX_TIME_IN_MINUTES); 
      }
      @Override public boolean isFirst() { return false; }
      @Override public boolean isAffirmative() { return false; }
      @Override public boolean isConstructive() { return true; }
      @Override public String toString() { return getName(); }
//      @Override public int getPrepTimeInMinutes() { 
//        return getSharedProperty(context, 
//            SettingsActivity.PREF_KEY_PREP_TIME, 
//            PREP_TIME_IN_MINUTES); 
//      }
    };
  }
  
  private static PolicySpeech createFirstNegativeRebuttal(final Context context) {
    return new PolicySpeech() {
      private static final long serialVersionUID = 9127480891246635051L;
      @Override public String getName() { return "1NR"; }
      @Override public int getDurationInMinutes() { 
        return getSharedProperty(context, 
            SettingsActivity.PREF_KEY_REBUTTAL_TIME, 
            REBUTTAL_DURATION_IN_MINUTES);
      }
      @Override public int getCrossExDurationInMinutes() { return 0; }
      @Override public boolean isFirst() { return true; }
      @Override public boolean isAffirmative() { return false; }
      @Override public boolean isConstructive() { return false; }
      @Override public String toString() { return getName(); }
//      @Override public int getPrepTimeInMinutes() { 
//        return getSharedProperty(context, 
//            SettingsActivity.PREF_KEY_PREP_TIME, 
//            PREP_TIME_IN_MINUTES); 
//      }
    };
  }

  private static PolicySpeech createFirstAffirmativeRebuttal(final Context context) {
    return new PolicySpeech() {
      private static final long serialVersionUID = 3108608108982870849L;
      @Override public String getName() { return "1AR"; }
      @Override public int getDurationInMinutes() { 
        return getSharedProperty(context, 
            SettingsActivity.PREF_KEY_REBUTTAL_TIME, 
            REBUTTAL_DURATION_IN_MINUTES); 
      }
      @Override public int getCrossExDurationInMinutes() { return 0; }
      @Override public boolean isFirst() { return true; }
      @Override public boolean isAffirmative() { return true; }
      @Override public boolean isConstructive() { return false; }
      @Override public String toString() { return getName(); }
//      @Override public int getPrepTimeInMinutes() { 
//        return getSharedProperty(context, 
//            SettingsActivity.PREF_KEY_PREP_TIME, 
//            PREP_TIME_IN_MINUTES); 
//      }
    };
  }

  private static PolicySpeech createSecondNegativeRebuttal(final Context context) {
    return new PolicySpeech() {
      private static final long serialVersionUID = -6697221244045534622L;
      @Override public String getName() { return "2NR"; }
      @Override public int getDurationInMinutes() { 
        return getSharedProperty(context, 
            SettingsActivity.PREF_KEY_REBUTTAL_TIME, 
            REBUTTAL_DURATION_IN_MINUTES); 
      }
      @Override public int getCrossExDurationInMinutes() { return 0; }
      @Override public boolean isFirst() { return false; }
      @Override public boolean isAffirmative() { return false; }
      @Override public boolean isConstructive() { return false; }
      @Override public String toString() { return getName(); }
//      @Override public int getPrepTimeInMinutes() { 
//        return getSharedProperty(context, 
//            SettingsActivity.PREF_KEY_PREP_TIME, 
//            PREP_TIME_IN_MINUTES); 
//      }
    };
  }

  private static PolicySpeech createSecondAffirmativeRebuttal(final Context context) {
    return new PolicySpeech() {
      private static final long serialVersionUID = -1841270525915267678L;
      @Override public String getName() { return "2AR"; }
      @Override public int getDurationInMinutes() { 
        return getSharedProperty(context, 
            SettingsActivity.PREF_KEY_REBUTTAL_TIME, 
            REBUTTAL_DURATION_IN_MINUTES); 
      }
      @Override public int getCrossExDurationInMinutes() { return 0; }
      @Override public boolean isFirst() { return false; }
      @Override public boolean isAffirmative() { return true; }
      @Override public boolean isConstructive() { return false; }
      @Override public String toString() { return getName(); }
//      @Override public int getPrepTimeInMinutes() { 
//        return getSharedProperty(context, 
//            SettingsActivity.PREF_KEY_PREP_TIME, 
//            PREP_TIME_IN_MINUTES); 
//      }
    };
  }

}
