package com.fillumina.performance.util.rnd;

import com.fillumina.performance.executor.generator.TestConfiguration;
import static com.fillumina.performance.executor.test.Sink.drain;
import com.fillumina.performance.template.MixedAssertionBuilder;
import com.fillumina.performance.template.MixedConfigurationBuilder;
import com.fillumina.performance.template.PerformanceTemplate;
import java.util.Random;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ComparativePerformanceApp {

    public static void main(final String[] args) {
        new PerformanceTemplate() {
            @Override
            public void addAssertions(MixedAssertionBuilder<?> assertions) {
            }

            @Override
            public void config(MixedConfigurationBuilder<?> config) {
                config.speedConfig().setSamples(33);
            }

            @Override
            public void addTests(TestConfiguration<?> tests) {
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
                tests.addTest("cached", new Runnable() {
                    private Random rnd = new CachedRandom(256);
                    @Override
                    public void run() {
                        drain(rnd.nextInt());
                    }
                });
            }
        }.executeWithFullOutput();
    }
}
