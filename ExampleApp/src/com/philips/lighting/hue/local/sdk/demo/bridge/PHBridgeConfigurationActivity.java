package com.philips.lighting.hue.local.sdk.demo.bridge;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.philips.lighting.hue.listener.PHBridgeConfigurationListener;
import com.philips.lighting.hue.local.sdk.demo.PHHomeActivity;
import com.philips.lighting.hue.local.sdk.demo.PHWizardAlertDialog;
import com.philips.lighting.hue.local.sdk.demo.R;
import com.philips.lighting.hue.local.sdk.demo.schedule.PHCreateNonRecurringScheduleActivity;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.wizard.helper.PHBridgeToggleAdapter;
import com.philips.lighting.hue.sdk.wizard.helper.PHClearableEditText;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeConfiguration;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHSoftwareUpdateStatus.PHStateType;

/**
 * Activity to know various properties of Connected Bridge.
 * 
 * @author Pallavi P. Ganorkar.
 */
public class PHBridgeConfigurationActivity extends Activity {
    private static final String TAG = "PHBridgeDetailsActivity";
    private ListView listDHCPSettings = null;
    private ListView listHTTPSettings = null;
    private static TextView txtBridgeName = null;
    private ToggleButton tglHttp = null;
    private ToggleButton tglDhcp = null;
    private LinearLayout dividerDhcp;
    private LinearLayout dividerHttp;
    private PHBridgeConfiguration bridgeConfig;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuration);
        phHueSDK = PHHueSDK.getInstance();
        bridge = phHueSDK.getSelectedBridge();
        phHueSDK.disableHeartbeat(bridge);
        bridgeConfig = bridge.getResourceCache().getBridgeConfiguration();

        final TextView txtBridgeTitle = (TextView) findViewById(R.id.txt_title);
        if (txtBridgeTitle != null) {
            txtBridgeTitle.setText(R.string.txt_name);
        }

        txtBridgeName = (TextView) findViewById(R.id.txt_description);
        if (txtBridgeName != null) {
            txtBridgeName.setText(bridgeConfig.getName());
        }

        TextView txtBridgeVersion = (TextView) findViewById(R.id.tvVersion);
        if (txtBridgeVersion != null) {
            txtBridgeVersion.setText("Version \n"
                    + bridgeConfig.getSoftwareVersion());
        }

        dividerDhcp = (LinearLayout) findViewById(R.id.divider_line_dhcp);
        dividerHttp = (LinearLayout) findViewById(R.id.divider_line_http_proxy);

        txtBridgeName = (TextView) findViewById(R.id.txt_description);

        tglDhcp = (ToggleButton) findViewById(R.id.tgleBtn_Dhcp);
        listDHCPSettings = (ListView) findViewById(R.id.listdhcp);
        listDHCPSettings.setOnItemClickListener(mOnListItemClickListener);
        listHTTPSettings = (ListView) findViewById(R.id.listhttp);
        listHTTPSettings.setOnItemClickListener(mOnListItemClickListener);

        if (tglDhcp != null) {

            if (bridgeConfig.getDhcpEnabled()) {
                tglDhcp.setChecked(true);

                if (listDHCPSettings != null) {
                    listDHCPSettings.setVisibility(View.GONE);
                    dividerDhcp.setVisibility(View.GONE);
                }
            } else {
                tglDhcp.setChecked(false);

                if (listDHCPSettings != null) {
                    listDHCPSettings.setVisibility(View.VISIBLE);
                    dividerDhcp.setVisibility(View.VISIBLE);
                    setdhcplistAdapter(listDHCPSettings);
                }

            }
            tglDhcp.setOnClickListener(mOnToggleClickListener);
        }

        tglHttp = (ToggleButton) findViewById(R.id.tgleBtn_http);

        if (tglHttp != null) {
            String proxy = bridgeConfig.getProxy();
            if (proxy != null
                    && (proxy.trim().length() == 0 || proxy
                            .equalsIgnoreCase("none"))) {
                tglHttp.setChecked(false);

                if (listHTTPSettings != null) {
                    listHTTPSettings.setVisibility(View.GONE);
                    dividerHttp.setVisibility(View.GONE);
                }
            } else {
                tglHttp.setChecked(true);
                if (listHTTPSettings != null) {
                    listHTTPSettings.setVisibility(View.VISIBLE);
                    dividerHttp.setVisibility(View.VISIBLE);
                    setHttpListAdapter(listHTTPSettings);
                }
            }
            tglHttp.setOnClickListener(mOnToggleClickListener);
        }

        final TextView txtUpdate = (TextView) findViewById(R.id.softwraeupdate);
        if (txtUpdate != null) {
            txtUpdate.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    PHWizardAlertDialog dialogManager = PHWizardAlertDialog
                            .getInstance();
                    dialogManager.showProgressDialog(R.string.txt_pleasewait,
                            PHBridgeConfigurationActivity.this);
                    bridge.getBridgeConfigurations(configListener);
                }
            });
        }

        final LinearLayout bridgeNameLayout = (LinearLayout) findViewById(R.id.bridge_name_layout);
        if (bridgeNameLayout != null) {
            bridgeNameLayout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    showBridgeRenameDialog(PHBridgeConfigurationActivity.this);

                }
            });
        }

    }

    /**
     * The bridge configuration listener used for bridge configuration related
     * API
     */
    private PHBridgeConfigurationListener configListener = new PHBridgeConfigurationListener() {

        /**
         * Called to convey success without any data from bridge
         */
        @Override
        public void onSuccess() {
            Log.v(TAG, "onSuccess");
            PHWizardAlertDialog dialogManager = PHWizardAlertDialog
                    .getInstance();
            dialogManager.closeProgressDialog();
            PHWizardAlertDialog.showResultDialog(
                    PHBridgeConfigurationActivity.this, getResources()
                            .getString(R.string.txt_sw_update_success),
                    R.string.txt_button_ok, R.string.txt_sw_success);
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
            Log.v(TAG, "onStateUpdate");
        }

        /**
         * Callback for receiving bridge configurations
         * 
         * @param newConfig
         *            the {@link PHBridgeConfiguration} object
         */
        @Override
        public void onReceivingConfiguration(PHBridgeConfiguration newConfig) {
            Log.v(TAG, "onReceivingConfiguration");
            PHWizardAlertDialog dialogManager = PHWizardAlertDialog
                    .getInstance();
            boolean isUpdateAvailable = newConfig.getSoftwareStatus()
                    .isSoftwareUpdateAvailable();
            dialogManager.closeProgressDialog();
            if (isUpdateAvailable) {

                showBridgeSoftwareUpdateDialog(PHBridgeConfigurationActivity.this);
            } else {
                if (bridgeConfig.getSoftwareStatus().getState() == PHStateType.NO_UPDATE) {

                    PHWizardAlertDialog
                            .showMessageDialog(
                                    getResources()
                                            .getString(
                                                    R.string.txt_sw_update_not_available),
                                    R.string.txt_sw,
                                    PHBridgeConfigurationActivity.this);
                } else if (bridgeConfig.getSoftwareStatus().getState() == PHStateType.UPDATE_DOWNLOADING) {

                    PHWizardAlertDialog
                            .showMessageDialog(
                                    getResources().getString(
                                            R.string.txt_sw_update_downloading),
                                    R.string.txt_sw,
                                    PHBridgeConfigurationActivity.this);
                }
            }
        }

        /**
         * The callback method for error
         * 
         * @code the error code
         * @message the error message
         */
        @Override
        public void onError(int code, String msg) {
            Log.e(TAG, "onError called:" + code + " Message:" + msg);
            PHWizardAlertDialog dialogManager = PHWizardAlertDialog
                    .getInstance();
            dialogManager.closeProgressDialog();
            if (code == PHHueError.SOFTWARE_UPDATE_NOT_AVAILABLE) {
                PHWizardAlertDialog.showMessageDialog(msg, R.string.txt_sw,
                        PHBridgeConfigurationActivity.this);
            } else if (code == PHHueError.SOFTWARE_UPDATE_DOWNLOADING) {
                PHWizardAlertDialog.showMessageDialog(msg, R.string.txt_sw,
                        PHBridgeConfigurationActivity.this);
            } else {
                PHWizardAlertDialog.showErrorDialog(
                        PHBridgeConfigurationActivity.this, msg,
                        R.string.txt_button_ok);
            }
        }
    };
    /**
     * List item click listener
     */
    private OnItemClickListener mOnListItemClickListener = new OnItemClickListener() {
        /**
         * Callback method to be invoked when an item in this AdapterView has
         * been clicked.
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
        public void onItemClick(AdapterView<?> listView, View view,
                int position, long arg3) {
            // TODO Auto-generated method stub
            final PHBridgeToggleAdapter bridgeAdapter = (PHBridgeToggleAdapter) listView
                    .getAdapter();

            bridgeAdapter.setOnClick(view, position);
        }
    };

    /**
     * Sets list adapter for DHCP list of options
     * 
     * @param listDHCPSettings
     *            the list view
     */
    private void setdhcplistAdapter(ListView listDHCPSettings) {
        String[] arrDHCPSettings = {
                getResources().getString(R.string.txt_IPaddress),
                getResources().getString(R.string.txt_Netmask),
                getResources().getString(R.string.txt_Gateway) };

        String[] arrDHCPValues = { bridgeConfig.getIpAddress(),
                bridgeConfig.getNetmask(), bridgeConfig.getGateway() };

        final PHBridgeToggleAdapter bridgeDhcpAdapter = new PHBridgeToggleAdapter(
                this, R.layout.mybridge_option_row, arrDHCPValues,
                arrDHCPSettings);
        listDHCPSettings.setAdapter(bridgeDhcpAdapter);

        setListViewHeightBasedOnChildren(listDHCPSettings);
    }

    /**
     * Used to measure and update the given list view
     * 
     * @param listView
     *            the list view object
     */
    private void setListViewHeightBasedOnChildren(ListView listView) {
        final ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {
            int totalHeight = 0;
            for (int nIndex = 0; nIndex < listAdapter.getCount(); nIndex++) {
                final View listItem = listAdapter.getView(nIndex, null,
                        listView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }

            final ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight
                    + (listView.getDividerHeight() * (listAdapter.getCount() + 1));
            listView.setLayoutParams(params);
        }
    }

    /**
     * Toggle button listener
     */
    private OnClickListener mOnToggleClickListener = new ToggleButton.OnClickListener() {
        /**
         * Called when button is clicked
         */
        @Override
        public void onClick(View v) {
            ToggleButton tgbtn = (ToggleButton) v;
            if (tgbtn.getId() == R.id.tgleBtn_Dhcp) {

                if (tgbtn.isChecked()) {

                    if (!bridgeConfig.getDhcpEnabled()) {

                    }
                    if (listDHCPSettings != null) {
                        listDHCPSettings.setVisibility(View.GONE);
                        dividerDhcp.setVisibility(View.GONE);
                    }
                } else {
                    if (listDHCPSettings != null) {
                        listDHCPSettings.setVisibility(View.VISIBLE);
                        dividerDhcp.setVisibility(View.VISIBLE);
                        setdhcplistAdapter(listDHCPSettings);
                    }
                }
            } else if (tgbtn.getId() == R.id.tgleBtn_http) {
                if (tgbtn.isChecked()) {
                    if (listHTTPSettings != null) {
                        listHTTPSettings.setVisibility(View.VISIBLE);
                        dividerHttp.setVisibility(View.VISIBLE);
                        setHttpListAdapter(listHTTPSettings);
                    }
                } else {

                    if (listHTTPSettings != null) {
                        listHTTPSettings.setVisibility(View.GONE);
                        dividerHttp.setVisibility(View.GONE);
                        setHttpListAdapter(listHTTPSettings);
                    }
                }
            }
        }
    };

    /**
     * Sets list adapter for HTTP list of options
     * 
     * @param listHTTPSettings
     *            the list view
     */
    private void setHttpListAdapter(ListView listHTTPSettings) {

        String[] arrHttpSettings = {
                getResources().getString(R.string.txt_server),
                getResources().getString(R.string.txt_port) };
        String[] arrHttpValues = { bridgeConfig.getProxy(),
                Integer.toString(bridgeConfig.getProxyPort()) };

        final PHBridgeToggleAdapter bridgeHttpAdapter = new PHBridgeToggleAdapter(
                this, R.layout.mybridge_option_row, arrHttpValues,
                arrHttpSettings);
        listHTTPSettings.setAdapter(bridgeHttpAdapter);

        setListViewHeightBasedOnChildren(listHTTPSettings);
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
        inflater.inflate(R.menu.save, menu);
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
        case R.id.save:
            saveData();
            break;
        case android.R.id.home:
            Intent intent = new Intent(this, PHHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            break;
        }
        return true;
    }

    /**
     * Saves bridge configurations using bridge API
     */
    private void saveData() {
        phHueSDK.disableHeartbeat(bridge);
        final PHBridgeConfiguration config = new PHBridgeConfiguration();
        config.setName(txtBridgeName.getText().toString());
        config.setDhcpEnabled(tglDhcp.isChecked());
        if (!tglDhcp.isChecked()) {
            PHBridgeToggleAdapter adapter = (PHBridgeToggleAdapter) listDHCPSettings
                    .getAdapter();
            String str = "";
            str = adapter.getItem(0).toString().trim();

            if (str == null || str.length() == 0 || !verifyText(str)) {
                PHWizardAlertDialog.showErrorDialog(this,
                        R.string.error_ip_address);
                return;
            }
            config.setIpAddress(str);
            str = "";
            str = adapter.getItem(1).toString().trim();
            if (str == null || str.length() == 0 || !verifyText(str)) {
                PHWizardAlertDialog.showErrorDialog(this,
                        R.string.error_ip_netmask);
                return;
            }
            config.setNetmask(str);
            str = "";
            str = adapter.getItem(2).toString().trim();
            if (str == null || str.length() == 0 || !verifyText(str)) {
                PHWizardAlertDialog.showErrorDialog(this,
                        R.string.error_ip_gateway);
                return;
            }

            config.setGateway(str);
        }
        if (tglHttp.isChecked()) {
            PHBridgeToggleAdapter adapter = (PHBridgeToggleAdapter) listHTTPSettings
                    .getAdapter();
            String str = adapter.getItem(0).toString().trim();
            if (str == null || str.length() == 0) {
                PHWizardAlertDialog
                        .showErrorDialog(this, R.string.error_config);
                return;
            }
            config.setProxy(str);
            str = "";

            str = adapter.getItem(1).toString().trim();
            if (str == null || str.length() == 0) {
                PHWizardAlertDialog
                        .showErrorDialog(this, R.string.error_config);
                return;
            }

            int proxyPort = 0;
            try {
                proxyPort = Integer.parseInt(str);
            } catch (NumberFormatException e) {
            }

            config.setProxyPort(proxyPort);

        } else if (!tglHttp.isChecked()) {
            // Proxy disabled. EMpty strings are a problem with config, so send
            // "none" instead of "".
            config.setProxy("none");
            config.setProxyPort(0);
        }

        final PHWizardAlertDialog dialogManager = PHWizardAlertDialog
                .getInstance();
        dialogManager.showProgressDialog(R.string.txt_saving, this);

        // calls bridge API to update bridge configuartions
        bridge.updateBridgeConfigurations(config,
                new PHBridgeConfigurationListener() {

                    @Override
                    public void onError(int code, final String msg) {

                        PHBridgeConfigurationActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                if (isCurrentActivity()) {
                                    PHWizardAlertDialog.showErrorDialog(PHBridgeConfigurationActivity.this, msg,R.string.btn_ok);
                                }
                            }
                          });
                        dialogManager.closeProgressDialog();
                        Log.v(TAG, "onError : " + code + " : " + msg);
                        
                    }

                    @Override
                    public void onStateUpdate( Hashtable<String, String> successResponse,List<PHHueError> errorResponse) {

                        StringBuffer sb = new StringBuffer();
                        sb.append("Success :  ");
                        for (String key : successResponse.keySet()) {
                            sb.append(key).append("\n\t\t\t\t");
                        }
                        sb.append("\n");
                        sb.append("Failures    \t  :  ");
                        for (int i = 0; i < errorResponse.size(); i++) {
                            PHHueError hueError = errorResponse.get(i);
                            sb.append(hueError.getAddress() + "("
                                    + hueError.getMessage() + ")"
                                    + "\n\t\t\t\t");
                        }
                        final String resultString = sb.toString();
                        dialogManager.closeProgressDialog();
                        Log.v(TAG, "successResponse : " + successResponse
                                + " errorResponse : " + errorResponse);
                        PHBridgeConfigurationActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                if (isCurrentActivity()) {
                                    PHWizardAlertDialog.showResultDialog( PHBridgeConfigurationActivity.this,resultString, R.string.btn_ok, R.string.txt_result);
                                }
                            }
                          });
                        
                    }

                    @Override
                    public void onSuccess() {
                        Log.e("onSuccess", "");
                    }
                });
    }

    /**
     * Validates ip address
     * 
     * @param text
     *            the ip address
     * @return true if valid ip address
     */
    private boolean verifyText(String text) {
        Pattern pattern;
        Matcher matcher;
        String ipaddressPattern = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        pattern = Pattern.compile(ipaddressPattern);
        matcher = pattern.matcher(text);

        if (matcher.matches()) {
            return true;
        }

        return false;

    }

    /**
     * Creates the change name dialog for light.
     */
    public void showBridgeRenameDialog(final Context activityContext) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityContext);

        builder.setTitle(R.string.txt_name);
        final PHClearableEditText editText = new PHClearableEditText(
                activityContext);
        editText.getEditText().setText(txtBridgeName.getText());
        editText.getEditText().requestFocus();
        editText.getEditText().setSelection(txtBridgeName.getText().length());

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(16);
        editText.setFilters(filterArray);

        builder.setView(editText)
                .setNegativeButton(R.string.btn_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                final String nameNew = editText.getEditText()
                                        .getText().toString().trim();
                                if (nameNew == null || nameNew.length() < 4
                                        || nameNew.length() > 16) {
                                    PHWizardAlertDialog.showErrorDialog(
                                            activityContext,
                                            R.string.error_bridge_name);
                                    return;
                                } else {
                                    // TODO set new name
                                    txtBridgeName.setText(nameNew);
                                }
                            }
                        })
                .setPositiveButton(R.string.btn_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

        AlertDialog alert = builder.create();
        alert.show();
        alert.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    /**
     * Creates the dialog for software update
     */
    public void showBridgeSoftwareUpdateDialog(final Context activityContext) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                activityContext);
        alertDialogBuilder.setTitle(R.string.txt_softwareupdate_available);

        alertDialogBuilder
                .setMessage(R.string.txt_softwareupdate_dialog)
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                PHWizardAlertDialog dialogManager = PHWizardAlertDialog
                                        .getInstance();
                                dialogManager.showProgressDialog(
                                        R.string.txt_pleasewait,
                                        PHBridgeConfigurationActivity.this);
                                bridge.updateSoftware(configListener);
                            }
                        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        phHueSDK.enableHeartbeat(bridge, PHHueSDK.HB_INTERVAL);
        super.onDestroy();
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
