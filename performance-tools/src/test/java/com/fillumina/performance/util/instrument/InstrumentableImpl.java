package com.fillumina.performance.util.instrument;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class InstrumentableImpl implements Instrumentable<InstrumentableImpl> {

    private int param;

    public int getInstrumentableParam() {
        return param;
    }

    public InstrumentableImpl setInstrumentableParam(int param) {
        this.param = param;
        return this;
    }

    @Override
    public <T extends Instrumenter<InstrumentableImpl>> T instrumentedBy(
            T instrumenter) {
        instrumenter.instrument(this);
        return instrumenter;
    }
}
