package com.philips.lighting.hue.local.sdk.demo.group;

import java.util.ArrayList;
import java.util.Hashtable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.philips.lighting.hue.listener.PHGroupListener;
import com.philips.lighting.hue.local.sdk.demo.PHHomeActivity;
import com.philips.lighting.hue.local.sdk.demo.PHWizardAlertDialog;
import com.philips.lighting.hue.local.sdk.demo.R;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHGroup;
import com.philips.lighting.model.PHHueError;

/**
 * Contains Demo for delete group API
 * 
 * @author Manmath R
 */
public class PHDeleteGroupActivity extends Activity {

    private ListView deleteGroupListView;
    private PHBridge bridge;
    private ArrayList<PHGroup> groups;

    /**
     * Called when the activity is first created.
     * 
     * @param savedInstanceState
     *            the bundle object
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_list);

        deleteGroupListView = (ListView) findViewById(R.id.list_items);
        deleteGroupListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        PHHueSDK phHueSDK = PHHueSDK.getInstance(getApplicationContext());
        bridge = phHueSDK.getSelectedBridge();
        groups = bridge.getResourceCache().getAllGroups();
        String[] arrGroup = new String[groups.size()];

        for (int i = 0; i < arrGroup.length; i++) {
            arrGroup[i] = groups.get(i).getName();
        }

        deleteGroupListView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice, arrGroup));
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
        inflater.inflate(R.menu.delete, menu);
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
        case R.id.delete_group:
            deleteGroup();
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
     * Deletes group using bridge API
     */
    private void deleteGroup() {
        long pos = deleteGroupListView.getCheckedItemPosition();
        if (pos == deleteGroupListView.INVALID_POSITION) {
            PHWizardAlertDialog.showErrorDialog(this, R.string.txt_empty_input);
            return;
        }
        String groupId = groups.get((int) pos).getIdentifier();

        final PHWizardAlertDialog dialogManager = PHWizardAlertDialog
                .getInstance();
        dialogManager.showProgressDialog(R.string.sending_progress,
                PHDeleteGroupActivity.this);
        // called delete API in bridge
        bridge.deleteGroup(groupId, new PHGroupListener() {

            @Override
            public void onSuccess() {
                dialogManager.closeProgressDialog();
                PHWizardAlertDialog.showResultDialog(
                        PHDeleteGroupActivity.this,
                        getString(R.string.txt_group_deleted), R.string.btn_ok,
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
                PHWizardAlertDialog.showErrorDialog(PHDeleteGroupActivity.this,
                        massage, R.string.btn_ok);
            }
        });
    }
}
