package com.fillumina.performance.assertion;

import com.fillumina.performance.util.stats.OnlineMeasure;
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
        AssertableMock ai = AssertableMock.createWithName("title",
                        "first", new OnlineMeasure(12.3),
                        "second", new OnlineMeasure(45.6));

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
}
