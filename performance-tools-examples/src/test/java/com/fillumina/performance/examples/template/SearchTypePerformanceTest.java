package com.fillumina.performance.examples.template;

import com.fillumina.performance.examples.PrintOut;
import com.fillumina.performance.examples.template.SearchTypePerformanceTest.Searcher;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.speed.stats.AssertSpeed;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParameterizedSequenceTestable;
import com.fillumina.performance.suite.SequenceContainer;
import com.fillumina.performance.template.ParameterizedSequenceAssertion;
import com.fillumina.performance.template.TestConfiguration;
import com.fillumina.performance.util.junit.JUnitParameterizedSequencePerformanceTemplate;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * It proves that linear search is faster than binary search for small set
 * and slower for bigger sets.
 *
 * @author Francesco Illuminati
 */
public class SearchTypePerformanceTest
        extends JUnitParameterizedSequencePerformanceTemplate<Searcher, String[]>{

    private PrintOut printOut = new PrintOut();

    @Test
    public void executeTest() {
        if (printOut.isPrintOut()) {
            executeWithFullOutput();
        } else {
            executeWithoutOutput();
        }
    }

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
        final SearchTypePerformanceTest test = new SearchTypePerformanceTest();
        test.printOut = new PrintOut(true);
        test.executeWithFullOutput();
    }

    @Override
    public void config(TestConfiguration config) {
        config.speedTestOnly();
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
    public void addAssertions(ParameterizedSequenceAssertion assertion) {
        assertion.speed()
                .forSequenceValue("10").forAllTests(
                    AssertSpeed.withTolerance(5)
                    .assertOrder("linear").lessThan("binary"))
                    .endTests()

                .forSequenceValue("30").forAllTests(
                    AssertSpeed.withTolerance(5)
                    .assertOrder("binary").lessThan("linear"))
                    .endTests();
    }

    @Override
    public void addTests(
            TestContainer<ParameterizedSequenceTestable<Searcher, String[]>> tests) {
        tests.addTest("test", new ParameterizedSequenceTestable<Searcher, String[]>() {
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
