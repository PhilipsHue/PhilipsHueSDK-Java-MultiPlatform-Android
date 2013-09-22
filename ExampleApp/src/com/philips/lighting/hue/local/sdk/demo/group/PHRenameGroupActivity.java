package com.philips.lighting.hue.local.sdk.demo.group;

import java.util.ArrayList;
import java.util.Hashtable;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.philips.lighting.hue.listener.PHGroupListener;
import com.philips.lighting.hue.local.sdk.demo.PHHomeActivity;
import com.philips.lighting.hue.local.sdk.demo.PHWizardAlertDialog;
import com.philips.lighting.hue.local.sdk.demo.R;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.wizard.helper.PHClearableEditText;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHGroup;
import com.philips.lighting.model.PHHueError;

/**
 * Contains Demo for renaming a Group using Bridge API
 * 
 * @author Manmath R
 */
public class PHRenameGroupActivity extends Activity implements
        OnItemClickListener {

    private ListView lvGroups;
    private PHBridge bridge;

    /**
     * Called when the activity is first created.
     * 
     * @param savedInstanceState
     *            the bundle object
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        lvGroups = (ListView) findViewById(R.id.list_bridges);

        // set empty view if adapter data size=0

        lvGroups.setOnItemClickListener(this);

        // Get SDK wrapper
        PHHueSDK phHueSDK = PHHueSDK.getInstance(getApplicationContext());
        bridge = phHueSDK.getSelectedBridge();
        lvGroups.setAdapter(new GroupListAdapter(bridge.getResourceCache()
                .getAllGroups()));
    }

    /**
     * List adapter for list displaying groups
     * 
     * @author Manmath R
     * 
     */
    class GroupListAdapter extends BaseAdapter {
        private ArrayList<PHGroup> groups;
        private LayoutInflater mInflater;

        /**
         * Constructor for adaptors
         * 
         * @param groups
         *            the array list of {@link PHGroup}
         */
        public GroupListAdapter(ArrayList<PHGroup> groups) {
            this.groups = groups;
            mInflater = LayoutInflater.from(PHRenameGroupActivity.this);
        }

        /**
         * How many items are in the data set represented by this Adapter.
         * 
         * @return number of rows
         */
        @Override
        public int getCount() {
            return groups.size();
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

            return groups.get(arg0);
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

            RowView rowView = null;
            if (convertView == null) {
                rowView = new RowView();
                convertView = mInflater.inflate(R.layout.home_row, null);
                rowView.tvName = (TextView) convertView
                        .findViewById(R.id.tv_bridge_ip);
                rowView.tvId = (TextView) convertView
                        .findViewById(R.id.tv_lastHeartbeat);
                convertView.setTag(rowView);
            } else {
                rowView = (RowView) convertView.getTag();
            }
            PHGroup group = groups.get(index);
            rowView.tvName.setText(group.getName());
            rowView.tvId.setText(group.getIdentifier());
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

    /**
     * Callback method to be invoked when an item in this AdapterView has been
     * clicked.
     * 
     * @param arg0
     *            The AdapterView where the click happened.
     * @param arg1
     *            The view within the AdapterView that was clicked
     * @param index
     *            The position of the view in the adapter.
     * @param arg3
     *            The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
            long id) {
        renameGroupDialog((PHGroup) lvGroups.getAdapter().getItem(position));
    }

    /**
     * Shows dialog for renaming a group
     * 
     * @param group
     *            the {@link PHGroup}
     * @param group
     */
    private void renameGroupDialog(final PHGroup group) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final PHClearableEditText input = new PHClearableEditText(this);
        input.getEditText().setSingleLine();
        input.getEditText().setText(group.getName());
        InputFilter[] filter = new InputFilter[1];
        filter[0] = new InputFilter.LengthFilter(32);
        input.getEditText().setFilters(filter);
        input.getEditText().setSelection(group.getName().length());
        builder.setMessage(R.string.enter_group_name)
                .setView(input)
                .setPositiveButton(R.string.btn_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                final String nameNew = input.getText()
                                        .toString().trim();
                                if (nameNew == null || nameNew.length() == 0) {
                                    PHWizardAlertDialog.showErrorDialog(
                                            PHRenameGroupActivity.this,
                                            R.string.txt_empty_input);
                                    return;
                                }
                                PHGroup newGroup = new PHGroup(nameNew, group
                                        .getIdentifier());
                                bridge.updateGroup(newGroup,
                                        new PHGroupListener() {

                                            @Override
                                            public void onSuccess() {
                                                group.setName(nameNew);
                                                // update list
                                                ((GroupListAdapter) lvGroups
                                                        .getAdapter())
                                                        .notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onStateUpdate(
                                                    Hashtable<String, String> successAttribute,
                                                    ArrayList<PHHueError> errorAttribute) {
                                                // TODO Auto-generated method
                                                // stub

                                            }

                                            @Override
                                            public void onError(int code,
                                                    String message) {
                                                PHWizardAlertDialog
                                                        .showAuthenticationErrorDialog(
                                                                PHRenameGroupActivity.this,
                                                                message,
                                                                R.string.btn_ok);
                                            }
                                        });
                            }
                        })
                .setNegativeButton(R.string.btn_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                dialog.cancel();

                            }
                        }).setCancelable(false);
        AlertDialog alert = builder.create();
        alert.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alert.show();

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

}
