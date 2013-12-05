package com.philips.lighting.hue.local.sdk.demo.schedule;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.philips.lighting.hue.listener.PHScheduleListener;
import com.philips.lighting.hue.local.sdk.demo.PHHomeActivity;
import com.philips.lighting.hue.local.sdk.demo.PHWizardAlertDialog;
import com.philips.lighting.hue.local.sdk.demo.R;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHSchedule;

/**
 * Contains demo for remove recurring timer API.
 * 
 * @author Pallavi P. Ganorkar
 */
public class PHRemoveRecurringTimerActivity extends Activity {

    private ListView removeRecurringTimerListView;
    private PHBridge bridge;

    private List<PHSchedule> recurringTimerList;

    /**
     * Called when the activity will start interacting with the user.
     * 
     * @param savedInstanceState
     *            the bundle object.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_list);

        removeRecurringTimerListView = (ListView) findViewById(R.id.list_items);
        removeRecurringTimerListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        PHHueSDK phHueSDK = PHHueSDK.getInstance();
        bridge = phHueSDK.getSelectedBridge();
        recurringTimerList = bridge.getResourceCache().getAllTimers(true);

        String[] arrTimer = new String[recurringTimerList.size()];

        for (int i = 0; i < arrTimer.length; i++) {
            arrTimer[i] = recurringTimerList.get(i).getName();
        }

        removeRecurringTimerListView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice, arrTimer));

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
        inflater.inflate(R.menu.delete, menu);
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
        case R.id.delete_group:
            removeTimer();
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
     * Removes recurring timer
     */
    private void removeTimer() {
        int pos = removeRecurringTimerListView.getCheckedItemPosition();

        if (pos == removeRecurringTimerListView.INVALID_POSITION) {
            PHWizardAlertDialog.showErrorDialog(this, R.string.txt_empty_input);
            return;
        }

        String timerID = recurringTimerList.get(pos).getIdentifier();

        final PHWizardAlertDialog dialogManager = PHWizardAlertDialog
                .getInstance();
        dialogManager.showProgressDialog(R.string.sending_progress,
                PHRemoveRecurringTimerActivity.this);

        // api call
        bridge.removeSchedule(timerID, new PHScheduleListener() {

            @Override
            public void onSuccess() {
                dialogManager.closeProgressDialog();
                PHWizardAlertDialog.showResultDialog(
                        PHRemoveRecurringTimerActivity.this,
                        getString(R.string.txt_timer_deleted), R.string.btn_ok,
                        R.string.txt_result);
                return;
            }

            @Override
            public void onStateUpdate(Hashtable<String, String> arg0,
                    List<PHHueError> arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(int code, final String message) {
                dialogManager.closeProgressDialog();
                PHRemoveRecurringTimerActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        if (isCurrentActivity()) {
                            PHWizardAlertDialog.showErrorDialog(PHRemoveRecurringTimerActivity.this, message,R.string.btn_ok);
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
