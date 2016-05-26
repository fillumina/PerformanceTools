package com.fillumina.performance.util.filter;

import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SampleFilterChain implements SampleFilter {
    private final int minSize;
    private final SampleFilter[] filters;

    public SampleFilterChain(int minSize, SampleFilter... filters) {
        this.minSize = minSize;
        this.filters = filters;
    }

    @Override
    public <T> List<T> filter(List<T> list,
            ValueExtractor<T, Double> extractor) {
        List<T> result = list;
        for (SampleFilter filter : filters) {
            if (result.size() > minSize) {
                result = filter.filter(result, extractor);
            }
        }
        return result;
    }

}
