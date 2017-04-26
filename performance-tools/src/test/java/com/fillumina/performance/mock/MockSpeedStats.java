package com.fillumina.performance.mock;

import com.fillumina.performance.speed.sample.IterationTimeCollector;
import com.fillumina.performance.speed.stats.SpeedSampleCollector;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.stats.NormalDistributionMeasureBuilder;
import com.fillumina.performance.util.stats.Ratio;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Francesco Illuminati
 */
public class MockSpeedStats {

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

            public Builder endTest() {
                Builder.this.dataList.add(this);
                return Builder.this;
            }
        }
    }

    public static void main(final String[] args) {
        printNormalDistributionSpeedStats();
        printCoincidentalSpeedStats();
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
                .buildWithCoincidentalValues());
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
                .buildWithNormalDistribution());
    }
}
