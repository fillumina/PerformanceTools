package com.fillumina.performance.mem.strgen;

import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.suite.strgen.ParameterizedSequenceStringGenerator;
import com.fillumina.performance.suite.strgen.ParameterizedStringGenerator;
import com.fillumina.performance.util.ComposedName;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AllocatedMemStatsStringGenerator {

    private static final ParameterizedStringGenerator<MemStats> PARAMETRIZED =
            new ParameterizedStringGenerator<>(MemStatsTableStringGenerator
                    .ALLOCATED_INSTANCE);

    private static final PerformanceViewer<Map<ComposedName, MemStats>>
            PARAMETRIZED_VIEWER = new PerformanceViewer<>(PARAMETRIZED);

    public static StringGenerator<Map<ComposedName, MemStats>>
            parameterized() {
        return PARAMETRIZED;
    }

    public static PerformanceViewer<Map<ComposedName, MemStats>>
            parameterizedViewer() {
        return PARAMETRIZED_VIEWER;
    }

    private static final ParameterizedSequenceStringGenerator<MemStats>
            PARAMETRIZED_SEQUENCE =
                new ParameterizedSequenceStringGenerator<>(PARAMETRIZED);

    private static final PerformanceViewer<Map<ComposedName, Map<ComposedName, MemStats>>>
            PARAMETRIZED_SEQUENCE_VIEWER = new PerformanceViewer<>(PARAMETRIZED_SEQUENCE);

    public static ParameterizedSequenceStringGenerator<MemStats>
            parameterizedSequence() {
        return PARAMETRIZED_SEQUENCE;
    }

    public static PerformanceViewer<Map<ComposedName, Map<ComposedName, MemStats>>>
            parameterizedSequenceViewer() {
        return PARAMETRIZED_SEQUENCE_VIEWER;
    }

}
