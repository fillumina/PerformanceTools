package com.fillumina.performance.util.interval;

import java.math.BigDecimal;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class DecimalIntervalIteratorTest {

    @Test
    public void shouldIterateOnBigDecimal() {
        final List<BigDecimal> list =
                DecimalInterval
                    .from(BigDecimal.valueOf(1D))
                    .to(BigDecimal.valueOf(1.9D))
                    .step(BigDecimal.valueOf(0.1D))
                    .toList();

        assertEquals(10, list.size());
        assertEquals(BigDecimal.valueOf(1D), list.get(0));
        assertEquals(BigDecimal.valueOf(1.9D), list.get(9));
    }
}
