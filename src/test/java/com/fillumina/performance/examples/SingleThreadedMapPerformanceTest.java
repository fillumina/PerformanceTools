package com.fillumina.performance.examples;

import com.fillumina.performance.producer.sequence.ParametrizedRunnable;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.sequence.ParametrizedPerformanceSuite;
import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.consumer.viewer.StringTableViewer;
import com.fillumina.performance.consumer.assertion.AssertPerformancesSuite;
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

    public static void main(final String[] args) {
        final SingleThreadedMapPerformanceTest test =
                new SingleThreadedMapPerformanceTest();
        test.execute(LOOPS, MAX_CAPACITY, new StringTableViewer());
    }

    @Test
    public void performanceTest() {
        execute(LOOPS, MAX_CAPACITY, createAssertSuite());
    }

    private AssertPerformancesSuite createAssertSuite() {
        final AssertPerformancesSuite ps = new AssertPerformancesSuite();

        ps.forExecution("SEQUENTIAL READ")
                .assertTest("TreeMap").equalsTo("HashMap");

        ps.forExecution("SEQUENTIAL WRITE")
                .assertTest("TreeMap").equalsTo("HashMap");

        ps.forExecution("RANDOM READ")
                .assertTest("TreeMap").slowerThan("HashMap");

        ps.forExecution("RANDOM WRITE")
                .assertTest("TreeMap").slowerThan("HashMap");

        return ps;
    }

    public void execute(final int loops, final int maxCapacity,
            final PerformanceConsumer performanceConsumer) {
        final ParametrizedPerformanceSuite<Map<Integer,String>> suite =
                createSingleThreadPerformanceSuite(maxCapacity);

        suite.setPerformanceConsumer(performanceConsumer);

        executeTests(suite, loops, maxCapacity);
    }

    private void executeTests(final ParametrizedPerformanceSuite<Map<Integer, String>> suite,
            final int loops, final int maxCapacity) {
        suite.execute("SEQUENTIAL READ", loops, new FilledMapTest(maxCapacity) {

            @Override
            public void call(Map<Integer, String> map, int i) {
                map.put(i % MAX_CAPACITY, "xyz");
            }
        });

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

    private ParametrizedPerformanceSuite<Map<Integer,String>>
            createSingleThreadPerformanceSuite(final int maxCapacity) {
        final ParametrizedPerformanceSuite<Map<Integer,String>> suite =
                new ParametrizedPerformanceSuite<>(PerformanceTimerBuilder.createSingleThread());

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
