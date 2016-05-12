package com.fillumina.performance.util.instrument;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface Instrumentable<I extends Instrumentable<I>> {

    <T extends Instrumenter<I>> T instrumentedBy(T instrumenter);
}
