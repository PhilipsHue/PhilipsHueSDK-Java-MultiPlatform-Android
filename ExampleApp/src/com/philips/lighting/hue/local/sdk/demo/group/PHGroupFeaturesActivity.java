package com.philips.lighting.hue.local.sdk.demo.group;

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
import com.philips.lighting.model.PHBridgeResourcesCache;
import com.philips.lighting.model.PHGroup;
import com.philips.lighting.model.PHLight;

/**
 * Lists all Group features found in SDK
 * 
 * @author Manmath R
 */
public class PHGroupFeaturesActivity extends Activity implements
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

        ListView groupFeatureListView = (ListView) findViewById(R.id.list_items);
        groupFeatureListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        groupFeatureListView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getResources()
                        .getStringArray(R.array.group_features)));
        groupFeatureListView.setOnItemClickListener(this);
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
        PHHueSDK phHueSDK = PHHueSDK.getInstance(getApplicationContext());
        PHBridgeResourcesCache cache = phHueSDK.getSelectedBridge()
                .getResourceCache();
        ArrayList<PHGroup> groups = cache.getAllGroups();
        ArrayList<PHLight> lights = cache.getAllLights();
        Intent intent = null;
        switch (index) {
        case 0:
            intent = new Intent(this, PHGetGroupsActivity.class);
            break;
        case 1:
            if (lights.size() < 2) {
                PHWizardAlertDialog.showErrorDialog(this,
                        R.string.txt_no_lights_found_for_group);
                return;
            }
            intent = new Intent(this, PHCreateGroupActivity.class);
            break;
        case 2:
            if (groups.size() == 0) {
                PHWizardAlertDialog.showErrorDialog(this,
                        R.string.txt_no_groups);
                return;
            }
            intent = new Intent(this, PHSetLightStateForGroupActivity.class);
            break;
        case 3:
            if (groups.size() == 0) {
                PHWizardAlertDialog.showErrorDialog(this,
                        R.string.txt_no_groups);
                return;
            }
            intent = new Intent(this, PHDeleteGroupActivity.class);
            break;
        case 4:
            if (groups.size() == 0) {
                PHWizardAlertDialog.showErrorDialog(this,
                        R.string.txt_no_groups);
                return;
            }
            intent = new Intent(this, PHRenameGroupActivity.class);
            break;
        default:
            break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

}
