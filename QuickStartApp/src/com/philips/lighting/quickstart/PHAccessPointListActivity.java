package com.philips.lighting.quickstart;

import java.util.ArrayList;

import android.app.Activity;
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
        phHueSDK = PHHueSDK.getInstance(getApplicationContext());
        adapter = new AccessPointListAdapter(this,
                phHueSDK.getAccessPointsFound());
        accessPointList.setAdapter(adapter);
    }

    /**
     * Called when the activity is becoming visible to the user.
     */
    @Override
    protected void onStart() {
        phHueSDK.setCurrentActivty(this);
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
            phHueSDK = PHHueSDK.getInstance(getApplicationContext());
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
        public AccessPointListAdapter(Context context,
                ArrayList<PHAccessPoint> accessPoints) {
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
        public View getView(final int position, View convertView,
                ViewGroup parent) {

            BridgeListItem item;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.selectbridge_item,
                        null);

                item = new BridgeListItem();
                item.bridgeMac = (TextView) convertView
                        .findViewById(R.id.bridge_mac);
                item.bridgeIp = (TextView) convertView
                        .findViewById(R.id.bridge_ip);

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
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        PHAccessPoint accessPoint = (PHAccessPoint) adapter.getItem(position);
        phHueSDK.connect(accessPoint);

    }

}
