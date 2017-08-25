package com.fillumina.performance.mock;

import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.OnlineMeasure;
import com.fillumina.performance.util.stats.Ratio;
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
        assertEquals(12.3, ai.getMeasure("first").getMean(), 0);
        assertEquals(45.6, ai.getMeasure("second").getMean(), 0);
    }

    @Test
    public void shouldCreateNameAndDataWithConstructor() {
        AssertableMock ai = new AssertableMock("title",
                LinkedMap.<TName,Measure>create(TN.tname("first"), new OnlineMeasure(12.3),
                    TN.tname("second"), new OnlineMeasure(45.6)));

        assertEquals("title", ai.getName());
        assertEquals(12.3, ai.getMeasure("first").getMean(), 0);
        assertEquals(45.6, ai.getMeasure("second").getMean(), 0);
    }

    @Test
    public void shouldReturnTheName() {
        AssertableMock ai = AssertableMock.create("first");

        assertEquals("first", ai.getName());
    }

    @Test
    public void shouldReturnTheMeasure() {
        AssertableMock ai = AssertableMock.create("first", 12.3);

        assertEquals(12.3, ai.getMeasure("first").getMean(), 0);
    }

    @Test
    public void shouldReturnNullIfUnexistentTest() {
        AssertableMock ai = AssertableMock.create("first", 12.3);

        assertNull(ai.getMeasure("not existent"));
    }

    @Test
    public void shouldInsertTwoMeasures() {
        AssertableMock ai = AssertableMock.create("first", 12.3, "second", 45.6);

        assertEquals(12.3, ai.getMeasure("first").getMean(), 0);
        assertEquals(45.6, ai.getMeasure("second").getMean(), 0);
    }

    @Test
    public void shouldGetTheRatioBetweenTwoMeasures() {
        AssertableMock ai = AssertableMock.create("first", 12.3, "second", 45.6);

        assertEquals(1.0,
                ai.getRatioToReferenceTest("second", Ratio.P_95).getValue(), 0);

        assertEquals(12.3 / 45.6,
                ai.getRatioToReferenceTest("first", Ratio.P_95).getValue(), 0);
    }

    @Test
    public void shouldGetTheRatioBetweenThreeMeasures() {
        AssertableMock ai = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertEquals(1.0,
                ai.getRatioToReferenceTest("second", Ratio.P_95).getValue(), 0);

        assertEquals(12.3 / 45.6,
                ai.getRatioToReferenceTest("first", Ratio.P_95).getValue(), 0);

        assertEquals(34.5 / 45.6,
                ai.getRatioToReferenceTest("third", Ratio.P_95).getValue(), 0);
    }
}
