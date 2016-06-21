package com.fillumina.performance.speed.stats;

import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.speed.stats.strgen.SpeedTableStringGenerator;
import com.fillumina.performance.suite.strgen.ParametrizedSequenceStringGenerator;
import com.fillumina.performance.suite.strgen.ParametrizedStringGenerator;
import com.fillumina.performance.util.ComposedName;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SpeedStringGenerator {

    private static final ParametrizedStringGenerator<SpeedStats> PARAMETRIZED =
            new ParametrizedStringGenerator<>(SpeedTableStringGenerator.INSTANCE);

    private static final PerformanceViewer<Map<ComposedName, SpeedStats>>
            PARAMETRIZED_VIEWER = new PerformanceViewer<>(PARAMETRIZED);

    public static StringGenerator<Map<ComposedName, SpeedStats>>
            parametrized() {
        return PARAMETRIZED;
    }

    public static PerformanceViewer<Map<ComposedName, SpeedStats>>
            parametrizedViewer() {
        return PARAMETRIZED_VIEWER;
    }

    private static final ParametrizedSequenceStringGenerator<SpeedStats>
            PARAMETRIZED_SEQUENCE =
                new ParametrizedSequenceStringGenerator<>(PARAMETRIZED);

    private static final PerformanceViewer<Map<ComposedName, Map<ComposedName, SpeedStats>>>
            PARAMETRIZED_SEQUENCE_VIEWER = new PerformanceViewer<>(PARAMETRIZED_SEQUENCE);

    public static ParametrizedSequenceStringGenerator<SpeedStats>
            parametrizedSequence() {
        return PARAMETRIZED_SEQUENCE;
    }

    public static PerformanceViewer<Map<ComposedName, Map<ComposedName, SpeedStats>>>
            parametrizedSequenceViewer() {
        return PARAMETRIZED_SEQUENCE_VIEWER;
    }

}
