package com.fillumina.performance.executor.stats;

import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.executor.sample.SampleValue;
import com.fillumina.performance.util.collection.IndexedHashMap;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.pathname.PathName;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.OnlineDimensionalMeasure;
import com.fillumina.performance.util.unit.QuantityList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StatsCreator implements StatsTyped {

    private final IndexedHashMap<PathName, QuantityList.Builder> valuesMap =
            new IndexedHashMap<>();
    private final StatsType type;
    private final IndexedHashMap<PathName, List<Integer>> roundIndexes =
            new IndexedHashMap<>();
    private int rounds;

    private static final class IndexedSample {
        private final int round;
        private final double value;

        private IndexedSample(int round, double value) {
            this.round = round;
            this.value = value;
        }
    }

    public StatsCreator(StatsType type) {
        this.type = type;
    }

    @Override
    public StatsType getStatsType() {
        return type;
    }

    public void addSample(Sample sample) {
        final int round = rounds++;
        sample.getValuesMap().values().forEach((SampleValue v) -> {
            PathName name = v.getPathName();
            getBuilder(name).add(v.getQuantity());
            List<Integer> indexes = roundIndexes.get(name);
            if (indexes == null) {
                indexes = new ArrayList<>();
                roundIndexes.put(name, indexes);
            }
            indexes.add(round);
        });
    }

    public Stats createStats() {
        return createStats(ListFilter.<Double>identity());
    }

    /**
     * Builds a {@link Stats} out of the collected samples.
     */
    public Stats createStats(ListFilter<Double> filter) {
        IndexedHashMap<PathName, DimensionalMeasure> map = new IndexedHashMap<>();
        IndexedHashMap<PathName, QuantityList> series = new IndexedHashMap<>();
        boolean[] keepRound = new boolean[rounds];
        Arrays.fill(keepRound, true);

        valuesMap.forEach((PathName name, QuantityList.Builder builder) -> {
            QuantityList values = builder.build();
            series.put(name, values);
            List<Integer> indexes = roundIndexes.get(name);
            List<IndexedSample> indexed = new ArrayList<>(values.size());
            for (int i = 0; i < values.size(); i++) {
                indexed.add(new IndexedSample(indexes.get(i), values.get(i)));
            }
            // A rejection of any variant rejects its entire measured round.
            Set<IndexedSample> retained = new HashSet<>(
                    filter.filter(indexed, sample -> sample.value));
            for (IndexedSample sample : indexed) {
                if (!retained.contains(sample)) {
                    keepRound[sample.round] = false;
                }
            }
        });

        series.forEach((PathName name, QuantityList values) -> {
            List<Integer> indexes = roundIndexes.get(name);
            List<Double> retained = new ArrayList<>(values.size());
            for (int i = 0; i < values.size(); i++) {
                if (keepRound[indexes.get(i)]) {
                    retained.add(values.get(i));
                }
            }
            map.put(name, new OnlineDimensionalMeasure(values.getUnit(), retained));
        });
        return new Stats(type, map);
    }

    /** Detects obvious serial dependence in unfiltered validation samples. */
    public boolean hasStrongSerialDependence() {
        for (QuantityList.Builder builder : valuesMap.values()) {
            QuantityList values = builder.build();
            int size = values.size();
            if (size < 4) {
                continue;
            }
            double mean = 0;
            for (double value : values) {
                mean += value;
            }
            mean /= size;
            double variance = 0;
            double lagged = 0;
            for (int i = 0; i < size; i++) {
                double deviation = values.get(i) - mean;
                variance += deviation * deviation;
                if (i > 0) {
                    lagged += deviation * (values.get(i - 1) - mean);
                }
            }
            if (variance > 0 && Math.abs(lagged / variance) > 3.0 / Math.sqrt(size)) {
                return true;
            }
        }
        return false;
    }

    private QuantityList.Builder getBuilder(PathName name) {
        QuantityList.Builder builder = valuesMap.get(name);
        if (builder == null) {
            builder = QuantityList.builder();
            valuesMap.put(name, builder);
        }
        return builder;
    }
}
