package com.fillumina.performance.mock;

import com.fillumina.performance.executor.MixedAssertableHolder;
import com.fillumina.performance.executor.TN;
import com.fillumina.performance.executor.stats.StatsCreator;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.stats.NormalDistributionMeasureBuilder;
import com.fillumina.performance.util.stats.Ratio;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Francesco Illuminati
 */
public class StatsMockBuilder {
    private CharSequence name;
    private Ratio confidence = Ratio.P_95;
    private final List<Data> dataList = new ArrayList<>();

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

    public MixedAssertableHolder buildWithCoincidentalValues() {
        int[] counter = new int[dataList.size()];

        int index = 0;
        for (Data data : dataList) {
            counter[index] = data.samples;
            index++;
        }

        StatsCreator<StatsMock,SampleMock> statsCreator =
                new StatsCreator<>(TN.tname(name));
        boolean added;
        do {
            added = false;
            index = 0;

            SampleCreator.Builder<SampleMock> sampleBuilder =
                    SampleCreator.builder(m -> new SampleMock(m));
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

        return statsCreator.getMixedAssertableHolder(ListFilter.identity());
    }

    public MixedAssertableHolder buildWithSyntheticNormalValues() {
        int[] counter = new int[dataList.size()];

        int index = 0;
        for (Data data : dataList) {
            counter[index] = data.samples;
            index++;
        }

        StatsCreator<StatsMock,SampleMock> statsCreator =
                new StatsCreator<>(TN.tname(name));
        boolean added;
        do {
            added = false;
            index = 0;
            SampleCreator.Builder<SampleMock> sampleBuilder =
                    SampleCreator.builder(m -> new SampleMock(m));
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

        return statsCreator.getMixedAssertableHolder(ListFilter.identity());
    }

    public MixedAssertableHolder buildWithNormalDistribution() {
        @SuppressWarnings("unchecked")
        Iterator<Double>[] iterators = new Iterator[dataList.size()];
        int[] counter = new int[dataList.size()];
        int index = 0;
        for (Data data : dataList) {
            double tolerance = 1 - confidence.getDecimal();
            iterators[index] = new NormalDistributionMeasureBuilder(
                    data.mean, data.stdev, tolerance, data.samples)
                    .iterator();
            counter[index] = data.samples;
            index++;
        }

        StatsCreator<StatsMock,SampleMock> statsCreator =
                new StatsCreator<>(TN.tname(name));
        boolean added;
        do {
            added = false;
            index = 0;
            SampleCreator.Builder<SampleMock> sampleBuilder =
                    SampleCreator.builder(m -> new SampleMock(m));

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
        return statsCreator.getMixedAssertableHolder(ListFilter.identity());
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
