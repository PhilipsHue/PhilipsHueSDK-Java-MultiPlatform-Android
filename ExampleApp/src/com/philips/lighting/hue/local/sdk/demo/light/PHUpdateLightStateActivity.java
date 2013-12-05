package com.philips.lighting.hue.local.sdk.demo.light;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.local.sdk.demo.PHHomeActivity;
import com.philips.lighting.hue.local.sdk.demo.PHWizardAlertDialog;
import com.philips.lighting.hue.local.sdk.demo.R;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLight.PHLightAlertMode;
import com.philips.lighting.model.PHLight.PHLightEffectMode;
import com.philips.lighting.model.PHLightState;

/**
 * Contains Demo for changing state of selected light using update light state
 * API
 * 
 * @author Manmath R
 */

public class PHUpdateLightStateActivity extends Activity {
    private String lightArray[];
    private List<PHLight> lights;
    private boolean[] selectedVales = new boolean[8];
    private ToggleButton tbOnOff;
    private ToggleButton tbGamutCorrection;
    private Spinner lightSpinner;
    private SeekBar sbHue;
    private SeekBar sbSaturation;
    private SeekBar sbBrightness;
    private SeekBar sbXValue;
    private SeekBar sbYValue;
    private SeekBar sbTTime;
    private TextView tvHue;
    private TextView tvSaturation;
    private TextView tvBrightness;
    private TextView tvX;
    private TextView tvY;
    private TextView tvTTime;
    private ToggleButton btnNonAlert;
    private ToggleButton btnSelectAlert;
    private ToggleButton btnLSelectAlert;
    private ToggleButton btnNoneEffect;
    private ToggleButton btnColorLoop;
    private float[] xy = new float[2];
    private PHLightAlertMode alertMode;
    private PHLightEffectMode effectMode;
    private PHHueSDK phHueSDK;
    private PHBridge bridge;
    private boolean apiCall;
    private CheckBox cbOnOFF, cbHue, cbSaturation, cbBrightness, cbXY,
            cbEffect, cbAlert, cbTTime;

    /**
     * Called when the activity is first created.
     * 
     * @param savedInstanceState
     *            the bundle object
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.light_state);
        apiCall = getIntent().getBooleanExtra("Api_Call", false);

        phHueSDK = PHHueSDK.getInstance();
        bridge = phHueSDK.getSelectedBridge();
        lights = bridge.getResourceCache().getAllLights();
        getLightNames();

        TextView tvLight = (TextView) findViewById(R.id.tvLight);
        lightSpinner = (Spinner) findViewById(R.id.lightSpinner);
        ArrayAdapter<String> lightSpinnerAdapter = new ArrayAdapter<String>(
                this, R.layout.light_spinner_item, lightArray);
        lightSpinner.setAdapter(lightSpinnerAdapter);
        if (!apiCall) {
            tvLight.setVisibility(View.GONE);
            lightSpinner.setVisibility(View.GONE);
        }
        initCheckboxes();
        initColorBand();
        initOptionTabs();
        initStausTexts();
        initOtherComponents();
        // light selection listener
        lightSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
                clearLastKnownLightState();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // clearLastKnownLightState();
            }

        });

    }

    /**
     * Clears light state data from UI
     */
    private void clearLastKnownLightState() {
        cbOnOFF.setChecked(false);
        tbOnOff.setChecked(false);

        cbHue.setChecked(false);
        sbHue.setProgress(0);
        tvHue.setText("" + 0);

        cbSaturation.setChecked(false);
        sbSaturation.setProgress(0);
        tvHue.setText("" + 0);

        cbBrightness.setChecked(false);
        sbBrightness.setProgress(0);
        tvBrightness.setText("" + 0);

        cbXY.setChecked(false);
        sbXValue.setProgress(0);
        tvX.setText("" + 0);
        sbYValue.setProgress(0);
        tvY.setText("" + 0);
        xy[0] = 0;
        xy[1] = 0;

        cbEffect.setChecked(false);
        btnNoneEffect.setChecked(false);
        btnColorLoop.setChecked(false);

        cbAlert.setChecked(false);
        btnNonAlert.setChecked(false);
        btnSelectAlert.setChecked(false);
        btnLSelectAlert.setChecked(false);

        cbTTime.setChecked(false);
        sbTTime.setProgress(0);
        tvTTime.setText("" + 0);
    }

