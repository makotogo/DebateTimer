package com.makotojava.android.debate;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.makotojava.android.debate.model.PolicySpeechSingleton;

public class SpeechPagerActivity extends Activity {
  
  private static final String TAG = SpeechPagerActivity.class.getName();
  
  private static final String CLASS_PREFIX = SpeechPagerActivity.class.getName();
  public static final String EXTRA_SPEECH_INDEX = CLASS_PREFIX + ".SPEECH_INDEX";
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "Activity onCreate() called");
    // First, delegate to super class
    super.onCreate(savedInstanceState);
    // First look at any extra goodies
    Bundle extras = getIntent().getExtras();
    int initialIndex = 0;
    if (extras != null) {
      initialIndex = extras.getInt(EXTRA_SPEECH_INDEX);
    }
    // Set the content view to our speech pager
    setContentView(R.layout.activity_speech_pager);    
    // Now create the Pager
    ViewPager viewPager = (ViewPager)findViewById(R.id.speechPager);
    // Create the PagerAdapter
    SpeechPagerAdapter adapter = new SpeechPagerAdapter(this, getFragmentManager());
    viewPager.setAdapter(adapter);
    viewPager.setCurrentItem(initialIndex);
    viewPager.setOnPageChangeListener(adapter);
    // Keep screen on
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    //
  }
  
  @Override
  protected void onResume() {
    Log.d(TAG, "Activity onResume() called");
    super.onResume();
  }

  @Override
  protected void onPause() {
    Log.d(TAG, "Activity onPause() called");
    super.onPause();
  }

  public class SpeechPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {
    
    private List<PolicySpeechFragment> _speechFragmentStore;
    private Context _context;
    
    private int _numberOfTimesScrollWarningShown = 0;
    
    private static final int SCROLL_WARNING_LIMIT = 2;

    public SpeechPagerAdapter(Context context, FragmentManager fm) {
      super(fm);
      Log.d(TAG, "SpeechPagerAdapter constructor called");
      _context = context;
      // There are a small number of speeches, so go ahead and pre-create
      /// their Fragments
      _speechFragmentStore = new ArrayList<PolicySpeechFragment>();
      for (int aa = 0; aa < PolicySpeechSingleton.instance(SpeechPagerActivity.this).getNumberOfSpeeches(); aa++) {
        _speechFragmentStore.add(PolicySpeechFragment.newInstance(aa));
      }
    }

    @Override
    public Fragment getItem(int itemIndex) {
      // Retrieve the Fragment from the store
      Fragment ret = _speechFragmentStore.get(itemIndex);
      return ret;
    }

    @Override
    public int getCount() {
      return PolicySpeechSingleton.instance(SpeechPagerActivity.this).getNumberOfSpeeches();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
      // Nothing to do
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
      // Nothing to do
    }

    @Override
    public void onPageSelected(int position) {
      if (_numberOfTimesScrollWarningShown < SCROLL_WARNING_LIMIT) {
        String msg = "Scrolling this app when there are active timers can cause bad things to happen. Bad things, man. You have been warned.";
        Log.d(TAG, msg);
        Toast toast = Toast.makeText(_context, msg, Toast.LENGTH_LONG);
        _numberOfTimesScrollWarningShown++;
        toast.show();
      }
      // This page just became active. Tell it to manage its state.
      _speechFragmentStore.get(position).manageState();
    }
    
  }

}
