package com.philips.lighting.hue.local.sdk.demo.light;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.philips.lighting.hue.local.sdk.demo.PHHomeActivity;
import com.philips.lighting.hue.local.sdk.demo.PHWizardAlertDialog;
import com.philips.lighting.hue.local.sdk.demo.R;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHLight;

/**
 * Lists all Light features found in SDK
 * 
 * @author Manmath R
 */
public class PHLightFeaturesActivity extends Activity implements
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
        setContentView(R.layout.item_list);

        ListView lightFeatureListView = (ListView) findViewById(R.id.list_items);
        lightFeatureListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lightFeatureListView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getResources()
                        .getStringArray(R.array.light_features)));

        lightFeatureListView.setOnItemClickListener(this);
    }

    /**
     * 
     * Callback method to be invoked when an item in this AdapterView has been
     * clicked.
     * 
     * @param arg0
     *            The AdapterView where the click happened.
     * 
     * @param arg1
     *            The view within the AdapterView that was clicked
     * 
     * @param index
     *            The position of the view in the adapter.
     * 
     * @param arg3
     *            The row id of the item that was clicked.
     */

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
        Intent intent = null;
        PHHueSDK phHueSDK = PHHueSDK.getInstance(getApplicationContext());
        ArrayList<PHLight> lights = phHueSDK.getSelectedBridge()
                .getResourceCache().getAllLights();
        switch (index) {
        case 0:
            if (lights.size() == 0) {
                PHWizardAlertDialog.showErrorDialog(this,
                        R.string.txt_no_lights_found);
                return;
            }
            intent = new Intent(this, PHGetLightsActivity.class);
            break;
        case 1:
            intent = new Intent(this, PHFindNewLightsActivity.class);
            break;
        case 2:
            intent = new Intent(this, PHFindNewLightsManualActivity.class);
            break;
        case 3:
            if (lights.size() == 0) {
                PHWizardAlertDialog.showErrorDialog(this,
                        R.string.txt_no_lights_found);
                return;
            }
            intent = new Intent(this, PHUpdateLightStateActivity.class);
            boolean isLightAPICall = true;
            intent.putExtra("Api_Call", isLightAPICall);
            break;
        case 4:
            if (lights.size() == 0) {
                PHWizardAlertDialog.showErrorDialog(this,
                        R.string.txt_no_lights_found);
                return;
            }
            intent = new Intent(this, PHRenameLightListActivity.class);
            break;
        default:
            break;
        }
        if (intent != null) {
            startActivity(intent);
        }
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
