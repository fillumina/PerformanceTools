package com.fillumina.performance.util.stats;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MeasureJoinTest {

    @Test
    public void shouldGiveSameResultsAsASingleMeasure() {
        double[][] measures = {
            {3, 5, 8, 2, 9.3, 4, 5.2, 3, 3.2, 7, 6.1, 1,},
            {4.2, 5, 7.3, 6.2, 3.2},
            {8.7, 5, 2.3, 1.4, 5.5, 3, 2, 6.7}
        };

        Measure a = new OnlineMeasure(measures[0]);
        Measure b = new OnlineMeasure(measures[1]);
        Measure c = new OnlineMeasure(measures[2]);

        Measure join = new MeasureJoin(a, b, c);

        Measure all = new OnlineMeasure(merge(measures));

        MeasureRatio ratio = new MeasureRatio(join, all, Ratio.P_999);
        //System.out.println("ratio= " + ratio.toString());
        assertEquals(ratio.toString(), 0, ratio.compare());

        assertEquals(all.getCount(), join.getCount(), 0.001);
        assertEquals(all.getMax(), join.getMax(), 0.001);
        assertEquals(all.getMin(), join.getMin(), 0.001);
        assertEquals(all.getSum(), join.getSum(), 0.001);
        assertEquals(all.getMean(), join.getMean(), 0.001);
        assertEquals(all.getVariance(), join.getVariance(), 0.001);
    }

    private double[] merge(double[][] array) {
        int count = 0;
        for (int i=0; i<array.length; i++) {
            count += array[i].length;
        }
        double[] all = new double[count];
        int index = 0;
        for (int i=0; i<array.length; i++) {
            int length = array[i].length;
            System.arraycopy(array[i], 0, all, index, length);
            index += length;
        }
        return all;
    }

}
