package com.fillumina.performance.consumer.viewer;

import com.fillumina.performance.producer.timer.LoopPerformances;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author fra
 */
public class StringTableViewerTest {

    @Test
    public void shouldPrintOutACvsRepresentationOfOneValue() {
        assertTableString("\nTitle (1,000 iterations)\n" +
                "First	   0 :	      0.01 ns		    100.00 %\n" +
                "     	   * :	      0.01 ns",
                "Title",
                new Object[][]{
                    {"First", 11}});
    }

    @Test
    public void shouldPrintOutACvsRepresentationOfTwoValues() {
        assertTableString("\nTitle (1,000 iterations)\n" +
                "First 	   0 :	      0.01 ns		     50.00 %\n" +
                "Second	   1 :	      0.02 ns		    100.00 %\n" +
                "      	   * :	      0.03 ns",
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
                "      	   * :	      0.07 ns",
                "Title",
                new Object[][]{
                    {"First", 11},
                    {"Second", 22},
                    {"Third", 33}});
    }

    private void assertTableString(final String expected,
            final String title,
            final Object[][] data) {
        final LoopPerformances lp = LoopPerformancesCreator.parse(1_000, data);

        final String result = new StringTableViewer(title, lp).getTable().toString();

        assertEquals(result + "\n\n", expected, result);
    }

}
