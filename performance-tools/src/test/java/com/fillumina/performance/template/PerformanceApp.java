package com.fillumina.performance.template;

import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.assertion.TNameMatcherAssertion;
import com.fillumina.performance.infrastructure.DoubleLfsrRunnable;
import com.fillumina.performance.infrastructure.LfsrRunnable;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.param.ParameterizedTestProducer;
import com.fillumina.performance.param.SequencedTestProducer;
import com.fillumina.performance.speed.sample.DefaultPerformanceTimer;
import com.fillumina.performance.speed.sample.PerformanceTimerFactory;
import com.fillumina.performance.speed.sample.iterator.SelectorMultiThreadPerformanceExecutor;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.progression.ConfigurableStatsProducer;
import com.fillumina.performance.speed.stats.progression.ConsecutiveExecutorStatsProducer;
import com.fillumina.performance.speed.stats.progression.IncreasingSamplesStrategy;
import com.fillumina.performance.speed.stats.progression.StatsProducerFactory;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.collection.LinkedTree;
import com.fillumina.performance.util.stats.Ratio;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceApp {

    public static void main(final String[] args) {
        increasingSample();
//        repeating();
    }

    private static class TestConfig
            implements
                ParameterizedTestProducer.Configuration,
                SequencedTestProducer.Configuration,
                TestContainer<Runnable> {

        @Override
        public LinkedTree<String, Object> getParameters() {
            return null;
        }

        @Override
        public LinkedTree<String, Object> getSequences() {
            return null;
        }

        @Override
        public LinkedMap<TName, Runnable> getTests() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public TestContainer<Runnable> addTests(Map<TName, Runnable> tests) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public TestContainer<Runnable> ignoreTest(String name, Runnable test) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public TestContainer<Runnable> ignoreTest(TName name, Runnable test) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public TestContainer<Runnable> addTest(String name, Runnable test) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public TestContainer<Runnable> addTest(TName name, Runnable test) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public TestContainer<Runnable> clearTests() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    private static class SpeedConfig
            implements
                SelectorMultiThreadPerformanceExecutor.Configuration,
                ConsecutiveExecutorStatsProducer.Configuration,
                ConfigurableStatsProducer.Configuration,
                IncreasingSamplesStrategy.Configuration {

        @Override
        public boolean isConsecutiveExecution() {
            return true;
        }

        @Override
        public int getConcurrencyLevel() {
            return 1;
        }

        @Override
        public int getWorkerNumber() {
            return 1;
        }

        @Override
        public long getTimeoutValue() {
            return 1_000;
        }

        @Override
        public TimeUnit getTimeoutUnit() {
            return TimeUnit.SECONDS;
        }

        @Override
        public long getTimeoutNanoseconds() {
            return 1_000_000_000_000L;
        }

        @Override
        public int getGarbageCollectorMillis() {
            return -1;
        }

        @Override
        public boolean getFilterSamples() {
            return true;
        }

        @Override
        public boolean getCoolDownCpu() {
            return false;
        }

        @Override
        public int getSamples() {
            return 33;
        }

        @Override
        public double getMaxPercentageMargin() {
            return 5.0;
        }

        @Override
        public int getMillisecondsPerSample() {
            return 250;
        }
    }


    private static void increasingSample() {
        ConsoleSpeedProgressionListener listener =
                new ConsoleSpeedProgressionListener(2);

        SpeedConfig speedConfig = new SpeedConfig();
        TestConfig testConfig = new TestConfig();

        Assertion<SpeedStats> assertion =
                new TNameMatcherAssertion<Assertion<SpeedStats>, SpeedStats>()
                        .withTolerance(Ratio.P_10)
                        .order().string("single").end()
                        .lessThan().string("double").end();

        new DefaultPerformanceTimer(
                new SelectorMultiThreadPerformanceExecutor(speedConfig))

                .instrumentedBy(
                    new ConfigurableStatsProducer(speedConfig,
                        new IncreasingSamplesStrategy(speedConfig)))

                .addSampleProgressionListener(listener)
                .addStatsProgressionListener(listener)

                .instrumentedBy(new ConsecutiveExecutorStatsProducer(speedConfig))
                .instrumentedBy(new ParameterizedTestProducer<>(testConfig))
                .instrumentedBy(new SequencedTestProducer<>(testConfig))

                .addTests(testConfig.getTests())

                .execute()

                .addAssertion(assertion)

                .print();
    }

    private static void repeating() {
        ConsoleSpeedProgressionListener listener =
                new ConsoleSpeedProgressionListener(2);

        PerformanceTimerFactory.createSingleThreaded()
                .instrumentedBy(StatsProducerFactory.INSTANCE
                        .useRepeatingStrategy().build())
                .addTest("single", new LfsrRunnable())
                .addTest("double", new DoubleLfsrRunnable())
                .addSampleProgressionListener(listener)
                .addStatsProgressionListener(listener)
                .execute()
                .print();
    }

}
