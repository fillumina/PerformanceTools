package com.fillumina.performance.consumer.viewer;

import com.fillumina.performance.producer.FakeLoopPerformancesCreator;
import com.fillumina.performance.producer.LoopPerformances;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Francesco Illuminati
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
        final LoopPerformances lp = FakeLoopPerformancesCreator.parse(1_000, data);

        final String result = StringCsvViewer.INSTANCE.toCsvString(lp).toString();

        assertEquals(expected, result);
    }
}
