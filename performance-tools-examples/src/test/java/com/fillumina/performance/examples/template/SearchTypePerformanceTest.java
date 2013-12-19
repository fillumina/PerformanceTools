package com.fillumina.performance.examples.template;

import com.fillumina.performance.consumer.assertion.PerformanceAssertion;
import com.fillumina.performance.consumer.assertion.SuiteExecutionAssertion;
import com.fillumina.performance.examples.template.SearchTypePerformanceTest.Searcher;
import com.fillumina.performance.producer.suite.ParameterContainer;
import com.fillumina.performance.producer.suite.ParametrizedSequenceRunnable;
import com.fillumina.performance.producer.suite.SequenceContainer;
import com.fillumina.performance.producer.suite.SequenceNominator;
import com.fillumina.performance.template.AssertionSuiteBuilder;
import com.fillumina.performance.template.ProgressionConfigurator;
import com.fillumina.performance.util.junit.JUnitParametrizedSequencePerformanceTemplate;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import static org.junit.Assert.*;

/**
 * It proves that linear search is faster than binary search for small set
 * and slower for bigger sets.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SearchTypePerformanceTest
        extends JUnitParametrizedSequencePerformanceTemplate<Searcher, String[]>{

    interface Searcher {
        int indexOf(final String[] strings, final String str);
    }

    static class LinearSearcher implements Searcher {

        @Override
        public int indexOf(final String[] strings, final String str) {
            for (int i=0,max=strings.length; i<max; i++) {
                if (strings[i].equals(str)) {
                    return i;
                }
            }
            throw new AssertionError();
        }
    }

    static class BinarySearcher implements Searcher {

        @Override
        public int indexOf(final String[] strings, final String str) {
            return Arrays.binarySearch(strings, str);
        }
    }

    public static void main(final String[] args) {
        new SearchTypePerformanceTest().executeWithOutput();
    }

    @Override
    public void init(final ProgressionConfigurator config) {
        config.setMaxStandardDeviation(2)
                .setPrintOutStdDeviation(false);
    }

    @Override
    public void addParameters(final ParameterContainer<Searcher> parameters) {
        parameters.addParameter("linear", new LinearSearcher())
                .addParameter("binary", new BinarySearcher());

    }

    @Override
    public void addSequence(final SequenceContainer<?, String[]> sequences) {
        final String[] locales = Locale.getISOCountries();
        Arrays.sort(locales);
        sequences.setSequence(Arrays.copyOf(locales, 10),
                Arrays.copyOf(locales, 30));

        sequences.setSequenceNominator(new SequenceNominator<String[]>() {

            @Override
            public String toString(final String[] sequenceItem) {
                return String.valueOf(sequenceItem.length);
            }
        });
    }

    @Override
    public void addIntermediateAssertions(final PerformanceAssertion assertion) {
        assertion.withPercentageTolerance(5F)
                .assertPercentageFor("linear").greaterThan(50);
    }

    @Override
    public void addAssertions(final AssertionSuiteBuilder assertionBuilder) {
        SuiteExecutionAssertion assertion = assertionBuilder.withTolerance(5F);

        assertion.forExecution("test-10")
            .assertTest("linear").fasterThan("binary");

        assertion.forExecution("test-30")
            .assertTest("binary").fasterThan("linear");
    }

    @Override
    protected ParametrizedSequenceRunnable<Searcher, String[]> getTest() {
        return new ParametrizedSequenceRunnable<Searcher, String[]>() {
            final Random rnd = new Random(System.currentTimeMillis());

            @Override
            public Object sink(final Searcher param, final String[] sequence) {
                final int pos = rnd.nextInt(sequence.length);
                final int result = param.indexOf(sequence, sequence[pos]);
                assertEquals(pos, result);
                return null;
            }
        };
    }
}
