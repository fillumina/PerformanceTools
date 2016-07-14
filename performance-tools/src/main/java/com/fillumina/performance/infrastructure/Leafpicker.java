package com.fillumina.performance.infrastructure;

import com.fillumina.performance.util.ComposedName;
import java.util.Collection;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface Leafpicker<A> {
    
    Collection<A> getLeaves(ComposedName name);
}
