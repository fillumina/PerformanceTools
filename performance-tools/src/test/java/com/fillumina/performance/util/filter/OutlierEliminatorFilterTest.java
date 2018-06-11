package com.fillumina.performance.util.filter;

import com.fillumina.performance.util.stats.OnlineMeasure;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * @see <a href='https://onlinecourses.science.psu.edu/stat200/node/135'>
 *  Identifying Outliers</a>
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class OutlierEliminatorFilterTest {

    @Test
    public void shouldNotEliminateAnySample() {
        List<Double> list = Arrays.asList(
                74.0, 88.0, 78.0, 90.0, 94.0, 90.0, 84.0, 90.0, 98.0, 80.0);
        List<Double> cleaned = OutlierEliminatorFilter.eliminateOutliers(list);
        assertEquals(list.size(), cleaned.size());
    }

    @Test
    public void shouldNotEliminateOutliers() {
        List<Double> list = Arrays.asList(
                0.0, 0.0, 2.0, 5.0, 8.0, 8.0, 8.0, 9.0, 9.0,
                10.0, 10.0, 10.0, 11.0, 12.0, 12.0, 12.0, 14.0,
                15.0, 20.0, 25.0);
        List<Double> cleaned = OutlierEliminatorFilter.eliminateOutliers(list);
        assertEquals( 5.991, new OnlineMeasure(list).getUnbiasedStandardDeviation(), 10E-3);
        assertEquals(list.size(), cleaned.size());
    }

    @Test
    public void shouldEliminateOneOutlier() {
        List<Double> list = Arrays.asList(
                0.0, 0.0, 2.0, 5.0, 8.0, 8.0, 8.0, 9.0, 9.0,
                10.0, 10.0, 10.0, 11.0, 12.0, 12.0, 12.0, 14.0,
                15.0, 20.0, 25.0, 80.0, 81.0);
        List<Double> cleaned = OutlierEliminatorFilter.eliminateOutliers(list);
        assertEquals(list.size(), cleaned.size() + 2);
    }

    @Test
    public void shouldNotEliminateIfStdDevIsZero() {
        List<Double> list = Arrays.asList(
            100.0, 100.0, 100.0, 100.0, 100.0, 100.0, 100.0, 100.0, 100.0, 100.0);
        List<Double> cleaned = OutlierEliminatorFilter.eliminateOutliers(list);
        assertEquals(list.size(), cleaned.size());
    }

}
