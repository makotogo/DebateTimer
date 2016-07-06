package com.makotojava.android.debate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/**
 * Original code from:
 * 
 * @author 
 *         http://www.techrepublic.com/blog/software-engineer/a-reusable-about-dialog
 *         -for-your-android-apps/
 * @author steve
 *
 */
public class AboutDialog extends Dialog {
  
  private static final String TAG = AboutDialog.class.getName();
  
  private Context _context = null;

  public AboutDialog(Context context) {
    super(context);

    _context = context;

  }

  /**
   * 
   * 
   * Standard Android on create method that gets called when the activity
   * initialized.
   */

  @Override
  public void onCreate(Bundle savedInstanceState) {
    setContentView(R.layout.about);

    TextView tv = (TextView) findViewById(R.id.legal_text);

    tv.setText(readRawTextFile(R.raw.legal));

//    tv = (TextView) findViewById(R.id.info_text);
//
//    tv.setText(Html.fromHtml(readRawTextFile(R.raw.info)));
//
    //tv.setLinkTextColor(Color.WHITE);

//    Linkify.addLinks(tv, Linkify.ALL);
    
    tv = (TextView)findViewById(R.id.info_version);
    String packageName = getContext().getPackageName();
    PackageInfo packageInfo;
    try {
      packageInfo = getContext().getPackageManager().getPackageInfo(packageName, 0);
      String appInfo = "Policy/CrossEx Debate Timer";
      String versionInfo = "Version " + 
          packageInfo.versionName + " (Build " + 
          Integer.toString(packageInfo.versionCode) + ")";
      tv.setText(appInfo + "\n" + versionInfo);
    } catch (NameNotFoundException e) {
      Log.e(TAG, "Call to getPackageInfo() failed! => ", e);
    }

  }

  public String readRawTextFile(int id) {

    InputStream inputStream = _context.getResources().openRawResource(id);

    InputStreamReader in = new InputStreamReader(inputStream);
    BufferedReader buf = new BufferedReader(in);

    String line;

    StringBuilder text = new StringBuilder();
    try {

      while ((line = buf.readLine()) != null)
        text.append(line);
      
      inputStream.close();
    } catch (IOException e) {
      return null;

    }
    
    return text.toString();

  }

}