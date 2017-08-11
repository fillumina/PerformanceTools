package com.fillumina.performance.time.stats.strgen;

import static com.fillumina.performance.mock.SpeedStatsMock.builder;
import com.fillumina.performance.time.stats.TimeSampleCollector;
import com.fillumina.performance.util.stats.Ratio;

/**
 * For visual inspection of different {@link StringGenerator}s.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TimeStatsAllGeneratorTestApp {

    public static void main(final String[] args) {
        tableAverageTime();
        tableThroughput();

        singleAverageTime();
        singleThroughput();

        parallelAverageTime();
        parallelThroughput();
    }

    private static void tableAverageTime() {
        System.out.println("STATS (tableAverageTime):");
        System.out.println(
                builder()
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
                    .buildWithSyntheticNormalValues(TimeSampleCollector::createAverageTimeCollector));
    }

    private static void tableThroughput() {
        System.out.println("STATS (tableThroughput):");
        System.out.println(
                builder()
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
                            TimeSampleCollector::createThroughputCollector));
    }

    private static void singleAverageTime() {
        System.out.println("STATS (singleAverageTime):");
        System.out.println(
                builder()
                    .iterationsPerSample(100)
                    .confidence(Ratio.decimal(0.1))
                    .addTest("single")
                        .timeNs(10.0)
                        .stdev(5.0)
                        .samples(80)
                    .endTest()
                    .buildWithSyntheticNormalValues(
                            TimeSampleCollector::createAverageTimeCollector));
    }

    private static void singleThroughput() {
        System.out.println("STATS (singleThroughput):");
        System.out.println(
                builder()
                    .iterationsPerSample(100)
                    .confidence(Ratio.decimal(0.1))
                    .addTest("single")
                        .timeNs(10.0)
                        .stdev(5.0)
                        .samples(80)
                    .endTest()
                    .buildWithSyntheticNormalValues(
                            TimeSampleCollector::createThroughputCollector));
    }

    private static void parallelAverageTime() {
        System.out.println("STATS (parallelAverageTime):");
        System.out.println(
                builder()
                    .iterationsPerSample(100)
                    .confidence(Ratio.decimal(0.1))
                    .addTest("single")
                        .timeNs(10.0)
                        .stdev(5.0)
                        .samples(80)
                    .endTest()
                    .addTest("worker 0")
                        .timeNs(9.0)
                        .stdev(7.0)
                        .samples(90)
                    .endTest()
                    .addTest("worker 1")
                        .timeNs(10.0)
                        .stdev(7.0)
                        .samples(90)
                    .endTest()
                    .addTest("worker 2")
                        .timeNs(11.0)
                        .stdev(7.0)
                        .samples(90)
                    .endTest()
                    .addTest("parallel")
                        .timeNs(20.0)
                        .stdev(7.0)
                        .samples(90)
                    .endTest()
                    .buildWithSyntheticNormalValues(
                            TimeSampleCollector::createAverageTimeCollector));
    }

    private static void parallelThroughput() {
        System.out.println("STATS (parallelThroughput):");
        System.out.println(
                builder()
                    .iterationsPerSample(100)
                    .confidence(Ratio.decimal(0.1))
                    .addTest("single")
                        .timeNs(10.0)
                        .stdev(5.0)
                        .samples(80)
                    .endTest()
                    .addTest("worker 0")
                        .timeNs(9.0)
                        .stdev(7.0)
                        .samples(90)
                    .endTest()
                    .addTest("worker 1")
                        .timeNs(10.0)
                        .stdev(7.0)
                        .samples(90)
                    .endTest()
                    .addTest("worker 2")
                        .timeNs(11.0)
                        .stdev(7.0)
                        .samples(90)
                    .endTest()
                    .addTest("parallel")
                        .timeNs(20.0)
                        .stdev(7.0)
                        .samples(90)
                    .endTest()
                    .buildWithSyntheticNormalValues(
                            TimeSampleCollector::createThroughputCollector));
    }

}
