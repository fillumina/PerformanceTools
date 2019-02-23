package com.fillumina.performance.mock;

import com.fillumina.performance.executor.PN;
import com.fillumina.performance.executor.param.Option;
import com.fillumina.performance.executor.param.OptionBuilder;
import com.fillumina.performance.executor.param.ParameterizedTestProducer;
import com.fillumina.performance.executor.param.SequencedTestProducer;
import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.executor.stats.MixedStatsHolder;
import com.fillumina.performance.executor.stats.MixedStatsHolderCreator;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.StatsType;
import com.fillumina.performance.util.collection.IndexedHashMap;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.stats.NormalDistributionMeasureBuilder;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.pathname.PathName;
import com.fillumina.performance.util.unit.Magnitude;
import com.fillumina.performance.util.unit.Unit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati
 */
public class StatsMockBuilder {
    private final List<Data> dataList = new ArrayList<>();
    private final StatsType statsType;
    private CharSequence name;
    private Ratio confidence = Ratio.P_95;
    private Map<PathName, Map<String,Option>> parameters = new IndexedHashMap<>();
    private Map<PathName, Map<String,Option>> sequences = new IndexedHashMap<>();

    public static Stats create(Object... objs) {
        return createWithTypes(MockStatsType.INSTANCE, PN.EMPTY, Magnitude.UNIT, objs);
    }

    public static Stats createWithTypes(
            StatsType statsType,
            PathName title,
            Unit<?> unit,
            Object... objs) {
        StatsMockBuilder builder = new StatsMockBuilder(statsType);
        for (int i=0,l=objs.length; i<l; i+=2) {
            String testName = (String) objs[i];
            double testMean = (double) objs[i+1];
            PathName tn = title.append(testName);
            builder.addTest(tn).mean(testMean).stdev(2.0).endTest();
        }
        return builder.buildWithSyntheticNormalValues(unit)
                .getFirstStatsHolder().getStats();
    }

    public StatsMockBuilder() {
        this(MockStatsType.INSTANCE);
    }

    public StatsMockBuilder(StatsType statsType) {
        this.statsType = statsType;
    }

    public StatsMockBuilder name(CharSequence name) {
        this.name = name;
        return this;
    }

    public StatsMockBuilder confidence(final Ratio value) {
        this.confidence = value;
        return this;
    }

    public Data addTest(CharSequence name) {
        return new Data(name);
    }

    public OptionBuilder<StatsMockBuilder>
                addParametersForTest(PathName name) {
        return new OptionBuilder<>( o -> {
                    parameters.put(name, o);
                    return StatsMockBuilder.this;
                });
    }

    public OptionBuilder<StatsMockBuilder>
                addSequencesForTest(PathName name) {
        return new OptionBuilder<>( o -> {
                    sequences.put(name, o);
                    return StatsMockBuilder.this;
                });
    }

    public MixedStatsHolder buildWithCoincidentalValues(Unit<?> unit) {
        int[] counter = new int[dataList.size()];

        int index = 0;
        for (Data data : dataList) {
            counter[index] = data.samples;
            index++;
        }

        MixedStatsHolderCreator statsCreator =
                new MixedStatsHolderCreator(PN.pname(name));
        boolean added;
        do {
            added = false;
            index = 0;

            SampleCreator.Builder sampleBuilder = SampleCreator.builder(
                    unit,
                    m -> new Sample(statsType, m));
            for (Data data : dataList) {
                if (counter[index] > 0) {
                    sampleBuilder.add(data.name, data.mean);
                    added = true;
                }
                counter[index]--;
                index++;
            }
            if (added) {
                statsCreator.addSample(sampleBuilder.buildSample());
            }
        } while(added);

        MixedStatsHolder mah = setOptionsPayload(statsCreator);
        return mah;
    }

    public MixedStatsHolder buildWithSyntheticNormalValues(Unit<?> unit) {
        int[] counter = new int[dataList.size()];

        int index = 0;
        for (Data data : dataList) {
            counter[index] = data.samples;
            index++;
        }

        MixedStatsHolderCreator statsCreator =
                new MixedStatsHolderCreator(PN.pname(name));
        boolean added;
        do {
            added = false;
            index = 0;
            SampleCreator.Builder sampleBuilder = SampleCreator.builder(
                    unit,
                    m -> new Sample(statsType, m));
            for (Data data : dataList) {
                int samples = counter[index];
                if (samples > 0) {
                    double linear = ((data.samples >> 1) - samples) * 1.0 / data.samples;
                    double variation = data.stdev * data.mean * linear / 8;
                    sampleBuilder.add(data.name, data.mean + variation);
                    added = true;
                }
                counter[index]--;
                index++;
            }
            if (added) {
                statsCreator.addSample(sampleBuilder.buildSample());
            }
        } while(added);

        MixedStatsHolder mah = setOptionsPayload(statsCreator);
        return mah;
    }

    private MixedStatsHolder setOptionsPayload(
            MixedStatsHolderCreator statsCreator) {
        final MixedStatsHolder mah =
                statsCreator.getMixedAssertableHolder(ListFilter.identity());
        final Stats stats = mah.getFirstStatsHolder().getStats();
        stats.putPayload(ParameterizedTestProducer.PARAMETERS, parameters);
        stats.putPayload(SequencedTestProducer.SEQUENCES, sequences);
        return mah;
    }

    public MixedStatsHolder buildWithNormalDistribution(Unit<?> unit) {
        @SuppressWarnings("unchecked")
        Iterator<Double>[] iterators = new Iterator[dataList.size()];
        int[] counter = new int[dataList.size()];
        int index = 0;
        for (Data data : dataList) {
            double tolerance = 1.0 - confidence.getDecimal();
            iterators[index] = new NormalDistributionMeasureBuilder(
                        data.mean, data.stdev, tolerance, data.samples)
                    .iterator();
            counter[index] = data.samples;
            index++;
        }

        MixedStatsHolderCreator statsCreator =
                new MixedStatsHolderCreator(PN.pname(name));
        boolean added;
        do {
            added = false;
            index = 0;
            SampleCreator.Builder sampleBuilder =
                    SampleCreator.builder(unit, m -> new Sample(statsType, m));

            for (Data data : dataList) {
                Iterator<Double> it = iterators[index];

                if (counter[index] > 0) {
                    sampleBuilder.add(data.name, it.next());
                    added = true;
                }

                counter[index]--;
                index++;
            }
            if (added) {
                statsCreator.addSample(sampleBuilder.buildSample());
            }
        } while(added);

        MixedStatsHolder mah = setOptionsPayload(statsCreator);
        return mah;
    }

    public class Data {
        private CharSequence name;
        private double mean;
        private double stdev;
        private int samples = 100;

        public Data(CharSequence name) {
            this.name = name;
        }

        public Data mean(final double value) {
            this.mean = value;
            return this;
        }

        public Data stdev(final double value) {
            this.stdev = value;
            return this;
        }

        public Data samples(final int value) {
            this.samples = value;
            return this;
        }

        public StatsMockBuilder endTest() {
            StatsMockBuilder.this.dataList.add(this);
            return StatsMockBuilder.this;
        }
    }
}
