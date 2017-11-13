package concisesoftware.com.datetimepicker;

import com.appeaser.sublimepickerlibrary.utilities.TimePickerUtils;

import org.junit.Test;

import java.util.ArrayList;

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
    public void degreesWithOffsetToDrawingZero() {
        float degreesWithOffsetToDrawing = TimePickerUtils.getDegreesWithOffsetToDrawing(0f, 255f);
        assertTrue(255f == degreesWithOffsetToDrawing);
    }
    @Test
    public void degreesWithOffsetToDrawingOtherValue() {
        float degreesWithOffsetToDrawing = TimePickerUtils.getDegreesWithOffsetToDrawing(105f, 255f);
        assertTrue(360f == degreesWithOffsetToDrawing);
        degreesWithOffsetToDrawing = TimePickerUtils.getDegreesWithOffsetToDrawing(355f, 255f);
        assertTrue(250f == degreesWithOffsetToDrawing);
        degreesWithOffsetToDrawing =  TimePickerUtils.getDegreesWithOffsetToDrawing(180f, 255f);
        assertTrue(75f == degreesWithOffsetToDrawing);
        degreesWithOffsetToDrawing =  TimePickerUtils.getDegreesWithOffsetToDrawing(270f, 255f);
        assertTrue(165f == degreesWithOffsetToDrawing);
    }

}