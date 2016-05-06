package com.fillumina.performance.util.filter;

import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SampleFilterChain implements SampleFilter {
    private final SampleFilter[] filters;

    public SampleFilterChain(SampleFilter... filters) {
        this.filters = filters;
    }

    @Override
    public <T> List<T> filter(List<T> list,
            ValueExtractor<T, Double> extractor) {
        List<T> result = list;
        for (SampleFilter filter : filters) {
            result = filter.filter(result, extractor);
        }
        return result;
    }

}
