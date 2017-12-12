package com.fillumina.performance.executor.stats;

import com.fillumina.performance.mock.MockStatsType;
import com.fillumina.performance.mock.SampleCreator;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class BiggerMeasureTest {

    @Test
    public void shouldGetBiggerMeasure() {
        StatsCreator creator = new StatsCreator(MockStatsType.INSTANCE);

        creator.addSample(SampleCreator.createSample("one", 10.0, "two", 20.0, "three", 30.0));
        creator.addSample(SampleCreator.createSample("one", 10.1, "two", 19.8, "three", 30.1));
        creator.addSample(SampleCreator.createSample("one", 9.88, "two", 20.2, "three", 29.8));
        creator.addSample(SampleCreator.createSample("one", 9.79, "two", 19.9, "three", 29.7));
        creator.addSample(SampleCreator.createSample("one", 10.2, "two", 20.1, "three", 30.0));

        Stats stats = creator.createStats();

        BiggerMeasure bigger = new BiggerMeasure(stats.getMeasureMap());

        assertEquals(2, bigger.getIndex());
        assertEquals("three", bigger.getName().toString());
        assertEquals(30E-9, bigger.getMeasure().getMean(), 1E-10);
    }

    @Test
    public void shouldReturnNullIfEmptyMapIsGiven() {
        StatsCreator creator = new StatsCreator(MockStatsType.INSTANCE);
        Stats stats = creator.createStats();

        BiggerMeasure bigger = new BiggerMeasure(stats.getMeasureMap());

        assertEquals(-1, bigger.getIndex());
        assertEquals(null, bigger.getName());
        assertEquals(null, bigger.getMeasure());
    }

    @Test
    public void shouldGetTheOnlyMeasure() {
        StatsCreator creator = new StatsCreator(MockStatsType.INSTANCE);

        creator.addSample(SampleCreator.createSample("one", 10.0));
        creator.addSample(SampleCreator.createSample("one", 10.1));
        creator.addSample(SampleCreator.createSample("one", 9.88));
        creator.addSample(SampleCreator.createSample("one", 9.79));
        creator.addSample(SampleCreator.createSample("one", 10.2));

        Stats stats = creator.createStats();

        BiggerMeasure bigger = new BiggerMeasure(stats.getMeasureMap());

        assertEquals(0, bigger.getIndex());
        assertEquals("one", bigger.getName().toString());
        assertEquals(10E-9, bigger.getMeasure().getMean(), 1E-11);
    }

}
