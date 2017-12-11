package com.appeaser.sublimepickerlibrary.helpers;

import java.util.Calendar;

/**
 * Created by Mirosław Juda on 08.12.2017.
 */

public interface SublimeDateTimeChangeListener {
    void onTimeChanged(int hour, int minute);

    void onDateChanged(Calendar date, Float rentalSpan);

    void onRentalSpanChanged(Float rentalSpan);
}
