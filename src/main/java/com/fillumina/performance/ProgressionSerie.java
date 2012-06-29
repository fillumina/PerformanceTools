package com.fillumina.performance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 *
 * @author fra
 */
public class ProgressionSerie {
    public static final int MAXIMUM_MAGNITUDE = 10;
    public static final int MINIMUM_ITERATIONS = 100;
    public static final int SAMPLE_PER_MAGNITUDE = 10;
    private static final double DEFAULT_MAXIMUM_STANDARD_DEVIATION = 1.5D;

    private final PerformanceTimer pt;

    public ProgressionSerie(final PerformanceTimer pt) {
        this.pt = pt;
    }

    public void printAutoTune(final double maxStdDev) {
        System.out.println(CsvHelper.toCsvString(autoTune(maxStdDev)));
    }

    public Collection<Double> autoTune() {
        return autoTune(DEFAULT_MAXIMUM_STANDARD_DEVIATION);
    }

    // TODO: check if it stabilizes after too few iterations (check how long it takes)
    public Collection<Double> autoTune(final double maxStdDevAllowed) {
        final int executorSize = pt.getPerformance().getTimeMap().size();
        final double[][] samples = new double[SAMPLE_PER_MAGNITUDE][executorSize];
        for (int magnitude=0; magnitude< MAXIMUM_MAGNITUDE; magnitude++) {
            final int iterations =
                    (int)Math.round(MINIMUM_ITERATIONS * Math.pow(10, magnitude));

            final Statistics[] statistics =
                    Statistics.createArray(executorSize);

            for (int sample=0; sample<SAMPLE_PER_MAGNITUDE; sample++) {
                pt.execute(iterations);
                final Collection<Double> percentages =
                        pt.getPerformance().getPercentages();
                samples[sample] = convert(
                        percentages.toArray(new Double[percentages.size()]));
                System.out.println(Arrays.toString(samples[sample]));
                loadStatisticsData(statistics, samples[sample]);
                pt.clear();
            }

            double maxStdDev = calculateMaxVariance(statistics);
            System.out.println("Iterations: " + String.format("%,d", iterations));
            System.out.println("Max standard deviation = " + maxStdDev);

            if (maxStdDev < maxStdDevAllowed) {
                System.out.println("variance OK!");
                final Collection<Double> perc = calculateAverageValues(statistics);
                System.out.println("Percentage: " +
                       CsvHelper.toCsvString(perc));
                return perc;
            } else {
                System.out.println("Variance too high, proceeding with next iteration:");
            }
        }
        return null;
    }

    public void printSerie(final int baseTimes,
            final int maximumMagnitude,
            final int samplePerMagnitude) {
        int index = 0;
        for (int i=0; i< maximumMagnitude; i++) {
            for (int j=0; j<samplePerMagnitude; j++) {
                final long loops = Math.round(baseTimes * Math.pow(10, i));
                pt.execute((int)loops);
                final String idxStr = String.format("%5d, ", index);
                final String csv = new Presenter(pt).toCsvString(loops);
                System.out.println(idxStr + csv);
                pt.clear();
                index++;
            }
        }
    }

    private Collection<Double> calculateAverageValues(final Statistics[] statistics) {
        final Collection<Double> perc = new ArrayList<>(statistics.length);
        for (int e=0; e<perc.size(); e++) {
            perc.add(statistics[e].average());
        }
        return perc;
    }

    private double calculateMaxVariance(final Statistics[] statistics) {
        final Statistics stats = new Statistics();
        for (int e=0; e<statistics.length; e++) {
            stats.add(statistics[e].standardDeviation());
        }
        final double maxVariance = stats.max();
        return maxVariance;
    }

    private void loadStatisticsData(final Statistics[] statistics,
            final double[] samples) {
        for (int e=0; e<samples.length; e++) {
            statistics[e].add(samples[e]);
        }
    }

    private double[] convert(final Double[] array) {
        if (array == null) {
            return null;
        }
        final double[] result = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            final Double value = array[i];
            result[i] = value == null ? 0 : value;
        }
        return result;
    }

}