    /**
     * Displays light state read from cache
     * 
     * @param lastKnownLightState
     *            the {@link PHLightState} object
     */
    private void displayLastKnowLightState(PHLightState lastKnownLightState) {
        clearLastKnownLightState();
        Boolean onOff = lastKnownLightState.isOn();
        if (onOff != null) {
            cbOnOFF.setChecked(true);
            tbOnOff.setChecked(onOff);
        }

        int hue = lastKnownLightState.getHue();
        if (hue > 0) {
            cbHue.setChecked(true);
            sbHue.setProgress(hue);
            tvHue.setText("" + hue);
        }

        int sat = lastKnownLightState.getSaturation();
        if (sat > 0) {
            cbSaturation.setChecked(true);
            sbSaturation.setProgress(sat);
            tvHue.setText("" + sat);
        }
        int bri = lastKnownLightState.getBrightness();
        if (bri > 0) {
            cbBrightness.setChecked(true);
            sbBrightness.setProgress(bri);
            tvBrightness.setText("" + bri);
        }
        float x = lastKnownLightState.getX();
        float y = lastKnownLightState.getY();
        if (x > 0.0 || y > 0.0) {
            cbXY.setChecked(true);
            sbXValue.setProgress((int) (x * 10));
            tvX.setText("" + x);
            sbYValue.setProgress((int) (y * 10));
            tvY.setText("" + y);
            xy[0] = x;
            xy[1] = y;
        }
        PHLightEffectMode lastEffectMode = lastKnownLightState.getEffectMode();
        if (lastEffectMode != null) {
            cbEffect.setChecked(true);
            if (lastEffectMode == PHLightEffectMode.EFFECT_NONE) {
                btnNoneEffect.setChecked(true);
                btnColorLoop.setChecked(false);
            } else if (lastEffectMode == PHLightEffectMode.EFFECT_COLORLOOP) {
                btnNoneEffect.setChecked(false);
                btnColorLoop.setChecked(true);
            }
        }

        PHLightAlertMode lastAlertMode = lastKnownLightState.getAlertMode();
        if (lastAlertMode != null) {
            cbAlert.setChecked(true);
            if (lastAlertMode == PHLightAlertMode.ALERT_NONE) {
                btnNonAlert.setChecked(true);
                btnSelectAlert.setChecked(false);
                btnLSelectAlert.setChecked(false);
            } else if (lastAlertMode == PHLightAlertMode.ALERT_SELECT) {
                btnNonAlert.setChecked(false);
                btnSelectAlert.setChecked(true);
                btnLSelectAlert.setChecked(false);
            } else if (lastAlertMode == PHLightAlertMode.ALERT_LSELECT) {
                btnNonAlert.setChecked(false);
                btnSelectAlert.setChecked(false);
                btnLSelectAlert.setChecked(true);
            }
        }
        int transitionTime = lastKnownLightState.getTransitionTime();
        if (transitionTime > 0) {
            cbTTime.setChecked(true);
            sbTTime.setProgress(transitionTime);
            tvTTime.setText("" + transitionTime);
        }
    }

    /**
     * Reads light names and stores in common container
     */
    private void getLightNames() {
        int size = lights.size();
        lightArray = new String[size];
        for (int i = 0; i < size; i++) {
            lightArray[i] = lights.get(i).getName();
        }
    }

