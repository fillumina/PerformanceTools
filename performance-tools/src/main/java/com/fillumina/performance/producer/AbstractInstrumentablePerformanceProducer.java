package com.fillumina.performance.producer;

/**
 * Allows a {@link AbstractPerformanceProducer} to be instrumented.
 *
 * An instrumenter uses the given {@link PerformanceExecutorInstrumenter} to
 * perform its tests.
 * <p>
 * An instrumenter can be used in two equivalent ways: indirectly or
 * directly.
 * <ul>
 * <li>
 * In the <b>indirect</b> way the {@link PerformanceProducer} is
 * defined first and than the <b>{@code instumentedBy()}</b> method is used:
 * <pre>
    PerformanceTimerFactory
        .createSingleThreaded()

        .addTest("example", new Runnable() {
            public void run() {
                // do the test
            }
        })

        .<b>instrumentedBy</b>(AutoProgressionPerformanceInstrumenter.builder()
            .setMaxStandardDeviation(1)
            .build())

        .addPerformanceConsumer(consumers)
        .execute();
 * </pre></li>
 * <li>
 * The <b>direct</b> way is a little more convoluted and the
 * {@link PerformanceExecutorInstrumenter} is defined first and its
 * <b>{@code instrument()}</b> method is used to define to which
 * {@link PerformanceProducer} it is applied:
 * <pre>
    AutoProgressionPerformanceInstrumenter.builder()
        .setMaxStandardDeviation(1)
        .build()

        .<b>instrument</b>(PerformanceTimerFactory.createSingleThreaded()
            .addTest("example", new Runnable() {
                public void run() {
                    // do the test
                }
            }))

        .addPerformanceConsumer(consumers)
        .execute();
 * </pre>
 * </li>
 * </ul>
 *
 * @see PerformanceExecutorInstrumenter
 * @see InstrumentablePerformanceExecutor
 * @author Francesco Illuminati
 */
public abstract class AbstractInstrumentablePerformanceProducer
        <T extends AbstractInstrumentablePerformanceProducer<T>>
    extends AbstractPerformanceProducer<T>
    implements InstrumentablePerformanceExecutor<T> {
    private static final long serialVersionUID = 1L;

    /**
     * Make the present {@link PerformanceProducer} being instrumented by an
     * instrumenter.
     *
     * @see com.fillumina.performance.producer.progression.AutoProgressionPerformanceInstrumenter
     */
    @Override
    public <K extends PerformanceExecutorInstrumenter> K
            instrumentedBy(final K instrumenter) {
        instrumenter.instrument(this);
        return instrumenter;
    }
}
