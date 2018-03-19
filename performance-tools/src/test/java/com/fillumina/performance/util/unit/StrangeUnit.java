package com.fillumina.performance.util.unit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
 enum StrangeUnit implements Unit<StrangeUnit> {
    // as suggested by A. Pettirossi's: "per due armadi passa 1 ed 1 sola scarpa"
    WARDROBES(1.0), SHOES(2.0), TREES(3.0);

    public static final Units<StrangeUnit> UNITS = new Units<>(values());

    private final double factor;

    private StrangeUnit(double factor) {
        this.factor = factor;
    }

    @Override
    public Units<StrangeUnit> units() {
        return UNITS;
    }

    @Override
    public double getFactor() {
        return factor;
    }

}
