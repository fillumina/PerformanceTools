package com.fillumina.performance.mem;

import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SingleMemStats implements Serializable {
    private static final long serialVersionUID = 1L;

    private final TName testName;
    private final Measure measure;
    private MeasureRatio ratio;

    public SingleMemStats(TName testName, Measure measure) {
        this.testName = testName;
        this.measure = measure;
    }

    public void setRatio(MeasureRatio ratio) {
        this.ratio = ratio;
    }

    public TName getTestName() {
        return testName;
    }

    public Measure getUsedMemory() {
        return measure;
    }

    public MeasureRatio getRatio() {
        return ratio;
    }
}
