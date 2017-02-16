package com.fillumina.performance.util.instrument;

/**
 * Defines a class able to control another class. The controlled class is passed
 * emphatically using the {@link #instrument()} method to expose its
 * dependency.
 *
 * @param instrumentable to be instrumented
 *
 * @see Instrumentable
 * @author Francesco Illuminati
 */
public interface Instrumenter<I extends Instrumentable<I>> {

    /**
     * Sort of setter that pose the emphasis on the controlling rule of
     * the instrumenter.
     */
    Instrumenter<I> instrument(I instrumentable);
}
