package com.fillumina.performance.util.unit;

import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
@RunWith(Parameterized.class)
public class UnitFormatterTest {

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                 {MemUnit.B}, {TimeUnit.SECONDS}
           });
    }

    private Unit unit;

    public UnitFormatterTest(Unit unit) {
        this.unit = unit;
    }

    @Test
    public void shouldFindACommonUnitForASerieOfValues() {
    }

}
