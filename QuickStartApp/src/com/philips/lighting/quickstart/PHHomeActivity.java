package com.philips.lighting.quickstart;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.philips.lighting.data.HueSharedPreferences;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueError;

public class PHHomeActivity extends Activity {

    private PHHueSDK phHueSDK;
    public static final String TAG = "QuickStart";
    private RelativeLayout relLayoutVersion;
    private TextView tvVersion;
    HueSharedPreferences prefs;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        initializeComponents();
        phHueSDK = PHHueSDK.create();
        
        // Set the Device Name (name of your app). This will be stored in your bridge whitelist entry.
        phHueSDK.setDeviceName("QuickStartApp");
        
        // Make footer visible for Home Activity
        relLayoutVersion.setVisibility(View.VISIBLE);
        tvVersion.setText(getString(R.string.txt_sdk_version) + phHueSDK.getSDKVersion());
        Log.w(TAG, "Listener Registered1.   Starting Search Manager.");
        // Register the PHSDKListener to receive callbacks from the bridge.
        phHueSDK.getNotificationManager().registerSDKListener(listener);
        
        // Try to automatically connect to the last known bridge.
        prefs = HueSharedPreferences.getInstance(getApplicationContext());
        String lastIpAddress   = prefs.getLastConnectedIPAddress();
        String lastUsername    = prefs.getUsername();

        // Automatically try and to connect to the last connected IP Address.  For multiple bridge support a different implementation is required.
        if (lastIpAddress !=null) {
            PHWizardAlertDialog.getInstance().showProgressDialog(R.string.connecting, PHHomeActivity.this);
            PHAccessPoint lastAccessPoint = new PHAccessPoint();
            lastAccessPoint.setIpAddress(lastIpAddress);
            lastAccessPoint.setUsername(lastUsername);
            phHueSDK.connect(lastAccessPoint);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    /**
     * Listeners AREA
     */

    // Local SDK LIstener
    private PHSDKListener listener = new PHSDKListener() {

        @Override
        public void onError(int code, final String message) {
            Log.e(TAG, "on Error Called : " + code + ":" + message);

            if (code == PHHueError.NO_CONNECTION) {
                Log.w(TAG, "On No Connection");
            } else if (code == PHHueError.AUTHENTICATION_FAILED) {  
                PHWizardAlertDialog.getInstance().closeProgressDialog();
            } else if (code == PHHueError.BRIDGE_NOT_RESPONDING) {
                Log.w("QuickStart", "Bridge Not Responding . . . ");
                PHWizardAlertDialog.getInstance().closeProgressDialog();
                PHHomeActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isCurrentActivity()) { PHWizardAlertDialog.showErrorDialog(PHHomeActivity.this, message, R.string.btn_ok); }
                    }
                }); 

            } 
            else {
                // For any other error
 
            }
        }

        @Override
        public void onCacheUpdated(int flags, PHBridge bridge) {
            Log.w(TAG, "On CacheUpdated");

        }

        @Override
        public void onAccessPointsFound(List<PHAccessPoint> accessPoint) {
            Log.w(TAG, "Access Points Found.");

            PHWizardAlertDialog.getInstance().closeProgressDialog();
            if (accessPoint != null && accessPoint.size() > 0) {
                phHueSDK.getAccessPointsFound().clear();
                phHueSDK.getAccessPointsFound().addAll(accessPoint);
                startActivity(new Intent(getApplicationContext(),PHAccessPointListActivity.class));
            } else {
                // TODO Test this.
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
//                PHWizardAlertDialog.getInstance().showProgressDialog(R.string.search_progress, PHHomeActivity.this);
//                PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
//                // Start the IP Scan Search if the UPNP and NPNP return 0 results.
//                sm.search(false, false, true);
//            }
            }
            
        }

        @Override
        public void onBridgeConnected(PHBridge b) {
            phHueSDK.setSelectedBridge(b);
            phHueSDK.enableHeartbeat(b, PHHueSDK.HB_INTERVAL);
            phHueSDK.getLastHeartbeat().put(b.getResourceCache().getBridgeConfiguration() .getIpAddress(), System.currentTimeMillis());
            prefs.setLastConnectedIPAddress(b.getResourceCache().getBridgeConfiguration().getIpAddress());
            prefs.setUsername(prefs.getUsername());
            PHWizardAlertDialog.getInstance().closeProgressDialog();
            startMainActivity();
        }

        @Override
        public void onAuthenticationRequired(PHAccessPoint accessPoint) {
            Log.w(TAG, "Authentication Required.");
            
            phHueSDK.startPushlinkAuthentication(accessPoint);
            startActivity(new Intent(PHHomeActivity.this, PHPushlinkActivity.class));
           
        }

        @Override
        public void onConnectionResumed(PHBridge bridge) {
            if (PHHomeActivity.this.isFinishing())
                return;
            
            Log.v(TAG, "onConnectionResumed" + bridge.getResourceCache().getBridgeConfiguration().getIpAddress());
            phHueSDK.getLastHeartbeat().put(bridge.getResourceCache().getBridgeConfiguration().getIpAddress(),  System.currentTimeMillis());
            for (int i = 0; i < phHueSDK.getDisconnectedAccessPoint().size(); i++) {

                if (phHueSDK.getDisconnectedAccessPoint().get(i).getIpAddress().equals(bridge.getResourceCache().getBridgeConfiguration().getIpAddress())) {
                    phHueSDK.getDisconnectedAccessPoint().remove(i);
                }
            }

        }

        @Override
        public void onConnectionLost(PHAccessPoint accessPoint) {
            Log.v(TAG, "onConnectionLost : " + accessPoint.getIpAddress());
            if (!phHueSDK.getDisconnectedAccessPoint().contains(accessPoint)) {
                phHueSDK.getDisconnectedAccessPoint().add(accessPoint);
            }
        }
    };


