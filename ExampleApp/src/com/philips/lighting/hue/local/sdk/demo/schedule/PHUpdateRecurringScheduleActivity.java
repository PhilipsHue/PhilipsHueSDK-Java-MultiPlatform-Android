package com.philips.lighting.hue.local.sdk.demo.schedule;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

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
import com.philips.lighting.model.PHSchedule.RecurringDay;

/**
 * Contains demo for update recurring schedule API.
 * 
 * @author Pallavi P. Ganorkar
 */
public class PHUpdateRecurringScheduleActivity extends Activity {
    private static final String TAG = "PHUpdateRecurringScheduleActivity";
    private Spinner scheduleSpinner;
    private List<PHSchedule> recurringSchedules;

    private String recurringSchedulesArray[];

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
    private EditText editTvRandomTime;
    private ToggleButton btnSun;
    private ToggleButton btnMon;
    private ToggleButton btnTue;
    private ToggleButton btnWed;
    private ToggleButton btnThur;
    private ToggleButton btnFri;
    private ToggleButton btnSat;

    private int mHour;
    private int mMinute;
    private static Date timeToSend;
    private PHLightState stateToSend;

    private static int recurringDays;
    private static String recurringDaysBitStr;

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

        recurringSchedules = bridge.getResourceCache().getAllSchedules(true);
        getRecurringSchedulesNames();

        ArrayAdapter<String> scheduleSpinnerAdapter = new ArrayAdapter<String>(
                this, R.layout.light_spinner_item, recurringSchedulesArray);
        scheduleSpinner.setAdapter(scheduleSpinnerAdapter);

        scheduleSpinner.setVisibility(View.VISIBLE);

        lights = bridge.getResourceCache().getAllLights();
        lightArray = phHueSDK.getLightNames(lights);
        if (lightArray.length == 0) {
            rbLightForSchedule.setEnabled(false);
        }

