package com.fillumina.performance.mock;

import com.fillumina.performance.executor.sample.AbstractSampleProducer;
import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.executor.sample.SampleValue;
import com.fillumina.performance.executor.stats.StatsType;
import com.fillumina.performance.util.pathname.PathName;
import com.fillumina.performance.util.pathname.PathNamedMap;
import com.fillumina.performance.util.stats.NormalDistributionMeasureBuilder;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.Unit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati
 */
public class SampleProducerMockBuilder {
    private final List<Data> dataList = new ArrayList<>();
    private final StatsType statsType;
    private int samples = 33;
    private Ratio confidence = Ratio.P_95;

    public SampleProducerMockBuilder() {
        this(MockStatsType.INSTANCE);
    }

    public SampleProducerMockBuilder(StatsType statsType) {
        this.statsType = statsType;
    }

    public class SampleProducerMock
            extends AbstractSampleProducer<SampleProducerMock> {

        private final List<PathNamedMap<SampleValue>> list;
        private int index;

        public SampleProducerMock(List<PathNamedMap<SampleValue>> list) {
            this.list = list;
        }

        @Override
        public Map<StatsType, Sample> get() {
            final PathNamedMap<SampleValue> map = new PathNamedMap<>();
            getTests().forEach((CharSequence name, Runnable test) -> {
                PathName pname = (PathName) name;
                map.put(pname, list.get(index).get(pname));
            });
            index = (index + 1) % list.size();
            return Collections.singletonMap(statsType,
                    new Sample(statsType, map));
        }

        @Override
        public Map<StatsType, Sample> executeWithIterations(int... iterations) {
            return get();
        }

        public List<PathNamedMap<SampleValue>> getList() {
            return list;
        }
    }

    public SampleProducerMockBuilder samples(final int samples) {
        this.samples = samples;
        return this;
    }

    public SampleProducerMockBuilder confidence(final Ratio value) {
        this.confidence = value;
        return this;
    }

    public Data addTest(CharSequence name) {
        return new Data(name);
    }

    public SampleProducerMock buildWithCoincidentalValues(Unit<?> unit) {
        List<PathNamedMap<SampleValue>> list = new ArrayList<>();
        for (int i=0; i<samples; i++) {
            SampleCreator.Builder sampleBuilder = SampleCreator.builder(
                    unit,
                    m -> new Sample(statsType, m));
            dataList.forEach((Data d) -> sampleBuilder.add(d.name, d.mean));
            list.add(sampleBuilder.getMap());
        }

        return new SampleProducerMock(list);
    }

    public SampleProducerMock buildWithSyntheticNormalValues(Unit<?> unit) {
        List<PathNamedMap<SampleValue>> list = new ArrayList<>();
        for (int i=0; i<samples; i++) {
            SampleCreator.Builder sampleBuilder = SampleCreator.builder(
                    unit,
                    m -> new Sample(MockStatsType.INSTANCE, m));
            for (Data data : dataList) {
                double linear = ((samples >> 1) - i) * 1.0 / samples;
                double variation = data.stdev * data.mean * linear / 8;
                sampleBuilder.add(data.name, data.mean + variation);
            }
            list.add(sampleBuilder.getMap());
        }

        return new SampleProducerMock(list);
    }

    public SampleProducerMock buildWithNormalDistribution(Unit<?> unit) {
        @SuppressWarnings("unchecked")
        List<Iterator<Double>> iterators = new ArrayList<>(dataList.size());

        final double tolerance = 1 - confidence.getDecimal();
        for (Data data : dataList) {
            iterators.add(new NormalDistributionMeasureBuilder(
                    data.mean, data.stdev, tolerance, samples)
                    .iterator());
        }

        List<PathNamedMap<SampleValue>> list = new ArrayList<>();
        for (int i=0; i<samples; i++) {
            int index = 0;
            SampleCreator.Builder sampleBuilder = SampleCreator.builder(
                    unit,
                    m -> new Sample(statsType, m));

            for (Data data : dataList) {
                Iterator<Double> it = iterators.get(index);
                sampleBuilder.add(data.name, it.next());
                index++;
            }
            list.add(sampleBuilder.getMap());
        }
        return new SampleProducerMock(list);
    }

    public class Data {
        private CharSequence name;
        private double mean;
        private double stdev;

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

        public SampleProducerMockBuilder endTest() {
            SampleProducerMockBuilder.this.dataList.add(this);
            return SampleProducerMockBuilder.this;
        }
    }
}
