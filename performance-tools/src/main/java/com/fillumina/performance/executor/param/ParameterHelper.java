package com.fillumina.performance.executor.param;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.executor.stats.MixedStatsHolder;
import com.fillumina.performance.executor.stats.StatsHolder;
import com.fillumina.performance.executor.stats.StatsType;
import com.fillumina.performance.util.Combinator;
import com.fillumina.performance.util.collection.IndexedHashMap;
import com.fillumina.performance.util.collection.LinkedTree;
import com.fillumina.performance.util.tname.TName;
import java.lang.annotation.Annotation;
import java.util.HashMap;
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
    static IndexedHashMap<TName, RunnableContainer> createParameterizedRunnables(
            Class<? extends Annotation> annotation,
            LinkedTree<String, Object> params,
            Runnable baseRunnable) {

        int[] max = calculateBranchesDepth(params);

        RunnableHelper paramSetter = new RunnableHelper(baseRunnable, annotation);

        IndexedHashMap<TName,RunnableContainer> linkedMap = new IndexedHashMap<>();
        OptionContainer.Builder optBuilder = OptionContainer.builder();

        for (Combinator.IntArrayCursorList combination : new Combinator(max)) {
            TName composedParamName = TN.EMPTY;
            IndexedHashMap<String, Object> parameters = new IndexedHashMap<>();

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
            OptionContainer options = optBuilder.build();

            linkedMap.put(composedParamName,
                    new RunnableContainer(runnable, options));
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
    public static void addOptionsToExtendedStats(
            final String payloadName,
            IndexedHashMap<TName, RunnableContainer> runnableMap,
            MixedStatsHolder result) {

        Map<TName, OptionContainer> optionMap = new HashMap<>();

        runnableMap.forEach((tn, rc) ->
                optionMap.put(tn, rc.getOptionContainer()) );

        result.getStatsMap().forEach((StatsType type, StatsHolder holder) -> {
                    holder.use(e -> {
                        e.putPayload(payloadName, optionMap);
                    });
                });
    }

}
