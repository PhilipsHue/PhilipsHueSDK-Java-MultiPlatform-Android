package com.philips.lighting.hue.local.sdk.demo;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueError;

/**
 * Activity to display list of connected bridges.
 * 
 * @author Pallavi P. Ganorakr
 */
public class PHAccessPointListActivity extends Activity implements
        OnItemClickListener {
    private AccessPointListAdapter adapter;
    private PHHueSDK phHueSDK;

    /**
     * Called when the activity will start interacting with the user.
     * 
     * @param savedInstanceState
     *            the bundle object.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.txt_selectbridges);
        setContentView(R.layout.bridgelistlinear);
        ListView accessPointList = (ListView) findViewById(R.id.bridge_list);
        accessPointList.setOnItemClickListener(this);
        phHueSDK = PHHueSDK.getInstance();
        adapter = new AccessPointListAdapter(this,phHueSDK.getAccessPointsFound());
        accessPointList.setAdapter(adapter);
        phHueSDK.getNotificationManager().registerSDKListener(listener);
    }

    /**
     * Called when the activity is becoming visible to the user.
     */
    @Override
    protected void onStart() {
        super.onStart();
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
        inflater.inflate(R.menu.bridgelist, menu);
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
        case R.id.refresh:
            phHueSDK = PHHueSDK.getInstance();
            PHWizardAlertDialog.getInstance().showProgressDialog(R.string.search_progress, this);
            PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
            sm.search(true, true);
            break;
        }
        return true;
    }

    /**
     * Method to refresh access point list.
     */
    public void refreshList() {
        adapter.updateData(phHueSDK.getAccessPointsFound());
    }

    /**
     * This class provides adapter for access point listview.
     * 
     * @author Pallavi P. Ganorkar.
     */
    private class AccessPointListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private ArrayList<PHAccessPoint> accessPoints;

        /**
         * View holder class for access point list.
         * 
         * @author Pallavi P. Ganorkar
         */
        class BridgeListItem {
            private TextView bridgeIp;
            private TextView bridgeMac;
        }

        /**
         * creates instance of {@link AccessPointListAdapter} class.
         * 
         * @param context
         *            the Context object.
         * @param accessPoints
         *            an array list of {@link PHAccessPoint} object to display.
         */
        public AccessPointListAdapter(Context context, ArrayList<PHAccessPoint> accessPoints) {
            // Cache the LayoutInflate to avoid asking for a new one each time.
            mInflater = LayoutInflater.from(context);
            this.accessPoints = accessPoints;
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
        public View getView(final int position, View convertView, ViewGroup parent) {

            BridgeListItem item;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.selectbridge_item, null);

                item = new BridgeListItem();
                item.bridgeMac = (TextView) convertView.findViewById(R.id.bridge_mac);
                item.bridgeIp = (TextView) convertView.findViewById(R.id.bridge_ip);

                convertView.setTag(item);
            } else {
                item = (BridgeListItem) convertView.getTag();
            }
            PHAccessPoint accessPoint = accessPoints.get(position);
            item.bridgeIp.setText(accessPoint.getIpAddress());
            item.bridgeMac.setText(accessPoint.getMacAddress());

            return convertView;
        }

        /**
         * Get the row id associated with the specified position in the list.
         * 
         * @param position
         *            the row index.
         * @return The id of the item at the specified position.
         */
        @Override
        public long getItemId(int position) {
            return 0;
        }

        /**
         * How many items are in the data set represented by this Adapter.
         * 
         * @return Count of items.
         */
        @Override
        public int getCount() {
            return accessPoints.size();
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
        public Object getItem(int position) {
            return accessPoints.get(position);
        }

        /**
         * Update date of the list view and refresh listview.
         * 
         * @param accessPoints
         *            an array list of {@link PHAccessPoint} objects.
         */
        void updateData(ArrayList<PHAccessPoint> accessPoints) {
            this.accessPoints = accessPoints;
            notifyDataSetChanged();
        }

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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HueSharedPreferences prefs = HueSharedPreferences.getInstance(getApplicationContext());
        PHAccessPoint accessPoint = (PHAccessPoint) adapter.getItem(position);
        accessPoint.setUsername(prefs.getUsername());
        
        PHBridge connectedBridge = phHueSDK.getSelectedBridge();       

        if (connectedBridge != null) {
            String connectedIP = connectedBridge.getResourceCache().getBridgeConfiguration().getIpAddress();
            if (connectedIP != null) {   // We are already connected here:-
                phHueSDK.disableHeartbeat(connectedBridge);
                phHueSDK.disconnect(connectedBridge);
            }
        }
        PHWizardAlertDialog.getInstance().showProgressDialog(R.string.connecting_progress, PHAccessPointListActivity.this);
        phHueSDK.connect(accessPoint);  
    }
   
    @Override
    protected void onPause(){
        super.onPause();
        PHWizardAlertDialog.getInstance().closeProgressDialog();
    }
    
    @Override
    protected void onDestroy(){
        super.onDestroy();
        phHueSDK.getNotificationManager().unregisterSDKListener(listener);
    }
    
    private PHSDKListener listener = new PHSDKListener() {

        @Override
        public void onAccessPointsFound(List<PHAccessPoint> accessPoint) {
            phHueSDK.getAccessPointsFound().clear();
            phHueSDK.getAccessPointsFound().addAll(accessPoint);
            PHAccessPointListActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onAuthenticationRequired(PHAccessPoint arg0) {}

        @Override
        public void onBridgeConnected(PHBridge arg0) {}

        @Override
        public void onCacheUpdated(int arg0, PHBridge arg1) {}

        @Override
        public void onConnectionLost(PHAccessPoint arg0) {}

        @Override
        public void onConnectionResumed(PHBridge arg0) {}

        @Override
        public void onError(int code, final String message) {
            if (code == PHMessageType.BRIDGE_NOT_FOUND) {
                PHWizardAlertDialog.getInstance().closeProgressDialog();

                PHAccessPointListActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        PHWizardAlertDialog.showErrorDialog(PHAccessPointListActivity.this, message, R.string.btn_ok);
                    }
                });                
            } else if (code == PHHueError.BRIDGE_NOT_RESPONDING) {
                PHWizardAlertDialog.getInstance().closeProgressDialog();
                PHAccessPointListActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isCurrentActivity()) { PHWizardAlertDialog.showErrorDialog(PHAccessPointListActivity.this, message, R.string.btn_ok); }
                    }
                }); 

            } 

        } // End of On Error
    };
    
    private boolean isCurrentActivity() {
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> RunningTask = mActivityManager.getRunningTasks(1);
        ActivityManager.RunningTaskInfo ar = RunningTask.get(0);
        String currentClass = "." + this.getClass().getSimpleName();
        String topActivity =  ar.topActivity.getShortClassName().toString();
        return topActivity.equals(currentClass);
    }
}

