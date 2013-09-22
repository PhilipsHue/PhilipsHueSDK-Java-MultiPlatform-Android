package com.philips.lighting.quickstart;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.philips.lighting.hue.sdk.PHHueSDK;

/**
 * Activity which gives hint for manual pushlink. needs to add <activity
 * android:theme="@android:style/Theme.Dialog" /> in manifest file
 * 
 * @author Stephen O'Reilly
 * 
 */

public class PHPushlinkActivity extends Activity {
    private ProgressBar pbar;
    private static final int MAX_TIME=30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pushlink);
        setTitle(R.string.txt_pushlink);
        pbar = (ProgressBar) findViewById(R.id.countdownPB);
        pbar.setMax(MAX_TIME);

    }

    @Override
    protected void onStart() {
        PHHueSDK phHueSDK;
        phHueSDK = PHHueSDK.getInstance(getApplicationContext());
        phHueSDK.setCurrentActivty(this);

        super.onStart();
    }

    public void incrementProgress() {
        pbar.incrementProgressBy(1);
    }

}
