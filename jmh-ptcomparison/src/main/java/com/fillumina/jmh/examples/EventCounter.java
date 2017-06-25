package com.fillumina.jmh.examples;

import com.fillumina.performance.time.sample.IterationTime;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.AbsoluteUnit;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.DimensionalOnlineMeasure;
import com.fillumina.performance.util.unit.AverageTimeUnit;
import com.fillumina.performance.util.unit.Unit;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class EventCounter {

    public interface Event {
        String getName();
        DimensionalMeasure getMeasure();
    }

    private static class EventImpl implements Event {
        private final String name;
        private final DimensionalOnlineMeasure measure =
                new DimensionalOnlineMeasure(AverageTimeUnit.NANOSECONDS);
        private long lastAccess;

        public EventImpl(String name, long now) {
            this.name = name;
            this.lastAccess = now;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public DimensionalMeasure getMeasure() {
            return measure;
        }

        void reset(long now) {
            lastAccess = now;
            measure.clear();
        }

        void ping() {
            long now = System.nanoTime();
            long elapsed = now - lastAccess;
            measure.add(elapsed);
            lastAccess = now;
        }

        @Override
        public String toString() {
            return name + " " + measure.toString();
        }
    }

    private final EventImpl[] events;
    private IterationTime iterationTime;


    public EventCounter(int size) {
        this.events = new EventImpl[size];
        long now = System.nanoTime();
        for (int index=0; index<size; index++) {
            this.events[index] = new EventImpl(String.valueOf(index), now);
        }
    }

    public EventCounter(String... names) {
        this.events = new EventImpl[names.length];
        int index = 0;
        long now = System.nanoTime();
        for (String n : names) {
            this.events[index] = new EventImpl(n, now);
            index++;
        }
    }

    public void reset() {
        long now = System.nanoTime();
        for (EventImpl e : events) {
            e.reset(now);
        }
    }

    public void event(int number) {
        events[number].ping();
    }

    List<Event> getEvents() {
        return Arrays.asList(events);
    }

    public EventCounter add(IterationTime iterationTime) {
        this.iterationTime = iterationTime;
        return this;
    }

    public void appendTo(Appendable appendable) {
        if (iterationTime != null) {
            try {
                appendable.append(
                        iterationTime.toString() + System.lineSeparator());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        double[] means = new double[events.length];
        double[] throughput = new double[events.length];
        int index = 0;
        for (EventImpl e : events) {
            final double mean = e.measure.getMean();
            means[index] = mean;
            throughput[index] = 1E9/mean;
            index++;
        }
        Unit meanUnit = AverageTimeUnit.UNITS
                .calculateAppropriatedUnitFrom(means);
        Unit throughputUnit = AbsoluteUnit.UNITS
                .calculateAppropriatedUnitFrom(throughput);

        TableFormatter table = new TableFormatter();
        table
                .cell("idx")
                .cell("name")
                .cell("count")
                .cell("speed").span(4)
                .cell("throughput").span(4)
                .cellIf(iterationTime != null, "ratio")
                .endl();
        for (int i=0; i<events.length; i++) {
            EventImpl e = events[i];
            DimensionalOnlineMeasure m = e.measure;
            double mean = m.getMean();
            double moe = m.getMarginOfError(Ratio.P_999);
            table
                    .cell(i)
                    .cell(e.name)
                    .cell(String.format("%,d",m.getCount()))
                    .cell(toString(meanUnit.convertFromBase(mean)))
                    .cell("+/-")
                    .cell(toString(meanUnit.convertFromBase(moe)))
                    .cell(meanUnit.toString(), "/op   ")
                    .cell(toString(throughputUnit.convertFromBase(1E9/mean)))
                    .cell("+/-")
                    .cell(toString(throughputUnit.convertFromBase(1E9/moe)))
                    .cell(throughputUnit.toString() + "op/s   ");
            if (iterationTime != null) {
                double ratio =
                        1.0 * m.getCount() / iterationTime.getIterations();
                table.cell(Ratio.decimal(ratio).toString());
            }
            table.endl();
        }
        table.appendToCatchingIOException(appendable);
    }

    private static String toString(double d) {
        return String.format("%,.3f", d);
    }

    public EventCounter print() {
        appendTo(System.out);
        return this;
    }

    public EventCounter printIf(boolean condition) {
        if (condition) {
            print();
        }
        return this;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        appendTo(buf);
        return buf.toString();
    }
}
