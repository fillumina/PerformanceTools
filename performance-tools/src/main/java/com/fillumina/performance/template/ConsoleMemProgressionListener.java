package com.fillumina.performance.template;

import com.fillumina.performance.mem.MemProgressionStatusListener;
import com.fillumina.performance.util.StopWatch;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.unit.IntervalUnit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class ConsoleMemProgressionListener
        implements MemProgressionStatusListener {

    private final StopWatch stopWatch = new StopWatch();
    private final int verbosity;
    private final String memTestType;

    public ConsoleMemProgressionListener(int verbosity, String memTestType) {
        this.verbosity = verbosity;
        this.memTestType = memTestType;
    }

    @Override
    public void accepts(TName fullTestName,
            int sample,
            int totalSamples,
            TName testName,
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
                    .append("Evaluating memory ")
                    .append(memTestType)
                    .append(" by '")
                    .append(fullTestName.toString())
                    .append("' :")
                    .append(System.lineSeparator());
        }
        String totalSamplesStr = Integer.toString(totalSamples);
        String sampleStr = Integer.toString(sample + 1);
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
                .append(" \t'")
                .append(testName)
                .append("' = ")
                .append(memoryUsed)
                .append(" bytes");
        if (sample + 1 == totalSamples) {
            buf.append(System.lineSeparator());
        }
        System.out.println(buf.toString());
    }

}
