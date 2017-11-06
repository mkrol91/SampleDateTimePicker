package concisesoftware.com.datetimepicker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.appeaser.sublimepickerlibrary.SublimePicker;
import com.appeaser.sublimepickerlibrary.common.ButtonHandler;
import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.appeaser.sublimepickerlibrary.helpers.SublimeListenerAdapter;
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SublimePicker picker;
    private LinearLayout timeButtonDate;
    private LinearLayout dateButtonDate;
    private LinearLayout timeButtonTimer;
    private LinearLayout dateButtonTimer;

    private SublimeListenerAdapter pickerListner = new SublimeListenerAdapter() {
        @Override
        public void onDateTimeRecurrenceSet(SublimePicker sublimeMaterialPicker, SelectedDate selectedDate, int hourOfDay, int minute, SublimeRecurrencePicker.RecurrenceOption recurrenceOption, String recurrenceRule) {

        }

        @Override
        public void onCancelled() {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        picker = findViewById(R.id.picker);
        timeButtonDate = picker.findViewById(R.id.time_button_date);
        dateButtonDate = picker.findViewById(R.id.date_button_date);
        timeButtonTimer = picker.findViewById(R.id.time_button_timer);
        dateButtonTimer = picker.findViewById(R.id.date_button_timer);

        SublimeOptions options = new SublimeOptions();

        Calendar calendar = Calendar.getInstance();
        int currentHourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        options.setTimeParams(currentHourOfDay, currentMinute, true);
        options.setDateParams(calendar);

        int displayOptions = 0;
        displayOptions |= SublimeOptions.ACTIVATE_TIME_PICKER;
        displayOptions |= SublimeOptions.ACTIVATE_DATE_PICKER;

        options.setPickerToShow(SublimeOptions.Picker.TIME_PICKER);
        options.setDisplayOptions(displayOptions);
        picker.initializePicker(options, pickerListner);

        timeButtonDate.setOnClickListener(this);
        dateButtonDate.setOnClickListener(this);
        timeButtonTimer.setOnClickListener(this);
        dateButtonTimer.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        ButtonHandler.Callback callback = picker.getmButtonLayoutCallback();
        if (picker != null && callback != null) {
            if (v.getId() == R.id.time_button_timer || v.getId() == R.id.time_button_date) {
                picker.getmButtonLayoutCallback().onSwitch(SublimeOptions.Picker.TIME_PICKER);
            } else if (v.getId() == R.id.date_button_timer || v.getId() == R.id.date_button_date) {
                picker.getmButtonLayoutCallback().onSwitch(SublimeOptions.Picker.DATE_PICKER);
            }
        }
    }
}
