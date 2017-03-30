package com.fillumina.performance.suite;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.speed.sample.strgen.SampleCsvStringGenerator;
import com.fillumina.performance.speed.sample.strgen.SampleLineStringGenerator;
import com.fillumina.performance.speed.stats.AssertSpeed;
import com.fillumina.performance.speed.stats.SpeedSuite;
import com.fillumina.performance.speed.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.speed.stats.progression.ProgressionPerformanceInstrumenter;
import com.fillumina.performance.speed.stats.strgen.WrapperSpeedStatsTableStringGenerator;
import com.fillumina.performance.template.ParameterizedSequenceMixedAssertion;
import com.fillumina.performance.template.ParameterizedSequencePerformanceTemplate;
import com.fillumina.performance.template.TestConfiguration;
import com.fillumina.performance.util.Bag;
import com.fillumina.performance.util.rnd.Lfsr;
import com.fillumina.performance.util.rnd.XorShiftPlusRandom;
import com.fillumina.performance.util.stats.Ratio;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class ParameterizedSequencePerformanceSuiteTest {
    private static final int ITERATIONS = 10;
    private static final int SAMPLE = 30;

    private Appendable printout;

    public static void main(final String[] args) {
        final ParameterizedSequencePerformanceSuiteTest test =
                new ParameterizedSequencePerformanceSuiteTest();
        test.printout = System.out;
        //test.shouldRunTheSameTestWithDifferentObjectAndSequenceItem();
//        test.shouldAssertParameterAndSequenceSuite();
        test.shouldBla();
    }

    @Test
    public void shouldRunTheSameTestWithDifferentObjectAndSequenceItem() {
        final Bag<String> countingMap = new Bag<>();

        PerformanceTimerFactory.createSingleThreaded()
            .addPerformanceConsumer(
                    SampleCsvStringGenerator.appendTo(printout))
            .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                        .setIterationProgression(ITERATIONS)
                        .setSamples(SAMPLE)
                        .build())
            .addPerformanceConsumer(WrapperSpeedStatsTableStringGenerator.appendTo(printout))
            .instrumentedBy(SpeedSuite.<Character>parameterizedSuite())
            .addParameter("First Object", 'a')
            .addParameter("Second Object", 'b')
            .instrumentedBy(SpeedSuite.<Character,Integer>parameterizedSequenceSuite())
            .setName("shouldRunTheSameTestWithDifferentObjectAndSequenceItem")
            .setSequence(1, 2, 3)
            .addTest("First Test",
                    new ParameterizedSequenceTestable<Character, Integer>() {

                @Override
                public Object test(final Character param, final Integer sequence) {
                    final String key = String.valueOf(param) + sequence;
                    countingMap.add(key);
                    return null;
                }
            })
            .addTest("Second Test",
                    new ParameterizedSequenceTestable<Character, Integer>() {

                @Override
                public Object test(final Character param, final Integer sequence) {
                    final String key = String.valueOf(param) + sequence;
                    countingMap.add(key);
                    return null;
                }
            })

            .execute()
            .printTo(printout);

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
            .addPerformanceConsumer(
                    SampleLineStringGenerator.appendTo(printout))
            .instrumentedBy(AutoProgressionPerformanceInstrumenter.builder()
                        .setSamples(100)
//                        .setGetSamplesUntilTimeout(true)
                        .build())
            .addPerformanceConsumer(WrapperSpeedStatsTableStringGenerator.appendTo(printout))
            .instrumentedBy(SpeedSuite.<List<Integer>>parameterizedSuite())
            .addParameter("LinkedList", new LinkedList<Integer>())
            .addParameter("ArrayList", new ArrayList<Integer>())
            .instrumentedBy(
                    SpeedSuite.<List<Integer>, Integer>parameterizedSequenceSuite())
            .setSequence(16, 128)
            .setName("shouldAssertParameterAndSequenceSuite")
            .addTest("Read Test",
                    new ParameterizedSequenceTestable<List<Integer>, Integer>() {
                private Lfsr lfsr;

                @Override
                public void setUp(List<Integer> list, Integer sequence) {
                    list.clear();
                    for (int i=0; i<sequence; i++) {
                        list.add(i);
                    }
                }

                @Override
                public void onBeforeTest(List<Integer> param,
                        Integer sequence, int iterations) {
                    switch (sequence) {
                        case 16: lfsr = new Lfsr(4); break;
                        case 128: lfsr = new Lfsr(7); break;
                    }
                }

                @Override
                public Object test(List<Integer> list, Integer sequence) {
                    int r = lfsr.next();
                    return list.get(r) == r;
                }
            })

            .addPerformanceConsumer(
                    AssertSpeed.parameterizedSequence()
                        .forSequenceValue("2")
                            .forAllTests()
                                .setTolerance(Ratio.percentage(5))
                                    .assertOrder("LinkedList").greaterThan("ArrayList")
                                .end()
                            .endTests()
                        .endSequences()
            )
            .execute()
            .checkAndPrint(printout, AssertSpeed.parameterizedSequence()
                        .forSequenceValue("2")
                            .forAllTests()
                                .setTolerance(Ratio.percentage(5))
                                    .assertOrder("LinkedList").greaterThan("ArrayList")
                                .end()
                            .endTests()
                        .endSequences()
            )
            .printTo(printout);
    }

    @Ignore @Test
    public void shouldBla() {
        new ParameterizedSequencePerformanceTemplate<List<Integer>,Integer>() {
            @Override
            public void addParameters(ParameterContainer<List<Integer>> params) {
                params
                        .addParameter("LinkedList", new LinkedList<>())
                        .addParameter("ArrayList", new ArrayList<>());
            }

            @Override
            public void addSequence(SequenceContainer<Integer> sequence) {
                sequence.setSequence(10, 100);
            }

            @Override
            public void addAssertions(
                    ParameterizedSequenceMixedAssertion assertion) {
            }

            @Override
            public void config(TestConfiguration config) {
                config.speedTestOnly();
            }

            @Override
            public void addTests(
                    TestContainer<ParameterizedSequenceTestable<List<Integer>, Integer>> tests) {
                tests.addTest("test", new ParameterizedSequenceTestable<List<Integer>, Integer>() {
                    private XorShiftPlusRandom rnd = new XorShiftPlusRandom();
                    private int[] randomSequence;
                    private int index;

                    @Override
                    public void setUp(List<Integer> list, Integer sequence) {
                        randomSequence = new int[sequence];
                        list.clear();
                        for (int i=0; i<sequence; i++) {
                            list.add(i);
                        }
                    }

                    @Override
                    public void onBeforeTest(List<Integer> param,
                            Integer sequence, int iterations) {
                        for (int i=0; i<sequence; i++) {
                            randomSequence[i] = rnd.nextInt(sequence);
                        }
                    }

                    @Override
                    public Object test(List<Integer> list, Integer sequence) {
                        final int r = randomSequence[index];
                        index += index % sequence;
                        return list.get(r) == r;
                    }
                });
            }
        }.executeWithFullOutput();
    }
}
