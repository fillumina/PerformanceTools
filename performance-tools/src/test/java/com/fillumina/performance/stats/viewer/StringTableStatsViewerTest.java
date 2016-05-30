package com.fillumina.performance.stats.viewer;

import com.fillumina.performance.stats.FakePerformanceCreator;
import com.fillumina.performance.stats.PerformanceStats;
import static com.fillumina.performance.util.CamelCaseHelper.convertToName;
import com.fillumina.performance.util.ComposedName;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
@Ignore // TODO the layout of the table is not final yet
public class StringTableStatsViewerTest {

    @Test
    public void shouldPrintOutACsvRepresentationOfOneValue() {
        assertTableString(
"print out a cvs representation of one value\n" +
"-------------------------------------------\n" +
"0  First  0.011 ± 0.0 (10 samples) ns  (confidence 100.0000 %)  100.00000 ± 0.00000 % (confidence 100.0000 %)\n",
                convertToName("shouldPrintOutACvsRepresentationOfOneValue"),
                new Object[][]{
                    {"First", 11}});
    }

    @Test
    public void shouldPrintOutACsvRepresentationOfTwoValues() {
        assertTableString(
"print out a cvs representation of two values\n" +
"--------------------------------------------\n" +
"0  First   0.011 ± 0.0 (10 samples) ns  (confidence 99.9000 %)   50.00000 ± 0.00000 % (confidence 99.9000 %)  \n" +
"1  Second  0.022 ± 0.0 (10 samples) ns  (confidence 100.0000 %)  100.00000 ± 0.00000 % (confidence 100.0000 %)\n",
                convertToName("shouldPrintOutACvsRepresentationOfTwoValues"),
                new Object[][]{
                    {"First", 11},
                    {"Second", 22}});
    }

    @Test
    public void shouldPrintOutACsvRepresentationOfThreeValues() {
        assertTableString(
"print out a cvs representation of three values\n" +
"----------------------------------------------\n" +
"0  First   0.011 ± 0.0 (10 samples) ns  (confidence 99.9000 %)   33.33333 ± 0.00000 % (confidence 99.9000 %)  \n" +
"1  Second  0.022 ± 0.0 (10 samples) ns  (confidence 99.9000 %)   66.66667 ± 0.00000 % (confidence 99.9000 %)  \n" +
"2  Third   0.033 ± 0.0 (10 samples) ns  (confidence 100.0000 %)  100.00000 ± 0.00000 % (confidence 100.0000 %)\n",
                convertToName("shouldPrintOutACvsRepresentationOfThreeValues"),
                new Object[][]{
                    {"First", 11},
                    {"Second", 22},
                    {"Third", 33}});
    }

    private void assertTableString(final String expected,
            final String title,
            final Object[][] data) {
        final PerformanceStats stats =
                FakePerformanceCreator.createStats(1_000, data);

        final String result = StringTableStatsViewer.INSTANCE
                .toString(ComposedName.create(title), stats);

//        System.out.println(result);

        assertEquals(result + "\n\n", expected, result);
    }

}
