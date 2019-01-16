package com.fillumina.performance.executor.param;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.executor.stats.MixedStatsHolder;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.StatsHolder;
import com.fillumina.performance.executor.stats.StatsType;
import com.fillumina.performance.util.Combinator;
import com.fillumina.performance.util.collection.IndexedHashMap;
import com.fillumina.performance.util.collection.LinkedTree;
import com.fillumina.performance.util.tname.TName;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParameterHelper {

    /**
     * Takes parameters values from {@code params} and creates a map
     * containing named runnables assigned with those parameters
     *
     * @param annotation    the annotation added to the parameters to change
     * @param params        the tree of values to assign
     * @param baseRunnable  the base object to be clones and assigned
     * @return              a map of named parameters assigned clones
     */
    static IndexedHashMap<TName, RunnableOptionsContainer>
                createParameterizedRunnables(
            Class<? extends Annotation> annotation,
            LinkedTree<String, Object> params,
            Runnable baseRunnable) {

        int[] max = calculateBranchesDepth(params);

        RunnableHelper paramSetter = new RunnableHelper(baseRunnable, annotation);

        IndexedHashMap<TName,RunnableOptionsContainer> linkedMap =
                new IndexedHashMap<>();

        for (Combinator.IntArrayCursorList combination : new Combinator(max)) {
            TName composedParamName = TN.EMPTY;
            IndexedHashMap<String, Object> parameters = new IndexedHashMap<>();

            OptionBuilder<?> optBuilder = new OptionBuilder<>();

            for (int i=0; i<combination.size(); i++) {
                LinkedTree<String, Object> options = params.getTreeAtIndex(i);
                Map.Entry<String, Object> selectedOption =
                        options.getTreeAtIndex(combination.getInt(i));

                String paramName = options.getKey();
                String optionName = selectedOption.getKey();
                Object optionValue = selectedOption.getValue();

                optBuilder.addOption(paramName, optionName, optionValue);

                composedParamName = composedParamName.append(optionName);
                parameters.put(paramName, optionValue);
            }

            Runnable runnable = paramSetter.cloneAndSetParameters(parameters);
            Map<String,Option> options = optBuilder.build();

            linkedMap.put(composedParamName,
                    new RunnableOptionsContainer(runnable, options));
        }

        return linkedMap;
    }

    static int[] calculateBranchesDepth(LinkedTree<String, Object> tree) {
        int[] max = new int[tree.size()];
        int counter = 0;
        for (LinkedTree<String,Object> t : tree) {
            max[counter] = t.size();
            counter++;
        }
        return max;
    }

    /**
     * Adds the options to the results of the experiment.
     */
    public static void addOptionsToStats(
            final String payloadName,
            IndexedHashMap<TName, RunnableOptionsContainer> runnableMap,
            MixedStatsHolder result) {

        Map<TName, Map<String,Option>> optionMap = new HashMap<>();

        runnableMap.forEach((tn, rc) ->
                optionMap.put(tn, rc.getOptionContainer()) );

        result.getStatsMap().forEach((StatsType type, StatsHolder holder) -> {
                    holder.use(e -> {
                        e.putPayload(payloadName, optionMap);
                    });
                });
    }

    public static Map<TName, Map<String,Option>> getOptionMap(Stats stats) {

        Map<TName, Map<String,Option>> parametersMap =
                stats.getPayload(ParameterizedTestProducer.PARAMETERS);

        Map<TName, Map<String,Option>> sequencesMap =
                stats.getPayload(SequencedTestProducer.SEQUENCES);

        Map<TName, Map<String,Option>> map = new LinkedHashMap<>();
        merge(parametersMap, map);
        merge(sequencesMap, map);

        return map;
    }

    private static void merge(Map<TName, Map<String, Option>> src,
            Map<TName, Map<String, Option>> dst) {
        if (src == null) {
            return;
        }
        src.forEach((TName tname, Map<String,Option> opts) -> {
            Map<String,Option> m = dst.get(tname);
            if (m != null) {
                m.putAll(opts);
            } else {
                dst.put(tname, new LinkedHashMap<>(opts));
            }
        });
    }
}
