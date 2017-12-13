package com.appeaser.sublimepickerlibrary.utilities;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.appeaser.sublimepickerlibrary.timepicker.RadialTimePickerView;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import static com.appeaser.sublimepickerlibrary.timepicker.RadialTimePickerView.UNITS_COUNT;
import static com.appeaser.sublimepickerlibrary.timepicker.RadialTimePickerView.UNIT_WIDTH;
import static com.appeaser.sublimepickerlibrary.utilities.Quarter.Q0;
import static com.appeaser.sublimepickerlibrary.utilities.Quarter.Q15;
import static com.appeaser.sublimepickerlibrary.utilities.Quarter.Q30;
import static com.appeaser.sublimepickerlibrary.utilities.Quarter.Q45;


/**
 * Created by Mirek on 07.11.2017.
 */

public class TimePickerUtils {

    public static final int HOURS_12 = 12;
    public static final int HOURS_24 = 24;
    public static final int FULL_ANGLE = 360;
    public static final int MINUTES_60 = 60;
    public static final int ANGLE_90 = 90;
    public static final int QUARTERS_COUNT = 4;

    public static float moveByAngle(Float angleToMove, float sweepAngle) {
        float degreesSum = angleToMove + sweepAngle;
        return degreesSum <= FULL_ANGLE ? degreesSum : sweepAngle - (FULL_ANGLE - angleToMove);
    }

    public static boolean isAngleBetweenAngles(float degrees, float startDrawingAngle, float endDrawingAngle) {
        return degrees >= startDrawingAngle && degrees <= endDrawingAngle ||
                degrees <= endDrawingAngle && endDrawingAngle < startDrawingAngle ||
                degrees >= startDrawingAngle && endDrawingAngle < startDrawingAngle;
    }

    public static ArrayList<Float> generateTimerStartArcAngles() {
        ArrayList<Float> startArcAngles = new ArrayList<>(UNITS_COUNT);
        for (int i = 0; i < UNITS_COUNT; i++) {
            float angle = i * UNIT_WIDTH;
            if (i == 0) {
                startArcAngles.add(FULL_ANGLE - 2 * UNIT_WIDTH);
            } else if (i == 1) {
                startArcAngles.add(FULL_ANGLE - UNIT_WIDTH);
            } else {
                startArcAngles.add(angle - 2 * UNIT_WIDTH);
            }
        }
        return startArcAngles;
    }


