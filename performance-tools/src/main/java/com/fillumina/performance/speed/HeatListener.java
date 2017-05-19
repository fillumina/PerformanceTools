package com.fillumina.performance.speed;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface HeatListener {

    // TODO use a Config like method
    public void notify(long currentMillis,
            double expected,
            double lastCheckValue,
            int coolingCounter,
            boolean isHot);

}
