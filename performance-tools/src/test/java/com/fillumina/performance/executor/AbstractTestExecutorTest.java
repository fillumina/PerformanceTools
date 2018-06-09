package com.fillumina.performance.executor;

import static com.fillumina.performance.executor.AbstractTestExecutor.UNNAMED_TEST_PREFIX;
import com.fillumina.performance.util.collection.IndexedHashMap;
import com.fillumina.performance.util.tname.TName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AbstractTestExecutorTest {

    private static class AbstractNamedTestExecutorImpl
            extends AbstractTestExecutor<
                AbstractNamedTestExecutorImpl, // self
                String,                        // message to consumers
                Integer,                       // test
                List<Integer>> {               // final result

        @Override
        public List<Integer> get() {
            IndexedHashMap<TName,Integer> map = getTests();
            List<Integer> list = new ArrayList<>(map.size());
            map.forEach((name, i) -> {
                dispatchToConsumers(name + "_" + i);
                list.add(i);
            });
            return list;
        }
    }

    private AbstractNamedTestExecutorImpl executor =
            new AbstractNamedTestExecutorImpl();

    @Test
    public void shouldAddConsumersIfTrue() {
        List<String> list = new ArrayList<>();
        executor.addConsumerIf(true, s -> list.add(s));
        executor.addTest("alpha", 10)
                .addTest("beta", 20)
                .addTest("gamma", 30)
                .execute();
        assertEquals(Arrays.asList("alpha_10", "beta_20", "gamma_30"), list);

    }

    @Test
    public void shouldNotAddConsumersIfFalse() {
        List<String> list = new ArrayList<>();
        executor.addConsumerIf(false, s -> list.add(s));
        executor.addTest("one", 1)
                .addTest("two", 2)
                .addTest("three", 3)
                .execute();
        assertTrue(list.isEmpty());

    }

    @Test
    public void shouldAddConsumers() {
        List<String> list = new ArrayList<>();
        executor.addConsumer(s -> list.add(s));
        executor.addTest("one", 1)
                .addTest("two", 2)
                .addTest("three", 3)
                .execute();
        assertEquals(Arrays.asList("one_1", "two_2", "three_3"), list);
    }

    @Test
    public void shouldRemoveConsumer() {
        List<String> list = new ArrayList<>();
        Consumer<String> adder = s -> list.add(s);
        executor.addConsumer(adder);
        executor.removeConsumer(adder);

        executor.addTest("one", 1)
                .addTest("two", 2)
                .addTest("three", 3)
                .execute();
        assertTrue(list.isEmpty());
    }

    @Test
    public void shouldClearConsumers() {
        List<String> list = new ArrayList<>();
        Consumer<String> adder = s -> list.add(s);
        executor.addConsumer(adder);
        executor.clearConsumers();

        executor.addTest("one", 1)
                .addTest("two", 2)
                .addTest("three", 3)
                .execute();
        assertTrue(list.isEmpty());
    }

    @Test
    public void shouldSetNameWithTName() {
        TName tname = TN.tname("one", "two", "three");
        executor.setName(tname);
        assertEquals(tname, executor.getName());
    }

    @Test
    public void shouldSetNameWithString() {
        String name = "one";
        executor.setName(name);
        assertEquals(TN.tname(name), executor.getName());
    }

    @Test
    public void shouldGetTests() {
        IndexedHashMap<TName, Integer> tests = executor
                .addTest("one", 1)
                .addTest("two", 2)
                .addTest("three", 3)
                .getTests();
        assertEquals(1, tests.get(TN.tname("one")), 0);
        assertEquals(2, tests.get(TN.tname("two")), 0);
        assertEquals(3, tests.get(TN.tname("three")), 0);
    }

    @Test
    public void shouldClearTests() {
        executor
                .addTest("one", 1)
                .addTest("two", 2)
                .addTest("three", 3)
                .clearTests();

        assertTrue(executor.getTests().isEmpty());
    }

    @Test
    public void shouldAddAnonymousTest() {
        executor.addTest(1)
                .addTest(2);

        assertEquals(1,
                executor.getTests().get(TN.tname(UNNAMED_TEST_PREFIX + "0")), 0);
        assertEquals(2,
                executor.getTests().get(TN.tname(UNNAMED_TEST_PREFIX + "1")), 0);
    }

    @Test
    public void shouldAddTestWithStringName() {
        executor.addTest("one", 111);
        assertEquals(111, executor.getTests().get(TN.tname("one")), 0);
    }

    @Test
    public void shouldAddTestWithTNameName() {
        TName tname = TN.tname("one","two");
        executor.addTest(tname, 111);
        assertEquals(111, executor.getTests().get(tname), 0);
    }

    @Test
    public void shouldAddTests() {
        Map<TName,Integer> tests = new HashMap<>();
        tests.put(TN.tname("one"), 1);
        tests.put(TN.tname("two"), 2);
        tests.put(TN.tname("three"), 3);

        executor.addTests(tests);

        assertEquals(1, tests.get(TN.tname("one")), 0);
        assertEquals(2, tests.get(TN.tname("two")), 0);
        assertEquals(3, tests.get(TN.tname("three")), 0);
    }

    @Test
    public void shouldIgnoreTestString() {
        executor.ignoreTest("one", 111);
        assertTrue(executor.getTests().isEmpty());
    }

    @Test
    public void shouldIgnoreTestTName() {
        TName tname = TN.tname("one","two");
        executor.ignoreTest(tname, 111);
        assertTrue(executor.getTests().isEmpty());
    }
}
