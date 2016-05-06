package com.fillumina.performance.util.filter;

import com.fillumina.performance.util.stats.RunningOnlineMeasure;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class JavaOptimizerFilter implements SampleFilter {

    public static final JavaOptimizerFilter INSTANCE =
            new JavaOptimizerFilter();

    private final int minStableSequenceLength;
    private final int minUnoptimizedSequnenceLength;

    public JavaOptimizerFilter() {
        this(10, 5);
    }

    public JavaOptimizerFilter(int minStableSequenceLength,
            int minUnoptimizedSequnenceLength) {
        this.minUnoptimizedSequnenceLength = minUnoptimizedSequnenceLength;
        this.minStableSequenceLength = minStableSequenceLength;
    }

    public List<Double> filter(List<Double> coll) {
        return filter(coll, DoubleValueExtractor.INSTANCE);
    }

    @Override
    public <T> List<T> filter(List<T> coll,
            ValueExtractor<T,Double> extractor) {
        List<T> list = new ArrayList<>(coll);
        Collections.reverse(list);
        RunningOnlineMeasure minMeasure = new RunningOnlineMeasure();
        int firstIndex = -1;
        int deoptimizedSeq = 0;
        int index = 0;
        for (T t : list) {
            double value = extractor.getValue(t);
            if (index > minStableSequenceLength &&
                    Math.abs(value - minMeasure.mean()) >
                    minMeasure.unbiasedStandardDeviation() * 3) {
                if (firstIndex == -1) {
                    firstIndex = index;
                }
                if (deoptimizedSeq > minUnoptimizedSequnenceLength) {
                    List<T> result = new ArrayList<>(index);
                    for (T tt: list) {
                        result.add(tt);
                        firstIndex--;
                        if (firstIndex == 0) {
                            Collections.reverse(result);
                            return result;
                        }
                    }
                }
                deoptimizedSeq++;
            } else {
                firstIndex = -1;
                minMeasure.add(value);
            }
            index++;
        }
        Collections.reverse(list);
        return list;
    }
}
