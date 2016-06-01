package com.fillumina.performance.sample;

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

        assertEquals(100, ita.getTime());
    }

    @Test
    public void shouldReturnInsertedIterations() {
        IterationTimeAccumulator ita = new IterationTimeAccumulator();
        ita.add(100, 5);

        assertEquals(5, ita.getIterations());
    }

    @Test
    public void shouldReturnTimeForIteration() {
        IterationTimeAccumulator ita = new IterationTimeAccumulator();
        ita.add(100, 5);

        assertEquals(20.0, ita.getTimePerIteration(), 0);
    }

    @Test
    public void shouldAccumulateDifferentTimes() {
        IterationTimeAccumulator ita = new IterationTimeAccumulator();
        ita.add(100, 5);
        ita.add(200, 10);

        assertEquals(300, ita.getTime());
        assertEquals(15, ita.getIterations());
        assertEquals(20.0, ita.getTimePerIteration(), 0);
    }
}
