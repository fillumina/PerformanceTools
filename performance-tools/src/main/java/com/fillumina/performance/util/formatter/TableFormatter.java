package com.fillumina.performance.util.formatter;

import java.util.ArrayList;
import java.util.List;


/**
 * Produces a table formatted string.
 *
 * @author Francesco Illuminati
 */
public class TableFormatter {
    private static final String SPAN = "\0SPAN";

    public static enum Alignment {
        LEFT, CENTER, RIGHT
    }

    public static class Cell {
        private final String value;
        private final int col, row;
        private Alignment pos = Alignment.LEFT;
        private int spanCol = 1;

        public Cell(int row, int col, String value) {
            this.row = row;
            this.col = col;
            this.value = value;
        }

        public Cell pos(final Alignment value) {
            this.pos = value;
            return this;
        }

        public Cell spanCol(final int value) {
            this.spanCol = value;
            return this;
        }

        int length() {
            return value == null ? 0 : value.length();
        }

        String getValue() {
            return value;
        }

        String toEqualizedString(int[] longer, int lenghtSeparator) {
            if (spanCol == 1) {
                if (value == null) {
                    return repeate(' ', length());
                }
                return equalize(value, longer[col] - length());
            }
            int l = -lenghtSeparator;
            int min = Math.min(spanCol, longer.length);
            for (int i=col; i<min; i++) {
                l += longer[i] + lenghtSeparator;
            }
            return equalize(value, l - value.length());
        }

        protected String equalize(String s, int l) {
            if (l == 0) {
                return s;
            }
            switch (pos) {
                case CENTER:
                    int m1 = l/2;
                    int m2 = l/2;
                    if (m1 + m2 != l) {
                        m2++;
                    }
                    return repeate(' ', m1) + s + repeate(' ', m2);
                case LEFT:  return s + repeate(' ', l);
                case RIGHT: return repeate(' ', l) + s;
                default: throw new AssertionError();
            }
        }
    }

    private static class HorizontalLine extends Cell {

        public HorizontalLine(int row, int col, String value) {
            super(row, col, value);
        }

        @Override
        String toEqualizedString(int[] longer, int lenghtSeparator) {
            String value = getValue();
            char c = value == null || value.length() < 1 ? '-' : value.charAt(0);
            return repeate(c, calculateLineLength(longer, lenghtSeparator));
        }
    }

    private final List<Cell> cells = new ArrayList<>();
    private Cell lastCell;
    private int col, row;
    private final String separator;

    public TableFormatter() {
        this(" ");
    }

    public TableFormatter(String separator) {
        this.separator = separator;
    }

    public boolean isEmpty() {
        return cells.isEmpty();
    }

    public TableFormatter header(String title) {
        return header(title, Alignment.CENTER, '-');
    }

    public TableFormatter headerRight(String title, char underlineChar) {
        return header(title, Alignment.RIGHT, underlineChar);
    }

    public TableFormatter headerLeft(String title, char underlineChar) {
        return header(title, Alignment.LEFT, underlineChar);
    }

    public TableFormatter headerCenter(String title, char underlineChar) {
        return header(title, Alignment.CENTER, underlineChar);
    }

    public TableFormatter header(String title, Alignment pos,
            char underlineChar) {
        cell(title).align(pos).span(9999);
        endl();
        hr('-').endl();
        return this;
    }

    public TableFormatter hr(char c) {
        lastCell = new HorizontalLine(row, col, String.valueOf(c));
        cells.add(lastCell);
        col++;
        return span(999);
    }

    public TableFormatter emptyLine() {
        return hr(' ');
    }

    /** If the value is equals to nullValue then prints nullValueMessage. */
    public TableFormatter param(String name, Object value,
            Object nullValue, String nullValueMessage) {
        if (value == nullValue) {
            line(name, ":", nullValueMessage);
        } else {
            line(name, ":", value);
        }
        return this;
    }

    /** Write out a line if the value isn't null (uses :). */
    public TableFormatter param(String name, Object value) {
        if (value != null) {
            line(name, ":", value);
        }
        return this;
    }

    /** Write out a line if the value isn't null (uses =). */
    public TableFormatter value(String name, Object value) {
        if (value != null) {
            line(name, "=", value);
        }
        return this;
    }

    /** Each param is on a separate cell all followed by a single end line. */
    public TableFormatter line(Object... values) {
        for (Object o : values) {
            if (o == null) {
                cell("");
            } else {
                cell(String.valueOf(o));
            }
        }
        endl();
        return this;
    }

