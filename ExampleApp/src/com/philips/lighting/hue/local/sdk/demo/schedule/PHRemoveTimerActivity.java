package com.philips.lighting.hue.local.sdk.demo.schedule;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;
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
 * Contains demo for remove timer API
 * 
 * @author Pallavi P. Ganorkar.
 */
public class PHRemoveTimerActivity extends Activity {

    private ListView removeTimerListView;
    private PHBridge bridge;

    private List<PHSchedule> nonRecurringTimerList;

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

        removeTimerListView = (ListView) findViewById(R.id.list_items);
        removeTimerListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        PHHueSDK phHueSDK = PHHueSDK.getInstance(getApplicationContext());
        bridge = phHueSDK.getSelectedBridge();
        nonRecurringTimerList = bridge.getResourceCache().getAllTimers(false);

        String[] arrTimer = new String[nonRecurringTimerList.size()];

        for (int i = 0; i < arrTimer.length; i++) {
            arrTimer[i] = nonRecurringTimerList.get(i).getName();
        }

        removeTimerListView.setAdapter(new ArrayAdapter<String>(this,
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
     * Removes non-recurring timer
     */
    private void removeTimer() {
        int pos = removeTimerListView.getCheckedItemPosition();

        if (pos == removeTimerListView.INVALID_POSITION) {
            PHWizardAlertDialog.showErrorDialog(this, R.string.txt_empty_input);
            return;
        }

        String timerID = nonRecurringTimerList.get(pos).getIdentifier();

        final PHWizardAlertDialog dialogManager = PHWizardAlertDialog
                .getInstance();
        dialogManager.showProgressDialog(R.string.sending_progress,
                PHRemoveTimerActivity.this);

        // api call
        bridge.removeSchedule(timerID, new PHScheduleListener() {

            @Override
            public void onSuccess() {
                dialogManager.closeProgressDialog();
                PHWizardAlertDialog.showResultDialog(
                        PHRemoveTimerActivity.this,
                        getString(R.string.txt_timer_deleted), R.string.btn_ok,
                        R.string.txt_result);
                return;
            }

            @Override
            public void onStateUpdate(Hashtable<String, String> arg0,
                    ArrayList<PHHueError> arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(int code, String massage) {
                dialogManager.closeProgressDialog();
                PHWizardAlertDialog.showErrorDialog(PHRemoveTimerActivity.this,
                        massage, R.string.btn_ok);
            }
        });
    }

}
