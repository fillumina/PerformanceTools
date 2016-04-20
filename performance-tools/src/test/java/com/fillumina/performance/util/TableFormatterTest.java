package com.fillumina.performance.util;

import com.fillumina.performance.util.TableFormatter.Cell;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TableFormatterTest {

    private TableFormatter tf = new TableFormatter();

    @Test
    public void shouldPrintSingleField() {
        assertEquals("one\n", tf.cell("one").toString());
    }

    @Test
    public void shouldPrintTwoFields() {
        assertEquals("one two\n", tf.cell("one").cell("two").toString());
    }

    @Test
    public void shouldPrintTwoLines() {
        assertEquals("one\ntwo\n", tf.cell("one").endl().cell("two").toString());
    }

    @Test
    public void shouldPrintTwoEqualizedLines() {
        final String str = tf.cell("one").cell("two").endl()
                .cell("three").cell("four")
                .toString();
        assertEquals("one   two \n" +
                     "three four\n", str);
    }

    @Test
    public void shouldPrintTwoEqualizedCenteredLines() {
        final String str = tf.cell("one").center().cell("two").right().endl()
                .cell("tirtythree").cell("fourtyfour")
                .toString();
        assertEquals("   one            two\n" +
                     "tirtythree fourtyfour\n", str);
    }

    @Test
    public void shouldUse2ColSpan() {
        final String str = tf.cell("one").center().span(2).cell("A").endl()
                .cell("tirtythree").cell("fourtyfour").cell("B")
                .toString();
        assertEquals("         one          A\n" +
                     "tirtythree fourtyfour B\n", str);
    }

    @Test
    public void shouldUse3ColSpan() {
        final String str = tf.cell("one").right().span(3).cell("A").endl()
                .cell("tirtythree").cell("fourtyfour").cell("bla").cell("B")
                .toString();
        assertEquals("                      one A\n" +
                     "tirtythree fourtyfour bla B\n", str);
    }

    @Test
    public void shouldPrintDiagonalCells() {
        List<Cell> cells = new ArrayList<>();
        cells.add(new Cell(0,0, "one"));
        cells.add(new Cell(1,1, "two"));
        cells.add(new Cell(2,2, "three"));
        final String str = TableFormatter.formatTable(cells, " ");
        assertEquals("one          \n" +
                     "    two      \n" +
                     "        three\n", str);
    }
}
