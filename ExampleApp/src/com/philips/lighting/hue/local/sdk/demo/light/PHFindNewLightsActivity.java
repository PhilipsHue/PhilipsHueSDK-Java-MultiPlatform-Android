package com.philips.lighting.hue.local.sdk.demo.light;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.local.sdk.demo.PHWizardAlertDialog;
import com.philips.lighting.hue.local.sdk.demo.R;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHNotificationManager;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;

/**
 * Activity to find & display list of lights newly added around the bridge .
 * 
 * @author Pallavi P. Ganorkar
 * 
 */
public class PHFindNewLightsActivity extends Activity {
    private ProgressBar pbar;
    private ListView lampList;
    private TextView textInfo;
    private PHBridge bridge;
    private PHHueSDK phHueSDK;

    /**
     * Called when the activity is first created.
     * 
     * @param savedInstanceState
     *            the bundle object
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.findnewlamps);

        Button btnSearch = (Button) findViewById(R.id.button_start_searching);
        final RelativeLayout infoRoot = (RelativeLayout) findViewById(R.id.info_root);
        final RelativeLayout findRoot = (RelativeLayout) findViewById(R.id.find_root);
        pbar = (ProgressBar) findViewById(R.id.progress_bar);
        lampList = (ListView) findViewById(R.id.lamp_list);
        textInfo = (TextView) findViewById(R.id.text_info);

        btnSearch.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                infoRoot.setVisibility(View.GONE);
                findRoot.setVisibility(View.VISIBLE);
                startFindLamp();

            }
        });
        pbar.setMax(60);
        phHueSDK = PHHueSDK.getInstance();
        bridge = phHueSDK.getSelectedBridge();
    }

    /**
     * starts executing find lamp API
     */
    private void startFindLamp() {

        textInfo.setText(R.string.txt_discovering);
        phHueSDK.disableHeartbeat(bridge);
        // Request bridge to start the search process
        bridge.findNewLights(new SampleLightListener());

    }

    /**
     * Pauses the activity
     */
    @Override
    protected void onPause() {

        PHNotificationManager notificationManager = phHueSDK
                .getNotificationManager();
        notificationManager.cancelSearchNotification();
        super.onPause();
    }

    /**
     * The light listener object
     * 
     * @author Manmath
     * 
     */
    class SampleLightListener extends PHLightListener {

        /**
         * The callback method for error
         * 
         * @code the error code
         * @message the error message
         */
        @Override
        public void onError(int code, String message) {
            PHWizardAlertDialog.showAuthenticationErrorDialog(
                    PHFindNewLightsActivity.this, message, R.string.btn_ok);

        }

        /**
         * Called when light state has changed.
         * 
         * @param successAttribute
         *            the attributes set in bridge
         * @param errorAttribute
         *            the attributes failed to set in bridge
         */
        @Override
        public void onStateUpdate(Hashtable<String, String> successAttribute,
               List<PHHueError> errorAttribute) {
            // TODO Auto-generated method stub

        }

        /**
         * Called to convey success without any data from bridge
         */
        @Override
        public void onSuccess() {

        }

        /**
         * The light headers received callback
         * 
         * @lightHeaders the array list of {@link PHBridgeResource}
         * 
         */
        @Override
        public void onReceivingLights(List<PHBridgeResource> lightHeaders) {

            if (lightHeaders != null && lightHeaders.size() > 0) {
                LampListAdapter adapter = new LampListAdapter(
                        PHFindNewLightsActivity.this, lightHeaders);
                lampList.setAdapter(adapter);
            }
            // if search is not complete
            pbar.incrementProgressBy(10);
            // 10 second polling

        }

        /**
         * Indicates search is complete
         */
        @Override
        public void onSearchComplete() {
            // max 60 second
            pbar.incrementProgressBy(60);
            ListAdapter adapter = lampList.getAdapter();
            if (adapter == null || adapter.getCount() == 0) {
                textInfo.setText(R.string.txt_no_lights);
            } else {
                textInfo.setVisibility(View.GONE);
            }

        }

    }

    /**
     * The LampList adapter class.
     */
    private class LampListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private List<PHBridgeResource> lamps;

        /**
         * Constructor for adaptors
         * 
         * @param context
         *            the activity context
         * @param lamps
         *            the array list of {@link PHBridgeResource}
         */
        public LampListAdapter(Context context,
                List<PHBridgeResource> lamps) {
            // Cache the LayoutInflate to avoid asking for a new one each time.

            mInflater = LayoutInflater.from(context);
            this.lamps = lamps;
        }

        /**
         * Get a View that displays the data at the specified position in the
         * data set.
         * 
         * @param position
         *            the row index
         * @param convertView
         *            the row view
         * @param parent
         *            the view group @ returns A View corresponding to the data
         *            at the specified position.
         */
        public View getView(final int position, View convertView,
                ViewGroup parent) {

            TextView tv;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.findnewlamps_item,
                        null);

                tv = (TextView) convertView.findViewById(R.id.lamp_name);
                convertView.setTag(tv);
            } else {
                tv = (TextView) convertView.getTag();
            }

            tv.setText(lamps.get(position).getName());
            return convertView;
        }

        /**
         * Get the row id associated with the specified position in the list.
         * 
         * @param position
         *            the row index
         * @return The id of the item at the specified position.
         * 
         */
        @Override
        public long getItemId(int position) {
            return 0;
        }

        /**
         * How many items are in the data set represented by this Adapter.
         * 
         * @return number of rows
         */
        @Override
        public int getCount() {
            return lamps.size();
        }

        /**
         * Get the data item associated with the specified position in the data
         * set. position
         * 
         * @param position
         *            the row index
         * @return the object at row index
         */
        @Override
        public Object getItem(int position) {
            return lamps.get(position);
        }
    }

    /**
     * Creates option menu
     * 
     * @param munu
     *            the menu object
     * @return true for the menu to be displayed
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.done, menu);
        return true;
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
        case R.id.done:

            finish();
            break;
        }
        return true;
    }

    /**
     * The final call you receive before your activity is destroyed. Heart beat
     * is preferred to be stopped while operation with bridge parameters.So
     * after finishing bridge activities the heart beat is restarted.
     * 
     */
    @Override
    protected void onDestroy() {

        phHueSDK.enableHeartbeat(bridge, PHHueSDK.HB_INTERVAL);
        super.onDestroy();
    }

}
