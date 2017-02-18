package com.fillumina.performance.mem.strgen;

import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.mem.sample.MemSample;
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

    public static final PerformanceConsumer<MemSample> appendTo(
            Appendable appendable) {
        return new PerformanceViewer<>(INSTANCE, appendable);
    }

    protected MemSampleLineStringGenerator() {}

    @Override
    public String toString(PHolder<MemSample> holder) {
        MemSample memSample = holder.getStats();
        return memSample.getTestName() + ": " + memSample.getBytes() + " bytes";
    }
}
