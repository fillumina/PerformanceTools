package com.fillumina.performance.util.rnd;

import static com.fillumina.performance.infrastructure.Sink.drain;
import com.fillumina.performance.template.MixedConfigurationBuilder;
import com.fillumina.performance.template.MixedAssertion;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.template.TestConfiguration;
import java.util.Random;
import org.junit.Assert;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LfsrTest {

    public static void main(final String[] args) {
        new PerformanceTemplate() {
            @Override
            public void addAssertions(MixedAssertion assertions) {
            }

            @Override
            public void config(MixedConfigurationBuilder config) {
                config.speedTestOnly();
            }

            @Override
            public void addTests(TestConfiguration<?> tests) {
                tests.addTest("lfsr", new Runnable() {
                    private Lfsr lfsr = new Lfsr();
                    @Override
                    public void run() {
                        drain(lfsr.next());
                    }
                });
                tests.addTest("full-lfsr", new Runnable() {
                    private Lfsr lfsr = new Lfsr();
                    @Override
                    public void run() {
                        drain(lfsr.fullNext());
                    }
                });
                tests.addTest("xorshiftplus", new Runnable() {
                    private Random rnd = new XorShiftPlusRandom();
                    @Override
                    public void run() {
                        drain(rnd.nextInt());
                    }
                });
            }
        }.executeWithFullOutput();
    }

    /**
     * See the Overview page of the project's javadocs for a general description
 of this unit run class.
     */
    @Test
    public void test_period() {
//        for (int i = 2; i <= 32; i++) {
//            checkPeriod(i);
//        }
        checkPeriod(32); // the one most used
    }

    private void checkPeriod(int i) throws IllegalArgumentException {
        checkPeriod(i, new Lfsr(i));
        checkPeriod(i, Lfsr.createRandom(i));
    }

    private void checkPeriod(int n, Lfsr lfsr) {
        int registerInitial = lfsr.next();
        int register;
        long period = 0;
        do {
            // only uncomment if testing small n to confirm that results make sense
            //System.out.println( toBinaryString(lfsr.register) );
            register = lfsr.next();
            period++;
        } while (register != registerInitial);
        diagnosePeriod(period, n);
    }


    private void diagnosePeriod(long period, int n) {
        long periodExpected = (1L << n) - 1L;
        String errMsg = "period = " + period +
                " IS NOT EQUAL TO THE MAXIMAL LENGTH VALUE of " +
                periodExpected + " for n = " + n;
        Assert.assertTrue(errMsg, period == periodExpected);
    }

    @Test
    public void shouldNormalNextNeverReturnZero() {
        Lfsr lfsr = new Lfsr(4);
        for (int i=0; i<32; i++) {
            assertFalse(lfsr.next() == 0);
        }
    }

    @Test
    public void shouldFullNextReturnZero() {
        boolean hadZero = false;
        Lfsr lfsr = new Lfsr(4);
        for (int i=0; i<32; i++) {
            if (lfsr.fullNext() == 0) {
                hadZero = true;
            }
        }
        assertTrue(hadZero);
    }


}
