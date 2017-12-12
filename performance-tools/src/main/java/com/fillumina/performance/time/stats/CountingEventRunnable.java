package com.fillumina.performance.time.stats;

import com.fillumina.performance.util.unit.IntervalUnit;
import com.fillumina.performance.util.unit.Quantity;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class CountingEventRunnable implements Runnable {
    private Quantity<IntervalUnit> interval;
    private EventContainer events;
    private Runnable runnable;

    public CountingEventRunnable setInterval(Quantity<IntervalUnit> interval) {
        this.interval = interval;
        return this;
    }

    public CountingEventRunnable setEvents(Event... events) {
        this.events = new EventContainer(events);
        return this;
    }

    public CountingEventRunnable setRunnable(Runnable runnable) {
        this.runnable = runnable;
        return this;
    }

    void reset() {
        events.reset();
    }

    List<Event> getEvents() {
        return events.getEvents();
    }

    public Quantity<IntervalUnit> getInterval() {
        return interval;
    }

    @Override
    public void run() {
        runnable.run();
    }
}
