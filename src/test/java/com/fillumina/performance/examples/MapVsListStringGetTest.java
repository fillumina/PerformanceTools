package com.fillumina.performance.examples;

import com.fillumina.performance.assertion.AssertPerformance;
import com.fillumina.performance.sequence.ParametrizedRunnable;
import com.fillumina.performance.sequence.PerformanceSuite;
import com.fillumina.performance.timer.PerformanceTimerBuilder;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author fra
 */
public class MapVsListStringGetTest {
    public static final int ITERATIONS = 100_000;

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

    @Test
    public void mapVsListTest() {

        final PerformanceSuite<Gettable> suite =
                new PerformanceSuite<>(PerformanceTimerBuilder.createSingleThread());

        suite.addObjectToTest("Map", new GettableMap());
        suite.addObjectToTest("List", new GettableList());

        for (int i=5; i<50; i+=5) {
            final int size = i;

            suite.execute(String.valueOf(i), ITERATIONS,
                    new ParametrizedRunnable<MapVsListStringGetTest.Gettable>() {
                Random rnd = new Random();

                @Override
                public void setUp(final Gettable gettable) {
                    gettable.init(size);
                }

                @Override
                public void call(final Gettable gettable) {
                    final int index = rnd.nextInt(size);
                    final String str = String.valueOf(index);
                    assertNotNull(gettable.get(str));
                }

            }).use(new AssertPerformance())
                .assertTest("Map").fasterThan("List");
        }
    }
}
