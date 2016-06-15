package com.fillumina.performance.util;

import java.util.ArrayList;
import java.util.List;


/**
 * Produces a table formatted string.
 *
 * @author Francesco Illuminati
 */
public class TableFormatter {
    private static final String SPAN_CELL = "***SPAN\0CELL***";

    private static enum Position {
        LEFT, CENTER, RIGHT
    }

    public static class Cell {
        private final String value;
        private final int col, row;
        private Position pos = Position.LEFT;
        private int spanCol = 1;

        public Cell(int row, int col, String value) {
            this.row = row;
            this.col = col;
            this.value = value;
        }

        public Cell pos(final Position value) {
            this.pos = value;
            return this;
        }

        public Cell spanCol(final int value) {
            this.spanCol = value;
            return this;
        }

        int length() {
            return value.length();
        }

        String toEqualizedString(int[] length, int lenghtSeparator) {
            if (spanCol == 1) {
                return equalize(value, length[col] - value.length());
            }
            int l = -lenghtSeparator;
            for (int i=0; i<spanCol; i++) {
                l += length[col + i] + lenghtSeparator;
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

    /** Each value is on a separate cell all followed by a single end line. */
    public TableFormatter line(Object... values) {
        for (Object o : values) {
            cell(String.valueOf(o));
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

    public TableFormatter cell(String value) {
        lastCell = new Cell(row, col, value);
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
        lastCell.pos(Position.LEFT);
        return this;
    }

    public TableFormatter right() {
        lastCell.pos(Position.RIGHT);
        return this;
    }

    public TableFormatter center() {
        lastCell.pos(Position.CENTER);
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
        int longer[] = longerStringByColumn(cells, maxCol);
        final int separatorLength = separator.length();
        for (Cell c : cells) {
            table[c.row][c.col] = c.toEqualizedString(longer, separatorLength);
            if (c.spanCol > 1) {
                for (int i=1; i<c.spanCol; i++) {
                    table[c.row][c.col + i] = SPAN_CELL;
                }
            }
        }
        StringBuilder buf = new StringBuilder();
        for (int r=0; r<maxRow; r++) {
            for (int c=0; c<maxCol; c++) {
                final String cell = table[r][c];
                if (cell != null) {
                    if (cell == SPAN_CELL) {
                        continue;
                    }
                    buf.append(cell);
                } else {
                    buf.append(repeate(' ', longer[c]));
                }
                if (c < maxCol - 1) {
                    buf.append(separator);
                }
            }
            buf.append(System.lineSeparator());
        }
        return buf.toString();
    }

    private static int[] longerStringByColumn(Iterable<Cell> cells, int maxCol) {
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

    public static String title(String title, char undelineChar) {
        StringBuilder buf = new StringBuilder();
        if (title != null && !title.isEmpty()) {
            buf.append(title)
                    .append(System.lineSeparator())
                    .append(TableFormatter.repeate(undelineChar, title.length()))
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
