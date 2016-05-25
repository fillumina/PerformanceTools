package com.fillumina.performance.util.filter;

import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class JavaOptimizerAndOutliersFilterTest {

    @Test
    public void shouldFilterOutFirstDeoptimizedSamples() {
        List<Double> list = Arrays.asList(
                // pre-optimization values
                10.0, 10.2, 9.89, 10.11, 9.08, 8.98, 8.88, 10.11,
                // optimized values
                4.0, 4.1, 3.8, 3.77, 4.2, 4.01, 4.1, 4.0, 4.0, 3.98, 3.88);
        List<Double> result = new JavaOptimizerFilter(10,5).filter(list);
//        System.out.println(result);
        assertEquals(11, result.size(), 0);
    }

    @Test
    public void shouldFilterOutLoneSpikedKeepingTheRest() {
        List<Double> list = Arrays.asList(
                4.2, 4.1, 3.99, 3.89, 4.0, 4.1,
                7.0, 7.5, 6.8, // this is a spike that should be ignored
                4.0, 4.1, 3.8, 3.77, 4.2, 4.01, 4.1, 4.0, 4.0, 3.98, 3.88);
        List<Double> result = JavaOptimizerFilter.INSTANCE.filter(list);
//        System.out.println(result);
        assertEquals(list.size(), result.size(), 0);
    }

}
