package com.fillumina.performance.consumer.viewer;

import com.fillumina.performance.producer.timer.LoopPerformancesCreator;
import com.fillumina.performance.producer.LoopPerformances;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author fra
 */
public class StringCsvViewerTest {

    @Test
    public void shouldPrintOutACvsRepresentationOfOneValue() {
        assertCvsString("1000, 100.00",
                new Object[][]{{"First", 11}});
    }

    @Test
    public void shouldPrintOutACvsRepresentationOfTwoValues() {
        assertCvsString("1000, 50.00, 100.00",
                new Object[][]{
                    {"First", 11},
                    {"Second", 22}});
    }

    @Test
    public void shouldPrintOutACvsRepresentationOfThreeValues() {
        assertCvsString("1000, 33.33, 66.67, 100.00",
                new Object[][]{
                    {"First", 11},
                    {"Second", 22},
                    {"Third", 33}});
    }

    private void assertCvsString(final String expected, final Object[][] data) {
        final LoopPerformances lp = LoopPerformancesCreator.parse(1_000, data);

        final String result = new StringCsvViewer(lp).toCsvString().toString();

        assertEquals(expected, result);
    }
}
