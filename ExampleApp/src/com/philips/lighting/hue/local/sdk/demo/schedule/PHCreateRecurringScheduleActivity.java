package com.philips.lighting.hue.local.sdk.demo.schedule;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

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
import com.philips.lighting.model.PHSchedule.RecurringDay;

/**
 * Contains Demo for create recurring schedule API.
 * 
 * @author Pallavi P. Ganorkar.
 */
public class PHCreateRecurringScheduleActivity extends Activity {
    private static final String TAG = "PHCreateRecurringScheduleActivity";
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

    private List<PHLight> lights;
    private List<PHGroup> groups;

    private int recurringDays;

    /**
     * Called when the activity will start interacting with the user.
     * 
     * @param savedInstanceState
     *            the bundle object.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String lightArray[];
        String groupArray[];
        
        setContentView(R.layout.createschedule);
        initComponents();

        phHueSDK = PHHueSDK.getInstance(getApplicationContext());
        bridge = phHueSDK.getSelectedBridge();

        // lights to create schedule.
        lights = bridge.getResourceCache().getAllLights();
        lightArray = phHueSDK.getLightNames(lights);
        if (lightArray.length == 0) {
            rbLightForSchedule.setEnabled(false);
        }

        // groups to create schedule.
        groups = bridge.getResourceCache().getAllGroups();
        groupArray=phHueSDK.getGroupNames(groups);
        if (groupArray.length == 0) {
            rbGroupForSchedule.setEnabled(false);
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

        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        btnScheduleTime.setText(R.string.btn_timer_time);
        // set listener for button click to set time.
        btnScheduleTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                TimePickerDialog timePicker = new TimePickerDialog(
                        PHCreateRecurringScheduleActivity.this,
                        mTimeSetListener, mHour, mMinute, true);

                timePicker.show();
            }
        });

        // set listener for radio button click for light.
        rbLightForSchedule.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                rbLightForSchedule.setChecked(true);
                rbGroupForSchedule.setChecked(false);
                lightSpinner.setEnabled(true);
                groupSpinner.setEnabled(false);

            }
        });

        // set listener for radio button click for group.
        rbGroupForSchedule.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                rbLightForSchedule.setChecked(false);
                rbGroupForSchedule.setChecked(true);
                lightSpinner.setEnabled(false);
                groupSpinner.setEnabled(true);

            }
        });

        // set listener for button click to set light state.
        btnScheduleLightState.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        PHCreateRecurringScheduleActivity.this,
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
            createRecurringSchedule();
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
        super.onStart();
        stateToSend = phHueSDK.getCurrentLightState();
    }

    /**
     * listener for view click.
     */
    private View.OnClickListener rucurringHandler = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            String recurringDaysBitStr = String.format("%07d", new BigInteger(
                    Integer.toBinaryString(recurringDays)));
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

    /**
     * Initialize the UI components.
     */
    void initComponents() {
        TableLayout tableLayout = (TableLayout) findViewById(R.id.TableLayout);

        TableRow tableRowRecurringDays = (TableRow) LayoutInflater.from(
                PHCreateRecurringScheduleActivity.this).inflate(
                R.layout.recurring_days, null);
        btnSun = (ToggleButton) tableRowRecurringDays.findViewById(R.id.btnSun);
        btnMon = (ToggleButton) tableRowRecurringDays.findViewById(R.id.btnMon);
        btnTue = (ToggleButton) tableRowRecurringDays.findViewById(R.id.btnTue);
        btnWed = (ToggleButton) tableRowRecurringDays.findViewById(R.id.btnWed);
        btnThur = (ToggleButton) tableRowRecurringDays
                .findViewById(R.id.btnThur);
        btnFri = (ToggleButton) tableRowRecurringDays.findViewById(R.id.btnFri);
        btnSat = (ToggleButton) tableRowRecurringDays.findViewById(R.id.btnSat);
        tableLayout.addView(tableRowRecurringDays, 4);

        btnSun.setOnClickListener(rucurringHandler);
        btnMon.setOnClickListener(rucurringHandler);
        btnTue.setOnClickListener(rucurringHandler);
        btnWed.setOnClickListener(rucurringHandler);
        btnThur.setOnClickListener(rucurringHandler);
        btnFri.setOnClickListener(rucurringHandler);
        btnSat.setOnClickListener(rucurringHandler);

        editTvScheduleName = (EditText) findViewById(R.id.editTvTimerName);
        rbLightForSchedule = (RadioButton) findViewById(R.id.rbLightForTimer);
        rbGroupForSchedule = (RadioButton) findViewById(R.id.rbGroupForTimer);
        lightSpinner = (Spinner) findViewById(R.id.lightSpinnerForTimer);
        groupSpinner = (Spinner) findViewById(R.id.groupSpinnerForTimer);

        btnScheduleLightState = (Button) findViewById(R.id.btnTimerLightState);
        editTvScheduleDescriptor = (EditText) findViewById(R.id.editTvTimerDescriptor);

        editTvRandomTime = (EditText) findViewById(R.id.editTvTimerRandomTime);

        btnScheduleTime = (Button) findViewById(R.id.btnTimerTime);
        TextView tvScheduleTime = (TextView) findViewById(R.id.tvTimerTime);
        tvScheduleTime.setText(R.string.txt_schedule_time);
    }

