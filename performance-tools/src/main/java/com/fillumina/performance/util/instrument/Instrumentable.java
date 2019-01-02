package com.fillumina.performance.util.instrument;

/**
 * Defines a class that can be controlled by an instrumenter. It's particularly
 * useful with fluent interfaces because it allows to proceed to
 * assign a component to an aggregate class without disrupting the
 * assignation flow.
 * <p>
 * This is an example of a component assigned to an aggregate using the
 * standard creation-assignation-execution paradigm:
 * <pre>
 * Component comp = new Component();
 * comp.setParam1("a");
 * comp.setParam2("b");
 *
 * Aggregate aggr = new Aggregate();
 * aggr.setComponent(comp);
 * aggr.setAggrParam(1);
 *
 * Result res = aggr.calculate();
 * </pre>
 * By using this interface and a fluent paradigm the previous code become:
 * <pre>
 * Result res = new Component()
 *      .setParam("a")
 *      .setParam("b")
 *      .instrumentedBy(new Aggregate())
 *      .setAggreParam(1)
 *      .calculate();
 * </pre>
 * <p>
 *
 * @param I self
 *
 * @see Instrumenter
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface Instrumentable<I extends Instrumentable<I>> {

    /**
     * The {@link #instrumentedBy(Instrumenter) } should be implemented like this:
     * <pre>
     *
       &#64;Override
       public &lt;T extends Instrumenter&lt;InstrumentableImpl&gt;&gt; T instrumentedBy(
               T instrumenter) {
           instrumenter.instrument(this);
           return instrumenter;
       }
      </pre>
     *
     * @param <T>
     * @param instrumenter
     * @return
     */
    <T extends Instrumenter<I>> T instrumentedBy(T instrumenter);
}
