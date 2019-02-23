package com.fillumina.performance.time.stats.strgen;

import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.util.CamelCaseUtils;
import com.fillumina.performance.util.Selectable;
import com.fillumina.performance.util.StringGenerator;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.pathname.PathName;
import java.io.IOException;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractTimeStatsBaseStringGenerator
        implements StringGenerator<Stats>, Selectable<Stats>, Serializable {

    private static final long serialVersionUID = 1L;
    private static final Ratio DEFAULT_CONFIDENCE = Ratio.P_99;

    protected final Ratio confidence;

    public AbstractTimeStatsBaseStringGenerator() {
        this(DEFAULT_CONFIDENCE);
    }

    public AbstractTimeStatsBaseStringGenerator(Ratio confidence) {
        this.confidence = confidence;
    }

    protected abstract boolean isStatsAssignableFrom(Stats assertable);

    @Override
    public int selectableRank(Stats assertable) {
        return isStatsAssignableFrom(assertable) ? 1 : -1;
    }

    protected void appendTitle(Appendable appendable, Stats stats)
            throws IOException {
        PathName testPrefix = PathName.getCommonPrefix(stats.getNames());
        String statsType = CamelCaseUtils.camelCaseToSentence(
                stats.getStatsType().toString());
        appendable.append(statsType);
        if (testPrefix != null && !testPrefix.isEmpty()) {
            appendable.append(" '")
                    .append(testPrefix.toString())
                    .append('\'');
        }
        appendable
                .append(':')
                .append(System.lineSeparator());
    }
}
