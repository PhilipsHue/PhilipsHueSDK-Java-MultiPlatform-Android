package com.philips.lighting.hue.local.sdk.demo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.philips.lighting.hue.sdk.PHHueSDK;

/**
 * Activity which gives hint for manual pushlink.
 * 
 * @author Pallavi P. Ganorkar
 */
public class PHPushlinkActivity extends Activity {
    private ProgressBar pbar;

    /**
     * Called when the activity will start interacting with the user.
     * 
     * @param savedInstanceState
     *            the bundle object.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pushlink);
        setTitle(R.string.txt_pushlink);
        pbar = (ProgressBar) findViewById(R.id.countdownPB);
        pbar.setMax(30);

    }

    /**
     * Called when the activity is becoming visible to the user.
     */
    @Override
    protected void onStart() {
        PHHueSDK phHueSDK = PHHueSDK.getInstance(getApplicationContext());
        phHueSDK.setCurrentActivty(this);

        super.onStart();
    }

    /**
     * Increment progress bar by 1.
     */
    public void incrementProgress() {
        pbar.incrementProgressBy(1);
    }

}
