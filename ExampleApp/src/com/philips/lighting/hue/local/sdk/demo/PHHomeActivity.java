package com.philips.lighting.hue.local.sdk.demo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.philips.lighting.hue.local.sdk.demo.bridge.PHBridgeFeaturesActivity;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.hue.sdk.data.BridgeHeader;
import com.philips.lighting.hue.sdk.utilities.impl.PHLog;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueError;

/**
 * Home screen for sample application and lists down bridges connected and last
 * known access points.
 * 
 * @author Manmath R.
 */
public class PHHomeActivity extends Activity implements OnItemClickListener {
    private PHHueSDK phHueSDK;
    private ListView lvBridges;
    private BridgeListAdapter adapter;
    private RelativeLayout relLayoutVersion;
    private TextView tvVersion;
    private CheckBox cbLogging;
    private PHLog log;
    private static final String TAG = "PHHomeActivity";

    /**
     * Called when the activity will start interacting with the user.
     * 
     * @param savedInstanceState
     *            the bundle object.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        initializeComponents();

        // Get SDK wrapper
        phHueSDK = PHHueSDK.create(getApplicationContext());

        // Make footer visible for Home Activity
        relLayoutVersion.setVisibility(View.VISIBLE);

        tvVersion.setText(getString(R.string.txt_sdk_version)
                + phHueSDK.getSDKVersion());

        lvBridges.setOnItemClickListener(this);

        // by default logging feature is enabled
        cbLogging.setChecked(true);

        phHueSDK.getNotificationManager().registerSDKListener(listener);
        // logging feature enabled or disabled at runtime
        cbLogging.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    log.setSdkLogLevel(PHLog.DEBUG);
                } else {
                    log.setSdkLogLevel(0);
                }
            }
        });
    }

    /**
     * Initialize the UI components.
     */
    private void initializeComponents() {
        relLayoutVersion = (RelativeLayout) findViewById(R.id.includeVersion);
        cbLogging = (CheckBox) findViewById(R.id.cbLogging);
        tvVersion = (TextView) findViewById(R.id.tvSdkVersion);
        lvBridges = (ListView) findViewById(R.id.list_bridges);
        // set empty view if adapter data size=0
        lvBridges.setEmptyView(findViewById(R.id.empty_list_view));
    }

    /**
     * Called when the activity will start interacting with the user
     */
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
     * This class provides adapter for bridge listview.
     * 
     * @author Manmath R.
     */
    class BridgeListAdapter extends BaseAdapter {
        private ArrayList<BridgeHeader> bridgeData;
        private LayoutInflater mInflater;

        /**
         * creates instance of {@link BridgeListAdapter} class.
         * 
         * @param context
         *            the Context object.
         * @param accessPoints
         *            an array list of {@link PHAccessPoint} object to display.
         */
        public BridgeListAdapter(ArrayList<BridgeHeader> bridgeData) {
            this.bridgeData = bridgeData;
            mInflater = LayoutInflater.from(PHHomeActivity.this);
        }

        /**
         * How many items are in the data set represented by this Adapter.
         * 
         * @return Count of items.
         */
        @Override
        public int getCount() {
            return bridgeData.size();
        }

        /**
         * Get the data item associated with the specified position in the data
         * set.
         * 
         * @param position
         *            Position of the item whose data we want within the
         *            adapter's data set.
         * @return The data at the specified position.
         */
        @Override
        public Object getItem(int arg0) {

            return bridgeData.get(arg0);
        }

        /**
         * Get the row id associated with the specified position in the list.
         * 
         * @param position
         *            the row index.
         * @return The id of the item at the specified position.
         */
        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        /**
         * Get a View that displays the data at the specified position in the
         * data set.
         * 
         * @param position
         *            the row index.
         * @param convertView
         *            the row view.
         * @param parent
         *            the view group.
         * @return A View corresponding to the data at the specified position.
         */
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

            // to display last heartbeat.
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

        /**
         * View holder class for bridge list.
         * 
         * @author Pallavi P. Ganorkar.
         */
        class RowView {
            private TextView tvIPAddress;
            private TextView tvStatus;
            private TextView tvLastHeartbeat;
        }

