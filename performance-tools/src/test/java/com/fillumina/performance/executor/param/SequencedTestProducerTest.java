package com.fillumina.performance.executor.param;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.executor.annotation.Sequence;
import com.fillumina.performance.executor.stats.MixedStatsHolder;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.StatsHolder;
import com.fillumina.performance.mock.MockStatsType;
import com.fillumina.performance.mock.NameStatsProducerMock;
import com.fillumina.performance.mock.StatsProducerMock;
import com.fillumina.performance.util.ReflectionHelper;
import com.fillumina.performance.util.collection.IndexedArrayMap;
import com.fillumina.performance.util.collection.LinkedTree;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.tname.TName;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SequencedTestProducerTest {
    private boolean printout = false;

    @Test
    public void shouldIncludeTestName() {
        final LinkedTree<String,Object> params =
                LinkedTree.<String,Object>builder()
                            .branch("param")
                                .leaf("a", 'a')
                                .leaf("b", 'b')
                            .end()
                            .getRoot();

        SequencedTestProducer producer =
                new SequencedTestProducer(params);

        NameStatsProducerMock statsProducer = new NameStatsProducerMock();

        producer.instrument(statsProducer);

        producer.addTest("test",
                new Runnable() {
                    @Sequence private char param;
                    @Override public void run() {}
                });

        producer.setName("XYZ");

        MixedStatsHolder holder = producer.execute();
        StatsHolder aHolder = holder.getStatsHolder(MockStatsType.INSTANCE);

        Stats statsA = aHolder.getStatsAtPath("XYZ", "a");
        assertEquals(1.0,
                statsA.getMeasure(TN.tname("XYZ", "a", "test")).getMean(), 0);

        Stats statsB = aHolder.getStatsAtPath("XYZ", "b");
        assertEquals(2.0,
                statsB.getMeasure(TN.tname("XYZ", "b", "test")).getMean(), 0);

        List<List<CharSequence>> tree = statsProducer.getTree();

        assertEquals(TN.tname("XYZ", "a", "test"), tree.get(0).get(0));
        assertEquals(TN.tname("XYZ", "b", "test"), tree.get(1).get(0));
    }

    @Test
    public void shouldSubstituteSinlgeParameterSingleTest() {
        final LinkedTree<String,Object> params =
                LinkedTree.<String,Object>builder()
                            .branch("confidence")
                                .leaf("P95", Ratio.P_95)
                                .leaf("P99", Ratio.P_99)
                            .end()
                            .getRoot();

        final IndexedArrayMap<String,Runnable> tests =
                IndexedArrayMap.<String,Runnable>create(
                        "one", new Runnable() {
                                @Sequence("confidence")
                                private Ratio ratio;

                                @Sequence
                                private int size;

                                @Override
                                public void run() {}
                            });

        List<Map<CharSequence, Runnable>> exec = getExecutedTests(tests, params,
                TN.tname("P95", "one"), 10.0,
                TN.tname("P95", "two"), 100.0,
                TN.tname("P99", "one"), 20.0,
                TN.tname("P99", "two"), 200.0);

        if (printout) {
            printTree(exec);
        }

        assertEquals(2, exec.size());

        assertValues(exec.get(0).get(TN.tname("P95", "one")), Ratio.P_95, 0);
        assertValues(exec.get(1).get(TN.tname("P99", "one")), Ratio.P_99, 0);
    }

    @Test
    public void shouldSubstituteParameter() {
        final LinkedTree<String,Object> params =
                LinkedTree.<String,Object>builder()
                            .branch("confidence")
                                .leaf("P95", Ratio.P_95)
                                .leaf("P99", Ratio.P_99)
                            .end()
                            .branch("size")
                                .leaf("10", 10)
                                .leaf("100", 100)
                            .end()
                            .getRoot();

        final IndexedArrayMap<String,Runnable> tests =
                IndexedArrayMap.<String,Runnable>create(
                        "one", new Runnable() {
                                @Sequence("confidence")
                                private Ratio ratio;

                                @Sequence
                                private int size;

                                @Override
                                public void run() {}
                            },
                        "two", new Runnable() {
                                @Sequence("confidence")
                                private Ratio ratio;

                                @Sequence
                                private int size;

                                @Override
                                public void run() {}
                            }
                );

        List<Map<CharSequence, Runnable>> exec = getExecutedTests(tests, params,
                TN.tname("P95", "10", "one"), 10.0,
                TN.tname("P95", "10", "two"), 100.0,
                TN.tname("P95", "100", "one"), 20.0,
                TN.tname("P95", "100", "two"), 200.0,
                TN.tname("P99", "10", "one"), 30.0,
                TN.tname("P99", "10", "two"), 300.0,
                TN.tname("P99", "100", "one"), 40.0,
                TN.tname("P99", "100", "two"), 400.0);

        if (printout) {
            printTree(exec);
        }

        assertEquals(4, exec.size());

        TName[] array = {
            TN.tname("P95", "10"),
            TN.tname("P99", "10"),
            TN.tname("P95", "100"),
            TN.tname("P99", "100")
        };

        int index = 0;
        for (TName name : array) {
            Map<CharSequence, Runnable> map = exec.get(index);
            index++;
            Runnable one = map.get(name.append("one"));
            Runnable two = map.get(name.append("two"));
            Ratio ratio = "P95".equals(name.getFirstName()) ?
                    Ratio.P_95 : Ratio.P_99;
            int size = "10".equals(name.getLastName()) ? 10 : 100;
            assertValues(one, ratio, size);
            assertValues(two, ratio, size);
        }
    }

    private void assertValues(Runnable runnable, Ratio ratio, int size) {
        final Ratio ratioFieldValue = (Ratio)
                ReflectionHelper.getFieldValue(runnable, "ratio");
        final int sizeFieldValue = (int)
                ReflectionHelper.getFieldValue(runnable, "size");

        assertEquals(ratio, ratioFieldValue);
        assertEquals(size, sizeFieldValue, 0);
    }

    private void printTree(List<Map<CharSequence, Runnable>> exec) {
        TableFormatter table = new TableFormatter("   ");
        table.row("test", "confidence", "size").hr('=');
        for (Map<CharSequence, Runnable> map : exec) {

            for (Entry<CharSequence, Runnable> e : map.entrySet()) {
                final Ratio ratioFieldValue = (Ratio)
                        ReflectionHelper.getFieldValue(e.getValue(), "ratio");
                final int sizeFieldValue = (int)
                        ReflectionHelper.getFieldValue(e.getValue(), "size");

                table.row(e.getKey(),
                        ratioFieldValue.getPercentage(),
                        "" + sizeFieldValue);
            }
            table.hr('-');
        }
        System.out.println(table.toString());
    }

    private List<Map<CharSequence, Runnable>> getExecutedTests(
            IndexedArrayMap<String,Runnable> tests,
            LinkedTree<String,Object> sequence,
            Object... results) {
        StatsProducerMock<Runnable> statsProducer =
                new StatsProducerMock<Runnable>(results)
                .evaluator((CharSequence testName, Runnable test) -> test);

        SequencedTestProducer sequencedTestProducer =
                new SequencedTestProducer(sequence);

        sequencedTestProducer.instrument(statsProducer);

        for (Entry<String,Runnable> entry : tests) {
            sequencedTestProducer.addTest(entry.getKey(), entry.getValue());
        }

        sequencedTestProducer.execute();
        return statsProducer.getEvaluatedTree();
    }

    public static void main(final String[] args) {
        SequencedTestProducerTest test = new SequencedTestProducerTest();
        test.printout = true;
        test.shouldSubstituteParameter();
    }
}
