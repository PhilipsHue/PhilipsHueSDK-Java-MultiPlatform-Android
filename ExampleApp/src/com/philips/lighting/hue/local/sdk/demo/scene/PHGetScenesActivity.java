package com.philips.lighting.hue.local.sdk.demo.scene;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.philips.lighting.hue.listener.PHSceneListener;
import com.philips.lighting.hue.local.sdk.demo.PHHomeActivity;
import com.philips.lighting.hue.local.sdk.demo.PHWizardAlertDialog;
import com.philips.lighting.hue.local.sdk.demo.R;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHScene;

/**
 * Contains Demo for getting all scenes from Bridge
 * 
 * @author Manmath R
 */
public class PHGetScenesActivity extends Activity {

    private ListView listView;

    /**
     * Called when the activity is first created.
     * 
     * @param savedInstanceState
     *            the bundle object
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_list);

        listView = (ListView) findViewById(R.id.list_items);
        TextView tvEmpty = (TextView) findViewById(R.id.empty_listview);
        tvEmpty.setText(R.string.txt_no_scene);
        listView.setEmptyView(tvEmpty);

        PHHueSDK phHueSDK = PHHueSDK.getInstance();
        PHBridge bridge = phHueSDK.getSelectedBridge();

        final PHWizardAlertDialog dialogManager = PHWizardAlertDialog
                .getInstance();
        dialogManager.showProgressDialog(R.string.sending_progress,
                PHGetScenesActivity.this);
        // called to get all scenes from bridge
        bridge.getAllScenes(new PHSceneListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStateUpdate(
                    Hashtable<String, String> successAttribute,
                    List<PHHueError> errorAttribute) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(int code, final String message) {
                dialogManager.closeProgressDialog();
                PHGetScenesActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        if (isCurrentActivity()) {
                            PHWizardAlertDialog.showErrorDialog(PHGetScenesActivity.this, message, R.string.btn_ok);
                        }
                    }
                  });
               
            }

            @Override
            public void onScenesReceived(List<PHScene> sceneList) {
                super.onScenesReceived(sceneList);
                dialogManager.closeProgressDialog();
                listView.setAdapter(new ScenesListAdapter(sceneList));
                listView.setSelector(android.R.color.transparent);
            }
        });
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
     * List adapter for displaying scenes
     * 
     * @author Manmath R
     * 
     */
    class ScenesListAdapter extends BaseAdapter {
        private List<PHScene> scenes;
        private LayoutInflater mInflater;

        /**
         * Constructor for adaptors
         * 
         * @param groupHeaders
         *            the array list of {@link PHScene}
         */
        public ScenesListAdapter(List<PHScene> scenes) {
            this.scenes = scenes;
            mInflater = LayoutInflater.from(PHGetScenesActivity.this);
        }

        /**
         * How many items are in the data set represented by this Adapter.
         * 
         * @return number of rows
         */
        @Override
        public int getCount() {
            return scenes.size();
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

            return scenes.get(arg0);
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
            PHScene scene = scenes.get(index);
            rv.tvName.setText(scene.getName());
            rv.tvId.setText(scene.getSceneIdentifier());
            return convertView;
        }

        class RowView {
            private TextView tvName;
            private TextView tvId;
        }
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
