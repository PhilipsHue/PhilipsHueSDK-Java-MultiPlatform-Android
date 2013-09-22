package com.philips.lighting.hue.local.sdk.demo;

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
 * @author Pallavi P. Ganorkar
 */
public final class PHWizardAlertDialog {

    private ProgressDialog pdialog;
    private static PHWizardAlertDialog dialogs;

    /**
     * The default constructor for {@link PHWizardAlertDialog} class.
     */
    private PHWizardAlertDialog() {

    }

    /**
     * Gives {@link PHWizardAlertDialog} class object.
     * 
     * @return the {@link PHWizardAlertDialog} object.
     */
    public static synchronized PHWizardAlertDialog getInstance() {
        if (dialogs == null) {
            dialogs = new PHWizardAlertDialog();
        }
        return dialogs;
    }

    /**
     * Dialog to show error.
     * 
     * @param activityContext
     *            the parent Activity context.
     * @param resID
     *            the String resource id to display message.
     */
    public static void showErrorDialog(Context activityContext, int resID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityContext);
        builder.setTitle(R.string.title_error)
                .setMessage(activityContext.getString(resID))
                .setPositiveButton(R.string.btn_ok, null);
        AlertDialog alert = builder.create();
        alert.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alert.show();
    }

    /**
     * Dialog to show error with custom String on button.
     * 
     * @param activityContext
     *            the parent Activity context.
     * @param message
     *            the message to display.
     * @param btnNameResId
     *            the String resource id to display text on button.
     */
    public static void showErrorDialog(Context activityContext, String message,
            int btnNameResId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityContext);
        builder.setTitle(R.string.title_error).setMessage(message)
                .setPositiveButton(btnNameResId, null);
        AlertDialog alert = builder.create();
        alert.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alert.show();
    }

    /**
     * Stops running progress-bar.
     */
    public void closeProgressDialog() {

        if (pdialog != null) {
            pdialog.dismiss();
            pdialog = null;
        }
    }

    /**
     * Shows progress-bar.
     * 
     * @param resID
     *            the String resource id to display message on progress bar.
     * @param ctx
     *            the parent Activity context.
     */
    public void showProgressDialog(int resID, Context ctx) {
        String message = ctx.getString(resID);
        pdialog = ProgressDialog.show(ctx, null, message, true, true);
        pdialog.setCancelable(false);

    }

    /**
     * Dialog to show authentication error.
     * 
     * @param activityContext
     *            the parent Activity context.
     * @param message
     *            the message to display on dialog.
     * @param btnNameResId
     *            the String resource id to display message on Button.
     */
    public static void showAuthenticationErrorDialog(
            final Activity activityContext, String message, int btnNameResId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityContext);
        builder.setTitle(R.string.title_error).setMessage(message)
                .setPositiveButton(btnNameResId, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activityContext.finish();

                    }
                });
        AlertDialog alert = builder.create();
        alert.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alert.show();
    }

    /**
     * Dialog to show result.
     * 
     * @param activityContext
     *            the parent Activity context.
     * @param message
     *            the message to display on dialog.
     * @param btnNameResId
     *            the String resource id to display message on Button.
     * @param txtResult
     *            the String resource id to display title.
     */
    public static void showResultDialog(final Activity activityContext,
            String message, int btnNameResId, int txtResult) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityContext);
        builder.setTitle(txtResult).setMessage(message)
                .setPositiveButton(btnNameResId, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activityContext.finish();

                    }
                });
        AlertDialog alert = builder.create();
        alert.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alert.show();
    }

    /**
     * Dialog to show message.
     * 
     * @param nMessage
     *            the message to display on dialog.
     * @param resId
     *            the String resource id to display title.
     * @param context
     *            the parent Activity context.
     */
    public static void showMessageDialog(String nMessage, int resId,
            Context context) {

        AlertDialog.Builder hueAlerts = new AlertDialog.Builder(context);
        hueAlerts.setTitle(resId);
        hueAlerts.setMessage(nMessage);
        hueAlerts.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        hueAlerts.setCancelable(false);
        hueAlerts.show();
    }

}
