package com.fillumina.performance.template;

import com.fillumina.performance.executor.generator.Verbosity;
import com.fillumina.performance.executor.progression.SampleProgressionStatus;
import com.fillumina.performance.executor.progression.SampleProgressionStatusListener;
import com.fillumina.performance.executor.progression.StatsProgressionStatusListener;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.util.StopWatch;
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
    public void acceptStatsProgressionStatus(TName name,
            Collection<? extends Stats<?>> stats, String statusMessage) {
    }

    @Override
    public void acceptSampleProgressionStatus(SampleProgressionStatus status) {
        if (!Verbosity.FULL_OUTPUT.equals(verbosity)) {
            return;
        }
        StringBuilder buf = new StringBuilder();
        long estimated = 0;
        int sample = status.getExecutedSamples();
        int totalSamples = status.getTotalSamples();
        TName testName = status.getLastStats().getStats().getName();

        if (sample > 0) {
            estimated = (stopWatch.stop() / sample) * (totalSamples - sample);
        } else {
            stopWatch.start();
            buf
                    .append("Evaluating memory ")
                    .append(memTestType)
                    .append(" by '")
                    .append(testName.toString())
                    .append("' :")
                    .append(System.lineSeparator());
        }
        String totalSamplesStr = Integer.toString(totalSamples);
        String sampleStr = Integer.toString(sample + 1);
        String etc;
        if (estimated == 0) {
            etc = " --";
        } else {
            etc = IntervalUnit.UNITS.toString(estimated, 0);
        }
        buf.append(TableFormatter.repeat(' ',
                totalSamplesStr.length() - sampleStr.length()))
                .append(sampleStr).append(" / ")
                .append(totalSamplesStr)
                .append(" ETC=") // Estimated Time to Complete
                .append(TableFormatter.padToLengthAfter(8, etc))
                .append(testName.getLastName())
                .append("' = ")
                .append(status.getSample().values().iterator().next().toString())
                .append(" bytes");
        if (sample + 1 == totalSamples) {
            buf.append(System.lineSeparator());
        }
        System.out.println(buf.toString());
    }

}
