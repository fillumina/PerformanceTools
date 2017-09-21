package com.fillumina.performance.mock;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.time.sample.TimeSampleCollector;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.util.stats.NormalDistributionMeasureBuilder;
import com.fillumina.performance.util.stats.Ratio;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

/**
 *
 * @author Francesco Illuminati
 */
public class SpeedStatsMock {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private long iterationsPerSample = 1L;
        private Ratio confidence = Ratio.P_95;
        private final List<Data> dataList = new ArrayList<>();

        public Builder iterationsPerSample(final long value) {
            this.iterationsPerSample = value;
            return this;
        }

        public Builder confidence(final Ratio value) {
            this.confidence = value;
            return this;
        }

        public Data addTest(String name) {
            return new Data(name);
        }

        /**
         * Creates the {@link TimeStats} based on coincidental samples.
         *
         * @param iterations how many iterations
         * @param data array:
         *        <ol>
         *        <li>name (String)
         *        <li>iterations (int)
         *        <li>time (long)
         *        </ol>
         * @return the created {@link TimeStats}
         */
        @SuppressWarnings("unchecked")
        public <T extends TimeStats> T buildWithCoincidentalValues(
                Supplier<TimeSampleCollector<T>> timeCollectorCreator) {
            TimeSampleCollector<T> speedSampleCollector =
                    createSampleCollector(timeCollectorCreator);

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
                TimeSampleCollector collector = new TimeSampleCollector();
                for (Data data : dataList) {
                    if (counter[index] > 0) {
                        collector.add(TN.tname(data.name),
                                (long)data.mean * iterationsPerSample,
                                (int)iterationsPerSample);
                        added = true;
                    }
                    counter[index]--;
                    index++;
                }
                speedSampleCollector.add(collector.createPerformanceSample());
            } while(added);

            return speedSampleCollector.createStatsAndFilterIf(false);
        }

        /**
         * Creates the {@link TimeStats} based on pseudo normal samples
         * (they aren't really normally distributed but creates some
         * data for standard deviation and errors to show non zero values).
         * The result is deterministic.
         *
         * @param iterations how many iterations
         * @param data array:
         *        <ol>
         *        <li>name (String)
         *        <li>iterations (int)
         *        <li>time (long)
         *        </ol>
         * @return the created {@link TimeStats}
         */
        @SuppressWarnings("unchecked")
        public <T extends TimeStats> T buildWithSyntheticNormalValues(
                Supplier<TimeSampleCollector<T>> timeCollectorCreator) {
            TimeSampleCollector<T> speedSampleCollector =
                    createSampleCollector(timeCollectorCreator);

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
                TimeSampleCollector collector = new TimeSampleCollector();
                for (Data data : dataList) {
                    int samples = counter[index];
                    if (samples > 0) {
                        double linear = ((data.samples >> 1) - samples) * 1.0 / data.samples;
                        double variation = data.stdev * data.mean * linear / 8;
                        collector.add(TN.tname(data.name),
                                (long) ((data.mean + variation) * iterationsPerSample),
                                (int)iterationsPerSample);
                        added = true;
                    }
                    counter[index]--;
                    index++;
                }
                speedSampleCollector.add(collector.createPerformanceSample());
            } while(added);

            return speedSampleCollector.createStatsAndFilterIf(false);
        }

        @SuppressWarnings("unchecked")
        protected <T extends TimeStats> TimeSampleCollector<T>
                createSampleCollector(
                        Supplier<TimeSampleCollector<T>> timeCollectorCreator) {
            return timeCollectorCreator.get();
        }

        /** *  Creates the {@link TimeStats} based on normal distribution. */
        public <T extends TimeStats> T buildWithNormalDistribution(
                Supplier<TimeSampleCollector<T>> timeCollectorCreator) {
            TimeSampleCollector<T> speedSampleCollector =
                    getSampleCollector(timeCollectorCreator);
            return speedSampleCollector.createStatsAndFilterIf(false);
        }

        public <T extends TimeStats> TimeSampleCollector<T>
                getSampleCollector(
                        Supplier<TimeSampleCollector<T>> timeCollectorCreator) {
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
            TimeSampleCollector<T> speedSampleCollector =
                    createSampleCollector(timeCollectorCreator);
            boolean added;
            do {
                added = false;
                index = 0;
                TimeSampleCollector collector = new TimeSampleCollector();
                for (Data data : dataList) {
                    Iterator<Double> it = iterators[index];

                    if (counter[index] > 0) {
                        long time = (long) (it.next() * iterationsPerSample);
                        collector.add(TN.tname(data.name),
                                time, (int)iterationsPerSample);
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

            public Builder endTest() {
                Builder.this.dataList.add(this);
                return Builder.this;
            }
        }
    }

    public static void main(final String[] args) {
        printNormalDistributionSpeedStats();
        printCoincidentalSpeedStats();
        printSyntheticSpeedStats();
    }

    private static void printCoincidentalSpeedStats() {
        System.out.println("STATS (coincidental values):");
        System.out.println(builder()
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
                .buildWithCoincidentalValues(
                        TimeSampleCollector::createAverageTimeCollector));
    }

    private static void printNormalDistributionSpeedStats() {
        System.out.println("STATS (normal distribution):");
        System.out.println(builder()
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
                .buildWithNormalDistribution(
                        TimeSampleCollector::createThroughputCollector));
    }

    private static void printSyntheticSpeedStats() {
        System.out.println("STATS (synthetic pseudo normal distribution):");
        System.out.println(builder()
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
                .buildWithSyntheticNormalValues(
                        TimeSampleCollector::createAverageTimeCollector));
    }
}
