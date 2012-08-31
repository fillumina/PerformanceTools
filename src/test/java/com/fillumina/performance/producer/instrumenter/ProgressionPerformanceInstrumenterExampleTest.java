package com.fillumina.performance.producer.instrumenter;

import com.fillumina.performance.consumer.viewer.StringCsvViewer;
import org.junit.Test;

/**
 * This is an example of a performance test that starts being favorable to
 * one test and ends being favorable to the other. In this
 * case the JVM optimizations have nothing to do with that but it's easy
 * to understand how they could impact performances in
 * the same way.
 *
 * @author fra
 */
@Deprecated // uses real timer
public class ProgressionPerformanceInstrumenterExampleTest
        extends ProgressionPerformanceInstrumenterHelper {

    public static void main(final String[] args) {
        new ProgressionPerformanceInstrumenterExampleTest()
                .testWith(StringCsvViewer.CONSUMER,
                new ProgressionPerformanceInstrumenter.Builder()
                    .setBaseAndMagnitude(1000, 3)
                    .setSamplePerIterations(10));
    }

    @Test
    public void shoudPerformanceChangeWithIterations() {
        testWith(createAssertionForIterationsSuite(),
                new ProgressionPerformanceInstrumenter.Builder()
                    .setBaseAndMagnitude(1000, 3)
                    .setSamplePerIterations(10));
    }
}
