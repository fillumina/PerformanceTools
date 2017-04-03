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
    private static final String NL = System.lineSeparator();

    private TableFormatter tf = new TableFormatter();

    @Test
    public void shouldPrintSingleField() {
        assertEquals("one" + NL, tf.cell("one").toString());
    }

    @Test
    public void shouldPrintTwoFields() {
        assertEquals("one two" + NL,
                tf.cell("one").cell("two").toString());
    }

    @Test
    public void shouldPrintTwoLines() {
        assertEquals("one" + NL + "two" + NL,
                tf.cell("one").endl().cell("two").toString());
    }

    @Test
    public void shouldPrintTwoEqualizedLines() {
        final String str = tf.cell("one").cell("two").endl()
                .cell("three").cell("four")
                .toString();
        assertEquals("one   two " + NL +
                     "three four" + NL,
                str);
    }

    @Test
    public void shouldPrintTwoEqualizedCenteredLines() {
        final String str = tf.cell("one").center().cell("two").right().endl()
                .cell("tirtythree").cell("fourtyfour")
                .toString();
        assertEquals("   one            two" + NL +
                     "tirtythree fourtyfour" + NL,
                str);
    }

    @Test
    public void shouldUse2ColSpan() {
        final String str = tf.cell("one").center().span(2).cell("A").endl()
                .cell("tirtythree").cell("fourtyfour").cell("B")
                .toString();
        assertEquals("         one          A" + NL +
                     "tirtythree fourtyfour B" + NL,
                str);
    }

    @Test
    public void shouldUse3ColSpan() {
        final String str = tf.cell("one").right().span(3).cell("A").endl()
                .cell("tirtythree").cell("fourtyfour").cell("bla").cell("B")
                .toString();
        assertEquals("                      one A" + NL +
                     "tirtythree fourtyfour bla B" + NL,
                str);
    }

    @Test
    public void shouldPrintDiagonalCells() {
        List<Cell> cells = new ArrayList<>();
        cells.add(new Cell(0,0, "one"));
        cells.add(new Cell(1,1, "two"));
        cells.add(new Cell(2,2, "three"));
        final String str = TableFormatter.formatTable(cells, " ");
        assertEquals("one          " + NL +
                     "    two      " + NL +
                     "        three" + NL,
                str);
    }

    @Test
    public void shouldPrintLines() {
        String table = new TableFormatter()
                .line("alfa", 1, null, 12.3)
                .line("beta", null, 2, 24.5)
                .toString();
        assertEquals("alfa 1   12.3" + NL +
                     "beta   2 24.5" + NL,
                table);
    }

    @Test
    public void shouldUseParameter() {
        String table = new TableFormatter()
                .param("first", 1)
                .param("second", 2)
                .param("default", -1, -1, "default")
                .toString();
        assertEquals("first   : 1      " + NL +
                     "second  : 2      " + NL +
                     "default : default" + NL,
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
        assertEquals("      title      " + NL +
                     "-----------------" + NL +
                     "first   : 1      " + NL +
                     "second  : 2      " + NL +
                     "default : default" + NL,
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
        assertEquals("title            " + NL +
                     "-----------------" + NL +
                     "first   : 1      " + NL +
                     "second  : 2      " + NL +
                     "default : default" + NL,
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
        assertEquals("            title" + NL +
                     "-----------------" + NL +
                     "first   : 1      " + NL +
                     "second  : 2      " + NL +
                     "default : default" + NL,
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
        assertEquals("      title      " + NL +
                     "-----------------" + NL +
                     "first   : 1      " + NL +
                     "second  : 2      " + NL +
                     "default : default" + NL,
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
        assertEquals("one     two    three" + NL +
                     "alpha          beta " + NL +
                     "alabama             " + NL +
                     "               right" + NL +
                     "left                " + NL +
                     "        center      " + NL,
                table);
    }

    @Test
    public void shouldWrapAText() {
        String expected =
            " 011111112 " + NL +
            " 7       3 " + NL +
            " 7 hello 3 " + NL +
            " 7       3 " + NL +
            " 655555554 " + NL;
        String result = TableFormatter.frame("01234567", 1, 1, "hello");

        assertEquals(expected, result);
    }

    @Test
    public void shouldWrapATextWithSingleChar() {
        String expected =
                " ********** " + NL +
                " *        * " + NL +
                " * hello  * " + NL +
                " * world! * " + NL +
                " *        * " + NL +
                " ********** " + NL;
        String result = TableFormatter.frame("*", 1, 1, "hello" + NL + "world!");

        assertEquals(expected, result);
    }

    @Test
    public void shouldWrapATextWithTwoChars() {
        String expected =
                " ---------- " + NL +
                " |        | " + NL +
                " | hello  | " + NL +
                " | world! | " + NL +
                " |        | " + NL +
                " ---------- " + NL;
        String result = TableFormatter.frame("-|", 1, 1, "hello" + NL + "world!");

        assertEquals(result, expected, result);
    }

    @Test
    public void shouldHrDrawALineUnder() {
        String table = new TableFormatter()
                .cell("one").cell("two").cell("three").endl()
                .hr('-')
                .toString();

        assertEquals(
                "one two three" + NL +
                "-------------" + NL,
                table);
    }

    @Test
    public void shouldHrDrawALineOver() {
        String table = new TableFormatter()
                .hr('-')
                .cell("one").cell("two").cell("three").endl()
                .toString();

        assertEquals(
                "-------------" + NL +
                "one two three" + NL,
                table);
    }

    @Test
    public void shouldSpanACellWithoutInfluencingOtherCells() {
        String table = new TableFormatter()
                .cell("Hello World").span(3).endl()
                .cell("one").cell("two").cell("three").endl()
                .toString();

        assertEquals(
                "Hello World  " + NL +
                "one two three" + NL,
                table);
    }

    @Test
    public void shouldPutTheTitleCenteredWithoutInfluencingOtherCells() {
        String table = new TableFormatter()
                .header("Hello World")
                .cell("one").cell("two").cell("three").endl()
                .toString();

        assertEquals(
                " Hello World " + NL +
                "-------------" + NL +
                "one two three" + NL,
                table);
    }

    @Test
    public void shouldManageComplexSpansPositionLeft() {
        String table = new TableFormatter()
                .cell("1111122").span(2).endl()
                .cell("one").cell("two").cell("three").endl()
                .cell("").cell("22222223").span(2).endl()
                .toString();

        assertEquals(NL + table,
                "1111122      " + NL +
                "one two three" + NL +
                "    22222223 " + NL,
                table);
    }

    @Test
    public void shouldManageComplexSpanPositionCenter() {
        String table = new TableFormatter()
                .cell("1111122").span(2).center().endl()
                .cell("one_one").cell("two_two").cell("three_three").endl()
                .cell("").cell("22222223").span(2).center().endl()
                .toString();

        assertEquals(NL + table,
                "    1111122                " + NL +
                "one_one two_two three_three" + NL +
                "             22222223      " + NL,
                table);
    }

    @Test
    public void shouldManageComplexSpanPositionRight() {
        String table = new TableFormatter()
                .cell("1111122").span(2).right().endl()
                .cell("one_one").cell("two_two").cell("three_three").endl()
                .cell("").cell("22222223").span(2).right().endl()
                .toString();

        assertEquals(NL + table,
                "        1111122            " + NL +
                "one_one two_two three_three" + NL +
                "                   22222223" + NL,
                table);
    }
}
