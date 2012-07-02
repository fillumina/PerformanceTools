package com.fillumina.performance;

import com.fillumina.performance.assertion.AssertPerformance;
import com.fillumina.performance.assertion.AssertPerformancesSuite;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.Test;

/**
 *
 * @author fra
 */
public class SingleThreadedMapPerformanceTest {
    private static final int LOOPS = 10_000_000;
    private static final int MAX_CAPACITY = 128;

    private PerformanceConsumer presenter = null;

    public static void main(final String[] args) {
        final SingleThreadedMapPerformanceTest test =
                new SingleThreadedMapPerformanceTest();
        test.presenter = new StringTablePresenter();
        test.execute(LOOPS, MAX_CAPACITY);
    }

    @Test
    public void performanceTest() {
        presenter = createPerformanceSuite();

        execute(LOOPS, MAX_CAPACITY);
    }

    private AssertPerformancesSuite createPerformanceSuite() {
        final AssertPerformancesSuite ps = new AssertPerformancesSuite();
        ps.addAssertionForTest("SEQUENTIAL WRITE")
                .assertTest("ConcurrentHashMap").slowerThan("HashMap");
        return ps;
    }

    public void execute(final int loops, final int maxCapacity) {
        final PerformanceSuite<Map<Integer,String>> suite =
                createSingleThreadPerformanceSuite(maxCapacity);

        suite.setPerformanceConsumer(presenter);

        executeTests(suite, loops, maxCapacity);
    }

    private void executeTests(final PerformanceSuite<Map<Integer, String>> suite,
            final int loops, final int maxCapacity) {
        suite.execute("SEQUENTIAL READ", loops, new FilledMapTest(maxCapacity) {

            @Override
            public void call(Map<Integer, String> map, int i) {
                map.put(i % MAX_CAPACITY, "xyz");
            }
        }).use(new AssertPerformance())
                .assertTest("SynchronizedHashMap").slowerThan("HashMap");

        suite.execute("SEQUENTIAL WRITE", loops, new MapTest(maxCapacity) {

            @Override
            public void call(Map<Integer, String> map, int i) {
                map.put(i % MAX_CAPACITY, "xyz");
            }
        });

        suite.execute("RANDOM READ", loops, new FilledMapTest(maxCapacity) {
            final Random rnd = new Random();

            @Override
            public void call(Map<Integer, String> map, int i) {
                map.get(rnd.nextInt(MAX_CAPACITY));
            }
        });

        suite.execute("RANDOM WRITE", loops,
                new ParametrizedRunnable<Map<Integer, String>>() {
            final Random rnd = new Random();

            @Override
            public void call(Map<Integer, String> map) {
                map.put(rnd.nextInt(MAX_CAPACITY), "xyz");
            }
        });
    }

    private PerformanceSuite<Map<Integer,String>>
            createSingleThreadPerformanceSuite(final int maxCapacity) {
        final PerformanceSuite<Map<Integer,String>> suite =
                new PerformanceSuite<>(PerformanceTimerBuilder.createSingleThread());

        suite.addObjectToTest("HashMap",
                new HashMap<Integer, String>(maxCapacity));

        suite.addObjectToTest("TreeMap",
                new TreeMap<Integer, String>());

        suite.addObjectToTest("LinkedHashMap",
                new LinkedHashMap<Integer, String>(maxCapacity));

        suite.addObjectToTest("SynchronizedLinkedHashMap",
                Collections.synchronizedMap(
                    new LinkedHashMap<Integer, String>(maxCapacity)));

        suite.addObjectToTest("ConcurrentHashMap",
                new ConcurrentHashMap<Integer, String>(maxCapacity));

        suite.addObjectToTest("SynchronizedHashMap",
                Collections.synchronizedMap(new HashMap<Integer, String>(maxCapacity)));

        return suite;
    }

    private static abstract class MapTest
            extends ParametrizedRunnable<Map<Integer, String>> {
        final int maxCapacity;
        int i=0;

        public MapTest(int maxCapacity) {
            this.maxCapacity = maxCapacity;
        }

        @Override
        public void call(final Map<Integer, String> param) {
            call(param, i % maxCapacity);
        }

        public abstract void call(final Map<Integer, String> param, final int i);
    }

    private static abstract class FilledMapTest extends MapTest {

        public FilledMapTest(int maxCapacity) {
            super(maxCapacity);
        }

        @Override
        public void setUp(final Map<Integer, String> map) {
            MapPerformanceApp.fillUpMap(map, maxCapacity);
        }
    }

}
