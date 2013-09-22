package com.philips.lighting.hue.local.sdk.demo.schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.philips.lighting.hue.listener.PHScheduleListener;
import com.philips.lighting.hue.local.sdk.demo.PHHomeActivity;
import com.philips.lighting.hue.local.sdk.demo.PHWizardAlertDialog;
import com.philips.lighting.hue.local.sdk.demo.R;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.wizard.helper.PHHelper;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHGroup;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import com.philips.lighting.model.PHSchedule;

/**
 * Contains demo for update non-recurring schedule API.
 * 
 * @author Pallavi P. Ganorkar
 */
public class PHUpdateNonRecurringScheduleActivity extends Activity {
    private static final String TAG = "PHUpdateNonRecurringScheduleActivity";
    private Spinner scheduleSpinner;
    private ArrayList<PHSchedule> nonRecurringSchedules;


    private PHHueSDK phHueSDK;
    private PHBridge bridge;

    private EditText editTvScheduleName;
    private Button btnScheduleTime;
    private RadioButton rbLightForSchedule;
    private RadioButton rbGroupForSchedule;
    private Spinner lightSpinner;
    private Spinner groupSpinner;
    private Button btnScheduleLightState;
    private EditText editTvScheduleDescriptor;

    private EditText editTvScheduleRandomTime;

    private int mHour;
    private int mMinute;
    private static Date timeToSend;
    private PHLightState stateToSend;

    private ArrayList<PHLight> lights;
    private ArrayList<PHGroup> groups;


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
        
        String lightArray[];
        String groupArray[];
        
        phHueSDK = PHHueSDK.getInstance(getApplicationContext());
        bridge = phHueSDK.getSelectedBridge();

        nonRecurringSchedules = bridge.getResourceCache()
                .getAllSchedules(false);
        String nonRecurringScheduleArray[] = phHueSDK
                .getScheduleNames(nonRecurringSchedules);

        ArrayAdapter<String> scheduleSpinnerAdapter = new ArrayAdapter<String>(
                this, R.layout.light_spinner_item, nonRecurringScheduleArray);
        scheduleSpinner.setAdapter(scheduleSpinnerAdapter);

        scheduleSpinner.setVisibility(View.VISIBLE);

        lights = bridge.getResourceCache().getAllLights();
        lightArray = phHueSDK.getLightNames(lights);
        if (lightArray.length == 0) {
            rbLightForSchedule.setEnabled(false);
        }

        groups = bridge.getResourceCache().getAllGroups();
        groupArray = phHueSDK.getGroupNames(groups);
        if (groupArray.length == 0) {
            rbGroupForSchedule.setEnabled(false);
        }

        ArrayAdapter<String> lightSpinnerAdapter = new ArrayAdapter<String>(
                this, R.layout.inner_spinner_item, lightArray);
        lightSpinner.setAdapter(lightSpinnerAdapter);
        lightSpinner.setEnabled(false);

        ArrayAdapter<String> groupSpinnerAdapter = new ArrayAdapter<String>(
                this, R.layout.inner_spinner_item, groupArray);
        groupSpinner.setAdapter(groupSpinnerAdapter);
        groupSpinner.setEnabled(false);

        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        btnScheduleTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                TimePickerDialog timePicker = new TimePickerDialog(
                        PHUpdateNonRecurringScheduleActivity.this,
                        mTimeSetListener, mHour, mMinute, true);

