package com.philips.lighting.hue.local.sdk.demo.schedule;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
 * Contains demo for update timer API.
 * 
 * @author Pallavi P. Ganorkar
 */
public class PHUpdateTimerActivity extends Activity {

    private Spinner timerSpinner;
    private List<PHSchedule> timers;
    private String timerArray[];

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

    private int mHour;
    private int mMinute;
    private PHLightState stateToSend;

    private List<PHLight> lights;
    private List<PHGroup> groups;


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
        
        phHueSDK = PHHueSDK.getInstance();
        bridge = phHueSDK.getSelectedBridge();

        timers = bridge.getResourceCache().getAllTimers(false);
        getTimerNames();

        ArrayAdapter<String> timerSpinnerAdapter = new ArrayAdapter<String>(
                this, R.layout.light_spinner_item, timerArray);
        timerSpinner.setAdapter(timerSpinnerAdapter);

        timerSpinner.setVisibility(View.VISIBLE);

        lights = bridge.getResourceCache().getAllLights();
        lightArray = phHueSDK.getLightNames(lights);

        if (lightArray.length == 0) {
            rbLightForTimer.setEnabled(false);
        }

        groups = bridge.getResourceCache().getAllGroups();
        groupArray = phHueSDK.getGroupNames(groups);

        if (groupArray.length == 0) {
            rbGroupForTimer.setEnabled(false);
        }

        ArrayAdapter<String> lightSpinnerAdapter = new ArrayAdapter<String>(
                this, R.layout.inner_spinner_item, lightArray);
        lightSpinner.setAdapter(lightSpinnerAdapter);
        lightSpinner.setEnabled(false);

        ArrayAdapter<String> groupSpinnerAdapter = new ArrayAdapter<String>(
                this, R.layout.inner_spinner_item, groupArray);
        groupSpinner.setAdapter(groupSpinnerAdapter);
        groupSpinner.setEnabled(false);

        btnTimerTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                TimePickerDialog timePicker = new TimePickerDialog(
                        PHUpdateTimerActivity.this, mTimeSetListener, mHour,
                        mMinute, true);

