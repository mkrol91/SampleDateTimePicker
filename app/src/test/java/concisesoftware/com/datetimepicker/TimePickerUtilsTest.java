package concisesoftware.com.datetimepicker;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.appeaser.sublimepickerlibrary.timepicker.RadialTimePickerView;
import com.appeaser.sublimepickerlibrary.utilities.TimePickerUtils;

import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class TimePickerUtilsTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void hourToCheckOnEdgeTest() {
        ArrayList<Integer> hoursToCheckTest = TimePickerUtils.getHoursToCheck(11, 1);
        assertEquals(3, hoursToCheckTest.size());
        assertEquals(new Integer(11), hoursToCheckTest.get(0));
        assertEquals(new Integer(1), hoursToCheckTest.get(2));
    }

    @Test
    public void hourstToCheckInTheMiddleTest() {
        ArrayList<Integer> hoursToCheckTest = TimePickerUtils.getHoursToCheck(2, 9);
        assertEquals(8, hoursToCheckTest.size());
        assertEquals(new Integer(2), hoursToCheckTest.get(0));
        assertEquals(new Integer(9), hoursToCheckTest.get(7));
        assertEquals(new Integer(5), hoursToCheckTest.get(3));
    }

    @Test
    public void hourstToCheckStartAndEndEqualsTest() {
        ArrayList<Integer> hoursToCheckTest = TimePickerUtils.getHoursToCheck(2, 2);
        assertEquals(1, hoursToCheckTest.size());
        assertEquals(new Integer(2), hoursToCheckTest.get(0));
    }

    @Test
    public void hourstToChangeOutOfRangePositive() {
        ArrayList<Integer> hoursToCheckTest = TimePickerUtils.getHoursToCheck(25, 48);
        assertEquals(0, hoursToCheckTest.size());
    }

    @Test
    public void hoursToCheckOutOfRangeNegative() {
        ArrayList<Integer> hoursToCheckTest = TimePickerUtils.getHoursToCheck(-1, -5);
        assertEquals(0, hoursToCheckTest.size());
    }

    @Test
    public void hoursToCheckOnEdge() {
        ArrayList<Integer> hoursToCheck = TimePickerUtils.getHoursToCheck(11, 15);
        assertEquals(5, hoursToCheck.size());
        assertEquals(new Integer(11), hoursToCheck.get(0));
        assertEquals(new Integer(13), hoursToCheck.get(2));
        assertEquals(new Integer(15), hoursToCheck.get(4));
    }

    @Test
    public void hoursToCheckOnPomTest() {
        ArrayList<Integer> hoursToCheck = TimePickerUtils.getHoursToCheck(22, 13);
        assertEquals(16, hoursToCheck.size());
        assertEquals(new Integer(13), hoursToCheck.get(15));
    }

    @Test
    public void hoursToCheckOnPomTest24h() {
        ArrayList<Integer> hoursToCheck = TimePickerUtils.getHoursToCheck(23, 22);
        assertEquals(24, hoursToCheck.size());
        assertEquals(new Integer(22), hoursToCheck.get(23));
        assertEquals(new Integer(1), hoursToCheck.get(2));
        assertEquals(new Integer(23), hoursToCheck.get(0));
    }

    @Test
    public void generateTimerStartArcAngles() {
        ArrayList<Float> startArcAngles = TimePickerUtils.generateTimerStartArcAngles(48, 7.5f);
        assertTrue(startArcAngles.get(0) == 345f);
        assertTrue(startArcAngles.get(1) == 352.5f);
        assertTrue(startArcAngles.get(2) == 0.0f);
        assertTrue(startArcAngles.get(3) == 7.5f);
    }

    @Test
    public void prepareTimerSections() {
        ArrayList<RadialTimePickerView.TimerSection> timerSections = getTimerSections();
        assertTrue(timerSections.size() == 12);
        assertTrue(timerSections.get(11).getSectionStartAngles().get(3) == 337.5f);
        assertTrue(timerSections.get(0).getSectionStartAngles().get(2) == 0.0f);
        assertTrue(timerSections.get(0).getHour() == 0);
        assertTrue(timerSections.get(1).getHour() == 1);
        assertTrue(timerSections.get(11).getHour() == 11);
    }

    @Test
    public void mapToTimeAsPairTest0_11h() {
        Pair<Integer, Integer> timeAsPair = TimePickerUtils.mapToTimeAsPair(4, 2, true, false);
        assertTrue(timeAsPair.first == 3);
        assertTrue(timeAsPair.second == 45);

        timeAsPair = TimePickerUtils.mapToTimeAsPair(4, 2, false, false);
        assertTrue(timeAsPair.first == 4);
        assertTrue(timeAsPair.second == 0);

        timeAsPair = TimePickerUtils.mapToTimeAsPair(0, 2, true, false);
        assertTrue(timeAsPair.first == 11);
        assertTrue(timeAsPair.second == 45);

        timeAsPair = TimePickerUtils.mapToTimeAsPair(0, 2, false, false);
        assertTrue(timeAsPair.first == 0);
        assertTrue(timeAsPair.second == 0);

        timeAsPair = TimePickerUtils.mapToTimeAsPair(0, 3, true, false);
        assertTrue(timeAsPair.first == 0);
        assertTrue(timeAsPair.second == 0);

        timeAsPair = TimePickerUtils.mapToTimeAsPair(0, 3, false, false);
        assertTrue(timeAsPair.first == 0);
        assertTrue(timeAsPair.second == 15);

        timeAsPair = TimePickerUtils.mapToTimeAsPair(0, 1, true, false);
        assertTrue(timeAsPair.first == 11);
        assertTrue(timeAsPair.second == 30);

        timeAsPair = TimePickerUtils.mapToTimeAsPair(0, 1, false, false);
        assertTrue(timeAsPair.first == 11);
        assertTrue(timeAsPair.second == 45);

        timeAsPair = TimePickerUtils.mapToTimeAsPair(0, 4, true, false);
        assertTrue(timeAsPair.first == 0);
        assertTrue(timeAsPair.second == 15);

        timeAsPair = TimePickerUtils.mapToTimeAsPair(0, 4, false, false);
        assertTrue(timeAsPair.first == 0);
        assertTrue(timeAsPair.second == 30);
    }

    @Test
    public void mapToTimeAsPairTest13_24h() {
        Pair<Integer, Integer> timeAsPair = TimePickerUtils.mapToTimeAsPair(12, 3, true, true);
        assertTrue(timeAsPair.first == 12);
        assertTrue(timeAsPair.second == 0);

        timeAsPair = TimePickerUtils.mapToTimeAsPair(12, 1, true, true);
        assertTrue(timeAsPair.first == 23);
        assertTrue(timeAsPair.second == 30);

        timeAsPair = TimePickerUtils.mapToTimeAsPair(12, 1, false, true);
        assertTrue(timeAsPair.first == 23);
        assertTrue(timeAsPair.second == 45);

        timeAsPair = TimePickerUtils.mapToTimeAsPair(12, 2, true, true);
        assertTrue(timeAsPair.first == 23);
        assertTrue(timeAsPair.second == 45);

        timeAsPair = TimePickerUtils.mapToTimeAsPair(12, 2, false, true);
        assertTrue(timeAsPair.first == 0);
        assertTrue(timeAsPair.second == 0);

        timeAsPair = TimePickerUtils.mapToTimeAsPair(12, 3, true, true);
        assertTrue(timeAsPair.first == 12);
        assertTrue(timeAsPair.second == 0);

        timeAsPair = TimePickerUtils.mapToTimeAsPair(12, 3, false, true);
        assertTrue(timeAsPair.first == 12);
        assertTrue(timeAsPair.second == 15);

        timeAsPair = TimePickerUtils.mapToTimeAsPair(12, 4, true, true);
        assertTrue(timeAsPair.first == 12);
        assertTrue(timeAsPair.second == 15);

        timeAsPair = TimePickerUtils.mapToTimeAsPair(12, 4, false, true);
        assertTrue(timeAsPair.first == 12);
        assertTrue(timeAsPair.second == 30);
    }

    private ArrayList<RadialTimePickerView.TimerSection> getTimerSections() {
        return getTimerSections(false);
    }

    private ArrayList<RadialTimePickerView.TimerSection> getTimerSections(boolean isPm) {
        ArrayList<Float> startArcAngles = TimePickerUtils.generateTimerStartArcAngles(48, 7.5f);
        return TimePickerUtils.generateTimerSections(startArcAngles, isPm);
    }

    @Test
    public void generateTimerSectionsTest() {
        ArrayList<RadialTimePickerView.TimerSection> timerSections = getTimerSections(true);
        assertTrue(timerSections.get(0).getHour() == 12);
        timerSections = getTimerSections(false);
        assertTrue(timerSections.get(0).getHour() == 0);
    }

    @Test
    public void findSectionForDegreesTest() {
        ArrayList<RadialTimePickerView.TimerSection> timerSections = getTimerSections();
        RadialTimePickerView.TimerSection sectionForDegrees = TimePickerUtils.findSectionForDegrees(timerSections, 34);
        assertTrue(sectionForDegrees.getHour() == 1);
        sectionForDegrees = TimePickerUtils.findSectionForDegrees(timerSections, 180);
        assertTrue(sectionForDegrees.getHour() == 6);
        sectionForDegrees = TimePickerUtils.findSectionForDegrees(timerSections, 355);
        assertTrue(sectionForDegrees.getHour() == 0);
    }

    @Test
    public void isDegreesCloserTest() {
        boolean isDegreesCloserToStartDegree =
                TimePickerUtils.isDegreeCloserToStartDegree(353,
                        352.5f, 0f);
        assertTrue(isDegreesCloserToStartDegree);
        isDegreesCloserToStartDegree =
                TimePickerUtils.isDegreeCloserToStartDegree(359,
                        352.5f, 0f);
        assertFalse(isDegreesCloserToStartDegree);
        isDegreesCloserToStartDegree = TimePickerUtils.isDegreeCloserToStartDegree(4,
                0f, 7.5f);
        assertFalse(isDegreesCloserToStartDegree);
        isDegreesCloserToStartDegree = TimePickerUtils.isDegreeCloserToStartDegree(3,
                0f, 7.5f);
        assertTrue(isDegreesCloserToStartDegree);
        isDegreesCloserToStartDegree = TimePickerUtils.isDegreeCloserToStartDegree(29,
                22.5f, 30f);
        assertFalse(isDegreesCloserToStartDegree);
    }

    @Test
    public void findStartAngleOfSectionWhichContainsDegreeTest() {
        RadialTimePickerView.TimerSection timerSection = getTimerSectionForHour12();
        float startAngle = TimePickerUtils.findStartAngleOfSectionWhichContainsDegree(353, timerSection);
        assertTrue(startAngle == 352.5);
        startAngle = TimePickerUtils.findStartAngleOfSectionWhichContainsDegree(3, timerSection);
        assertTrue(startAngle == 0f);
    }

    @Test
    public void findQuarterOfSectionWhichContainsDegreeTest() {
        RadialTimePickerView.TimerSection timerSection = getTimerSectionForHour12();
        int quarter = TimePickerUtils.findUnasignedQuarterOfSectionWhichContainsDegree(348, timerSection);
        assertTrue(quarter == 1);
        quarter = TimePickerUtils.findUnasignedQuarterOfSectionWhichContainsDegree(358, timerSection);
        assertTrue(quarter == 2);
        quarter = TimePickerUtils.findUnasignedQuarterOfSectionWhichContainsDegree(3, timerSection);
        assertTrue(quarter == 3);
        quarter = TimePickerUtils.findUnasignedQuarterOfSectionWhichContainsDegree(13, timerSection);
        assertTrue(quarter == 4);
    }

    @NonNull
    private RadialTimePickerView.TimerSection getTimerSectionForHour12() {
        RadialTimePickerView.TimerSection timerSection = new RadialTimePickerView.TimerSection();
        timerSection.setHour(12);
        ArrayList<Float> sectionAngles = new ArrayList<>();
        sectionAngles.add(345f);
        sectionAngles.add(352.5f);
        sectionAngles.add(0f);
        sectionAngles.add(7.5f);
        timerSection.setSectionStartAngles(sectionAngles);
        return timerSection;
    }

    @Test
    public void findStartAngleOfSectionErrorCase() {
        RadialTimePickerView.TimerSection timerSection = new RadialTimePickerView.TimerSection();
        timerSection.setHour(12);
        ArrayList<Float> sectionAngles = new ArrayList<>();
        sectionAngles.add(135f);
        sectionAngles.add(142.5f);
        sectionAngles.add(150f);
        sectionAngles.add(157.5f);
        timerSection.setSectionStartAngles(sectionAngles);
        float startAngle = TimePickerUtils.findStartAngleOfSectionWhichContainsDegree(162, timerSection);
        assertTrue(startAngle == 157.5f);
    }

    @Test
    public void degreesWithOffsetToDrawingZero() {
        float degreesWithOffsetToDrawing = TimePickerUtils.moveByAngle(0f, 255f);
        assertTrue(255f == degreesWithOffsetToDrawing);
    }

    @Test
    public void degreesWithOffsetToDrawingOtherValue() {
        float degreesWithOffsetToDrawing = TimePickerUtils.moveByAngle(105f, 255f);
        assertTrue(360f == degreesWithOffsetToDrawing);
        degreesWithOffsetToDrawing = TimePickerUtils.moveByAngle(355f, 255f);
        assertTrue(250f == degreesWithOffsetToDrawing);
        degreesWithOffsetToDrawing = TimePickerUtils.moveByAngle(180f, 255f);
        assertTrue(75f == degreesWithOffsetToDrawing);
        degreesWithOffsetToDrawing = TimePickerUtils.moveByAngle(270f, 255f);
        assertTrue(165f == degreesWithOffsetToDrawing);
    }

    @Test
    public void moveByAngleTest() {
        float moved = TimePickerUtils.moveByAngle(255f, 240f);
        assertTrue(moved == 135f);
        moved = TimePickerUtils.moveByAngle(0f, 145f);
        assertTrue(moved == 145f);
        moved = TimePickerUtils.moveByAngle(187f, 0);
        assertTrue(moved == 187f);
        moved = TimePickerUtils.moveByAngle(360f, 360f);
        assertTrue(moved == 360f);
    }

    @Test
    public void isAngleBetweenAnglesTest() {
        boolean isAngleBetweenAngles = TimePickerUtils.isAngleBetweenAngles(45, 40, 135);
        assertTrue(isAngleBetweenAngles);
        isAngleBetweenAngles = TimePickerUtils.isAngleBetweenAngles(20, 355, 30);
        assertTrue(isAngleBetweenAngles);
        isAngleBetweenAngles = TimePickerUtils.isAngleBetweenAngles(350, 355, 352);
        assertTrue(isAngleBetweenAngles);
        isAngleBetweenAngles = TimePickerUtils.isAngleBetweenAngles(355, 340, 60);
        assertTrue(isAngleBetweenAngles);
        isAngleBetweenAngles = TimePickerUtils.isAngleBetweenAngles(111, 111, 111);
        assertTrue(isAngleBetweenAngles);
        isAngleBetweenAngles = TimePickerUtils.isAngleBetweenAngles(343, 255, 135);
        assertTrue(isAngleBetweenAngles);
    }

    @Test
    public void findSectionForHourTest() {
        ArrayList<RadialTimePickerView.TimerSection> timerSections = getTimerSections();
        RadialTimePickerView.TimerSection timerSection = TimePickerUtils.findSectionForHour(8, timerSections);
        assertTrue(timerSection.getHour() == 8);

        timerSection = TimePickerUtils.findSectionForHour(0, getTimerSections(false));
        assertTrue(timerSection.getHour() == 0);

        timerSection = TimePickerUtils.findSectionForHour(12, getTimerSections(true));
        assertTrue(timerSection.getHour() == 12);

        timerSection = TimePickerUtils.findSectionForHour(12, getTimerSections(true));
        assertTrue(timerSection.getHour() == 12);
    }

    @Test
    public void findStartAngleInSectionForGivenMinutesTest() {
        ArrayList<RadialTimePickerView.TimerSection> timerSections = getTimerSections();

        RadialTimePickerView.TimerSection timerSection = TimePickerUtils.findSectionForHour(12, timerSections);
        float angle = TimePickerUtils.findAngleForGivenMinutesAndHours(15, timerSection);
        assertTrue(angle == 7.5f);

        timerSection = TimePickerUtils.findSectionForHour(0, getTimerSections(false));
        angle = TimePickerUtils.findAngleForGivenMinutesAndHours(15, timerSection);
        assertTrue(angle == 7.5f);

        timerSection = TimePickerUtils.findSectionForHour(0, getTimerSections(false));
        angle = TimePickerUtils.findAngleForGivenMinutesAndHours(30, timerSection);
        assertTrue(angle == 15);

        timerSection = TimePickerUtils.findSectionForHour(12, getTimerSections(false));
        angle = TimePickerUtils.findAngleForGivenMinutesAndHours(30, timerSection);
        assertTrue(angle == 15);

        timerSection = TimePickerUtils.findSectionForHour(1, getTimerSections(false));
        angle = TimePickerUtils.findAngleForGivenMinutesAndHours(15, timerSection);
        assertTrue(angle == 37.5f);

        timerSection = TimePickerUtils.findSectionForHour(11, getTimerSections(false));
        angle = TimePickerUtils.findAngleForGivenMinutesAndHours(45, timerSection);
        assertTrue(angle == 352.5f);

        timerSection = TimePickerUtils.findSectionForHour(12, getTimerSections(false));
        angle = TimePickerUtils.findAngleForGivenMinutesAndHours(0, timerSection);
        assertTrue(angle == 0);

        timerSection = TimePickerUtils.findSectionForHour(12, getTimerSections(false));
        angle = TimePickerUtils.findAngleForGivenMinutesAndHours(45, timerSection);
        assertTrue(angle == 22.5);

        timerSection = TimePickerUtils.findSectionForHour(24, getTimerSections(true));
        angle = TimePickerUtils.findAngleForGivenMinutesAndHours(0, timerSection);
        assertTrue(angle == 0);
    }

    @Test
    public void mapStartAngleToDrawArcAngleTest() {
        float mappedAngle = TimePickerUtils.mapStartAngleToDrawArcAngle(90);
        assertTrue(mappedAngle == 0.0f);
        mappedAngle = TimePickerUtils.mapStartAngleToDrawArcAngle(180);
        assertTrue(mappedAngle == 90);
        mappedAngle = TimePickerUtils.mapStartAngleToDrawArcAngle(10);
        assertTrue(mappedAngle == 280);
    }

    @Test
    public void isEnteredTimePm() {
        assertTrue(TimePickerUtils.isTimePm(13, 0));
        assertTrue(TimePickerUtils.isTimePm(12, 45));
        assertFalse(TimePickerUtils.isTimePm(11, 45));
        assertFalse(TimePickerUtils.isTimePm(0, 15));
        assertFalse(TimePickerUtils.isTimePm(12, 0));
        assertFalse(TimePickerUtils.isTimePm(5, 0));
        assertTrue(TimePickerUtils.isTimePm(23, 45));
    }

    @Test
    public void timeAsMinAndRevert() {
        int timeAsMinutes = TimePickerUtils.getTimeAsMinutes(12, 45);
        Pair<Integer, Integer> time = TimePickerUtils.timeInMinutesAsHourAndMin(timeAsMinutes);
        assertTrue(time.first == 12);
        assertTrue(time.second == 45);

        timeAsMinutes = TimePickerUtils.getTimeAsMinutes(0, 0);
        time = TimePickerUtils.timeInMinutesAsHourAndMin(timeAsMinutes);
        assertTrue(time.first == 0);
        assertTrue(time.second == 0);
    }

    @Test
    public void checkAccesOnIntervalsEdgeTest() {
        Pair<Integer, Integer> selectedTime = new Pair<>(5, 0);
        Pair<Integer, Integer> startTime = new Pair<>(5, 0);
        Pair<Integer, Integer> endTime = new Pair<>(6, 0);

        assertTrue(TimePickerUtils.isSelectedInBlockedArea(selectedTime, startTime, endTime));

        selectedTime = new Pair<>(6, 0);
        assertFalse(TimePickerUtils.isSelectedInBlockedArea(selectedTime, startTime, endTime));

        selectedTime = new Pair<>(11, 45);
        startTime = new Pair<>(6, 15);
        endTime = new Pair<>(11, 45);
        assertFalse(TimePickerUtils.isSelectedInBlockedArea(selectedTime, startTime, endTime));
    }

    @Test
    public void isTimeBetweenTimesTest() {
        Pair<Integer, Integer> selectedTime = new Pair<>(6, 15);
        Pair<Integer, Integer> startTime = new Pair<>(8, 30);
        Pair<Integer, Integer> endTime = new Pair<>(11, 30);
        assertFalse(TimePickerUtils.isSelectedInBlockedArea(selectedTime, startTime, endTime));

        selectedTime = new Pair<>(8, 15);
        startTime = new Pair<>(8, 30);
        endTime = new Pair<>(11, 30);
        assertFalse(TimePickerUtils.isSelectedInBlockedArea(selectedTime, startTime, endTime));

        selectedTime = new Pair<>(8, 45);
        startTime = new Pair<>(8, 30);
        endTime = new Pair<>(11, 30);
        assertTrue(TimePickerUtils.isSelectedInBlockedArea(selectedTime, startTime, endTime));

        selectedTime = new Pair<>(18, 45);
        startTime = new Pair<>(15, 30);
        endTime = new Pair<>(19, 30);
        assertTrue(TimePickerUtils.isSelectedInBlockedArea(selectedTime, startTime, endTime));

        selectedTime = new Pair<>(23, 45);
        startTime = new Pair<>(15, 30);
        endTime = new Pair<>(19, 30);
        assertFalse(TimePickerUtils.isSelectedInBlockedArea(selectedTime, startTime, endTime));

        selectedTime = new Pair<>(23, 45);
        startTime = new Pair<>(15, 30);
        endTime = new Pair<>(5, 30);
        assertTrue(TimePickerUtils.isSelectedInBlockedArea(selectedTime, startTime, endTime));

        selectedTime = new Pair<>(0, 45);
        startTime = new Pair<>(15, 30);
        endTime = new Pair<>(5, 30);
        assertTrue(TimePickerUtils.isSelectedInBlockedArea(selectedTime, startTime, endTime));

        selectedTime = new Pair<>(12, 30);
        startTime = new Pair<>(10, 30);
        endTime = new Pair<>(13, 30);
        assertTrue(TimePickerUtils.isSelectedInBlockedArea(selectedTime, startTime, endTime));

        selectedTime = new Pair<>(11, 30);
        startTime = new Pair<>(10, 30);
        endTime = new Pair<>(13, 30);
        assertTrue(TimePickerUtils.isSelectedInBlockedArea(selectedTime, startTime, endTime));

        selectedTime = new Pair<>(8, 15);
        startTime = new Pair<>(8, 15);
        endTime = new Pair<>(11, 30);
        assertFalse(TimePickerUtils.isSelectedInBlockedArea(selectedTime, startTime, endTime));

        selectedTime = new Pair<>(11, 30);
        startTime = new Pair<>(8, 15);
        endTime = new Pair<>(11, 30);
        assertFalse(TimePickerUtils.isSelectedInBlockedArea(selectedTime, startTime, endTime));

        selectedTime = new Pair<>(24, 0);
        startTime = new Pair<>(24, 0);
        endTime = new Pair<>(14, 30);
        assertFalse(TimePickerUtils.isSelectedInBlockedArea(selectedTime, startTime, endTime));

        selectedTime = new Pair<>(13, 0);
        startTime = new Pair<>(24, 0);
        endTime = new Pair<>(24, 0);
        assertFalse(TimePickerUtils.isSelectedInBlockedArea(selectedTime, startTime, endTime));

        selectedTime = new Pair<>(12, 0);
        startTime = new Pair<>(12, 0);
        endTime = new Pair<>(14, 30);
        assertFalse(TimePickerUtils.isSelectedInBlockedArea(selectedTime, startTime, endTime));

        selectedTime = new Pair<>(12, 0);
        startTime = new Pair<>(11, 15);
        endTime = new Pair<>(14, 45);

        assertTrue(TimePickerUtils.isSelectedInBlockedArea(selectedTime, startTime, endTime));
    }

}