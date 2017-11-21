package concisesoftware.com.datetimepicker;

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
        assertTrue(timerSections.get(0).getHour() == 12);
        assertTrue(timerSections.get(1).getHour() == 1);
        assertTrue(timerSections.get(11).getHour() == 11);
    }

    private ArrayList<RadialTimePickerView.TimerSection> getTimerSections() {
        ArrayList<Float> startArcAngles = TimePickerUtils.generateTimerStartArcAngles(48, 7.5f);
        return TimePickerUtils.generateTimerSections(startArcAngles);
    }

    @Test
    public void findSectionForDegreesTest() {
        ArrayList<RadialTimePickerView.TimerSection> timerSections = getTimerSections();
        RadialTimePickerView.TimerSection sectionForDegrees = TimePickerUtils.findSectionForDegrees(timerSections, 34);
        assertTrue(sectionForDegrees.getHour() == 1);
        sectionForDegrees = TimePickerUtils.findSectionForDegrees(timerSections, 180);
        assertTrue(sectionForDegrees.getHour() == 6);
        sectionForDegrees = TimePickerUtils.findSectionForDegrees(timerSections, 355);
        assertTrue(sectionForDegrees.getHour() == 12);
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
    public void findStartAngleOfSectionWhichContainsDegree() {
        RadialTimePickerView.TimerSection timerSection = new RadialTimePickerView.TimerSection();
        timerSection.setHour(12);
        ArrayList<Float> sectionAngles = new ArrayList<>();
        sectionAngles.add(345f);
        sectionAngles.add(352.5f);
        sectionAngles.add(0f);
        sectionAngles.add(7.5f);
        timerSection.setSectionStartAngles(sectionAngles);
        float startAngle = TimePickerUtils.findStartAngleOfSectionWhichContainsDegree(353, timerSection);
        assertTrue(startAngle == 352.5);
        startAngle = TimePickerUtils.findStartAngleOfSectionWhichContainsDegree(3, timerSection);
        assertTrue(startAngle == 0f);
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

}