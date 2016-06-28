package com.fillumina.performance.suite;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.speed.sample.strgen.SampleCsvStringGenerator;
import com.fillumina.performance.speed.sample.strgen.SampleLineStringGenerator;
import com.fillumina.performance.speed.stats.AssertSpeed;
import com.fillumina.performance.speed.stats.SpeedSuite;
import com.fillumina.performance.speed.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.speed.stats.progression.ProgressionPerformanceInstrumenter;
import com.fillumina.performance.speed.stats.strgen.SpeedTableStringGenerator;
import com.fillumina.performance.util.Bag;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import static org.junit.Assert.*;
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

    @Test
    public void shouldRunTheSameTestWithDifferentObjectAndSequenceItem() {
        final Bag<String> countingMap = new Bag<>();

        PerformanceTimerFactory.createSingleThreaded()
            .addPerformanceConsumerIf(printout, SampleCsvStringGenerator.VIEWER)
            .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                        .setTimeoutSeconds(30)
                        .setIterationProgression(ITERATIONS)
                        .setSamples(SAMPLE)
                        .build())
            .addPerformanceConsumerIf(printout, SpeedTableStringGenerator.VIEWER)
            .instrumentedBy(SpeedSuite.<Character>parametrizedSuite())
            .addParameter("First Object", 'a')
            .addParameter("Second Object", 'b')
            .instrumentedBy(SpeedSuite.<Character,Integer>parametrizedSequenceSuite())
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
            .addPerformanceConsumerIf(printout, SampleLineStringGenerator.VIEWER)
            .instrumentedBy(AutoProgressionPerformanceInstrumenter.builder()
                        .setTimeoutSeconds(600)
//                        .setGetSamplesUntilTimeout(true)
                        .build())
            .addPerformanceConsumerIf(printout, SpeedTableStringGenerator.VIEWER)
            .instrumentedBy(SpeedSuite.<List<Integer>>parametrizedSuite())
            .addParameter("LinkedList", new LinkedList<Integer>())
            .addParameter("ArrayList", new ArrayList<Integer>())
            .instrumentedBy(
                    SpeedSuite.<List<Integer>, Integer>parametrizedSequenceSuite())
            .setSequence(10, 100)
            .setName("shouldAssertParameterAndSequenceSuite")
            .addTest("Read Test",
                    new ParametrizedSequenceTestable<List<Integer>, Integer>() {
                private int[] randomSequence;
                private int index;

                @Override
                public void setUp(List<Integer> list, Integer sequence) {
                    ThreadLocalRandom rnd = ThreadLocalRandom.current();
                    randomSequence = new int[sequence];
                    list.clear();
                    for (int i=0; i<sequence; i++) {
                        list.add(i);
                        randomSequence[i] = rnd.nextInt(sequence);
                    }
                }

                @Override
                public Object test(List<Integer> param, Integer sequence) {
                    final int r = randomSequence[index];
                    index += index % sequence;
                    return param.get(r) == r;
                }
            })

            .addPerformanceConsumer(AssertSpeed.parametrizedSequence()
                    .forSequence("2")
                        .forAllTests(AssertSpeed.withTolerancePercentage(5)
                            .assertOrder("LinkedList").greaterThan("ArrayList"))
                        .endTests())

            .execute()
            .printIf(printout);
    }
}
