package com.appeaser.sublimepickerlibrary.utilities;

import java.util.ArrayList;

/**
 * Created by Mirek on 07.11.2017.
 */

public class TimePickerUtils {
    public static ArrayList<Integer> getHoursToCheck(final int startHour, final int endHour) {
        final int hoursCount = 12;
        int hoursToCheckCount = 0;
        if (endHour < startHour) {
            if (endHour <= hoursCount) {
                hoursToCheckCount = endHour + hoursCount - startHour + 1;
            }
        } else if (endHour > startHour) {
            hoursToCheckCount = endHour - startHour + 1;
        }
        ArrayList<Integer> hoursToCheck = new ArrayList<>(hoursToCheckCount);
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

    public static float getDegreesWithOffsetToDrawing(float degrees, float offsetInZeroDegree) {
        float degreesOffsetSum = degrees + offsetInZeroDegree;
        if (degreesOffsetSum <= 360) {
            return degreesOffsetSum;
        } else {
            return offsetInZeroDegree - (360 - degrees);
        }
    }

    public static float moveByAngle(Float angleToMove, float sweepAngle) {
        float degreesSum = angleToMove + sweepAngle;
        if (degreesSum <= 360) {
            return degreesSum;
        } else {
            return sweepAngle - (360 - angleToMove);
        }
    }
}
