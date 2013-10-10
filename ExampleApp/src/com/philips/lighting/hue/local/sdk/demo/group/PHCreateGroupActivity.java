package com.philips.lighting.hue.local.sdk.demo.group;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.philips.lighting.hue.listener.PHGroupListener;
import com.philips.lighting.hue.local.sdk.demo.PHHomeActivity;
import com.philips.lighting.hue.local.sdk.demo.PHWizardAlertDialog;
import com.philips.lighting.hue.local.sdk.demo.R;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHGroup;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;

/**
 * Contains Demo for create group API
 * 
 * @author Manmath R
 * 
 */
public class PHCreateGroupActivity extends Activity {

    private static final String TAG = "PHCreateGroupActivity";
    private ListView lightlistView;
    private EditText edtGroupName;
    private PHBridge bridge;
    private List<PHLight> lights;

    /**
     * Called when the activity is first created.
     * 
     * @param savedInstanceState
     *            the bundle object
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creategroup);

        edtGroupName = (EditText) findViewById(R.id.edtGroupName);
        lightlistView = (ListView) findViewById(R.id.lvLightsforGroup);
        lightlistView.setItemsCanFocus(false);
        lightlistView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        PHHueSDK phHueSDK = PHHueSDK.getInstance(getApplicationContext());
        bridge = phHueSDK.getSelectedBridge();
        lights = bridge.getResourceCache().getAllLights();

        String[] arrLightNames = new String[lights.size()];
        for (int i = 0; i < arrLightNames.length; i++) {
            arrLightNames[i] = lights.get(i).getName();
        }

        lightlistView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice,
                arrLightNames));
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
        inflater.inflate(R.menu.create_group, menu);
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
        case R.id.create_group:
            saveGroup();
            break;

        case android.R.id.home:
            Intent intent = new Intent(this, PHHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            break;

        default:
            break;
        }
        return true;
    }

    /**
     * Validate & Save create group to bridge.
     */
    private void saveGroup() {
        String groupName = edtGroupName.getText().toString();
        if (groupName.length() == 0) {
            PHWizardAlertDialog.showErrorDialog(PHCreateGroupActivity.this,
                    getResources().getString(R.string.txt_empty_input),
                    R.string.btn_ok);
            return;
        }
        SparseBooleanArray sparseBoolArray = lightlistView.getCheckedItemPositions();

        if (sparseBoolArray == null) {
            PHWizardAlertDialog.showErrorDialog(PHCreateGroupActivity.this,
                    getResources().getString(R.string.txt_no_lights_for_group),
                    R.string.btn_ok);
            return;
        }

        ArrayList<String> selectedlightIds = new ArrayList<String>();
        for (int i = 0; i < lights.size(); i++) {

            if (sparseBoolArray.get(i)) {
                selectedlightIds.add(lights.get(i).getIdentifier());

            }
        }
        if (selectedlightIds == null || selectedlightIds.size() <= 1) {
            PHWizardAlertDialog.showErrorDialog(PHCreateGroupActivity.this,
                    getResources().getString(R.string.txt_no_lights_for_group),
                    R.string.btn_ok);
            return;
        } else {
            String[] lightIdentifiers = selectedlightIds.toArray(new String[0]);

            final PHWizardAlertDialog dialogManager = PHWizardAlertDialog
                    .getInstance();
            dialogManager.showProgressDialog(R.string.sending_progress,
                    PHCreateGroupActivity.this);

            bridge.createGroup(groupName, lightIdentifiers,
                    new PHGroupListener() {

                        @Override
                        public void onError(int code, String msg) {
                            // TODO Auto-generated method stub
                            Log.v(TAG, "onError : " + code + " : " + msg);
                            dialogManager.closeProgressDialog();
                            PHWizardAlertDialog.showErrorDialog(
                                    PHCreateGroupActivity.this, msg,
                                    R.string.btn_ok);
                        }

                        @Override
                        public void onStateUpdate(
                                Hashtable<String, String> arg0,
                                List<PHHueError> arg1) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onSuccess() {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onCreated(PHGroup group) {
                            dialogManager.closeProgressDialog();
                            PHWizardAlertDialog.showResultDialog(
                                    PHCreateGroupActivity.this,
                                    getString(R.string.txt_group_created),
                                    R.string.btn_ok, R.string.txt_result);
                            return;
                        }

                    });
        }
    }

}