    /**
     * initialize check boxes from View
     */
    private void initCheckboxes() {
        cbOnOFF = (CheckBox) findViewById(R.id.checkBoxOnOFF);
        cbHue = (CheckBox) findViewById(R.id.checkBoxHue);
        cbSaturation = (CheckBox) findViewById(R.id.checkBoxSat);
        cbBrightness = (CheckBox) findViewById(R.id.checkBoxBri);
        cbXY = (CheckBox) findViewById(R.id.checkBoxXY);
        cbEffect = (CheckBox) findViewById(R.id.checkBoxEffect);
        cbAlert = (CheckBox) findViewById(R.id.checkBoxAlert);
        cbTTime = (CheckBox) findViewById(R.id.checkBoxTTime);
        cbOnOFF.setOnCheckedChangeListener(cbListener);
        cbHue.setOnCheckedChangeListener(cbListener);
        cbSaturation.setOnCheckedChangeListener(cbListener);
        cbBrightness.setOnCheckedChangeListener(cbListener);
        cbXY.setOnCheckedChangeListener(cbListener);
        cbEffect.setOnCheckedChangeListener(cbListener);
        cbAlert.setOnCheckedChangeListener(cbListener);
        cbEffect.setOnCheckedChangeListener(cbListener);
        cbTTime.setOnCheckedChangeListener(cbListener);
    }

    /**
     * initialize UI components from View
     */
    private void initOtherComponents() {
        tbOnOff = (ToggleButton) findViewById(R.id.tbOnOff);
        tbGamutCorrection = (ToggleButton) findViewById(R.id.tbGamutCorrection);
        sbHue = (SeekBar) findViewById(R.id.sbHue);
        sbSaturation = (SeekBar) findViewById(R.id.sbSat);
        sbBrightness = (SeekBar) findViewById(R.id.sbBri);
        sbXValue = (SeekBar) findViewById(R.id.sbX);
        sbYValue = (SeekBar) findViewById(R.id.sbY);
        sbTTime = (SeekBar) findViewById(R.id.sbTTime);
        // Based on light type, choose values
        sbHue.setMax(65535);
        sbSaturation.setMax(255);
        sbBrightness.setMax(255);
        // scaled to 10 times to neutralize float
        sbXValue.setMax(10);
        sbYValue.setMax(10);
        sbTTime.setMax(100);
        sbXValue.setOnSeekBarChangeListener(sbListener);
        sbYValue.setOnSeekBarChangeListener(sbListener);
        sbSaturation.setOnSeekBarChangeListener(sbListener);
        sbBrightness.setOnSeekBarChangeListener(sbListener);
        sbHue.setOnSeekBarChangeListener(sbListener);
        sbTTime.setOnSeekBarChangeListener(sbListener);
    }

    /**
     * initialize color band UI from View
     */
    private void initColorBand() {
        View vRed = findViewById(R.id.vRed);
        View vBlue = findViewById(R.id.vBlue);
        View vGreen = findViewById(R.id.vGreen);
        View vYellow = findViewById(R.id.vYellow);
        View vViolet = findViewById(R.id.vViolet);
        View vOrange = findViewById(R.id.vOrange);
        vRed.setOnClickListener(colorListener);
        vBlue.setOnClickListener(colorListener);
        vGreen.setOnClickListener(colorListener);
        vYellow.setOnClickListener(colorListener);
        vViolet.setOnClickListener(colorListener);
        vOrange.setOnClickListener(colorListener);
    }

    /**
     * initialize buttons from View
     */
    private void initOptionTabs() {
        btnNonAlert = (ToggleButton) findViewById(R.id.btnNoneAlert);
        btnSelectAlert = (ToggleButton) findViewById(R.id.btnSelectAlert);
        btnLSelectAlert = (ToggleButton) findViewById(R.id.btnLSelectAlert);
        btnSelectAlert.setOnClickListener(btnListener);
        btnNonAlert.setOnClickListener(btnListener);
        btnLSelectAlert.setOnClickListener(btnListener);
        btnNoneEffect = (ToggleButton) findViewById(R.id.btnNoneEffect);
        btnColorLoop = (ToggleButton) findViewById(R.id.btnColorLoop);
        btnNoneEffect.setOnClickListener(btnListener);
        btnColorLoop.setOnClickListener(btnListener);
    }

