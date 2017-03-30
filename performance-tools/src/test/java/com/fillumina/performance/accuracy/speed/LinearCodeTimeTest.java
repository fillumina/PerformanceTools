package com.fillumina.performance.accuracy.speed;

import static com.fillumina.performance.infrastructure.Sink.drain;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.template.ProgressionAssertion;
import com.fillumina.performance.template.TestConfiguration;
import com.fillumina.performance.util.rnd.Lfsr;
import com.fillumina.performance.util.stats.Ratio;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LinearCodeTimeTest extends PerformanceTemplate {

    public static void main(final String[] args) {
        new LinearCodeTimeTest().executeWithFullOutput();
    }

    @Test
    public void shouldACodeExecutedTwiceTakeDoubleTheTime() {
        new LinearCodeTimeTest().executeWithFullOutput();
//        new LinearCodeTimeTest().executeWithoutOutput();
    }

    @Override
    public void addAssertions(ProgressionAssertion assertions) {
        assertions.speedWithTolerance(Ratio.percentage(5))
                .assertPercentage("single").sameAs(50);
    }

    @Override
    public void config(TestConfiguration config) {
        config.speedTestOnly()
                .setSampleTimeMillis(500);
    }

    @Override
    public void addTests(TestContainer<Testable> tests) {
        tests.addTest("single", new Testable() {
            private final Lfsr lfsr = new Lfsr();

            @Override
            public void test() {
                drain(lfsr.next());
            }
        });
        tests.addTest("double", new Testable() {
            private final Lfsr lfsr = new Lfsr();

            @Override
            public void test() {
                drain(lfsr.next());
                drain(lfsr.next());
            }
        });
    }

}
