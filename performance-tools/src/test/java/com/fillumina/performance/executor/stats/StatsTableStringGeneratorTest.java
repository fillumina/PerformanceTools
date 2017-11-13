package com.fillumina.performance.executor.stats;

import com.fillumina.performance.mock.StatsMock;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StatsTableStringGeneratorTest {

    private boolean output;

    public static void main(final String[] args) {
        StatsTableStringGeneratorTest test = new StatsTableStringGeneratorTest();
        test.output = true;
        test.shouldGenerateStringFromSingleStats();
        test.shouldGenerateStringFromMultipleStats();
    }

    @Test
    public void shouldGenerateStringFromSingleStats() {
        StatsMock stats = StatsMock.builder()
                .name("single")
                .addTest("one")
                    .mean(10.0)
                    .stdev(2.3)
                    .samples(33)
                .endTest()
                .buildWithSyntheticNormalValues()
                .getStats(StatsMock.class)
                .getAssertable();

        String str = StatsTableStringGenerator.INSTANCE.toString(stats);
        assertNotNull(str);
        if (output) {
            System.out.println(str);
        }
    }

    @Test
    public void shouldGenerateStringFromMultipleStats() {
        StatsMock stats = StatsMock.builder()
                .name("multiple")
                .addTest("one")
                    .mean(10.0)
                    .stdev(2.3)
                    .samples(33)
                .endTest()
                .addTest("two")
                    .mean(20.0)
                    .stdev(4.3)
                    .samples(100)
                .endTest()
                .buildWithSyntheticNormalValues()
                .getStats(StatsMock.class)
                .getAssertable();

        String str = StatsTableStringGenerator.INSTANCE.toString(stats);
        assertNotNull(str);
        if (output) {
            System.out.println(str);
        }
    }

}
