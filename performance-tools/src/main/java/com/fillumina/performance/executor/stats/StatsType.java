package com.fillumina.performance.executor.stats;

/**
 * Defines the type of the statistics. Because it's used as key in hash tables
 * it's important that every {@link StatsType} defined has its own different
 * hash-code.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface StatsType {

    StatsType DEFAULT = new StatsType() { };

}
