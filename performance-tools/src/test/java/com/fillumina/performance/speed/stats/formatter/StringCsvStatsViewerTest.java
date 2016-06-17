package com.fillumina.performance.speed.stats.formatter;

import com.fillumina.performance.speed.stats.strgen.SpeedCsvStringGenerator;
import com.fillumina.performance.FakePerformanceCreator;
import com.fillumina.performance.speed.stats.PerformanceStats;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class StringCsvStatsViewerTest {

    @Test
    public void shouldPrintOutACvsRepresentationOfOneValue() {
        assertCvsString("110, 10000",
                new Object[][]{{"First", 11}});
    }

    @Test
    public void shouldPrintOutACvsRepresentationOfTwoValues() {
        assertCvsString("110, 10000, 220, 10000",
                new Object[][]{
                    {"First", 11},
                    {"Second", 22}});
    }

    @Test
    public void shouldPrintOutACvsRepresentationOfThreeValues() {
        assertCvsString("110, 10000, 220, 10000, 330, 10000",
                new Object[][]{
                    {"First", 11},
                    {"Second", 22},
                    {"Third", 33}});
    }

    private void assertCvsString(final String expected, final Object[][] data) {
        final PerformanceStats stats =
                FakePerformanceCreator.createCoincidentalStats(1_000, data);

        final String result = SpeedCsvStringGenerator.INSTANCE.toString(stats);

//        System.out.println(result);

        assertEquals(expected, result);
    }
}
