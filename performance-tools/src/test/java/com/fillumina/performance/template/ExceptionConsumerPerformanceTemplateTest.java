package com.fillumina.performance.template;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.LfsrRunnable;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.Sink;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.util.rnd.Lfsr;
import com.fillumina.performance.util.stats.Ratio;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ExceptionConsumerPerformanceTemplateTest
        extends PerformanceTemplate {

    private boolean assertionErrorConsumerCaptured;

    public static void main(final String[] args) {
        new ExceptionConsumerPerformanceTemplateTest()
                .executeWithFullOutput();
    }

    @Test
    public void shouldThrowException() {
        executeWithoutOutput();
        assertTrue(assertionErrorConsumerCaptured);
    }

    @Override
    public void addAssertions(ProgressionAssertion assertions) {
        assertions.speedWithTolerance(Ratio.percentage(5))
                .assertOrder("fast").lessThan("slow");
    }

    @Override
    public void config(Configuration<PerformanceTemplate> config) {
        config.setTestListener(new TestListener() {
            @Override
            public <S extends Assertable, M extends Assertable> boolean notify(
                        Configuration config,
                        MixedAssertion<?, ?> assertion,
                        PHolder<S> speedStats,
                        PHolder<M> usedMemStats,
                        PHolder<M> allocatedMemStats,
                        Throwable exception) {
                    assertionErrorConsumerCaptured = true;
                    return false;
                }
            }).speedTestOnly();
    }

    @Override
    public void addTests(TestContainer<Runnable> tests) {
        tests.addTest("slow", new LfsrRunnable());
        tests.addTest("fast", new Runnable() {
            private Lfsr lfsr = new Lfsr();

            @Override
            public void run() {
                for (int i=0; i<1_000; i++) {
                    lfsr.next();
                }
                Sink.drain(lfsr.next());
            }
        });
    }

}