    /**
     * update the displayed time on button.
     */
    private void updateDisplay() {
        btnScheduleTime.setText(new StringBuilder().append(PHHelper.pad(mHour))
                .append(":").append(PHHelper.pad(mMinute)));
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
     * Creates recurring schedule.
     */
    private void createRecurringSchedule() {
        PHSchedule schedule;
        // name
        String timerName = editTvScheduleName.getText().toString().trim();
        if (timerName.length() == 0) {
            PHWizardAlertDialog.showErrorDialog(
                    PHCreateRecurringScheduleActivity.this, getResources()
                            .getString(R.string.txt_empty_timer_name),
                    R.string.btn_ok);
            return;
        }
        schedule = new PHSchedule(timerName);

        // time and recurring days.
        if (timeToSend == null || recurringDays == 0) {
            PHWizardAlertDialog.showErrorDialog(
                    PHCreateRecurringScheduleActivity.this,
                    getResources().getString(
                            R.string.txt_empty_time_recurring_schedule),
                    R.string.btn_ok);
            return;
        } else {
            schedule.setDate(timeToSend);
            schedule.setRecurringDays(recurringDays);
        }

        // random time
        String randomTimeStr = editTvRandomTime.getText().toString().trim();
        if (randomTimeStr.length() != 0) {
            schedule.setRandomTime(Integer.parseInt(randomTimeStr));
        }

        // light or group for schedule.
        String lightIdentifier = null;
        String groupIdentifier = null;
        if (rbLightForSchedule.isChecked()) {
            int pos = lightSpinner.getSelectedItemPosition();
            PHLight light = lights.get(pos);
            lightIdentifier = light.getIdentifier();
            schedule.setLightIdentifier(lightIdentifier);

        } else if (rbGroupForSchedule.isChecked()) {
            int pos = groupSpinner.getSelectedItemPosition();
            PHGroup group = groups.get(pos);
            groupIdentifier = group.getIdentifier();
            schedule.setGroupIdentifier(groupIdentifier);
        } else {
            PHWizardAlertDialog.showErrorDialog(
                    PHCreateRecurringScheduleActivity.this, getResources()
                            .getString(R.string.txt_empty_light_group),
                    R.string.btn_ok);
            return;
        }

        if (lightIdentifier == null && groupIdentifier == null) {
            PHWizardAlertDialog.showErrorDialog(
                    PHCreateRecurringScheduleActivity.this, getResources()
                            .getString(R.string.txt_empty_light_group),
                    R.string.btn_ok);
            return;
        }

        // light state
        if (stateToSend == null) {
            PHWizardAlertDialog.showErrorDialog(
                    PHCreateRecurringScheduleActivity.this, getResources()
                            .getString(R.string.txt_empty_light_state),
                    R.string.btn_ok);
            return;
        }

        schedule.setLightState(stateToSend);

        // description
        String timerDescription = editTvScheduleDescriptor.getText().toString()
                .trim();
        if (timerDescription.length() != 0) {
            schedule.setDescription(timerDescription);
        }

        final PHWizardAlertDialog dialogManager = PHWizardAlertDialog
                .getInstance();
        dialogManager.showProgressDialog(R.string.sending_progress,
                PHCreateRecurringScheduleActivity.this);

        // api call
        bridge.createSchedule(schedule, new PHScheduleListener() {

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
                Log.v(TAG, "onError : " + code + " : " + msg);
                dialogManager.closeProgressDialog();
                PHWizardAlertDialog.showErrorDialog(
                        PHCreateRecurringScheduleActivity.this, msg,
                        R.string.btn_ok);

            }

            @Override
            public void onCreated(PHSchedule schedule) {
                dialogManager.closeProgressDialog();
                PHWizardAlertDialog.showResultDialog(
                        PHCreateRecurringScheduleActivity.this,
                        getString(R.string.txt_timer_created), R.string.btn_ok,
                        R.string.txt_result);
                return;
            }
        });
    }

}
