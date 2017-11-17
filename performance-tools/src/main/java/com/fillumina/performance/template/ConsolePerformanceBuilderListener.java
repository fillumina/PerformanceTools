package com.fillumina.performance.template;

import com.fillumina.performance.util.AppendableWrapper;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.unit.IntervalUnit;
import com.fillumina.performance.util.unit.Quantity;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ConsolePerformanceBuilderListener extends AppendableWrapper
        implements PerformanceBuilderListener {

    private final Verbosity verbosity;
    private final AlertPlayer.Configuration playerConfig;
    private final boolean throwsExceptionOnFailure;

    public ConsolePerformanceBuilderListener(Appendable appendable,
            Verbosity verbosity,
            AlertPlayer.Configuration playerConfig,
            boolean throwsExceptionOnFailure) {
        super(appendable);
        this.verbosity = verbosity;
        this.playerConfig = playerConfig;
        this.throwsExceptionOnFailure = throwsExceptionOnFailure;
    }

    @Override
    public void onConfiguration(MixedConfiguration configuration) {
        if (Verbosity.NO_OUTPUT.isLessThan(verbosity)) {
            println(TableFormatter.title("CONFIGURATION", '='));
            println(configuration.toString());
            newline();
            newline();
            println(TableFormatter.title("EXECUTION", '='));
        }
    }

    @Override
    public void onResults(
            MixedConfiguration configuration,
            MixedAssertionableResult<?> mixedResult,
            Quantity<IntervalUnit> elapsed) {

        boolean someAssertionFailed = mixedResult.isSomeAssertionFailed();
        if (Verbosity.OUTPUT_ONLY_RESULTS.isLessThanOrEqual(verbosity)) {
            printResults(configuration, mixedResult);

            if (someAssertionFailed) {
                mixedResult.appendFailedAssertionsTo(getAppendable());
                new AlertPlayer(playerConfig).onFailure();
            } else {
                new AlertPlayer(playerConfig).onSuccess();
            }

            newline();
            println("Performance test total time: " + elapsed.toString());
        }

        if (someAssertionFailed && throwsExceptionOnFailure) {
            StringBuilder buf = new StringBuilder();
            mixedResult.appendFailedAssertionsTo(buf);
            throw new AssertionError(buf.toString());
        }
    }

    private void printResults(MixedConfiguration configuration,
            MixedAssertionableResult<?> mixedResult) {
        newline();
        printResultTitle(configuration);
        println(configuration.toString());
        newline();
        mixedResult.appendResultsAndAssertionsTo(getAppendable());
    }

    private void printResultTitle(MixedConfiguration configuration) {
        TName testName = configuration.getTestConfig().getName();
        if (testName == null || testName.isEmpty()) {
            println(TableFormatter.title("RESULTS", '='));
        } else {
            println(TableFormatter.title("RESULTS OF '" +
                    testName + "'", '='));
        }
    }
}