                timePicker.show();
            }
        });

        rbLightForTimer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                rbLightForTimer.setChecked(true);
                rbGroupForTimer.setChecked(false);
                lightSpinner.setEnabled(true);
                groupSpinner.setEnabled(false);
            }
        });

        rbGroupForTimer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                rbLightForTimer.setChecked(false);
                rbGroupForTimer.setChecked(true);
                lightSpinner.setEnabled(false);
                groupSpinner.setEnabled(true);
            }
        });

        btnTimerLightState.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PHUpdateTimerActivity.this,
                        PHUpdateScheduleLightStateActivity.class);
                if (intent != null) {
                    startActivity(intent);
                }
            }
        });

        timerSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {

                PHSchedule timer = timers.get(position);
                editTvTimerName.setText(timer.getName());

                if (timer.getLightIdentifier() != null) {
                    rbLightForTimer.setChecked(true);
                    lightSpinner.setEnabled(true);
                    String lightIdentifier = timer.getLightIdentifier();
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
                        phHueSDK.setCurrentLightState(timer.getLightState());
                    }
                } else if (timer.getGroupIdentifier() != null) {
                    rbGroupForTimer.setChecked(true);
                    groupSpinner.setEnabled(true);
                    String groupIdentifier = timer.getGroupIdentifier();

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
                        phHueSDK.setCurrentLightState(timer.getLightState());
                    }
                }

                int lastTimerTime = timer.getTimer();
                mHour = lastTimerTime / 3600;
                mMinute = (lastTimerTime % 3600) / 60;
                updateDisplay();

                editTvTimerDescriptor.setText(timer.getDescription());

                editTvTimerRandomTime.setText(Integer.toString(timer
                        .getRandomTime()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    /**
     * Called when the activity is becoming visible to the user.
     */
    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        stateToSend = phHueSDK.getCurrentLightState();
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
            updateTimer();
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
     * Initialize the UI components.
     */
    void initComponents() {
        timerSpinner = (Spinner) findViewById(R.id.timerSpinner);
        editTvTimerName = (EditText) findViewById(R.id.editTvTimerName);
        rbLightForTimer = (RadioButton) findViewById(R.id.rbLightForTimer);
        rbGroupForTimer = (RadioButton) findViewById(R.id.rbGroupForTimer);
        lightSpinner = (Spinner) findViewById(R.id.lightSpinnerForTimer);
        groupSpinner = (Spinner) findViewById(R.id.groupSpinnerForTimer);
        btnTimerTime = (Button) findViewById(R.id.btnTimerTime);
        btnTimerLightState = (Button) findViewById(R.id.btnTimerLightState);
        editTvTimerDescriptor = (EditText) findViewById(R.id.editTvTimerDescriptor);
        editTvTimerRandomTime = (EditText) findViewById(R.id.editTvTimerRandomTime);
    }

    private void getTimerNames() {
        int size = timers.size();
        timerArray = new String[size];
        for (int i = 0; i < size; i++) {
            timerArray[i] = timers.get(i).getName();
        }
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
     * update the displayed time on button.
     */
    private void updateDisplay() {
        btnTimerTime.setText(new StringBuilder().append(PHHelper.pad(mHour))
                .append("h  ").append(PHHelper.pad(mMinute)).append("m"));
    }

    /**
     * Updates timer.
     */
    private void updateTimer() {
        int pos = timerSpinner.getSelectedItemPosition();
        PHSchedule timer = timers.get(pos);

        String timerName = editTvTimerName.getText().toString().trim();
        if (timerName.length() != 0) {
            timer.setName(timerName);
        }

        String lightIdentifier = null;
        String groupIdentifier = null;
        if (rbLightForTimer.isChecked()) {
            int lightPos = lightSpinner.getSelectedItemPosition();
            PHLight light = lights.get(lightPos);
            lightIdentifier = light.getIdentifier();
            timer.setLightIdentifier(lightIdentifier);
            timer.setGroupIdentifier(null);

        } else if (rbGroupForTimer.isChecked()) {
            int groupPos = groupSpinner.getSelectedItemPosition();
            PHGroup group = groups.get(groupPos);
            groupIdentifier = group.getIdentifier();
            timer.setGroupIdentifier(groupIdentifier);
            timer.setLightIdentifier(null);
        }

        if (stateToSend != null) {
            timer.setLightState(stateToSend);
        }

        String timerDescription = editTvTimerDescriptor.getText().toString()
                .trim();

        if (timerDescription.length() != 0) {
            timer.setDescription(timerDescription);
        }

        timer.setTimer(mHour * 3600 + mMinute * 60);

        String randomTimeStr = editTvTimerRandomTime.getText().toString()
                .trim();
        if (randomTimeStr.length() != 0) {
            timer.setRandomTime(Integer.parseInt(randomTimeStr));
        }

        final PHWizardAlertDialog dialogManager = PHWizardAlertDialog
                .getInstance();
        dialogManager.showProgressDialog(R.string.sending_progress,
                PHUpdateTimerActivity.this);

        bridge.updateSchedule(timer, new PHScheduleListener() {

            @Override
            public void onSuccess() {

                dialogManager.closeProgressDialog();
                PHUpdateTimerActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        if (isCurrentActivity()) {
                            PHWizardAlertDialog.showResultDialog( PHUpdateTimerActivity.this, getString(R.string.txt_timer_updated), R.string.btn_ok,R.string.txt_result);
                        }
                    }
                  });
                
            }

            @Override
            public void onStateUpdate(
                    Hashtable<String, String> successAttribute,
                    List<PHHueError> errorAttribute) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onError(int code, final String msg) {
                dialogManager.closeProgressDialog();
                PHUpdateTimerActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        if (isCurrentActivity()) {
                            PHWizardAlertDialog.showErrorDialog(PHUpdateTimerActivity.this,msg, R.string.btn_ok);
                        }
                    }
                  });
                
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
