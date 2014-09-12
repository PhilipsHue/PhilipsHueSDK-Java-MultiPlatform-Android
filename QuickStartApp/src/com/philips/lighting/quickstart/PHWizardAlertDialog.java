package com.philips.lighting.quickstart;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.WindowManager;

/**
 * Generic class for Alert and Progress dialogs wizard
 * 
 * 
 */

public final class PHWizardAlertDialog {

    private ProgressDialog pdialog;
    private static PHWizardAlertDialog dialogs;

    private PHWizardAlertDialog() {

    }

    public static synchronized PHWizardAlertDialog getInstance() {
        if (dialogs == null) {
            dialogs = new PHWizardAlertDialog();
        }
        return dialogs;
    }

    /**
     * 
     * @param activityContext
     * @param resID
     * @param btnNameResId  String resource id for button name
     */
    public static void showErrorDialog(Context activityContext, String msg, int btnNameResId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityContext);
        builder.setTitle(R.string.title_error).setMessage(msg).setPositiveButton(btnNameResId, null);
        AlertDialog alert = builder.create();
        alert.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        if (! ((Activity) activityContext).isFinishing()) {
            alert.show();
        }
       
    }

    /**
     * Stops running progress-bar
     */
    public void closeProgressDialog() {

        if (pdialog != null) {
            pdialog.dismiss();
            pdialog = null;
        }
    }

    /**
     * Shows progress-bar
     * 
     * @param resID
     * @param act
     */
    public void showProgressDialog(int resID, Context ctx) {
        String message = ctx.getString(resID);
        pdialog = ProgressDialog.show(ctx, null, message, true, true);
        pdialog.setCancelable(false);

    }

    /**
     * 
     * @param activityContext
     * @param msg
     * @param btnNameResId
     */
    public static void showAuthenticationErrorDialog(
            final Activity activityContext, String msg, int btnNameResId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityContext);
        builder.setTitle(R.string.title_error).setMessage(msg)
                .setPositiveButton(btnNameResId, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activityContext.finish();

                    }
                });
        AlertDialog alert = builder.create();
        alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alert.show();
    }

}
