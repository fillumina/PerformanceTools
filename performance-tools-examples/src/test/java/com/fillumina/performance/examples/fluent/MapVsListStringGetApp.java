package com.fillumina.performance.examples.fluent;

import com.fillumina.performance.consumer.assertion.AssertPerformance;
import com.fillumina.performance.producer.suite.ParametrizedSequenceRunnable;
import com.fillumina.performance.producer.suite.ParametrizedSequencePerformanceSuite;
import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.util.interval.IntegerInterval;
import com.fillumina.performance.util.junit.JUnitSimplePerformanceTemplate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.*;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MapVsListStringGetApp extends JUnitSimplePerformanceTemplate {

    public static void main(final String[] args) {
        new MapVsListStringGetApp().testWithOutput();
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

    @Override
    public void executePerformanceTest(final PerformanceConsumer iterationConsumer,
            final PerformanceConsumer resultConsumer) {

        PerformanceTimerFactory.createSingleThreaded()
            .addPerformanceConsumer(iterationConsumer)

            .instrumentedBy(AutoProgressionPerformanceInstrumenter.builder()
                    .setSamplesPerStep(10)
                    .setBaseIterations(1_000)
                    .setMaxStandardDeviation(1.6)
                    .setTimeout(30, TimeUnit.SECONDS)
                    .build())

            .instrumentedBy(new ParametrizedSequencePerformanceSuite<Gettable, Integer>())

            .addParameter("Map", new GettableMap())
            .addParameter("List", new GettableList())

            .setSequence(IntegerInterval.cycle()
               .from(5).to(50).step(5).iterator())

            .addPerformanceConsumer(resultConsumer)

            .executeTest("", new ParametrizedSequenceRunnable
                <MapVsListStringGetApp.Gettable, Integer>() {
                    private final Random rnd = new Random();

                    @Override
                    public void setUp(final Gettable gettable, final Integer size) {
                        gettable.init(size);
                    }

                    @Override
                    public Object sink(final Gettable gettable, final Integer size) {
                        final int index = rnd.nextInt(size);
                        final String str = String.valueOf(index);
                        assertNotNull(gettable.get(str));
                        return null;
                    }

                })
                .use(new AssertPerformance()
                    .assertTest("Map").fasterThan("List"));
    }
}
