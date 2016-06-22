package com.fillumina.performance.examples.template;

import com.fillumina.performance.assertion.AssertParametrizedSequencePerformance;
import com.fillumina.performance.examples.template.SearchTypePerformanceTest.Searcher;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.speed.stats.AssertSpeed;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParametrizedSequenceTestable;
import com.fillumina.performance.suite.SequenceContainer;
import com.fillumina.performance.template.TestConfigurator;
import com.fillumina.performance.util.junit.JUnitParametrizedSequencePerformanceTemplate;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import static org.junit.Assert.*;

/**
 * It proves that linear search is faster than binary search for small set
 * and slower for bigger sets.
 *
 * @author Francesco Illuminati
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
    public void config(TestConfigurator configuration) {
    }

    @Override
    public void addParameters(final ParameterContainer<Searcher> parameters) {
        parameters.addParameter("linear", new LinearSearcher())
                .addParameter("binary", new BinarySearcher());

    }

    @Override
    public void addSequence(final SequenceContainer<String[]> sequences) {
        final String[] locales = Locale.getISOCountries();
        Arrays.sort(locales);
        sequences.setSequenceItem("10", Arrays.copyOf(locales, 10));
        sequences.setSequenceItem("30", Arrays.copyOf(locales, 30));
    }

    @Override
    public void addAssertions(
            AssertParametrizedSequencePerformance<Void, SpeedStats> assertion) {
        assertion
                .forSequence("10").forAllParams(
                    AssertSpeed.withTolerance(5)
                    .assertOrder("linear").lessThan("binary"))
                    .endParams()

                .forSequence("30").forAllParams(
                    AssertSpeed.withTolerance(5)
                    .assertOrder("binary").lessThan("linear"))
                    .endParams();
    }

    @Override
    public void addTests(
            TestContainer<ParametrizedSequenceTestable<Searcher, String[]>> tests) {
        tests.addTest("test", new ParametrizedSequenceTestable<Searcher, String[]>() {
            final Random rnd = new Random(System.currentTimeMillis());

            @Override
            public Object test(final Searcher param, final String[] sequence) {
                final int pos = rnd.nextInt(sequence.length);
                final int result = param.indexOf(sequence, sequence[pos]);
                assertEquals(pos, result);
                return null;
            }
        });
    }
}