        groups = bridge.getResourceCache().getAllGroups();
        groupArray=phHueSDK.getGroupNames(groups);
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
                TimePickerDialog timePicker = new TimePickerDialog(
                        PHUpdateRecurringScheduleActivity.this,
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
                        PHUpdateRecurringScheduleActivity.this,
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

                PHSchedule timer = recurringSchedules.get(position);
                editTvScheduleName.setText(timer.getName());
                if (timer.getLightIdentifier() != null) {
                    rbLightForSchedule.setChecked(true);
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
                        rbGroupForSchedule.setChecked(false);
                        groupSpinner.setEnabled(false);
                    }
                } else if (timer.getGroupIdentifier() != null) {
                    rbGroupForSchedule.setChecked(true);
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
                        rbLightForSchedule.setChecked(false);
                        lightSpinner.setEnabled(false);
                    }
                }

                Date lastScheduleTime = timer.getDate();
                if (lastScheduleTime != null) {
                    mHour = lastScheduleTime.getHours();
                    mMinute = lastScheduleTime.getMinutes();
                    timeToSend = Calendar.getInstance().getTime();
                    timeToSend.setHours(mHour);
                    timeToSend.setMinutes(mMinute);
                    updateDisplay();
                }

                editTvScheduleDescriptor.setText(timer.getDescription());
                editTvRandomTime.setText(Integer.toString(timer.getRandomTime()));

                recurringDays = timer.getRecurringDays();
                recurringDaysBitStr = String.format("%07d", new BigInteger(
                        Integer.toBinaryString(recurringDays)));

                btnSun.setChecked(false);
                btnSat.setChecked(false);
                btnFri.setChecked(false);
                btnThur.setChecked(false);
                btnThur.setChecked(false);
                btnWed.setChecked(false);
                btnTue.setChecked(false);
                btnMon.setChecked(false);

                for (int i = 0; i < recurringDaysBitStr.length(); i++) {
                    switch (i) {
                    case 0:
                        if (recurringDaysBitStr.charAt(0) == '1') {

                            recurringDays = (recurringDays | RecurringDay.RECURRING_MONDAY
                                    .getValue());
                            btnMon.setChecked(true);
                        }

                        break;
                    case 1:
                        if (recurringDaysBitStr.charAt(1) == '1') {
                            recurringDays = (recurringDays | RecurringDay.RECURRING_TUESDAY
                                    .getValue());
                            btnTue.setChecked(true);
                        }
                        break;
                    case 2:
                        if (recurringDaysBitStr.charAt(2) == '1') {
                            recurringDays = (recurringDays | RecurringDay.RECURRING_WEDNESDAY
                                    .getValue());
                            btnWed.setChecked(true);
                        }
                        break;
                    case 3:
                        if (recurringDaysBitStr.charAt(3) == '1') {
                            recurringDays = (recurringDays | RecurringDay.RECURRING_THURSDAY
                                    .getValue());
                            btnThur.setChecked(true);
                        }
                        break;
                    case 4:
                        if (recurringDaysBitStr.charAt(4) == '1') {
                            recurringDays = (recurringDays | RecurringDay.RECURRING_FRIDAY
                                    .getValue());
                            btnFri.setChecked(true);
                        }
                        break;
                    case 5:
                        if (recurringDaysBitStr.charAt(5) == '1') {
                            recurringDays = (recurringDays | RecurringDay.RECURRING_SATURDAY
                                    .getValue());
                            btnSat.setChecked(true);
                        }
                        break;
                    case 6:
                        if (recurringDaysBitStr.charAt(6) == '1') {
                            recurringDays = (recurringDays | RecurringDay.RECURRING_SUNDAY
                                    .getValue());
                            btnSun.setChecked(true);
                        }
                        break;
                    default:
                        break;
                    }
                }
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
            updateRecurringScheudle();
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
        TextView tvScheduleTime;
        TableLayout tableLayout = (TableLayout) findViewById(R.id.TableLayout);

        TableRow tableRowRecurringDays = (TableRow) LayoutInflater.from(
                PHUpdateRecurringScheduleActivity.this).inflate(
                R.layout.recurring_days, null);
        btnSun = (ToggleButton) tableRowRecurringDays.findViewById(R.id.btnSun);
        btnMon = (ToggleButton) tableRowRecurringDays.findViewById(R.id.btnMon);
        btnTue = (ToggleButton) tableRowRecurringDays.findViewById(R.id.btnTue);
        btnWed = (ToggleButton) tableRowRecurringDays.findViewById(R.id.btnWed);
        btnThur = (ToggleButton) tableRowRecurringDays
                .findViewById(R.id.btnThur);
        btnFri = (ToggleButton) tableRowRecurringDays.findViewById(R.id.btnFri);
        btnSat = (ToggleButton) tableRowRecurringDays.findViewById(R.id.btnSat);
        tableLayout.addView(tableRowRecurringDays, 3);

        btnSun.setOnClickListener(rucurringHandler);
        btnMon.setOnClickListener(rucurringHandler);
        btnTue.setOnClickListener(rucurringHandler);
        btnWed.setOnClickListener(rucurringHandler);
        btnThur.setOnClickListener(rucurringHandler);
        btnFri.setOnClickListener(rucurringHandler);
        btnSat.setOnClickListener(rucurringHandler);
        scheduleSpinner = (Spinner) findViewById(R.id.timerSpinner);
        editTvScheduleName = (EditText) findViewById(R.id.editTvTimerName);
        rbLightForSchedule = (RadioButton) findViewById(R.id.rbLightForTimer);
        rbGroupForSchedule = (RadioButton) findViewById(R.id.rbGroupForTimer);
        lightSpinner = (Spinner) findViewById(R.id.lightSpinnerForTimer);
        groupSpinner = (Spinner) findViewById(R.id.groupSpinnerForTimer);
        btnScheduleTime = (Button) findViewById(R.id.btnTimerTime);
        tvScheduleTime = (TextView) findViewById(R.id.tvTimerTime);
        tvScheduleTime.setText(R.string.txt_schedule_time);
        btnScheduleLightState = (Button) findViewById(R.id.btnTimerLightState);
        editTvScheduleDescriptor = (EditText) findViewById(R.id.editTvTimerDescriptor);
        editTvRandomTime = (EditText) findViewById(R.id.editTvTimerRandomTime);
    }

    private void getRecurringSchedulesNames() {
        int size = recurringSchedules.size();
        recurringSchedulesArray = new String[size];
        for (int i = 0; i < size; i++) {
            recurringSchedulesArray[i] = recurringSchedules.get(i).getName();
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
     * Updates recurring schedule.
     */
    private void updateRecurringScheudle() {
        int pos = scheduleSpinner.getSelectedItemPosition();
        PHSchedule schedule = recurringSchedules.get(pos);

        String name = editTvScheduleName.getText().toString().trim();
        if (name.length() != 0) {
            schedule.setName(name);
        }

        if (timeToSend == null || recurringDays == 0) {
            PHWizardAlertDialog.showErrorDialog(
                    PHUpdateRecurringScheduleActivity.this, getResources()
                            .getString(R.string.txt_empty_time),
                    R.string.btn_ok);
            return;
        } else {
            schedule.setDate(timeToSend);
            schedule.setRecurringDays(recurringDays);
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

        String description = editTvScheduleDescriptor.getText().toString()
                .trim();

        if (description.length() != 0) {
            schedule.setDescription(description);
        }

        String timerRandomTime = editTvRandomTime.getText().toString().trim();
        if (timerRandomTime.length() != 0) {
            schedule.setRandomTime(Integer.parseInt(timerRandomTime));
        }

        final PHWizardAlertDialog dialogManager = PHWizardAlertDialog
                .getInstance();
        dialogManager.showProgressDialog(R.string.sending_progress,
                PHUpdateRecurringScheduleActivity.this);

        bridge.updateSchedule(schedule, new PHScheduleListener() {

            @Override
            public void onSuccess() {

                dialogManager.closeProgressDialog();
                PHUpdateRecurringScheduleActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        if (isCurrentActivity()) {
                            PHWizardAlertDialog.showResultDialog( PHUpdateRecurringScheduleActivity.this,getString(R.string.txt_timer_updated), R.string.btn_ok,R.string.txt_result);
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
                Log.v(TAG, "onError : " + code + " : " + msg);
                PHUpdateRecurringScheduleActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        if (isCurrentActivity()) {
                            PHWizardAlertDialog.showErrorDialog(PHUpdateRecurringScheduleActivity.this, msg,R.string.btn_ok);
                        }
                    }
                  });

            }
        });
    }

    /**
     * Listener for ToggleButton for recurring days.
     */
    private View.OnClickListener rucurringHandler = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            StringBuffer sb = new StringBuffer(recurringDaysBitStr);
            switch (v.getId()) {
            case R.id.btnSun:
                if (btnSun.isChecked()) {

                    recurringDays = (recurringDays | RecurringDay.RECURRING_SUNDAY
                            .getValue());
                } else {
                    sb.setCharAt(6, '0');
                    recurringDaysBitStr = sb.toString();
                    recurringDays = Integer.parseInt(recurringDaysBitStr, 2);
                }
                break;
            case R.id.btnMon:
                if (btnMon.isChecked()) {
                    recurringDays = (recurringDays | RecurringDay.RECURRING_MONDAY
                            .getValue());
                } else {
                    sb.setCharAt(0, '0');
                    recurringDaysBitStr = sb.toString();
                    recurringDays = Integer.parseInt(recurringDaysBitStr, 2);
                }
                break;
            case R.id.btnTue:
                if (btnTue.isChecked()) {
                    recurringDays = (recurringDays | RecurringDay.RECURRING_TUESDAY
                            .getValue());
                } else {
                    sb.setCharAt(1, '0');
                    recurringDaysBitStr = sb.toString();
                    recurringDays = Integer.parseInt(recurringDaysBitStr, 2);
                }
                break;
            case R.id.btnWed:
                if (btnWed.isChecked()) {
                    recurringDays = (recurringDays | RecurringDay.RECURRING_WEDNESDAY
                            .getValue());
                } else {
                    sb.setCharAt(2, '0');
                    recurringDaysBitStr = sb.toString();
                    recurringDays = Integer.parseInt(recurringDaysBitStr, 2);
                }
                break;
            case R.id.btnThur:
                if (btnThur.isChecked()) {
                    recurringDays = (recurringDays | RecurringDay.RECURRING_THURSDAY
                            .getValue());
                } else {
                    sb.setCharAt(3, '0');
                    recurringDaysBitStr = sb.toString();
                    recurringDays = Integer.parseInt(recurringDaysBitStr, 2);
                }
                break;
            case R.id.btnFri:
                if (btnFri.isChecked()) {
                    recurringDays = (recurringDays | RecurringDay.RECURRING_FRIDAY
                            .getValue());
                } else {
                    sb.setCharAt(4, '0');
                    recurringDaysBitStr = sb.toString();
                    recurringDays = Integer.parseInt(recurringDaysBitStr, 2);
                }
                break;
            case R.id.btnSat:
                if (btnSat.isChecked()) {
                    recurringDays = (recurringDays | RecurringDay.RECURRING_SATURDAY
                            .getValue());
                } else {
                    sb.setCharAt(5, '0');
                    recurringDaysBitStr = sb.toString();
                    recurringDays = Integer.parseInt(recurringDaysBitStr, 2);
                }
                break;
            }
        }
    };
    
    private boolean isCurrentActivity() {
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> RunningTask = mActivityManager.getRunningTasks(1);
        ActivityManager.RunningTaskInfo ar = RunningTask.get(0);
        String currentClass = "." + this.getClass().getSimpleName();
        String topActivity =  ar.topActivity.getShortClassName().toString();
        return topActivity.contains(currentClass);
    }
}
