package com.fillumina.performance.mem.strgen;

import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.suite.strgen.ParametrizedSequenceStringGenerator;
import com.fillumina.performance.suite.strgen.ParametrizedStringGenerator;
import com.fillumina.performance.util.ComposedName;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class UsedMemStatsStringGenerator {

    private static final ParametrizedStringGenerator<MemStats> PARAMETRIZED =
            new ParametrizedStringGenerator<>(
                    MemStatsTableStringGenerator.USED_INSTANCE);

    private static final PerformanceViewer<Map<ComposedName, MemStats>>
            PARAMETRIZED_VIEWER = new PerformanceViewer<>(PARAMETRIZED);

    public static StringGenerator<Map<ComposedName, MemStats>>
            parametrized() {
        return PARAMETRIZED;
    }

    public static PerformanceViewer<Map<ComposedName, MemStats>>
            parametrizedViewer() {
        return PARAMETRIZED_VIEWER;
    }

    private static final ParametrizedSequenceStringGenerator<MemStats>
            PARAMETRIZED_SEQUENCE =
                new ParametrizedSequenceStringGenerator<>(PARAMETRIZED);

    private static final PerformanceViewer<Map<ComposedName, Map<ComposedName, MemStats>>>
            PARAMETRIZED_SEQUENCE_VIEWER =
                new PerformanceViewer<>(PARAMETRIZED_SEQUENCE);

    public static ParametrizedSequenceStringGenerator<MemStats>
            parametrizedSequence() {
        return PARAMETRIZED_SEQUENCE;
    }

    public static PerformanceViewer<Map<ComposedName, Map<ComposedName, MemStats>>>
            parametrizedSequenceViewer() {
        return PARAMETRIZED_SEQUENCE_VIEWER;
    }

}
