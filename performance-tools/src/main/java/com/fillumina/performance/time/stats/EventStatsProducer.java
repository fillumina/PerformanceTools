package com.fillumina.performance.time.stats;

import com.fillumina.performance.executor.stats.AbstractStatsProducer;
import com.fillumina.performance.executor.stats.MixedStatsHolder;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.StatsHolder;
import com.fillumina.performance.executor.stats.StatsType;
import com.fillumina.performance.executor.stats.StatsTypeImpl;
import com.fillumina.performance.util.collection.IndexedArrayMap;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.IntervalUnit;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class EventStatsProducer
        extends AbstractStatsProducer<EventStatsProducer> {

    public final static StatsType EVENT_TYPE = new StatsTypeImpl("Event");

    @Override
    public MixedStatsHolder get() {
        assertTestsPresent();
        StatsHolder result;
        StatsHolder.Builder builder =
                StatsHolder.builder(EVENT_TYPE, getName());
        for (Map.Entry<TName,Runnable> e : getTests()) {
            TName name = e.getKey();
            Runnable test = e.getValue();
            if (test instanceof CountingEventRunnable) {
                Stats stats = execute(name, (CountingEventRunnable)test);
                result = new StatsHolder(EVENT_TYPE, stats);
                builder.addSubExperiment(result);
            } else {
                throw new IllegalStateException("cannot manage tests different than " +
                        CountingEventRunnable.class.getSimpleName() + ", was: " +
                        test.getClass().getCanonicalName());
            }
        }
        return new MixedStatsHolder(builder.build());
    }

    private static Stats execute(
            TName testName,
            CountingEventRunnable test) {
        long intervalNs = (long) test.getInterval().as(IntervalUnit.NANOSECONDS);
        long iterations = 0;
        long finishTime = System.nanoTime() + intervalNs;
        long now;
        test.reset();
        do {
            test.run();
            iterations++;
            now = System.nanoTime();
        } while (now < finishTime);
        long elapsedTime = now - finishTime + intervalNs;

        return createStats(testName, test, iterations, elapsedTime);
    }

    private static Stats createStats(TName testName,
            CountingEventRunnable test,
            long iterations,
            long elapsed) {
        List<Event> events = test.getEvents();
        IndexedArrayMap<TName, DimensionalMeasure> map = new IndexedArrayMap<>();
        for (Event e : events) {
            TName tname = testName.append(e.getName());
            map.put(tname, e.getMeasure());
        }
        return new Stats(EVENT_TYPE, map);
    }
}
