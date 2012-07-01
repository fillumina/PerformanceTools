package com.fillumina.performance;

/**
 *
 * @author fra
 */
public class StringCsvPresenter extends AbstractPerformanceConsumer<StringCsvPresenter> {
    private static final long serialVersionUID = 1L;

    public StringOutputHolder toCsvString() {
        StringBuilder buf = new StringBuilder();
        buf.append(String.format("%d, ", getLoopPerformances().getIterations()));
        buf.append(CsvHelper.toCsvString(getLoopPerformances().getPercentageList()));
        return new StringOutputHolder(buf.toString());
    }

    @Override
    public void consume() {
        toCsvString().println();
    }
}
