package com.philips.lighting.hue.sdk.wizard.helper;

import android.widget.Spinner;

/**
 * Helper class for application code
 * 
 * @author Pallavi P. Ganorkar.
 */
public class PHHelper {

    /**
     * Returns String format of integer with padding of 2 digits.
     * 
     * @param number
     *            the number to format
     * @return the String format of the given number.
     */
    public static String pad(int c) {
        if (c >= 10) {
            return String.valueOf(c);
        } else {
            return "0" + c;
        }
    }

    /**
     * Returns index (starting at 0) of the myString in data item of spinner.
     * 
     * @param spinner
     *            the Spinner instance.
     * @param myString
     *            the string to find index.
     * @return index of myString in data item of spinner. if myString not found
     *         returns index 0.
     */
    public static int getIndex(Spinner spinner, String myString) {

        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(myString)) {
                index = i;
            }
        }
        return index;
    }
}
