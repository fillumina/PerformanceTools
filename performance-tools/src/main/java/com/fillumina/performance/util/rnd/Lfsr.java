/**
 * Copyright (C) 2008 Brent Boyer
 * <p>
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Lesser
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the Lesser GNU General Public License
 * along with this program (see the license directory in this project).
 * If not, see <http://www.gnu.org/licenses/>.
 *
 * @see
 * https://github.com/magro/elliptic-benchmark/blob/master/src/main/java/bb/science/Lfsr.java
 */
package com.fillumina.performance.util.rnd;

/**
 * Implements a
 * <a href="http://en.wikipedia.org/wiki/Linear_feedback_shift_register">
 * linear feedback shift register</a> (LFSR).
 * <p>
 * LFSR algorithm is a very fast way to produce pseudo-random sequences of
 * numbers within the required range. It's a very poor pseudo-random number
 * generator because the sequence is always repeated equals.
 * <p>
 * Because it's guaranteed to generate all numbers within the given range
 * (use {@link #fullNext() } to include 0) it can be used as a sequence
 * generator with unpredictable order to call indexed functions,
 * i.e.: {@code list.get(lfsr.next()) }. The sequence is repeated.
 * <p>
 * This class is not thread safe.
 * <p>
 * @author Brent Boyer
 * @see
 * <a href="http://forum.java.sun.com/thread.jspa?threadID=5276320&tstart=0">this
 * forum posting</a>
 */
public class Lfsr {

    private final int mask;
    private final int taps;
    private int register;
    private int zero;

    /**
     * Create a random LFSR that doesn't repeate the same sequence.
     *
     * @param n number of bits
     */
    public static Lfsr createRandom(int n) {
        return new Lfsr(n, calculateRandomSeed(n));
    }

    public Lfsr() {
        this(32, calculateRandomSeed(32));
    }

    /**
     * Convenience constructor that simply calls
     * <code>{@link #Lfsr(int, int) Lfsr}(n, 1)</code>.
     * <p>
     * @throws IllegalArgumentException if n < 2 or n > 32
     */
    public Lfsr(int n) throws IllegalArgumentException {
        this(n, 1);
    }

    /**
     * Fundamental constructor.
     *
     * @throws IllegalArgumentException if n < 2 or n > 32;
     *                                  if seed == 0 or has high bits (i.e. beyond the nth bit) set
     */
    public Lfsr(int n, int seed) throws IllegalArgumentException {
        if (n < 2) {
            throw new IllegalArgumentException("n = " + n + " < 2");
        }
        if (n > 32) {
            throw new IllegalArgumentException("n = " + n + " > 32");
        }
        if (seed == 0) {
            throw new IllegalArgumentException(
                    "seed == 0; illegal because this will cause the " +
                    "internal state to be stuck at 0 forever");
        }

        this.mask = makeMask(n);
        this.taps = makeTaps(n);
        this.register = seed;
        this.zero = seed & mask;

        if ((seed & (~mask)) != 0) {
            throw new IllegalArgumentException("seed = " + seed +
                    " has high bits (i.e. beyond the nth bit) set," +
                    " making it invalid state for a n = " + n + " LFSR");
        }
    }

    /**
     * Advances the internal state of this instance.
     * This method is essentially 2 lines of code: a loop head and its body.
     * It involves 6 bitwise and/or unary integer operators, <i>so it is
     * very simple and fast</i>.
     * In spite of the simplicity of the computation, the LFSR internal state
     * (stored in the {@link #register} field) is pseudo-random.
     * It should be impossible for a smart compiler to cut many corners and
     * avoid doing the computations.
     * <p>
     * <b>IMPORTANT NOTE:</b> lfsr <b>never</b> returns 0.
     *
     * @return the final value of the internal state
     */
    public int next() {
        register = ((register >>> 1) ^ (-(register & 1) & taps)) & mask;
        return register;
    }

    /** Insert 0 into the sequence at a random place decided by seed. */
    public int fullNext() {
        if (zero < 0) {
            zero = -zero;
            return zero;
        }
        int next = next();
        if (next == zero) {
            zero = -zero;
            return 0;
        }
        return next;
    }

    /**
     * Returns a pseudo-random seed suitable for an LFSR of size n.
     * This method is normally used when calling the seed specifying
     * <code>{@link #Lfsr(int, int) constructor}</code>.
     *
     * @throws IllegalArgumentException if n < 0 or n > 32
     */
    public static int calculateRandomSeed(int n) throws IllegalArgumentException {
        // n checked by makeMask below

        int mask = makeMask(n);
        int seed;
        do {
            // CRITICAL: must mask seed in order to make it be a valid LFSR state
            seed = timeRelatedRandom() & mask;
        } while (seed == 0);
        return seed;
    }

    private static int timeRelatedRandom() {
        long time = System.nanoTime();
        return (int) (time ^ (time >>> 32));
    }

