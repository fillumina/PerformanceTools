package com.fillumina.performance.template;

import com.fillumina.performance.executor.sample.AbstractSample;
import com.fillumina.performance.executor.sample.SampleValue;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.producer.SampleProgressionStatus;
import com.fillumina.performance.executor.stats.producer.SampleProgressionStatusListener;
import com.fillumina.performance.executor.stats.producer.StatsProgressionStatus;
import com.fillumina.performance.executor.stats.producer.StatsProgressionStatusListener;
import com.fillumina.performance.mem.sample.AbstractMemSample;
import com.fillumina.performance.mem.stats.MemStats;
import com.fillumina.performance.mem.stats.MemStatsTableStringGenerator;
import com.fillumina.performance.util.StopWatch;
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
public class ConsoleMemProgressionListener
        implements
            SampleProgressionStatusListener,
            StatsProgressionStatusListener {

    private final StopWatch stopWatch = new StopWatch();
    private final Verbosity verbosity;
    private final Ratio confidence;
    private final String memTestType;

    public ConsoleMemProgressionListener(
            Verbosity verbosity,
            Ratio confidence,
            String memTestType) {
        this.verbosity = verbosity;
        this.confidence = confidence;
        this.memTestType = memTestType;
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
            System.out.println(
                    MemStatsTableStringGenerator.INSTANCE.toString((MemStats)t));
        }
    }

    @Override
    public void acceptSampleProgressionStatus(SampleProgressionStatus status) {
        if (Verbosity.FULL_OUTPUT.isGreaterThan(verbosity)) {
            return;
        }
        StringBuilder buf = new StringBuilder();
        long estimated = 0;
        int sample = status.getExecutedSamples();
        int totalSamples = status.getTotalSamples();
        TName testName = status.getLastStats().getStats().getName();

        if (sample == 1) {
            buf
                    .append("Evaluating memory ")
                    .append(memTestType)
                    .append(" by '")
                    .append(testName.toString())
                    .append("' :")
                    .append(System.lineSeparator());
            stopWatch.start();
        } else {
            estimated = (stopWatch.stop() / sample) * (totalSamples - sample);
        }
        String totalSamplesStr = Integer.toString(totalSamples);
        String sampleStr = Integer.toString(sample);
        String etc;
        if (estimated == 0) {
            etc = " --";
        } else {
            etc = IntervalUnit.UNITS.toPrettyString(estimated, 2);
        }
        buf.append(TableFormatter.repeat(' ',
                totalSamplesStr.length() - sampleStr.length()))
                .append(sampleStr).append(" / ")
                .append(totalSamplesStr)
                .append(" ETC=") // Estimated Time to Complete
                .append(TableFormatter.padToLengthBefore(14, etc))
                .append(" \tbytes = ");
        CsvFormatter cf = new CsvFormatter();
        for (AbstractSample<?,?,?> s : status.getSamples().values()) {
            AbstractMemSample<?,?> ms = (AbstractMemSample) s;
            for (SampleValue sv : ms.getValuesMap().values()) {
                cf.append(Math.round(sv.getValue()));
            }
        }
        buf.append(cf.toString());
        System.out.println(buf.toString());
    }

}
