package com.appeaser.sublimepickerlibrary.utilities;

import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * Created by Mirek on 07.11.2017.
 */

public class TimePickerUtils {

    public static final int HOURS_12 = 12;
    public static final int HOURS_24 = 24;

    public static ArrayList<Integer> getHoursToCheck(final int startHour, final int endHour) {
        ArrayList<Integer> hoursToCheck;
        int hoursToCheckCount = 0;
        if (startHour > HOURS_12) {
            hoursToCheckCount = calculateHoursToCheckCount(startHour, endHour, HOURS_24, hoursToCheckCount);
            hoursToCheck = addHoursToCheck(startHour, HOURS_24, hoursToCheckCount);
        } else if (endHour > HOURS_12) {
            hoursToCheckCount = endHour - startHour + 1;
            hoursToCheck = addHoursToCheck(startHour, HOURS_24, hoursToCheckCount);
        } else {
            hoursToCheckCount = 0;
            hoursToCheckCount = calculateHoursToCheckCount(startHour, endHour, HOURS_12, hoursToCheckCount);
            hoursToCheck = addHoursToCheck(startHour, HOURS_12, hoursToCheckCount);
        }
        return hoursToCheck;
    }

    private static int calculateHoursToCheckCount(int startHour, int endHour, int hoursCount,
                                                  int hoursToCheckCount) {
        if (startHour <= HOURS_24 && startHour >= HOURS_12
                && endHour >= HOURS_12 && endHour <= HOURS_24 && endHour < startHour) {
            hoursToCheckCount = HOURS_24 - startHour + HOURS_12 + (endHour - HOURS_12) + 1;
        } else {
            if (endHour < startHour) {
                if (endHour <= hoursCount) {
                    hoursToCheckCount = endHour + hoursCount - startHour + 1;
                }
            } else if (endHour > startHour) {
                hoursToCheckCount = endHour - startHour + 1;
            }
        }
        return hoursToCheckCount;
    }

    @NonNull
    private static ArrayList<Integer> addHoursToCheck(int startHour, int hoursCount,
                                                      int hoursToCheckCount) {
        ArrayList<Integer> hoursToCheck;
        hoursToCheck = new ArrayList<>(hoursToCheckCount);
        if (startHour <= hoursCount && startHour >= 1) {
            hoursToCheck.add(startHour);
            for (int i = 1; i < hoursToCheckCount; i++) {
                int nextHourToAdd = startHour + i;
                if (nextHourToAdd > hoursCount) {
                    nextHourToAdd %= hoursCount;
                }
                hoursToCheck.add(nextHourToAdd);
            }
        }
        return hoursToCheck;
    }

    public static float moveByAngle(Float angleToMove, float sweepAngle) {
        float degreesSum = angleToMove + sweepAngle;
        return degreesSum <= 360 ? degreesSum : sweepAngle - (360 - angleToMove);
    }

    public static boolean isAngleBetweenAngles(float degrees, float startDrawingAngle, float endDrawingAngle) {
        return degrees >= startDrawingAngle && degrees <= endDrawingAngle ||
                degrees <= endDrawingAngle && endDrawingAngle < startDrawingAngle ||
                degrees >= startDrawingAngle && endDrawingAngle < startDrawingAngle;
    }
}
