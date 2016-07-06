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
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.makotojava.android.debate.model.PolicySpeechFactory;
import com.makotojava.android.debate.model.PolicySpeechSingleton;
import com.makotojava.android.debate.util.RateApplicationUtils;

public class PolicyActivity extends Activity {

    private static final String TAG = PolicyActivity.class.getName();

    private PolicySpeechSingleton getPolicySpeechSingleton() {

        return PolicySpeechSingleton.instance(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }
        // Initialize Globals
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String prepTimeAsString = sharedPreferences.getString(SettingsActivity.PREF_KEY_PREP_TIME,
                Integer.toString(PolicySpeechFactory.PREP_TIME_IN_MINUTES));
        int prepTime = Integer.parseInt(prepTimeAsString);
        getPolicySpeechSingleton().setAffirmativePrepRemainingTime(
                prepTime * PolicySpeechFactory.NUMBER_OF_MILLIS_IN_ONE_MINUTE
        );
        getPolicySpeechSingleton().setNegativePrepRemainingTime(
                prepTime * PolicySpeechFactory.NUMBER_OF_MILLIS_IN_ONE_MINUTE
        );
        // Keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //
        getActionBar().setTitle(R.string.app_name);
        // Prompt the user for a rating
        //RateApplicationUtils.handleApplicationLaunch(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.policy_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menu_item_settings) {
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_item_rate_app) {
            RateApplicationUtils.goToPlayStoreListing(this);
            return true;
        } else if (id == R.id.menu_item_about) {
            AboutDialog aboutDialog = new AboutDialog(this);
            aboutDialog.setTitle(R.string.about);
            aboutDialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_policy,
                    container, false);

            // Fill the ListView with data
            /// It's okay to hard-code it. It will never change (unless they change
            /// the rules for Policy Debate)
            String[] columnNames = {
                    "Speech"
            };
            int[] rowIds = {
                    R.id.speech
            };
            // Create the List<Map<String,String>> that contains the data
            List<HashMap<String, String>> data = generateListData(columnNames);
            // Now create SimpleAdapter, which will map the data to the ListView
            SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(), data, R.layout.list_item_speech, columnNames, rowIds);
            ListView speechList = (ListView) rootView.findViewById(R.id.speechList);
            speechList.setAdapter(simpleAdapter);
            // Now install a Click Listener
            speechList.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.i(TAG, "Item at position " + position + " clicked. id => " + id + " item => " + parent.getItemAtPosition(position));
                    Intent intent = new Intent(getActivity(), SpeechPagerActivity.class);
                    intent.putExtra(SpeechPagerActivity.EXTRA_SPEECH_INDEX, position);
                    //
                    startActivity(intent);
                }

            });
            // Now return the newly created view
            return rootView;
        }

        private List<HashMap<String, String>> generateListData(String[] columnNames) {
            List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> item = new HashMap<>();
            item.put(columnNames[0], getResources().getString(R.string.speech_1ac));
            data.add(item);
            item = new HashMap<>();
            item.put(columnNames[0], getResources().getString(R.string.speech_1nc));
            data.add(item);
            item = new HashMap<>();
            item.put(columnNames[0], getResources().getString(R.string.speech_2ac));
            data.add(item);
            item = new HashMap<>();
            item.put(columnNames[0], getResources().getString(R.string.speech_2nc));
            data.add(item);
            item = new HashMap<>();
            item.put(columnNames[0], getResources().getString(R.string.speech_1nr));
            data.add(item);
            item = new HashMap<>();
            item.put(columnNames[0], getResources().getString(R.string.speech_1ar));
            data.add(item);
            item = new HashMap<>();
            item.put(columnNames[0], getResources().getString(R.string.speech_2nr));
            data.add(item);
            item = new HashMap<>();
            item.put(columnNames[0], getResources().getString(R.string.speech_2ar));
            data.add(item);

            return data;
        }
    }
}
