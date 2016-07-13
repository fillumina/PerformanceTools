package com.fillumina.performance.template;

import com.fillumina.performance.mem.MemProgressionStatusListener;
import com.fillumina.performance.util.StopWatch;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.unit.IntervalUnit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ConsoleMemProgressionListener
        implements MemProgressionStatusListener {

    private final StopWatch stopWatch = new StopWatch();
    private final int verbosity;
    private final String memTestType;

    public ConsoleMemProgressionListener(int verbosity, String memTestType) {
        this.verbosity = verbosity;
        this.memTestType = memTestType;
    }

    @Override
    public void accepts(int sample,
            int totalSamples,
            String testName,
            long memoryUsed) {
        if (verbosity < 1) {
            return;
        }
        StringBuilder buf = new StringBuilder();
        long estimated = 0;
        if (sample > 0) {
            estimated = (stopWatch.stop() / sample) * (totalSamples - sample);
        } else {
            stopWatch.start();
            buf
                    .append(System.lineSeparator())
                    .append("Evaluating memory ")
                    .append(memTestType)
                    .append(" by ")
                    .append(testName)
                    .append(':')
                    .append(System.lineSeparator());
        }
        String totalSamplesStr = Integer.toString(totalSamples);
        String sampleStr = Integer.toString(sample);
        buf.append(TableFormatter.repeate(' ',
                totalSamplesStr.length() - sampleStr.length()))
                .append(sampleStr).append(" / ")
                .append(totalSamplesStr)
                .append("  ETC=") // Estimated Time to Complete
                .append(IntervalUnit.FORMATTER.toString(estimated, 0))
                .append("  '")
                .append(testName)
                .append("' = ")
                .append(memoryUsed)
                .append(" bytes");
        System.out.println(buf.toString());
    }

}
