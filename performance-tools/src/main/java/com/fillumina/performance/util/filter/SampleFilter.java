package com.fillumina.performance.util.filter;

import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface SampleFilter {

    <T> List<T> filter(List<T> list,
            ValueExtractor<T, Double> extractor);

}
