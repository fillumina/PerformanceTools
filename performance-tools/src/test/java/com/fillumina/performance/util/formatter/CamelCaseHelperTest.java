package com.fillumina.performance.util.formatter;

import com.fillumina.performance.util.formatter.CamelCaseHelper;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class CamelCaseHelperTest {

    @Test
    public void shouldConvert() {
        assertEquals("first element",
                CamelCaseHelper.convertToName("getFirstElement"));
        assertEquals("print out",
                CamelCaseHelper.convertToName("shouldPrintOut"));
    }
}