    /**
     * initialize textviews from View
     */
    private void initStausTexts() {
        tvHue = (TextView) findViewById(R.id.tvsbHue);
        tvSaturation = (TextView) findViewById(R.id.tvsbSat);
        tvBrightness = (TextView) findViewById(R.id.tvsbBri);
        tvX = (TextView) findViewById(R.id.tvsbX);
        tvY = (TextView) findViewById(R.id.tvsbY);
        tvTTime = (TextView) findViewById(R.id.tvsbTTime);
    }

    /**
     * get the light state values selected for checked options using listener
     */
    private OnCheckedChangeListener cbListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                boolean isChecked) {
            int id = buttonView.getId();
            switch (id) {
            case R.id.checkBoxOnOFF:
                selectedVales[0] = isChecked;
                break;
            case R.id.checkBoxHue:
                selectedVales[1] = isChecked;
                break;
            case R.id.checkBoxSat:
                selectedVales[2] = isChecked;
                break;
            case R.id.checkBoxBri:
                selectedVales[3] = isChecked;
                break;
            case R.id.checkBoxXY:
                selectedVales[4] = isChecked;
                break;
            case R.id.checkBoxEffect:
                selectedVales[5] = isChecked;
                break;
            case R.id.checkBoxAlert:
                selectedVales[6] = isChecked;
                break;
            case R.id.checkBoxTTime:
                selectedVales[7] = isChecked;
                break;
            default:
                break;
            }
        }
    };
    /**
     * Color bar listener
     */
    private OnClickListener colorListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            int colorCode = 0;
            switch (id) {
            case R.id.vRed:
                colorCode = Color.RED;
                break;
            case R.id.vBlue:
                colorCode = Color.BLUE;
                break;
            case R.id.vGreen:
                colorCode = Color.GREEN;
                break;
            case R.id.vYellow:
                colorCode = Color.YELLOW;
                break;
            case R.id.vViolet:
                colorCode = Color.rgb(128, 0, 128);
                break;
            case R.id.vOrange:
                colorCode = Color.rgb(255, 165, 0);
                break;
            }
            int pos = lightSpinner.getSelectedItemPosition();
            PHLight light = lights.get(pos);
            if (tbGamutCorrection.isChecked()) {
                xy = PHUtilities.calculateXY(colorCode, light.getModelNumber());
            } else {
                xy = PHUtilities.calculateXY(colorCode, " ");
            }

            sbXValue.setProgress((int) (xy[0] * 10));
            sbYValue.setProgress((int) (xy[1] * 10));
            sbXValue.invalidate();
            sbYValue.invalidate();
        }
    };
    /**
     * Button listener
     */
    private OnClickListener btnListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
            case R.id.btnNoneAlert:
                alertMode = PHLightAlertMode.ALERT_NONE;
                btnSelectAlert.setChecked(false);
                btnLSelectAlert.setChecked(false);
                break;
            case R.id.btnSelectAlert:
                alertMode = PHLightAlertMode.ALERT_SELECT;
                btnNonAlert.setChecked(false);
                btnLSelectAlert.setChecked(false);
                break;
            case R.id.btnLSelectAlert:
                alertMode = PHLightAlertMode.ALERT_LSELECT;
                btnSelectAlert.setChecked(false);
                btnNonAlert.setChecked(false);
                break;
            case R.id.btnNoneEffect:
                effectMode = PHLightEffectMode.EFFECT_NONE;
                btnColorLoop.setChecked(false);
                break;
            case R.id.btnColorLoop:
                effectMode = PHLightEffectMode.EFFECT_COLORLOOP;
                btnNoneEffect.setChecked(false);
                break;
            default:
                break;
            }
        }
    };
    /**
     * Seek bar listener for selecting lamps
     */
    private OnSeekBarChangeListener sbListener = new OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                boolean fromUser) {
            int id = seekBar.getId();
            switch (id) {
            case R.id.sbX:
                xy[0] = (float) (progress / 10.0);
                tvX.setText(Float.toString(xy[0]));
                break;
            case R.id.sbY:
                xy[1] = (float) (progress / 10.0);
                tvY.setText(Float.toString(xy[1]));
                break;
            case R.id.sbHue:
                tvHue.setText(Integer.toString(progress));
                break;
            case R.id.sbBri:
                tvBrightness.setText(Integer.toString(progress));
                break;
            case R.id.sbSat:
                tvSaturation.setText(Integer.toString(progress));
                break;
            case R.id.sbTTime:
                tvTTime.setText(Integer.toString(progress));
                break;

            default:
                break;
            }
        }
    };

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
        if (!apiCall) {
            inflater.inflate(R.menu.done, menu);
        } else {
            inflater.inflate(R.menu.read_cache, menu);
            inflater.inflate(R.menu.send, menu);

        }

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
        case R.id.send:
            updateLightState();
            break;

        case R.id.done:
            setLightState();

        case R.id.read_cache:
            int pos = lightSpinner.getSelectedItemPosition();
            PHLight light = lights.get(pos);
            PHLightState lastKnownLightState = light.getLastKnownLightState();
            displayLastKnowLightState(lastKnownLightState);
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
     * Stores selected light state for future usage Used for schedule, timer
     * operations
     */
    private void setLightState() {
        PHLightState state = getCurrentLightState();
        phHueSDK.setCurrentLightState(state);
        this.finish();
    }

    /**
     * Collects input data and calls update light state API in bridge
     */
    private void updateLightState() {
        int pos = lightSpinner.getSelectedItemPosition();
        PHLight light = lights.get(pos);
        PHLightState state = getCurrentLightState();
        final PHWizardAlertDialog dialogManager = PHWizardAlertDialog
                .getInstance();
        dialogManager.showProgressDialog(R.string.search_progress, this);
        bridge.updateLightState(light.getIdentifier(), state,
                new PHLightListener() {

                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onStateUpdate(
                            Hashtable<String, String> successResponse,
                            List<PHHueError> errorResponse) {
                        dialogManager.closeProgressDialog();
                        StringBuffer sb = new StringBuffer();
                        sb.append("Success :  ");
                        for (String key : successResponse.keySet()) {
                            sb.append(key).append("\n\t\t\t\t");
                        }
                        sb.append("\n");
                        sb.append("Failures    \t  :  ");
                        for (int i = 0; i < errorResponse.size(); i++) {
                            PHHueError hueError = errorResponse.get(i);
                            sb.append(hueError.getAddress() + " - ("
                                    + hueError.getMessage() + ")"
                                    + "\n\t\t\t\t");
                        }
                        String resultString = sb.toString();
                        PHWizardAlertDialog.showResultDialog(
                                PHUpdateLightStateActivity.this, resultString,
                                R.string.btn_ok, R.string.txt_result);
                    }

                    @Override
                    public void onError(int arg0, String arg1) {
                        dialogManager.closeProgressDialog();
                    }
                });

    }

    /**
     * Collects current light state parameters from UI
     * 
     * @return the {@link PHLightState} object
     */
    private PHLightState getCurrentLightState() {
        PHLightState state = new PHLightState();
        if (selectedVales[0]) {
            state.setOn(tbOnOff.isChecked());
        }
        if (selectedVales[1]) {
            state.setHue(sbHue.getProgress());
        }
        if (selectedVales[2]) {
            state.setSaturation(sbSaturation.getProgress());
        }
        if (selectedVales[3]) {
            state.setBrightness(sbBrightness.getProgress());
        }
        if (selectedVales[4]) {
            state.setX(xy[0]);
            state.setY(xy[1]);
        }
        if (selectedVales[5]) {
            state.setEffectMode(effectMode);
        }
        if (selectedVales[6]) {
            state.setAlertMode(alertMode);
        }
        if (selectedVales[7]) {
            state.setTransitionTime(sbTTime.getProgress());
        }
        return state;
    }
}
