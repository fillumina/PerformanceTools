package com.fillumina.performance.examples;

import com.fillumina.performance.consumer.assertion.AssertPerformance;
import com.fillumina.performance.producer.suite.ParametrizedSequenceRunnable;
import com.fillumina.performance.producer.suite.ParametrizedSequencePerformanceSuite;
import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.util.interval.IntegerInterval;
import java.util.*;
import static org.junit.Assert.*;

/**
 *
 * @author fra
 */
public class MapVsListStringGetApp {
    public static final int ITERATIONS = 100_000;

    public static void main(final String[] args) {
        new MapVsListStringGetApp().usingSequencedPerformanceSuite();
    }

    private static interface Gettable {
        void init(int size);
        String get(String value);
    }

    private static class GettableMap implements Gettable {
        private Map<String, String> map;

        @Override
        public void init(int size) {
            map = new HashMap<>(size);
            for (int i=0; i<size; i++) {
                final String str = String.valueOf(i);
                map.put(str,str);
            }
        }

        @Override
        public String get(String value) {
            return map.get(value);
        }
    }

    private static class GettableList implements Gettable {
        private List<String> list;

        @Override
        public void init(int size) {
            list = new ArrayList<>(size);
            for (int i=0; i<size; i++) {
                final String str = String.valueOf(i);
                list.add(str);
            }
        }

        @Override
        public String get(String value) {
            for (String s : list) {
                if (value.equals(s)) {
                    return s;
                }
            }
            return null;
        }
    }

    public void usingSequencedPerformanceSuite() {
        final ParametrizedSequencePerformanceSuite<Gettable, Integer> suite =
                new ParametrizedSequencePerformanceSuite<>(
                    PerformanceTimerBuilder.createSingleThread());

        suite.addObjectToTest("Map", new GettableMap());
        suite.addObjectToTest("List", new GettableList());

        suite.addSequence(IntegerInterval.cycleFor()
                .start(5).end(50).step(5).iterator());

        //suite.setPerformanceConsumer(new StringTableViewer());

        suite.executeTest(ITERATIONS, new ParametrizedSequenceRunnable
                <MapVsListStringGetApp.Gettable, Integer>() {
            private final Random rnd = new Random();

            @Override
            public void setUp(final Gettable gettable, final Integer size) {
                gettable.init(size);
            }

            @Override
            public void call(final Gettable gettable, final Integer size) {
                final int index = rnd.nextInt(size);
                final String str = String.valueOf(index);
                assertNotNull(gettable.get(str));
            }

        }).addPerformanceConsumer(new AssertPerformance()
            .assertTest("Map").fasterThan("List"));
    }
}
