package com.fillumina.performance.template;

import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.executor.sample.SampleValue;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.producer.SampleProgressionStatus;
import com.fillumina.performance.executor.stats.producer.SampleProgressionStatusListener;
import com.fillumina.performance.executor.stats.producer.StatsProgressionStatus;
import com.fillumina.performance.executor.stats.producer.StatsProgressionStatusListener;
import com.fillumina.performance.time.stats.strgen.TimeStatsStringGeneratorSelector;
import com.fillumina.performance.util.LinearEtaEstimator;
import com.fillumina.performance.util.StringGenerator;
import com.fillumina.performance.util.formatter.CsvFormatter;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.pathname.PathName;
import com.fillumina.performance.util.unit.IntervalUnit;
import com.fillumina.performance.util.unit.Quantity;
import java.util.Collection;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ConsoleTimeProgressionListener
        implements
            SampleProgressionStatusListener,
            StatsProgressionStatusListener {

    private final Verbosity verbosity;
    private final StringGenerator<Stats> stringGenerator;
    private final LinearEtaEstimator eta = new LinearEtaEstimator();

    public ConsoleTimeProgressionListener(Verbosity verbosity,
            Ratio confidence) {
        this.verbosity = verbosity;
        this.stringGenerator = new TimeStatsStringGeneratorSelector(confidence);
    }

    @Override
    public void acceptSampleProgressionStatus(SampleProgressionStatus status) {
        if (Verbosity.FULL_OUTPUT.isGreaterThan(verbosity)) {
            return;
        }
        StringBuilder buf = new StringBuilder();
        Quantity<IntervalUnit> etaQuantity = LinearEtaEstimator.ZERO;
        int sample = status.getExecutedSamples();
        if (sample == 1 || sample == status.getTotalSamples()) {
            eta.start();
        } else if (sample > 1) {
            etaQuantity = eta.getEta(status.getError());
        }
        String totalSamplesStr = Integer.toString(status.getTotalSamples());
        String sampleStr = Integer.toString(sample);
        String etc;
        if (etaQuantity == LinearEtaEstimator.ZERO) {
            etc = " --";
        } else {
            etc = etaQuantity.toPrettyString(1);
        }
        etc = TableFormatter.padToLengthBefore(13, etc);
        buf.append(TableFormatter.repeat(' ',
                totalSamplesStr.length() - sampleStr.length()))
                .append(sampleStr).append(" / ")
                .append(totalSamplesStr)
                .append(" ETA=") // Estimated Time to Complete
                .append(etc)
                .append(" \titerations= ");

        CsvFormatter cf = new CsvFormatter();
        Sample timeSample = status.getSamples().values().iterator().next();
        for (SampleValue sv : timeSample.getValuesMap().values()) {
            cf.append(sv.toStringValue());
        }
        buf.append(cf.toString());

        buf.append("   ").append(status.getStatusMessage());

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
        PathName name = status.getName();
        Collection<? extends Stats> stats = status.getStats();
        String statusMessage = status.getStatusMessage();

        if (Verbosity.MEDIUM_OUTPUT.isGreaterThan(verbosity)) {
            return;
        }
        if (name != null && !name.isEmpty()) {
            System.out.println("");
            System.out.println(TableFormatter.title("TEST " + name, '-'));
        }
        if (statusMessage != null && !statusMessage.isEmpty()) {
            System.out.println("");
            System.out.println("iterator status: " + statusMessage);
        }
        if (!stats.isEmpty()) {
            System.out.println("");
            for (Stats t : stats) {
                System.out.println(stringGenerator.toString(t));
            }
        }
    }
}
