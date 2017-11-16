package com.fillumina.performance.util.sequence;

import java.math.BigDecimal;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class DecimalSequenceTest {

    @Test
    public void shouldIterateOnBigDecimal() {
        final List<BigDecimal> list =
                DecimalSequence
                    .from(BigDecimal.valueOf(1D))
                    .to(BigDecimal.valueOf(1.9D))
                    .step(BigDecimal.valueOf(0.1D))
                    .toList();

        assertEquals(10, list.size());
        assertEquals(BigDecimal.valueOf(1D), list.get(0));
        assertEquals(BigDecimal.valueOf(1.8D), list.get(8));
    }

    @Test
    public void shouldIterateUntil() {
        final List<BigDecimal> list =
                DecimalSequence
                    .from(BigDecimal.valueOf(0.0))
                    .until(BigDecimal.valueOf(10D))
                    .step(BigDecimal.valueOf(1D))
                    .toList();

        assertEquals(10, list.size());
        for (int i=0; i<10; i++) {
            assertEquals(BigDecimal.valueOf(1.0 * i), list.get(i));
        }
    }
}
