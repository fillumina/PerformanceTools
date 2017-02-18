package com.fillumina.performance.mem.strgen;

import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.suite.strgen.ParameterizedSequenceStringGenerator;
import com.fillumina.performance.suite.strgen.ParameterizedStringGenerator;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class UsedMemStatsStringGenerator {

    private static final ParameterizedStringGenerator<MemStats>
            PARAMETRIZED = new ParameterizedStringGenerator<>(
                    MemStatsTableStringGenerator.USED_INSTANCE);

    private static final PerformanceViewer<PHolder<MemStats>>
            PARAMETRIZED_VIEWER = new PerformanceViewer<>(PARAMETRIZED);

    public static StringGenerator<PHolder<MemStats>> parameterized() {
        return PARAMETRIZED;
    }

    public static PerformanceViewer<PHolder<MemStats>> parameterizedViewer() {
        return PARAMETRIZED_VIEWER;
    }

    private static final ParameterizedSequenceStringGenerator<MemStats>
            PARAMETRIZED_SEQUENCE =
                new ParameterizedSequenceStringGenerator<>(PARAMETRIZED);

    private static final PerformanceViewer<PHolder<PHolder<MemStats>>>
            PARAMETRIZED_SEQUENCE_VIEWER =
                new PerformanceViewer<>(PARAMETRIZED_SEQUENCE);

    public static ParameterizedSequenceStringGenerator<MemStats>
            parameterizedSequence() {
        return PARAMETRIZED_SEQUENCE;
    }

    public static PerformanceViewer<PHolder<PHolder<MemStats>>>
            parameterizedSequenceViewer() {
        return PARAMETRIZED_SEQUENCE_VIEWER;
    }

}
