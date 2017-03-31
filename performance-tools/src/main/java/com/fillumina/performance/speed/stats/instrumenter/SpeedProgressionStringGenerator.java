package com.fillumina.performance.speed.stats.instrumenter;

import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.strgen.WrapperSpeedStatsTableStringGenerator;
import com.fillumina.performance.suite.strgen.ParameterizedSequenceStringGenerator;
import com.fillumina.performance.suite.strgen.ParameterizedStringGenerator;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SpeedProgressionStringGenerator {

    private static final ParameterizedStringGenerator<SpeedStats> PARAMETRIZED =
            new ParameterizedStringGenerator<>(
                    WrapperSpeedStatsTableStringGenerator.INSTANCE);

    private static final PerformanceViewer<PHolder<SpeedStats>>
            PARAMETRIZED_VIEWER = new PerformanceViewer<>(PARAMETRIZED);

    public static StringGenerator<PHolder<SpeedStats>> parameterized() {
        return PARAMETRIZED;
    }

    public static PerformanceViewer<PHolder<SpeedStats>> parameterized(
            Appendable appendable) {
        return new PerformanceViewer<>(PARAMETRIZED, appendable);
    }

    public static PerformanceViewer<PHolder<SpeedStats>> parameterizedViewer() {
        return PARAMETRIZED_VIEWER;
    }

    private static final ParameterizedSequenceStringGenerator<SpeedStats>
            PARAMETRIZED_SEQUENCE =
                new ParameterizedSequenceStringGenerator<>(PARAMETRIZED);

    private static final PerformanceViewer<PHolder<PHolder<SpeedStats>>>
            PARAMETRIZED_SEQUENCE_VIEWER = new PerformanceViewer<>(PARAMETRIZED_SEQUENCE);

    public static ParameterizedSequenceStringGenerator<SpeedStats>
            parameterizedSequence() {
        return PARAMETRIZED_SEQUENCE;
    }

    public static PerformanceViewer<PHolder<PHolder<SpeedStats>>>
            parameterizedSequence(Appendable appendable) {
        return new PerformanceViewer<>(PARAMETRIZED_SEQUENCE, appendable);
    }

    public static PerformanceViewer<PHolder<PHolder<SpeedStats>>>
            parameterizedSequenceViewer() {
        return PARAMETRIZED_SEQUENCE_VIEWER;
    }
}
