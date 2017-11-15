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

        if (Verbosity.OUTPUT_ONLY_RESULTS.isLessThanOrEqual(verbosity)) {

            newline();

            printResultTitle(configuration);

            println(configuration.toString());

            newline();

            mixedResult.appendResultsAndAssertionsTo(getAppendable());

            boolean failedAssertion = mixedResult.isSomeAssertionFailed();
            if (failedAssertion) {
                mixedResult.appendFailedAssertionsTo(getAppendable());
            }

            newline();
            println("Performance test total time: " + elapsed.toString());

            if (mixedResult.isSomeAssertionFailed()) {
                StringBuilder buf = new StringBuilder();
                mixedResult.appendFailedAssertionsTo(buf);
                println(buf);
                new AlertPlayer(playerConfig).onFailure();
                if (throwsExceptionOnFailure) {
                    throw new AssertionError(buf);
                }
            } else {
                new AlertPlayer(playerConfig).onSuccess();
            }

        }
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
