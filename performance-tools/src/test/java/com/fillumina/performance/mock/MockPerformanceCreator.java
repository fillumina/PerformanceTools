package com.fillumina.performance.mock;

import com.fillumina.performance.speed.sample.IterationTimeCollector;
import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.speed.stats.SingleSpeedStats;
import com.fillumina.performance.speed.stats.SpeedSampleCollector;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.stats.NormalDistributionMeasureBuilder;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Francesco Illuminati
 */
public class MockPerformanceCreator {

    public static SingleStatsBuilder singleSpeedStatsBuilder() {
        return new SingleStatsBuilder();
    }

    public static class SingleStatsBuilder {
        private String name;
        private DimensionalMeasure timeNs;
        private long totalIterations;
        private long samples;
        private long originalSamples;
        private long totalTime;

        public SingleStatsBuilder name(final String value) {
            this.name = value;
            return this;
        }

        public SingleStatsBuilder timeNs(final DimensionalMeasure value) {
            this.timeNs = value;
            return this;
        }

        public SingleStatsBuilder totalIterations(final long value) {
            this.totalIterations = value;
            return this;
        }

        public SingleStatsBuilder samples(final long value) {
            this.samples = value;
            return this;
        }

        public SingleStatsBuilder originalSamples(final long value) {
            this.originalSamples = value;
            return this;
        }

        public SingleStatsBuilder totalTime(final long value) {
            this.totalTime = value;
            return this;
        }

        public SingleSpeedStats build() {
            return new SingleSpeedStats(name, timeNs, totalIterations, samples,
                    originalSamples, totalTime);
        }
    }

    public static SpeedStatsBuilder speedStatsBuilder() {
        return new SpeedStatsBuilder();
    }

    public static class SpeedStatsBuilder {
        private long iterationsPerSample = 1L;
        private Ratio confidence = Ratio.P_95;
        private final List<Data> dataList = new ArrayList<>();

        public SpeedStatsBuilder iterationsPerSample(final long value) {
            this.iterationsPerSample = value;
            return this;
        }

        public SpeedStatsBuilder confidence(final Ratio value) {
            this.confidence = value;
            return this;
        }

        public Data addTest(String name) {
            return new Data(name);
        }

        /**
         * Creates the {@link SpeedStats} based on coincidental samples.
         *
         * @param iterations how many iterations
         * @param data array:
         *        <ol>
         *        <li>name (String)
         *        <li>iterations (int)
         *        <li>time (long)
         *        </ol>
         * @return the created {@link SpeedStats}
         */
        public SpeedStats buildWithCoincidentalValues() {
            SpeedSampleCollector speedSampleCollector = new SpeedSampleCollector();

            int[] counter = new int[dataList.size()];

            int index = 0;
            for (Data data : dataList) {
                counter[index] = data.samples;
                index++;
            }

            boolean added;
            do {
                added = false;
                index = 0;
                IterationTimeCollector collector = new IterationTimeCollector();
                for (Data data : dataList) {
                    if (counter[index] > 0) {
                        collector.add(data.name,
                                (long)data.mean * iterationsPerSample,
                                (int)iterationsPerSample);
                        added = true;
                    }
                    counter[index]--;
                    index++;
                }
                speedSampleCollector.add(collector.createPerformanceSample());
            } while(added);

            return speedSampleCollector.createPerformanceStatsAndFilterIf(false);
        }

        /** Creates the {@link SpeedStats} based on normal distribution. */
        public SpeedStats buildWithNormalDistribution() {
            SpeedSampleCollector speedSampleCollector = getSampleCollector();
            return speedSampleCollector.createPerformanceStatsAndFilterIf(false);
        }

        public SpeedSampleCollector getSampleCollector() {
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
            SpeedSampleCollector speedSampleCollector = new SpeedSampleCollector();
            boolean added;
            do {
                added = false;
                index = 0;
                IterationTimeCollector collector = new IterationTimeCollector();
                for (Data data : dataList) {
                    Iterator<Double> it = iterators[index];

                    if (counter[index] > 0) {
                        long time = (long) (it.next() * iterationsPerSample);
                        collector.add(data.name, time, (int)iterationsPerSample);
                        added = true;
                    }

                    counter[index]--;
                    index++;
                }
                speedSampleCollector.add(collector.createPerformanceSample());
            } while(added);
            return speedSampleCollector;
        }

        public class Data {
            private String name;
            private double mean;
            private double stdev;
            private int samples = 100;

            public Data(String name) {
                this.name = name;
            }

            public Data timeNs(final double value) {
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

            public SpeedStatsBuilder endTest() {
                SpeedStatsBuilder.this.dataList.add(this);
                return SpeedStatsBuilder.this;
            }
        }

    }

    public static SpeedSampleBuilder speedSampleBuilder() {
        return new SpeedSampleBuilder();
    }

    public static class SpeedSampleBuilder {

        private List<TestSample> list = new ArrayList<>();

        public TestSample addTest(String name) {
            return new TestSample(name);
        }

        public class TestSample {
            private String name;
            private long timeNs;
            private long iterations = 10L;

            public TestSample(String name) {
                this.name = name;
            }

            public TestSample timePerOp(final long value) {
                this.timeNs = value;
                return this;
            }

            public TestSample iterations(final long value) {
                this.iterations = value;
                return this;
            }

            public SpeedSampleBuilder endTest() {
                SpeedSampleBuilder.this.list.add(this);
                return SpeedSampleBuilder.this;
            }
        }

        public SpeedSample createSample() {
            IterationTimeCollector collector = new IterationTimeCollector();
            for (TestSample ts : list) {
                collector.add(ts.name, ts.timeNs * ts.iterations, (int)ts.iterations);
            }
            return collector.createPerformanceSample();
        }
    }

    public static void main(final String[] args) {
        printSample();
        printNormalDistributionSpeedStats();
        printCoincidentalSpeedStats();
    }

    private static void printCoincidentalSpeedStats() {
        System.out.println("STATS (coincidental values):");
        System.out.println(speedStatsBuilder()
                .iterationsPerSample(100)
                .confidence(Ratio.decimal(0.1))
                .addTest("first")
                    .timeNs(10.0)
                    .stdev(5.0)
                    .samples(80)
                .endTest()
                .addTest("second")
                    .timeNs(20.0)
                    .stdev(7.0)
                    .samples(90)
                .endTest()
                .buildWithCoincidentalValues());
    }

    private static void printNormalDistributionSpeedStats() {
        System.out.println("STATS (normal distribution):");
        System.out.println(speedStatsBuilder()
                .iterationsPerSample(100)
                .confidence(Ratio.decimal(0.1))
                .addTest("first")
                    .timeNs(10.0)
                    .stdev(5.0)
                    .samples(80)
                .endTest()
                .addTest("second")
                    .timeNs(20.0)
                    .stdev(7.0)
                    .samples(90)
                .endTest()
                .buildWithNormalDistribution());
    }

    private static void printSample() {
        System.out.println("SAMPLE:");
        System.out.println(speedSampleBuilder()
                .addTest("first")
                    .timePerOp(10)
                    .iterations(2_000)
                .endTest()
                .addTest("second")
                    .timePerOp(50)
                    .iterations(500)
                .endTest()
                .createSample());
    }
}
