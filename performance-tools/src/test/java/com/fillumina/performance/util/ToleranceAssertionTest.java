package com.fillumina.performance.util;


import com.fillumina.performance.util.stats.Ratio;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ToleranceAssertionTest {

    @Test
    public void shouldZeroBeEquals() {
        ToleranceAssertion.assertEquals(0, 0,
                Ratio.percentage(10));
    }

    @Test
    public void shouldBeEquals() {
        ToleranceAssertion.assertEquals(1.0, 1.0,
                Ratio.percentage(0));
    }

    @Test
    public void shouldBeEqualsWithinPercentage() {
        ToleranceAssertion.assertEquals(1.0, 1.1,
                Ratio.percentage(20));
    }

    @Test(expected = AssertionError.class)
    public void shouldNotBeEqualsWithinPercentage() {
        ToleranceAssertion.assertEquals(1.0, 1.22,
                Ratio.percentage(20));
    }

    @Test(expected = AssertionError.class)
    public void shouldNotBeEqualsWithinPercentageInverse() {
        ToleranceAssertion.assertEquals(1.26, 1.0,
                Ratio.percentage(20));
    }

    @Test
    public void shouldBeLess() {
        ToleranceAssertion.assertLess(1.0, 1.1, Ratio.ZERO);
    }

    @Test
    public void shouldBeLessThanZero() {
        ToleranceAssertion.assertLess(-1.0, 0, Ratio.percentage(5));
    }

    @Test
    public void shouldBeLessWithinPercentage() {
        ToleranceAssertion.assertLess(1.1, 1.0, Ratio.percentage(20));
    }

    @Test(expected = AssertionError.class)
    public void shouldNotBeLessWithinPercentage() {
        ToleranceAssertion.assertLess(1.21, 1.0, Ratio.percentage(20));
    }

    @Test
    public void shouldBeGreater() {
        ToleranceAssertion.assertGreater(1.0, 1.0, Ratio.ZERO);
    }

    @Test
    public void shouldBeGreaterThanZero() {
        ToleranceAssertion.assertLess(0, 1.0, Ratio.percentage(5));
    }

    @Test
    public void shouldBeGreaterWithinPercentage() {
        ToleranceAssertion.assertGreater(1.0, 1.1, Ratio.percentage(20));
    }

    @Test(expected = AssertionError.class)
    public void shouldNotBeGreaterWithinPercentage() {
        ToleranceAssertion.assertGreater(1.0, 1.26, Ratio.percentage(20));
    }
}
