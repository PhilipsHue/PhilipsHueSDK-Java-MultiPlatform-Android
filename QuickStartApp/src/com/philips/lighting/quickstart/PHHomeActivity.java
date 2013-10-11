package com.philips.lighting.quickstart;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.hue.sdk.data.BridgeHeader;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueError;

public class PHHomeActivity extends Activity implements OnItemClickListener {

    private PHHueSDK phHueSDK;
    private BridgeListAdapter adapter;
    public static final String TAG = "QuickStart";
    private ListView lvBridges;
    private RelativeLayout relLayoutVersion;
    private TextView tvVersion;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        initializeComponents();
        phHueSDK = PHHueSDK.create(getApplicationContext());

        lvBridges.setOnItemClickListener(this);
        
        // Make footer visible for Home Activity
        relLayoutVersion.setVisibility(View.VISIBLE);
        tvVersion.setText(getString(R.string.txt_sdk_version) + phHueSDK.getSDKVersion());
        Log.w(TAG, "Listener Registered1.   Starting Search Manager.");
        // Register the PHSDKListener to receive callbacks from the bridge.
        phHueSDK.getNotificationManager().registerSDKListener(listener);

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
        public void onError(int code, String message) {
            Log.e(TAG, "on Error Called : " + code + ":" + message);
            Activity act = phHueSDK.getCurrentActivty();

            if (act == null) {
                return;
            }
            if (code == PHMessageType.BRIDGE_NOT_FOUND) {
                PHWizardAlertDialog.getInstance().closeProgressDialog();
                PHWizardAlertDialog.showErrorDialog(act, message,
                        R.string.btn_ok);
            } else if (code == PHMessageType.PUSHLINK_BUTTON_NOT_PRESSED) {
                if (act instanceof PHPushlinkActivity) {
                    ((PHPushlinkActivity) act).incrementProgress();
                }
            } else if (code == PHMessageType.PUSHLINK_AUTHENTICATION_FAILED) {
                if (act instanceof PHPushlinkActivity) {
                    ((PHPushlinkActivity) act).incrementProgress();
                }
                PHWizardAlertDialog.showAuthenticationErrorDialog(act, message,
                        R.string.btn_ok);

            } else if (code == PHHueError.BRIDGE_NOT_RESPONDING) {
                PHWizardAlertDialog.getInstance().closeProgressDialog();
                PHWizardAlertDialog.showErrorDialog(act, message, R.string.btn_ok);

            } else if (code == PHHueError.NO_CONNECTION) {
                Log.w(TAG, "On No Connection");
            } else if (code == PHHueError.AUTHENTICATION_FAILED) {
                PHWizardAlertDialog.getInstance().closeProgressDialog();

                if (act instanceof PHHomeActivity) {
                    PHWizardAlertDialog.showErrorDialog(act, message,
                            R.string.btn_ok);
                    ((PHHomeActivity) act).refresh();
                } else {
                    goBackToHome();
                }
            } else {
                // For any other error
                PHWizardAlertDialog.getInstance().closeProgressDialog();
                Toast.makeText(phHueSDK.getApplicationContext(), message,
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCacheUpdated(int flags, PHBridge bridge) {
            Log.w(TAG, "On CacheUpdated");

        }

        @Override
        public void onAccessPointsFound(List<PHAccessPoint> accessPoint) {
            Log.w(TAG, "Access Points Found.");
            Activity act = phHueSDK.getCurrentActivty();

            PHWizardAlertDialog.getInstance().closeProgressDialog();
            if (accessPoint != null && accessPoint.size() > 0) {
                phHueSDK.getAccessPointsFound().clear();
                phHueSDK.getAccessPointsFound().addAll(accessPoint);
                // show list of bridges
                if (act instanceof PHAccessPointListActivity) {
                    ((PHAccessPointListActivity) act).refreshList();
                } else {
                    act.startActivity(new Intent(act,
                            PHAccessPointListActivity.class));
                }

            } else {
                // show error dialog
                PHWizardAlertDialog.showErrorDialog(act,
                        phHueSDK.getApplicationContext().getString( R.string.could_not_find_bridge),R.string.btn_retry);
            }
        }

        @Override
        public void onBridgeConnected(PHBridge b) {
            Log.w(TAG, "Bridge Connected.");
            phHueSDK.setSelectedBridge(b);
            phHueSDK.getLastHeartbeat().put(b.getResourceCache().getBridgeConfiguration() .getIpAddress(), System.currentTimeMillis());
            PHWizardAlertDialog.getInstance().closeProgressDialog();
            Intent intent = new Intent(getApplicationContext(),
                    MyApplicationActivity.class);
            startActivity(intent);
        }

        @Override
        public void onAuthenticationRequired(PHAccessPoint accessPoint) {
            Log.w(TAG, "Authentication Required.");

            Activity act = phHueSDK.getCurrentActivty();
            PHWizardAlertDialog.getInstance().closeProgressDialog();
            act.startActivity(new Intent(act, PHPushlinkActivity.class));
            phHueSDK.startPushlinkAuthentication(accessPoint);
            if (act instanceof PHAccessPointListActivity) {
                act.finish();
            } else if (act instanceof PHHomeActivity) {
                ((PHHomeActivity) act).refresh();
            }
        }

        @Override
        public void onConnectionResumed(PHBridge bridge) {
            
            if (PHHomeActivity.this.isFinishing())
                return;
            
            Log.v(TAG, "onConnectionResumed" + bridge.getResourceCache().getBridgeConfiguration().getIpAddress());
            phHueSDK.getLastHeartbeat().put(bridge.getResourceCache().getBridgeConfiguration().getIpAddress(),  System.currentTimeMillis());
            for (int i = 0; i < phHueSDK.getDisconnectedAccessPoint().size(); i++) {

                if (phHueSDK.getDisconnectedAccessPoint()
                        .get(i).getIpAddress()
                        .equals(bridge.getResourceCache().getBridgeConfiguration().getIpAddress())) {
                    phHueSDK.getDisconnectedAccessPoint().remove(i);
                }
            }

            PHHomeActivity.this.refresh();
        }

        @Override
        public void onConnectionLost(PHAccessPoint accessPoint) {
            Activity act = phHueSDK.getCurrentActivty();
            Log.v(TAG, "onConnectionLost : " + accessPoint.getIpAddress());
            if (!phHueSDK.getDisconnectedAccessPoint().contains(accessPoint)) {
                phHueSDK.getDisconnectedAccessPoint().add(accessPoint);
                if (act instanceof PHHomeActivity) {
                    ((PHHomeActivity) act).refresh();
                } else {
                    goBackToHome();
                }
            }
        }
    };

    public void refresh() {
        if (adapter != null) {
            if (phHueSDK == null) {
                phHueSDK = PHHueSDK.create(getApplicationContext());
            }
            adapter.updateData(phHueSDK.getBridgesForDisplay());
        }

    }

    /**
     * Adapter for bridges list
     * 
     * @author Steve O'R
     * 
     */
    class BridgeListAdapter extends BaseAdapter {
        private List <BridgeHeader> bridgeData;
        private LayoutInflater mInflater;

        public BridgeListAdapter(ArrayList<BridgeHeader> bridgeData) {
            this.bridgeData = bridgeData;
            mInflater = LayoutInflater.from(PHHomeActivity.this);
        }

        @Override
        public int getCount() {
            return bridgeData.size();
        }

        @Override
        public Object getItem(int arg0) {

            return bridgeData.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int index, View convertView, ViewGroup arg2) {

            RowView rv = null;
            if (convertView == null) {
                rv = new RowView();
                convertView = mInflater.inflate(R.layout.home_row, null);
                rv.tvIPAddress = (TextView) convertView
                        .findViewById(R.id.tv_bridge_ip);
                rv.tvStatus = (TextView) convertView
                        .findViewById(R.id.tv_status);
                rv.tvStatus.setVisibility(View.VISIBLE);
                rv.tvLastHeartbeat = (TextView) convertView
                        .findViewById(R.id.tv_lastHeartbeat);
                convertView.setTag(rv);
            } else {
                rv = (RowView) convertView.getTag();
            }
            BridgeHeader data = bridgeData.get(index);
            rv.tvIPAddress.setText(data.getIPAddress());
            rv.tvStatus.setText(data.getStatus());
            if (data.getLastHeartbeat() == -1) {
                rv.tvLastHeartbeat.setText(R.string.txt_last_heartbeat);
            } else {
                SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss aa");
                rv.tvLastHeartbeat.setText(getResources().getString(
                        R.string.txt_last_heartbeat)
                        + " " + df.format(new Date(data.getLastHeartbeat())));
            }

            return convertView;
        }

        class RowView {
            private TextView tvIPAddress;
            private TextView tvStatus;
            private TextView tvLastHeartbeat;
        }

        public void updateData(ArrayList<BridgeHeader> data) {
            bridgeData = data;
            notifyDataSetChanged();
        }
    }

    public void goBackToHome() {
        Intent i = new Intent(phHueSDK.getApplicationContext(),
                PHHomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    /**
     * Initialize the UI components.
     */
    private void initializeComponents() {
        relLayoutVersion = (RelativeLayout) findViewById(R.id.includeVersion);
        tvVersion = (TextView) findViewById(R.id.tvSdkVersion);
        lvBridges = (ListView) findViewById(R.id.list_bridges);
        // set empty view if adapter data size=0
        lvBridges.setEmptyView(findViewById(R.id.empty_list_view));
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        phHueSDK.setCurrentActivty(this);
        ArrayList<BridgeHeader> bridgeData = phHueSDK.getBridgesForDisplay();
        adapter = new BridgeListAdapter(bridgeData);
        lvBridges.setAdapter(adapter);
        lvBridges.invalidate();
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
            PHWizardAlertDialog.getInstance().showProgressDialog(R.string.search_progress, this);
            PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
            // Start the UPNP Searching of local bridges.
            sm.search(true, true);
            break;
        }
        return true;
    }
    
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
        BridgeHeader header = (BridgeHeader) lvBridges.getItemAtPosition(index);
        if (header.getStatus().equals("Connected")) {

            Intent intent = new Intent(this, MyApplicationActivity.class);
            startActivity(intent);
        } else {
            PHAccessPoint accessPoint = new PHAccessPoint();
            accessPoint.setIpAddress(header.getIPAddress());
            phHueSDK.connect(accessPoint);
        }
    }

    /**
     * Called when the activity has detected the user's press of the back key.
     */
    @Override
    public void onBackPressed() {
        // Preferred to do it here because it might take more time. So let's
        // don't do this time consuming task inside onDestroy.
        if (phHueSDK != null) {
            phHueSDK.stopAllHeartbeat();
            phHueSDK.destroySDK();
            phHueSDK = null;
        }

        super.onBackPressed();
    }
}
