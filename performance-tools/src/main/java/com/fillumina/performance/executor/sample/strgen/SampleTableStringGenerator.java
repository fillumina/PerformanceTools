package com.fillumina.performance.executor.sample.strgen;

import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.executor.sample.SampleValue;
import com.fillumina.performance.util.StringGenerator;
import com.fillumina.performance.util.Viewer;
import com.fillumina.performance.util.formatter.TableFormatter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SampleTableStringGenerator
        implements StringGenerator<Sample>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final SampleTableStringGenerator INSTANCE =
            new SampleTableStringGenerator();

    public static final Viewer<Sample> VIEWER = new Viewer<>(INSTANCE);

    protected SampleTableStringGenerator() {}

    @Override
    public void appendTo(Appendable appendable, Sample sample)
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
