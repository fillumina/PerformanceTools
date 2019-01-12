package com.fillumina.performance.integration;

import com.fillumina.performance.executor.annotation.Param;
import com.fillumina.performance.executor.annotation.Sequence;
import com.fillumina.performance.executor.generator.TestConfiguration;
import com.fillumina.performance.template.MixedAssertionBuilder;
import com.fillumina.performance.template.MixedConfigurationBuilder;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.util.sequence.IntegerSequence;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.MemUnit;
import org.junit.Test;

/**
 * Uses parameters and sequences to specify a complex test executed against
 * speed and memory. Checks complex assertions against it.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParameterizedSequencePerformanceTemplateTest
        extends PerformanceTemplate {

    private static final String TEST = "test";

    public interface Creator {
        Object create(int size);
    }

    public static void main(final String[] args) {
        new ParameterizedSequencePerformanceTemplateTest()
                .executeWithFullOutput();
    }

    @Test
    public void shouldExecuteTest() {
        executeWithoutOutput();
    }

    @Override
    public void config(MixedConfigurationBuilder<?> config) {
    }

    @Override
    public void addTests(TestConfiguration<?> tests) {
        tests
            .addTest(TEST, new Runnable() {
                @Param
                private Creator creator;

                @Sequence
                private int size;

                @Override public void run() {
                    creator.create(size);
                }
            })
            .addParameter("creator")
                .value("byte", (Creator) (int size) -> new byte[100 * size])
                .value("double", (Creator) (int size) -> new double[100 * size])
                .end()
            .end()
            .addSequence("size").values(IntegerSequence.from(1).to(3).step(1));
    }

    @Override
    public void addAssertions(MixedAssertionBuilder<?> assertions) {
        assertions
            .avgTime()
                .forTest("size_1", TEST)
                    .tolerance(Ratio.percentage(5))
                    .order("byte").lessThan("double")
                .end()
            .usedMemory()
                .forTest("size_1", TEST)
                    .tolerance(Ratio.percentage(5))
                    .order("byte").lessThan("double")
                    .value("byte").equalsTo(MemUnit.B.quantity(120))
                .forTest("size_2", TEST)
                    .tolerance(Ratio.percentage(5))
                    .order("byte").lessThan("double")
                .end()
            .allocatedMemory()
                .with().all().end()
                    .tolerance(Ratio.percentage(5))
                    .value("byte").equalsTo(MemUnit.B.zero())
                    .value("double").equalsTo(MemUnit.B.zero())
                .end()
            .build();
    }
}
