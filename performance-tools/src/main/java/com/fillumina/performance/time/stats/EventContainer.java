package com.fillumina.performance.time.stats;

import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.AverageTimeUnit;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.ThroughputUnit;
import com.fillumina.performance.util.unit.Unit;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class EventContainer {

    private final Event[] events;

    public EventContainer(Event... events) {
        this.events = events;
    }

    public void reset() {
        for (Event e : events) {
            e.reset();
        }
    }

    public List<Event> getEvents() {
        return Arrays.asList(events);
    }

    public void appendTo(Appendable appendable) {
        double[] means = new double[events.length];
        double[] throughput = new double[events.length];
        int index = 0;
        for (Event e : events) {
            final double mean = e.getMeasure().getMean();
            means[index] = mean;
            throughput[index] = 1E9/mean;
            index++;
        }
        Unit<?> meanUnit = AverageTimeUnit.UNITS
                .calculateAppropriatedUnitFrom(means);
        Unit<?> throughputUnit = ThroughputUnit.UNITS
                .calculateAppropriatedUnitFrom(throughput);

        TableFormatter table = new TableFormatter();
        table
                .cell("idx")
                .cell("name")
                .cell("count")
                .cell("speed").span(4)
                .cell("throughput").span(4)
                .endl();
        for (int i=0; i<events.length; i++) {
            Event e = events[i];
            DimensionalMeasure m = e.getMeasure();
            double mean = m.getMean();
            double moe = m.getMarginOfError(Ratio.P_999);
            table
                    .cell(i)
                    .cell(e.getName())
                    .cell(String.format("%,d",m.getCount()))
                    .cell(toString(meanUnit.convertFromBase(mean)))
                    .cell("+/-")
                    .cell(toString(meanUnit.convertFromBase(moe)))
                    .cell(meanUnit.toString(), "/op   ")
                    .cell(toString(throughputUnit.convertFromBase(1E9/mean)))
                    .cell("+/-")
                    .cell(toString(throughputUnit.convertFromBase(1E9/moe)))
                    .cell(throughputUnit.toString() + "op/s   ");
            table.endl();
        }
        table.appendToCatchingIOException(appendable);
    }

    private static String toString(double d) {
        return String.format("%,.3f", d);
    }

    public EventContainer print() {
        appendTo(System.out);
        return this;
    }

    public EventContainer printIf(boolean condition) {
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
