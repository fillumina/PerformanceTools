package com.fillumina.performance.time.stats.progression;

import com.fillumina.performance.infrastructure.TestOperation;
import com.fillumina.performance.time.stats.SingleTimeStats;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureSum;
import com.fillumina.performance.util.stats.MeasureDifference;
import com.fillumina.performance.util.unit.DimensionalWrapperMeasure;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ConfigurableAdvancedStatsProducer<T extends TimeStats>
        extends ConfigurableStatsProducer<T> {

    public interface Configuration
            extends ConfigurableStatsProducer.Configuration {
        List<TestOperation> getTestOperation();
    }

    private final List<TestOperation> operations;

    public ConfigurableAdvancedStatsProducer(
            Configuration config,
            Strategy<T> strategy) {
        super(config, strategy);
        this.operations = config.getTestOperation();
    }

    @Override
    protected T executeTests() {
        final T stats = super.executeTests();
        if (operations == null || operations.isEmpty()) {
            return stats;
        }
        T current = stats;
        for (TestOperation to : operations) {
            TName parent = stats.getTestNames().iterator().next();
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
            SingleTimeStats sa = stats.getSingleStatsMap().get(na);
            SingleTimeStats sb = stats.getSingleStatsMap().get(nb);
            final long samples = (sa.getSamples() + sb.getSamples()) / 2;

            SingleTimeStats single = new SingleTimeStats(
                    name,
                    new DimensionalWrapperMeasure(result),
                    sa.getTotalIterations() + sb.getTotalIterations(),
                    samples, samples,
                    sa.getTotalTime() + sb.getTotalTime());

            current = addNewSingleStats(current, single);
        }
        return current;
    }

    @SuppressWarnings("unchecked")
    private T addNewSingleStats(T stats, SingleTimeStats single) {
        return (T) stats.add(single);
    }
}
