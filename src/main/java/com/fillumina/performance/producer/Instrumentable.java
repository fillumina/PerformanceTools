package com.fillumina.performance.producer;

/**
 *
 * @author fra
 */
public interface Instrumentable {

    <T extends PerformanceExecutorInstrumenter> T
            instrumentedBy(final T instrumenter);
}