//    public void goBackToHome() {
//        Intent i = new Intent(getApplicationContext(),PHHomeActivity.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//    }

    /**
     * Initialize the UI components.
     */
    private void initializeComponents() {
        relLayoutVersion = (RelativeLayout) findViewById(R.id.includeVersion);
        tvVersion = (TextView) findViewById(R.id.tvSdkVersion);
    }

    /**
     * Called when option is selected.
     * 
     * @param item
     *            the MenuItem object.
     * @return boolean Return false to allow normal menu processing to proceed,
     *         true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.find_new_bridge:
            // Display Search Progress animation.
            PHWizardAlertDialog.getInstance().showProgressDialog(R.string.search_progress, PHHomeActivity.this);
            PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
            // Start the UPNP Searching of local bridges.
            sm.search(true, true);
            break;
        }
        return true;
    }


    /**
     * Called when the activity has detected the user's press of the back key.
     */
    @Override
    public void onBackPressed() {
        // Preferred to do it here because it might take more time. So let's
        // don't do this time consuming task inside onDestroy.
        if (phHueSDK != null) {
            phHueSDK.disableAllHeartbeat();
            phHueSDK.destroySDK();
            phHueSDK = null;
        }

        super.onBackPressed();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        phHueSDK.disableAllHeartbeat();
    }
    
    public void startMainActivity() {   
        Intent intent = new Intent(getApplicationContext(), MyApplicationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            intent.addFlags(0x8000); // equal to Intent.FLAG_ACTIVITY_CLEAR_TASK which is only available from API level 11
        startActivity(intent);
    }
    
    private boolean isCurrentActivity() {
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> RunningTask = mActivityManager.getRunningTasks(1);
        ActivityManager.RunningTaskInfo ar = RunningTask.get(0);
        String currentClass = "." + this.getClass().getSimpleName();
        String topActivity =  ar.topActivity.getShortClassName().toString();
        return topActivity.equals(currentClass);
    }
}
