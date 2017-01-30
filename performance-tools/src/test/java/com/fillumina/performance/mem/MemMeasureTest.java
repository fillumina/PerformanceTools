package com.fillumina.performance.mem;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemMeasureTest {

    @Test
    public void shouldReturnValue() {
        assertEquals(0, new MemMeasure(0).getValue(), 0);
        assertEquals(12, new MemMeasure(12.34).getValue(), 0);
    }

    @Test
    public void shouldAssertEquals() throws Exception {
        new MemMeasure(17).assertEquals(17);
    }

    @Test(expected = AssertionError.class)
    public void shouldThrowAnExceptionIfNotEquals() {
        new MemMeasure(15).assertEquals(1024);
    }

    @Test
    public void shouldAssertEqualsWithDelta() throws Exception {
        new MemMeasure(23).assertEquals(20, 3);
    }

    @Test(expected = AssertionError.class)
    public void shouldThrowAnExceptionIfNotEqualsWithinDelta() {
        new MemMeasure(45).assertEquals(40, 3);
    }

    @Test
    public void shouldAssertLessThan() throws Exception {
        new MemMeasure(7).assertLessThan(13);
    }

    @Test(expected = AssertionError.class)
    public void shouldThrowAnExceptionIfNotLessThan() {
        new MemMeasure(81).assertLessThan(81);
    }

    @Test
    public void shoulsAssertLessThanWithDelta() throws Exception {
        new MemMeasure(3).assertLessThan(5, 1);
    }

    @Test(expected = AssertionError.class)
    public void shoulsThrowExceptionIfNotLessThanWithDelta() throws Exception {
        new MemMeasure(3).assertLessThan(5, 2);
    }

    @Test
    public void shouldAssertGreaterThan() throws Exception {
        new MemMeasure(76).assertGreaterThan(75);
    }

    @Test(expected = AssertionError.class)
    public void shouldThrowExceptionIfNotGreaterThan() {
        new MemMeasure(76).assertGreaterThan(76);
    }

    @Test
    public void shouldAssertGreaterThanWithinDelta() throws Exception {
        new MemMeasure(65).assertGreaterThan(30, 34);
    }

    @Test(expected = AssertionError.class)
    public void shouldThrowExceptionIfNotGreaterWithinDelta() {
        new MemMeasure(65).assertGreaterThan(30, 35);
    }
}
