package com.fillumina.performance;

import java.io.Serializable;

/**
 *
 * @author fra
 */
public interface Presenter extends Serializable {

    Presenter setMessage(final String message);

    Presenter setPerformances(LoopPerformances performances);

    void show();

}