    /**
     * Returns an integer with all the n low order bits set to 1 and all the
     * high
     * order bits set to 0.
     * When used as a bitwise AND mask, this will preserve the n low order bits
     * and drop the high order bits.
     * <p>
     * @throws IllegalArgumentException if n < 0 or n > 32
     */
    private static int makeMask(int n) throws IllegalArgumentException {
        if (n < 0) {
            throw new IllegalArgumentException("n cannot be negative, n=" + n);
        }
        if (n > 32) {
            throw new IllegalArgumentException("n=" + n + " > 32");
        }
        if (n == 32) {
            return ~0;	// ~0 is thirty two 1's in binary
        }

        /*
        The n = 32 special case must be detected for two reasons, one obvious
        and one subtle.

        The obvious reason is that any int that is left shifted by 32 or more
        ought to pushed into a long value which is impossible, so you know
        something weird must happen.

        The subtle reason is the details of how Java's shift operators
        (<<, >>, >>>) work:
        they only use the 5 lower bits of the right side operand
        (i.e. shift amount).
        (This statement assumes that the left hand operand is an int; if it
        is a long, then the lower 6 bits are used.)
        THIS MEANS THAT THEY ONLY DO WHAT YOU THINK THEY WILL WHEN THE SHIFT
        AMOUNT IS INSIDE THE RANGE [0, 31].
        So, in the code above, 1 << n when n = 32 evaluates to 1 << 0
        (because 32 has 0 in its lower 5 bits)
        so the overall expression is then (1 << 0) - 1 == 1 - 1 == 0 which is
        a wrong result.

        This "use only the lower shift bits" behavior is why this code
        (also suggested by Sean Anderson)
                return (~0) >>> (32 - n);
        cannot be used:
            it fails at n = 0 (returning thirty two ones instead of 0).

        References:
                http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6201273
                http://www.davidflanagan.com/blog/000021.html
                http://java.sun.com/docs/books/jls/third_edition/html/expressions.html#15.19
         */
        // acknowledgement: this technique sent to me by Sean Anderson,
        // author of http://graphics.stanford.edu/~seander/bithacks.html
        return (1 << n) - 1;
    }

    /**
     * Returns an integer which can function as the taps of n order maximal
     * LFSR.
     *
     * @throws IllegalArgumentException if n < 2 or n > 32
     */
    private static int makeTaps(int n) throws IllegalArgumentException {
        // There is no easy algorithm to generate the taps as a function of n.
        // Instead simply have to rely on known results.
        // The values below are all taken from
        // http://homepage.mac.com/afj/taplist.html
        // (except for case 2; I think that I figured that one out myself).
        // A less complete reference is
        // http://en.wikipedia.org/wiki/Linear_feedback_shift_register#Some_Polynomials_for_Maximal_LFSRs
        switch (n) {
            case 2: return (1 << 1) | (1 << 0);	// i.e. 2 1
            case 3: return (1 << 2) | (1 << 1);	// i.e. 3 2
            case 4: return (1 << 3) | (1 << 2);	// i.e. 4 3
            case 5: return (1 << 4) | (1 << 2);	// i.e. 5 3
            case 6: return (1 << 5) | (1 << 4);	// i.e. 6 5
            case 7: return (1 << 6) | (1 << 5);	// i.e. 7 6
            case 8: return (1 << 7) | (1 << 6) | (1 << 5) | (1 << 0);	// i.e. 8 7 6 1
            case 9: return (1 << 8) | (1 << 4);	// i.e. 9 5
            case 10: return (1 << 9) | (1 << 6);	// i.e. 10 7
            case 11: return (1 << 10) | (1 << 8);	// i.e. 11 9
            case 12: return (1 << 11) | (1 << 10) | (1 << 9) | (1 << 3);	// i.e. 12 11 10 4
            case 13: return (1 << 12) | (1 << 11) | (1 << 10) | (1 << 7);	// i.e. 13 12 11 8
            case 14: return (1 << 13) | (1 << 12) | (1 << 11) | (1 << 1);	// i.e. 14 13 12 2
            case 15: return (1 << 14) | (1 << 13);	// i.e. 15 14
            case 16: return (1 << 15) | (1 << 14) | (1 << 12) | (1 << 3);	// i.e. 16 15 13 4
            case 17: return (1 << 16) | (1 << 13);	// i.e. 17 14
            case 18: return (1 << 17) | (1 << 10);	// i.e. 18 11
            case 19: return (1 << 18) | (1 << 17) | (1 << 16) | (1 << 13);	// i.e. 19 18 17 14
            case 20: return (1 << 19) | (1 << 16);	// i.e. 20 17
            case 21: return (1 << 20) | (1 << 18);	// i.e. 21 19
            case 22: return (1 << 21) | (1 << 20);	// i.e. 22 21
            case 23: return (1 << 22) | (1 << 17);	// i.e. 23 18
            case 24: return (1 << 23) | (1 << 22) | (1 << 21) | (1 << 16);	// i.e. 24 23 22 17
            case 25: return (1 << 24) | (1 << 21);	// i.e. 25 22
            case 26: return (1 << 25) | (1 << 24) | (1 << 23) | (1 << 19);	// i.e. 26 25 24 20
            case 27: return (1 << 26) | (1 << 25) | (1 << 24) | (1 << 21);	// i.e. 27 26 25 22
            case 28: return (1 << 27) | (1 << 24);	// i.e. 28 25
            case 29: return (1 << 28) | (1 << 26);	// i.e. 29 27
            case 30: return (1 << 29) | (1 << 28) | (1 << 27) | (1 << 6);	// i.e. 30 29 28 7
            case 31: return (1 << 30) | (1 << 27);	// i.e. 31 28
            case 32: return (1 << 31) | (1 << 30) | (1 << 29) | (1 << 9);	// i.e. 32 31 30 10

            default: throw new IllegalArgumentException("n=" + n +
                        " is an illegal value");
        }
    }
}
