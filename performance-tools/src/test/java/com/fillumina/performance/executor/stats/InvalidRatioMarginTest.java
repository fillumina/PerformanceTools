package com.fillumina.performance.executor.stats;

import com.fillumina.performance.executor.PN;
import com.fillumina.performance.util.pathname.PathName;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.OnlineDimensionalMeasure;
import java.util.LinkedHashMap;
import java.util.Map;
import static org.junit.Assert.assertFalse;
import org.junit.Test;

public class InvalidRatioMarginTest {
    @Test
    public void shouldNotReportZeroMarginForAnInvalidReferenceRatio() {
        OnlineDimensionalMeasure noisy = new OnlineDimensionalMeasure();
        OnlineDimensionalMeasure steady = new OnlineDimensionalMeasure();
        for (int i = 0; i < 33; i++) {
            noisy.addSample(i % 2 == 0 ? 102 : -98);
            steady.addSample(1);
        }
        Map<PathName, DimensionalMeasure> measures = new LinkedHashMap<>();
        measures.put(PN.pname("noisy"), noisy);
        measures.put(PN.pname("steady"), steady);
        Stats stats = new Stats(measures);

        assertFalse(stats.getRatioWithRef("steady", Ratio.P_99).isValid());
        assertFalse("invalid interval must not appear to meet a finite margin",
                stats.getMaximumPercentageMargin(Ratio.P_99).isValid());
    }
}
