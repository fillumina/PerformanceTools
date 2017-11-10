package com.fillumina.performance.executor.generator;

import com.fillumina.performance.executor.AssertableHolder;
import com.fillumina.performance.executor.MixedAssertableHolder;
import com.fillumina.performance.executor.TN;
import com.fillumina.performance.executor.annotation.Param;
import com.fillumina.performance.executor.annotation.Sequence;
import com.fillumina.performance.mock.SampleProducerMockBuilder;
import com.fillumina.performance.mock.StatsMock;
import com.fillumina.performance.util.tname.TName;
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

        MixedAssertableHolder holder = PerformanceGenerator.INSTANCE
                .executeSingleTest(testConfig, prodConf);

        // holder.print();
        StatsMock stats = holder.getStats(StatsMock.class).getAssertable();

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

        MixedAssertableHolder holder = PerformanceGenerator.INSTANCE
                .executeSingleTest(testConfig, prodConf);

        AssertableHolder<StatsMock> aHolder = holder.getStats(StatsMock.class);
        StatsMock stats = aHolder.getAssertable(TN.tname("a"));

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

        MixedAssertableHolder holder = PerformanceGenerator.INSTANCE
                .executeSingleTest(testConfig, prodConf);

        AssertableHolder<StatsMock> aHolder = holder.getStats(StatsMock.class);

        StatsMock stats1 = aHolder.getAssertable(TN.tname("one"));
        assertEquals(10.0, stats1.getMeasure(TN.tname("one","a")).getMean(), 1);
        assertEquals(33, stats1.getMeasure(TN.tname("one","a")).getCount(), 0);

        StatsMock stats2 = aHolder.getAssertable(TN.tname("two"));
        assertEquals(20.0, stats2.getMeasure(TN.tname("two","a")).getMean(), 1);
        assertEquals(33, stats2.getMeasure(TN.tname("two","a")).getCount(), 0);
    }

}