        /**
         * Update date of the list view and refresh listview.
         * 
         * @param data
         *            an array list of {@link BridgeHeader} objects.
         */
        public void updateData(ArrayList<BridgeHeader> data) {
            bridgeData = data;
            notifyDataSetChanged();
        }
    }

    /**
     * Creates option menu.
     * 
     * @param menu
     *            the Menu Object.
     * @return true for the menu to be displayed.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home, menu);
        return true;
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
            PHWizardAlertDialog.getInstance().showProgressDialog(
                    R.string.search_progress, this);
            PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK
                    .getSDKService(PHHueSDK.SEARCH_BRIDGE);
            sm.search(true, true);
            break;
        }
        return true;
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has been
     * clicked.
     * 
     * @param parent
     *            The AdapterView where the click happened.
     * @param view
     *            The view within the AdapterView that was clicked
     * @param position
     *            The position of the view in the adapter.
     * @param id
     *            The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
        BridgeHeader header = (BridgeHeader) lvBridges.getItemAtPosition(index);
        if (header.getStatus().equals("Connected")) {
            Intent intent = new Intent(this, PHBridgeFeaturesActivity.class);
            startActivity(intent);
        } else {
            PHAccessPoint accessPoint = new PHAccessPoint();
            accessPoint.setIpAddress(header.getIPAddress());
            phHueSDK.connect(accessPoint);
        }
    }

    /**
     * Method to refresh bridge list.
     */
    public void refresh() {
        if (adapter != null) {
            if (phHueSDK == null) {
                phHueSDK = PHHueSDK.getInstance(getApplicationContext());
            }
            adapter.updateData(phHueSDK.getBridgesForDisplay());
        }
    }

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

            // This code is an example to show how to know which bridge entity
            // is updated
            if (((flags & PHMessageType.LIGHTS_CACHE_UPDATED) == PHMessageType.LIGHTS_CACHE_UPDATED)) {

            }

            if (((flags & PHMessageType.GROUPS_CACHE_UPDATED) == PHMessageType.GROUPS_CACHE_UPDATED)) {

            }

            if (((flags & PHMessageType.BRIDGE_CONFIGURATION_CACHE_UPDATED) == PHMessageType.BRIDGE_CONFIGURATION_CACHE_UPDATED)) {

            }

        }

        /**
         * Provides lists of access points found during the search
         * 
         * @param accessPoint
         *            the array list of {@link PHAccessPoint}
         */
        @Override
        public void onAccessPointsFound(ArrayList<PHAccessPoint> accessPoint) {
            Activity act = phHueSDK.getCurrentActivty();
            
            PHWizardAlertDialog.getInstance().closeProgressDialog();
            if (accessPoint != null && accessPoint.size() > 0) {
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
                        phHueSDK.getApplicationContext().getString(R.string.could_not_find_bridge), R.string.btn_retry);
            }
        }

        @Override
        public void onBridgeConnected(PHBridge bridge) {
            Log.d(TAG, "connected to bridge");
            phHueSDK.setSelectedBridge(bridge);

            Activity act = phHueSDK.getCurrentActivty();
            phHueSDK.getLastHeartbeat().put(bridge.getResourceCache().getBridgeConfiguration().getIpAddress(),  System.currentTimeMillis());
            PHWizardAlertDialog.getInstance().closeProgressDialog();

            if (act == null) {
                return;
            }
            if (act instanceof PHPushlinkActivity
                    || act instanceof PHAccessPointListActivity) {
               // finish Push link Dialog , it's connected after pushlink
                act.finish(); 
                            
            } else if (act instanceof PHHomeActivity) {
                ((PHHomeActivity) act).refresh();
            }

        }

        @Override
        public void onAuthenticationRequired(PHAccessPoint accessPoint) {
            Log.d(TAG, "onAuthentication");
            Activity act = phHueSDK.getCurrentActivty();
            PHWizardAlertDialog.getInstance().closeProgressDialog();
            act.startActivity(new Intent(act, PHPushlinkActivity.class));
            phHueSDK.startPushlinkAuthentication(accessPoint);
            if (act instanceof PHAccessPointListActivity) {
                act.finish(); 
                // it finishes bridge list
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

    public void goBackToHome() {
        Intent i = new Intent(phHueSDK.getApplicationContext(),
                PHHomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }
}
