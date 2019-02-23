package com.fillumina.performance.executor.param;

import com.fillumina.performance.executor.PN;
import com.fillumina.performance.executor.annotation.Param;
import com.fillumina.performance.executor.stats.MixedStatsHolder;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.mock.MockStatsType;
import com.fillumina.performance.mock.NameStatsProducerMock;
import com.fillumina.performance.mock.StatsProducerMock;
import com.fillumina.performance.util.collection.IndexedHashMap;
import com.fillumina.performance.util.collection.LinkedTree;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.reflection.ReflectionHelper;
import com.fillumina.performance.util.pathname.PathName;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParameterizedTestProducerTest {
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

        ParameterizedTestProducer producer =
                new ParameterizedTestProducer(params);

        NameStatsProducerMock statsProducer = new NameStatsProducerMock();

        producer.instrument(statsProducer);

        producer.addTest("test",
                new Runnable() {
                    @Param private char param;
                    @Override public void run() {}
                });

        producer.setName("XYZ");

        MixedStatsHolder holder = producer.execute();
        Stats stats = holder
                .getStatsHolder(MockStatsType.INSTANCE)
                .getStatsAtPath("XYZ", "test");

        assertEquals(1.0,
                stats.getMeasure(PN.pname("XYZ", "test", "a")).getMean(), 0);
        assertEquals(2.0,
                stats.getMeasure(PN.pname("XYZ", "test", "b")).getMean(), 0);

        List<List<CharSequence>> tree = statsProducer.getTree();
        List<CharSequence> stats0 = tree.get(0);

        assertEquals(PN.pname("XYZ", "test", "a"), stats0.get(0));
        assertEquals(PN.pname("XYZ", "test", "b"), stats0.get(1));
    }

    @Test
    public void shouldAddPayloadToStats() {
        final LinkedTree<String,Object> params =
                LinkedTree.<String,Object>builder()
                            .branch("param1")
                                .leaf("a", 'a')
                                .leaf("b", 'b')
                            .end()
                            .branch("param2")
                                .leaf("1", 1)
                                .leaf("2", 2)
                            .end()
                            .getRoot();

        ParameterizedTestProducer producer =
                new ParameterizedTestProducer(params);

        NameStatsProducerMock statsProducer = new NameStatsProducerMock();

        producer.instrument(statsProducer);

        producer.addTest("test",
                new Runnable() {
                    @Param private char param1;
                    @Param private int param2;
                    @Override public void run() {}
                });

        MixedStatsHolder holder = producer.execute();
        Stats stats = holder.getStatsHolder(MockStatsType.INSTANCE)
                .getStatsAtPath("test");

        Map<PathName, Map<String,Option>> options =
                stats.<Map<PathName, Map<String,Option>>>getPayload(
                        ParameterizedTestProducer.PARAMETERS);

        assertOption(options, 'a', 1);
        assertOption(options, 'b', 1);
        assertOption(options, 'a', 2);
        assertOption(options, 'b', 2);

        try {
            assertOption(options, 'c', 666);
        } catch (NullPointerException e) {
            // all right
        }
    }

    private void assertOption(Map<PathName, Map<String,Option>> options, char a, int v) {
        Map<String,Option> optCont = options.get(PN.pname(String.valueOf(a), String.valueOf(v)));
        assertEquals(a, (char)optCont.get("param1").getOptionValue());
        assertEquals(v, (int)optCont.get("param2").getOptionValue());
    }

    @Test
    public void shouldSubstituteSingleParameterInSingleClass() {
        final LinkedTree<String,Object> params =
                LinkedTree.<String,Object>builder()
                            .branch("list")
                                .leaf("linked", new LinkedList<>())
                                .leaf("array", new ArrayList<>())
                            .end()
                            .getRoot();

        final IndexedHashMap<String,Runnable> tests =
                IndexedHashMap.<String,Runnable>create(
                        "one", new Runnable() {
                                @Param
                                private List<String> list;

                                @Param
                                private int size;

                                @Override
                                public void run() {}
                            });

        List<Map<CharSequence, Runnable>> exec = getExecutedTests(tests, params,
                PN.pname("one", "linked"), 10.0,
                PN.pname("one", "array"), 20.0,
                PN.pname("two", "linked"), 30.0,
                PN.pname("two", "array"), 40.0);

        if (printout) {
            printTree(exec);
        }

        assertEquals(1, exec.size());

        Map<CharSequence, Runnable> one = exec.get(0);
        assertValues(one.get(PN.pname("one", "linked")), LinkedList.class, 0);
        assertValues(one.get(PN.pname("one", "array")), ArrayList.class, 0);
    }

    @Test
    public void shouldSubstituteParameter() {
        final LinkedTree<String,Object> params =
                LinkedTree.<String,Object>builder()
                            .branch("list")
                                .leaf("linked", new LinkedList<>())
                                .leaf("array", new ArrayList<>())
                            .end()
                            .branch("size")
                                .leaf("10", 10)
                                .leaf("100", 100)
                            .end()
                            .getRoot();

        final IndexedHashMap<String,Runnable> tests =
                IndexedHashMap.<String,Runnable>create(
                        "one", new Runnable() {
                                @Param
                                private List<String> list;

                                @Param
                                private int size;

                                @Override
                                public void run() {}
                            },
                        "two", new Runnable() {
                                @Param
                                private List<String> list;

                                @Param
                                private int size;

                                @Override
                                public void run() {}
                            }
                );

        List<Map<CharSequence, Runnable>> exec = getExecutedTests(tests, params,
                PN.pname("one", "linked", "10"), 10.0,
                PN.pname("one", "linked", "100"), 100.0,
                PN.pname("one", "array", "10"), 20.0,
                PN.pname("one", "array", "100"), 200.0,
                PN.pname("two", "linked", "10"), 30.0,
                PN.pname("two", "linked", "100"), 300.0,
                PN.pname("two", "array", "10"), 40.0,
                PN.pname("two", "array", "100"), 400.0);

        if (printout) {
            printTree(exec);
        }

        assertEquals(2, exec.size());

        checkTree(exec);
    }

    public static class ListSizeRunnable implements Runnable {
        @Param
        private List<String> list;

        @Param
        private int size;

        @Override
        public void run() {}
    }

    @Test
    public void shouldSubstituteParameterInExtendedClass() {
        final LinkedTree<String,Object> params =
                LinkedTree.<String,Object>builder()
                            .branch("list")
                                .leaf("linked", new LinkedList<>())
                                .leaf("array", new ArrayList<>())
                            .end()
                            .branch("size")
                                .leaf("10", 10)
                                .leaf("100", 100)
                            .end()
                            .getRoot();

        final IndexedHashMap<String,Runnable> tests =
                IndexedHashMap.<String,Runnable>create("one", new ListSizeRunnable(),
                        "two", new ListSizeRunnable());

        List<Map<CharSequence, Runnable>> exec = getExecutedTests(tests, params,
                PN.pname("one", "linked", "10"), 10.0,
                PN.pname("one", "linked", "100"), 100.0,
                PN.pname("one", "array", "10"), 20.0,
                PN.pname("one", "array", "100"), 200.0,
                PN.pname("two", "linked", "10"), 30.0,
                PN.pname("two", "linked", "100"), 300.0,
                PN.pname("two", "array", "10"), 40.0,
                PN.pname("two", "array", "100"), 400.0);

        if (printout) {
            printTree(exec);
        }

        assertEquals(2, exec.size());


        checkTree(exec);
    }

    private void checkTree(List<Map<CharSequence, Runnable>> exec) {
        Map<CharSequence, Runnable> one = exec.get(0);
        assertValues(one.get(PN.pname("one", "linked", "10")),
                LinkedList.class, 10);
        assertValues(one.get(PN.pname("one", "linked", "100")),
                LinkedList.class, 100);
        assertValues(one.get(PN.pname("one", "array", "10")),
                ArrayList.class, 10);
        assertValues(one.get(PN.pname("one", "array", "100")),
                ArrayList.class, 100);

        Map<CharSequence, Runnable> two = exec.get(1);
        assertValues(two.get(PN.pname("two", "linked", "10")),
                LinkedList.class, 10);
        assertValues(two.get(PN.pname("two", "linked", "100")),
                LinkedList.class, 100);
        assertValues(two.get(PN.pname("two", "array", "10")),
                ArrayList.class, 10);
        assertValues(two.get(PN.pname("two", "array", "100")),
                ArrayList.class, 100);
    }

    private void assertValues(Runnable runnable, Class<?> clazz, int size) {
        final Object listFieldValue =
                ReflectionHelper.getFieldValue(runnable, "list");
        final Object sizeFieldValue =
                ReflectionHelper.getFieldValue(runnable, "size");

        assertEquals(clazz, listFieldValue.getClass());
        assertEquals(size, sizeFieldValue);
    }

    private void printTree(List<Map<CharSequence, Runnable>> exec) {
        TableFormatter table = new TableFormatter("   ");
        table.row("test", "list", "size").hr('=');
        for (Map<CharSequence, Runnable> map : exec) {

            map.entrySet().forEach(e -> {
                final List<?> listFieldValue = (List<?>)
                        ReflectionHelper.getFieldValue(e.getValue(), "list");
                final int sizeFieldValue = (int)
                        ReflectionHelper.getFieldValue(e.getValue(), "size");

                table.row(e.getKey(),
                        listFieldValue.getClass().getSimpleName(),
                        "" + sizeFieldValue);
            });
            table.hr('-');
        }
        System.out.println(table.toString());
    }

    private List<Map<CharSequence, Runnable>> getExecutedTests(
            IndexedHashMap<String,Runnable> tests,
            LinkedTree<String,Object> params,
            Object... results) {
        StatsProducerMock<Runnable> statsProducer =
                new StatsProducerMock<Runnable>(results)
                .evaluator((CharSequence testName, Runnable test) -> test);

        ParameterizedTestProducer parameterizedTestProducer =
                new ParameterizedTestProducer(params);

        parameterizedTestProducer.instrument(statsProducer);
        tests.forEach((String name, Runnable test) ->
            parameterizedTestProducer.addTest(name, test) );
        parameterizedTestProducer.execute();
        return statsProducer.getEvaluatedTree();
    }

    public static void main(final String[] args) {
        final ParameterizedTestProducerTest test =
                new ParameterizedTestProducerTest();
        test.printout = true;
        test.shouldSubstituteSingleParameterInSingleClass();
//        test.shouldSubstituteParameter();
//        test.shouldSubstituteParameterInExtendedClass();
    }
}
