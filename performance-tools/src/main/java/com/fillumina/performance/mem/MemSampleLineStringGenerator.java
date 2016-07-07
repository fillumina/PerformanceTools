package com.fillumina.performance.mem;

import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.util.ComposedName;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemSampleLineStringGenerator
        implements StringGenerator<MemSample>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final MemSampleLineStringGenerator INSTANCE =
            new MemSampleLineStringGenerator();

    public static final PerformanceViewer<MemSample> VIEWER =
            new PerformanceViewer<>(INSTANCE);

    protected MemSampleLineStringGenerator() {}

    @Override
    public String toString(MemSample memSample) {
        return memSample.getTestName() + ": " + memSample.getBytes() + " bytes";
    }

    @Override
    public String toString(ComposedName name, MemSample memSample) {
        return toString(memSample);
    }
}
