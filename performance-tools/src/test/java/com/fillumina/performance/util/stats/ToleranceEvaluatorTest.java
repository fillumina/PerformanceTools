package com.fillumina.performance.util.stats;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ToleranceEvaluatorTest {

    @Test
    public void shouldBeLessWithNoTolerance() {
        ToleranceEvaluator ev = new ToleranceEvaluator(Ratio.ZERO);
        assertTrue(ev.lt(1, 2));
    }

    @Test
    public void shouldNotBeLessWithNoTolerance() {
        ToleranceEvaluator ev = new ToleranceEvaluator(Ratio.ZERO);
        assertFalse(ev.lt(2, 1));
    }

    @Test
    public void shouldNotBeLessIfEqualsWithNoTolerance() {
        ToleranceEvaluator ev = new ToleranceEvaluator(Ratio.ZERO);
        assertFalse(ev.lt(3, 3));
    }

    @Test
    public void shouldBeLessThanWithTolerance10() {
        ToleranceEvaluator ev = new ToleranceEvaluator(Ratio.percentage(10));
        assertTrue(ev.lt(109, 100));
    }

    @Test
    public void shouldNotBeLessThanWithTolerance10() {
        ToleranceEvaluator ev = new ToleranceEvaluator(Ratio.percentage(10));
        assertFalse(ev.lt(110, 100));
    }

    @Test
    public void shouldBeEqualsWithTolerance10() {
        ToleranceEvaluator ev = new ToleranceEvaluator(Ratio.percentage(10));
        assertTrue(ev.value(100).equals(109));
        assertTrue(ev.value(109).equals(100));
    }

    @Test
    public void shouldNotBeEqualsWithTolerance10() {
        ToleranceEvaluator ev = new ToleranceEvaluator(Ratio.percentage(10));
        assertFalse(ev.value(100).equals(110));
        assertFalse(ev.value(110).equals(100));
    }

    @Test
    public void shouldBeLessThanWithTolerance20() {
        ToleranceEvaluator ev = new ToleranceEvaluator(Ratio.percentage(20));
        assertTrue(ev.lt(119, 100));
    }

    @Test
    public void shouldNotBeLessThanWithTolerance20() {
        ToleranceEvaluator ev = new ToleranceEvaluator(Ratio.percentage(20));
        assertFalse(ev.lt(120, 100));
    }

    @Test
    public void shouldBeEqualsWithTolerance20() {
        ToleranceEvaluator ev = new ToleranceEvaluator(Ratio.percentage(20));
        assertTrue(ev.value(100).equals(119));
        assertTrue(ev.value(119).equals(100));
    }

    @Test
    public void shouldNotBeEqualsWithTolerance20() {
        ToleranceEvaluator ev = new ToleranceEvaluator(Ratio.percentage(20));
        assertFalse(ev.value(100).equals(120));
        assertFalse(ev.value(120).equals(100));
    }

    @Test
    public void shouldBeLessThanWithTolerance5() {
        ToleranceEvaluator ev = new ToleranceEvaluator(Ratio.percentage(5));
        assertTrue(ev.lt(104, 100));
    }

    @Test
    public void shouldNotBeLessThanWithTolerance5() {
        ToleranceEvaluator ev = new ToleranceEvaluator(Ratio.percentage(5));
        assertFalse(ev.lt(105, 100));
    }

    @Test
    public void shouldBeEqualsWithTolerance5() {
        ToleranceEvaluator ev = new ToleranceEvaluator(Ratio.percentage(5));
        assertTrue(ev.value(100).equals(104));
        assertTrue(ev.value(104).equals(100));
    }

    @Test
    public void shouldNotBeEqualsWithTolerance5() {
        ToleranceEvaluator ev = new ToleranceEvaluator(Ratio.percentage(5));
        assertFalse(ev.value(100).equals(105));
        assertFalse(ev.value(105).equals(100));
    }

    @Test
    public void shouldEqualIntervalBeAccepted() {
        ToleranceEvaluator ev = new ToleranceEvaluator(Ratio.ZERO);
        assertTrue(ev.value(100).between(100, 100));
    }

    @Test
    public void shouldEqualIntervalBeAcceptedAndToleranceAccountedFor() {
        ToleranceEvaluator ev = new ToleranceEvaluator(Ratio.percentage(10));
        assertTrue(ev.value(109).between(100, 100));
    }

    @Test
    public void shouldEqualIntervalBeAcceptedAndToleranceAccountedForMinus() {
        ToleranceEvaluator ev = new ToleranceEvaluator(Ratio.percentage(10));
        assertTrue(ev.value(91).between(100, 100));
    }

    @Test
    public void shouldNotEqualIntervalBeAcceptedAndToleranceAccountedForMinus() {
        ToleranceEvaluator ev = new ToleranceEvaluator(Ratio.percentage(10));
        assertFalse(ev.value(90).between(100, 100));
    }

    @Test
    public void shouldEqualZero() {
        ToleranceEvaluator ev = new ToleranceEvaluator(Ratio.percentage(10));
        assertTrue(ev.value(0.09).equals(0));
    }

    @Test
    public void shouldNotEqualZero() {
        ToleranceEvaluator ev = new ToleranceEvaluator(Ratio.percentage(5));
        assertFalse(ev.value(0.09).equals(0));
    }

    @Test
    public void shouldEqualZeroReverse() {
        ToleranceEvaluator ev = new ToleranceEvaluator(Ratio.percentage(10));
        assertTrue(ev.value(0).equals(0.09));
    }

    @Test
    public void shouldNotEqualZeroReverse() {
        ToleranceEvaluator ev = new ToleranceEvaluator(Ratio.percentage(5));
        assertFalse(ev.value(0).equals(0.09));
    }
}
