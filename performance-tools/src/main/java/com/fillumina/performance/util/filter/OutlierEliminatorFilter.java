package com.fillumina.performance.util.filter;

import com.fillumina.performance.util.stats.OnlineMeasure;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class OutlierEliminatorFilter implements SampleFilter {

    public static final OutlierEliminatorFilter INSTANCE =
            new OutlierEliminatorFilter();

    /**
     * Uses the z-score method repeatedly to eliminate outliers.
     *
     * @see <a href='https://onlinecourses.science.psu.edu/stat200/node/135'>
     *  Identifying Outliers</a>
     * @param list
     * @return
     */
    public static List<Double> eliminateOutliers(List<Double> list) {
        int size;
        List<Double> result = list;
        do {
            size = result.size();
            result = eliminate(result);
        } while(result.size() < size);
        return result;
    }

    private static List<Double> eliminate(List<Double> list) {
        OnlineMeasure measure = new OnlineMeasure(list);
        double stdev = measure.getUnbiasedStandardDeviation();
        if (stdev == 0) {
            return list;
        }
        double mean = measure.getMean();
        List<Double> cleanedList = new ArrayList<>(list.size());
        for (double x : list) {
            double z = (x - mean) / stdev;
            if (z >= -3 && z <= 3) {
                cleanedList.add(x);
            }
        }
        return cleanedList;
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
    public <T> List<T> filter(List<T> list, ValueExtractor<T,Double> v) {
        int size;
        List<T> result = list;
        do {
            size = result.size();
            result = eliminate(result, v);
        } while(result.size() < size);
        return result;
    }

    private static <T> List<T> eliminate(List<T> list,
            ValueExtractor<T,Double> v) {
        OnlineMeasure measure = new OnlineMeasure();
        for (T t: list) {
            measure.add(v.getValue(t));
        }
        double stdev = measure.getUnbiasedStandardDeviation();
        if (stdev == 0) {
            return list;
        }
        double mean = measure.getMean();
        List<T> cleanedList = new ArrayList<>(list.size());
        for (T t : list) {
            double x = v.getValue(t);
            double z = (x - mean) / stdev;
            if (z >= -3 && z <= 3) {
                cleanedList.add(t);
            }
        }
        return cleanedList;
    }

}
