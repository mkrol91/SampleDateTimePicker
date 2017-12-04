package com.appeaser.sublimepickerlibrary.utilities;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.appeaser.sublimepickerlibrary.timepicker.RadialTimePickerView;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import static com.appeaser.sublimepickerlibrary.timepicker.RadialTimePickerView.UNITS_COUNT;
import static com.appeaser.sublimepickerlibrary.timepicker.RadialTimePickerView.UNIT_WIDTH;

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
                    timerSection.setHour(isPm ? HOURS_12 : 0);
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
            Float endAngle = sectionStartAngles.get(3) + UNIT_WIDTH;

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
                return isDegreesCloserToStartDegree ? new Pair<>(HOURS_12 - 1, 30) : new Pair<>(HOURS_12 - 1, 45);
            } else if (hour == 12) {
                return isDegreesCloserToStartDegree ? new Pair<>(HOURS_24 - 1, 30) : new Pair<>(HOURS_24 - 1, 45);
            }
            return isDegreesCloserToStartDegree ? new Pair<>(hour - 1, 30) : new Pair<>(hour - 1, 45);
        } else if (unassignedQuarter == 2) {
            if (hour == 0) {
                return isDegreesCloserToStartDegree ? new Pair<>(HOURS_12 - 1, 45) : new Pair<>(0, 0);
            } else if (hour == 12) {
                return isDegreesCloserToStartDegree ? new Pair<>(HOURS_24 - 1, 45) : new Pair<>(HOURS_12, 0);
            }
            return isDegreesCloserToStartDegree ? new Pair<>(hour - 1, 45) : new Pair<>(hour, 0);
        } else if (unassignedQuarter == 3) {
            return isDegreesCloserToStartDegree ? new Pair<>(hour, 0) : new Pair<>(hour, 15);
        } else if (unassignedQuarter == 4) {
            if (hour == 0) {
                return isDegreesCloserToStartDegree ? new Pair<>(hour, 15) : new Pair<>(hour, 30);
            } else if (hour == 12) {
                return isDegreesCloserToStartDegree ? new Pair<>(HOURS_12, 15) : new Pair<>(HOURS_12, 30);
            }
            return isDegreesCloserToStartDegree ? new Pair<>(hour, 15) : new Pair<>(hour, 30);
        }
        return null;
    }

    public static RadialTimePickerView.TimerSection findSectionForHour(int hour, ArrayList<RadialTimePickerView.TimerSection> timerSections) {
        if (timerSections != null && timerSections.size() != 0) {
            for (RadialTimePickerView.TimerSection timerSection : timerSections) {
                if (timerSection.getHour() == hour || (timerSection.getHour() == 12 && hour == 0)
                        || (timerSection.getHour() == 24 && hour == 12)) {
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
                case 0:
                    return section.getSectionStartAngles().get(2);
                case 15:
                    return section.getSectionStartAngles().get(3);
                case 30:
                    return section.getSectionStartAngles().get(3) + UNIT_WIDTH;
                case 45:
                    return section.getSectionStartAngles().get(3) + 15f;
            }
        }
        return -1.0f;
    }

    public static float mapStartAngleToDrawArcAngle(float startAngle) {
        if (startAngle < 90) {
            return 360 - (90 - startAngle);
        } else {
            return startAngle - 90;
        }
    }

    public static boolean isTimePm(int hour, int minute) {
        if (hour == 12 && minute == 0) {
            return true;
        } else if (hour == 12 && minute > 0) {
            return true;
        } else if (hour > 12) {
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

//        if (!isStartTimePm && isEndTimePm) {
//            if (sweepAngles.first == FULL_ANGLE) {
//                sweepAngles.first -= UNIT_WIDTH;
//            }
//        }

    }

    public static int getTimeAsMinutes(int startHour, int startMinute) {
        return startHour * 60 + startMinute;
    }

    public static Pair<Integer, Integer> timeInMinutesAsHourAndMin(int timeInMinutes) {
        return new Pair<>(timeInMinutes / 60, timeInMinutes % 60);
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
        int twelveInMin = TimePickerUtils.getTimeAsMinutes(12, 0);
        int twentyFourInMin = TimePickerUtils.getTimeAsMinutes(24, 0);

        //edge case
        if (!isStartPm && isEndPm && selectedTimeInMin == twelveInMin) {
            return true;
        }

        if (isSelectedPm == isStartPm && isStartPm == isEndPm) {
            return selectedTimeInMin > startTimeInMin && selectedTimeInMin <= endTimeMin;
        } else if (isSelectedPm && isStartPm) {
            if (selectedTimeInMin > startTimeInMin && selectedTimeInMin <= twentyFourInMin) {
                return true;
            }
        } else if ((!isSelectedPm && isStartPm && !isEndPm) || (isSelectedPm && isEndPm)) {
            if (selectedTimeInMin <= endTimeMin) {
                return true;
            }
        } else if (!isSelectedPm && !isStartPm) {
            if (selectedTimeInMin > startTimeInMin && selectedTimeInMin <= twelveInMin) {
                return true;
            }
        }
        return false;
    }

    public static LinkedHashSet<Integer> extractHoursToOvershadow(LockedInterval lockedInterval) {
        LinkedHashSet<Integer> hoursToOvershadow = new LinkedHashSet<>();
        boolean istStartTimePm = TimePickerUtils.isTimePm(lockedInterval.getStartHour(), lockedInterval.getStartMinute());
        boolean isEndTimePm = TimePickerUtils.isTimePm(lockedInterval.getEndHour(), lockedInterval.getEndMinute());

        if (!istStartTimePm && !isEndTimePm) {
            addHourByHourTillEnd(lockedInterval, hoursToOvershadow);
        } else if (!istStartTimePm) {
            addHourByHourTillEndCheck12(lockedInterval, hoursToOvershadow);
        } else if (!isEndTimePm) {
            addHourByHourTill24Check24(lockedInterval, hoursToOvershadow);
            addFromOneTillEnd(lockedInterval, hoursToOvershadow);
        } else {
            addHourByHourTillEnd(lockedInterval, hoursToOvershadow);
        }

        return hoursToOvershadow;
    }

    private static void addFromOneTillEnd(LockedInterval lockedInterval,
                                          LinkedHashSet<Integer> hoursToOvershadow) {
        for (int hour = 1; hour <= lockedInterval.getEndHour(); hour++) {
            hoursToOvershadow.add(hour);
        }
    }

    private static void addHourByHourTill24Check24(LockedInterval lockedInterval,
                                                   LinkedHashSet<Integer> hoursToOvershadow) {
        for (int hour = lockedInterval.getStartHour() + 1; hour <= 24; hour++) {
            if (hour == 24) {
                hoursToOvershadow.add(hour - 12);
            } else {
                hoursToOvershadow.add(hour);
            }
        }
    }

    private static void addHourByHourTillEndCheck12(LockedInterval lockedInterval,
                                                    LinkedHashSet<Integer> hoursToOvershadow) {
        for (int hour = lockedInterval.getStartHour() + 1; hour <= lockedInterval.getEndHour(); hour++) {
            if (hour == 12) {
                hoursToOvershadow.add(hour + 12);
            } else {
                hoursToOvershadow.add(hour);
            }
        }
    }

    private static void addHourByHourTillEnd(LockedInterval lockedInterval,
                                             LinkedHashSet<Integer> hoursToOvershadow) {
        for (int hour = lockedInterval.getStartHour() + 1; hour <= lockedInterval.getEndHour(); hour++) {
            hoursToOvershadow.add(hour);
        }
    }
}