                timePicker.show();
            }
        });

        rbLightForSchedule.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                rbLightForSchedule.setChecked(true);
                rbGroupForSchedule.setChecked(false);
                lightSpinner.setEnabled(true);
                groupSpinner.setEnabled(false);
            }
        });

        rbGroupForSchedule.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                rbLightForSchedule.setChecked(false);
                rbGroupForSchedule.setChecked(true);
                lightSpinner.setEnabled(false);
                groupSpinner.setEnabled(true);
            }
        });

        btnScheduleLightState.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        PHUpdateNonRecurringScheduleActivity.this,
                        PHUpdateScheduleLightStateActivity.class);
                if (intent != null) {
                    startActivity(intent);
                }
            }
        });

        scheduleSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {

                PHSchedule schedule = nonRecurringSchedules.get(position);
                editTvScheduleName.setText(schedule.getName());
                if (schedule.getLightIdentifier() != null) {
                    rbLightForSchedule.setChecked(true);
                    lightSpinner.setEnabled(true);
                    String lightIdentifier = schedule.getLightIdentifier();
                    PHLight light = null;
                    for (int i = 0; i < lights.size(); i++) {
                        if (lightIdentifier.equals(lights.get(i)
                                .getIdentifier())) {
                            light = lights.get(i);
                            break;
                        }
                    }
                    if (light != null) {
                        lightSpinner.setSelection(PHHelper.getIndex(
                                lightSpinner, light.getName()));
                        phHueSDK.setCurrentLightState(schedule.getLightState());
                        rbGroupForSchedule.setChecked(false);
                        groupSpinner.setEnabled(false);
                    }

                } else if (schedule.getGroupIdentifier() != null) {
                    rbGroupForSchedule.setChecked(true);
                    groupSpinner.setEnabled(true);
                    String groupIdentifier = schedule.getGroupIdentifier();

                    PHGroup group = null;
                    for (int i = 0; i < groups.size(); i++) {
                        if (groupIdentifier.equals(groups.get(i)
                                .getIdentifier())) {
                            group = groups.get(i);
                            break;
                        }
                    }
                    if (group != null) {
                        groupSpinner.setSelection(PHHelper.getIndex(
                                groupSpinner, group.getName()));
                        phHueSDK.setCurrentLightState(schedule.getLightState());
                        rbLightForSchedule.setChecked(false);
                        lightSpinner.setEnabled(false);
                    }
                }

                Date lastScheduleTime = schedule.getDate();
                if (lastScheduleTime != null) {
                    mHour = lastScheduleTime.getHours();
                    mMinute = lastScheduleTime.getMinutes();
                    timeToSend = Calendar.getInstance().getTime();
                    timeToSend.setHours(mHour);
                    timeToSend.setMinutes(mMinute);
                    updateDisplay();
                }
                editTvScheduleDescriptor.setText(schedule.getDescription());
                editTvScheduleRandomTime.setText(Integer.toString(schedule
                        .getRandomTime()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
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
        inflater.inflate(R.menu.send, menu);
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
        case R.id.send:
            updateNonRecurringSchedule();
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

    /**
     * Initialize the UI components.
     */
    void initComponents() {
        scheduleSpinner = (Spinner) findViewById(R.id.timerSpinner);
        editTvScheduleName = (EditText) findViewById(R.id.editTvTimerName);
        rbLightForSchedule = (RadioButton) findViewById(R.id.rbLightForTimer);
        rbGroupForSchedule = (RadioButton) findViewById(R.id.rbGroupForTimer);
        lightSpinner = (Spinner) findViewById(R.id.lightSpinnerForTimer);
        groupSpinner = (Spinner) findViewById(R.id.groupSpinnerForTimer);
        btnScheduleTime = (Button) findViewById(R.id.btnTimerTime);
        TextView tvScheduleTime = (TextView) findViewById(R.id.tvTimerTime);
        tvScheduleTime.setText(R.string.txt_schedule_time);
        btnScheduleLightState = (Button) findViewById(R.id.btnTimerLightState);
        editTvScheduleDescriptor = (EditText) findViewById(R.id.editTvTimerDescriptor);

        editTvScheduleRandomTime = (EditText) findViewById(R.id.editTvTimerRandomTime);
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

            timeToSend = Calendar.getInstance().getTime();
            timeToSend.setHours(mHour);
            timeToSend.setMinutes(mMinute);
        }
    };

    /**
     * update the displayed time on button.
     */
    private void updateDisplay() {
        btnScheduleTime.setText(new StringBuilder().append(PHHelper.pad(mHour))
                .append(":").append(PHHelper.pad(mMinute)));
    }

    /**
     * Updates non recurring schedule.
     */
    private void updateNonRecurringSchedule() {
        int pos = scheduleSpinner.getSelectedItemPosition();
        PHSchedule schedule = nonRecurringSchedules.get(pos);

        String timerName = editTvScheduleName.getText().toString().trim();
        if (timerName.length() != 0) {
            schedule.setName(timerName);
        }

        if (timeToSend == null) {
            PHWizardAlertDialog.showErrorDialog(
                    PHUpdateNonRecurringScheduleActivity.this, getResources()
                            .getString(R.string.txt_empty_time),
                    R.string.btn_ok);
            return;
        } else {
            schedule.setDate(timeToSend);

        }

        String lightIdentifier = null;
        String groupIdentifier = null;
        if (rbLightForSchedule.isChecked()) {
            int lightPos = lightSpinner.getSelectedItemPosition();
            PHLight light = lights.get(lightPos);
            lightIdentifier = light.getIdentifier();
            schedule.setLightIdentifier(lightIdentifier);
            schedule.setGroupIdentifier(null);

        } else if (rbGroupForSchedule.isChecked()) {
            int groupPos = groupSpinner.getSelectedItemPosition();
            PHGroup group = groups.get(groupPos);
            groupIdentifier = group.getIdentifier();
            schedule.setGroupIdentifier(groupIdentifier);
            schedule.setLightIdentifier(null);
        }

        if (stateToSend != null) {
            schedule.setLightState(stateToSend);
        }

        String timerDescription = editTvScheduleDescriptor.getText().toString()
                .trim();

        if (timerDescription.length() != 0) {
            schedule.setDescription(timerDescription);
        }

        String timerRandomTime = editTvScheduleRandomTime.getText().toString()
                .trim();
        if (timerRandomTime.length() != 0) {
            schedule.setRandomTime(Integer.parseInt(timerRandomTime));
        }

        final PHWizardAlertDialog dialogManager = PHWizardAlertDialog
                .getInstance();
        dialogManager.showProgressDialog(R.string.sending_progress,
                PHUpdateNonRecurringScheduleActivity.this);

        bridge.updateSchedule(schedule, new PHScheduleListener() {

            @Override
            public void onSuccess() {

                dialogManager.closeProgressDialog();
                PHWizardAlertDialog.showResultDialog(
                        PHUpdateNonRecurringScheduleActivity.this,
                        getString(R.string.txt_timer_updated), R.string.btn_ok,
                        R.string.txt_result);
            }

            @Override
            public void onStateUpdate(
                    Hashtable<String, String> successAttribute,
                    ArrayList<PHHueError> errorAttribute) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onError(int code, String msg) {
                dialogManager.closeProgressDialog();
                Log.v(TAG, "onError : " + code + " : " + msg);
                PHWizardAlertDialog.showErrorDialog(
                        PHUpdateNonRecurringScheduleActivity.this, msg,
                        R.string.btn_ok);
            }
        });
    }

}
