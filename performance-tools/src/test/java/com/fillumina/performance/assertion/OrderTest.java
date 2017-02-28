package com.fillumina.performance.assertion;

import com.fillumina.performance.util.stats.Ratio;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class OrderTest {

    @Test
    public void shouldBeLessThanWithNoTolerance() {
        Order order = new Order(Ratio.ZERO);
        assertTrue(order.lt(1, 2));
    }

    @Test
    public void shouldNotBeLessThanWithNoTolerance() {
        Order order = new Order(Ratio.ZERO);
        assertFalse(order.lt(2, 1));
    }

    @Test
    public void shouldLtNotBeEqualsWithNoTolerance() {
        Order order = new Order(Ratio.ZERO);
        assertFalse(order.lt(3, 3));
    }

    @Test
    public void shouldBeLessThanWithHighTolerance() {
        Order order = new Order(Ratio.percentage(20));
        assertTrue(order.lt(8, 9));
    }

    @Test
    public void shouldBeGreaterThanWithNoTolerance() {
        Order order = new Order(Ratio.ZERO);
        assertTrue(order.gt(2, 1));
    }

    @Test
    public void shouldNotBeGreaterThanWithNoTolerance() {
        Order order = new Order(Ratio.ZERO);
        assertFalse(order.gt(1, 2));
    }

    @Test
    public void shouldBeGreaterThanWithHighTolerance() {
        Order order = new Order(Ratio.P_100);
        assertFalse(order.gt(1, 2));
    }

    @Test
    public void shouldGtNotBeEqualsWithNoTolerance() {
        Order order = new Order(Ratio.ZERO);
        assertFalse(order.gt(3, 3));
    }

    @Test
    public void shouldBeGreaterThanWithTolerance() {
        Order order = new Order(Ratio.percentage(20));
        assertTrue(order.lt(20, 18));
    }

}
