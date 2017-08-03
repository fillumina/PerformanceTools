package com.fillumina.performance.util.stats;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * @see https://en.wikipedia.org/wiki/Simple_linear_regression
 * @see http://www.statisticshowto.com/how-to-find-a-linear-regression-equation/
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SimpleLinearRegressionTest {

    @Test
    public void shouldCalculateAandB() {
        SimpleLinearRegression slr = SimpleLinearRegression.builder()
                .add(43, 99)
                .add(21, 65)
                .add(25, 79)
                .add(42, 75)
                .add(57, 87)
                .add(59, 81)
                .build();

        //System.out.println(slr);

        assertEquals(65.1416, slr.getA(), 0.001);
        assertEquals(.385225, slr.getB(), 0.001);
    }

    /**
     * @see https://en.wikipedia.org/wiki/Coefficient_of_determination
     */
    @Test
    public void shouldRSquareFit() {
        SimpleLinearRegression slr = SimpleLinearRegression.builder()
                .add(1, 1.9)
                .add(2, 3.7)
                .add(3, 5.8)
                .add(4, 8.0)
                .add(5, 9.6)
                .build();

        assertEquals(slr.toString(), 0.998, slr.getRSquared(), 0.001);
    }

    /**
     * @see https://en.wikipedia.org/wiki/Coefficient_of_determination
     */
    @Test
    public void shouldRSquareNotFit() {
        SimpleLinearRegression slr = SimpleLinearRegression.builder()
                .add(1, 1.9)
                .add(2, 7.3)
                .add(3, 0.8)
                .add(4, 5.0)
                .add(5, 2.6)
                .build();

        assertEquals(slr.toString(), 0.002, slr.getRSquared(), 0.001);
    }

}
