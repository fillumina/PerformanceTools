package com.fillumina.jmh.examples.event;

import com.fillumina.jmh.examples.event.EventCounter.Event;
import com.fillumina.performance.util.TimeSpan;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class CountingEventRunnable implements Runnable {
    private long interval;
    private EventCounter counter;
    private CountingRunnable runnable;

    public CountingEventRunnable setInterval(TimeSpan interval) {
        this.interval = interval.asNanos();
        return this;
    }

    public CountingEventRunnable setEvents(int size) {
        counter = new EventCounter(size);
        return this;
    }

    public CountingEventRunnable setEvents(String... eventNames) {
        counter = new EventCounter(eventNames);
        return this;
    }

    public CountingEventRunnable setRunnable(CountingRunnable runnable) {
        this.runnable = runnable;
        return this;
    }

    void reset() {
        counter.reset();
    }

    List<Event> getEvents() {
        return counter.getEvents();
    }

    public long getInterval() {
        return interval;
    }

    @Override
    public void run() {
        runnable.run(counter);
    }
}
