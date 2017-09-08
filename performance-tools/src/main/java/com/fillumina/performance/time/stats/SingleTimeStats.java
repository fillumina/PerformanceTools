package com.fillumina.performance.time.stats;

import com.fillumina.performance.infrastructure.stats.SingleStats;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import java.io.Serializable;

/**
 * Contains the statistics relative to a specific test.
 *
 * @author Francesco Illuminati
 */
public class SingleTimeStats extends SingleStats implements Serializable {
    private static final long serialVersionUID = 1L;

    private final long totalIterations;
    private final long originalSamples;
    private final long totalTime;

    public SingleTimeStats(TName name,
            DimensionalMeasure measure,
            long totalIterations,
            long originalSamples,
            long totalTime) {
        super(name, measure);
        this.totalIterations = totalIterations;
        this.originalSamples = originalSamples;
        this.totalTime = totalTime;
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
        return getTestName() + ":\t" + getMeasure().toString() +
                "\t (" + totalIterations + ")";
    }
}
