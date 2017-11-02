package com.fillumina.performance.template;

import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.producer.SampleProgressionStatus;
import com.fillumina.performance.executor.stats.producer.SampleProgressionStatusListener;
import com.fillumina.performance.executor.stats.producer.StatsProgressionStatus;
import com.fillumina.performance.executor.stats.producer.StatsProgressionStatusListener;
import com.fillumina.performance.time.sample.AbstractTimeSample;
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

    public ConsoleSpeedProgressionListener(Verbosity verbosity,
            Ratio confidence) {
        this.verbosity = verbosity;
        this.stringGenerator =
                new TimeStatsStringGeneratorSelector<>(confidence);
    }

    @Override
    public void acceptSampleProgressionStatus(SampleProgressionStatus status) {
        if (Verbosity.FULL_OUTPUT.isGreaterThan(verbosity)) {
            return;
        }
        StringBuilder buf = new StringBuilder();
        long estimated = 0;
        final int sample = status.getExecutedSamples();
        AbstractTimeSample timeSample = (AbstractTimeSample)
                    status.getSamples().values().iterator().next();
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
            for (TimeSampleValue tsv : timeSample.getValuesMap().values()) {
                itTable
                        .cell(pos)
                        .cell("'" + tsv.getName().toString() + "'")
                        .cell(tsv.getIterations())
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
        for (TimeSampleValue tsv : timeSample.getValuesMap().values()) {
            cf.append(/*'\'', entry.getKey(), "' ",*/tsv.getTimeNs());
        }
        buf.append(cf.toString());
        switch (status.getTimeSpentCoolingCpuMs()) {
            case -1:
                // no check has been done
                break;
            case 0:
                buf.append("  CPU is cool");
                break;
            default:
                buf.append("  CPU cooled ")
                    .append(status.getTimeSpentCoolingCpuMs())
                    .append(" ms");
        }
        System.out.println(buf.toString());
    }

    @Override
    public void acceptStatsProgressionStatus(StatsProgressionStatus status) {
        TName name = status.getName();
        Collection<? extends Stats<?>> stats = status.getStats();
        String statusMessage = status.getStatusMessage();

        stopWatch.reset();
        if (Verbosity.MEDIUM_OUTPUT.isGreaterThan(verbosity)) {
            return;
        }
        if (name != null && !name.isEmpty()) {
            System.out.println("");
            System.out.println(TableFormatter.title("TEST " + name, '-'));
        }
        if (statusMessage != null) {
            System.out.println(statusMessage);
        }
        for (Stats<?> t : stats) {
            System.out.println(stringGenerator.toString((TimeStats)t));
        }
    }
}
