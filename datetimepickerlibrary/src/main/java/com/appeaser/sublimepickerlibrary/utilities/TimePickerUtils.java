package com.appeaser.sublimepickerlibrary.utilities;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

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


    public static ArrayList<RadialTimePickerView.TimerSection> generateTimerSections(ArrayList<Float> startArcAngles, boolean isPm) {
        ArrayList<RadialTimePickerView.TimerSection> timerSections = new ArrayList<>(startArcAngles.size() / 4);
        int hour = 0;
        for (int i = 0; i < startArcAngles.size(); i++) {
            ArrayList<Float> sectionsStartAngles = new ArrayList<>();
            if (i % 4 == 0 && (i + 3) < startArcAngles.size()) {
                RadialTimePickerView.TimerSection timerSection = new RadialTimePickerView.TimerSection();

                for (int j = i; j < i + 4; j++) {
                    sectionsStartAngles.add(startArcAngles.get(j));
                }
                timerSection.setSectionStartAngles(sectionsStartAngles);

                if (i == 0) {
                    timerSection.setHour(isPm ? HOURS_12 + 12 : HOURS_12);
                } else {
                    timerSection.setHour(isPm ? ++hour + 12 : ++hour);
                }
                timerSections.add(timerSection);
            }
        }
        return timerSections;
    }

    public static RadialTimePickerView.TimerSection findSectionForDegrees(@NonNull ArrayList<RadialTimePickerView.TimerSection> timerSections,
                                                                          int degrees) {
        for (RadialTimePickerView.TimerSection timerSection : timerSections) {
            ArrayList<Float> sectionStartAngles = timerSection.getSectionStartAngles();

            Float startAngle = sectionStartAngles.get(0);
            Float endAngle = sectionStartAngles.get(3) + 7.5f;

            if ((degrees >= startAngle && degrees <= endAngle)) {
                return timerSection;
            }

            if ((degrees >= startAngle && degrees <= 360 && endAngle < startAngle) ||
                    (degrees > 0 && degrees <= endAngle && endAngle < startAngle)) {
                return timerSection;
            }

        }
        return null;
    }

    public static boolean isDegreeCloserToStartDegree(int degrees, float startDegree, float endDegree) {
        float distanceToStartDegrees = 0;
        float distanceToEndDegrees = 0;
        distanceToStartDegrees = degrees - startDegree;
        if (endDegree > startDegree) {
            distanceToEndDegrees = endDegree - degrees;
        } else if (endDegree < startDegree) {
            distanceToEndDegrees = 360 - degrees;
        }
        return distanceToStartDegrees < distanceToEndDegrees;
    }

    public static float findStartAngleOfSectionWhichContainsDegree(int degrees,
                                                                   RadialTimePickerView.TimerSection sectionForDegrees) {
        ArrayList<Float> sectionStartAngles = sectionForDegrees.getSectionStartAngles();
        if (sectionStartAngles != null) {
            for (Float startAngle : sectionStartAngles) {
                if (degrees >= startAngle && degrees <= startAngle + 7.5f) {
                    return startAngle;
                }
            }
        }
        return 0;
    }

    public static int findUnasignedQuarterOfSectionWhichContainsDegree(int degrees,
                                                                       RadialTimePickerView.TimerSection sectionForDegrees) {
        ArrayList<Float> sectionStartAngles = sectionForDegrees.getSectionStartAngles();
        if (sectionStartAngles != null) {
            for (int i = 0; i < sectionStartAngles.size(); i++) {
                Float angle = sectionStartAngles.get(i);
                if (degrees >= angle && degrees <= angle + 7.5f) {
                    return i + 1;
                }
            }
        }
        return -1;
    }

    public static Pair<Integer, Integer> mapToTimeAsPair(int hour, int unassignedQuarter, boolean isDegreesCloserToStartDegree) {
        if (unassignedQuarter == 1) {
            return isDegreesCloserToStartDegree ? new Pair<>(hour - 1, 30) : new Pair<>(hour - 1, 45);
        } else if (unassignedQuarter == 2) {
            return isDegreesCloserToStartDegree ? new Pair<>(hour - 1, 45) : new Pair<>(hour, 0);
        } else if (unassignedQuarter == 3) {
            return isDegreesCloserToStartDegree ? new Pair<>(hour, 0) : new Pair<>(hour, 15);
        } else if (unassignedQuarter == 4) {
            return isDegreesCloserToStartDegree ? new Pair<>(hour, 15) : new Pair<>(hour, 30);
        }
        return null;
    }
}
