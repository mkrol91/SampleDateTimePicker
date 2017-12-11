package com.appeaser.sublimepickerlibrary.datepicker;

import java.io.Serializable;

/**
 * Created by Piotr Galbas on 2017-12-07.
 */
public class RentalSpan implements Serializable {
    public static final float HALF_DAY = 0.5f;

    private float selectedSpan;
    private float maxSpan;

    public RentalSpan(float max) {
        selectedSpan = HALF_DAY;
        maxSpan = max;
    }

    public RentalSpan(float max, float selected) {
        selectedSpan = selected;
        maxSpan = max;
    }

    public RentalSpan(final RentalSpan days) {
        selectedSpan = days.selectedSpan;
        maxSpan = days.maxSpan;
    }

    public float getSelectedSpan() {
        return selectedSpan;
    }

    public float getMaxSpan() {
        return maxSpan;
    }

    public void setMaxSpan(final float maxSpan) {
        this.maxSpan = maxSpan;
        if (selectedSpan > maxSpan) {
            selectedSpan = maxSpan;
        }
    }

    public int getDaysDifference() {
        if (selectedSpan < 1f) {
            return 0;
        }
        return (int) (selectedSpan - 1);
    }

    public float decreaseSelection() {
        if (selectedSpan == 1f) {
            selectedSpan = HALF_DAY;
        } else if (selectedSpan > 1f) {
            selectedSpan--;
        }
        return selectedSpan;
    }

    public float increaseSelection() {
        if (selectedSpan == HALF_DAY && HALF_DAY < maxSpan) {
            selectedSpan = 1f;
        } else if (selectedSpan < maxSpan) {
            selectedSpan++;
        }
        return selectedSpan;
    }

    public boolean isMinSelected() {
        return selectedSpan == HALF_DAY;
    }

    public boolean isMaxSelected() {
        return selectedSpan == maxSpan;
    }
}
