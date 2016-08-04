package com.fillumina.performance.util.formatter;

import com.fillumina.performance.util.formatter.TableFormatter.Cell;
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

    @Test
    public void shouldPrintLines() {
        String table = new TableFormatter()
                .line("alfa", 1, null, 12.3)
                .line("beta", null, 2, 24.5)
                .toString();
        assertEquals("alfa 1   12.3\n" +
                     "beta   2 24.5\n",
                table);
    }

    @Test
    public void shouldUseParameter() {
        String table = new TableFormatter()
                .param("first", 1)
                .param("second", 2)
                .param("default", -1, -1, "default")
                .toString();
        assertEquals("first   : 1      \n" +
                     "second  : 2      \n" +
                     "default : default\n",
                table);
    }

    @Test
    public void shouldUseCenteredHeaderWithParameter() {
        String table = new TableFormatter()
                .headerCenter("title", '-')
                .param("first", 1)
                .param("second", 2)
                .param("default", -1, -1, "default")
                .toString();
        assertEquals("      title      \n" +
                     "-----------------\n" +
                     "first   : 1      \n" +
                     "second  : 2      \n" +
                     "default : default\n",
                table);
    }

    @Test
    public void shouldUseLeftHeaderWithParameter() {
        String table = new TableFormatter()
                .headerLeft("title", '-')
                .param("first", 1)
                .param("second", 2)
                .param("default", -1, -1, "default")
                .toString();
        assertEquals("title            \n" +
                     "-----------------\n" +
                     "first   : 1      \n" +
                     "second  : 2      \n" +
                     "default : default\n",
                table);
    }

    @Test
    public void shouldUseRightHeaderWithParameter() {
        String table = new TableFormatter()
                .headerRight("title", '-')
                .param("first", 1)
                .param("second", 2)
                .param("default", -1, -1, "default")
                .toString();
        assertEquals("            title\n" +
                     "-----------------\n" +
                     "first   : 1      \n" +
                     "second  : 2      \n" +
                     "default : default\n",
                table);
    }

    @Test
    public void shouldUseHeaderWithParameter() {
        String table = new TableFormatter()
                .header("title")
                .param("first", 1)
                .param("second", 2)
                .param("default", -1, -1, "default")
                .toString();
        assertEquals("      title      \n" +
                     "-----------------\n" +
                     "first   : 1      \n" +
                     "second  : 2      \n" +
                     "default : default\n",
                table);
    }

    @Test
    public void shouldUseLine() {
        String table = new TableFormatter()
                .line("one", "two", "three")
                .line("alpha", "", "beta")
                .line("alabama")
                .line(null, null, "right")
                .line("left", null, null)
                .line(null, "center", null)
                .toString();
        assertEquals("one     two    three\n" +
                     "alpha          beta \n" +
                     "alabama             \n" +
                     "               right\n" +
                     "left                \n" +
                     "        center      \n",
                table);
    }
}
