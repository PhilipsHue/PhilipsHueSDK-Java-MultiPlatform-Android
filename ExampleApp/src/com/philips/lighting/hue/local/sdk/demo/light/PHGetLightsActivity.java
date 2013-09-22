package com.philips.lighting.hue.local.sdk.demo.light;

import java.util.ArrayList;
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

import com.philips.lighting.hue.local.sdk.demo.PHHomeActivity;
import com.philips.lighting.hue.local.sdk.demo.R;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;

/**
 * Contains Demo for getting lights from bridge cache maintained inside the SDK.
 * There is no API exposed in this version to get lights only from bridge
 * hardware.
 * 
 * @author Manmath R
 */
public class PHGetLightsActivity extends Activity {
    private ListView lvLights;

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
        // set empty view if adpater data size=0
        TextView tvEmpty = (TextView) findViewById(R.id.empty_list_view);
        tvEmpty.setText(R.string.txt_no_lights);
        lvLights.setEmptyView(tvEmpty);
        // Get SDK wrapper
        PHHueSDK phHueSDK = PHHueSDK.getInstance(getApplicationContext());
        PHBridge bridge = phHueSDK.getSelectedBridge();
        lvLights.setAdapter(new LightListAdapter(bridge.getResourceCache()
                .getAllLights()));
        lvLights.setSelector(android.R.color.transparent);
    }

    /**
     * Adapter for Light list
     * 
     * @author Manmath R
     */
    class LightListAdapter extends BaseAdapter {
        private List<PHLight> lights;
        private LayoutInflater mInflater;

        public LightListAdapter(ArrayList<PHLight> lights) {
            this.lights = lights;
            mInflater = LayoutInflater.from(PHGetLightsActivity.this);
        }

        @Override
        public int getCount() {
            return lights.size();
        }

        @Override
        public Object getItem(int arg0) {

            return lights.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

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

        class RowView {
            private TextView tvName;
            private TextView tvId;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e("check", "test ...");
        switch (item.getItemId()) {

        case android.R.id.home:
            Log.e("check", "test1 ...");
            Intent intent = new Intent(this, PHHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            break;
        }
        return true;
    }
}
