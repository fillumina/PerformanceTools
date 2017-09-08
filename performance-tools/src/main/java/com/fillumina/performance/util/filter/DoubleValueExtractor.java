package com.fillumina.performance.util.filter;

import java.util.function.Function;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class DoubleValueExtractor implements Function<Double,Double> {
    public static final DoubleValueExtractor INSTANCE =
            new DoubleValueExtractor();

    private DoubleValueExtractor() {}

    @Override
    public Double apply(Double t) {
        return t;
    }

}
