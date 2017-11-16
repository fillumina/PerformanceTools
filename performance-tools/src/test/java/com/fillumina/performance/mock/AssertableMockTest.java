package com.fillumina.performance.mock;

import com.fillumina.performance.assertion.TestNotFoundException;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertableMockTest {

    @Test
    public void shouldCreateNameAndData() {
        AssertableMock assertable = AssertableMock.create(
                "title", "first", 12.3, "second", 45.6);

        assertEquals("title", assertable.getName());
        assertEquals(12.3, assertable.getMeasure("first").getMean(), 0);
        assertEquals(45.6, assertable.getMeasure("second").getMean(), 0);
    }

    @Test
    public void shouldCreateNameAndDataWithConstructor() {
        AssertableMock assertable = AssertableMock.createWithName("title",
                        "first", 12.3, "second", 45.6);

        assertEquals("title", assertable.getName());
        assertEquals(12.3, assertable.getMeasure("first").getMean(), 0);
        assertEquals(45.6, assertable.getMeasure("second").getMean(), 0);
    }

    @Test
    public void shouldReturnTheName() {
        AssertableMock assertable = AssertableMock.create("first");

        assertEquals("first", assertable.getName());
    }

    @Test
    public void shouldReturnTheMeasure() {
        AssertableMock assertable = AssertableMock.create("first", 12.3);

        assertEquals(12.3, assertable.getMeasure("first").getMean(), 0);
    }

    @Test(expected=TestNotFoundException.class)
    public void shouldReturnNullIfUnexistentTest() {
        AssertableMock assertable = AssertableMock.create("first", 12.3);

        assertable.getMeasure("not existent");
    }

    @Test
    public void shouldInsertTwoMeasures() {
        AssertableMock assertable =
                AssertableMock.create("first", 12.3, "second", 45.6);

        assertEquals(12.3, assertable.getMeasure("first").getMean(), 0);
        assertEquals(45.6, assertable.getMeasure("second").getMean(), 0);
    }
}