    /** Adds all the values to the same cell. */
    public TableFormatter cell(Object... values) {
        StringBuilder buf = new StringBuilder();
        for (Object o : values) {
            buf.append(String.valueOf(o));
        }
        return cell(buf.toString());
    }

    public TableFormatter cell(Object value) {
        lastCell = new Cell(row, col, value == null ? "" : value.toString());
        cells.add(lastCell);
        col++;
        return this;
    }

    public TableFormatter pad(int p) {
        cells.add(new Cell(row, col, repeate(' ', p)));
        col++;
        return this;
    }

    public TableFormatter span(int col) {
        lastCell.spanCol(col);
        this.col += col - 1;
        return this;
    }

    public TableFormatter left() {
        align(Alignment.LEFT);
        return this;
    }

    public TableFormatter right() {
        align(Alignment.RIGHT);
        return this;
    }

    public TableFormatter center() {
        align(Alignment.CENTER);
        return this;
    }

    public TableFormatter align(Alignment pos) {
        lastCell.pos(pos);
        return this;
    }

    public TableFormatter endl() {
        col = 0;
        row++;
        return this;
    }

    @Override
    public String toString() {
        return formatTable(cells, separator);
    }

    public static String formatTable(Iterable<Cell> cells, String separator) {
        int maxCol = 0, maxRow = 0;
        for (Cell c : cells) {
            if (c.col > maxCol) {
                maxCol = c.col;
            }
            if (c.row > maxRow) {
                maxRow = c.row;
            }
        }
        maxCol++;
        maxRow++;
        String[][] table =  new String[maxRow][maxCol];
        int longer[] = calculateLongerStringByColumn(cells, maxCol);
        final int separatorLength = separator.length();
        for (Cell c : cells) {
            table[c.row][c.col] = c.toEqualizedString(longer, separatorLength);
            if (c.spanCol > 1) {
                int min = Math.min(c.spanCol, table[c.row].length);
                for (int i=c.col + 1; i<min; i++) {
                    table[c.row][i] = SPAN;
                }
            }
        }
        StringBuilder buf = new StringBuilder();
        for (int r=0; r<maxRow; r++) {
            for (int c=0; c<maxCol; c++) {
                final String cell = table[r][c];
                if (!SPAN.equals(cell)) {
                    if (cell != null) {
                        buf.append(cell);
                    } else {
                        buf.append(repeate(' ', longer[c]));
                    }
                }
                if (c < maxCol - 1 && !SPAN.equals(table[r][c + 1])) {
                   buf.append(separator);
                }
           }
            buf.append(System.lineSeparator());
        }
        return buf.toString();
    }

    private static int[] calculateLongerStringByColumn(Iterable<Cell> cells,
            int maxCol) {
        int length;
        int longer[] = new int[maxCol];
        for (Cell cell : cells) {
            length = cell.length();
            if (length > longer[cell.col]) {
                longer[cell.col] = length;
            }
        }
        return longer;
    }

    private static int calculateLineLength(int[] longer, int separatorLength) {
        int total = -separatorLength;
        for (int l : longer) {
            total += l + separatorLength;
        }
        return total;
    }

    public static String frame(String title, char character) {
        StringBuilder buf = new StringBuilder();
        if (title != null && !title.isEmpty()) {
            final int size = 4 + title.length();
            buf
                    .append(TableFormatter.repeate(character, size))
                    .append(System.lineSeparator())
                    .append(character).append(' ')
                    .append(title)
                    .append(' ').append(character)
                    .append(System.lineSeparator())
                    .append(TableFormatter.repeate(character, size))
                    .append(System.lineSeparator());
        }
        return buf.toString();
    }

    public static String title(String title, char underlineChar) {
        return title(title, underlineChar, title.length());
    }

    public static String title(String title,
            char underlineChar,
            int repeatUnderlineChar) {
        StringBuilder buf = new StringBuilder();
        if (title != null && !title.isEmpty()) {
            buf.append(title)
                    .append(System.lineSeparator())
                    .append(TableFormatter.repeate(underlineChar,
                            repeatUnderlineChar))
                    .append(System.lineSeparator());
        }
        return buf.toString();
    }

    /**
     * Creates a string with r repetitions of the c character.
     * @param c the character to repeat
     * @param r how many times c has to be repeated
     * @return a string with the c character repeated r times
     */
    public static String repeate(char c, int r) {
        char[] a = new char[r];
        for (int i=0; i<r; i++) {
            a[i] = c;
        }
        return new String(a);
    }
}
