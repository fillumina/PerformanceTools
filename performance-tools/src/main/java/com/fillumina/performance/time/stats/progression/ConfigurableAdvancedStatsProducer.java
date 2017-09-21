package com.fillumina.performance.time.stats.progression;

import com.fillumina.performance.executor.TestOperation;
import com.fillumina.performance.time.stats.SingleTimeStats;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureDifference;
import com.fillumina.performance.util.stats.MeasureSum;
import com.fillumina.performance.util.tname.TName;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
// TODO adapt something directly on samples insto collectors not here on stats
public class ConfigurableAdvancedStatsProducer<T extends TimeStats> {

    private final List<TestOperation> operations;

    public ConfigurableAdvancedStatsProducer(
            List<TestOperation> testOperations) {
        this.operations = testOperations;
    }

    protected T executeOperations(T stats) {
        if (operations == null || operations.isEmpty()) {
            return stats;
        }
        T current = stats;
        for (TestOperation to : operations) {
            TName parent = stats.getNames().iterator().next();
            final String na = to.getFirstTestName();
            final String nb = to.getSecondTestName();
            TName name = parent.append(to.toString());
            Measure ma = stats.getMeasure(na);
            Measure mb = stats.getMeasure(nb);
            Measure result = null;
            switch (to.getOperation()) {
                case ADD:
                    result = new MeasureSum(ma, mb);
                    break;

                case SUBTRACT:
                    result = new MeasureDifference(ma, mb);
                    break;

                default:
                    throw new AssertionError("DEV: case not considered: " +
                            to.getOperation().toString());
            }
////            SingleTimeStats sa = stats.getSingleStatsMap().get(na);
////            SingleTimeStats sb = stats.getSingleStatsMap().get(nb);
////            final long samples = (sa.getSamples() + sb.getSamples()) / 2;
//
//            SingleTimeStats single = new SingleTimeStats(
//                    name,
//                    new DimensionalMeasure(
//                            AverageTimeUnit.NANOSECONDS, result),
//                    (sa.getTotalIterations() + sb.getTotalIterations()) / 2,
//                    samples, samples,
//                    sa.getTotalTime() + sb.getTotalTime());
//
//            current = addNewSingleStats(current, single);
        }
        return current;
    }

    @SuppressWarnings("unchecked")
    private T addNewSingleStats(T stats, SingleTimeStats single) {
        return null;//TimeStats.add(stats, single);
    }
}
