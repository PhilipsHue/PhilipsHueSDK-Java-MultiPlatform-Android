package com.philips.lighting.hue.local.sdk.demo.light;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.local.sdk.demo.PHWizardAlertDialog;
import com.philips.lighting.hue.local.sdk.demo.R;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;

/**
 * Contains Demo for Find new lights manually by entering hex code of the lamp.
 * 
 * @author Manmath R
 * 
 */
public class PHFindNewLightsManualActivity extends Activity {

    private PHHueSDK phHueSDK;
    private Button btnSearch;
    private ListView listView;
    private ArrayList<String> serials = new ArrayList<String>();
    private ArrayList<PHBridgeResource> tempLightHeaders = new ArrayList<PHBridgeResource>();

    /**
     * Called when the activity is first created.
     * 
     * @param savedInstanceState
     *            the bundle object
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_new_lights_manual);

        phHueSDK = PHHueSDK.getInstance();
        Button btnAdd = (Button) findViewById(R.id.btn_add_light_serial);
        btnSearch = (Button) findViewById(R.id.button_start_searching);
        btnAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showEntryDialog();

            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startSearch();
            }
        });

        listView = (ListView) findViewById(R.id.lampsidlist);
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(new ArrayAdapter<String>(this,
                R.layout.findnewlamps_item, R.id.lamp_name, serials));

    }

    /**
     * Shows dialog to enter lamp's hex code
     */
    private void showEntryDialog() {

        AlertDialog.Builder popup = new AlertDialog.Builder(this);
        popup.setTitle(R.string.popup_add_serial);
        final EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setFocusable(true);
        input.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        popup.setView(input);
        popup.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String data = input.getText().toString();
                if (data == null || data.length() != 6) {
                    PHWizardAlertDialog.showErrorDialog(
                            PHFindNewLightsManualActivity.this, getResources()
                                    .getString(R.string.txt_serial_not_valid),
                            R.string.btn_ok);
                    return;
                }
                serials.add(data);
                listView.invalidate();
                dialog.dismiss();
                btnSearch.setEnabled(true);
            }
        });

        popup.show();
    }

    /**
     * starts search for new lamps
     */
    private void startSearch() {

        PHBridge bridge = phHueSDK.getSelectedBridge();
        final PHWizardAlertDialog dialogManager = PHWizardAlertDialog
                .getInstance();
        dialogManager.showProgressDialog(R.string.sending_progress, this);
        bridge.findNewLightsWithSerials(serials, new PHLightListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub

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
                        PHFindNewLightsManualActivity.this, msg,
                        R.string.btn_ok);
            }

            @Override
            public void onReceivingLights(
                    List<PHBridgeResource> lightHeaders) {

                if (lightHeaders != null && lightHeaders.size() > 0 && !tempLightHeaders.containsAll(lightHeaders)) {
                        tempLightHeaders.addAll(lightHeaders);
                }

            }

            @Override
            public void onSearchComplete() {
                dialogManager.closeProgressDialog();
                showLights(tempLightHeaders);
            }
        });

    }

    /**
     * show lights in a list after hex coded are entered
     * 
     * @param lightHeaders
     *            the array list of {@link PHBridgeResource}
     */
    private void showLights(ArrayList<PHBridgeResource> lightHeaders) {
        ArrayList<String> names = new ArrayList<String>(lightHeaders.size());
        for (PHBridgeResource header : lightHeaders) {
            names.add(header.getName());
        }
        AlertDialog.Builder hueAlerts = new AlertDialog.Builder(this);
        hueAlerts.setTitle(R.string.title_lights);
        hueAlerts.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, names), null);
        hueAlerts.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        hueAlerts.setCancelable(false);
        hueAlerts.show();
    }

}
