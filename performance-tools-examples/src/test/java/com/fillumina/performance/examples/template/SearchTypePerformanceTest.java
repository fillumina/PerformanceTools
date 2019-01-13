package com.fillumina.performance.examples.template;

import com.fillumina.performance.examples.PrintOut;
import com.fillumina.performance.examples.template.SearchTypePerformanceTest.Searcher;
import com.fillumina.performance.executor.annotation.Param;
import com.fillumina.performance.executor.annotation.Sequence;
import com.fillumina.performance.executor.generator.TestConfiguration;
import com.fillumina.performance.template.MixedAssertionBuilder;
import com.fillumina.performance.template.MixedConfigurationBuilder;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.util.stats.Ratio;
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
public class SearchTypePerformanceTest extends PerformanceTemplate {

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
    public void config(MixedConfigurationBuilder<?> config) {
        config.speedConfig();
    }


    @Override
    public void addTests(TestConfiguration<?> tests) {
        tests.addTest("test", new Runnable() {
            final Random rnd = new Random(System.currentTimeMillis());

            @Param
            private Searcher searcher;

            @Sequence
            private String[] names;

            @Override
            public void run() {
                final int pos = rnd.nextInt(names.length);
                final int result = searcher.indexOf(names, names[pos]);
                assertEquals(pos, result);
            }
        });

        tests.parameters().name("searcher")
                .value("linear", new LinearSearcher())
                .value("binary", new BinarySearcher())
                .end();

        final String[] locales = Locale.getISOCountries();
        Arrays.sort(locales);

        tests.sequences().name("names")
                .value("10", Arrays.copyOf(locales, 10))
                .value("30", Arrays.copyOf(locales, 30))
                .value("200", Arrays.copyOf(locales, 200))
                .end();
    }

    @Override
    public void addAssertions(MixedAssertionBuilder<?> assertions) {
        assertions.avgTime()
            .with().string("10").all().end()
                    .tolerance(Ratio.percentage(5))
                        .order("linear").lessThan("binary")

            .with().string("30").all().end()
                    .tolerance(Ratio.percentage(5))
                    .order("binary").lessThan("linear")
            .end();
    }

}
