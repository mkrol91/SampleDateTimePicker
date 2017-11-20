package com.appeaser.sublimepickerlibrary.utilities;

import android.support.annotation.NonNull;

import com.appeaser.sublimepickerlibrary.timepicker.RadialTimePickerView;

import java.util.ArrayList;

/**
 * Created by Mirek on 07.11.2017.
 */

public class TimePickerUtils {

    public static final int HOURS_12 = 12;
    public static final int HOURS_24 = 24;
    public static final int FULL_ANGLE = 360;

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
        return degreesSum <= FULL_ANGLE ? degreesSum : sweepAngle - (FULL_ANGLE - angleToMove);
    }

    public static boolean isAngleBetweenAngles(float degrees, float startDrawingAngle, float endDrawingAngle) {
        return degrees >= startDrawingAngle && degrees <= endDrawingAngle ||
                degrees <= endDrawingAngle && endDrawingAngle < startDrawingAngle ||
                degrees >= startDrawingAngle && endDrawingAngle < startDrawingAngle;
    }

    public static ArrayList<Float> generateTimerStartArcAngles(int unitsCount, float unitWidth) {
        ArrayList<Float> startArcAngles = new ArrayList<>(unitsCount);
        for (int i = 0; i < unitsCount; i++) {
            float angle = i * unitWidth;
            if (i == 0) {
                startArcAngles.add(FULL_ANGLE - 2 * unitWidth);
            } else if (i == 1) {
                startArcAngles.add(FULL_ANGLE - unitWidth);
            } else {
                startArcAngles.add(angle - 2 * unitWidth);
            }
        }
        return startArcAngles;
    }


    public static ArrayList<RadialTimePickerView.TimerSection> generateTimerSections(ArrayList<Float> startArcAngles) {
        ArrayList<RadialTimePickerView.TimerSection> timerSections = new ArrayList<>(startArcAngles.size() / 4);
        int hoursCount = HOURS_12;
        for (int i = 0; i < startArcAngles.size(); i++) {
            if (i % 4 == 0 && (i + 3) < startArcAngles.size()) {
                RadialTimePickerView.TimerSection timerSection = new RadialTimePickerView.TimerSection();
                timerSection.setHour(hoursCount);
                timerSection.setFirstPartAngle(startArcAngles.get(i));
                timerSection.setSecondPartAngle(startArcAngles.get(i + 1));
                timerSection.setThirdPartAngle(startArcAngles.get(i + 2));
                timerSection.setFourthPartAngle(startArcAngles.get(i + 3));
                timerSections.add(timerSection);
                hoursCount--;
            }
        }
        return timerSections;
    }
}
