package com.fillumina.performance.consumer.viewer;

import com.fillumina.performance.producer.FakeLoopPerformancesCreator;
import com.fillumina.performance.producer.LoopPerformances;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StringTableViewerTest {

    @Test
    public void shouldPrintOutACvsRepresentationOfOneValue() {
        assertTableString("\nTitle (1,000 iterations)\n" +
                "First	   0 :	      0.01 ns		    100.00 %\n" +
                "     	   * :	      0.01 ns\n",
                "Title",
                new Object[][]{
                    {"First", 11}});
    }

    @Test
    public void shouldPrintOutACvsRepresentationOfTwoValues() {
        assertTableString("\nTitle (1,000 iterations)\n" +
                "First 	   0 :	      0.01 ns		     50.00 %\n" +
                "Second	   1 :	      0.02 ns		    100.00 %\n" +
                "      	   * :	      0.03 ns\n",
                "Title",
                new Object[][]{
                    {"First", 11},
                    {"Second", 22}});
    }

    @Test
    public void shouldPrintOutACvsRepresentationOfThreeValues() {
        assertTableString("\nTitle (1,000 iterations)\n" +
                "First 	   0 :	      0.01 ns		     33.33 %\n" +
                "Second	   1 :	      0.02 ns		     66.67 %\n" +
                "Third 	   2 :	      0.03 ns		    100.00 %\n" +
                "      	   * :	      0.07 ns\n",
                "Title",
                new Object[][]{
                    {"First", 11},
                    {"Second", 22},
                    {"Third", 33}});
    }

    private void assertTableString(final String expected,
            final String title,
            final Object[][] data) {
        final LoopPerformances lp = FakeLoopPerformancesCreator.parse(1_000, data);

        final String result = StringTableViewer.INSTANCE
                .getTable(title, lp).toString();

        assertEquals(result + "\n\n", expected, result);
    }

}
