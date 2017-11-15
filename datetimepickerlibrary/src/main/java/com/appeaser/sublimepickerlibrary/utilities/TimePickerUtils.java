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
