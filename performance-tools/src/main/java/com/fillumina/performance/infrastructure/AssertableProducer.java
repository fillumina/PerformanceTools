package com.fillumina.performance.infrastructure;

import com.fillumina.performance.util.Nominable;
import java.util.function.Supplier;


/**
 * A {@link AssertableProducer} produces assertables.
 *
 * @param A statistics
 * @param T test
 *
 * @author Francesco Illuminati
 */
public interface AssertableProducer
        extends Supplier<MixedAssertableHolder>, Nominable<AssertableProducer> {
}
