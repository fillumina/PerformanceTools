package com.fillumina.performance.util.instrument;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class InstrumenterImpl implements Instrumenter<InstrumentableImpl> {

    private InstrumentableImpl instrumentable;
    private int param;

    public InstrumenterImpl setInstrumenterParam(int param) {
        this.param = param;
        return this;
    }

    @Override
    public InstrumenterImpl instrument(
            InstrumentableImpl instrumentable) {
        this.instrumentable = instrumentable;
        return this;
    }

    public int calculate() {
        return param * instrumentable.getInstrumentableParam();
    }
}
