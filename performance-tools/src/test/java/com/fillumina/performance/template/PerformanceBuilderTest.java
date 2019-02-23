package com.fillumina.performance.template;

import com.fillumina.performance.executor.PN;
import com.fillumina.performance.executor.generator.ProducerConfiguration;
import com.fillumina.performance.executor.generator.ProducerConfigurationImpl;
import com.fillumina.performance.executor.generator.TestConfiguration;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.mock.MockStatsType;
import com.fillumina.performance.mock.SampleProducerMockBuilder;
import com.fillumina.performance.template.PerformanceBuilder.MixedHolder;
import com.fillumina.performance.util.unit.IntervalUnit;
import com.fillumina.performance.util.unit.Magnitude;
import com.fillumina.performance.util.unit.Quantity;
import java.util.Collections;
import java.util.List;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceBuilderTest {

    public class ConfigurationImpl
            implements PerformanceBuilder.Configuration {
        String failureAudioFilename;
        String successAudioFilename;
        boolean alertActive;
        PerformanceBuilderListener listener;
        TestConfiguration<?> testConfig;
        List<ProducerConfiguration> producers;
        MixedAssertionableResult.Builder resultBuilder =
                new MixedAssertionableResult.Builder();

        @Override
        public void setConsole(Appendable appendable, Verbosity verbosity) {
        }

        @Override
        public String getFailureAudioFilename() {
            return failureAudioFilename;
        }

        @Override
        public String getSuccessAudioFilename() {
            return successAudioFilename;
        }

        @Override
        public boolean isAlertActive() {
            return alertActive;
        }

        @Override
        public PerformanceBuilderListener getPerformanceBuilderListener() {
            return listener;
        }

        @Override
        public MixedAssertionableResult.Builder getMixedAssertionableResultBuilder() {
            return resultBuilder;
        }

        @Override
        public TestConfiguration<?> getTestConfig() {
            return testConfig;
        }

        @Override
        public List<ProducerConfiguration> getProducers() {
            return producers;
        }
    }

    @Test
    public void shouldExecuteTest() {
        ConfigurationImpl config = new ConfigurationImpl();
        config.producers = Collections.singletonList(
                new ProducerConfigurationImpl(
                        new SampleProducerMockBuilder()
                                .addTest("first")
                                    .mean(12.3)
                                    .stdev(4.5)
                                .endTest()
                                .addTest("second")
                                    .mean(78.9)
                                    .stdev(1.5)
                                .endTest()
                            .buildWithSyntheticNormalValues(Magnitude.UNIT) ));

        config.listener = new PerformanceBuilderListener() {
            @Override
            public void onResults(MixedConfiguration configuration,
                    MixedAssertionableResult<?> mixedStats,
                    Quantity<IntervalUnit> elapsed) {
            }

            @Override
            public void onConfiguration(MixedConfiguration configuration) {
            }
        };

        config.testConfig = new TestConfiguration()
                    .addTest("first", () -> {})
                    .addTest("second", () -> {})
                .build();


        MixedHolder mHolder = new PerformanceBuilder(config)
                .exec(null, Verbosity.NO_OUTPUT);

        Stats stats = mHolder.getResult(MockStatsType.INSTANCE)
                .getStatsHolder().getStats();

        // System.out.println(stats.toString());

        assertTrue(stats.getNames().contains(PN.pname("first")));
        assertTrue(stats.getNames().contains(PN.pname("second")));
    }

}
