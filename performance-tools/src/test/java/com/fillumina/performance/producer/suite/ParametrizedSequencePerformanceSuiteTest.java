package com.fillumina.performance.producer.suite;

import com.fillumina.performance.util.Bag;
import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.consumer.assertion.AssertPerformance;
import com.fillumina.performance.consumer.assertion.AssertPerformanceForExecutionSuite;
import com.fillumina.performance.consumer.viewer.StringTableViewer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParametrizedSequencePerformanceSuiteTest {
    private static final int ITERATIONS = 10;

    private boolean printout = false;

    public static void main(final String[] args) {
        final ParametrizedSequencePerformanceSuiteTest test =
                new ParametrizedSequencePerformanceSuiteTest();
        test.printout = true;
        test.shouldRunTheSameTestWithDifferentObjectAndSequenceItem();
        test.shouldAssertParameterAndSequenceSuite();
    }

    @Test
    public void shouldRunTheSameTestWithDifferentObjectAndSequenceItem() {
        final Bag<String> countingMap = new Bag<>();

        PerformanceTimerFactory.createSingleThreaded()
            .setIterations(ITERATIONS)

            .instrumentedBy(
                new ParametrizedSequencePerformanceSuite<Character, Integer>()
                .addParameter("First Object", 'a')
                .addParameter("Second Object", 'b')
                .setSequence(1, 2, 3))

            .addPerformanceConsumer(printout ? StringTableViewer.INSTANCE : null)

            .executeTest("EXECUTION",
                    new ParametrizedSequenceRunnable<Character, Integer>() {

                @Override
                public Object sink(final Character param, final Integer sequence) {
                    final String key = String.valueOf(param) + sequence;
                    countingMap.add(key);
                    return null;
                }
            }).whenever(printout).use(StringTableViewer.INSTANCE);

            assertEquals(6, countingMap.size());
            assertEquals(ITERATIONS, countingMap.getCount("a1"), 0);
            assertEquals(ITERATIONS, countingMap.getCount("a2"), 0);
            assertEquals(ITERATIONS, countingMap.getCount("a3"), 0);
            assertEquals(ITERATIONS, countingMap.getCount("b1"), 0);
            assertEquals(ITERATIONS, countingMap.getCount("b2"), 0);
            assertEquals(ITERATIONS, countingMap.getCount("b3"), 0);
    }

    @Test
    public void shouldAssertParameterAndSequenceSuite() {

        PerformanceTimerFactory.createSingleThreaded()
            .setIterations(ITERATIONS)

            .instrumentedBy(
                new ParametrizedSequencePerformanceSuite<List<Integer>, Integer>()
                .addParameter("LinkedList", new LinkedList<Integer>())
                .addParameter("ArrayList", new ArrayList<Integer>())
                .setSequence(10, 1_000))

            // checked at each item of the sequence
            .addPerformanceConsumer(
                AssertPerformanceForExecutionSuite.withTolerance(5)
                    .forExecution("ASSERTION-10")
                        .assertTest("LinkedList").slowerThan("ArrayList"),
                AssertPerformanceForExecutionSuite.withTolerance(5)
                    .forExecution("ASSERTION-1000")
                        .assertTest("LinkedList").slowerThan("ArrayList"))

            .addPerformanceConsumer(StringTableViewer.INSTANCE)

            .executeTest("ASSERTION",
                    new ParametrizedSequenceRunnable<List<Integer>, Integer>() {
                private final Random rnd = new Random();

                @Override
                public void setUp(final List<Integer> param, final Integer sequence) {
                    param.clear();
                    for (int i=0; i<sequence; i++) {
                        param.add(i);
                    }
                }

                @Override
                public Object sink(final List<Integer> param,
                        final Integer sequence) {
                    return param.get(rnd.nextInt(sequence));
                }
            })
                // checked on the average of all the sequences
                .use(AssertPerformance.withTolerance(5)
                    .assertPercentageFor("LinkedList").sameAs(100))
                .whenever(printout).use(StringTableViewer.INSTANCE);
    }
}
