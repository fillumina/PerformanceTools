package com.fillumina.performance.mock;

import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.OnlineMeasure;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.collection.LinkedMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertableMockTest {

    @Test
    public void shouldCreateNameAndData() {
        AssertableMock ai = AssertableMock.create(
                "title", "first", 12.3, "second", 45.6);

        assertEquals("title", ai.getName());
        assertEquals(12.3, ai.getValue("first").getMean(), 0);
        assertEquals(45.6, ai.getValue("second").getMean(), 0);
    }

    @Test
    public void shouldCreateNameAndDataWithConstructor() {
        AssertableMock ai = new AssertableMock("title",
                LinkedMap.<String,Measure>create(
                    "first", new OnlineMeasure(12.3),
                    "second", new OnlineMeasure(45.6)));

        assertEquals("title", ai.getName());
        assertEquals(12.3, ai.getValue("first").getMean(), 0);
        assertEquals(45.6, ai.getValue("second").getMean(), 0);
    }

    @Test
    public void shouldReturnTheName() {
        AssertableMock ai = AssertableMock.create("first");

        assertEquals("first", ai.getName());
    }

    @Test
    public void shouldReturnTheMeasure() {
        AssertableMock ai = AssertableMock.create("first", 12.3);

        assertEquals(12.3, ai.getValue("first").getMean(), 0);
    }

    @Test
    public void shouldReturnNullIfUnexistentTest() {
        AssertableMock ai = AssertableMock.create("first", 12.3);

        assertNull(ai.getValue("not existent"));
    }

    @Test
    public void shouldInsertTwoMeasures() {
        AssertableMock ai = AssertableMock.create("first", 12.3, "second", 45.6);

        assertEquals(12.3, ai.getValue("first").getMean(), 0);
        assertEquals(45.6, ai.getValue("second").getMean(), 0);
    }

    @Test
    public void shouldGetTheRatioBetweenTwoMeasures() {
        AssertableMock ai = AssertableMock.create("first", 12.3, "second", 45.6);

        assertEquals(1.0,
                ai.getRatioWithSlowestTest("second", Ratio.P_95).getValue(), 0);

        assertEquals(12.3 / 45.6,
                ai.getRatioWithSlowestTest("first", Ratio.P_95).getValue(), 0);
    }

    @Test
    public void shouldGetTheRatioBetweenThreeMeasures() {
        AssertableMock ai = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertEquals(1.0,
                ai.getRatioWithSlowestTest("second", Ratio.P_95).getValue(), 0);

        assertEquals(12.3 / 45.6,
                ai.getRatioWithSlowestTest("first", Ratio.P_95).getValue(), 0);

        assertEquals(34.5 / 45.6,
                ai.getRatioWithSlowestTest("third", Ratio.P_95).getValue(), 0);
    }
}
