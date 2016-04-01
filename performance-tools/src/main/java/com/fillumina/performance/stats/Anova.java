package com.fillumina.performance.stats;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Anova {

    private final boolean valid;
    private final double meanSquareAmong;
    private final double meanSquareWithin;
    private final double anova;

    public Anova(double confidence,
            Measure globalStat, Measure... stats) {
        final int groups = stats.length;
        double sumOfSquareAmong = 0;
        double sumOfSquareWithin = 0;
        long count = 0;
        final double globalMean = globalStat.mean();
        double value;
        Measure stat;
        for (int i=0; i<groups; i++) {
            stat = stats[i];
            value = (stat.mean() - globalMean);
            sumOfSquareAmong += stat.count() * value * value;
            sumOfSquareWithin += stat.variance() * stat.count();
            count += stats[i].count();
        }

        long dfNum = groups - 1;
        long dfDen = count - groups;

        meanSquareAmong = sumOfSquareAmong / dfNum;
        meanSquareWithin = sumOfSquareWithin / dfDen;
        anova = meanSquareAmong / meanSquareWithin;


        double f = StatFunctions.inverseFishF(1 - confidence, dfNum, dfDen);

        this.valid = anova > f;
    }

    /**
     * @see <a href='https://web.mst.edu/~psyworld/tukeyssteps.htm'>
     *  Tukey's HSD Posto Hoc Test</a>
     * @return
     */
    public static boolean tukeyHsd() {
        return false;
    }

    public boolean isStatisticallyRelevant() {
        return valid;
    }

    public double getMeanSquareAmong() {
        return meanSquareAmong;
    }

    public double getMeanSquareWithin() {
        return meanSquareWithin;
    }

    public double getAnova() {
        return anova;
    }
}
