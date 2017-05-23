package com.fillumina.performance.util.formatter;

import java.io.IOException;
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
            int min = col + Math.min(spanCol, longer.length);
            for (int i = col; i < min; i++) {
                l += longer[i] + lenghtSeparator;
            }
            return equalize(value, l - value.length());
        }

        protected String equalize(String s, int l) {
            if (l <= 0) {
                return s;
            }
            switch (pos) {
                case CENTER:
                    int m1 = l / 2;
                    int m2 = l / 2;
                    if (m1 + m2 != l) {
                        m2++;
                    }
                    return repeate(' ', m1) + s + repeate(' ', m2);
                case LEFT: return s + repeate(' ', l);
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
    private char[] frame;
    private int margin;
    private int padding;

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
        cell(title).align(pos).span(9999).endl().hr(underlineChar);
        return this;
    }

    public TableFormatter hr(char c) {
        lastCell = new HorizontalLine(row, col, String.valueOf(c));
        cells.add(lastCell);
        span(999);
        return endl();
    }

    public TableFormatter margin(String str, int margin, int padding) {
        this.frame = frameChars(str);
        this.padding = padding;
        this.margin = margin;
        return this;
    }

    public static char[] frameChars(String str) {
        if (str == null ||
                (str.length() != 1 && str.length() != 2 && str.length() != 8)) {
            throw new IllegalArgumentException(
                    "margin must have 1,2 or 8 characters");
        }
        char[] frame;
        switch (str.length()) {
            case 1:
                frame = new char[8];
                for (int i = 0; i < frame.length; i++) {
                    frame[i] = str.charAt(0);
                }
                return frame;
            case 2:
                frame = new char[8];
                frame[0] = str.charAt(0);
                frame[1] = str.charAt(0);
                frame[2] = str.charAt(0);
                frame[3] = str.charAt(1);
                frame[4] = str.charAt(0);
                frame[5] = str.charAt(0);
                frame[6] = str.charAt(0);
                frame[7] = str.charAt(1);
                return frame;
            case 8:
                return str.toCharArray();
        }
        throw new AssertionError("cannot be here");
    }

    /**
     * If the value is equals to nullValue then prints nullValueMessage.
     */
    public TableFormatter param(String name, Object value,
            Object nullValue, String nullValueMessage) {
        if (value == nullValue) {
            line(name, ":", nullValueMessage);
        } else {
            line(name, ":", value);
        }
        return this;
    }

    /**
     * Write out a line if the value isn't null (uses :).
     */
    public TableFormatter param(String name, Object value) {
        if (value != null) {
            line(name, ":", value);
        }
        return this;
    }

    /**
     * Write out a line if the value isn't null (uses =).
     */
    public TableFormatter value(String name, Object value) {
        if (value != null) {
            line(name, "=", value);
        }
        return this;
    }

    /**
     * Each param is on a separate cell all followed by a single end line.
     */
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

    /**
     * Adds all the values to the same cell.
     */
    public TableFormatter cell(Object... values) {
        StringBuilder buf = new StringBuilder();
        for (Object o : values) {
            buf.append(String.valueOf(o));
        }
        return cell(buf.toString());
    }

    public TableFormatter cell() {
        return cell("");
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

    public TableFormatter emptyLine() {
        return endl();
    }

    public TableFormatter endl() {
        col = 0;
        row++;
        return this;
    }

    @Override
    public String toString() {
        final String table = formatTable(cells, separator);
        if (frame != null) {
            return frame(frame, margin, padding, table);
        }
        return table;
    }

    public void appendTo(Appendable appendable) throws IOException {
        if (frame != null) {
            String table = formatTable(cells, separator);
            frame(appendable, frame, margin, padding, table);
        } else {
            formatTable(appendable, cells, separator);
        }
    }

    public void appendToCatchingIOException(Appendable appendable) {
        try {
            if (frame != null) {
                String table = formatTable(cells, separator);
                frame(appendable, frame, margin, padding, table);
            } else {
                formatTable(appendable, cells, separator);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String formatTable(Iterable<Cell> cells, String separator) {
        StringBuilder buf = new StringBuilder();
        try {
            formatTable(buf, cells, separator);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return buf.toString();
    }

    public static void formatTable(
            Appendable appendable,
            Iterable<Cell> cells,
            String separator) throws IOException {
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
        String[][] table = new String[maxRow][maxCol];
        final int separatorLength = separator.length();
        int longer[] = calculateLongerStringByColumn(cells, maxCol,
                separatorLength);
        for (Cell c : cells) {
            table[c.row][c.col] = c.toEqualizedString(longer, separatorLength);
            if (c.spanCol > 1) {
                int min = Math.min(c.col + c.spanCol, table[c.row].length);
                for (int i = c.col + 1; i < min; i++) {
                    table[c.row][i] = SPAN;
                }
            }
        }
        for (int r = 0; r < maxRow; r++) {
            int lineLength = 0;
            for (int c = 0; c < maxCol; c++) {
                final String cell = table[r][c];
                if (!SPAN.equals(cell)) {
                    if (cell != null) {
                        String str = cell;
                        lineLength += str.length();
                        appendable.append(str);
                    } else {
                        int missing = spanLength(longer, 0, c) +
                                c * separatorLength -
                                lineLength;
                        if (missing > 0) {
                            lineLength += missing;
                            appendable.append(repeate(' ', missing));
                        }
                    }
                }
                if (c < maxCol - 1 && !SPAN.equals(table[r][c + 1])) {
                    lineLength += separatorLength;
                    appendable.append(separator);
                }
            }
            appendable.append(System.lineSeparator());
        }
    }

    public static String frame(String frame,
            int margin, int padding, String text) {
        return frame(frameChars(frame), margin, padding, text);
    }

    public static String frame(char[] frame,
            int margin, int padding, String text) {
        StringBuilder buf = new StringBuilder();
        try {
            frame(buf, frame, margin, padding, text);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return buf.toString();
    }

    public static void frame(Appendable appendable,
            char[] frame, int margin, int padding, String text)
            throws IOException {
        final String lf = System.lineSeparator();
        String[] lines = text.split(lf);
        int longer = getLongerLineLength(lines);
        final String marginStr = space(margin);
        final String paddingStr = space(padding);
        final String longerStr = space(longer);
        appendable.append(marginStr)
                .append(frame[0])
                .append(repeate(frame[1], longer + (padding * 2)))
                .append(frame[2])
                .append(marginStr)
                .append(lf);
        for (int i = 0; i < padding; i++) {
            appendable.append(marginStr)
                    .append(frame[7])
                    .append(paddingStr)
                    .append(longerStr)
                    .append(paddingStr)
                    .append(frame[3])
                    .append(marginStr)
                    .append(lf);
        }
        for (String line : lines) {
            appendable.append(marginStr)
                    .append(frame[7])
                    .append(paddingStr)
                    .append(line)
                    .append(space(longer - line.length()))
                    .append(paddingStr)
                    .append(frame[3])
                    .append(marginStr)
                    .append(lf);
        }
        for (int i = 0; i < padding; i++) {
            appendable.append(marginStr)
                    .append(frame[7])
                    .append(paddingStr)
                    .append(longerStr)
                    .append(paddingStr)
                    .append(frame[3])
                    .append(marginStr)
                    .append(lf);
        }
        appendable.append(marginStr)
                .append(frame[6])
                .append(repeate(frame[5], longer + (padding * 2)))
                .append(frame[4])
                .append(marginStr)
                .append(lf);
    }

    public static int getLongerLineLength(String[] lines) {
        int longer = 0;
        for (String line : lines) {
            int l = line.length();
            if (l > longer) {
                longer = l;
            }
        }
        return longer;
    }

    private static int[] calculateLongerStringByColumn(Iterable<Cell> cells,
            int maxCol, int separatorLength) {
        int length;
        int longer[] = new int[maxCol];
        for (Cell cell : cells) {
            if (cell.spanCol == 1) {
                length = cell.length();
                if (length > longer[cell.col]) {
                    longer[cell.col] = length;
                }
            }
        }
        for (Cell cell : cells) {
            if (cell.spanCol > 1) {
                length = cell.length();
                int lastCell = cell.col + cell.spanCol - 1;
                int over = length -
                        spanLength(longer, cell.col, lastCell) -
                        (separatorLength * (cell.spanCol - 1));
                if (over > 0) {
                    longer[lastCell] += over;
                }
            }
        }
        return longer;
    }

    private static int spanLength(int[] longer, int from, int to) {
        int accumulator = 0;
        for (int i = from; i <= to; i++) {
            if (i < longer.length) {
                accumulator += longer[i];
            }
        }
        return accumulator;
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
                    .append(repeate(character, size))
                    .append(System.lineSeparator())
                    .append(character).append(' ')
                    .append(title)
                    .append(' ').append(character)
                    .append(System.lineSeparator())
                    .append(repeate(character, size))
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

    public static String padToLengthAfter(int lenght, String s) {
        return s + space(lenght - s.length());
    }

    public static String padToLengthBefore(int lenght, String s) {
        return space(lenght - s.length()) + s;
    }

    public static String padToLengthCenter(int lenght, String s) {
        int l = lenght - s.length();
        int l2 = l / 2;
        return space(l2) + s + space(l - l2);
    }

    public static String space(int r) {
        return repeate(' ', r);
    }

    /**
     * Creates a string with r repetitions of the c character.
     *
     * @param c the character to repeat
     * @param r how many times c has to be repeated
     * @return a string with the c character repeated r times
     */
    public static String repeate(char c, int r) {
        char[] a = new char[r];
        for (int i = 0; i < r; i++) {
            a[i] = c;
        }
        return new String(a);
    }
}
