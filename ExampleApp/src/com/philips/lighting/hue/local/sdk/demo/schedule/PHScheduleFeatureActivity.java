package com.philips.lighting.hue.local.sdk.demo.schedule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.philips.lighting.hue.local.sdk.demo.PHHomeActivity;
import com.philips.lighting.hue.local.sdk.demo.R;

/**
 * Lists all Schedule features found in SDK
 * 
 * @author Pallavi P. Ganorkar.
 */
public class PHScheduleFeatureActivity extends Activity implements
        OnItemClickListener {

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

        ListView scheduleFeatureListView = (ListView) findViewById(R.id.list_items);
        scheduleFeatureListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        scheduleFeatureListView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getResources()
                        .getStringArray(R.array.schedule_features)));

        scheduleFeatureListView.setOnItemClickListener(this);

    }

    /**
     * Callback method to be invoked when an item in this AdapterView has been
     * clicked.
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
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
            long id) {

        Intent intent = null;
        switch (position) {
        case 0:
            intent = new Intent(this,
                    PHNonRecurringScheduleFeaturesActivity.class);
            break;
        case 1:
            intent = new Intent(this, PHRecurringScheduleFeaturesActivity.class);
            break;
        case 2:
            intent = new Intent(this, PHTimerFeaturesActivity.class);
            break;
        case 3:
            intent = new Intent(this, PHRecurringTimerFeaturesActivity.class);
            break;
        default:
            break;
        }
        if (intent != null) {
            startActivity(intent);
        }
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
        case android.R.id.home:
            Intent intent = new Intent(this, PHHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            break;
        }
        return true;

    }
}
