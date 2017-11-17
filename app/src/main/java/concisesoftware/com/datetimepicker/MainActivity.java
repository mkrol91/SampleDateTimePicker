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

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SublimePicker picker;
    private LinearLayout timeButtonDate;
    private LinearLayout dateButtonDate;

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
        timeButtonDate = picker.findViewById(R.id.tab_button_time);
        dateButtonDate = picker.findViewById(R.id.tab_button_date);

        SublimeOptions options = new SublimeOptions();

        Calendar startCal = Calendar.getInstance();
        int currentHourOfDay = startCal.get(Calendar.HOUR_OF_DAY);
        int currentMinute = startCal.get(Calendar.MINUTE);

        Calendar endCal = Calendar.getInstance();
        endCal.add(Calendar.YEAR, 1);

        options.setTimeParams(currentHourOfDay, currentMinute, true);
        options.setDateRange(startCal.getTimeInMillis(), endCal.getTimeInMillis());
        options.setDisabledDays(getDisabledDays());
        options.setSubsequentDays(SublimeOptions.RentalSpan.TWO_DAYS);

        int displayOptions = 0;
        displayOptions |= SublimeOptions.ACTIVATE_TIME_PICKER;
        displayOptions |= SublimeOptions.ACTIVATE_DATE_PICKER;

        options.setPickerToShow(SublimeOptions.Picker.TIME_PICKER);
        options.setDisplayOptions(displayOptions);
        picker.initializePicker(options, pickerListner);

        timeButtonDate.setOnClickListener(this);
        dateButtonDate.setOnClickListener(this);
    }

    private ArrayList<Calendar> getDisabledDays() {
        ArrayList<Calendar> cals = new ArrayList<>();
        Calendar date1 = Calendar.getInstance();
        date1.clear();
        date1.set(2017,10,17);
        cals.add(date1);
        Calendar date2 = Calendar.getInstance();
        date2.clear();
        date2.set(2017,10,18);
        cals.add(date2);
        Calendar date3 = Calendar.getInstance();
        date3.clear();
        date3.set(2017,10,27);
        cals.add(date3);
        Calendar date4 = Calendar.getInstance();
        date4.clear();
        date4.set(2017,11,1);
        cals.add(date4);
        return cals;
    }

    @Override
    public void onClick(View v) {
        ButtonHandler.Callback callback = picker.getmButtonLayoutCallback();
        if (picker != null && callback != null) {
            if (v.getId() == R.id.tab_button_time) {
                picker.getmButtonLayoutCallback().onSwitch(SublimeOptions.Picker.TIME_PICKER);
            } else if (v.getId() == R.id.tab_button_date) {
                picker.getmButtonLayoutCallback().onSwitch(SublimeOptions.Picker.DATE_PICKER);
            }
        }
    }
}