    public static ArrayList<RadialTimePickerView.TimerSection> generateTimerSections(ArrayList<Float> startArcAngles, boolean isPm) {
        ArrayList<RadialTimePickerView.TimerSection> timerSections = new ArrayList<>(startArcAngles.size() / QUARTERS_COUNT);
        int hour = 0;
        for (int i = 0; i < startArcAngles.size(); i++) {
            ArrayList<Float> sectionsStartAngles = new ArrayList<>();
            if (i % QUARTERS_COUNT == 0 && (i + 3) < startArcAngles.size()) {
                RadialTimePickerView.TimerSection timerSection = new RadialTimePickerView.TimerSection();

                for (int j = i; j < i + QUARTERS_COUNT; j++) {
                    sectionsStartAngles.add(startArcAngles.get(j));
                }
                timerSection.setSectionStartAngles(sectionsStartAngles);

                if (i == 0) {
                    timerSection.setHour(isPm ? HOURS_12 : 0);
                } else {
                    timerSection.setHour(isPm ? ++hour + HOURS_12 : ++hour);
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
            Float endAngle = sectionStartAngles.get(3) + UNIT_WIDTH;

            if ((degrees >= startAngle && degrees <= endAngle)) {
                return timerSection;
            }

            if ((degrees >= startAngle && degrees <= FULL_ANGLE && endAngle < startAngle) ||
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
            distanceToEndDegrees = FULL_ANGLE - degrees;
        }
        return distanceToStartDegrees < distanceToEndDegrees;
    }

    public static float findStartAngleOfSectionWhichContainsDegree(int degrees,
                                                                   RadialTimePickerView.TimerSection sectionForDegrees) {
        ArrayList<Float> sectionStartAngles = sectionForDegrees.getSectionStartAngles();
        if (sectionStartAngles != null) {
            for (Float startAngle : sectionStartAngles) {
                if (degrees >= startAngle && degrees <= startAngle + UNIT_WIDTH) {
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
                if (degrees >= angle && degrees <= angle + UNIT_WIDTH) {
                    return i + 1;
                }
            }
        }
        return -1;
    }

    public static Pair<Integer, Integer> mapToTimeAsPair(int hour, int unassignedQuarter, boolean isDegreesCloserToStartDegree, boolean isPm) {
        if (unassignedQuarter == 1) {
            if (hour == 0) {
                return isDegreesCloserToStartDegree ? new Pair<>(HOURS_12 - 1, Q30) : new Pair<>(HOURS_12 - 1, Q45);
            } else if (hour == HOURS_12) {
                return isDegreesCloserToStartDegree ? new Pair<>(HOURS_24 - 1, Q30) : new Pair<>(HOURS_24 - 1, Q45);
            }
            return isDegreesCloserToStartDegree ? new Pair<>(hour - 1, Q30) : new Pair<>(hour - 1, Q45);
        } else if (unassignedQuarter == 2) {
            if (hour == 0) {
                return isDegreesCloserToStartDegree ? new Pair<>(HOURS_12 - 1, Q45) : new Pair<>(0, 0);
            } else if (hour == HOURS_12) {
                return isDegreesCloserToStartDegree ? new Pair<>(HOURS_24 - 1, Q45) : new Pair<>(HOURS_12, 0);
            }
            return isDegreesCloserToStartDegree ? new Pair<>(hour - 1, Q45) : new Pair<>(hour, 0);
        } else if (unassignedQuarter == 3) {
            return isDegreesCloserToStartDegree ? new Pair<>(hour, Q0) : new Pair<>(hour, Q15);
        } else if (unassignedQuarter == QUARTERS_COUNT) {
            if (hour == 0) {
                return isDegreesCloserToStartDegree ? new Pair<>(hour, Q15) : new Pair<>(hour, Q30);
            } else if (hour == HOURS_12) {
                return isDegreesCloserToStartDegree ? new Pair<>(HOURS_12, Q15) : new Pair<>(HOURS_12, Q30);
            }
            return isDegreesCloserToStartDegree ? new Pair<>(hour, Q15) : new Pair<>(hour, Q30);
        }
        return null;
    }

    public static RadialTimePickerView.TimerSection findSectionForHour(int hour, ArrayList<RadialTimePickerView.TimerSection> timerSections) {
        if (timerSections != null && timerSections.size() != 0) {
            for (RadialTimePickerView.TimerSection timerSection : timerSections) {
                if (timerSection.getHour() == hour || (timerSection.getHour() == HOURS_12 && hour == 0)
                        || (timerSection.getHour() == HOURS_24 && hour == HOURS_12)) {
                    return timerSection;
                }
            }
        }
        return null;
    }

    public static float findAngleForGivenMinutesAndHours(int minutes,
                                                         RadialTimePickerView.TimerSection section) {
        if (section != null) {
            switch (minutes) {
                case Q0:
                    return section.getSectionStartAngles().get(2);
                case Q15:
                    return section.getSectionStartAngles().get(3);
                case Q30:
                    return section.getSectionStartAngles().get(3) + UNIT_WIDTH;
                case Q45:
                    return section.getSectionStartAngles().get(3) + 15f;
            }
        }
        return -1.0f;
    }

    public static float mapStartAngleToDrawArcAngle(float startAngle) {
        if (startAngle < ANGLE_90) {
            return FULL_ANGLE - (ANGLE_90 - startAngle);
        } else {
            return startAngle - ANGLE_90;
        }
    }

    public static boolean isTimePm(int hour, int minute) {
        if (hour == HOURS_12 && minute == 0) {
            return true;
        } else if (hour == HOURS_12 && minute > 0) {
            return true;
        } else if (hour > HOURS_12) {
            return true;
        }
        return false;
    }

    public static @NonNull
    Pair<Float, Float> findSweepAngles(float startAngle, float endAngle,
                                       boolean isStartTimePm, boolean isEndTimePm) {
        Float amSweep = null;
        Float pmSweep = null;
        if (!isStartTimePm && !isEndTimePm && endAngle > startAngle) {
            amSweep = endAngle - startAngle;
        } else if (isStartTimePm && isEndTimePm && endAngle > startAngle) {
            pmSweep = endAngle - startAngle;
        } else if (!isStartTimePm) {
            amSweep = FULL_ANGLE - startAngle;
            pmSweep = endAngle;
        } else {
            amSweep = endAngle;
            pmSweep = FULL_ANGLE - startAngle;
        }

        return new Pair<>(amSweep, pmSweep);
    }

    public static int getTimeAsMinutes(int startHour, int startMinute) {
        return startHour * MINUTES_60 + startMinute;
    }

    public static Pair<Integer, Integer> timeInMinutesAsHourAndMin(int timeInMinutes) {
        return new Pair<>(timeInMinutes / MINUTES_60, timeInMinutes % MINUTES_60);
    }

    public static boolean isSelectedInBlockedArea(Pair<Integer, Integer> selectedTime,
                                                  Pair<Integer, Integer> startHourAndMin,
                                                  Pair<Integer, Integer> endHourAndMin) {
        boolean isSelectedPm = TimePickerUtils.isTimePm(selectedTime.first, selectedTime.second);
        boolean isStartPm = TimePickerUtils.isTimePm(startHourAndMin.first, startHourAndMin.second);
        boolean isEndPm = TimePickerUtils.isTimePm(endHourAndMin.first, endHourAndMin.second);
        int selectedTimeInMin = TimePickerUtils.getTimeAsMinutes(selectedTime.first, selectedTime.second);
        int startTimeInMin = TimePickerUtils.getTimeAsMinutes(startHourAndMin.first, startHourAndMin.second);
        int endTimeMin = TimePickerUtils.getTimeAsMinutes(endHourAndMin.first, endHourAndMin.second);
        int twelveInMin = TimePickerUtils.getTimeAsMinutes(HOURS_12, Q0);
        int twentyFourInMin = TimePickerUtils.getTimeAsMinutes(HOURS_24, Q0);

        //edge case
        if (!isStartPm && isEndPm && selectedTimeInMin == twelveInMin && endTimeMin != twelveInMin) {
            return true;
        } else if (!isStartPm && isEndPm && selectedTimeInMin == twelveInMin) {
            return false;
        }

        if (isSelectedPm == isStartPm && isStartPm == isEndPm) {
            return selectedTimeInMin >= startTimeInMin && selectedTimeInMin < endTimeMin;
        } else if (isSelectedPm && isStartPm) {
            if (selectedTimeInMin >= startTimeInMin && selectedTimeInMin <= twentyFourInMin) {
                return true;
            }
        } else if ((!isSelectedPm && isStartPm && !isEndPm) || (isSelectedPm && isEndPm)) {
            if (selectedTimeInMin < endTimeMin) {
                return true;
            }
        } else if (!isSelectedPm && !isStartPm) {
            if (selectedTimeInMin >= startTimeInMin && selectedTimeInMin <= twelveInMin) {
                return true;
            }
        }
        return false;
    }

    public static LinkedHashSet<Integer> extractHoursToOvershadow(LockedInterval lockedInterval) {
        LinkedHashSet<Integer> hoursToOvershadow = new LinkedHashSet<>();
        boolean istStartTimePm = TimePickerUtils.isTimePm(lockedInterval.getStartHour(), lockedInterval.getStartMinute());
        boolean isEndTimePm = TimePickerUtils.isTimePm(lockedInterval.getEndHour(), lockedInterval.getEndMinute());
        int startIterHour = getStartIterHour(lockedInterval);
        int endIterHour = getEndIterHour(lockedInterval);

        if (!istStartTimePm && !isEndTimePm) {
            for (int hour = startIterHour; hour <= endIterHour; hour++) {
                hoursToOvershadow.add(hour);
            }
        } else if (!istStartTimePm) {
            for (int hour = startIterHour; hour <= endIterHour; hour++) {
                if (hour == HOURS_12) {
                    hoursToOvershadow.add(HOURS_24);
                } else {
                    hoursToOvershadow.add(hour);
                }
            }
        } else if (!isEndTimePm) {
            for (int hour = startIterHour; hour <= HOURS_24; hour++) {
                if (hour == HOURS_24 && TimePickerUtils.isEndHour0(lockedInterval)) {
                } else if (hour == HOURS_24 && !TimePickerUtils.isEndHour0(lockedInterval)) {
                    hoursToOvershadow.add(HOURS_12);
                } else if (hour == HOURS_12 && !TimePickerUtils.isEndHour0(lockedInterval)) {
                    hoursToOvershadow.add(HOURS_24);
                } else {
                    hoursToOvershadow.add(hour);
                }
            }
            for (int hour = 1; hour <= endIterHour; hour++) {
                hoursToOvershadow.add(hour);
            }
        } else {
            for (int hour = startIterHour; hour <= endIterHour; hour++) {
                if (hour == HOURS_12) {
                    hoursToOvershadow.add(HOURS_24);
                } else {
                    hoursToOvershadow.add(hour);
                }
            }
        }

        return hoursToOvershadow;
    }

    public static boolean isEndHour12(LockedInterval lockedInterval) {
        return lockedInterval.getEndHour() == HOURS_12 && lockedInterval.getEndMinute() == 0;
    }

    public static boolean isEndHour0(LockedInterval lockedInterval) {
        return lockedInterval.getEndHour() == 0 && lockedInterval.getEndMinute() == 0;
    }

    private static int getStartIterHour(LockedInterval lockedInterval) {
        int startIterHour;
        if (lockedInterval.getStartMinute() > 0) {
            startIterHour = lockedInterval.getStartHour() + 1;
        } else {
            startIterHour = lockedInterval.getStartHour();
        }
        return startIterHour;
    }

    private static int getEndIterHour(LockedInterval lockedInterval) {
        int endIterHour;
        if (lockedInterval.getEndMinute() > 0) {
            endIterHour = lockedInterval.getEndHour();
        } else {
            endIterHour = lockedInterval.getEndHour() - 1;
        }
        return endIterHour;
    }

}
