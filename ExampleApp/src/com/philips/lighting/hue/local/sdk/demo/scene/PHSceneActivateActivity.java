package com.philips.lighting.hue.local.sdk.demo.scene;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.philips.lighting.hue.listener.PHSceneListener;
import com.philips.lighting.hue.local.sdk.demo.PHHomeActivity;
import com.philips.lighting.hue.local.sdk.demo.PHWizardAlertDialog;
import com.philips.lighting.hue.local.sdk.demo.R;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHScene;

/**
 * Contains Demo for activating an existing scene in bridge.
 * 
 * @author Manmath R
 */
public class PHSceneActivateActivity extends Activity {

    private ListView listView;
    private PHBridge bridge;
    private List<PHScene> scenes;

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
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        PHHueSDK phHueSDK = PHHueSDK.getInstance();
        bridge = phHueSDK.getSelectedBridge();
        scenes = bridge.getResourceCache().getAllScenes();
        String[] arrIds = new String[scenes.size()];

        for (int i = 0; i < arrIds.length; i++) {
            arrIds[i] = scenes.get(i).getSceneIdentifier();
        }
        listView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice, arrIds));
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
        inflater.inflate(R.menu.activate, menu);
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
        case R.id.activate:
            activateScene();
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
     * Activates selected scene using Bridge API
     */
    private void activateScene() {
        long pos = listView.getCheckedItemPosition();
        if (pos == listView.INVALID_POSITION) {
            PHWizardAlertDialog.showErrorDialog(this, R.string.txt_empty_input);
            return;
        }
        String sceneId = scenes.get((int) pos).getSceneIdentifier();

        final PHWizardAlertDialog dialogManager = PHWizardAlertDialog
                .getInstance();
        dialogManager.showProgressDialog(R.string.sending_progress,
                PHSceneActivateActivity.this);
        // lets send it for all light : group "0"
        // bridge api call for activating scene
        bridge.activateScene(sceneId, "0", new PHSceneListener() {

            @Override
            public void onSuccess() {
                dialogManager.closeProgressDialog();
                PHWizardAlertDialog.showResultDialog(
                        PHSceneActivateActivity.this,
                        getString(R.string.txt_scene_activated),
                        R.string.btn_ok, R.string.txt_result);

            }

            @Override
            public void onStateUpdate(Hashtable<String, String> arg0,
                    List<PHHueError> arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(int code, String msg) {
                dialogManager.closeProgressDialog();
                PHWizardAlertDialog.showErrorDialog(
                        PHSceneActivateActivity.this, msg, R.string.btn_ok);

            }

            @Override
            public void onScenesReceived(List<PHScene> arg0) {
                // TODO Auto-generated method stub

            }
        });
    }
}
