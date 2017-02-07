package com.fillumina.performance.speed.stats.progression;

import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.strgen.WrapperSpeedStatsTableStringGenerator;
import com.fillumina.performance.suite.strgen.ParameterizedSequenceStringGenerator;
import com.fillumina.performance.suite.strgen.ParameterizedStringGenerator;
import com.fillumina.performance.util.ComposedName;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SpeedProgressionStringGenerator {

    private static final ParameterizedStringGenerator<SpeedStats> PARAMETRIZED =
            new ParameterizedStringGenerator<>(WrapperSpeedStatsTableStringGenerator.INSTANCE);

    private static final PerformanceViewer<Map<ComposedName, SpeedStats>>
            PARAMETRIZED_VIEWER = new PerformanceViewer<>(PARAMETRIZED);

    public static StringGenerator<Map<ComposedName, SpeedStats>>
            parameterized() {
        return PARAMETRIZED;
    }

    public static PerformanceViewer<Map<ComposedName, SpeedStats>>
            parameterized(Appendable appendable) {
        return new PerformanceViewer<>(PARAMETRIZED, appendable);
    }

    public static PerformanceViewer<Map<ComposedName, SpeedStats>>
            parameterizedViewer() {
        return PARAMETRIZED_VIEWER;
    }

    private static final ParameterizedSequenceStringGenerator<SpeedStats>
            PARAMETRIZED_SEQUENCE =
                new ParameterizedSequenceStringGenerator<>(PARAMETRIZED);

    private static final PerformanceViewer<Map<ComposedName, Map<ComposedName, SpeedStats>>>
            PARAMETRIZED_SEQUENCE_VIEWER = new PerformanceViewer<>(PARAMETRIZED_SEQUENCE);

    public static ParameterizedSequenceStringGenerator<SpeedStats>
            parameterizedSequence() {
        return PARAMETRIZED_SEQUENCE;
    }

    public static PerformanceViewer<Map<ComposedName, Map<ComposedName, SpeedStats>>>
            parameterizedSequence(Appendable appendable) {
        return new PerformanceViewer<>(PARAMETRIZED_SEQUENCE, appendable);
    }

    public static PerformanceViewer<Map<ComposedName, Map<ComposedName, SpeedStats>>>
            parameterizedSequenceViewer() {
        return PARAMETRIZED_SEQUENCE_VIEWER;
    }
}
