package com.philips.lighting.hue.local.sdk.demo.bridge;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

import com.philips.lighting.hue.local.sdk.demo.R;
import com.philips.lighting.hue.local.sdk.demo.group.PHGroupFeaturesActivity;
import com.philips.lighting.hue.local.sdk.demo.light.PHLightFeaturesActivity;
import com.philips.lighting.hue.local.sdk.demo.scene.PHSceneFeaturesActivity;
import com.philips.lighting.hue.local.sdk.demo.schedule.PHScheduleFeatureActivity;
import com.philips.lighting.hue.sdk.PHHueSDK;

/**
 * This class lists down all the bridge properties.
 * 
 * @author Manmath R
 * 
 */
public class PHBridgeFeaturesActivity extends ListActivity implements
        OnItemClickListener {

    /**
     * Called when the activity is first created.
     * 
     * @param savedInstanceState
     *            the bundle object
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PHHueSDK phHueSDK = PHHueSDK.getInstance();
        setTitle(phHueSDK.getSelectedBridge().getResourceCache()
                .getBridgeConfiguration().getIpAddress());
        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getResources()
                        .getStringArray(R.array.bridge_features)));
        getListView().setOnItemClickListener(this);
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
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
            long arg3) {

        Intent intent = null;
        switch (position) {
        case 0:
            intent = new Intent(this, PHLightFeaturesActivity.class);
            break;
        case 1:
            intent = new Intent(this, PHGroupFeaturesActivity.class);
            break;
        case 2:
            intent = new Intent(this, PHBridgeConfigurationActivity.class);
            break;
        case 3:
            intent = new Intent(this, PHScheduleFeatureActivity.class);
            break;
        case 4:
            intent = new Intent(this, PHSceneFeaturesActivity.class);
            break;
        default:
            break;
        }
        if (intent != null) {
            startActivity(intent);
        }

    }
    @Override
    protected void onDestroy() {
        PHHueSDK phHueSDK = PHHueSDK.getInstance();
        if (phHueSDK.getSelectedBridge() != null) {
            phHueSDK.disableAllHeartbeat();
            phHueSDK.disconnect(phHueSDK.getSelectedBridge());
            super.onDestroy();
        }
    }
}
