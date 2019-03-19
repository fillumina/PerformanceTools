package com.fillumina.performance.time.sample;

import com.fillumina.performance.executor.PN;
import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.util.pathname.PathName;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TimeSampleCollectorTest {

    @Test
    public void shouldBeEmtpy() {
        TimeSampleCollector tsc = new TimeSampleCollector();
        assertTrue(tsc.isEmpty());
    }

    @Test
    public void shouldReturnTotalTime() {
        PathName one = PN.pname("one");
        PathName two = PN.pname("two");

        TimeSampleCollector tsc = new TimeSampleCollector();

        tsc.add(one, 100, 10);
        tsc.add(two, 50, 20);

        tsc.add(one, 100, 10);
        tsc.add(two, 50, 20);

        assertEquals(300, tsc.getTotalTimeNs());
    }

    @Test
    public void shouldReturnAverageTimeSample() {
        PathName one = PN.pname("one");
        PathName two = PN.pname("two");

        TimeSampleCollector tsc = new TimeSampleCollector();

        tsc.add(one, 100, 10);
        tsc.add(two, 50, 10);

        tsc.add(one, 100, 10);
        tsc.add(two, 50, 10);

        Sample sample = tsc.buildAverageTimeSample();

        List<PathName> names = sample.getTestNames();

        assertEquals(2, names.size());
        assertTrue(names.containsAll(Arrays.asList("one", "two")));

        assertEquals(10, sample.getQuantity("one").getValue(), 0);
        assertEquals(5, sample.getQuantity("two").getValue(), 0);
    }

    @Test
    public void shouldReturnThroughputSample() {
        PathName one = PN.pname("one");
        PathName two = PN.pname("two");

        TimeSampleCollector tsc = new TimeSampleCollector();

        tsc.add(one, 100, 10);
        tsc.add(two, 50, 10);

        tsc.add(one, 100, 10);
        tsc.add(two, 50, 10);

        Sample sample = tsc.buildThroughputSample();

        List<PathName> names = sample.getTestNames();

        assertEquals(2, names.size());
        assertTrue(names.containsAll(Arrays.asList("one", "two")));

        // 20 op / 200 ns = 0.1 op/ns -> 0.1*10^9 op/s -> 1*10^8 op/s
        assertEquals(1E8, sample.getQuantity("one").getValue(), 0);
        assertEquals(2E8, sample.getQuantity("two").getValue(), 0);
    }


}
