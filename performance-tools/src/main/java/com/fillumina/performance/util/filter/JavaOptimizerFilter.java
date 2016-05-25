package com.fillumina.performance.util.filter;

import com.fillumina.performance.util.stats.OnlineMeasure;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class JavaOptimizerFilter implements SampleFilter {
    private final double STD_FACTOR = 3.0;

    public static final JavaOptimizerFilter INSTANCE =
            new JavaOptimizerFilter();

    private final int minStableSequenceLength;
    private final int minUnoptimizedSequnenceLength;

    public JavaOptimizerFilter() {
        this(33, 10);
    }

    public JavaOptimizerFilter(int minStableSequenceLength,
            int minUnoptimizedSequnenceLength) {
        this.minStableSequenceLength = minStableSequenceLength;
        this.minUnoptimizedSequnenceLength = minUnoptimizedSequnenceLength;
    }

    public List<Double> filter(List<Double> coll) {
        return filter(coll, DoubleValueExtractor.INSTANCE);
    }

    @Override
    public <T> List<T> filter(List<T> coll,
            ValueExtractor<T,Double> extractor) {
        List<T> list = new ArrayList<>(coll);
        Collections.reverse(list);
        OnlineMeasure minMeasure = new OnlineMeasure();
        int firstIndex = -1;
        int deoptimizedSeq = 0;
        int index = 0;
        for (T t : list) {
            double value = extractor.getValue(t);
            if (index > minStableSequenceLength &&
                    Math.abs(value - minMeasure.getMean()) >
                    minMeasure.getUnbiasedStandardDeviation() * STD_FACTOR) {
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
