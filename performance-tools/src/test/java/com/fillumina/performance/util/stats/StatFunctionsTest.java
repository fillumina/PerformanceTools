package com.fillumina.performance.util.stats;

import static com.fillumina.performance.util.stats.StatFunctions.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @see <a href="https://it.wikipedia.org/wiki/Distribuzione_t_di_Student">
 *  Wikipedia: Distribuzione di Student (Italian)</a>
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StatFunctionsTest {

    @Test
    public void testZeta() {
        assertEquals(1.28, zeta(0.8), 1E-2);
        assertEquals(1.645, zeta(0.9), 1E-3);
        assertEquals(1.96, zeta(0.95), 1E-3); // usually aproximated to 2
        assertEquals(2.33, zeta(0.98), 1E-2);
        assertEquals(2.58, zeta(0.99), 1E-2);
    }

    @Test
    public void testStudent() {
        assertEquals(3.077, student(0.8, 1), 1E-3);
        assertEquals(3.169, student(0.99, 10), 1E-3);
        assertEquals(2.101, student(0.95, 18), 1E-3);
        assertEquals(2.937, student(0.995, 50), 1E-3);
    }
}
