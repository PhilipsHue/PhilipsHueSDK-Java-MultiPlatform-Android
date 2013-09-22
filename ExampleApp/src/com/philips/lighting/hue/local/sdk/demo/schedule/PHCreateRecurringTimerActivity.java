package com.philips.lighting.hue.local.sdk.demo.schedule;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TimePicker;

import com.philips.lighting.hue.listener.PHScheduleListener;
import com.philips.lighting.hue.local.sdk.demo.PHHomeActivity;
import com.philips.lighting.hue.local.sdk.demo.PHWizardAlertDialog;
import com.philips.lighting.hue.local.sdk.demo.R;
import com.philips.lighting.hue.local.sdk.demo.light.PHUpdateLightStateActivity;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.wizard.helper.PHHelper;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHGroup;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import com.philips.lighting.model.PHSchedule;

/**
 * Contains demo for create recurring timer API.
 * 
 * @author Pallavi P. Ganorkar
 */
public class PHCreateRecurringTimerActivity extends Activity {

    private static final String TAG = "PHCreateTimerActivity";
    private PHHueSDK phHueSDK;
    private PHBridge bridge;

    private EditText editTvTimerName;
    private Button btnTimerTime;
    private RadioButton rbLightForTimer;
    private RadioButton rbGroupForTimer;
    private Spinner lightSpinner;
    private Spinner groupSpinner;
    private Button btnTimerLightState;
    private EditText editTvTimerDescriptor;
    private EditText editTvTimerRandomTime;
    private EditText editTvTimerRecurringInterval;

    private int mHour;
    private int mMinute;
    private PHLightState stateToSend;

    private List<PHLight> lights;
    private ArrayList<PHGroup> groups;
    private String lightArray[];
    private String groupArray[];

    /**
     * Called when the activity will start interacting with the user.
     * 
     * @param savedInstanceState
     *            the bundle object.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createschedule);
        initComponents();

        phHueSDK = PHHueSDK.getInstance(getApplicationContext());
        bridge = phHueSDK.getSelectedBridge();

        // lights to create timer.
        lights = bridge.getResourceCache().getAllLights();
        getLightNames();
        if (lightArray.length == 0) {
            rbLightForTimer.setEnabled(false);
        }

        // groups to create schedule.
        groups = bridge.getResourceCache().getAllGroups();
        getGroupNames();
        if (groupArray.length == 0) {
            rbGroupForTimer.setEnabled(false);
        }

        // set adapter to light spinner
        ArrayAdapter<String> lightSpinnerAdapter = new ArrayAdapter<String>(
                this, R.layout.inner_spinner_item, lightArray);
        lightSpinner.setAdapter(lightSpinnerAdapter);
        lightSpinner.setEnabled(false);

        // set adapter to group spinner
        ArrayAdapter<String> groupSpinnerAdapter = new ArrayAdapter<String>(
                this, R.layout.inner_spinner_item, groupArray);
        groupSpinner.setAdapter(groupSpinnerAdapter);
        groupSpinner.setEnabled(false);

        // set default time for timer
        mHour = 0;
        mMinute = 1;

        String timeString = String.format("%2d" + "h" + "%2d" + "m", mHour,
                mMinute);
        btnTimerTime.setText(timeString);

        // set listener for button click to set time.
        btnTimerTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TimePickerDialog timePicker = new TimePickerDialog(
                        PHCreateRecurringTimerActivity.this, mTimeSetListener,
                        mHour, mMinute, true);

                timePicker.show();
            }
        });

        // set listener for radio button click for light.
        rbLightForTimer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                rbLightForTimer.setChecked(true);
                rbGroupForTimer.setChecked(false);
                lightSpinner.setEnabled(true);
                groupSpinner.setEnabled(false);
            }
        });

        // set listener for radio button click for group.
        rbGroupForTimer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                rbLightForTimer.setChecked(false);
                rbGroupForTimer.setChecked(true);
                lightSpinner.setEnabled(false);
                groupSpinner.setEnabled(true);
            }
        });

        // set listener for button click to set light state.
        btnTimerLightState.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PHCreateRecurringTimerActivity.this,
                        PHUpdateLightStateActivity.class);
                if (intent != null) {
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * Creates option menu.
     * 
     * @param menu
     *            the Menu Object.
     * @return true for the menu to be displayed.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_timer, menu);
        return true;
    }

    /**
     * Called when option is selected.
     * 
     * @param item
     *            the MenuItem object.
     * @return boolean Return false to allow normal menu processing to proceed,
     *         true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.create_timer:
            createRecurringTimer();
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
     * Called when the activity is becoming visible to the user.
     */
    @Override
    protected void onStart() {
        super.onStart();
        stateToSend = phHueSDK.getCurrentLightState();
    }

    private void getLightNames() {
        int size = lights.size();
        lightArray = new String[size];
        for (int i = 0; i < size; i++) {
            lightArray[i] = lights.get(i).getName();
        }
    }

    private void getGroupNames() {
        int size = groups.size();
        groupArray = new String[size];
        for (int i = 0; i < size; i++) {
            groupArray[i] = groups.get(i).getName();
        }
    }

