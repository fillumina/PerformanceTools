package com.fillumina.performance.template;

import com.fillumina.performance.util.AppendableWrapper;
import com.fillumina.performance.util.StopWatch;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.formatter.TimeFormat;
import com.fillumina.performance.util.tname.TName;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MixedPrinter extends AppendableWrapper {

    private final Verbosity verbosity;

    public MixedPrinter(Appendable appendable, Verbosity verbosity) {
        super(appendable);
        this.verbosity = verbosity;
    }

    public void printConfiguration(
            MixedConfigurationBuilder<?>.Configuration configuration) {
        if (Verbosity.NO_OUTPUT.isLessThan(verbosity)) {
            println(TableFormatter.title("CONFIGURATION", '='));
            println(configuration.toString());
            newline();
            newline();
            println(TableFormatter.title("EXECUTION", '='));
        }
    }

    public void appendResults(
            MixedConfigurationBuilder<?>.Configuration configuration,
            MixedAssertionableResult<?> mixedStats,
            StopWatch watch) {
        if (Verbosity.OUTPUT_ONLY_RESULTS.isLessThanOrEqual(verbosity)) {

            newline();

            printResultTitle(configuration);

            println(configuration.toString());

            newline();

            mixedStats.appendResultsAndAssertionsTo(getAppendable());

            boolean failedAssertion = mixedStats.isSomeAssertionFailed();
            if (failedAssertion) {
                mixedStats.appendFailedAssertionsTo(getAppendable());
            }

            newline();
            println("Performance test total time: " +
                    TimeFormat.TEXT.formatNanoseconds(watch.stop(),
                            TimeFormat.Precision.MILLISECOND));
        }
    }

    private void printResultTitle(
            MixedConfigurationBuilder<?>.Configuration configuration) {
        TName testName = configuration.getTestConfig().getName();
        if (testName == null || testName.isEmpty()) {
            println(TableFormatter.title("RESULTS", '='));
        } else {
            println(TableFormatter.title("RESULTS OF '" +
                    testName + "'", '='));
        }
    }
}
