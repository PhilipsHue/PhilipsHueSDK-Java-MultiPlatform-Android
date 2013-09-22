package com.philips.lighting.hue.sdk.wizard.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.philips.lighting.hue.local.sdk.demo.PHWizardAlertDialog;
import com.philips.lighting.hue.local.sdk.demo.R;

/**
 * Adapter to bind data to the list with toggling button.
 * 
 * @author Pallavi P. Ganorkar
 * 
 */

public class PHBridgeToggleAdapter extends ArrayAdapter<String> {
    private String[] toggleOptions;
    private String[] toggleValues;
    private int selectedIndex = -1;
    private LayoutInflater mInflater;

    /**
     * Constructs Adapter object
     * 
     * @param context
     *            the activity context
     * @param listitemview
     *            the list item view
     * @param values
     *            array of values
     * @param options
     *            array of options
     */
    public PHBridgeToggleAdapter(Context context, int listitemview,
            String[] values, String[] options) {
        super(context, listitemview, values);

        toggleOptions = options;
        toggleValues = values;
        mInflater = LayoutInflater.from(context);
    }

    /**
     * Get a View that displays the data at the specified position in the data
     * set.
     * 
     * @param position
     *            the row index
     * @param convertView
     *            the row view
     * @param parent
     *            the view group @ returns A View corresponding to the data at
     *            the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ConfigOptionHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.mybridge_option_row, null);

            holder = new ConfigOptionHolder();

            holder.txtTitle = (TextView) convertView
                    .findViewById(R.id.txt_title);
            holder.txtDesc = (TextView) convertView
                    .findViewById(R.id.txt_description);
            convertView.setTag(holder);
        } else {
            holder = (ConfigOptionHolder) convertView.getTag();
        }

        holder.txtTitle.setText(toggleOptions[position]);
        holder.txtDesc.setText(toggleValues[position]);
        holder.txtTitle.setTag(toggleOptions[position]);
        holder.txtDesc.setTag(toggleValues[position]);
        if (position == selectedIndex) {
            convertView.setSelected(true);
        } else {
            convertView.setSelected(false);
        }

        return convertView;
    }

    /**
     * Defines action for on click of an item
     * 
     * @param view
     *            the selected view
     * @param position
     *            the position clicked
     */
    public void setOnClick(View view, int position) {
        ConfigOptionHolder listItem = (ConfigOptionHolder) view.getTag();

        createChangeTextDialog(view.getContext(), listItem, position);
    }

    /**
     * Get the data item associated with the specified position in the data set.
     * position
     * 
     * @param position
     *            the row index
     * @return the object at row index
     */
    @Override
    public String getItem(int position) {
        return toggleValues[position];
    }

    /**
     * The row view holder
     * 
     */
    static class ConfigOptionHolder {
        private TextView txtTitle;
        private TextView txtDesc;
    }

    /**
     * Dialog to change DHCP & HTTP-Proxy setting
     * 
     * @param activityContext
     *            the activity context
     * @param itemview
     *            the item view clicked
     * @param position
     *            the position selected
     */
    public void createChangeTextDialog(final Context activityContext,
            final ConfigOptionHolder itemview, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityContext);

        builder.setTitle(itemview.txtTitle.getText());
        final PHClearableEditText editText = new PHClearableEditText(
                activityContext);
        final TextView txtDesc = (TextView) itemview.txtDesc;
        editText.getEditText().setText(txtDesc.getText());
        editText.getEditText().requestFocus();
        editText.getEditText().setSelection(txtDesc.getText().length());

        builder.setView(editText)
                .setNegativeButton(R.string.btn_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                final String nameNew = editText.getEditText()
                                        .getText().toString().trim();
                                if (nameNew.length() == 0) {
                                    PHWizardAlertDialog.showErrorDialog(
                                            activityContext,
                                            R.string.emptystringerror);
                                    return;
                                }
                                dialog.cancel();
                                txtDesc.setText(nameNew);
                                toggleValues[position] = nameNew;
                            }
                        })
                .setPositiveButton(R.string.btn_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();

                            }
                        });
        AlertDialog alert = builder.create();
        alert.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alert.show();
    }
}
