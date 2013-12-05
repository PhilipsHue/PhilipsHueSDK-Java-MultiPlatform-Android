package com.philips.lighting.hue.local.sdk.demo.light;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
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

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.local.sdk.demo.PHHomeActivity;
import com.philips.lighting.hue.local.sdk.demo.PHWizardAlertDialog;
import com.philips.lighting.hue.local.sdk.demo.R;
import com.philips.lighting.hue.local.sdk.demo.group.PHCreateGroupActivity;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.wizard.helper.PHClearableEditText;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;

/**
 * Contains Demo for renaming light name
 * 
 * @author Manmath R
 */
public class PHRenameLightListActivity extends Activity implements
        OnItemClickListener {
    private ListView lvLights;
    private PHBridge bridge;

    /**
     * Called when the activity is first created.
     * 
     * @param savedInstanceState
     *            the bundle object
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        lvLights = (ListView) findViewById(R.id.list_bridges);
        // set empty view if adapter data size=0
        TextView tvEmpty = (TextView) findViewById(R.id.empty_list_view);
        tvEmpty.setText(R.string.txt_no_lights);
        lvLights.setEmptyView(tvEmpty);
        lvLights.setOnItemClickListener(this);
        // Get SDK wrapper
        PHHueSDK phHueSDK = PHHueSDK.getInstance();
        bridge = phHueSDK.getSelectedBridge();
        lvLights.setAdapter(new LightListAdapter(bridge.getResourceCache().getAllLights()));
    }

    /**
     * Adapter for bridges list
     * 
     * @author Manmath R
     */
    class LightListAdapter extends BaseAdapter {
        private List<PHLight> lights;
        private LayoutInflater mInflater;

        /**
         * Constructor for adaptors
         * 
         * @param lights
         *            the array list of {@link PHLight}
         */
        public LightListAdapter(List<PHLight> lights) {
            this.lights = lights;
            mInflater = LayoutInflater.from(PHRenameLightListActivity.this);
        }

        /**
         * How many items are in the data set represented by this Adapter.
         * 
         * @return number of rows
         */
        @Override
        public int getCount() {
            return lights.size();
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

            return lights.get(arg0);
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
            PHLight light = lights.get(index);
            rv.tvName.setText(light.getName());
            rv.tvId.setText(light.getIdentifier());
            return convertView;
        }

        /**
         * The row view abstraction
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
    public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
        renameLampDialog((PHLight) lvLights.getAdapter().getItem(index));
    }

    /**
     * Rename dialog is shown for the selected {@link PHLight}
     * 
     * @param light
     *            the {@link PHLight} object
     */
    private void renameLampDialog(final PHLight light) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final PHClearableEditText input = new PHClearableEditText(this);
        input.getEditText().setSingleLine();
        input.getEditText().setText(light.getName());
        InputFilter[] filter = new InputFilter[1];
        filter[0] = new InputFilter.LengthFilter(32);
        input.getEditText().setFilters(filter);
        input.getEditText().setSelection(light.getName().length());
        builder.setMessage(R.string.enter_light_name)
                .setView(input)
                .setPositiveButton(R.string.btn_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                final String nameNew = input.getText()
                                        .toString().trim();
                                if (nameNew == null || nameNew.length() == 0) {
                                    PHWizardAlertDialog.showErrorDialog(
                                            PHRenameLightListActivity.this,
                                            R.string.txt_empty_input);
                                    return;
                                }
                                PHLight newLight = new PHLight(nameNew, light
                                        .getIdentifier(), light
                                        .getVersionNumber(), light
                                        .getModelNumber());
                                bridge.updateLight(newLight,
                                        new PHLightListener() {

                                            @Override
                                            public void onSuccess() {
                                                light.setName(nameNew);
                                                // update list
                                                ((LightListAdapter) lvLights
                                                        .getAdapter())
                                                        .notifyDataSetChanged();

                                            }

                                            @Override
                                            public void onStateUpdate(
                                                    Hashtable<String, String> successAttribute,
                                                    List<PHHueError> errorAttribute) {
                                                // TODO Auto-generated method
                                                // stub

                                            }

                                            @Override
                                            public void onError(int code, final String message) {
                                                PHRenameLightListActivity.this.runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        if (isCurrentActivity()) {
                                                            PHWizardAlertDialog.showAuthenticationErrorDialog(PHRenameLightListActivity.this, message, R.string.btn_ok);
                                                        }
                                                    }
                                                  });
                                               
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
    
    private boolean isCurrentActivity() {
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> RunningTask = mActivityManager.getRunningTasks(1);
        ActivityManager.RunningTaskInfo ar = RunningTask.get(0);
        String currentClass = "." + this.getClass().getSimpleName();
        String topActivity =  ar.topActivity.getShortClassName().toString();
        return topActivity.contains(currentClass);
    }
}
