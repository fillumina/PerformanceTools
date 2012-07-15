package com.fillumina.performance.producer.instrumenter;

import com.fillumina.performance.consumer.viewer.StringCsvViewer;
import org.junit.Test;

/**
 *
 * @author fra
 */
public class AutoProgressionPerformanceInstrumenterExampleTest
        extends ProgressionPerformanceInstrumenterHelper {

    public static void main(final String[] args) {
        new ProgressionPerformanceInstrumenterExampleTest()
                .testWith(StringCsvViewer.CONSUMER,
                new AutoProgressionPerformanceInstrumenter.Builder()
                    .setMaxStandardDeviation(0.4));
    }

    @Test
    public void shoudPerformanceChangeWithIterations() {
        testWith(createAssertionForIterationsSuite(),
                new AutoProgressionPerformanceInstrumenter.Builder()
                    .setMaxStandardDeviation(0.4));
    }
}
