package com.fillumina.performance.util.stats;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class FixedSampleMeanTest {

    @Test(expected = RuntimeException.class)
    public void shouldBeTheMeanOf0ValuesBe0() {
        FixedSampleMean fsm = new FixedSampleMean(10);

        fsm.getMean();
    }

    @Test
    public void shouldTheMeanOf1ValueBeTheValue() {
        FixedSampleMean fsm = new FixedSampleMean(10);

        fsm.addSample(12.3);

        assertEquals(12.3, fsm.getMean(), 0);
    }

    @Test
    public void shouldTheMeanOf2ValuesBeTheMeanOfThem() {
        FixedSampleMean fsm = new FixedSampleMean(10);

        fsm.addSample(12.3);
        fsm.addSample(45.6);

        assertEquals((12.3 + 45.6)/2, fsm.getMean(), 0);
    }

    @Test
    public void shouldRemoveTheOldValuesFromTheMean() {
        FixedSampleMean fsm = new FixedSampleMean(3);

        fsm.addSample(1);
        fsm.addSample(2);
        fsm.addSample(3);
        fsm.addSample(4);
        fsm.addSample(5);
        fsm.addSample(6);

        assertEquals((4.0 + 5.0 + 6.0)/3.0, fsm.getMean(), 0);
    }
}
