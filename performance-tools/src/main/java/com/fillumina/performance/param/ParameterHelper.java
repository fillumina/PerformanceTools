package com.fillumina.performance.param;

import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.param.RunnableHelper.Cloner;
import com.fillumina.performance.util.Combinator;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.collection.LinkedMap;
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

    static LinkedMap<TName, Runnable> createParameterizedRunnable(
            Runnable baseRunnable,
            LinkedTree<String, Object> params,
            Class<? extends Annotation> annotation) {

        int[] max = calculateBranchesDepth(params);

        RunnableHelper paramSetter =
                new RunnableHelper(baseRunnable, annotation);

        LinkedMap<TName,Runnable> linkedMap = new LinkedMap<>();
        for (Combinator.IntArrayCursorList combination : new Combinator(max)) {
            TName composedParamName = TN.EMPTY;
            LinkedMap<String, Object> parameters = new LinkedMap<>();
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
            LinkedMap<String, Object> parameters) {
        for (Entry<String, Object> entry : parameters) {
            final String paramName = entry.getKey();
            final Object paramValue = entry.getValue();
            cloner.set(paramName, paramValue);
        }
        return cloner.get();
    }

}
