package com.fillumina.performance.util.filter;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class DoubleValueExtractor implements ValueExtractor<Double,Double> {
    public static final DoubleValueExtractor INSTANCE =
            new DoubleValueExtractor();

    private DoubleValueExtractor() {}

    @Override
    public Double getValue(Double t) {
        return t;
    }

}
