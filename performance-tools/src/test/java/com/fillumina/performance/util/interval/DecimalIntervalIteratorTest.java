package com.fillumina.performance.util.interval;

import java.math.BigDecimal;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class DecimalIntervalIteratorTest extends IntervalIteratorTestHelper {

    @Test
    public void shouldIterateOnBigDecimal() {
        final List<BigDecimal> list = getList(
                DecimalInterval.cycleFor()
                    .start(BigDecimal.valueOf(1D))
                    .end(BigDecimal.valueOf(1.9D))
                    .step(BigDecimal.valueOf(0.1D)));

        assertEquals(10, list.size());
        assertEquals(BigDecimal.valueOf(1D), list.get(0));
        assertEquals(BigDecimal.valueOf(1.9D), list.get(9));
    }
}
