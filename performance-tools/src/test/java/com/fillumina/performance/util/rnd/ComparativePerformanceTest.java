package com.fillumina.performance.util.rnd;

import static com.fillumina.performance.infrastructure.Sink.drain;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.template.ProgressionAssertion;
import com.fillumina.performance.template.TestConfiguration;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ComparativePerformanceTest {

    public static void main(final String[] args) {
        new PerformanceTemplate() {
            @Override
            public void addAssertions(ProgressionAssertion assertions) {
            }

            @Override
            public void config(TestConfiguration config) {
                config.speedTestOnly()
                        .setSamples(33);
            }

            @Override
            public void addTests(TestContainer<Testable> tests) {
                tests.addTest("lfsr", new Testable() {
                    private Lfsr lfsr = new Lfsr();
                    @Override
                    public void test() {
                        drain(lfsr.next());
                    }
                });
                tests.addTest("xorshift", new Testable() {
                    private XorShiftPlusRandom rnd = new XorShiftPlusRandom();
                    @Override
                    public void test() {
                        drain(rnd.nextInt());
                    }
                });
                tests.addTest("high quality", new Testable() {
                    private HighQualityRandom rnd = new HighQualityRandom();
                    @Override
                    public void test() {
                        drain(rnd.nextInt());
                    }
                });
            }
        }.executeWithFullOutput();
    }
}
