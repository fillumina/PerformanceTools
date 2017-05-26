package com.fillumina.performance.template;

import com.fillumina.performance.util.stats.Ratio;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MixedAssertionTest {

    @Test
    public void testSpeedWithTolerance() {
        MixedAssertion<?> ma = new MixedAssertion<>();
        ma.speed()
                .assertValue("pippo").greaterThan(12)
                .tolerance(Ratio.ZERO)
                .assertValue("stocazzo").lessThan(34);
    }

}
