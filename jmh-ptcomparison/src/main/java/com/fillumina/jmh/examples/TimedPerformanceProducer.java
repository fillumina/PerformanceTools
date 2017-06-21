package com.fillumina.jmh.examples;

import com.fillumina.performance.infrastructure.AbstractPerformanceProducer;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.speed.sample.IterationTime;
import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.TimeSpan;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.unit.AbsoluteUnit;
import com.fillumina.performance.util.unit.IntervalUnit;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TimedPerformanceProducer
        extends AbstractPerformanceProducer
                    <TimedPerformanceProducer, SpeedSample, Runnable> {

    public static class ExecutorIterationTime implements IterationTime {
        private final TName name;
        private final long iterations;
        private final long elapsedNs;

        public ExecutorIterationTime(TName name,
                long iterations, long elapsedNs) {
            this.name = name;
            this.iterations = iterations;
            this.elapsedNs = elapsedNs;
        }

        public TName getName() {
            return name;
        }

        @Override
        public long getIterations() {
            return iterations;
        }

        @Override
        public long getTimeNs() {
            return elapsedNs;
        }

        public ExecutorIterationTime print() {
            System.out.println(toString());
            return this;
        }

        public ExecutorIterationTime appendTo(Appendable appendable) {
            try {
                appendable.append(toString());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            return this;
        }

        @Override
        public String toString() {
            return "iterations : " +
                    String.format(Locale.US, "%,d", iterations) +
                    " (" + AbsoluteUnit.getHelper().toPrettyString(iterations) +
                    ")" + System.lineSeparator() +
                    "elapsed    : " +
                    IntervalUnit.getHelper().toPrettyString(elapsedNs);
        }
    }

    private long intervalNs = TimeSpan.set().sec(1).asNanos();
    private Map<TName,EventCounter> map = new LinkedMap<>();

    public TimedPerformanceProducer setInterval(final long interval) {
        this.intervalNs = interval;
        return this;
    }

    public TimedPerformanceProducer setInterval(final TimeSpan interval) {
        this.intervalNs = interval.asNanos();
        return this;
    }

    @Override
    public PHolder<SpeedSample> execute() {
        assertTestsPresent();
        Map<TName, IterationTime> timeMap = new LinkedMap<>();
        for (Map.Entry<TName,Runnable> e : getTests()) {
            TName name = e.getKey();
            Runnable test = e.getValue();
            IterationTime it;
            if (test instanceof CountingEventTest) {
                it = execute(intervalNs, (CountingEventTest)test);
            } else {
                it = execute(intervalNs, test);
            }
            timeMap.put(name, it);
        }
        SpeedSample speedSample = new SpeedSample(timeMap);
        return new PHolder<>(speedSample);
    }

    public static ExecutorIterationTime execute(long ns, Runnable runnable) {
        long counter = 0;
        long finishTime = System.nanoTime() + ns;
        long now;
        do {
            runnable.run();
            counter++;
            now = System.nanoTime();
        } while (now < finishTime);
        return new ExecutorIterationTime(null, counter, now - finishTime + ns);
    }
}
