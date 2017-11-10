package com.fillumina.performance.time.sample;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class IterationTimeAccumulatorTest {

    @Test
    public void shouldReturnInsertedTimes() {
        IterationTimeAccumulator ita = new IterationTimeAccumulator();
        ita.add(100, 5);

        assertEquals(100, ita.getTimeNs());
    }

    @Test
    public void shouldReturnInsertedIterations() {
        IterationTimeAccumulator ita = new IterationTimeAccumulator();
        ita.add(100, 5);

        assertEquals(5, ita.getIterations());
    }

    @Test
    public void shouldAdd() {
        IterationTimeAccumulator ita = new IterationTimeAccumulator();
        ita.add(100, 5);
        ita.add(100, 5);

        assertEquals(10, ita.getIterations());
        assertEquals(200, ita.getTimeNs());
    }
}
