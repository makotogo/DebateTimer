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

import java.io.Serializable;

public interface PolicySpeech extends Serializable {

  /**
   * Returns the name of this Policy Speech, such as
   * 1NR, 2AC, etc.
   */
  String getName();

  /**
   * Returns the duration of this speech in minutes.
   * So, it's not just a clever method name, huh? 
   */
  int getDurationInMinutes();
  
  /**
   * Returns the duration, in minutes, of any allowed 
   * Cross Examination following this speech. Zero if
   * CrossEx is not allowed for this speech.
   */
  int getCrossExDurationInMinutes();
  
  /**
   * Returns the prep time, in minutes, that remains
   */
  //int getPrepTimeInMinutes();
  
  /**
   * Returns true if this speech is the first, false if
   * it is the second
   */
  boolean isFirst();
  
  /**
   * Returns true if this speech is Affirmative, false
   * if it is Negative.
   */
  boolean isAffirmative();

  /**
   * Returns true if this speech is Constructive, false
   * if it is Rebuttal. 
   */
  boolean isConstructive();
  
  /**
   * Produce a string representation of this object
   */
  String toString();
}
