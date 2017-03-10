package com.fillumina.performance.template;

import com.fillumina.performance.speed.sample.IterationTime;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.progression.SampleProgressionStatus;
import com.fillumina.performance.speed.stats.progression.SampleProgressionStatusListener;
import com.fillumina.performance.speed.stats.progression.StatsProgressionStatusListener;
import com.fillumina.performance.util.StaticPath;
import com.fillumina.performance.util.StopWatch;
import com.fillumina.performance.util.formatter.CsvFormatter;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.unit.IntervalUnit;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class ConsoleSpeedProgressionListener
        implements SampleProgressionStatusListener,
        StatsProgressionStatusListener {

    private final int verbosity;
    private final StopWatch stopWatch = new StopWatch();

    public ConsoleSpeedProgressionListener(int verbosity) {
        this.verbosity = verbosity;
    }

    @Override
    public void acceptSampleProgressionStatus(SampleProgressionStatus status) {
        if (verbosity < 2) {
            return;
        }
        StringBuilder buf = new StringBuilder();
        long estimated = 0;
        final int sample = status.getSample();
        if (sample > 1) {
            estimated =
                    (stopWatch.stop() / sample) *
                    (status.getTotalSamples() - sample);
        } else {
            stopWatch.start();
            buf.append("ITERATIONS PER SAMPLE:")
                    .append(System.lineSeparator());
            TableFormatter itTable = new TableFormatter();
            int pos = 0;
            for (Map.Entry<String, IterationTime> entry :
                    status.getSpeedSample().getTimeMap().entrySet()) {
                itTable
                        .cell(pos)
                        .param(entry.getKey(),entry.getValue().getIterations());
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
            etc = IntervalUnit.getHelper().toString(estimated, 0);
        }
        buf.append(TableFormatter.repeate(' ',
                totalSamplesStr.length() - sampleStr.length()))
                .append(sampleStr).append(" / ")
                .append(totalSamplesStr)
                .append(" ETC=") // Estimated Time to Complete
                .append(etc)
                .append(" \ttime(ns)= ");
        CsvFormatter cf = new CsvFormatter();
        for (Map.Entry<String, IterationTime> entry :
                status.getSpeedSample().getTimeMap().entrySet()) {
            cf.append(/*'\'', entry.getKey(), "' ",*/entry.getValue().getTimeNs());
        }
        buf.append(cf.toString());
        System.out.println(buf.toString());
    }

    @Override
    public void acceptStatsProgressionStatus(StaticPath name, SpeedStats stats,
            String rejectionMessage) {
        stopWatch.reset();
        if (verbosity <= 1) {
            return;
        }
        if (!name.isEmpty()) {
            System.out.println("");
            System.out.println(TableFormatter.title("TEST " + name, '-'));
        }
        if (rejectionMessage != null) {
            System.out.println("REJECTED STATS: " + rejectionMessage);
        }
        System.out.println(stats.toString());
    }

}
