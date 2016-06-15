package com.fillumina.performance.util.filter;

import com.fillumina.performance.util.stats.OnlineMeasure;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * JVM continously optimizes the executing code improving its performances so,
 * if the iterations for each sample are enough, it might be that the last
 * samples refer to a code which is very different from the first ones.
 * This filter starts from the last samples and go back until it finds a
 * statistically relevant discontinuity in the performances and takes only
 * those last statistics which are optimized and stable.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class JavaOptimizerFilter<T> implements ListFilter<T, Double> {
    public static final JavaOptimizerFilter<?> INSTANCE =
            new JavaOptimizerFilter<>();

    private final int minStableSequenceLength;
    private final int minUnoptimizedSequnenceLength;
    private final double stdevFactor;

    public JavaOptimizerFilter() {
        this(33, 10);
    }

    public JavaOptimizerFilter(int minStableSequenceLength,
            int minUnoptimizedSequnenceLength) {
        this(minStableSequenceLength, minUnoptimizedSequnenceLength, 3.0);
    }

    public JavaOptimizerFilter(int minStableSequenceLength,
            int minUnoptimizedSequnenceLength,
            double stdevFactor) {
        this.minStableSequenceLength = minStableSequenceLength;
        this.minUnoptimizedSequnenceLength = minUnoptimizedSequnenceLength;
        this.stdevFactor = stdevFactor;
    }

    @SuppressWarnings("unchecked")
    public static <S> JavaOptimizerFilter<S> instance() {
        return (JavaOptimizerFilter<S>) INSTANCE;
    }

    public static List<Double> filter(List<Double> coll) {
        return JavaOptimizerFilter.<Double>instance()
                .filter(coll, DoubleValueExtractor.INSTANCE);
    }

    @Override
    public List<T> filter(List<T> coll,
            ValueExtractor<T,Double> extractor) {
        OnlineMeasure stats = new OnlineMeasure();
        int deoptimizedSeq = 0;
        int size = coll.size();
        List<T> result = new ArrayList<>(size);
        int index = 1;
        for (int i=size-1; i>=0; i--) {
            T t = coll.get(i);
            double value = extractor.getValue(t);

            if (index > minStableSequenceLength &&
                    stats.isOutlier(value, stdevFactor)) {
                if (deoptimizedSeq > minUnoptimizedSequnenceLength) {
                    break;
                }
                deoptimizedSeq++;
            } else {
                result.add(t);
                stats.add(value);
            }

            index++;
        }
        if (result.size() > minStableSequenceLength) {
            Collections.reverse(result);
            return result;
        }
        return coll;
    }
}
