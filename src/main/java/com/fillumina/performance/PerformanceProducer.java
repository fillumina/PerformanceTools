package com.fillumina.performance;

/**
 *
 * @author fra
 */
public interface PerformanceProducer {

    <T extends PerformanceConsumer> T use(T presenter);

}