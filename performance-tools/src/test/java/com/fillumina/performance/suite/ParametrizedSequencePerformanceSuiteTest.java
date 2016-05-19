package com.fillumina.performance.suite;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.sample.viewer.StringCsvSampleViewer;
import com.fillumina.performance.stats.assertion.AssertPerformance;
import com.fillumina.performance.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.stats.viewer.StringTableStatsViewer;
import com.fillumina.performance.suite.assertion.AssertParametrizedSequencePerformance;
import com.fillumina.performance.util.Bag;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class ParametrizedSequencePerformanceSuiteTest {
    private static final int ITERATIONS = 10;
    private static final int SAMPLE = 30;

    private boolean printout = false;

    public static void main(final String[] args) {
        final ParametrizedSequencePerformanceSuiteTest test =
                new ParametrizedSequencePerformanceSuiteTest();
        test.printout = true;
        //test.shouldRunTheSameTestWithDifferentObjectAndSequenceItem();
        test.shouldAssertParameterAndSequenceSuite();
    }

    @Ignore @Test
    public void shouldRunTheSameTestWithDifferentObjectAndSequenceItem() {
        final Bag<String> countingMap = new Bag<>();

        PerformanceTimerFactory.createSingleThreaded()
            .addPerformanceConsumerIf(printout, StringCsvSampleViewer.INSTANCE)
            .instrumentedBy(AutoProgressionPerformanceInstrumenter.builder()
                        .setTimeoutSeconds(30)
                        //.setIterationProgression(ITERATIONS)
                        //.setSamplesPerStep(SAMPLE)
                        .build())
            .addPerformanceConsumerIf(printout, StringTableStatsViewer.INSTANCE)
            .instrumentedBy(new ParametrizedPerformanceSuite<Character>())
            .addParameter("First Object", 'a')
            .addParameter("Second Object", 'b')
            .instrumentedBy(
                    new ParametrizedSequencePerformanceSuite<Character, Integer>())
            .setName("shouldRunTheSameTestWithDifferentObjectAndSequenceItem")
            .setSequence(1, 2, 3)
            .addTest("First Test",
                    new ParametrizedSequenceTestable<Character, Integer>() {

                @Override
                public Object test(final Character param, final Integer sequence) {
                    final String key = String.valueOf(param) + sequence;
                    countingMap.add(key);
                    return null;
                }
            })
            .addTest("Second Test",
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

        final int totalTestOccurrences = ITERATIONS * SAMPLE * 2;

        assertEquals(6, countingMap.size());
        assertEquals(totalTestOccurrences, countingMap.getCount("a1"), 0);
        assertEquals(totalTestOccurrences, countingMap.getCount("a2"), 0);
        assertEquals(totalTestOccurrences, countingMap.getCount("a3"), 0);
        assertEquals(totalTestOccurrences, countingMap.getCount("b1"), 0);
        assertEquals(totalTestOccurrences, countingMap.getCount("b2"), 0);
        assertEquals(totalTestOccurrences, countingMap.getCount("b3"), 0);
    }

    @Test
    public void shouldAssertParameterAndSequenceSuite() {

        PerformanceTimerFactory.createSingleThreaded()
            .addPerformanceConsumerIf(printout, StringCsvSampleViewer.INSTANCE)
            .instrumentedBy(AutoProgressionPerformanceInstrumenter.builder()
                        .setTimeoutSeconds(30)
//                        .setGetSamplesUntilTimeout(true)
                        .build())
            .addPerformanceConsumerIf(printout, StringTableStatsViewer.INSTANCE)
            .instrumentedBy(
                    new ParametrizedPerformanceSuite<List<Integer>>())
            .addParameter("LinkedList", new LinkedList<Integer>())
            .addParameter("ArrayList", new ArrayList<Integer>())
            .instrumentedBy(
                    new ParametrizedSequencePerformanceSuite<List<Integer>, Integer>())
            .setSequence(10, 100)
            .setName("shouldAssertParameterAndSequenceSuite")
            .addTest("Read Test",
                    new ParametrizedSequenceTestable<List<Integer>, Integer>() {
                private final Random rnd = new Random(System.currentTimeMillis());

                @Override
                public void setUp(List<Integer> param, Integer sequence) {
                    param.clear();
                    for (int i=0; i<sequence; i++) {
                        param.add(i);
                    }
                }

                @Override
                public Object test(List<Integer> param, Integer sequence) {
                    final int r = rnd.nextInt(sequence);
                    return param.get(r) == r;
                }
            })

            .addPerformanceConsumer(AssertParametrizedSequencePerformance
                    .create()
                    .forSequence("2")
                        .forAllTests(AssertPerformance.withTolerance(5)
                                .assertSpeed("LinkedList")
                                    .slowerThan("ArrayList"))
                    .end())

            .execute()
            .printIf(printout);
    }
}
