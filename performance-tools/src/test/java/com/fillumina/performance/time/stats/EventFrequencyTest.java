package com.fillumina.performance.time.stats;

import com.fillumina.performance.util.Holder;
import com.fillumina.performance.util.Looper;
import com.fillumina.performance.util.unit.IntervalUnit;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class EventFrequencyTest {

    public static void main(String[] args) {

        EventFrequency ef = new EventFrequency();
        Looper.loop(IntervalUnit.SECONDS.quantity(10),
                () -> {
                    float random = (float) Math.random();
                    float wowSignal = (float) Math.PI / 4;
                    if (random == wowSignal) {
                        // WOW, that's unusual.
                        ef.fire("wow");
                    }
                });
        ef.getPerformances().print();

        // using indexes
        new EventFrequency("case1", "case2", "total")
            .loop(IntervalUnit.SECONDS.quantity(1),
                (event) -> {
                    if (Math.random() < 0.1) {
                        event.fire(0);
                    } else {
                        event.fire(1);
                    }
                    event.fire(2);
                })
            .print();

        // using names
        new EventFrequency("one", "two", "total")
            .loop(IntervalUnit.SECONDS.quantity(5),
                (event) -> {
                    if (Math.random() < 0.1) {
                        event.fire("one");
                    } else {
                        event.fire("two");
                    }
                    event.fire("total");
                })
            .print();
    }

    @Test
    public void shouldGetThePercentage() {
        Holder.Integer counter = new Holder.Integer();
        new EventFrequency()
            .loop(IntervalUnit.SECONDS.quantity(1),
                (event) -> {
                    if (counter.getValue() % 5 == 0) {
                        event.fire("divisible by 5");
                    } else {
                        event.fire("not divisible by 5");
                    }
                    event.fire("total");
                    counter.incrementAndGet();
                });
//            .print();
    }

}
