package com.fillumina.performance.speed.stats.progression;

import com.fillumina.performance.infrastructure.TestOperation;
import com.fillumina.performance.speed.stats.SingleSpeedStats;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureAddiction;
import com.fillumina.performance.util.stats.MeasureDifference;
import com.fillumina.performance.util.unit.DimensionalWrapperMeasure;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ConfigurableAdvancedStatsProducer
        extends ConfigurableStatsProducer {

    public interface Configuration
            extends ConfigurableStatsProducer.Configuration {
        List<TestOperation> getTestOperation();
    }

    private final List<TestOperation> operations;

    public ConfigurableAdvancedStatsProducer(
            Configuration config,
            Strategy strategy) {
        super(config, strategy);
        this.operations = config.getTestOperation();
    }

    @Override
    protected SpeedStats executeTests() {
        final SpeedStats stats = super.executeTests();
        if (operations == null || operations.isEmpty()) {
            return stats;
        }
        SpeedStats current = stats;
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
                    result = new MeasureAddiction(ma, mb);
                    break;

                case SUBTRACT:
                    result = new MeasureDifference(ma, mb);
                    break;

                default:
                    throw new AssertionError("DEV: case not considered: " +
                            to.getOperation().toString());
            }
            SingleSpeedStats sa = stats.getSingleStatsMap().get(na);
            SingleSpeedStats sb = stats.getSingleStatsMap().get(nb);
            final long samples = (sa.getSamples() + sb.getSamples()) / 2;

            SingleSpeedStats single = new SingleSpeedStats(
                    name,
                    new DimensionalWrapperMeasure(result),
                    sa.getTotalIterations() + sb.getTotalIterations(),
                    samples, samples,
                    sa.getTotalTime() + sb.getTotalTime());

            current = SpeedStats.add(current, single);
        }
        return current;
    }
}
