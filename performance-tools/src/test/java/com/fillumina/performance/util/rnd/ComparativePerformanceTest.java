package com.fillumina.performance.util.rnd;

import static com.fillumina.performance.infrastructure.Sink.drain;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.template.ProgressionAssertion;
import com.fillumina.performance.template.Configuration;

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
            public void config(Configuration config) {
                config.speedTestOnly()
                        .setSamples(33);
            }

            @Override
            public void addTests(TestContainer<Runnable> tests) {
                tests.addTest("lfsr", new Runnable() {
                    private Lfsr lfsr = new Lfsr();
                    @Override
                    public void run() {
                        drain(lfsr.next());
                    }
                });
                tests.addTest("xorshift", new Runnable() {
                    private XorShiftPlusRandom rnd = new XorShiftPlusRandom();
                    @Override
                    public void run() {
                        drain(rnd.nextInt());
                    }
                });
                tests.addTest("high quality", new Runnable() {
                    private HighQualityRandom rnd = new HighQualityRandom();
                    @Override
                    public void run() {
                        drain(rnd.nextInt());
                    }
                });
            }
        }.executeWithFullOutput();
    }
}
