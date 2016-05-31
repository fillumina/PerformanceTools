package com.fillumina.performance.util.instrument;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class InstrumentableTest {

    @Test
    public void shouldCalculateWithStandardParadigm() {
        InstrumentableImpl a = new InstrumentableImpl();
        a.setInstrumentableParam(3);

        InstrumenterImpl b = new InstrumenterImpl();
        b.setInstrumenterParam(4);
        b.instrument(a);

        assertEquals(12, b.calculate());
    }

    @Test
    public void shouldCalculateUsingFluentInterface() {
        assertEquals(12,
                new InstrumentableImpl()
                .setInstrumentableParam(3)
                .instrumentedBy(new InstrumenterImpl())
                .setInstrumenterParam(4)
                .calculate());
    }

}
