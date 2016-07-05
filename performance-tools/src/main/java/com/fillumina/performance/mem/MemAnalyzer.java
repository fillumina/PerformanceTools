package com.fillumina.performance.mem;

import com.fillumina.performance.infrastructure.AbstractPerformanceProducer;
import com.fillumina.performance.infrastructure.PerformanceHolder;
import com.fillumina.performance.infrastructure.StatsProducer;
import com.fillumina.performance.speed.sample.Testable;
import com.fillumina.performance.util.instrument.Instrumenter;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.DimensionalOnlineMeasure;
import com.fillumina.performance.util.unit.MemUnit;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemAnalyzer
        extends AbstractPerformanceProducer<MemAnalyzer, MemStats, Testable>
        implements StatsProducer<MemStats> {
    public static final int REPETITIONS =
            MemoryConsumption.INSTANCE.getByteGranularity();

    public static final MemAnalyzer INSTANCE = new MemAnalyzer();
    private static final MemoryConsumption MC = MemoryConsumption.INSTANCE;

    private MemAnalyzer() {}

    public Map<String, Measure> memoryUsage(final Map<String, Testable> tests) {
        Map<String, Measure> measures = new LinkedHashMap<>(tests.size());
        for (Map.Entry<String,Testable> entry : tests.entrySet()) {
            String name = entry.getKey();
            Testable test = entry.getValue();

            measures.put(name, memoryUsage(test));
        }
        return measures;
    }

    public DimensionalMeasure memoryUsage(Testable testable) {
        return new DimensionalOnlineMeasure(MemUnit.INSTANCE,
            Collections.singletonList(execute(testable)));
    }

    /**
     * @return how much memory {@link Testable} is using (16 byte granularity).
     */
    public long execute(Testable testable) {
        int i;
        int rep = REPETITIONS;
        MC.start();
        for (i = 0; i < rep; i++) {
            if (testable.test() == this) {
                throw new AssertionError("cannot happen");
            }
        }
        return nextPair(MC.getUsedMemory() / rep);
    }

    static long nextPair(long x) {
        return x + (x & 1);
    }

    @Override
    public PerformanceHolder<MemStats> execute() {
        MemStatsBuilder builder = new MemStatsBuilder(getTests().size());
        for (Map.Entry<String, Testable> entry : getTests().entrySet()) {
            final String testName = entry.getKey();
            final Testable testable = entry.getValue();

            Measure m = memoryUsage(testable);
            builder.add(testName, m);
        }
        return new PerformanceHolder<>(builder.build());
    }

    @Override
    public <T extends Instrumenter<StatsProducer<MemStats>>> T instrumentedBy(
            T instrumenter) {
        instrumenter.instrument(this);
        return instrumenter;
    }
}
