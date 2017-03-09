package com.fillumina.performance.infrastructure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertableImplTest {

    @Test
    public void shouldReturnTheName() {
        AssertableImpl ai = new AssertableImpl("first");

        assertEquals("first", ai.getName());
    }

    @Test
    public void shouldReturnTheMeasure() {
        AssertableImpl ai = new AssertableImpl("first", 12.3);

        assertEquals(12.3, ai.getValue("first").getMean(), 0);
    }

    @Test
    public void shouldReturnNullIfUnexistentTest() {
        AssertableImpl ai = new AssertableImpl("first", 12.3);

        assertNull(ai.getValue("not existent"));
    }

    @Test
    public void shouldInsertTwoMeasures() {
        AssertableImpl ai = new AssertableImpl("first", 12.3, "second", 45.6);

        assertEquals(12.3, ai.getValue("first").getMean(), 0);
        assertEquals(45.6, ai.getValue("second").getMean(), 0);
    }

    @Test
    public void shouldGetTheRatioBetweenTwoMeasures() {
        AssertableImpl ai = new AssertableImpl("first", 12.3, "second", 45.6);

        assertEquals(1.0,
                ai.getRatioWithSlowestTest("second").getValue(), 0);

        assertEquals(12.3 / 45.6,
                ai.getRatioWithSlowestTest("first").getValue(), 0);
    }

    @Test
    public void shouldGetTheRatioBetweenThreeMeasures() {
        AssertableImpl ai = new AssertableImpl(
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertEquals(1.0,
                ai.getRatioWithSlowestTest("second").getValue(), 0);

        assertEquals(12.3 / 45.6,
                ai.getRatioWithSlowestTest("first").getValue(), 0);

        assertEquals(34.5 / 45.6,
                ai.getRatioWithSlowestTest("third").getValue(), 0);
    }
}
