package com.fillumina.performance.executor.generator;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.executor.annotation.Param;
import com.fillumina.performance.executor.annotation.Sequence;
import com.fillumina.performance.executor.stats.MixedStatsHolder;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.StatsHolder;
import com.fillumina.performance.executor.stats.StatsType;
import com.fillumina.performance.mock.MockStatsType;
import com.fillumina.performance.mock.SampleProducerMockBuilder;
import com.fillumina.performance.mock.StatsMockBuilder;
import com.fillumina.performance.util.tname.TName;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceGeneratorTest {

    @Test
    public void shouldExecuteSingleTest() {
        ProducerConfiguration prodConf = new ProducerConfigurationImpl(
            new SampleProducerMockBuilder()
                .samples(33)
                .addTest("a")
                    .mean(10.0)
                    .stdev(1.2)
                .endTest()
                .buildWithSyntheticNormalValues());
        TestConfiguration<?> testConfig = new TestConfiguration<>()
                .addTest("a", () -> {});

        MixedStatsHolder holder = PerformanceGenerator.INSTANCE
                .executeSingleTest(testConfig, prodConf);

        // holder.print();
        Stats stats = holder.getStatsHolder(MockStatsType.INSTANCE).getStats();

        assertEquals(10.0, stats.getMeasure(TN.tname("a")).getMean(), 0.1);
        assertEquals(33, stats.getMeasure(TN.tname("a")).getCount(), 0);
    }

    @Test
    public void shouldExecuteSingleTestWithParameter() {
        TName a1 = TN.tname("a", "one");
        TName a2 = TN.tname("a", "two");
        ProducerConfiguration prodConf = new ProducerConfigurationImpl(
            new SampleProducerMockBuilder()
                .samples(33)
                .addTest(a1)
                    .mean(10.0)
                    .stdev(1.2)
                .endTest()
                .addTest(a2)
                    .mean(20.0)
                    .stdev(1.2)
                .endTest()
                .buildWithSyntheticNormalValues());
        TestConfiguration<?> testConfig = new TestConfiguration<>()
                .parameters()
                    .name("param")
                        .value("one", 1)
                        .value("two", 2)
                    .end()
                .end()
                .addTest("a",
                        new Runnable() {
                            @Param("param") private int value;
                            @Override public void run() {}
                        });

        MixedStatsHolder holder = PerformanceGenerator.INSTANCE
                .executeSingleTest(testConfig, prodConf);

        StatsHolder aHolder = holder.getStatsHolder(MockStatsType.INSTANCE);
        Stats stats = aHolder.getStats(TN.tname("a"));

        assertEquals(10.0, stats.getMeasure(a1).getMean(), 0.1);
        assertEquals(33, stats.getMeasure(a1).getCount(), 0);

        assertEquals(20.0, stats.getMeasure(a2).getMean(), 0.1);
        assertEquals(33, stats.getMeasure(a2).getCount(), 0);
    }

    @Test
    public void shouldExecuteSingleTestWithSequence() {
        ProducerConfiguration prodConf = new ProducerConfigurationImpl(
            new SampleProducerMockBuilder()
                .samples(33)
                .addTest(TN.tname("one", "a"))
                    .mean(10.0)
                    .stdev(1.2)
                .endTest()
                .addTest(TN.tname("two", "a"))
                    .mean(20.0)
                    .stdev(1.2)
                .endTest()
                .buildWithSyntheticNormalValues())
            .samples(33);

        TestConfiguration<?> testConfig = new TestConfiguration<>()
                .sequences()
                    .name("seq")
                        .value("one", 1)
                        .value("two", 2)
                    .end()
                .end()
                .addTest("a",
                        new Runnable() {
                            @Sequence("seq") private int value;
                            @Override public void run() {}
                        });

        MixedStatsHolder holder = PerformanceGenerator.INSTANCE
                .executeSingleTest(testConfig, prodConf);

        StatsHolder aHolder = holder.getStatsHolder(MockStatsType.INSTANCE);

        Stats stats1 = aHolder.getStats(TN.tname("one"));
        assertEquals(10.0, stats1.getMeasure(TN.tname("one","a")).getMean(), 1);
        assertEquals(33, stats1.getMeasure(TN.tname("one","a")).getCount(), 0);

        Stats stats2 = aHolder.getStats(TN.tname("two"));
        assertEquals(20.0, stats2.getMeasure(TN.tname("two","a")).getMean(), 1);
        assertEquals(33, stats2.getMeasure(TN.tname("two","a")).getCount(), 0);
    }

    public static final StatsType TYPE_A = new MockStatsType("TYPE_A");
    public static final StatsType TYPE_B = new MockStatsType("TYPE_B");

    public static class PerformanceGeneratorMock
            extends PerformanceGenerator {

        private MixedStatsHolder[] array = new MixedStatsHolder[2];
        private int index;

        public PerformanceGeneratorMock() {
            Stats statsA = createStats(TYPE_A, "a", 10.0);
            array[0] = MixedStatsHolder.builder()
                    .addAssertable(TYPE_A, TN.tname("a"), statsA)
                    .build();

            Stats statsB = createStats(TYPE_B, "a", 20.0);
            array[1] = MixedStatsHolder.builder()
                    .addAssertable(TYPE_B, TN.tname("a"), statsB)
                    .build();
        }

        private Stats createStats(StatsType type, String name, double mean) {
            return new StatsMockBuilder(type).addTest(TN.tname(name))
                    .mean(mean)
                    .endTest()
                    .buildWithSyntheticNormalValues()
                    .getStatsHolder(type)
                    .getStats();
        }

        @Override
        public MixedStatsHolder executeSingleTest(
                TestConfiguration<?> testConfig,
                ProducerConfiguration prodConfig) {
            return array[index++];
        }
    }

    @Test
    public void shouldExecuteMultiTest() {
        ProducerConfiguration prodConfA = new ProducerConfigurationImpl(
            new SampleProducerMockBuilder(TYPE_A)
                .samples(33)
                .addTest("a")
                    .mean(10.0)
                    .stdev(1.2)
                .endTest()
                .buildWithSyntheticNormalValues());

        ProducerConfiguration prodConfB = new ProducerConfigurationImpl(
            new SampleProducerMockBuilder(TYPE_B)
                .samples(33)
                .addTest("a")
                    .mean(20.0)
                    .stdev(1.2)
                .endTest()
                .buildWithSyntheticNormalValues());

        TestConfiguration<?> testConfig = new TestConfiguration<>()
                .addTest("a", () -> {});

        MixedStatsHolder holder = new PerformanceGeneratorMock()
                .executeMixedTests(testConfig,
                        Arrays.asList(prodConfA, prodConfB));

//        holder.print();

        Stats statsA = holder.getStatsHolder(TYPE_A).getStats();
        assertEquals(10.0, statsA.getMeasure(TN.tname("a")).getMean(), 0.1);

        Stats statsB = holder.getStatsHolder(TYPE_B).getStats();
        assertEquals(20.0, statsB.getMeasure(TN.tname("a")).getMean(), 0.1);
    }

}
