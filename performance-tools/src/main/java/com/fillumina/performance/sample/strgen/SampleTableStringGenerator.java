package com.fillumina.performance.sample.strgen;

import com.fillumina.performance.sample.AbstractSample;
import com.fillumina.performance.sample.SampleValue;
import com.fillumina.performance.util.formatter.TableFormatter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import com.fillumina.performance.util.StringGenerator;
import com.fillumina.performance.util.Viewer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SampleTableStringGenerator
        implements StringGenerator
                        <AbstractSample<?,? extends SampleValue,?>>,
                   Serializable {
    private static final long serialVersionUID = 1L;

    public static final SampleTableStringGenerator INSTANCE =
            new SampleTableStringGenerator();

    public static final Viewer<AbstractSample<?,?,?>> VIEWER =
            new Viewer<>(INSTANCE);

    protected SampleTableStringGenerator() {}

    @Override
    public void appendTo(Appendable appendable,
            AbstractSample<?,?,?> sample)
            throws IOException {
        TableFormatter table = new TableFormatter();
        boolean header = true;
        for (SampleValue v : sample.getValuesMap().values()) {
            Map<String, String> params = v.toTable();
            if (header) {
                header = false;
                for (String title : params.keySet()) {
                    table.cell(title);
                }
                table.endl();
            }
            for (String value : params.values()) {
                table.cell(value);
            }
            table.endl();
        }
        table.appendTo(appendable);
    }
}
