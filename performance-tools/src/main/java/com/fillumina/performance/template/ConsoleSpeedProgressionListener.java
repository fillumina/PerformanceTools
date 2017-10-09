package com.fillumina.performance.template;

import com.fillumina.performance.executor.generator.Verbosity;
import com.fillumina.performance.executor.progression.SampleProgressionStatus;
import com.fillumina.performance.executor.progression.SampleProgressionStatusListener;
import com.fillumina.performance.executor.progression.StatsProgressionStatusListener;
import com.fillumina.performance.time.sample.TimeSampleValue;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.time.stats.strgen.TimeStatsStringGeneratorSelector;
import com.fillumina.performance.util.StopWatch;
import com.fillumina.performance.util.StringGenerator;
import com.fillumina.performance.util.formatter.CsvFormatter;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.unit.IntervalUnit;
import java.util.Collection;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ConsoleSpeedProgressionListener
        implements
            SampleProgressionStatusListener,
            StatsProgressionStatusListener {

    private final Verbosity verbosity;
    private final StringGenerator<TimeStats> stringGenerator;
    private final StopWatch stopWatch = new StopWatch();

    public ConsoleSpeedProgressionListener(Verbosity verbosity) {
        this(verbosity, Ratio.P_95);
    }

    public ConsoleSpeedProgressionListener(Verbosity verbosity,
            Ratio confidence) {
        this.verbosity = verbosity;
        this.stringGenerator =
                new TimeStatsStringGeneratorSelector(confidence);
    }

    @Override
    public void acceptSampleProgressionStatus(SampleProgressionStatus status) {
        if (!Verbosity.FULL_OUTPUT.equals(verbosity)) {
            return;
        }
        StringBuilder buf = new StringBuilder();
        long estimated = 0;
        final int sample = status.getExecutedSamples();
        if (sample > 1) {
            estimated =
                    (stopWatch.stop() / sample) *
                    (status.getTotalSamples() - sample);
        } else {
            stopWatch.start();
            buf.append("ITERATIONS PER SAMPLE:")
                    .append(System.lineSeparator());
            TableFormatter itTable = new TableFormatter();
            itTable.cell("idx").cell("name").cell("iterations").endl();
            int pos = 0;
            for (Map.Entry<TName, TimeSampleValue> entry :
                    status.getAverageTimeSample().getValuesMap()) {
                itTable
                        .cell(pos)
                        .cell("'" + entry.getKey().toString() + "'")
                        .cell(entry.getValue().getIterations())
                        .endl();
                pos++;
            }
            buf.append(itTable.toString());
            buf.append(System.lineSeparator());
        }
        String totalSamplesStr = Integer.toString(status.getTotalSamples());
        String sampleStr = Integer.toString(sample);
        String etc;
        if (estimated == 0) {
            etc = " --";
        } else {
            etc = IntervalUnit.UNITS.toPrettyString(estimated, 2);
        }
        etc = TableFormatter.padToLengthBefore(13, etc);
        buf.append(TableFormatter.repeat(' ',
                totalSamplesStr.length() - sampleStr.length()))
                .append(sampleStr).append(" / ")
                .append(totalSamplesStr)
                .append(" ETC=") // Estimated Time to Complete
                .append(etc)
                .append(" \ttime(ns)= ");

        CsvFormatter cf = new CsvFormatter();
        for (Map.Entry<TName, TimeSampleValue> entry :
                status.getSample().entrySet()) {
            cf.append(/*'\'', entry.getKey(), "' ",*/entry.getValue().getTimeNs());
        }
        buf.append(cf.toString());
        switch (status.getTimeSpentCoolingCpuMs()) {
            case -1:
                // no check done
                break;
            case 0:
                buf.append("  CPU cool");
                break;
            default:
                buf.append("  CPU cooled ")
                    .append(status.getTimeSpentCoolingCpuMs())
                    .append(" ms");
        }
        System.out.println(buf.toString());
    }

    @Override
    public void acceptStatsProgressionStatus(TName name,
            Collection<TimeStats> stats,
            String rejectionMessage) {
        stopWatch.reset();
        if (Verbosity.MEDIUM_OUTPUT.isGreaterThan(verbosity)) {
            return;
        }
        if (name != null && !name.isEmpty()) {
            System.out.println("");
            System.out.println(TableFormatter.title("TEST " + name, '-'));
        }
        if (rejectionMessage != null) {
            System.out.println(rejectionMessage);
        }
        for (TimeStats t : stats) {
            System.out.println(stringGenerator.toString(t));
        }
    }

    @Override
    public void acceptWarmupProgressionStatus(TName name, double speed) {
        String speedStr = String.format("%,.2f", speed);
        System.out.println("warming up '" + name + "'\t" + speedStr + " op/s");
    }
}
