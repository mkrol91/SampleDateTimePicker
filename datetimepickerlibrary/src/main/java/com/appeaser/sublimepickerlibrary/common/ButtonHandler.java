package com.appeaser.sublimepickerlibrary.common;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;

import com.appeaser.sublimepickerlibrary.SublimePicker;
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions;

/**
 * Created by Admin on 15/02/2016.
 */
public class ButtonHandler implements View.OnClickListener {

    // Can be 'android.widget.Button' or 'android.widget.ImageView'
    View mPositiveButtonDP, mPositiveButtonTP, mNegativeButtonDP, mNegativeButtonTP;
    // 'Button' used for switching between 'SublimeDatePicker'
    // and 'SublimeTimePicker'. Also displays the currently
    // selected date/time depending on the visible picker
    Button mSwitcherButtonDP, mSwitcherButtonTP;
    Callback mCallback;
    private ButtonLayout mPortraitButtonHandler;

    public ButtonHandler(@NonNull SublimePicker sublimePicker) {
        // Takes care of initialization
//        mPortraitButtonHandler = sublimePicker.findViewById(R.id.button_layout);
    }

    /**
     * Initializes state for this layout
     *
     * @param switcherRequired Whether the switcher button needs
     *                         to be shown.
     * @param callback         Callback to 'SublimePicker'
     */
    public void applyOptions(boolean switcherRequired, @NonNull Callback callback) {
        mCallback = callback;

        // Let ButtonLayout handle callbacks
        if (mPortraitButtonHandler != null) {
            mPortraitButtonHandler.applyOptions(switcherRequired, callback);
        }
    }

    // Returns whether switcher button is being used in this layout
    public boolean isSwitcherButtonEnabled() {
        return mPortraitButtonHandler != null && mPortraitButtonHandler.isSwitcherButtonEnabled();
    }

    // Used when the pickers are switched
    public void updateSwitcherText(@NonNull SublimeOptions.Picker displayedPicker, CharSequence text) {
        if (mPortraitButtonHandler != null) {
            mPortraitButtonHandler.updateSwitcherText(text);
        }
    }

    // Disables the positive button as and when the user selected options
    // become invalid.
    public void updateValidity(boolean valid) {
        if (mPortraitButtonHandler != null) {
            mPortraitButtonHandler.updateValidity(valid);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mPositiveButtonDP || v == mPositiveButtonTP) {
            mCallback.onOkay();
        } else if (v == mNegativeButtonDP || v == mNegativeButtonTP) {
            mCallback.onCancel();
        } else if (v == mSwitcherButtonDP || v == mSwitcherButtonTP) {
            mCallback.onSwitch();
        }
    }

    public interface Callback {
        void onOkay();

        void onCancel();

        void onSwitch();

        void onSwitch(SublimeOptions.Picker picker);
    }
}
