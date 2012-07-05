/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fillumina.performance.producer;

import com.fillumina.performance.consumer.PerformanceConsumer;

/**
 *
 * @author fra
 */
public interface PerformanceProducer {

    ChainablePerformanceProducer setPerformanceConsumer(PerformanceConsumer consumer);

}
