package com.fillumina.performance.executor.stats;

import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.tname.TNamed;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import java.io.Serializable;

/**
 * Contains the statistics relative to a specific test.
 *
 * @author Francesco Illuminati
 */
public class SingleStats implements TNamed, Serializable {
    private static final long serialVersionUID = 1L;

    private final TName name;
    private final DimensionalMeasure measure;

    private final long totalIterations;
    private final long originalSamples;
    private final long totalTime;

//    public SingleStats(TName name, DimensionalMeasure measure) {
//        this.name = name;
//        this.measure = measure;
//    }

    public SingleStats(TName name,
            DimensionalMeasure measure,
            long totalIterations,
            long originalSamples,
            long totalTime) {
        this.name = name;
        this.measure = measure;
        this.totalIterations = totalIterations;
        this.originalSamples = originalSamples;
        this.totalTime = totalTime;
    }

    @Override
    public TName getName() {
        return name;
    }

    public DimensionalMeasure getMeasure() {
        return measure;
    }

    /** Total number of iIterations performed. */
    public long getOriginalSamples() {
        return originalSamples;
    }

    /** Total time used to perform the test. */
    public long getTotalTime() {
        return totalTime;
    }

    /**
     * Total number of iIterations used to create the statistics
     * (after outliers elimination).
     */
    public long getTotalIterations() {
        return totalIterations;
    }

    public long getIterationsPerSample() {
        return totalIterations / getSamples();
    }

    public long getSamples() {
        return getMeasure().getCount();
    }

    @Override
    public String toString() {
        return getName() + ":\t" + getMeasure().toString() +
                "\t (" + totalIterations + ")";
    }
}
