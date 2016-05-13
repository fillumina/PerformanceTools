package com.fillumina.performance.suite;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.stats.progression.ProgressionPerformanceInstrumenter;
import com.fillumina.performance.util.Bag;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class ParametrizedSequencePerformanceSuiteTest {
    private static final int ITERATIONS = 10;
    private static final int SAMPLE = 30;

    private boolean printout = true;

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

            .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                        .setIterationProgression(ITERATIONS)
                        .setSamplesPerStep(SAMPLE)
                        .build())
            //.addPerformanceConsumerIf(printout, StringTableStatsViewer.INSTANCE)
            .instrumentedBy(new ParametrizedPerformanceSuite<Character>())
            .addParameter("First Object", 'a')
            .addParameter("Second Object", 'b')
            .instrumentedBy(
                    new ParametrizedSequencePerformanceSuite<Character, Integer>())
            .setSequence(1, 2, 3)
            .addTest("EXECUTION",
                    new ParametrizedSequenceTestable<Character, Integer>() {

                @Override
                public Object test(final Character param, final Integer sequence) {
                    final String key = String.valueOf(param) + sequence;
                    countingMap.add(key);
                    return null;
                }
            })

            .execute()
            .printIf(printout);

            assertEquals(6, countingMap.size());
            assertEquals(ITERATIONS * SAMPLE, countingMap.getCount("a1"), 0);
            assertEquals(ITERATIONS * SAMPLE, countingMap.getCount("a2"), 0);
            assertEquals(ITERATIONS * SAMPLE, countingMap.getCount("a3"), 0);
            assertEquals(ITERATIONS * SAMPLE, countingMap.getCount("b1"), 0);
            assertEquals(ITERATIONS * SAMPLE, countingMap.getCount("b2"), 0);
            assertEquals(ITERATIONS * SAMPLE, countingMap.getCount("b3"), 0);
    }

    @Test
    public void shouldAssertParameterAndSequenceSuite() {

        PerformanceTimerFactory.createSingleThreaded()
            .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                        .setIterationProgression(30)
                        .setAddBaselineTest(false)
                        .build())
            .instrumentedBy(
                    new ParametrizedPerformanceSuite<List<Integer>>())
            .addParameter("LinkedList", new LinkedList<Integer>())
            .addParameter("ArrayList", new ArrayList<Integer>())
            .instrumentedBy(
                    new ParametrizedSequencePerformanceSuite<List<Integer>, Integer>())
            .setSequence(1, 2, 3)
            .setSequence(10, 1_000)

            .addTest("ASSERTION",
                    new ParametrizedSequenceTestable<List<Integer>, Integer>() {
                private final Random rnd = new Random(System.currentTimeMillis());

                @Override
                public void setUp(final List<Integer> param, final Integer sequence) {
                    param.clear();
                    for (int i=0; i<sequence; i++) {
                        param.add(i);
                    }
                }

                @Override
                public Object test(final List<Integer> param,
                        final Integer sequence) {
                    return param.get(rnd.nextInt(sequence));
                }
            })

                //FIXME assertion
            // checked for each item of the sequence
//            .addPerformanceConsumer(
//                AssertPerformance.withTolerance(5)
//                    .forExecution("ASSERTION_10")
//                        .assertSpeed("LinkedList").slowerThan("ArrayList"),
//                AssertPerformance.withTolerance(5)
//                    .forExecution("ASSERTION_1000")
//                        .assertSpeed("LinkedList").slowerThan("ArrayList"))

            .execute()
            .printIf(printout);
    }
}
