package com.philips.lighting.hue.local.sdk.demo.scene;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
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

import com.philips.lighting.hue.listener.PHSceneListener;
import com.philips.lighting.hue.local.sdk.demo.PHHomeActivity;
import com.philips.lighting.hue.local.sdk.demo.PHWizardAlertDialog;
import com.philips.lighting.hue.local.sdk.demo.R;
import com.philips.lighting.hue.local.sdk.demo.group.PHCreateGroupActivity;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHScene;

/**
 * Contains Demo for create Scene API. Creation of scene takes time based on
 * number of lights available to bridge.
 * 
 * @author Manmath R
 */
public class PHCreateSceneActivity extends Activity {

    private static final String TAG = "PHCreateSceneActivity";
    private ListView lightlistView;
    private EditText edtSceneId;
    private EditText edtSceneName;
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
        setContentView(R.layout.createscene);

        edtSceneId = (EditText) findViewById(R.id.edtSceneId);
        edtSceneName = (EditText) findViewById(R.id.edtSceneName);
        lightlistView = (ListView) findViewById(R.id.lvLightsforScene);

        lightlistView.setItemsCanFocus(false);
        lightlistView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        PHHueSDK phHueSDK = PHHueSDK.getInstance();
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
        inflater.inflate(R.menu.create_scene, menu);
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
        case R.id.create_scene:
            saveScene();
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
     * Validate input & create Scene using bridge API
     */
    private void saveScene() {
        String sceneId = edtSceneId.getText().toString().trim();
        if (sceneId.length() == 0) {
            PHWizardAlertDialog.showErrorDialog(PHCreateSceneActivity.this,
                    getResources().getString(R.string.txt_empty_input),
                    R.string.btn_ok);
            return;
        }
        String sceneName = edtSceneName.getText().toString().trim();
        if (sceneName.length() == 0) {
            PHWizardAlertDialog.showErrorDialog(PHCreateSceneActivity.this,
                    getResources().getString(R.string.txt_empty_input),
                    R.string.btn_ok);
            return;
        }
        SparseBooleanArray sparseBoolArray = lightlistView.getCheckedItemPositions();

        if (sparseBoolArray == null || sparseBoolArray.size() == 0) {
            PHWizardAlertDialog.showErrorDialog(PHCreateSceneActivity.this,
                    getResources().getString(R.string.txt_no_lights_for_scene),
                    R.string.btn_ok);
            return;
        }

        ArrayList<String> selectedlightIds = new ArrayList<String>();
        for (int i = 0; i < lights.size(); i++) {

            if (sparseBoolArray.get(i)) {
                selectedlightIds.add(lights.get(i).getIdentifier());

            }
        }
        String[] lightIdentifiers = selectedlightIds.toArray(new String[0]);

        final PHWizardAlertDialog dialogManager = PHWizardAlertDialog
                .getInstance();
        dialogManager.showProgressDialog(R.string.sending_progress,
                PHCreateSceneActivity.this);

        PHScene scene = new PHScene();
        scene.setName(sceneName);
        scene.setLightIdentifiers(lightIdentifiers);
        scene.setSceneIdentifier(sceneId);
        // Bridge API called to create a scene
        bridge.saveScene(scene, new PHSceneListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStateUpdate(Hashtable<String, String> arg0,
                    List<PHHueError> arg1) {
                dialogManager.closeProgressDialog();
                PHWizardAlertDialog.showResultDialog(
                        PHCreateSceneActivity.this,
                        getString(R.string.txt_scene_created), R.string.btn_ok,
                        R.string.txt_result);
            }

            @Override
            public void onError(int code, final String msg) {
                Log.v(TAG, "onError : " + code + " : " + msg);
                dialogManager.closeProgressDialog();
                PHCreateSceneActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        if (isCurrentActivity()) {
                            PHWizardAlertDialog.showErrorDialog(PHCreateSceneActivity.this, msg, R.string.btn_ok);
                        }
                    }
                  });
               
            }

            @Override
            public void onScenesReceived(List<PHScene> arg0) {
                // TODO Auto-generated method stub

            }
        });
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
