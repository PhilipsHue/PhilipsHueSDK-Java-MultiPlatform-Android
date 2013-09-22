package com.philips.lighting.hue.sdk.wizard.helper;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.philips.lighting.hue.local.sdk.demo.R;

/**
 * Class to provide custom Edit Text
 * 
 * @author Pallavi P. Ganorkar
 * 
 */

public class PHClearableEditText extends RelativeLayout {
    private EditText editText;
    private Button btnClear;

    public PHClearableEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews();
    }

    public PHClearableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public PHClearableEditText(Context context) {
        super(context);
        initViews();
    }

    /**
     * Initialize views
     */
    void initViews() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.clearable_edit_text, this, true);
        editText = (EditText) findViewById(R.id.clearable_edit);
        btnClear = (Button) findViewById(R.id.clearable_button_clear);
        btnClear.setVisibility(RelativeLayout.INVISIBLE);
        clearText();
        showHideClearButton();
    }

    /**
     * Clear text
     */
    void clearText() {
        btnClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });
    }

    /**
     * Shows or hides clear button based text length is greater than 0
     */
    void showHideClearButton() {
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
                if (s.length() > 0) {
                    btnClear.setVisibility(RelativeLayout.VISIBLE);
                } else {
                    btnClear.setVisibility(RelativeLayout.INVISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public Editable getText() {
        return editText.getText();
    }

    public EditText getEditText() {
        return editText;
    }

    public void setFilters(InputFilter[] filterArray) {
        editText.setFilters(filterArray);
    }

}