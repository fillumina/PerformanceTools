package com.fillumina.performance.template;

import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.executor.sample.SampleValue;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.producer.FixedSamplesAndIterationsStrategy;
import com.fillumina.performance.executor.stats.producer.SampleProgressionStatus;
import com.fillumina.performance.executor.stats.producer.SampleProgressionStatusListener;
import com.fillumina.performance.executor.stats.producer.StatsProgressionStatus;
import com.fillumina.performance.executor.stats.producer.StatsProgressionStatusListener;
import com.fillumina.performance.mem.stats.MemStatsTableStringGenerator;
import com.fillumina.performance.util.LinearEtaEstimator;
import com.fillumina.performance.util.formatter.CsvFormatter;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.pathname.PathName;
import com.fillumina.performance.util.unit.IntervalUnit;
import com.fillumina.performance.util.unit.MemUnit;
import com.fillumina.performance.util.unit.Quantity;
import java.util.Collection;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ConsoleMemProgressionListener
        implements
            SampleProgressionStatusListener,
            StatsProgressionStatusListener {

    private final LinearEtaEstimator eta = new LinearEtaEstimator();
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
        String statusMessage = status.getStatusMessage();
        PathName name = status.getName();
        Collection<? extends Stats> stats = status.getStats();

        if (FixedSamplesAndIterationsStrategy.WARMUP_STATUS.equals(statusMessage) ||
                Verbosity.MEDIUM_OUTPUT.isGreaterThan(verbosity)) {
            return;
        }
        if (name != null && !name.isEmpty()) {
            System.out.println("");
            System.out.println(TableFormatter.title("TEST " + name, '-'));
        }
        for (Stats t : stats) {
            System.out.println(
                    MemStatsTableStringGenerator.INSTANCE.toString(t));
        }
    }

    @Override
    public void acceptSampleProgressionStatus(SampleProgressionStatus status) {
        if (Verbosity.FULL_OUTPUT.isGreaterThan(verbosity)) {
            return;
        }
        StringBuilder buf = new StringBuilder();
        Quantity<IntervalUnit> error = LinearEtaEstimator.ZERO;
        int sample = status.getExecutedSamples();
        int totalSamples = status.getTotalSamples();
        PathName testName = status.getLastStats().getFirstStatsHolder().getPathName();

        if (sample == 1) {
            buf
                    .append(status.getStatusMessage())
                    .append(" memory ")
                    .append(memTestType)
                    .append(" by '")
                    .append(testName.toString())
                    .append("' :")
                    .append(System.lineSeparator());
            eta.start();
        } else {
            error = eta.getEta(status.getError());
        }
        String totalSamplesStr = Integer.toString(totalSamples);
        String sampleStr = Integer.toString(sample);
        String etc;
        if (error == LinearEtaEstimator.ZERO) {
            etc = " --";
        } else {
            etc = error.toPrettyString(1);
        }
        buf.append(TableFormatter.repeat(' ',
                totalSamplesStr.length() - sampleStr.length()))
                .append(sampleStr).append(" / ")
                .append(totalSamplesStr)
                .append(" ETC=") // Estimated Time to Complete
                .append(TableFormatter.padToLengthBefore(14, etc))
                .append(" \tbytes = ");
        CsvFormatter cf = new CsvFormatter();
        for (Sample s : status.getSamples().values()) {
            for (SampleValue sv : s.getValuesMap().values()) {
                cf.append(Math.round(sv.getQuantity().as(MemUnit.B)));
            }
        }
        buf.append(cf.toString());
        System.out.println(buf.toString());
    }

}
