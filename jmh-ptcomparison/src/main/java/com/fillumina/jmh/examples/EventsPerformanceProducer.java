package com.fillumina.jmh.examples;

import com.fillumina.jmh.examples.EventCounter.Event;
import com.fillumina.performance.infrastructure.AbstractPerformanceProducer;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.speed.stats.SingleSpeedStats;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MultiMeasure;
import com.fillumina.performance.util.unit.DimensionalOnlineMeasure;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class EventsPerformanceProducer
        extends AbstractPerformanceProducer
                    <EventsPerformanceProducer, SpeedStats, Runnable> {

    @Override
    public PHolder<SpeedStats> execute() {
        assertTestsPresent();
        PHolder<SpeedStats> result = null;
        PHolder.Builder<SpeedStats> builder = PHolder.experiment(getName());
        for (Map.Entry<TName,Runnable> e : getTests()) {
            TName name = e.getKey();
            Runnable test = e.getValue();
            if (test instanceof CountingEventTest) {
                SpeedStats stats = execute(name, (CountingEventTest)test);
                result = new PHolder<>(stats);
                builder.addSubExperiment(result);
            } else {
                throw new IllegalStateException("cannot manage tests different than " +
                        CountingEventTest.class.getSimpleName() + ", was: " +
                        test.getClass().getCanonicalName());
            }
        }
//        if (getTests().size() > 1) {
            return builder.build();
//        } else {
//            return result;
//        }
    }

    private static SpeedStats execute(
            TName testName,
            CountingEventTest test) {
        long interval = test.getInterval();
        long iterations = 0;
        long finishTime = System.nanoTime() + interval;
        long now;
        test.reset();
        do {
            test.run();
            iterations++;
            now = System.nanoTime();
        } while (now < finishTime);
        long elapsedTime = now - finishTime + interval;

        return createStats(testName, test, iterations, elapsedTime);
    }

    private static SpeedStats createStats(TName testName,
            CountingEventTest test,
            long iterations,
            long elapsed) {
        List<Event> events = test.getEvents();
        Measure[] measures = new Measure[events.size() + 1];
        LinkedHashMap<TName, SingleSpeedStats> map = new LinkedHashMap<>();
        int index = 0;
        for (Event e : events) {
            measures[index] = e.getMeasure();
            TName tname = testName.append(e.getName());
            SingleSpeedStats s = new SingleSpeedStats(
                    tname,
                    e.getMeasure(),
                    iterations,
                    1, 1,
                    elapsed);
            map.put(tname, s);
            index++;
        }
        DimensionalOnlineMeasure total =
                new DimensionalOnlineMeasure(1.0 * elapsed / iterations);
        measures[index] = total;
        TName totalName = testName.append("total");
        map.put(totalName,
                new SingleSpeedStats(totalName, total, iterations, 1, 1, elapsed));
        MultiMeasure mm = MultiMeasure.createFrom(measures);
        return new SpeedStats(mm, map);
    }
}
