package com.fillumina.performance.executor.param;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.executor.param.RunnableHelper.Cloner;
import com.fillumina.performance.util.Combinator;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.collection.ArrayMap;
import com.fillumina.performance.util.collection.LinkedTree;
import com.fillumina.performance.util.collection.Tree;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParameterHelper {

    static ArrayMap<TName, Runnable> createParameterizedRunnable(
            Runnable baseRunnable,
            LinkedTree<String, Object> params,
            Class<? extends Annotation> annotation) {

        int[] max = calculateBranchesDepth(params);

        RunnableHelper paramSetter =
                new RunnableHelper(baseRunnable, annotation);

        ArrayMap<TName,Runnable> linkedMap = new ArrayMap<>();
        for (Combinator.IntArrayCursorList combination : new Combinator(max)) {
            TName composedParamName = TN.EMPTY;
            ArrayMap<String, Object> parameters = new ArrayMap<>();
            for (int i=0; i<combination.size(); i++) {
                LinkedTree<String, Object> options = params.getTreeAtIndex(i);
                final Map.Entry<String, Object> selectedOption =
                        options.getTreeAtIndex(combination.getInt(i));

                String paramName = options.getKey();
                String optionName = selectedOption.getKey();
                Object optionValue = selectedOption.getValue();

                composedParamName = composedParamName.append(optionName);
                parameters.put(paramName, optionValue);
            }
            Runnable runnable = paramSetter.cloneAndSetParameters(parameters);
            linkedMap.put(composedParamName, runnable);
        }

        return linkedMap;
    }

    static int[] calculateBranchesDepth(LinkedTree<String, Object> tree) {
        int[] max = new int[tree.size()];
        int counter = 0;
        for (Tree<String,Object> t : tree) {
            max[counter] = t.size();
            counter++;
        }
        return max;
    }

    static Runnable setParameters(Cloner cloner,
            ArrayMap<String, Object> parameters) {
        for (Entry<String, Object> entry : parameters) {
            final String paramName = entry.getKey();
            final Object paramValue = entry.getValue();
            cloner.set(paramName, paramValue);
        }
        return cloner.get();
    }

}