    /**
     * Initialize the UI components.
     */
    void initComponents() {
        TableLayout  tableLayout = (TableLayout) findViewById(R.id.TableLayout);
        TableRow tableRowRecurringTimerInterval = (TableRow) LayoutInflater.from(
                PHCreateRecurringTimerActivity.this).inflate(
                R.layout.recurring_timer_interval, null);
        editTvTimerRecurringInterval = (EditText) tableRowRecurringTimerInterval
                .findViewById(R.id.editTvTimerRecurringInterval);
        tableLayout.addView(tableRowRecurringTimerInterval, 4);
        btnTimerTime = (Button) findViewById(R.id.btnTimerTime);
        editTvTimerName = (EditText) findViewById(R.id.editTvTimerName);
        rbLightForTimer = (RadioButton) findViewById(R.id.rbLightForTimer);
        rbGroupForTimer = (RadioButton) findViewById(R.id.rbGroupForTimer);
        lightSpinner = (Spinner) findViewById(R.id.lightSpinnerForTimer);
        groupSpinner = (Spinner) findViewById(R.id.groupSpinnerForTimer);
        btnTimerLightState = (Button) findViewById(R.id.btnTimerLightState);
        editTvTimerDescriptor = (EditText) findViewById(R.id.editTvTimerDescriptor);
        editTvTimerRandomTime = (EditText) findViewById(R.id.editTvTimerRandomTime);
    }

    /**
     * update the displayed time on button.
     */
    private void updateDisplay() {
        btnTimerTime.setText(new StringBuilder().append(PHHelper.pad(mHour))
                .append("h  ").append(PHHelper.pad(mMinute)).append("m"));
    }

    /**
     * Listener for TimerPicker dialog to indicate the user is done filling in
     * the time.
     */
    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mHour = hourOfDay;
            mMinute = minute;
            updateDisplay();
        }
    };

    /**
     * Creates recurring timer.
     */
    private void createRecurringTimer() {
        PHSchedule timer;

        // name
        String timerName = editTvTimerName.getText().toString().trim();
        if (timerName.length() == 0) {
            PHWizardAlertDialog.showErrorDialog(
                    PHCreateRecurringTimerActivity.this, getResources()
                            .getString(R.string.txt_empty_timer_name),
                    R.string.btn_ok);
            return;
        }
        timer = new PHSchedule(timerName);

        // timer time
        timer.setTimer(mHour * 3600 + mMinute * 60);

        // random time
        String randomTimeStr = editTvTimerRandomTime.getText().toString()
                .trim();
        if (randomTimeStr.length() != 0) {
            timer.setRandomTime(Integer.parseInt(randomTimeStr));
        }

        // recurring timer interval
        String recurringIntervalStr = editTvTimerRecurringInterval.getText()
                .toString().trim();
        if (recurringIntervalStr.length() == 0) {
            PHWizardAlertDialog.showErrorDialog(
                    PHCreateRecurringTimerActivity.this,
                    getResources().getString(
                            R.string.txt_empty_recurring_timer_interval),
                    R.string.btn_ok);
            return;
        } else {
            timer.setRecurringTimerInterval(Integer
                    .parseInt(recurringIntervalStr));
        }

        // light or group for schedule.
        String lightIdentifier = null;
        String groupIdentifier = null;
        if (rbLightForTimer.isChecked()) {
            int pos = lightSpinner.getSelectedItemPosition();
            PHLight light = lights.get(pos);
            lightIdentifier = light.getIdentifier();
            timer.setLightIdentifier(lightIdentifier);

        } else if (rbGroupForTimer.isChecked()) {
            int pos = groupSpinner.getSelectedItemPosition();
            PHGroup group = groups.get(pos);
            groupIdentifier = group.getIdentifier();
            timer.setGroupIdentifier(groupIdentifier);
        } else {
            PHWizardAlertDialog.showErrorDialog(
                    PHCreateRecurringTimerActivity.this, getResources()
                            .getString(R.string.txt_empty_light_group),
                    R.string.btn_ok);
            return;
        }

        if (lightIdentifier == null && groupIdentifier == null) {
            PHWizardAlertDialog.showErrorDialog(
                    PHCreateRecurringTimerActivity.this, getResources()
                            .getString(R.string.txt_empty_light_group),
                    R.string.btn_ok);
            return;
        }

        // light state
        if (stateToSend == null) {
            PHWizardAlertDialog.showErrorDialog(
                    PHCreateRecurringTimerActivity.this, getResources()
                            .getString(R.string.txt_empty_light_state),
                    R.string.btn_ok);
            return;
        }

        timer.setLightState(stateToSend);

        // description
        String timerDescription = editTvTimerDescriptor.getText().toString()
                .trim();
        if (timerDescription.length() != 0) {
            timer.setDescription(timerDescription);
        }

        final PHWizardAlertDialog dialogManager = PHWizardAlertDialog
                .getInstance();
        dialogManager.showProgressDialog(R.string.sending_progress,
                PHCreateRecurringTimerActivity.this);

        // api call
        bridge.createSchedule(timer, new PHScheduleListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStateUpdate(Hashtable<String, String> arg0,
                    ArrayList<PHHueError> arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(int code, String msg) {
                Log.v(TAG, "onError : " + code + " : " + msg);
                dialogManager.closeProgressDialog();
                PHWizardAlertDialog.showErrorDialog(
                        PHCreateRecurringTimerActivity.this, msg,
                        R.string.btn_ok);

            }

            @Override
            public void onCreated(PHSchedule schedule) {
                dialogManager.closeProgressDialog();
                PHWizardAlertDialog.showResultDialog(
                        PHCreateRecurringTimerActivity.this,
                        getString(R.string.txt_timer_created), R.string.btn_ok,
                        R.string.txt_result);
                return;
            }
        });
    }

}
