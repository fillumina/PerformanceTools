package com.fillumina.performance.util.filter;

import com.fillumina.performance.util.stats.OnlineMeasure;
import java.util.ArrayList;
import java.util.List;

/**
 * Removes the samples that lay outside 3 times the standard deviation
 * from the mean of the sample collection. Removing outliers is recommended
 * so that statistics are unaffected by spurious measures due to external
 * factors.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class OutlierEliminatorFilter<T> implements ListFilter<T, Double> {
    public static final double DEFAULT_STANDARD_FACTOR = 3.0;

    public static final OutlierEliminatorFilter<?> INSTANCE =
            new OutlierEliminatorFilter<>();

    private final double stdevFactor;

    @SuppressWarnings("unchecked")
    public static <S> OutlierEliminatorFilter<S> instance() {
        return (OutlierEliminatorFilter<S>) INSTANCE;
    }

    /**
     * Uses the z-score method repeatedly to eliminate outliers.
     *
     * @see <a href='https://onlinecourses.science.psu.edu/stat200/node/135'>
     *  Identifying Outliers</a>
     * @param list
     * @return
     */
    public static List<Double> eliminateOutliers(List<Double> list) {
        return OutlierEliminatorFilter.<Double>instance()
                .filter(list, DoubleValueExtractor.INSTANCE);
    }

    public OutlierEliminatorFilter() {
        this(DEFAULT_STANDARD_FACTOR);
    }

    public OutlierEliminatorFilter(double stdevFactor) {
        this.stdevFactor = stdevFactor;
    }

    /**
     * Uses the z-score method repeatedly to eliminate outliers.
     *
     * @see <a href='https://onlinecourses.science.psu.edu/stat200/node/135'>
     *  Identifying Outliers</a>
     * @param list
     * @return
     */
    @Override
    public List<T> filter(List<T> list,
            ValueExtractor<T,Double> valueExtractor) {
        int size;
        List<T> result = list;
        do {
            size = result.size();
            result = createNewFilteredList(result, valueExtractor);
        } while(result.size() < size);
        return result;
    }

    private <T> List<T> createNewFilteredList(List<T> list,
            ValueExtractor<T,Double> valueExtractor) {
        OnlineMeasure measure = new OnlineMeasure();
        for (T t: list) {
            measure.add(valueExtractor.getValue(t));
        }
        double stdev = measure.getUnbiasedStandardDeviation();
        if (stdev == 0) {
            return list;
        }
        double mean = measure.getMean();
        List<T> cleanedList = new ArrayList<>(list.size());
        for (T t : list) {
            double x = valueExtractor.getValue(t);
            double z = (x - mean) / stdev;
            if (z >= -stdevFactor && z <= stdevFactor) {
                cleanedList.add(t);
            }
        }
        return cleanedList;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{stdevFactor=" + stdevFactor + '}';
    }
}
