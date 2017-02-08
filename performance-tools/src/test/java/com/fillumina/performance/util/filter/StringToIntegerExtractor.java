package com.fillumina.performance.util.filter;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class StringToIntegerExtractor implements ValueExtractor<String, Integer> {
    public static final StringToIntegerExtractor INSTANCE =
            new StringToIntegerExtractor();

    private StringToIntegerExtractor() {}

    @Override
    public Integer getValue(String t) {
        return Integer.valueOf(t);
    }

}
