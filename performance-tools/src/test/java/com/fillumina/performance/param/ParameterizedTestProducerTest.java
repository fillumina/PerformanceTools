package com.fillumina.performance.param;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.annotation.Param;
import com.fillumina.performance.mock.StatsProducerMock;
import com.fillumina.performance.util.ReflectionHelper;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.collection.LinkedTree;
import com.fillumina.performance.util.collection.Tree;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParameterizedTestProducerTest {
    private boolean printout;

    @Test
    public void shouldSubstituteSingleParameterInSingleClass() {
        final LinkedTree<String,Object> params =
                LinkedTree.<String,Object>builder()
                            .branch("list")
                                .leaf("linked", new LinkedList<>())
                                .leaf("array", new ArrayList<>())
                            .end()
                            .getRoot();

        final LinkedMap<String,Runnable> tests =
                LinkedMap.<String,Runnable>create(
                        "one", new Runnable() {
                                @Param
                                private List<String> list;

                                @Param
                                private int size;

                                @Override
                                public void run() {}
                            });

        LinkedTree<String, Runnable> exec = getExecutedTests(tests, params);

        if (printout) {
            printTree(exec);
        }

        assertEquals(1, exec.size());

        Tree<String, Runnable> one = exec.getTree("one");
        assertValues(one.get("linked"), LinkedList.class, 0);
        assertValues(one.get("array"), ArrayList.class, 0);
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

        final LinkedMap<String,Runnable> tests =
                LinkedMap.<String,Runnable>create(
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

        LinkedTree<String, Runnable> exec = getExecutedTests(tests, params);

        if (printout) {
            printTree(exec);
        }

        assertEquals(2, exec.size());

        Tree<String, Runnable> one = exec.getTree("one");
        assertValues(one.get(TName.join("linked","10")), LinkedList.class, 10);
        assertValues(one.get(TName.join("linked","100")), LinkedList.class, 100);
        assertValues(one.get(TName.join("array","10")), ArrayList.class, 10);
        assertValues(one.get(TName.join("array", "100")), ArrayList.class, 100);

        Tree<String, Runnable> two = exec.getTree("one");
        assertValues(two.get(TName.join("linked","10")), LinkedList.class, 10);
        assertValues(two.get(TName.join("linked","100")), LinkedList.class, 100);
        assertValues(two.get(TName.join("array","10")), ArrayList.class, 10);
        assertValues(two.get(TName.join("array", "100")), ArrayList.class, 100);
    }

    public static class InnerRunnable implements Runnable {
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

        final LinkedMap<String,Runnable> tests =
                LinkedMap.<String,Runnable>create(
                        "one", new InnerRunnable(),
                        "two", new InnerRunnable());

        LinkedTree<String, Runnable> exec = getExecutedTests(tests, params);

        if (printout) {
            printTree(exec);
        }

        assertEquals(2, exec.size());

        Tree<String, Runnable> one = exec.getTree("one");
        assertValues(one.get(TName.join("linked","10")), LinkedList.class, 10);
        assertValues(one.get(TName.join("linked","100")), LinkedList.class, 100);
        assertValues(one.get(TName.join("array","10")), ArrayList.class, 10);
        assertValues(one.get(TName.join("array", "100")), ArrayList.class, 100);

        Tree<String, Runnable> two = exec.getTree("one");
        assertValues(two.get(TName.join("linked","10")), LinkedList.class, 10);
        assertValues(two.get(TName.join("linked","100")), LinkedList.class, 100);
        assertValues(two.get(TName.join("array","10")), ArrayList.class, 10);
        assertValues(two.get(TName.join("array", "100")), ArrayList.class, 100);
    }

    private void assertValues(Runnable runnable, Class<?> clazz, int size) {
        final Object listFieldValue =
                ReflectionHelper.getFieldValue(runnable, "list");
        final Object sizeFieldValue =
                ReflectionHelper.getFieldValue(runnable, "size");

        assertEquals(clazz, listFieldValue.getClass());
        assertEquals(size, sizeFieldValue);
    }

    private void printTree(LinkedTree<String, Runnable> exec) {
        for (Tree<String, Runnable> entry : exec) {
            System.out.println("");
            System.out.println("test: " + entry.getKey());

            for (Entry<String, Runnable> e : entry) {
                final Object listFieldValue =
                        ReflectionHelper.getFieldValue(e.getValue(), "list");
                final Object sizeFieldValue =
                        ReflectionHelper.getFieldValue(e.getValue(), "size");

                System.out.println(e.getKey() +
                        "\t" + listFieldValue.getClass().getCanonicalName() +
                        "\t" + sizeFieldValue);
            }
        }
    }

    private LinkedTree<String, Runnable> getExecutedTests(
            LinkedMap<String,Runnable> tests,
            LinkedTree<String,Object> params) {
        StatsProducerMock<Assertable> statsProducer = new StatsProducerMock<>();
        ParameterizedTestProducer<Assertable> parameterizedTestProducer =
                new ParameterizedTestProducer<>(params);
        parameterizedTestProducer.instrument(statsProducer);
        for (Entry<String,Runnable> entry : tests) {
            parameterizedTestProducer.addTest(entry.getKey(), entry.getValue());
        }
        parameterizedTestProducer.execute();
        return  statsProducer.getExecutedTests();
    }

    public static void main(final String[] args) {
        final ParameterizedTestProducerTest test =
                new ParameterizedTestProducerTest();
        test.printout = true;
        test.shouldSubstituteParameter();
        test.shouldSubstituteParameterInExtendedClass();
    }
}
