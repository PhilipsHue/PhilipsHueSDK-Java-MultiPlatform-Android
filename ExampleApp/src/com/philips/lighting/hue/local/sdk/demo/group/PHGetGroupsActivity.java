package com.philips.lighting.hue.local.sdk.demo.group;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.philips.lighting.hue.listener.PHGroupListener;
import com.philips.lighting.hue.local.sdk.demo.PHHomeActivity;
import com.philips.lighting.hue.local.sdk.demo.PHWizardAlertDialog;
import com.philips.lighting.hue.local.sdk.demo.R;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHGroup;
import com.philips.lighting.model.PHHueError;

/**
 * Contains Demo for getting groups using bridge API
 * 
 * @author Manmath R
 */
public class PHGetGroupsActivity extends Activity {
    private static final String TAG = "PHGetGroupActivity";
    private ListView lvGroups;

    /**
     * Called when the activity is first created.
     * 
     * @param savedInstanceState
     *            the bundle object
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_group);

        lvGroups = (ListView) findViewById(R.id.list_groups);
        // set empty view if adapter data size=0
        TextView tvEmpty = (TextView) findViewById(R.id.empty_group_listview);
        tvEmpty.setText(R.string.txt_no_groups);
        lvGroups.setEmptyView(tvEmpty);

        // Get SDK wrapper
        PHHueSDK phHueSDK = PHHueSDK.getInstance();
        PHBridge bridge = phHueSDK.getSelectedBridge();

        final PHWizardAlertDialog dialogManager = PHWizardAlertDialog
                .getInstance();
        dialogManager.showProgressDialog(R.string.sending_progress,
                PHGetGroupsActivity.this);
        // call for getting groups from bridge
        List<PHGroup> groups = bridge.getResourceCache().getAllGroups();

        dialogManager.closeProgressDialog();
        lvGroups.setAdapter(new GroupListAdapter(groups));
        lvGroups.setSelector(android.R.color.transparent);
    }

    /**
     * Called when option is selected
     * 
     * @param item
     *            the MenuItem object.
     * @return boolean Return false to allow normal menu processing to proceed,
     *         true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            Intent intent = new Intent(this, PHHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            break;
        }
        return true;
    }

    /**
     * List adapter for displaying groups
     * 
     * @author Manmath R
     * 
     */
    class GroupListAdapter extends BaseAdapter {
        private List<PHGroup> groupHeaders;
        private LayoutInflater mInflater;

        /**
         * Constructor for adaptors
         * 
         * @param groupHeaders
         *            the array list of {@link PHBridgeResource}
         */
        public GroupListAdapter(List<PHGroup> groupHeaders) {
            this.groupHeaders = groupHeaders;
            mInflater = LayoutInflater.from(PHGetGroupsActivity.this);
        }

        /**
         * How many items are in the data set represented by this Adapter.
         * 
         * @return number of rows
         */
        @Override
        public int getCount() {
            return groupHeaders.size();
        }

        /**
         * Get the data item associated with the specified position in the data
         * set. position
         * 
         * @param arg0
         *            the row index
         * @return the object at row index
         */
        @Override
        public Object getItem(int arg0) {
            return groupHeaders.get(arg0);
        }

        /**
         * Get the row id associated with the specified position in the list.
         * 
         * @param arg0
         *            the row index
         * @return The id of the item at the specified position.
         * 
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
         * @param index
         *            the row index
         * @param convertView
         *            the row view
         * @param arg2
         *            the view group @ returns A View corresponding to the data
         *            at the specified position.
         */
        @Override
        public View getView(int index, View convertView, ViewGroup arg2) {

            RowView rv = null;
            if (convertView == null) {
                rv = new RowView();
                convertView = mInflater.inflate(R.layout.home_row, null);
                rv.tvName = (TextView) convertView
                        .findViewById(R.id.tv_bridge_ip);
                rv.tvId = (TextView) convertView
                        .findViewById(R.id.tv_lastHeartbeat);
                convertView.setTag(rv);
            } else {
                rv = (RowView) convertView.getTag();
            }
            PHBridgeResource groupHeader = groupHeaders.get(index);
            rv.tvName.setText(groupHeader.getName());
            rv.tvId.setText(groupHeader.getIdentifier());
            return convertView;
        }

        /**
         * Abstracts row view
         * 
         * @author Manmath R
         * 
         */
        class RowView {
            private TextView tvName;
            private TextView tvId;
        }
    }
}
