package com.fillumina.performance.util.filter;

import com.fillumina.performance.util.stats.OnlineMeasure;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * JVM continously optimizes the executing code improving its performances so,
 * if the iterations for each sample are enough, it might be that the last
 * samples iterates over a code which is very different from the one used in the
 * first samples.
 * <p>
 * This filter starts from the last samples and go back until it finds a
 * statistically relevant discontinuity in the performances and takes only
 * those last statistics which are optimized and stable. Using this filter
 * accounts automatically for warm-up cycles.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ConvergenceFilter implements ListFilter<Double> {
    public static final ConvergenceFilter INSTANCE = new ConvergenceFilter();

    private final int minStableSequenceLength;
    private final int minUnoptimizedSequnenceLength;
    private final double stdevFactor;

    public ConvergenceFilter() {
        this(33, 10);
    }

    public ConvergenceFilter(int minStableSequenceLength,
            int minUnoptimizedSequnenceLength) {
        this(minStableSequenceLength, minUnoptimizedSequnenceLength, 3.0);
    }

    public ConvergenceFilter(int minStableSequenceLength,
            int minUnoptimizedSequnenceLength,
            double stdevFactor) {
        this.minStableSequenceLength = minStableSequenceLength;
        this.minUnoptimizedSequnenceLength = minUnoptimizedSequnenceLength;
        this.stdevFactor = stdevFactor;
    }

    public static ConvergenceFilter instance() {
        return INSTANCE;
    }

    @Override
    public <T> List<T> filter(List<T> coll,
            Function<T,Double> extractor) {
        OnlineMeasure stats = new OnlineMeasure();
        int deoptimizedSeq = 0;
        int size = coll.size();
        List<T> result = new ArrayList<>(size);
        int index = 1;
        for (int i=size-1; i>=0; i--) {
            T t = coll.get(i);
            double value = extractor.apply(t);

            if (index > minStableSequenceLength &&
                    stats.isOutlier(value, stdevFactor)) {
                if (deoptimizedSeq > minUnoptimizedSequnenceLength) {
                    break;
                }
                deoptimizedSeq++;
            } else {
                result.add(t);
                stats.addSample(value);
            }

            index++;
        }
        if (result.size() > minStableSequenceLength) {
            Collections.reverse(result);
            return result;
        }
        return coll;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                "{minStableSequenceLength=" + minStableSequenceLength +
                ", minUnoptimizedSequnenceLength=" + minUnoptimizedSequnenceLength +
                ", stdevFactor=" + stdevFactor + '}';
    }
}
